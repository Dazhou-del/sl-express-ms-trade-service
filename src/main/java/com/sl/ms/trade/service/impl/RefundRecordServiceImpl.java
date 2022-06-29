package com.sl.ms.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sl.ms.trade.constant.TradingConstant;
import com.sl.ms.trade.entity.RefundRecordEntity;
import com.sl.ms.trade.mapper.RefundRecordMapper;
import com.sl.ms.trade.service.RefundRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description： 退款记录服务实现类
 */
@Service
public class RefundRecordServiceImpl extends ServiceImpl<RefundRecordMapper, RefundRecordEntity> implements RefundRecordService {

    @Override
    public RefundRecordEntity findByRefundNo(Long refundNo) {
        LambdaQueryWrapper<RefundRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RefundRecordEntity::getRefundNo, refundNo);
        return super.getOne(queryWrapper);
    }

    @Override
    public List<RefundRecordEntity> findList(Long tradingOrderNo) {
        LambdaQueryWrapper<RefundRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RefundRecordEntity::getTradingOrderNo, tradingOrderNo);
        return super.list(queryWrapper);
    }
}
