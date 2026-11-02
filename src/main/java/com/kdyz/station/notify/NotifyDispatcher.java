package com.kdyz.station.notify;

import com.kdyz.station.domain.Parcel;
import java.util.List;

/** 按优先级依次尝试渠道：微信订阅消息免费，优先；失败再发短信。 */
public class NotifyDispatcher {
    private final List<Notifier> chain;

    public NotifyDispatcher(List<Notifier> chain) { this.chain = List.copyOf(chain); }

    /** 返回成功的渠道名，全部失败返回 null。 */
    public String dispatch(Parcel parcel) {
        for (Notifier n : chain) {
            if (n.notifyInbound(parcel)) {
                return n.channel();
            }
        }
        return null;
    }
}
