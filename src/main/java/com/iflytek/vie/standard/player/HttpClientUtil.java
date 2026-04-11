package com.iflytek.vie.standard.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtil {
   private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

   public static String sendGet(String url, Map<String, String> parameters) {
      String result = "";
      BufferedReader in = null;
      StringBuffer sb = new StringBuffer();
      String params = "";

      try {
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

         URL connURL = new URL(full_url);
         HttpURLConnection httpConn = (HttpURLConnection)connURL.openConnection();
         httpConn.setRequestProperty("Accept", "*/*");
         httpConn.setRequestProperty("Connection", "Keep-Alive");
         httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
         httpConn.connect();
         Map<String, List<String>> headers = httpConn.getHeaderFields();
         Iterator var10 = headers.keySet().iterator();

         while(var10.hasNext()) {
            String key = (String)var10.next();
            System.out.println(key + "\t：\t" + headers.get(key));
         }

         String line;
         for(in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8")); (line = in.readLine()) != null; result = result + line) {
         }
      } catch (Exception var20) {
         var20.printStackTrace();
      } finally {
         try {
            if (in != null) {
               in.close();
            }
         } catch (IOException var19) {
            var19.printStackTrace();
         }

      }

      logger.info("httpClient的get方式获取的返回结果为：{}", result);
      return result;
   }

   public static String sendPost(String url, Map<String, String> parameters) {
      String result = "";
      BufferedReader in = null;
      PrintWriter out = null;
      StringBuffer sb = new StringBuffer();
      String params = "";

      try {
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
         }

         URL connURL = new URL(url);
         HttpURLConnection httpConn = (HttpURLConnection)connURL.openConnection();
         httpConn.setRequestProperty("Accept", "*/*");
         httpConn.setRequestProperty("Connection", "Keep-Alive");
         httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
         httpConn.setDoInput(true);
         httpConn.setDoOutput(true);
         out = new PrintWriter(httpConn.getOutputStream());
         out.write(params);
         out.flush();

         String line;
         for(in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8")); (line = in.readLine()) != null; result = result + line) {
         }
      } catch (Exception var18) {
         var18.printStackTrace();
      } finally {
         try {
            if (out != null) {
               out.close();
            }

            if (in != null) {
               in.close();
            }
         } catch (IOException var17) {
            var17.printStackTrace();
         }

      }

      return result;
   }
}
