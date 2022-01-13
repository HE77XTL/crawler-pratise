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
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return sqlSession.selectList("getToBeProcessedLink");
    };
    public List<Link> selectLinkByUrl(String url) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return sqlSession.selectList("selectLinkByUrl", url);
    };

    public void updateLinkStatus(Link link) {
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        sqlSession.update("updateLinkStatus", link);
    }

    public void insertIntoLINK(Link link) {
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        sqlSession.insert("insertIntoLINK", link);
    }

    public void insertArticleToNews(News news) {
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        sqlSession.insert("insertArticleToNews", news);
    }





}
