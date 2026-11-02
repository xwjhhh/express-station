package com.kdyz.station.pickup;

import java.security.SecureRandom;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 取件码 = 库位编码 + "-" + 4位随机数，如 A3-2-4821。
 * 居民凭前缀直接找到货架，随机段防止猜码冒领(变更CR-02)。
 * 生产环境用 Redis SETNX 保证多实例唯一，这里用并发集合模拟。
 */
public class SlotPickupCodeGenerator implements PickupCodeGenerator {
    private final SecureRandom random = new SecureRandom();
    private final Set<String> active = ConcurrentHashMap.newKeySet();

    @Override
    public String generate(String slot) {
        for (int i = 0; i < 1000; i++) {
            String code = slot + "-" + String.format("%04d", random.nextInt(10000));
            if (active.add(code)) {   // add 是原子操作，相当于 SETNX
                return code;
            }
        }
        throw new IllegalStateException("库位 " + slot + " 取件码空间耗尽");
    }

    @Override
    public void recycle(String code) { active.remove(code); }

    public int activeCount() { return active.size(); }
}
