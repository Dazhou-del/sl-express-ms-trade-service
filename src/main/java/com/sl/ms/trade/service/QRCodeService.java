package com.sl.ms.trade.service;

/**
 * @author zzj
 * @version 1.0
 */
public interface QRCodeService {

    /**
     * 生成二维码
     *
     * @param content 二维码中的内容
     * @return 图片base64数据
     */
    String generate(String content);

}
