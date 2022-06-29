package com.sl.ms.trade.service;

import com.sl.ms.trade.domain.TradingDTO;
import com.sl.transport.common.exception.SLException;

/**
 * 二维码支付
 */
public interface NativePayService {


    /***
     * 查看二维码信息
     * 收银员通过收银台或商户后台调用此接口，生成二维码后，展示给用户，商户可以多次展示二维码
     *
     * @param productOrderNo 订单单号
     * @return 交易单
     */
    String queryQrCodeUrl(Long productOrderNo);

    /***
     *  统一收单线下交易预创建
     * 收银员通过收银台或商户后台调用此接口，生成二维码后，展示给用户，由用户扫描二维码完成订单支付。
     * @param tradingDTO 交易单数据
     * @return 交易单数据
     */
    TradingDTO createDownLineTrading(TradingDTO tradingDTO);

}
