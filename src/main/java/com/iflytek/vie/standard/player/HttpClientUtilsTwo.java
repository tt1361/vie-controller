package com.iflytek.vie.standard.player;

import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtilsTwo {
   private static final Logger logger = LoggerFactory.getLogger(HttpClientUtilsTwo.class);

   public static String sendGet(String url, Map<String, String> parameters) {
      CloseableHttpClient httpclient = HttpClients.createDefault();
      String params = "";

      try {
         StringBuffer sb = new StringBuffer();
         String full_url = url;
         if (parameters != null) {
            Iterator var7;
            String name;
            if (parameters.size() == 1) {
               var7 = parameters.keySet().iterator();

               while(var7.hasNext()) {
                  name = (String)var7.next();
                  sb.append(name).append("=").append(URLEncoder.encode((String)parameters.get(name), "UTF-8"));
               }

               params = sb.toString();
            } else {
               var7 = parameters.keySet().iterator();

               while(var7.hasNext()) {
                  name = (String)var7.next();
                  sb.append(name).append("=").append(URLEncoder.encode((String)parameters.get(name), "UTF-8")).append("&");
               }

               String temp_params = sb.toString();
               params = temp_params.substring(0, temp_params.length() - 1);
            }

            full_url = url + "?" + params;
         }

         logger.info("HttpGet方式请求接口为：{}", full_url);
         HttpGet httpGet = new HttpGet(full_url);
         CloseableHttpResponse httpResponse = httpclient.execute(httpGet);
         return EntityUtils.toString(httpResponse.getEntity());
      } catch (Exception var9) {
         logger.error("HttpGet方式请求报错:", var9);
         return "false";
      }
   }
}
