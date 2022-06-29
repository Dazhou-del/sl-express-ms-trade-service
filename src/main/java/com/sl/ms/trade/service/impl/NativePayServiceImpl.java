package com.sl.ms.trade.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import com.sl.ms.trade.constant.Constants;
import com.sl.ms.trade.constant.TradingCacheConstant;
import com.sl.ms.trade.constant.TradingConstant;
import com.sl.ms.trade.domain.TradingDTO;
import com.sl.ms.trade.entity.TradingEntity;
import com.sl.ms.trade.enums.TradingEnum;
import com.sl.ms.trade.handler.BeforePayHandler;
import com.sl.ms.trade.handler.HandlerFactory;
import com.sl.ms.trade.handler.NativePayHandler;
import com.sl.ms.trade.service.NativePayService;
import com.sl.ms.trade.service.QRCodeService;
import com.sl.ms.trade.service.TradingService;
import com.sl.transport.common.exception.SLException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Native支付方式Face接口：商户生成二维码，用户扫描支付
 *
 * @author zzj
 * @version 1.0
 */
@Service
@Slf4j
public class NativePayServiceImpl implements NativePayService {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private TradingService tradingService;
    @Resource
    private BeforePayHandler beforePayHandler;
    @Resource
    private QRCodeService qrCodeService;

    @Override
    public String queryQrCodeUrl(Long productOrderNo) {
        TradingEntity trading = this.tradingService.findTradByProductOrderNo(productOrderNo);
        if (StrUtil.equals(trading.getTradingState(), TradingConstant.YJS)) {
            //订单已完成，不返回二维码
            throw new SLException(TradingEnum.TRADING_STATE_SUCCEED);
        }
        return trading.getQrCode();
    }

    @Override
    public TradingDTO createDownLineTrading(TradingDTO tradingDTO) {
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
            NativePayHandler nativePayHandler = HandlerFactory.get(tradingDTO.getTradingChannel(), NativePayHandler.class);
            nativePayHandler.createDownLineTrading(tradingDTO);

            //生成统一收款二维码
            String placeOrderMsg = tradingDTO.getPlaceOrderMsg();
            String qrCode = this.qrCodeService.generate(placeOrderMsg);
            tradingDTO.setQrCode(qrCode);

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
            log.error("统一收单线下交易预创建异常:{}", ExceptionUtil.stacktraceToString(e));
            throw new SLException(TradingEnum.NATIVE_PAY_FAIL);
        } finally {
            lock.unlock();
        }
    }
}
