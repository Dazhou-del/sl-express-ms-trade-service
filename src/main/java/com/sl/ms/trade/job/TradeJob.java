package com.sl.ms.trade.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import com.sl.ms.trade.entity.RefundRecordEntity;
import com.sl.ms.trade.entity.TradingEntity;
import com.sl.ms.trade.enums.RefundStatusEnum;
import com.sl.ms.trade.enums.TradingStateEnum;
import com.sl.ms.trade.service.BasicPayService;
import com.sl.ms.trade.service.RefundRecordService;
import com.sl.ms.trade.service.TradingService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 交易任务，主要是查询订单的支付状态 和 退款的成功状态
 *
 * @author zzj
 * @version 1.0
 */
@Component
@Slf4j
public class TradeJob {

    @Value("${sl.job.trading.count:100}")
    private Integer tradingCount;
    @Value("${sl.job.refund.count:100}")
    private Integer refundCount;
    @Resource
    private TradingService tradingService;
    @Resource
    private RefundRecordService refundRecordService;
    @Resource
    private BasicPayService basicPayService;

    /**
     * 分片广播方式查询支付状态
     * 逻辑：每次最多查询{tradingCount}个未完成的交易单，交易单id与shardTotal取模，值等于shardIndex进行处理
     */
    @XxlJob("tradingJob")
    public void tradingJob() {
        // 分片参数
        int shardIndex = NumberUtil.max(XxlJobHelper.getShardIndex(), 0);
        int shardTotal = NumberUtil.max(XxlJobHelper.getShardTotal(), 1);

        List<TradingEntity> list = this.tradingService.findListByTradingState(TradingStateEnum.FKZ, tradingCount);
        if (CollUtil.isEmpty(list)) {
            XxlJobHelper.log("查询到交易单列表为空！shardIndex = {}, shardTotal = {}", shardIndex, shardTotal);
            return;
        }

        for (TradingEntity trading : list) {
            if (trading.getTradingOrderNo() % shardTotal != shardIndex) {
                continue;
            }
            try {
                //查询交易单
                this.basicPayService.queryTrading(trading.getTradingOrderNo());
            } catch (Exception e) {
                XxlJobHelper.log("查询交易单出错！shardIndex = {}, shardTotal = {}, trading = {}", shardIndex, shardTotal, trading, e);
            }
        }
    }

    /**
     * 分片广播方式查询退款状态
     */
    @XxlJob("refundJob")
    public void refundJob() {
        // 分片参数
        int shardIndex = NumberUtil.max(XxlJobHelper.getShardIndex(), 0);
        int shardTotal = NumberUtil.max(XxlJobHelper.getShardTotal(), 1);

        List<RefundRecordEntity> list = this.refundRecordService.findListByRefundStatus(RefundStatusEnum.SENDING, refundCount);
        if (CollUtil.isEmpty(list)) {
            XxlJobHelper.log("查询到退款单列表为空！shardIndex = {}, shardTotal = {}", shardIndex, shardTotal);
            return;
        }

        for (RefundRecordEntity refundRecord : list) {
            if (refundRecord.getRefundNo() % shardTotal != shardIndex) {
                continue;
            }
            try {
                //查询退款单
                this.basicPayService.queryRefundTrading(refundRecord.getRefundNo());
            } catch (Exception e) {
                XxlJobHelper.log("查询退款单出错！shardIndex = {}, shardTotal = {}, refundRecord = {}", shardIndex, shardTotal, refundRecord, e);
            }
        }
    }
}
