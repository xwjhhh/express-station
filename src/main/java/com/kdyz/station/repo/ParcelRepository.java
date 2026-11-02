package com.kdyz.station.repo;

import com.kdyz.station.domain.Parcel;
import java.util.Collection;
import java.util.Optional;

/** 包裹仓储接口：生产环境实现为 MySQL + Redis，测试用内存实现。 */
public interface ParcelRepository {
    void save(Parcel parcel);

    Optional<Parcel> findByWaybill(String waybillNo);

    /** 原子地按取件码取出包裹并解除索引；并发取件时只有一个调用方能拿到。 */
    Optional<Parcel> takeByPickupCode(String code);

    Collection<Parcel> inStock();
}
