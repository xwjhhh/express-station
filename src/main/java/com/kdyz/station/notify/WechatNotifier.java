package com.kdyz.station.notify;

import com.kdyz.station.domain.Parcel;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/** 微信订阅消息渠道(变更CR-01新增)。仅对已授权订阅的用户发送。 */
public class WechatNotifier extends AbstractNotifier {
    private final Set<String> subscribed = ConcurrentHashMap.newKeySet();
    private final List<String> outbox = new CopyOnWriteArrayList<>();

    public WechatNotifier() { super(1); }

    public void subscribe(String phone) { subscribed.add(phone); }

    @Override public String channel() { return "WECHAT"; }

    @Override
    protected String buildMessage(Parcel p) {
        return "取件码:" + p.getPickupCode() + "|库位:" + p.getShelfSlot() + "|快递:" + p.getCourier();
    }

    @Override
    protected boolean doSend(String phone, String message) {
        if (!subscribed.contains(phone)) {
            return false;
        }
        outbox.add(phone + ": " + message);
        return true;
    }

    public List<String> outbox() { return outbox; }
}
