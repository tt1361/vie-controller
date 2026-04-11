package com.iflytek.vie.standard.player;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class RestTemplateUtils {
   private static final RestTemplate restTemplate = new RestTemplate();

   public static <T> ResponseEntity<T> get(String url, Class<T> responseType) {
      return restTemplate.getForEntity(url, responseType, new Object[0]);
   }

   public static <T> ResponseEntity<T> get(String url, Class<T> responseType, Object... uriVariables) {
      return restTemplate.getForEntity(url, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> get(String url, Map<String, ?> uriVariables, Class<T> responseType) {
      return restTemplate.getForEntity(url, responseType, uriVariables);
   }

   public static Boolean getBoolean(String url, MultiValueMap<String, String> param) {
      UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
      URI uri = builder.queryParams(param).build().encode().toUri();
      ResponseEntity<Boolean> forEntity = restTemplate.getForEntity(uri, Boolean.class);
      return (Boolean)forEntity.getBody();
   }

   public static <T> ResponseEntity<T> get(String url, Map<String, String> headers, Class<T> responseType, Object... uriVariables) {
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setAll(headers);
      return get(url, httpHeaders, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> get(String url, HttpHeaders headers, Class<T> responseType, Object... uriVariables) {
      HttpEntity<?> requestEntity = new HttpEntity(headers);
      return exchange(url, HttpMethod.GET, requestEntity, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> get(String url, Map<String, String> headers, Class<T> responseType, Map<String, ?> uriVariables) {
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setAll(headers);
      return get(url, httpHeaders, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> get(String url, HttpHeaders headers, Class<T> responseType, Map<String, ?> uriVariables) {
      HttpEntity<?> requestEntity = new HttpEntity(headers);
      return exchange(url, HttpMethod.GET, requestEntity, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> post(String url, Class<T> responseType) {
      return restTemplate.postForEntity(url, HttpEntity.EMPTY, responseType, new Object[0]);
   }

   public static <T> ResponseEntity<T> post(String url, Object requestBody, Class<T> responseType) {
      HttpHeaders httpHeaders = new HttpHeaders();
      Map<String, String> headers = new HashMap();
      headers.put("Access-Control-Allow-Credentials", "true");
      httpHeaders.setAll(headers);
      return restTemplate.postForEntity(url, requestBody, responseType, new Object[0]);
   }

   public static <T> ResponseEntity<T> post(String url, Object requestBody, Class<T> responseType, Object... uriVariables) {
      return restTemplate.postForEntity(url, requestBody, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> post(String url, Object requestBody, Class<T> responseType, Map<String, ?> uriVariables) {
      return restTemplate.postForEntity(url, requestBody, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> post(String url, Map<String, String> headers, Object requestBody, Class<T> responseType, Object... uriVariables) {
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setAll(headers);
      return post(url, httpHeaders, requestBody, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> post(String url, HttpHeaders headers, Object requestBody, Class<T> responseType, Object... uriVariables) {
      HttpEntity<Object> requestEntity = new HttpEntity(requestBody, headers);
      return post(url, requestEntity, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> post(String url, Map<String, String> headers, Object requestBody, Class<T> responseType, Map<String, ?> uriVariables) {
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setAll(headers);
      return post(url, httpHeaders, requestBody, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> post(String url, HttpHeaders headers, Object requestBody, Class<T> responseType, Map<String, ?> uriVariables) {
      HttpEntity<Object> requestEntity = new HttpEntity(requestBody, headers);
      return post(url, requestEntity, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> post(String url, HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) {
      return restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> post(String url, HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) {
      return restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> put(String url, Class<T> responseType, Object... uriVariables) {
      return put(url, HttpEntity.EMPTY, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> put(String url, Object requestBody, Class<T> responseType, Object... uriVariables) {
      HttpEntity<Object> requestEntity = new HttpEntity(requestBody);
      return put(url, requestEntity, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> put(String url, Object requestBody, Class<T> responseType, Map<String, ?> uriVariables) {
      HttpEntity<Object> requestEntity = new HttpEntity(requestBody);
      return put(url, requestEntity, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> put(String url, Map<String, String> headers, Object requestBody, Class<T> responseType, Object... uriVariables) {
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setAll(headers);
      return put(url, httpHeaders, requestBody, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> put(String url, HttpHeaders headers, Object requestBody, Class<T> responseType, Object... uriVariables) {
      HttpEntity<Object> requestEntity = new HttpEntity(requestBody, headers);
      return put(url, requestEntity, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> put(String url, Map<String, String> headers, Object requestBody, Class<T> responseType, Map<String, ?> uriVariables) {
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setAll(headers);
      return put(url, httpHeaders, requestBody, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> put(String url, HttpHeaders headers, Object requestBody, Class<T> responseType, Map<String, ?> uriVariables) {
      HttpEntity<Object> requestEntity = new HttpEntity(requestBody, headers);
      return put(url, requestEntity, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> put(String url, HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) {
      return restTemplate.exchange(url, HttpMethod.PUT, requestEntity, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> put(String url, HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) {
      return restTemplate.exchange(url, HttpMethod.PUT, requestEntity, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> delete(String url, Class<T> responseType, Object... uriVariables) {
      return delete(url, HttpEntity.EMPTY, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> delete(String url, Class<T> responseType, Map<String, ?> uriVariables) {
      return delete(url, HttpEntity.EMPTY, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> delete(String url, Object requestBody, Class<T> responseType, Object... uriVariables) {
      HttpEntity<Object> requestEntity = new HttpEntity(requestBody);
      return delete(url, requestEntity, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> delete(String url, Object requestBody, Class<T> responseType, Map<String, ?> uriVariables) {
      HttpEntity<Object> requestEntity = new HttpEntity(requestBody);
      return delete(url, requestEntity, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> delete(String url, Map<String, String> headers, Class<T> responseType, Object... uriVariables) {
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setAll(headers);
      return delete(url, httpHeaders, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> delete(String url, HttpHeaders headers, Class<T> responseType, Object... uriVariables) {
      HttpEntity<Object> requestEntity = new HttpEntity(headers);
      return delete(url, requestEntity, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> delete(String url, Map<String, String> headers, Class<T> responseType, Map<String, ?> uriVariables) {
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setAll(headers);
      return delete(url, httpHeaders, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> delete(String url, HttpHeaders headers, Class<T> responseType, Map<String, ?> uriVariables) {
      HttpEntity<Object> requestEntity = new HttpEntity(headers);
      return delete(url, requestEntity, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> delete(String url, Map<String, String> headers, Object requestBody, Class<T> responseType, Object... uriVariables) {
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setAll(headers);
      return delete(url, httpHeaders, requestBody, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> delete(String url, HttpHeaders headers, Object requestBody, Class<T> responseType, Object... uriVariables) {
      HttpEntity<Object> requestEntity = new HttpEntity(requestBody, headers);
      return delete(url, requestEntity, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> delete(String url, Map<String, String> headers, Object requestBody, Class<T> responseType, Map<String, ?> uriVariables) {
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setAll(headers);
      return delete(url, httpHeaders, requestBody, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> delete(String url, HttpHeaders headers, Object requestBody, Class<T> responseType, Map<String, ?> uriVariables) {
      HttpEntity<Object> requestEntity = new HttpEntity(requestBody, headers);
      return delete(url, requestEntity, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> delete(String url, HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) {
      return restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> delete(String url, HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) {
      return restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) {
      return restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
   }

   public static <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) {
      return restTemplate.exchange(url, method, requestEntity, responseType, uriVariables);
   }

   public static RestTemplate getRestTemplate() {
      return restTemplate;
   }
}
