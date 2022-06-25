package com.sl.ms.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sl.ms.trade.entity.RefundRecordEntity;

/**
 * @Description： 退款记录表服务类
 */
public interface RefundRecordService extends IService<RefundRecordEntity> {

    /***
     * @description 查询当前订单是否有退款中的记录
     *
     * @param productOrderNo 交易系统订单号
     * @return 退款记录
     */
    RefundRecordEntity findRefundRecordByProductOrderNoAndSending(Long productOrderNo);
}
