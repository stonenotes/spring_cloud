package com.stonenotes.es;

import com.stonenotes.es.pojo.Item;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: javan
 * @Date: 2019/6/13
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EsApplication.class)
public class EsAppTest {

    @Autowired
    private ElasticSearchTemplate elasticSearchTemplate;

    @Test
    public void existsIndex() {
        System.out.println("---------------" + elasticSearchTemplate.existsIndex("item"));
    }

    @Test
    public void createIndex() throws IOException {
        elasticSearchTemplate.createIndex(Item.class);
    }

    @Test
    public void createIndexAsync() {
        elasticSearchTemplate.createIndexAsync(Item.class);
    }

    @Test
    public void deleteIndex() {
        elasticSearchTemplate.deleteIndex("item");
    }

    @Test
    public void insert() throws IOException {
        RequestBuilder builder = new RequestBuilder();
        builder.setIndexName("item");
        builder.setId("2");
        Item item = new Item(2L, "大米手机7", " 手机",
                "小米", 4499.00, "http://image.leyou.com/13123.jpg");
        elasticSearchTemplate.insert(builder, item);
    }

    @Test
    public void insertMore() throws IOException {
        RequestBuilder builder = new RequestBuilder();
        builder.setIndexName("item");
        List<Object> list = new ArrayList<>();
        for (int i = 100; i < 1000; i++) {
            list.add(new Item((long) i, "华为-" + i, "手机-" + i, "华为", 4499.00 + i, "http://image.leyou.com/" + i + ".jps"));
        }
        boolean result = elasticSearchTemplate.insertMore(builder, list);
        System.out.println("insertMore: " + result);
    }

    @Test
    public void getOne() throws IOException {
        RequestBuilder builder = new RequestBuilder();
        builder.setIndexName("item");
        builder.setId("2");
//        builder.setIncludes(new String[]{"id", "title", "category"});
        builder.setExcludes(new String[]{"brand"});
        Item item = elasticSearchTemplate.getOne(builder, Item.class);
        System.out.println(item.toString());
    }

    @Test
    public void updateOne() throws IOException {
        RequestBuilder builder = new RequestBuilder();
        builder.setIndexName("item");
        builder.setId("2");
        Item item = new Item();
        item.setId(2L);
        item.setTitle("1机密手机");
        item.setImages("111");
        System.out.println(elasticSearchTemplate.update(builder, item));
    }

}
