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
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.beans.Encoder;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SinaCrawler extends Thread{
    private JdbcCrawlerDao dao;

    public SinaCrawler(JdbcCrawlerDao dao) {
        this.dao = dao;
    }


    @Override
    public void run()  {
        List<Link> nextLinkList;

        while ((nextLinkList = dao.getToBeProcessedLinkAndUpdateStatus()).size() > 0) {
            Link nextLink = nextLinkList.get(0);
            String url = nextLink.getUrl();
            Integer id = nextLink.getId();

            try {
                linkHandleProcess(url, id);
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                continue;
            }
        }
    }

    public void linkHandleProcess(String  url, Integer id) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException {
        PageData pageData = null;
        pageData = getPageDataByUrl(url);
        System.out.println("id = " + id);
        System.out.println("url = " + url);
        System.out.println("title = " + pageData.getTitle());

        List<String> aLinks = pageData.getHrefList();
        saveLinks(aLinks);
        saveNews(url, pageData.getTitle(), pageData.getContent());
    }

    public Document getDocumentByUrl(String url) throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(
                SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
                NoopHostnameVerifier.INSTANCE);
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(3000)
                .setConnectTimeout(3000)
                .setConnectionRequestTimeout(3000)
                .setCookieSpec(CookieSpecs.STANDARD_STRICT)
                .build();

        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(scsf)
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();

        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("user-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");
        CloseableHttpResponse httpGetResponse = httpclient.execute(httpGet);
        HttpEntity httpGetResponseEntity = httpGetResponse.getEntity();
        InputStream inputStream = httpGetResponseEntity.getContent();
        String html = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

        return Jsoup.parse(html);
    }


    public PageData getPageDataByUrl(String url) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        List<String> pageALink = new ArrayList<>();
        Document document = getDocumentByUrl(url);
        List<Element> aTagList = document.select("a");
        String title = document.select("section").select("article").select("h1").text();
        String content = document.select("section").select("p").text();

        for (Element aTag : aTagList) {
            String href = aTag.attr("href");
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

    public void  saveLinks(List<String> linkList) {
        for (String url : linkList) {
            Boolean isExist = dao.selectLinkByUrl(url).size() > 0;
            if (isExist) {
                continue;
            }
            HashMap<String, Object> insertLinkMap = new HashMap();
            insertLinkMap.put("url", url);
            insertLinkMap.put("status", 0);
            dao.insertIntoLINK(insertLinkMap);
        }
    }

    public void saveNews(String url, String title, String content) {
        if (!title.isEmpty() && !content.isEmpty() && content.length() < 99999) {
            News news = new News(url, title, content, Instant.now(), Instant.now());
            dao.insertArticleToNews(news);
        }
    }
}



