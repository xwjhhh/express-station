package com.kdyz.station.domain;

/** 包裹子类集合。 */
public final class ParcelTypes {
    private ParcelTypes() {}

    /** 普通包裹：滞留 72 小时。 */
    public static class NormalParcel extends Parcel {
        public NormalParcel(String no, String courier, String phone) { super(no, courier, phone); }
        @Override public int maxStayHours() { return 72; }
        @Override public ShelfZone preferredZone() { return ShelfZone.NORMAL; }
    }

    /** 生鲜包裹：放冷藏区，滞留 24 小时。 */
    public static class FreshParcel extends Parcel {
        public FreshParcel(String no, String courier, String phone) { super(no, courier, phone); }
        @Override public int maxStayHours() { return 24; }
        @Override public ShelfZone preferredZone() { return ShelfZone.COLD; }
    }

    /** 大件包裹：放地堆区，滞留 120 小时。 */
    public static class BulkyParcel extends Parcel {
        public BulkyParcel(String no, String courier, String phone) { super(no, courier, phone); }
        @Override public int maxStayHours() { return 120; }
        @Override public ShelfZone preferredZone() { return ShelfZone.FLOOR; }
    }
}
