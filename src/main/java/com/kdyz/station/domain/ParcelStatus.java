package com.kdyz.station.domain;

import java.util.EnumSet;
import java.util.Set;

/** 包裹状态机：已录入 → 在库 → (已取件 | 滞留 → 退回/已取件)。 */
public enum ParcelStatus {
    CREATED, IN_STOCK, OVERDUE, PICKED_UP, RETURNED;

    private Set<ParcelStatus> next() {
        switch (this) {
            case CREATED: return EnumSet.of(IN_STOCK);
            case IN_STOCK: return EnumSet.of(PICKED_UP, OVERDUE);
            case OVERDUE: return EnumSet.of(PICKED_UP, RETURNED);
            default: return EnumSet.noneOf(ParcelStatus.class);
        }
    }

    public void check(ParcelStatus target) {
        if (!next().contains(target)) {
            throw new IllegalStateException("非法状态流转: " + this + " -> " + target);
        }
    }
}
