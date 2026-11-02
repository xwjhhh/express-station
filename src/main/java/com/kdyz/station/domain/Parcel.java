package com.kdyz.station.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/** 包裹抽象类：不同类型包裹(普通/生鲜/大件)共享入库、取件、滞留判断逻辑。 */
public abstract class Parcel {
    private final String waybillNo;      // 快递单号
    private final String courier;        // 快递公司
    private final String receiverPhone;  // 收件人手机号
    private ParcelStatus status = ParcelStatus.CREATED;
    private String shelfSlot;            // 库位编码，如 A-03-2
    private String pickupCode;           // 取件码，如 A3-2-4821
    private LocalDateTime inboundTime;
    private LocalDateTime outboundTime;

    protected Parcel(String waybillNo, String courier, String receiverPhone) {
        this.waybillNo = Objects.requireNonNull(waybillNo);
        this.courier = Objects.requireNonNull(courier);
        if (receiverPhone == null || !receiverPhone.matches("1\\d{10}")) {
            throw new IllegalArgumentException("收件人手机号不合法: " + receiverPhone);
        }
        this.receiverPhone = receiverPhone;
    }

    /** 允许在驿站滞留的最长小时数，由子类决定。 */
    public abstract int maxStayHours();

    /** 货架区域偏好，由子类决定(生鲜放冷藏区，大件放地堆区)。 */
    public abstract ShelfZone preferredZone();

    public void inbound(String slot, String code, LocalDateTime now) {
        status.check(ParcelStatus.IN_STOCK);
        this.shelfSlot = slot;
        this.pickupCode = code;
        this.inboundTime = now;
        this.status = ParcelStatus.IN_STOCK;
    }

    public void pickup(LocalDateTime now) {
        status.check(ParcelStatus.PICKED_UP);
        this.outboundTime = now;
        this.status = ParcelStatus.PICKED_UP;
    }

    public void markOverdue() {
        status.check(ParcelStatus.OVERDUE);
        this.status = ParcelStatus.OVERDUE;
    }

    public void returnToCourier() {
        status.check(ParcelStatus.RETURNED);
        this.status = ParcelStatus.RETURNED;
    }

    public boolean isOverdue(LocalDateTime now) {
        return inboundTime != null && status == ParcelStatus.IN_STOCK
                && inboundTime.plusHours(maxStayHours()).isBefore(now);
    }

    public String getWaybillNo() { return waybillNo; }
    public String getCourier() { return courier; }
    public String getReceiverPhone() { return receiverPhone; }
    public ParcelStatus getStatus() { return status; }
    public String getShelfSlot() { return shelfSlot; }
    public String getPickupCode() { return pickupCode; }
    public LocalDateTime getInboundTime() { return inboundTime; }
    public LocalDateTime getOutboundTime() { return outboundTime; }

    /** 手机尾号，用于通知文案脱敏。 */
    public String phoneTail() { return receiverPhone.substring(7); }
}
