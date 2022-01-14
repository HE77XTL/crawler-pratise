package com.github.hextl;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import com.github.hextl.dao.JdbcCrawlerDao;

public class Main {
    public static void main(String[] args)  {
        JdbcCrawlerDao dao = new JdbcCrawlerDao();
        Object lock = new Object();
        for (int i = 0; i < 4; i++) {
            SinaCrawler sinaCrawler = new SinaCrawler(dao);
            synchronized (lock) {
                sinaCrawler.start();
            }
        }
    }
}



