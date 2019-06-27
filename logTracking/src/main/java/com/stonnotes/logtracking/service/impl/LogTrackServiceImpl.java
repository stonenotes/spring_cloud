package com.stonnotes.logtracking.service.impl;

import com.alibaba.fastjson.JSON;
import com.stonnotes.logtracking.config.EsConfigParam;
import com.stonnotes.logtracking.pojo.LogInfo;
import com.stonnotes.logtracking.request.LogSearchRequest;
import com.stonnotes.logtracking.result.Result;
import com.stonnotes.logtracking.service.LogTrackService;
import com.stonnotes.logtracking.utils.Constants;
import com.stonnotes.logtracking.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.ScrollableHitSource;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: javan
 * @Date: 2019/6/20
 */
@Service
@Slf4j
public class LogTrackServiceImpl implements LogTrackService {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    private EsConfigParam esConfigParam;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public Result<List<LogInfo>> query(LogSearchRequest requestParam) throws IOException {
        long lastTime = System.currentTimeMillis();
        String id = requestParam.getId();
        String startTime = requestParam.getStartTime();
        String endTime = requestParam.getEndTime();
        String level = requestParam.getLevel();
        String message = requestParam.getMessage();
        int pageNum = requestParam.getPageNum() == 0 ? 0 : requestParam.getPageNum() - 1;
        int pageSize = requestParam.getPageSize() <= 0 ? 20 : requestParam.getPageSize();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 添加时间范围查询
        addTimeRangeQuery(boolQuery, startTime, endTime);
        if (!StringUtils.isEmpty(id)) {
//            searchSourceBuilder.query(QueryBuilders.matchQuery("id", id).operator(Operator.AND));
            boolQuery.must(QueryBuilders.termsQuery("id", id));
        }
        if (!StringUtils.isEmpty(level)) {
//             searchSourceBuilder.query(QueryBuilders.termsQuery("level", level));
//            searchSourceBuilder.query(QueryBuilders.matchQuery("level", level).operator(Operator.AND));
             boolQuery.must(QueryBuilders.termsQuery("level", level));
        }
        if (!StringUtils.isEmpty(message)) {
            // searchSourceBuilder.query(QueryBuilders.matchQuery("message", message).operator(Operator.AND));
            boolQuery.must(QueryBuilders.matchQuery("message", message));
        }
        searchSourceBuilder.query(boolQuery);
        SearchRequest request = new SearchRequest(Constants.OT_LOTTETY_INDEX_NAME);
        request.source(searchSourceBuilder
                .from(pageNum * pageSize)
                .size(pageSize)
                .sort("createTime", SortOrder.DESC));
        SearchResponse search =  restHighLevelClient.search(request, RequestOptions.DEFAULT);
        System.out.println("Hits:" + search.getHits().getTotalHits());
        List<LogInfo> list = new ArrayList<>();
        search.getHits().forEach(e -> {
            LogInfo logInfo = JSON.parseObject(e.getSourceAsString(), LogInfo.class);
            logInfo.setId(e.getId());
            list.add(logInfo);
            System.out.println(e.getId() + ":" + DateUtil.dateFormat(logInfo.getCreateTime()) + "------" + logInfo.getMessage());
        });
        Result result = Result.success(list);
        result.setTotal(search.getHits().getTotalHits().value);
        long remain = search.getHits().getTotalHits().value % pageSize;
        long totalPage = search.getHits().getTotalHits().value / pageSize;
        result.setTotalPage(remain == 0 ? totalPage : totalPage + 1);
        System.out.println("一共执行时间: " + (System.currentTimeMillis() - lastTime));
        return result;
    }

    @Override
    public LogInfo queryById(String id) throws IOException {
        GetRequest request = new GetRequest(Constants.OT_LOTTETY_INDEX_NAME, id);   // 指定routing的数据，查询也要指定
        GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
        LogInfo logInfo = JSON.parseObject(response.getSourceAsString(), LogInfo.class);
        if (logInfo != null){
            logInfo.setId(response.getId());
            System.out.println(logInfo.toString());
        }
        return logInfo;
    }

    @Override
    public void deletePeriod() throws IOException {
        long lastTime = System.currentTimeMillis();
        long diffTime = esConfigParam.getClearTime() * 24 * 60 * 60 * 1000L;
        long timePoint = System.currentTimeMillis() - diffTime;
        DeleteByQueryRequest request = new DeleteByQueryRequest(Constants.OT_LOTTETY_INDEX_NAME);
        request.setQuery(QueryBuilders.rangeQuery("createTime").lt(timePoint));
        BulkByScrollResponse bulkResponse = restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
        try {
            bulkResponse = restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bulkResponse == null)
            return;
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
        bulkFailures.forEach(e ->
                System.err.println("Cause:" + e.getCause().getMessage() + "Index:" + e.getIndex() + ",Type:" + e.getType() + ",Id:" + e.getId())
        );
        System.out.println("一共执行时间: " + (System.currentTimeMillis() - lastTime));
    }

    @Override
    public void insert(List<LogInfo> list) {
        if (list == null || list.size() == 0)
            return;
        long lastTime = System.currentTimeMillis();
        BulkRequest request = new BulkRequest();
        list.forEach(item->{
            IndexRequest indexRequest = new IndexRequest(Constants.OT_LOTTETY_INDEX_NAME).id(item.getId());
            indexRequest.source(JSON.toJSONString(item), XContentType.JSON);
            request.add(indexRequest);
        });
        BulkResponse bulkResponse = null;
        try {
            bulkResponse = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("批量插入日志失败：{}", e);
        }
        if (bulkResponse == null)
            return;
        System.out.println("批量插入日志成功: " + bulkResponse);
        System.out.println("批量插入一共执行时间: " + (System.currentTimeMillis() - lastTime));
    }

    private void addTimeRangeQuery(BoolQueryBuilder boolQueryBuilder, String startTime, String endTime){
        if (StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)) {
            return;
        }
        RangeQueryBuilder rangeQueryBuilder = null;
        long startTimeSearch;
        if (!StringUtils.isEmpty(startTime)) {
            startTimeSearch = DateUtil.stringToDate(startTime, DATE_FORMAT).getTime();
            rangeQueryBuilder = QueryBuilders.rangeQuery("createTime").gte(startTimeSearch);
        }
        if (!StringUtils.isEmpty(endTime)) {
            long endTimeSearch = DateUtil.stringToDate(endTime, DATE_FORMAT).getTime();
            if (rangeQueryBuilder == null) {
                rangeQueryBuilder = QueryBuilders.rangeQuery("createTime");
            }
            rangeQueryBuilder.lte(endTimeSearch);
        }
        if (rangeQueryBuilder != null) {
            boolQueryBuilder.must(rangeQueryBuilder);
        }
    }
}
