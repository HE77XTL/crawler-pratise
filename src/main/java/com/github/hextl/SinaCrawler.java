package com.github.hextl;

import com.github.hextl.dao.JdbcCrawlerDao;
import com.github.hextl.entity.Link;
import com.github.hextl.entity.News;
import com.github.hextl.entity.PageData;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SinaCrawler {
    private JdbcCrawlerDao dao;

    public SinaCrawler(JdbcCrawlerDao dao) {
        this.dao = dao;
    }


    public void run() throws IOException {
        PageData pageData = getPageDataByUrl("https://sina.cn");
        dao.updateLinkStatus(new Link(1, 1));
        List<String> aLinks = pageData.getHrefList();
        saveLinks(aLinks);
        saveNews("https://sina.cn", pageData.getTitle(), pageData.getContent());

        List<Link> nextLinkList;

        while ((nextLinkList = dao.getToBeProcessedLink()).size() > 0) {
            Link nextLink = nextLinkList.get(0);
            String url = nextLink.getUrl();
            Integer id = nextLink.getId();

            System.out.println("url" + url);
            System.out.println("title" + pageData.getTitle());


            PageData nextPageData = getPageDataByUrl(url);
            dao.updateLinkStatus(new Link(id, 1));
            List<String> nextALinks = nextPageData.getHrefList();
            saveLinks(nextALinks);
            saveNews(url, pageData.getTitle(), pageData.getContent());
        }
    }

    public Document getDocumentByUrl(String url) throws IOException {
        System.out.println(url);
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
        CloseableHttpResponse httpGetResponse = httpclient.execute(httpGet);
        HttpEntity httpGetResponseEntity = httpGetResponse.getEntity();
        InputStream inputStream = httpGetResponseEntity.getContent();
        String html = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

        System.out.println("html:" + html);

        return Jsoup.parse(html);
    }


    public PageData getPageDataByUrl(String url) throws IOException {

        List<String> pageALink = new ArrayList<>();
        Document document = getDocumentByUrl(url);
        List<Element> aTagList = document.select("a");
        String title = document.select("section").select("article").select("h1").text();
        String content = document.select("section").select("p").text();

        for (Element aTag : aTagList) {

            String href = aTag.attr("href");

            System.out.println("href = " + href);

            if (href.startsWith("//")) {
                href = "http:" + href;
            }

            if (href.contains("reload=sina")) {
                continue;
            }

            if (href.contains("sina.cn") && href.length() < 990) {
                pageALink.add(href);
            }
        }
        return new PageData(pageALink, title, content);
    }

    public void saveLinks(List<String> linkList) {
        for (String url : linkList) {
            Boolean isExist = dao.selectLinkByUrl(url).size() > 0;
            if (isExist) {
                continue;
            }
            dao.insertIntoLINK(new Link(url, 0));
        }
    }

    public void saveNews(String url, String title, String content) {
        if (!title.isEmpty() && !content.isEmpty()) {
            News news = new News(url, title, content, Instant.now(), Instant.now());
            dao.insertArticleToNews(news);
        }
    }
}



