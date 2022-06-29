package com.sl.ms.trade.handler;

import com.sl.ms.trade.domain.RefundRecordDTO;
import com.sl.ms.trade.domain.TradingDTO;
import com.sl.ms.trade.entity.RefundRecordEntity;
import com.sl.ms.trade.entity.TradingEntity;
import com.sl.transport.common.exception.SLException;

import java.math.BigDecimal;

/**
 * 交易前置处理接口
 */
public interface BeforePayHandler {


    /***
     * CreateTrading交易幂等性
     *
     * @param tradingDTO 交易订单
     * @return 交易单数据
     */
    TradingDTO idempotentCreateTrading(TradingDTO tradingDTO) throws SLException;

    /***
     * 交易单参数校验
     * @param tradingDTO 交易订单
     * @return 是否符合要求
     */
    Boolean checkCreateTrading(TradingDTO tradingDTO);

    /***
     * QueryTrading交易单参数校验
     * @param trading 交易订单
     * @return 是否符合要求
     */
    void checkQueryTrading(TradingEntity trading);

    /***
     * RefundTrading退款交易幂等性
     */
    RefundRecordEntity idempotentRefundTrading(TradingEntity trading, BigDecimal refundAmount);

    /***
     * RefundTrading退款交易单参数校验
     * @param trading 交易订单
     */
    void checkRefundTrading(TradingEntity trading);


    /***
     * QueryRefundTrading交易单参数校验
     * @param refundRecord 退款记录
     */
    void checkQueryRefundTrading(RefundRecordEntity refundRecord);
}
