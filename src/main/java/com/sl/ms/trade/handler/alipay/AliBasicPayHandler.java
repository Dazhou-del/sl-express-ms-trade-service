package com.sl.ms.trade.handler.alipay;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.payment.common.models.AlipayTradeCloseResponse;
import com.alipay.easysdk.payment.common.models.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import com.alipay.easysdk.payment.common.models.AlipayTradeRefundResponse;
import com.sl.ms.trade.constant.Constants;
import com.sl.ms.trade.constant.TradingConstant;
import com.sl.ms.trade.entity.RefundRecordEntity;
import com.sl.ms.trade.entity.TradingEntity;
import com.sl.ms.trade.enums.PayChannelEnum;
import com.sl.ms.trade.enums.TradingEnum;
import com.sl.ms.trade.handler.BasicPayHandler;
import com.sl.ms.trade.service.PayChannelService;
import com.sl.transport.common.exception.SLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 支付宝基础支付功能的实现
 *
 * @author zzj
 * @version 1.0
 */
@Slf4j
@Component("aliBasicPayHandler")
public class AliBasicPayHandler implements BasicPayHandler {

    @Resource
    private PayChannelService payChannelService;

    @Override
    public Boolean queryTrading(TradingEntity trading) throws SLException {
        //查询配置
        Config config = AlipayConfig.getConfig(trading.getEnterpriseId());
        //Factory使用配置
        Factory.setOptions(config);
        AlipayTradeQueryResponse queryResponse;
        try {
            //调用支付宝API：通用查询支付情况
            queryResponse = Factory
                    .Payment
                    .Common()
                    .query(String.valueOf(trading.getTradingOrderNo()));
        } catch (Exception e) {
            log.error("查询支付宝统一下单失败：trading = {}", trading, e);
            throw new SLException(TradingEnum.NATIVE_QUERY_FAIL);
        }

        //修改交易单状态
        trading.setResultCode(queryResponse.getSubCode());
        trading.setResultMsg(queryResponse.getSubMsg());
        trading.setResultJson(JSONUtil.toJsonStr(queryResponse));

        boolean success = ResponseChecker.success(queryResponse);
        //响应成功，分析交易状态
        if (success) {
            String tradeStatus = queryResponse.getTradeStatus();
            if (TradingConstant.ALI_TRADE_CLOSED.equals(tradeStatus)) {
                //支付取消：TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）
                trading.setTradingState(TradingConstant.QXDD);
            } else if (StrUtil.equalsAny(tradeStatus, TradingConstant.ALI_TRADE_SUCCESS, TradingConstant.ALI_TRADE_FINISHED)) {
                // TRADE_SUCCESS（交易支付成功）
                // TRADE_FINISHED（交易结束，不可退款）
                trading.setTradingState(TradingConstant.YJS);
            } else {
                //非最终状态不处理，当前交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）不处理
                return false;
            }
            return true;
        }
        throw new SLException(trading.getResultJson(), Constants.ERROR);
    }

    @Override
    public Boolean closeTrading(TradingEntity trading) throws SLException {
        //查询配置
        Config config = AlipayConfig.getConfig(trading.getEnterpriseId());
        //Factory使用配置
        Factory.setOptions(config);
        try {
            //调用支付宝API：通用查询支付情况
            AlipayTradeCloseResponse closeResponse = Factory
                    .Payment
                    .Common()
                    .close(String.valueOf(trading.getTradingOrderNo()));
            boolean success = ResponseChecker.success(closeResponse);
            if (success) {
                trading.setTradingState(TradingConstant.QXDD);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new SLException(TradingEnum.CLOSE_FAIL);
        }
    }

    @Override
    public Boolean refundTrading(RefundRecordEntity refundRecord) throws SLException {
        //查询配置
        Config config = AlipayConfig.getConfig(refundRecord.getEnterpriseId());
        //Factory使用配置
        Factory.setOptions(config);
        //调用支付宝API：通用查询支付情况
        AlipayTradeRefundResponse refundResponse;
        try {
            // 支付宝easy sdk
            refundResponse = Factory
                    .Payment
                    .Common()
                    //扩展参数：退款单号
                    .optional("out_request_no", refundRecord.getRefundNo())
                    .refund(Convert.toStr(refundRecord.getTradingOrderNo()),
                            Convert.toStr(refundRecord.getRefundAmount()));
        } catch (Exception e) {
            log.error("调用支付宝退款接口出错！refundRecord = {}", refundRecord, e);
            throw new SLException(TradingEnum.NATIVE_REFUND_FAIL);
        }
        refundRecord.setRefundCode(refundResponse.getSubCode());
        refundRecord.setRefundMsg(JSONUtil.toJsonStr(refundResponse));
        boolean success = ResponseChecker.success(refundResponse);
        if (success) {
            refundRecord.setRefundStatus(TradingConstant.REFUND_STATUS_SUCCESS);
            return true;
        }
        throw new SLException(refundRecord.getRefundMsg(), Constants.ERROR);
    }

    @Override
    public Boolean queryRefundTrading(RefundRecordEntity refundRecord) throws SLException {
        //查询配置
        Config config = AlipayConfig.getConfig(refundRecord.getEnterpriseId());
        //Factory使用配置
        Factory.setOptions(config);
        AlipayTradeFastpayRefundQueryResponse response;
        try {
            response = Factory.Payment.Common().queryRefund(
                    Convert.toStr(refundRecord.getTradingOrderNo()),
                    Convert.toStr(refundRecord.getRefundNo()));
        } catch (Exception e) {
            log.error("调用支付宝查询退款接口出错！refundRecord = {}", refundRecord, e);
            throw new SLException(TradingEnum.NATIVE_REFUND_FAIL);
        }

        refundRecord.setRefundCode(response.getSubCode());
        refundRecord.setRefundMsg(JSONUtil.toJsonStr(response));
        boolean success = ResponseChecker.success(response);
        if (success) {
            refundRecord.setRefundStatus(TradingConstant.REFUND_STATUS_SUCCESS);
            return true;
        }
        throw new SLException(refundRecord.getRefundMsg(), Constants.ERROR);
    }

    @Override
    public PayChannelEnum payChannel() {
        return PayChannelEnum.ALI_PAY;
    }
}