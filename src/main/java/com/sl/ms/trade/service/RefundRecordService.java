package com.sl.ms.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sl.ms.trade.entity.RefundRecordEntity;

import java.util.List;

/**
 * @Description： 退款记录表服务类
 */
public interface RefundRecordService extends IService<RefundRecordEntity> {

    /**
     * 根据退款单号查询退款记录
     *
     * @param refundNo 退款单号
     * @return 退款记录数据
     */
    RefundRecordEntity findByRefundNo(Long refundNo);

    /**
     * 根据交易单号查询退款列表
     *
     * @param tradingOrderNo 交易单号
     * @return 退款列表
     */
    List<RefundRecordEntity> findListByTradingOrderNo(Long tradingOrderNo);

    /**
     * 根据订单号查询退款列表
     *
     * @param productOrderNo 订单号
     * @return 退款列表
     */
    List<RefundRecordEntity> findListByProductOrderNo(Long productOrderNo);
}
