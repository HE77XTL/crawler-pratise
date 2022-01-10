package com.github.hextl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class Main {
  public static void main(String[] args) throws IOException {
      String resource = "mybatis-config.xml";
      InputStream inputStream = Resources.getResourceAsStream(resource);
      SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
      SqlSession session = sqlSessionFactory.openSession();
      System.out.println(session.selectList("selectLink"));





//    CloseableHttpClient httpclient = HttpClients.createDefault();
//    HttpGet httpGet = new HttpGet("http://sina.cn");
//    try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
//      System.out.println(response1.getStatusLine());
//      HttpEntity entity1 = response1.getEntity();
////      System.out.println(EntityUtils.toString(entity1));
//    }

  }
}



