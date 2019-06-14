package com.stonenotes.es;

import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsApplicationTests {

    private RestHighLevelClient mClient;

    @Test
    public void contextLoads() {
        System.out.println("========contextLoads========");
    }

    @Before
    public void init(){
        //Low Level Client init
        RestClientBuilder builder = RestClient.builder(
                new HttpHost("localhost", 9200, "http"));
        //High Level Client init
         mClient = new RestHighLevelClient(builder);
    }

    @Test
    public void getRequest() {
//        GetRequest getRequest = new GetRequest("goods", "_doc", "1");
        GetRequest getRequest = new GetRequest("goods");
        try {
            GetResponse getResponse = mClient.get(getRequest, RequestOptions.DEFAULT);
            System.out.println(getResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
