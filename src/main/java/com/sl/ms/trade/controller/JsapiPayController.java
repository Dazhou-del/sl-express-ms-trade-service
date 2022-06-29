package com.sl.ms.trade.controller;

import com.sl.ms.trade.domain.TradingDTO;
import com.sl.ms.trade.service.JsapiPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Native支付方式Face接口：商户生成二维码，用户扫描支付
 */
@RequestMapping("jsapi")
@RestController
@Api(tags = "Jsapi支付")
public class JsapiPayController {

    @Resource
    private JsapiPayService jsapiPayService;

    /***
     * 统一jsapi交易预创建
     * 商户系统先调用该接口在微信支付服务后台生成预支付交易单，返回正确的预支付交易会话标识后再按Native、
     * JSAPI、APP等不同场景生成交易串调起支付。
     * @param tradingDTO 交易单
     *
     * @return 交易单，支付串码
     */
    @PostMapping
    @ApiOperation(value = "jsapi预交易", notes = "jsapi预交易")
    @ApiImplicitParam(name = "tradingDTO", value = "交易单", required = true)
    public TradingDTO createJsapiTrading(@RequestBody TradingDTO tradingDTO) {
        return this.jsapiPayService.createJsapiTrading(tradingDTO);
    }

}
