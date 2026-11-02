package com.kdyz.station.notify;

import com.kdyz.station.domain.Parcel;

/** 取件通知渠道接口。新增渠道(如智能柜屏幕、APP推送)只需实现本接口。 */
public interface Notifier {
    /** 渠道名称，如 SMS、WECHAT。 */
    String channel();

    /** 发送入库通知，返回是否成功。 */
    boolean notifyInbound(Parcel parcel);
}
