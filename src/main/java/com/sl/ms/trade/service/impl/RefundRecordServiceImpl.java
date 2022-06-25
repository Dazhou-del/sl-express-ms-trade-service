package com.sl.ms.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sl.ms.trade.constant.TradingConstant;
import com.sl.ms.trade.entity.RefundRecordEntity;
import com.sl.ms.trade.mapper.RefundRecordMapper;
import com.sl.ms.trade.service.RefundRecordService;
import org.springframework.stereotype.Service;

/**
 * @Description： 退款记录服务实现类
 */
@Service
public class RefundRecordServiceImpl extends ServiceImpl<RefundRecordMapper, RefundRecordEntity> implements RefundRecordService {

    @Override
    public RefundRecordEntity findRefundRecordByProductOrderNoAndSending(Long productOrderNo) {
        LambdaQueryWrapper<RefundRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RefundRecordEntity::getProductOrderNo, productOrderNo)
                .eq(RefundRecordEntity::getRefundStatus, TradingConstant.REFUND_STATUS_SENDING);
        return getOne(queryWrapper);
    }
}
