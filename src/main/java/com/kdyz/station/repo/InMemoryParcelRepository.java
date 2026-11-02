package com.kdyz.station.repo;

import com.kdyz.station.domain.Parcel;
import com.kdyz.station.domain.ParcelStatus;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryParcelRepository implements ParcelRepository {
    private final Map<String, Parcel> byWaybill = new ConcurrentHashMap<>();
    private final Map<String, Parcel> byCode = new ConcurrentHashMap<>();

    @Override
    public void save(Parcel p) {
        if (byWaybill.putIfAbsent(p.getWaybillNo(), p) != null) {
            throw new IllegalStateException("重复入库: " + p.getWaybillNo());
        }
        if (p.getPickupCode() != null) {
            byCode.put(p.getPickupCode(), p);
        }
    }

    @Override
    public Optional<Parcel> findByWaybill(String no) { return Optional.ofNullable(byWaybill.get(no)); }

    @Override
    public Optional<Parcel> takeByPickupCode(String code) {
        return Optional.ofNullable(byCode.remove(code));   // remove 原子，防止一件被取两次
    }

    @Override
    public Collection<Parcel> inStock() {
        return byWaybill.values().stream()
                .filter(p -> p.getStatus() == ParcelStatus.IN_STOCK || p.getStatus() == ParcelStatus.OVERDUE)
                .collect(Collectors.toList());
    }
}
