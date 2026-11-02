package com.kdyz.station.service;

import com.kdyz.station.domain.Parcel;
import com.kdyz.station.notify.NotifyDispatcher;
import com.kdyz.station.pickup.PickupCodeGenerator;
import com.kdyz.station.repo.ParcelRepository;
import com.kdyz.station.shelf.ShelfAllocationStrategy;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** 驿站核心业务：入库、取件出库、滞留件扫描。依赖全部通过接口注入，便于替换与测试。 */
public class StationService {
    private final ParcelRepository repo;
    private final ShelfAllocationStrategy shelf;
    private final PickupCodeGenerator codes;
    private final NotifyDispatcher notifier;
    private final Clock clock;

    public StationService(ParcelRepository repo, ShelfAllocationStrategy shelf,
                          PickupCodeGenerator codes, NotifyDispatcher notifier, Clock clock) {
        this.repo = repo;
        this.shelf = shelf;
        this.codes = codes;
        this.notifier = notifier;
        this.clock = clock;
    }

    /** 包裹入库：分配库位 → 生成取件码 → 保存 → 通知收件人。返回取件码。 */
    public String inbound(Parcel p) {
        if (repo.findByWaybill(p.getWaybillNo()).isPresent()) {
            throw new IllegalStateException("重复入库: " + p.getWaybillNo());
        }
        String slot = shelf.allocate(p);
        String code = codes.generate(slot);
        p.inbound(slot, code, LocalDateTime.now(clock));
        repo.save(p);
        notifier.dispatch(p);
        return code;
    }

    /** 凭取件码取件。同一取件码并发提交时只有一次成功。 */
    public Optional<Parcel> pickup(String code) {
        Optional<Parcel> got = repo.takeByPickupCode(code);
        got.ifPresent(p -> {
            p.pickup(LocalDateTime.now(clock));
            shelf.release(p.getShelfSlot());
            codes.recycle(code);
        });
        return got;
    }

    /** 定时任务调用：把超过滞留时限的包裹标记为滞留件。返回本次标记的数量。 */
    public List<Parcel> scanOverdue() {
        LocalDateTime now = LocalDateTime.now(clock);
        List<Parcel> list = repo.inStock().stream().filter(p -> p.isOverdue(now)).collect(Collectors.toList());
        list.forEach(Parcel::markOverdue);
        return list;
    }
}
