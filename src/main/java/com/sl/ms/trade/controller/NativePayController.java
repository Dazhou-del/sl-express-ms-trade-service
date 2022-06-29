package com.sl.ms.trade.controller;

import com.sl.ms.trade.domain.TradingDTO;
import com.sl.ms.trade.service.NativePayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Native支付方式Face接口：商户生成二维码，用户扫描支付
 */
@RequestMapping("native")
@RestController
@Api(tags = "Native支付")
public class NativePayController {

    @Resource
    private NativePayService nativePayService;

    /***
     * @description 统一收单线下交易预创建
     * 收银员通过收银台或商户后台调用此接口，生成二维码后，展示给用户，由用户扫描二维码完成订单支付。
     * @param tradingDTO 订单单
     * @return 二维码路径
     */
    @PostMapping
    @ApiOperation(value = "统一收单线下交易", notes = "统一收单线下交易")
    @ApiImplicitParam(name = "tradingDTO", value = "交易单", required = true)
    public TradingDTO createDownLineTrading(@RequestBody TradingDTO tradingDTO) {
        return this.nativePayService.createDownLineTrading(tradingDTO);
    }

    @GetMapping("qrcode/{orderNo}")
    @ApiOperation(value = "查看二维码", notes = "查看二维码")
    @ApiImplicitParam(name = "orderNo", value = "查看二维码")
    public String queryQrCode(@PathVariable("orderNo") Long orderNo) {
        return this.nativePayService.queryQrCodeUrl(orderNo);
    }

}
