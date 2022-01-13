package com.github.hextl.dao;

import com.github.hextl.entity.Link;
import com.github.hextl.entity.News;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

public class JdbcCrawlerDao {
    private SqlSessionFactory sqlSessionFactory;

    public JdbcCrawlerDao() {
        String resource = "db/mybatis/mybatis-config.xml";
        InputStream inputStream;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }

    public List<Link> getToBeProcessedLink() {
        List<Link> links;
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            links = sqlSession.selectList("getToBeProcessedLink");
        }

        return links;
    }

    ;

    public List<Link> selectLinkByUrl(String url) {
        List<Link> links;
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            links = sqlSession.selectList("selectLinkByUrl", url);
        }

        return links;
    }

    ;

    public void updateLinkStatus(HashMap link) {
        try(SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            sqlSession.update("updateLinkStatus", link);
        }
    }

    public void insertIntoLINK(HashMap link) {
        try(SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            sqlSession.insert("insertIntoLINK", link);
        }

    }

    public void insertArticleToNews(News news) {
        try(SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            sqlSession.insert("insertArticleToNews", news);
        }
    }

}
