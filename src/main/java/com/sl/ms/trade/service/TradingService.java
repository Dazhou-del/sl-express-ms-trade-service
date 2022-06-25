package com.sl.ms.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sl.ms.trade.entity.TradingEntity;

import java.util.List;

/**
 * @Description：交易订单表 服务类
 */
public interface TradingService extends IService<TradingEntity> {

    /***
     * @description 按交易单号查询交易单
     * @param tradingOrderNo 交易单号
     * @return
     */
    TradingEntity findTradByTradingOrderNo(Long tradingOrderNo);

    /***
     * @description 按订单单号查询交易单
     * @param productOrderNo 交易单号
     * @return
     */
    TradingEntity findTradByProductOrderNo(Long productOrderNo);

    /***
     * @description 按交易状态查询交易单
     * @param tradingState
     * @return
     */
    List<TradingEntity> findTradingByTradingState(String tradingState);
}
