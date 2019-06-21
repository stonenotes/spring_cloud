package com.stonenotes.logtracking;

import com.alibaba.fastjson.JSON;
import com.stonnotes.logtracking.LogTrackingApplication;
import com.stonnotes.logtracking.config.EsConfigParam;
import com.stonnotes.logtracking.pojo.LogInfo;
import com.stonnotes.logtracking.utils.Constants;
import com.stonnotes.logtracking.utils.DataUtil;
import com.stonnotes.logtracking.utils.DateUtil;
import com.stonnotes.logtracking.utils.IdWorker;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.ScrollableHitSource;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @Author: javan
 * @Date: 2019/6/19
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LogTrackingApplication.class)
public class LogTrackingTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private EsConfigParam esConfigParam;

    @Autowired
    IdWorker idWorker;

    String stackTrace = "java.lang.ArithmeticException: / by zero\n" +
            "at com.stonenotes.lombok.LombokTest.logError(LombokTest.java:45)\n" +
            "at com.stonenotes.lombok.LombokTest.error(LombokTest.java:33)\n" +
            "at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
            "at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n" +
            "at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n" +
            "at java.lang.reflect.Method.invoke(Method.java:498)\n" +
            "at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)\n" +
            "at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)\n" +
            "at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)\n" +
            "at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)\n";

    @Test
    public void testJson() {
        //System.out.println(JSON.toJSONString(new LogInfo("", 123l, "ddd", "", "aaa", "bbb", "ccc")));

//        long createTime = DateUtil.stringToDate("2019-06-19 14:35:30.347").getTime();
//        long diffTime = esConfigParam.getClearTime() * 24 * 60 * 60 * 1000L;
//        long timePoint = System.currentTimeMillis() - diffTime;
//        System.out.println((createTime - timePoint) + "\n" + createTime + "\n" + timePoint + "\n" + diffTime);

        for (int i = 0; i < 1000; i++) {
            System.out.println(DataUtil.getRandomMessage());
        }
    }

    // 添加一条数据
    @Test
    public void insertLog() throws IOException {
        long lastTime = System.currentTimeMillis();
        // long createTime = DateUtil.stringToDate("2019-06-20 13:35:30.347").getTime();
//        long createTime = DateUtil.stringToDate("2019-06-19 14:35:30.347").getTime();
//        long createTime = DateUtil.stringToDate("2019-06-20 15:41:30.347").getTime();
//        long createTime = DateUtil.stringToDate("2019-06-20 15:41:31.347").getTime();
//        long createTime = DateUtil.stringToDate("2019-06-20 15:40:35.347").getTime();
        long createTime = DateUtil.stringToDate("2019-06-20 16:40:50.347").getTime();
        String id = idWorker.nextId();
        LogInfo logInfo = new LogInfo(id, createTime, "ERROR", "main",
                "com.stonenotes.lombok.LombokTest", "大于等于", stackTrace, "");
        IndexRequest request = new IndexRequest("logtrack", "_doc", id);
        request.source(JSON.toJSONString(logInfo), XContentType.JSON);
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        System.out.println(response);
        System.out.println("一共执行时间: " + (System.currentTimeMillis() - lastTime));
    }

    // 批量添加数据
    @Test
    public void buildAdd() throws IOException {
        long lastTime = System.currentTimeMillis();
        BulkRequest request = new BulkRequest();
//        long createTime = DateUtil.stringToDate("2019-06-20 11:38:30.347").getTime();
//        long createTime = DateUtil.stringToDate("2019-06-20 11:48:30.347").getTime();
        long createTime = DateUtil.stringToDate("2019-06-20 11:28:30.347").getTime();
        for (int i = 1; i <= 10000; i++) {
            String id = idWorker.nextId();
            LogInfo logInfo;
            if (i % 15 == 0) {
                logInfo = new LogInfo(id, createTime + (i * 1000 + i), "ERROR", "main",
                        "com.stonenotes.lombok.LombokTest", DataUtil.getRandomMessage(), stackTrace, "");
            } else {
                logInfo = new LogInfo(id, createTime + (i * 1000 + i), "INFO", "main",
                        "com.stonenotes.lombok.LombokTest", DataUtil.getRandomMessage(), null, null);
            }
            IndexRequest indexRequest = new IndexRequest(Constants.OT_LOTTETY_INDEX_NAME).id(id);
            indexRequest.source(JSON.toJSONString(logInfo), XContentType.JSON);
            request.add(indexRequest);
        }
        BulkResponse bulkResponse = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        System.out.println(bulkResponse);
        System.out.println("一共执行时间: " + (System.currentTimeMillis() - lastTime));
    }

    /**
     * matchQuery
     * 排序，高亮查询
     *
     * @return
     * @throws IOException
     */
    @Test
    public void search() throws IOException {
        long lastTime = System.currentTimeMillis();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
//                .query(QueryBuilders.matchQuery("level", "ERROR"))
                .query(QueryBuilders.matchQuery("message", "opens"))
                .sort("createTime", SortOrder.DESC) // 根据分数倒序排序
                .from(0)    // 返回结果开始位置
                .size(25)    // 返回结果数量
                .timeout(TimeValue.timeValueSeconds(10))    // 超时
                .highlighter(new HighlightBuilder()
                        .field("message", 200)
                        .preTags("<pre>").postTags("</pre>"));
        SearchRequest searchRequest = new SearchRequest("logtrack").source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            System.out.println("Hits:" + searchResponse.getHits().getTotalHits());
            searchResponse.getHits().forEach(e -> {
                System.out.println("分数：" + e.getScore() + "，结果：" + e.getSourceAsString());
                Map<String, HighlightField> highlightFields = e.getHighlightFields();
                for (String key : highlightFields.keySet()) {
                    HighlightField field = highlightFields.get(key);
                    System.out.println(key + "：" + field.fragments()[0]/* + "," + field.fragments().length*/);
                }

            });
        } catch (ElasticsearchException e) {
            if (e.status() == RestStatus.NOT_FOUND) {
                System.out.println("Index Not Found-" + e.getIndex());
            }
        }
        System.out.println("一共执行时间: " + (System.currentTimeMillis() - lastTime));
    }

    /**
     * termQuery
     * rangeQuery
     * prefixQuery
     * wildcardQuery
     *
     * @return
     * @throws IOException
     */
    @Test
    public void termSearch() throws IOException {
        long lastTime = System.currentTimeMillis();
//        long createTime = DateUtil.stringToDate("2019-06-20 15:30:30.347").getTime();
//        long createTime2 = DateUtil.stringToDate("2019-06-20 15:40:30.347").getTime();

//        long createTime2 = DateUtil.stringToDate("2019-06-20 15:41:30.347").getTime();
        long createTime2 = DateUtil.stringToDate("2019-06-20 15:41:31.347").getTime();
        long createTime = DateUtil.stringToDate("2019-06-20 15:40:35.347").getTime();
//        long createTime = DateUtil.stringToDate("2019-06-20 15:40:50.347").getTime();

        SearchRequest request = new SearchRequest("logtrack");
        String[] includeFields = new String[]{"_id", "createTime", "message"};
        String[] excludeFields = new String[]{"level"};
        request.source(new SearchSourceBuilder()
                        // .query(QueryBuilders.matchQuery("message", "无敌"))
                        // 关键字查询
                        //.query(QueryBuilders.termsQuery("message", "爱情", "面向"))
                        // 范围查询
                        .query(QueryBuilders.rangeQuery("createTime").gt(createTime).lte(createTime2))
                        // 前缀查询
                        //.query(QueryBuilders.prefixQuery("username", "孙"))
                        // 通配符查询
//                .query(QueryBuilders.wildcardQuery("username", "西蒙*"))
                        .fetchSource(includeFields, excludeFields)  // 过滤源
                        .from(0)
                        .size(10)
                        .sort("createTime", SortOrder.DESC)
        );
        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        System.out.println("Hits:" + search.getHits().getTotalHits());
        search.getHits().forEach(e -> {
            LogInfo logInfo = JSON.parseObject(e.getSourceAsString(), LogInfo.class);
            System.out.println(e.getId() + ":" + DateUtil.dateFormat(logInfo.getCreateTime()) + "------" + logInfo.getMessage());
        });
        System.out.println("一共执行时间: " + (System.currentTimeMillis() - lastTime));
    }

    /**
     * 聚合查询
     *
     * @return
     * @throws IOException
     */
    @Test
    public void aggSearch() throws IOException {
        long lastTime = System.currentTimeMillis();
        SearchRequest request = new SearchRequest("logtrack");
        request.source(new SearchSourceBuilder()
                .query(QueryBuilders.matchAllQuery())
                .aggregation(AggregationBuilders.avg("timeAVG").field("createTime"))
                .from(0)
                .size(5)
                .sort("createTime", SortOrder.DESC)
        );
        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        System.out.println("Hits:" + search.getHits().getTotalHits());
        search.getHits().forEach(e -> {
            System.out.println(e.getSourceAsString());
        });
        Avg avg = search.getAggregations().get("timeAVG");
        System.out.println("平均值：" + new DecimalFormat("0.00").format(avg.getValue()));
        System.out.println("一共执行时间: " + (System.currentTimeMillis() - lastTime));
    }

    /**
     * 复合查询
     *
     * @return
     * @throws IOException
     */
    @Test
    public void boolSearch() throws IOException {
        long lastTime = System.currentTimeMillis();
        long createTime = DateUtil.stringToDate("2019-06-20 15:40:35.347").getTime();
        SearchRequest request = new SearchRequest("logtrack");
        request.source(new SearchSourceBuilder()
                .query(QueryBuilders.boolQuery()
                        .must(QueryBuilders.rangeQuery("createTime").gt(createTime))
                        .mustNot(QueryBuilders.termQuery("message", "大于"))
                )
                .from(0)
                .size(5)
                .sort("createTime", SortOrder.DESC)
        );
        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        System.out.println("Hits:" + search.getHits().getTotalHits());
        search.getHits().forEach(e -> {
            System.out.println(e.getSourceAsString());
        });
        System.out.println("一共执行时间: " + (System.currentTimeMillis() - lastTime));
    }

    /**
     * 根据查询条件删除
     *
     * @return
     * @throws IOException
     */
    @Test
    public void deleteByQuery() throws IOException {
        long lastTime = System.currentTimeMillis();
        long createTime = DateUtil.stringToDate("2019-06-20 13:35:30.347").getTime();
        DeleteByQueryRequest request = new DeleteByQueryRequest("logtrack");
        // request.setConflicts("proceed");    // 发生冲突即略过
        // request.setQuery(QueryBuilders.matchQuery("flag","2"));
        // request.setQuery(QueryBuilders.rangeQuery("createTime").lte(createTime));
//        request.setQuery(QueryBuilders.matchQuery("level", "ERROR"));
        request.setQuery(QueryBuilders.matchQuery("level", "INFO"));
        BulkByScrollResponse bulkResponse = restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
        TimeValue timeTaken = bulkResponse.getTook();
        boolean timedOut = bulkResponse.isTimedOut();
        long totalDocs = bulkResponse.getTotal();
        long updatedDocs = bulkResponse.getUpdated();
        long deletedDocs = bulkResponse.getDeleted();
        long batches = bulkResponse.getBatches();
        long noops = bulkResponse.getNoops();
        long versionConflicts = bulkResponse.getVersionConflicts();
        System.out.println("花费时间：" + timeTaken + ",是否超时：" + timedOut + ",总文档数：" + totalDocs + ",更新数：" +
                updatedDocs + ",删除数：" + deletedDocs + ",批量次数：" + batches + ",跳过数：" + noops + ",冲突数：" + versionConflicts);
        List<ScrollableHitSource.SearchFailure> searchFailures = bulkResponse.getSearchFailures();  // 搜索期间的故障
        searchFailures.forEach(e -> {
            System.err.println("Cause:" + e.getReason().getMessage() + "Index:" + e.getIndex() + ",NodeId:" + e.getNodeId() + ",ShardId:" + e.getShardId());
        });
        List<BulkItemResponse.Failure> bulkFailures = bulkResponse.getBulkFailures();   // 批量索引期间的故障
        bulkFailures.forEach(e -> {
            System.err.println("Cause:" + e.getCause().getMessage() + "Index:" + e.getIndex() + ",Type:" + e.getType() + ",Id:" + e.getId());
        });
        System.out.println("一共执行时间: " + (System.currentTimeMillis() - lastTime));
    }

    @Test
    public void getOne() {
        GetRequest request = new GetRequest("logtrack", "");   // 指定routing的数据，查询也要指定
        GetResponse response = null;
        try {
            response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println(e.getMessage());
            return;
        }
        LogInfo logInfo = JSON.parseObject(response.getSourceAsString(), LogInfo.class);
        logInfo.setId(response.getId());
        System.out.println(logInfo.toString());
    }
}
