package com.sl.ms.trade.handler.wechat;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import com.sl.ms.trade.constant.TradingConstant;
import com.sl.ms.trade.domain.TradingDTO;
import com.sl.ms.trade.entity.TradingEntity;
import com.sl.ms.trade.enums.PayChannelEnum;
import com.sl.ms.trade.enums.TradingEnum;
import com.sl.ms.trade.handler.JsapiPayHandler;
import com.sl.ms.trade.handler.wechat.response.WeChatResponse;
import com.sl.transport.common.exception.SLException;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 微信jsapi的实现
 *
 * @author zzj
 * @version 1.0
 */
@Component("wechatJsapiPayHandler")
public class WechatJsapiPayHandler implements JsapiPayHandler {

    @Override
    public void createJsapiTrading(TradingEntity tradingEntity) {
        // 查询配置
        WechatPayHttpClient client = WechatPayHttpClient.get(tradingEntity.getEnterpriseId());
        //请求地址
        String apiPath = "/v3/pay/transactions/jsapi";
        //请求参数
        Map<String, Object> params = MapUtil.<String, Object>builder()
                .put("mchid", client.getMchId())
                .put("appid", client.getAppId())
                .put("description", tradingEntity.getMemo())
                .put("notify_url", client.getNotifyUrl())
                .put("out_trade_no", Convert.toStr(tradingEntity.getTradingOrderNo()))
                .put("amount", MapUtil.<String, Object>builder()
                        .put("total", Convert.toInt(NumberUtil.mul(tradingEntity.getTradingAmount(), 100))) //金额，单位：分
                        .put("currency", "CNY") //人民币
                        .build())
                .put("payer", MapUtil.<String, Object>builder()
                        .put("openid", tradingEntity.getOpenId()) //用户识别标识
                        .build())
                .build();
        try {
            WeChatResponse response = client.doPost(apiPath, params);
            if (!response.isOk()) {
                //下单失败
                throw new SLException(TradingEnum.NATIVE_PAY_FAIL);
            }
            //指定统一下单code
            tradingEntity.setPlaceOrderCode(Convert.toStr(response.getStatus()));
            //jsapi发起支付需要的预支付id
            tradingEntity.setPlaceOrderMsg(JSONUtil.parseObj(response.getBody()).getStr("prepay_id"));
            //设置jsapi调起支付的参数
            tradingEntity.setPlaceOrderJson(JSONUtil.toJsonStr(response));
            //指定交易状态
            tradingEntity.setTradingState(TradingConstant.FKZ);
        } catch (Exception e) {
            throw new SLException(TradingEnum.NATIVE_PAY_FAIL);
        }
    }

    @Override
    public PayChannelEnum payChannel() {
        return PayChannelEnum.WECHAT_PAY;
    }
}
