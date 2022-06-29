package com.sl.ms.trade.enums;

import com.sl.transport.common.enums.BaseErrorEnum;

/**
 * 交易枚举
 */
public enum TradingEnum implements BaseErrorEnum {

    SUCCEED(200, "操作成功"),
    ERROR(500, "操作失败"),
    CHECK_TRADING_FAIL(43001, "交易单校验失败"),
    TRY_LOCK_TRADING_FAIL(43002, "交易单加锁失败"),
    PAYING_TRADING_FAIL(43003, "交易单支付失败"),
    TRADING_STATE_SUCCEED(43004, "交易单已完成"),
    TRADING_STATE_PAYING(43005, "交易单交易中"),
    CONFIG_EMPTY(43006, "支付配置为空"),
    CONFIG_ERROR(43016, "支付配置错误"),
    NATIVE_PAY_FAIL(43007, "统一下单交易失败"),
    NATIVE_QRCODE_FAIL(43008, "生成二维码失败"),
    REFUND_FAIL(43008, "查询统一下单交易退款失败"),
    SAVE_OR_UPDATE_FAIL(43009, "交易单保存或修改失败"),
    TRADING_TYPE_FAIL(43010, "未定义的交易类型"),
    NATIVE_QUERY_FAIL(43011, "查询统一下单交易失败"),
    NATIVE_REFUND_FAIL(43012, "统一下单退款交易失败"),
    NATIVE_QUERY_REFUND_FAIL(43013, "统一下单查询退款失败"),
    CASH_PAY_FAIL(43014, "现金交易失败"),
    CASH_REFUND_FAIL(43015, "统一下单退款交易失败"),
    CREDIT_PAY_FAIL(43016, "信用交易失败"),
    LIST_TRADE_STATE_FAIL(43017, "按交易状态查询交易单失败"),
    NOT_FOUND(43018, "交易单不存在"),
    CLOSE_FAIL(43019, "关闭交易单失败"),
    BASIC_REFUND_OUT_FAIL(43020, "退款金额超过订单总金额"),
    REFUND_NOT_FOUND(43021, "退款记录不存在"),
    REFUND_ALREADY_COMPLETED(43022, "退款记录已经完成");

    private Integer code;
    private Integer status;
    private String value;

    TradingEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
        this.status = 500;
    }

    TradingEnum(Integer code, Integer status, String value) {
        this.code = code;
        this.value = value;
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public Integer getStatus() {
        return this.status;
    }
}
