package com.kdyz.station.notify;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** 短信渠道。真实环境调用短信网关，此处记录到发件箱以便测试。 */
public class SmsNotifier extends AbstractNotifier {
    private final List<String> outbox = new CopyOnWriteArrayList<>();

    public SmsNotifier() { super(2); }

    @Override public String channel() { return "SMS"; }

    @Override
    protected boolean doSend(String phone, String message) {
        outbox.add(phone + ": " + message);
        return true;
    }

    public List<String> outbox() { return outbox; }
}
