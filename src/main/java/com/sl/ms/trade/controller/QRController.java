package com.sl.ms.trade.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zzj
 * @version 1.0
 */
@RequestMapping("qr")
@Controller
public class QRController {

    @GetMapping
    public String qrPay(HttpServletRequest request){
        String useragent = request.getHeader("user-agent");
        System.out.println(useragent);
        // return "redirect:https://qr.alipay.com/bax04226cknt6ciadgms00bb";
        // return "redirect:weixin://wxpay/bizpayurl?pr=3lUrGAZzz";
        return "redirect:https://wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb?prepay_id=wx2016121516420242444321ca0631331346&package=1405458241";
    }

}
