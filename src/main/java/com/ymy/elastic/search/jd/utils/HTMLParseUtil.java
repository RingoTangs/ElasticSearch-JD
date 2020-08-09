package com.ymy.elastic.search.jd.utils;

import com.ymy.elastic.search.jd.entity.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component("htmlParseUtil")
public class HTMLParseUtil {

    public List<Content> parseJD(String keyword) throws Exception {
        // 1、目标url
        String url = "https://search.jd.com/Search?keyword=" + keyword + "&enc=utf-8";

        // 2、Jsoup解析网页,Document对象就是HTML页面的DOM对象！
        Document document = Jsoup.parse(new URL(url), 30000);
        Element j_goodsList = document.getElementById("J_goodsList");
        Elements lis = j_goodsList.getElementsByTag("li");

        // 3、从HTML页面到数据封装成对象保存
        List<Content> contents = new ArrayList<>();
        for (Element li : lis) {
            String img = li.getElementsByTag("img").eq(0).attr("src");
            String price = li.getElementsByClass("p-price").eq(0).text();
            String title = li.getElementsByClass("p-name").eq(0).text();
            contents.add(new Content(title, img, price));
        }
        return contents;
    }
}
