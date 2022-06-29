package com.sl.ms.trade.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.sl.ms.trade.constant.Constants;
import com.sl.ms.trade.constant.TradingCacheConstant;
import com.sl.ms.trade.domain.TradingDTO;
import com.sl.ms.trade.entity.TradingEntity;
import com.sl.ms.trade.enums.TradingEnum;
import com.sl.ms.trade.handler.BeforePayHandler;
import com.sl.ms.trade.handler.HandlerFactory;
import com.sl.ms.trade.handler.JsapiPayHandler;
import com.sl.ms.trade.service.JsapiPayService;
import com.sl.ms.trade.service.TradingService;
import com.sl.transport.common.exception.SLException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class JsapiPayServiceImpl implements JsapiPayService {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private TradingService tradingService;
    @Resource
    private BeforePayHandler beforePayHandler;

    @Override
    public TradingDTO createJsapiTrading(TradingDTO tradingDTO) {
        //交易前置处理：检测交易单参数
        Boolean flag = beforePayHandler.checkCreateTrading(tradingDTO);
        if (!flag) {
            throw new SLException(TradingEnum.NATIVE_PAY_FAIL);
        }
        tradingDTO.setEnableFlag(Constants.YES);

        //对交易订单加锁
        Long productOrderNo = tradingDTO.getProductOrderNo();
        String key = TradingCacheConstant.CREATE_PAY + productOrderNo;
        RLock lock = redissonClient.getLock(key);
        try {
            //锁定
            lock.lock();

            //交易前置处理：幂等性处理
            this.beforePayHandler.idempotentCreateTrading(tradingDTO);

            //调用不同的支付渠道进行处理
            JsapiPayHandler jsapiPayHandler = HandlerFactory.get(tradingDTO.getTradingChannel(), JsapiPayHandler.class);
            jsapiPayHandler.createJsapiTrading(tradingDTO);

            //新增或更新交易数据
            TradingEntity tradingEntity = BeanUtil.toBean(tradingDTO, TradingEntity.class);
            flag = this.tradingService.saveOrUpdate(tradingEntity);
            if (!flag) {
                throw new SLException(TradingEnum.SAVE_OR_UPDATE_FAIL);
            }

            return BeanUtil.toBean(tradingEntity, TradingDTO.class);
        } catch (SLException e) {
            throw e;
        } catch (Exception e) {
            log.error("Jsapi预创建异常: tradingDTO = {}", tradingDTO, e);
            throw new SLException(TradingEnum.NATIVE_PAY_FAIL);
        } finally {
            lock.unlock();
        }
    }
}
