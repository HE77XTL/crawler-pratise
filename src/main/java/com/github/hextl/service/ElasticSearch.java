package com.github.hextl.service;


import com.github.hextl.entity.News;
import org.apache.http.HttpHost;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElasticSearch {
    private SqlSessionFactory sqlSessionFactory;

    public ElasticSearch() {
        String resource = "db/mybatis/mybatis-config.xml";
        InputStream inputStream;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }

    public static void main(String[] args) throws IOException {
        ElasticSearch elasticSearch = new ElasticSearch();
        try (RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("101.43.38.193", 9200, "http")))) {
            for (int i = 0; i < 10; i++) {
                BulkRequest bulkRequest = new BulkRequest();
                //索引请求的对象
                IndexRequest request = new IndexRequest("news");
                //指定变量到属性的映射

                List<News> list = elasticSearch.getNewsFromDB();
                for (News news : list
                ) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("url", news.getUrl());
                    data.put("title", news.getTitle());
                    data.put("content", news.getContent());
                    data.put("create_time", news.getCreate_time());
                    data.put("modify_time", news.getModify_time());
                    request.source(data, XContentType.JSON);
                    //发起请求
                    bulkRequest.add(request);
                }
                client.bulk(bulkRequest, RequestOptions.DEFAULT);
            }
        }
    }



    public List<News> getNewsFromDB() {
        List<News> list;
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            list = sqlSession.selectList("selectNewFromNews");
        }
        return list;
    }
}
