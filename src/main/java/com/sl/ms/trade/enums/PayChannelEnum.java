package com.sl.ms.trade.enums;

import com.sl.transport.common.enums.BaseEnum;

/**
 * @author zzj
 * @version 1.0
 */
public enum PayChannelEnum implements BaseEnum {
    ALI_PAY(1, "支付宝"), WECHAT_PAY(2, "微信支付");

    private Integer code;
    private String value;

    PayChannelEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    @Override
    public Integer getCode() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }
}
