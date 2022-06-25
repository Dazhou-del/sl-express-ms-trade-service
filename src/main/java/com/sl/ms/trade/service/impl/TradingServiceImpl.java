package com.sl.ms.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sl.ms.trade.constant.Constants;
import com.sl.ms.trade.entity.TradingEntity;
import com.sl.ms.trade.mapper.TradingMapper;
import com.sl.ms.trade.service.TradingService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description：交易订单表 服务实现类
 */
@Service
public class TradingServiceImpl extends ServiceImpl<TradingMapper, TradingEntity> implements TradingService {

    @Override
    public TradingEntity findTradByTradingOrderNo(Long tradingOrderNo) {
        LambdaQueryWrapper<TradingEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradingEntity::getTradingOrderNo, tradingOrderNo);
        return super.getOne(queryWrapper);
    }

    @Override
    public TradingEntity findTradByProductOrderNo(Long productOrderNo) {
        LambdaQueryWrapper<TradingEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradingEntity::getProductOrderNo, productOrderNo);
        return super.getOne(queryWrapper);
    }

    @Override
    public List<TradingEntity> findTradingByTradingState(String tradingState) {
        LambdaQueryWrapper<TradingEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradingEntity::getTradingState, tradingState)
                .eq(TradingEntity::getEnableFlag, Constants.YES);
        return list(queryWrapper);
    }
}
