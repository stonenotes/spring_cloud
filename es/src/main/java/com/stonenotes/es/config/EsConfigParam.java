package com.stonenotes.es.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: javan
 * @Date: 2019/6/14
 */
@Component
@Data
@ConfigurationProperties(prefix = "es")
public class EsConfigParam {

    private String[] hosts;
    private int port;
    private int connectTimeout; // 连接超时时间
    private int socketTimeout;// 连接超时时间
    private int connectionRequestTimeout;// 获取连接的超时时间
    private int maxConnectNum; // 最大连接数
    private int maxConnectPerRoute;// 最大路由连接数
    private String schema;

}
