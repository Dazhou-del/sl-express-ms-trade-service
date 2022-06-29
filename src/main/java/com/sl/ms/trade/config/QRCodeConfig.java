package com.sl.ms.trade.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 二维码生成参数配置
 *
 * @author zzj
 * @version 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "sl.qrcode")
public class QRCodeConfig {

    //边距，二维码和背景之间的边距
    private Integer margin = 2;
    // 二维码颜色，默认黑色
    private String foreColor = "#000000";
    //背景色，默认白色
    private String backColor = "#ffffff";
    //纠错级别，可选参数：L、M、Q、H，默认：M
    //低级别的像素块更大，可以远距离识别，但是遮挡就会造成无法识别。高级别则相反，像素块小，允许遮挡一定范围，但是像素块更密集。
    private String errorCorrectionLevel = "M";
    //宽
    private Integer width = 300;
    //高
    private Integer height = 300;
}
