package com.kdyz.station.shelf;

import com.kdyz.station.domain.Parcel;

/** 库位分配策略接口(策略模式)：可替换为按手机尾号、按快递公司等策略。 */
public interface ShelfAllocationStrategy {
    /** 为包裹分配库位，返回库位编码；无可用库位时抛出 IllegalStateException。 */
    String allocate(Parcel parcel);

    /** 包裹出库后释放库位。 */
    void release(String slot);
}
