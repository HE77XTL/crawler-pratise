package com.github.hextl;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import com.github.hextl.dao.JdbcCrawlerDao;

public class Main {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        JdbcCrawlerDao dao = new JdbcCrawlerDao();
        SinaCrawler sinaCrawler = new SinaCrawler(dao);
        sinaCrawler.run();
    }
}



