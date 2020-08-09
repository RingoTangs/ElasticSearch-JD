jsoup依赖

```xml
<dependencies>
    <!--jsoup爬取数据-->
    <dependency>
        <groupId>org.jsoup</groupId>
        <artifactId>jsoup</artifactId>
        <version>1.10.2</version>
    </dependency>
</dependencies>
```

解析网页

```java
com.ymy.elastic.search.jd.utils.HTMLParseUtil
```

ES封装的方法

```java
com.ymy.elastic.search.jd.utils.ElasticSearchUtil
```

项目中的接口

```java
1、"/parse/{keyword}"：在浏览器地址栏中输入"keyword"可以抓取数据到ES中！
2、"/search/{keyword}"：用于检索，在搜索框中输入关键字，点击搜索即可！
```



