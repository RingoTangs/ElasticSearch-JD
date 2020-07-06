package com.ymy.elastic.search.jd.service;

import com.ymy.elastic.search.jd.entity.Content;
import com.ymy.elastic.search.jd.entity.support.SimpleResponse;
import com.ymy.elastic.search.jd.utils.ElasticSearchUtil;
import com.ymy.elastic.search.jd.utils.HTMLParseUtil;
import org.apache.http.HttpStatus;
import org.elasticsearch.rest.RestStatus;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class ContentService {
    @Resource
    private HTMLParseUtil htmlParseUtil;

    @Resource
    private ElasticSearchUtil elasticSearchUtil;

    /**
     * 1、解析数据放到ES库中
     */
    public Boolean parseContent(String index, String keyword) throws Exception {

        // 1、创建索引
        elasticSearchUtil.createIndex("jd_goods");

        // 1、解析HTML页面返回List集合
        List<Content> contents = htmlParseUtil.parseJD(keyword);
        // 2、把List集合批量插入到ES中
        return elasticSearchUtil.bulkAddDocs(index, contents);
    }

    /**
     * 2、ES中搜索
     */
    public SimpleResponse searchByKeyword(String index, String keyword, Integer pageNo, Integer pageSize) {
        try {
            List<Map<String, Object>> data = elasticSearchUtil.searchByKeyword(index, keyword, pageNo, pageSize);
            if (data.size() > 0) {
                return new SimpleResponse(HttpStatus.SC_OK,"查询成功",data);
            }
            return new SimpleResponse(HttpStatus.SC_NOT_FOUND,"查询失败",null);
        } catch (Exception e) {
            return new SimpleResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR,"服务器内部错误",null);
        }
    }
}
