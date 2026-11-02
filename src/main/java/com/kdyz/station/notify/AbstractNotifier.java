package com.kdyz.station.notify;

import com.kdyz.station.domain.Parcel;

/**
 * 模板方法：统一“组装文案 → 发送 → 失败重试”流程，子类只实现 doSend。
 */
public abstract class AbstractNotifier implements Notifier {
    private final int maxRetry;

    protected AbstractNotifier(int maxRetry) { this.maxRetry = maxRetry; }

    @Override
    public final boolean notifyInbound(Parcel p) {
        String msg = buildMessage(p);
        for (int i = 0; i <= maxRetry; i++) {
            if (doSend(p.getReceiverPhone(), msg)) {
                return true;
            }
        }
        return false;
    }

    protected String buildMessage(Parcel p) {
        return String.format("【社区驿站】尾号%s的%s包裹已到站，取件码%s，请在%d小时内取件。",
                p.phoneTail(), p.getCourier(), p.getPickupCode(), p.maxStayHours());
    }

    /** 具体渠道发送实现。 */
    protected abstract boolean doSend(String phone, String message);
}
