package com.stonnotes.logtracking.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: javan
 * @Date: 2019/6/13
 */
@Configuration
public class EsConfiguration {

    @Autowired
    private  EsConfigParam esConfigParam;

    @Bean
    public RestHighLevelClient client() {
        List<HttpHost> hostList = new ArrayList<>();
        for (String host : esConfigParam.getHosts()) {
            hostList.add(new HttpHost(host, esConfigParam.getPort(), esConfigParam.getSchema()));
        }
        RestClientBuilder builder = RestClient.builder(hostList.toArray(new HttpHost[hostList.size()]));
        // 异步httpclient连接延时配置
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(esConfigParam.getConnectTimeout());
            requestConfigBuilder.setSocketTimeout(esConfigParam.getSocketTimeout());
            requestConfigBuilder.setConnectionRequestTimeout(esConfigParam.getConnectionRequestTimeout());
            return requestConfigBuilder;
        });
        // 异步httpclient连接数配置
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(esConfigParam.getMaxConnectNum());
            httpClientBuilder.setMaxConnPerRoute(esConfigParam.getMaxConnectPerRoute());
            return httpClientBuilder;
        });
        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }



}
