package com.kdyz.station;

import com.kdyz.station.domain.Parcel;
import com.kdyz.station.domain.ParcelStatus;
import com.kdyz.station.domain.ParcelTypes.BulkyParcel;
import com.kdyz.station.domain.ParcelTypes.FreshParcel;
import com.kdyz.station.domain.ParcelTypes.NormalParcel;
import com.kdyz.station.notify.NotifyDispatcher;
import com.kdyz.station.notify.SmsNotifier;
import com.kdyz.station.notify.WechatNotifier;
import com.kdyz.station.pickup.SlotPickupCodeGenerator;
import com.kdyz.station.repo.InMemoryParcelRepository;
import com.kdyz.station.service.StationService;
import com.kdyz.station.shelf.LeastLoadedShelfStrategy;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/** 无第三方依赖的自检程序：功能用例 + 1万并发取件用例。 */
public class StationCheck {
    static int passed = 0;

    static void check(boolean ok, String name) {
        if (!ok) throw new AssertionError("FAIL: " + name);
        passed++;
        System.out.println("PASS  " + name);
    }

    public static void main(String[] args) throws Exception {
        functional();
        concurrent(10_000);
        System.out.println("全部通过，共 " + passed + " 项");
    }

    static void functional() {
        Instant t0 = Instant.parse("2026-12-01T08:00:00Z");
        Clock[] clock = {Clock.fixed(t0, ZoneId.of("Asia/Shanghai"))};
        Clock delegating = new Clock() {
            public ZoneId getZone() { return clock[0].getZone(); }
            public Clock withZone(ZoneId z) { return this; }
            public Instant instant() { return clock[0].instant(); }
        };
        SmsNotifier sms = new SmsNotifier();
        WechatNotifier wx = new WechatNotifier();
        wx.subscribe("13800000002");
        LeastLoadedShelfStrategy shelf = new LeastLoadedShelfStrategy(5, 4, 30);
        StationService svc = new StationService(new InMemoryParcelRepository(), shelf,
                new SlotPickupCodeGenerator(), new NotifyDispatcher(List.of(wx, sms)), delegating);

        String c1 = svc.inbound(new NormalParcel("SF001", "顺丰", "13800000001"));
        String c2 = svc.inbound(new FreshParcel("JD002", "京东", "13800000002"));
        String c3 = svc.inbound(new BulkyParcel("ZT003", "中通", "13800000003"));
        check(c1.startsWith("A") && c2.startsWith("C") && c3.startsWith("F"), "按包裹类型分配到普通/冷藏/地堆区");
        check(c1.matches("A\\d-\\d-\\d{4}"), "取件码格式为 库位-4位随机数");
        check(sms.outbox().size() == 2 && wx.outbox().size() == 1, "已订阅用户走微信，其余降级短信");
        check(sms.outbox().get(0).contains("尾号0001"), "短信文案手机号脱敏");
        try {
            svc.inbound(new NormalParcel("SF001", "顺丰", "13800000001"));
            check(false, "重复入库应被拒绝");
        } catch (IllegalStateException e) {
            check(true, "重复入库被拒绝");
        }
        try {
            new NormalParcel("X", "顺丰", "12345");
            check(false, "非法手机号应被拒绝");
        } catch (IllegalArgumentException e) {
            check(true, "非法手机号被拒绝");
        }
        check(svc.pickup(c1).isPresent(), "凭取件码取件成功");
        check(svc.pickup(c1).isEmpty(), "同一取件码不能重复取件");
        check(svc.pickup("A9-9-0000").isEmpty(), "错误取件码取件失败");

        clock[0] = Clock.offset(clock[0], Duration.ofHours(25));
        List<Parcel> overdue = svc.scanOverdue();
        check(overdue.size() == 1 && overdue.get(0).getWaybillNo().equals("JD002"), "生鲜件24小时后转滞留，普通件不受影响");
        check(svc.pickup(c2).map(Parcel::getStatus).orElse(null) == ParcelStatus.PICKED_UP, "滞留件仍可取件");
    }

    static void concurrent(int users) throws Exception {
        LeastLoadedShelfStrategy shelf = new LeastLoadedShelfStrategy(20, 5, 200);
        SlotPickupCodeGenerator gen = new SlotPickupCodeGenerator();
        StationService svc = new StationService(new InMemoryParcelRepository(), shelf, gen,
                new NotifyDispatcher(List.of(new SmsNotifier())), Clock.systemDefaultZone());
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < users; i++) {
            codes.add(svc.inbound(new NormalParcel("YT" + i, "圆通", String.format("139%08d", i))));
        }
        check(gen.activeCount() == users, users + " 个在库包裹取件码互不重复");

        // 每个取件码由2个线程同时提交，共 2 万次请求
        ExecutorService pool = Executors.newFixedThreadPool(200);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger ok = new AtomicInteger();
        long t = System.nanoTime();
        for (int r = 0; r < 2; r++) {
            for (String c : codes) {
                pool.submit(() -> {
                    start.await();
                    if (svc.pickup(c).isPresent()) ok.incrementAndGet();
                    return null;
                });
            }
        }
        start.countDown();
        pool.shutdown();
        pool.awaitTermination(60, TimeUnit.SECONDS);
        long ms = (System.nanoTime() - t) / 1_000_000;
        check(ok.get() == users, "2万次并发取件请求恰好成功 " + users + " 次，无重复出库（" + ms + "ms）");
        check(gen.activeCount() == 0, "取件后取件码全部回收");
    }
}
