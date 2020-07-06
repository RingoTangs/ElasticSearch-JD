package com.ymy.elastic.search.jd;

import com.sun.org.apache.bcel.internal.generic.ATHROW;
import com.ymy.elastic.search.jd.entity.Content;
import com.ymy.elastic.search.jd.entity.support.SimpleResponse;
import com.ymy.elastic.search.jd.service.ContentService;
import com.ymy.elastic.search.jd.utils.Constant;
import com.ymy.elastic.search.jd.utils.ElasticSearchUtil;
import com.ymy.elastic.search.jd.utils.HTMLParseUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestUtil {

    @Resource
    private ElasticSearchUtil elasticSearchUtil;

    @Resource
    private HTMLParseUtil htmlParseUtil;

    @Resource
    private ContentService contentService;

    @Test
    public void createIndexAndBulkInsert() throws Exception{
        // 1、创建索引
        elasticSearchUtil.createIndex(Constant.JD_GOODS_INDEX);

        // 2、爬取数据
        List<Content> contents = htmlParseUtil.parseJD("java");

        // 3、将数据添加到ES中
        boolean ret = elasticSearchUtil.bulkAddDocs(Constant.JD_GOODS_INDEX, contents);

        System.out.println(ret);
    }

    @Test
    public void testService() throws Exception{
        Boolean ret = contentService.parseContent(Constant.JD_GOODS_INDEX, "java");
        System.out.println(ret);
    }

    @Test
    public void testSearch() throws Exception {
        SimpleResponse jk = contentService.searchByKeyword(Constant.JD_GOODS_INDEX, "acvasd", 0, 20);
        System.out.println(jk);
    }

}
