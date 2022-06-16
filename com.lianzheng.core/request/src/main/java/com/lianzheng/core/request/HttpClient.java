package com.lianzheng.core.request;
/**
 * @Description: 请求的包装类，主要是为了统一保存日志
 * @author: 何江雁
 * @date: 2021年10月25日 15:10
 */
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.Asserts;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

@Log4j2
public class HttpClient {
    public static final JSONObject get(String url, Map<String, String> body) throws IOException {
        return request(new RequestInput("GET", url, body));
    }
    public static final JSONObject post(String url, Map<String, String> body) throws IOException {
        return request(new RequestInput("POST", url, body));
    }
    public static final <T> T get(String url, Map<String, String> body, Class<T> clazz) throws IOException {
        JSONObject result = request(new RequestInput("GET", url, body));
        return result.toJavaObject(clazz);
    }
    public static final <T> T post(String url, Map<String, String> body, Class<T> clazz) throws IOException {
        JSONObject result = request(new RequestInput("POST", url, body));
        return result.toJavaObject(clazz);
    }
    public static final JSONObject request(RequestInput input) throws IOException {
        Asserts.notNull(input, "input");
        Asserts.notBlank(input.getUrl(), "url");
        log.info("sending request to "+input.getUrl());

        HttpClientBuilder builder = HttpClientBuilder.create();
        HttpRequestBase request = createRequest(input);

        try (CloseableHttpClient client = builder.build()) {
            try (CloseableHttpResponse response = client.execute(request)) {
                int status = response.getStatusLine().getStatusCode();
                log.info(String.format("received response of %s(%s)",input.getUrl(), status) );
                if (status != HttpStatus.SC_OK) {
                    throw new RuntimeException();
                }
                HttpEntity entity = response.getEntity();
                String body = EntityUtils.toString(entity, "utf-8");
                log.info(String.format("%s(body : %s)",input.getUrl(), body) );
                JSONObject responseBody = JSON.parseObject(body);
                return responseBody;
            }
        }

//        return null;
    }

    private static final HttpRequestBase createRequest(RequestInput input){
        HttpRequestBase request = null;
        switch (input.getMethod()){
            case "POST":
                request = new HttpPost(input.getUrl());
                break;
            case "GET":
                request = new HttpGet(input.getUrl());
                break;
            default:
                //throw new RuntimeException("Not support the method of http");
                break;
        }
        if(request == null){
            throw new RuntimeException("Not support the method of http");
        }

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(input.getTimeout()).build();
        request.setConfig(requestConfig);

        Map<String, String> headers = input.getHeaders();
        for (String key: headers.keySet()) {
            request.setHeader(key, headers.get(key));
        }
//        request.setHeader("User-Agent",
//                "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Mobile Safari/537.36");
//        request.setHeader("Connection", "keep-alive");
        return request;
    }
}
