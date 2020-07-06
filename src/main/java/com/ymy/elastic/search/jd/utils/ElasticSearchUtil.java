package com.ymy.elastic.search.jd.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ElasticSearchUtil {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 在ES中创建索引
     */
    public boolean createIndex(String index) throws Exception {
        if (indexIsExists(index)) {
            return false;
        }
        // 1、创建索引请求
        CreateIndexRequest request = new CreateIndexRequest(index);
        // 2、执行创建请求
        CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        // 3、返回结果
        return response.isAcknowledged();
    }

    /**
     * 测试索引是否存在
     */
    public boolean indexIsExists(String index) throws Exception {
        // 1、获得索引请求
        GetIndexRequest request = new GetIndexRequest(index);
        // 2、执行请求
        boolean ret = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        return ret;
    }

    /**
     * 在ES中批量插入数据
     */
    public boolean bulkAddDocs(String index, List<? extends Object> list) throws Exception {
        // 1、创建批量添加请求
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("30s");

        // 2、将数据放到请求中
        for (int i = 0; i < list.size(); i++) {
            bulkRequest.add(
                    new IndexRequest(index)
                            .source(objectMapper.writeValueAsString(list.get(i)), XContentType.JSON));
        }
        // 3、执行请求
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulkResponse.hasFailures();
    }

    /**
     * 在ES中搜索
     */
    public List<Map<String, Object>> searchByKeyword(String index, String keyword, Integer pageNo, Integer pageSize) throws Exception {
        // 1、创建查询请求
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 2、添加搜索条件
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(Constant.JD_GOODS_FIELD_TITLE, keyword);
        searchSourceBuilder.query(matchQueryBuilder);
        // 设置分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);
        //设置查询的超时时间
        searchSourceBuilder.timeout(TimeValue.timeValueSeconds(60));

        // 设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.requireFieldMatch(false); //关闭多个高亮字段显示
        highlightBuilder.field(Constant.JD_GOODS_FIELD_TITLE);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);

        // 3、执行请求
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 4、解析结果
        List<Map<String, Object>> result = new ArrayList<>();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            // 解析高亮的字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get(Constant.JD_GOODS_FIELD_TITLE);
            if (title != null) {
                Text[] fragments = title.fragments();
                String n_title = "";
                for (Text fragment : fragments) {
                    n_title += fragment;
                }
                // 替换原来的title
                sourceAsMap.put(Constant.JD_GOODS_FIELD_TITLE, n_title);
            }
            result.add(sourceAsMap);
        }
        return result;
    }
}
