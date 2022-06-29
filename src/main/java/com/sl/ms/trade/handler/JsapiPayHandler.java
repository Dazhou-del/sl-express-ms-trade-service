package com.sl.ms.trade.handler;

import com.sl.ms.trade.domain.TradingDTO;

/**
 * jsapi下单处理
 *
 * @author zzj
 * @version 1.0
 */
public interface JsapiPayHandler extends PayChannelHandler {

    /**
     * 创建交易
     *
     * @param tradingDTO 交易单
     */
    void createJsapiTrading(TradingDTO tradingDTO);
}
