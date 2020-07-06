package com.ymy.elastic.search.jd.controller;

import com.ymy.elastic.search.jd.entity.support.SimpleResponse;
import com.ymy.elastic.search.jd.service.ContentService;
import com.ymy.elastic.search.jd.utils.Constant;
import org.apache.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ContentController {

    @Resource
    private ContentService contentService;

    @GetMapping("/parse/{keyword}")
    public SimpleResponse Insert2ES(@PathVariable("keyword") String keyword) {
        try {
            Boolean ret = contentService.parseContent(Constant.JD_GOODS_INDEX, keyword);
            if (ret){
                return new SimpleResponse(HttpStatus.SC_OK,"插入到ES成功",null);
            }
            return new SimpleResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR,"服务器内部错误",null);
        } catch (Exception e) {
            return new SimpleResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, "服务器内部错误", null);
        }
    }

    @GetMapping("/search/{keyword}")
    public SimpleResponse searchByKeyword(@PathVariable("keyword") String keyword) {
        return contentService.searchByKeyword(Constant.JD_GOODS_INDEX, keyword, 0, 30);
    }
}
