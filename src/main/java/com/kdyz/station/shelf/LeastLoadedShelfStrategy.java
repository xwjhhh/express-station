package com.kdyz.station.shelf;

import com.kdyz.station.domain.Parcel;
import com.kdyz.station.domain.ShelfZone;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** 最少占用优先：在包裹偏好区域内选择当前件数最少的库位，均衡货架负载。 */
public class LeastLoadedShelfStrategy implements ShelfAllocationStrategy {
    private final Map<ShelfZone, List<String>> slotsByZone = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> used = new ConcurrentHashMap<>();
    private final int capacityPerSlot;

    /** @param shelves 每个区域的货架数，@param layers 每个货架层数 */
    public LeastLoadedShelfStrategy(int shelves, int layers, int capacityPerSlot) {
        this.capacityPerSlot = capacityPerSlot;
        for (ShelfZone z : ShelfZone.values()) {
            List<String> slots = new ArrayList<>();
            for (int s = 1; s <= shelves; s++) {
                for (int l = 1; l <= layers; l++) {
                    String code = String.format("%s%d-%d", z.prefix(), s, l);
                    slots.add(code);
                    used.put(code, new AtomicInteger());
                }
            }
            slotsByZone.put(z, slots);
        }
    }

    @Override
    public synchronized String allocate(Parcel parcel) {
        String best = null;
        int min = Integer.MAX_VALUE;
        for (String s : slotsByZone.get(parcel.preferredZone())) {
            int n = used.get(s).get();
            if (n < capacityPerSlot && n < min) {
                best = s;
                min = n;
            }
        }
        if (best == null) {
            throw new IllegalStateException("区域已满: " + parcel.preferredZone());
        }
        used.get(best).incrementAndGet();
        return best;
    }

    @Override
    public void release(String slot) {
        used.get(slot).updateAndGet(v -> Math.max(0, v - 1));
    }

    public int usedOf(String slot) { return used.get(slot).get(); }
}
