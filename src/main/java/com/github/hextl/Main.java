package com.github.hextl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) throws IOException {
        List<String> aLinks = getLinks("https://sina.cn");
        saveLinks(aLinks);
    }

    public static List<String> getLinks(String url) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("user-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");
        List<String> result = new ArrayList<>();
        try (CloseableHttpResponse httpGetResponse = httpclient.execute(httpGet)) {
            HttpEntity httpGetResponseEntity = httpGetResponse.getEntity();
            InputStream inputStream = httpGetResponseEntity.getContent();
            String html = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            Document document = Jsoup.parse(html);
            List<Element> aTagList = document.select("a");

            for (Element aTag : aTagList) {
                String href = aTag.attr("href");
                if (href.contains("sina.cn")) {
                    result.add(href);
                }
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void saveLinks(List<String> linkList) throws IOException {
        String resource = "db/mybatis/mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession(true);

        for (String url : linkList) {
            sqlSession.insert("insertIntoLINK", url);
        }
    }

}



