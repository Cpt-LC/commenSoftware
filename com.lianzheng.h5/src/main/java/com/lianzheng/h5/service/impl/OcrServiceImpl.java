package com.lianzheng.h5.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lianzheng.core.server.exception.RRException;
import com.lianzheng.h5.dto.CheckFaceDTO;
import com.lianzheng.h5.service.IOcrService;
import com.lianzheng.h5.util.FileUtil;
import com.lianzheng.h5.util.ImageBase64Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author kk
 * @date 2022/6/13 22:27
 * @describe
 * @remark
 */
@Service
public class OcrServiceImpl implements IOcrService {


    @Value("${faceCompare.geo.customerCode}")
    private String geoCustomerCode;
    @Value("${faceCompare.geo.uno}")
    private String geoUno;
    @Value("${faceCompare.geo.username}")
    private String geoUsername;
    @Value("${faceCompare.geo.password}")
    private String geoPassword;
    @Value("${faceCompare.geo.loginUrl}")
    private String geoLoginUrl;
    @Value("${faceCompare.geo.compareUrl}")
    private String geoCompareUrl;
    @Value("${faceCompare.useProxy}")
    private String useProxy;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Map<String, Object> checkFace(CheckFaceDTO checkFaceDTO) throws Exception {
        Map<String,Object> result = new HashMap<>();
        result.put("data",geoFaceCompare(checkFaceDTO.getName(),checkFaceDTO.getCardNo(),FileUtil.multipartFileToFile(checkFaceDTO.getFaceImgFile()),1));
        return result;
    }


    private String getAuthCode() {
        String authCode = "";
        long time = System.currentTimeMillis();
        String timestamp = String.valueOf(time / 1000);
        String serialStr = UUID.randomUUID().toString().replace("-", "");
        serialStr = StringUtils.leftPad(serialStr, 32, "0");
        authCode = timestamp + geoCustomerCode + serialStr;
        return authCode;
    }

    // 代理获取集奥的登陆信息
    private Map<String, Object> getToken() {
        Map<String, Object> resultMap = new HashMap<>();
        // 先从缓存取，没有的话调用接口
        String cacheData = stringRedisTemplate.opsForValue().get("GEO_TOKEN_CACHE");
        if (StringUtils.isNotBlank(cacheData)) {
            JSONObject dataObj = JSON.parseObject(cacheData);
            resultMap.put("tokenId", dataObj.getString("tokenId"));
            resultMap.put("digitalSignatureKey", dataObj.getString("digitalSignatureKey"));
        } else {
            JSONObject params = new JSONObject();
            params.put("username", geoUsername);
            params.put("password", geoPassword);
            params.put("dsign", 0);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("uno", geoUno);
            headers.add("encrypted", "0");
            HttpEntity<String> httpEntity = new HttpEntity<>(params.toString(), headers);
            RestTemplate restTemplate = "yes".equals(useProxy) ? new RestTemplate(getFactory()) : new RestTemplate();
            String result = restTemplate.postForObject(geoLoginUrl, httpEntity, String.class);
            if (StringUtils.isNotBlank(result)) {
                JSONObject resultJson = (JSONObject) JSONObject.parse(result);
                String resultCode = resultJson.getString("code");
                if ("200".equals(resultCode)) {
                    JSONObject dataObj = (JSONObject) resultJson.get("data");
                    resultMap.put("tokenId", dataObj.getString("tokenId"));
                    resultMap.put("digitalSignatureKey", dataObj.getString("digitalSignatureKey"));
                    stringRedisTemplate.opsForValue().set("GEO_TOKEN_CACHE", dataObj.toJSONString(), 12 * 3600L);
                } else {
                    throw new RRException("人脸比对授权失败！");
                }
            } else {
                throw new RRException("人脸比对授权失败！");
            }
        }
        return resultMap;
    }

    // 代理
    static ClientHttpRequestFactory getFactory() {

        final String username = "username";
        final String password = "12345678";
        final String proxyUrl = "49.233.56.250";
        final int port = 9083;

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(proxyUrl, port), new UsernamePasswordCredentials(username, password));

        HttpHost myProxy = new HttpHost(proxyUrl, port);

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setProxy(myProxy).setDefaultCredentialsProvider(credsProvider).disableCookieManagement();

        HttpClient httpClient = clientBuilder.build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);

        return factory;
    }

    private JSONObject geoFaceCompare(String idcard_name, String idcard_number, File faceFile, int i) {
        if (faceFile == null) {
            throw new RRException("未找到人脸文件！");
        }
        JSONObject params = new JSONObject();
        String authCode = getAuthCode();
        params.put("authCode", authCode);
        params.put("innerIfType", "L1");
        params.put("realName", idcard_name);
        params.put("idNumber", idcard_number);
        params.put("photo", ImageBase64Util.getBase64Str(faceFile));


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> headerMap = getToken();
        if (headerMap != null) {
            for (String key : headerMap.keySet()) {
                headers.add(key, String.valueOf(headerMap.get(key)));
            }
        }

        HttpEntity<String> httpEntity = new HttpEntity<>(params.toString(), headers);

        RestTemplate restTemplate = "yes".equals(useProxy) ? new RestTemplate(getFactory()) : new RestTemplate();
        String result = restTemplate.postForObject(geoCompareUrl, httpEntity, String.class);
        if (StringUtils.isNotBlank(result)) {
            JSONObject resultJson = (JSONObject) JSONObject.parse(result);
            String resultCode = resultJson.getString("code");
            if ("200".equals(resultCode)) {
                JSONObject dataObj = (JSONObject) resultJson.get("data");
                JSONArray rslArray = dataObj.getJSONArray("RSL");
                if (rslArray != null && rslArray.size() > 0) {
                    JSONObject firstObj = rslArray.getJSONObject(0);
                    JSONObject rsObj = firstObj.getJSONObject("RS");
                    if (rsObj != null) {
                        return rsObj;
                    } else {
                        if (i == 1) {
                            throw new RRException("人脸比对失败！");
                        } else {
                            throw new RRException("证件比对失败！");
                        }
                    }
                } else {
                    if (i == 1) {
                        throw new RRException("人脸比对失败！");
                    } else {
                        throw new RRException("证件比对失败！");
                    }
                }
            } else {
                if ("-100".equals(resultCode) || "-200".equals(resultCode) || "-300".equals(resultCode)) {
                    stringRedisTemplate.delete("GEO_TOKEN_CACHE");
                }
                if (i == 1) {
                    throw new RRException("人脸比对失败！");
                } else {
                    throw new RRException("证件比对失败！");
                }
            }
        } else {
            if (i == 1) {
                throw new RRException("人脸比对失败！");
            } else {
                throw new RRException("证件比对失败！");
            }
        }

    }

}
