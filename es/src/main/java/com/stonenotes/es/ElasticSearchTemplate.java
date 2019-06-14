package com.stonenotes.es;

import com.alibaba.fastjson.JSON;
import com.stonenotes.es.annotations.MyDocument;
import com.stonenotes.es.annotations.MyField;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @Author: javan
 * @Date: 2019/6/13
 * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.1/java-rest-high-create-index.html
 */
@Component
public class ElasticSearchTemplate {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public boolean existsIndex(String index) {
        GetIndexRequest request = new GetIndexRequest(index);
        try {
            boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
            return exists;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 创建index并mapping
     *
     * @param clazz
     * @throws IOException
     */
    public void createIndex(Class clazz) {
        try {
            CreateIndexRequest request = createIndexRequest(clazz);
            if (request == null) return;
            restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void createIndexAsync(Class clazz) {
        try {
            CreateIndexRequest request = createIndexRequest(clazz);
            if (request == null) return;
            restHighLevelClient.indices().createAsync(request, RequestOptions.DEFAULT, new ActionListener<CreateIndexResponse>() {
                @Override
                public void onResponse(CreateIndexResponse createIndexResponse) {
                    System.out.println("======= createIndexAsync success==" + createIndexResponse.index());
                }

                @Override
                public void onFailure(Exception e) {
                    System.out.println("======= createIndexAsync error==" + e.getMessage());
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void deleteIndex(String indexName) {
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        try {
            restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void insert(RequestBuilder builder, Object param) throws IOException {
        IndexRequest request = new IndexRequest(builder.getIndexName());
        request.source(JSON.toJSONString(param), XContentType.JSON);
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        System.out.println(response.status().getStatus());
    }

    public boolean insertMore(RequestBuilder builder, List<Object> params) throws IOException {
        BulkRequest request = new BulkRequest();
        params.forEach(item ->
                request.add(new IndexRequest(builder.getIndexName()).source(JSON.toJSONString(item), XContentType.JSON)));
        BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        System.out.println(response.status().getStatus());
        return response.status() == RestStatus.OK;
    }

    public <T> T getOne(RequestBuilder builder, Class<T> clazz) throws IOException {
        GetRequest request = new GetRequest(builder.getIndexName(), builder.getId());
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, builder.getIncludes(), builder.getExcludes());
        request.fetchSourceContext(fetchSourceContext);
        GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
        if (!response.isExists()) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(response.getSource()), clazz);
    }

    public boolean update(RequestBuilder builder, Object param) throws IOException {
        UpdateRequest request = new UpdateRequest(builder.getIndexName(), builder.getId());
        request.doc(JSON.toJSONString(param), XContentType.JSON);
        UpdateResponse response = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        return response.status() == RestStatus.OK;
    }

    /**
     * 创建index映射
     *
     * @param clazz
     * @return
     * @throws IOException
     */
    private CreateIndexRequest createIndexRequest(Class clazz) throws IOException {
        MyDocument myDocument = (MyDocument) clazz.getAnnotation(MyDocument.class);
        if (existsIndex(myDocument.indexName())) {
            return null;
        }
        CreateIndexRequest request = new CreateIndexRequest(myDocument.indexName());
        request.settings(Settings.builder()
                .put("index.number_of_shards", myDocument.shards())
                .put("index.number_of_replicas", myDocument.replicas())
        );
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                Field[] fs = clazz.getDeclaredFields();
                for (Field f : fs) {
                    System.out.println(f.getName());
                    MyField myField = f.getAnnotation(MyField.class);
                    if (myField != null) {
                        builder.startObject(f.getName());
                        System.out.println(myField.type().getValue() + ", " + myField.analyzer());
                        String type = myField.type().getValue();
                        builder.field("type", type);
                        String analyzer = myField.analyzer();
                        if (!StringUtils.isEmpty(analyzer)) {
                            builder.field("analyzer", analyzer);
                        }
                        builder.endObject();
                    }
                }
            }
            builder.endObject();
        }
        builder.endObject();
        request.mapping(builder);
        return request;
    }

}
