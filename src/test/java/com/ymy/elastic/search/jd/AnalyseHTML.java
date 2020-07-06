package com.ymy.elastic.search.jd;

import com.ymy.elastic.search.jd.utils.HTMLParseUtil;
import net.minidev.json.JSONUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AnalyseHTML {

    @Autowired
    private HTMLParseUtil htmlParseUtil;

    @Test
    public void analyse() throws Exception{
        htmlParseUtil.parseJD("编程珠玑").forEach(System.out::println);
    }

}
