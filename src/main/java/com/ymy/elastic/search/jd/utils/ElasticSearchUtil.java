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
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class ElasticSearchUtil {

    @Resource
    private RestHighLevelClient client;

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
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
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
        boolean ret = client.indices().exists(request, RequestOptions.DEFAULT);
        return ret;
    }

    /**
     * 在ES中批量插入数据
     *
     * @param index 索引名字
     * @param list  要插入的文档
     * @return true 插入成功 false插入失败
     */
    public boolean bulkAddDocs(String index, List<? extends Object> list) throws Exception {
        // 1、创建批量添加请求
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("30s");

        // 2、将数据放到请求中
        for (int i = 0; i < list.size(); i++) {
            bulkRequest.add(new IndexRequest(index)
                    .source(objectMapper.writeValueAsString(list.get(i)), XContentType.JSON));
        }
        // 3、执行请求
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulkResponse.hasFailures();
    }

    /**
     * 在ES中按照关键字检索文档
     *
     * @param index    索引名字
     * @param keyword  关键字
     * @param pageNo   当前页
     * @param pageSize 页面大小
     * @return 检索结果
     */
    public List<Map<String, Object>> searchByKeyword(String index, String keyword, Integer pageNo, Integer pageSize) throws Exception {
        // 1、创建查询请求
        SearchRequest request = new SearchRequest(index);


        // 设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .requireFieldMatch(false)    //关闭多个高亮字段显示
                .field(Constant.JD_GOODS_FIELD_TITLE)    // 高亮字段
                .preTags("<span style='color:red'>")    // H5标签前缀
                .postTags("</span>");      // H5标签后缀

        // 2、添加搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.matchQuery(Constant.JD_GOODS_FIELD_TITLE, keyword)) // 查询条件
                .from(pageNo) // 当前页
                .size(pageSize) // 页面大小
                .timeout(TimeValue.timeValueSeconds(60)) // 超时时间
                .highlighter(highlightBuilder); // 设置高亮

        // 3、执行请求
        request.source(searchSourceBuilder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        // 4、解析结果
        List<Map<String, Object>> result = new ArrayList<>();
        SearchHit[] hits = response.getHits().getHits(); // 获取到SearchHit数组

        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            // 解析高亮字段(将高亮字段替换到_source中并保存)
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get(Constant.JD_GOODS_FIELD_TITLE);
            if (title != null) {
                Text[] fragments = title.fragments();
                String newTitle = "";
                for (Text fragment : fragments) {
                    newTitle += fragment;
                }
                // 替换原来的title
                sourceAsMap.put(Constant.JD_GOODS_FIELD_TITLE, newTitle);
            }
            result.add(sourceAsMap);
        }
        return result;
    }
}
