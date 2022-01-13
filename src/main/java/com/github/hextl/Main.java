package com.github.hextl;
import java.io.IOException;
import com.github.hextl.dao.JdbcCrawlerDao;

public class Main {
    public static void main(String[] args) throws IOException {
        JdbcCrawlerDao dao = new JdbcCrawlerDao();
        SinaCrawler sinaCrawler = new SinaCrawler(dao);
        sinaCrawler.run();
    }
}



