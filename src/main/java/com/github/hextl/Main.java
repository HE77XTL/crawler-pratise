package com.github.hextl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.hextl.entity.Link;
import com.github.hextl.entity.News;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
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
import org.jsoup.select.Elements;


import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) throws IOException {
        List<String> aLinks = getLinks("https://sina.cn");
        saveLinks(aLinks);

        String resource = "db/mybatis/mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession(true);

        List<Link> nextLinkList;
        while ((nextLinkList = sqlSession.selectList("getToBeProcessedLink")).size() > 0) {
            Link nextLink = nextLinkList.get(0);
            String url = nextLink.getUrl();
            Integer id = nextLink.getId();

            saveLinks(getLinks(url));

            HashMap updateLinkMap = new HashMap<String, Object>();
            updateLinkMap.put("id", id);
            updateLinkMap.put("status", 1);
            sqlSession.update("updateLinkStatus", updateLinkMap);
        }
    }

    public static List<String> getLinks(String url) throws UnsupportedEncodingException {

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(10000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setCookieSpec(CookieSpecs.STANDARD_STRICT)
                .build();

        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();


        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("user-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");
        List<String> result = new ArrayList<>();
        try (CloseableHttpResponse httpGetResponse = httpclient.execute(httpGet)) {
            HttpEntity httpGetResponseEntity = httpGetResponse.getEntity();
            InputStream inputStream = httpGetResponseEntity.getContent();
            String html = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            Document document = Jsoup.parse(html);
            List<Element> aTagList = document.select("a");

            String title = document.select("section").select("article").select("h1").text();
            Elements contentP = document.select("section").select("p");
            String content = document.select("section").select("p").text();


            String resource = "db/mybatis/mybatis-config.xml";
            InputStream sqlInputStream = Resources.getResourceAsStream(resource);
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(sqlInputStream);
            SqlSession sqlSession = sqlSessionFactory.openSession(true);


            if(!title.isEmpty() && !content.isEmpty()) {
                News news = new News(url,title,content, Instant.now(),Instant.now());
                sqlSession.insert("insertArticleToNews", news);
            }

            System.out.println("title:" + title);
            System.out.println("content:" + content);

            for (Element aTag : aTagList) {
                String href = aTag.attr("href");
                if (href.contains("sina.cn") && href.length() < 990) {
                    if(href.startsWith("//")) {
                        href = "http:" + href;
                    }
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
            Boolean isExist = sqlSession.selectList("selectLink", url).size() > 0;
            if (isExist) {
                continue;
            }
            HashMap linkMap = new HashMap<String, Object>();
            linkMap.put("url", url);
            linkMap.put("status", 0);
            System.out.println(url);
            sqlSession.insert("insertIntoLINK", linkMap);
        }
    }

}



