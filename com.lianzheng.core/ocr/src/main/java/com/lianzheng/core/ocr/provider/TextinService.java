package com.lianzheng.core.ocr.provider;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lianzheng.core.ocr.config.OcrConfig;
import com.lianzheng.core.ocr.config.OcrSettings;
import com.lianzheng.core.ocr.entity.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alibaba.fastjson.JSON;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @Description: 参考https://www.textin.com/document/index
 * @author: 何江雁
 * @date: 2022年02月16日 17:06
 */
@Log
@Service("TextinService")
public class TextinService extends OcrBaseService {

    private IdCardResult backResult;
    private IdCardResult frontResult;

    @Autowired
    private OcrConfig ocrConfig;

    @Override
    public OcrResult extract(OcrInput ocrInput) throws IOException {
        IdCardInput input = (IdCardInput) ocrInput;
        byte[] backImage = input.getBackImage(); // 身份证人像面
        byte[] frontImage = input.getFrontImage(); // 身份证国徽面

        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(2);
        threadPool.execute(new Thread(() -> {
            try {
                log.info("身份证人像面开始");
                backResult = innerExtractIdCard(backImage);
                latch.countDown();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        threadPool.execute(new Thread(() -> {
            try {
                log.info("身份证国徽面开始");
                frontResult = innerExtractIdCard(frontImage);
                latch.countDown();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        //等待进行结束
        try {
            latch.await();
            log.info("识别完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
        threadPool.shutdown();

        return new IdCardResult(backResult.getName(), backResult.getIdNumber(), frontResult.getValidateDate(), backResult.getBirth(),
                backResult.getAddress(), frontResult.getIssueAuthority(), backResult.getSex(), backResult.getNationality());
    }

    public IdCardResult innerExtractIdCard(byte[] imgData) throws IOException {
        OcrSettings settings = ocrConfig.getOcrSettings();
        // 身份证识别
        String url = "https://api.textin.com/robot/v1.0/api/id_card?head_portrait=1";
        // 请登录后前往 “工作台-账号设置-开发者信息” 查看 x-ti-app-id
        // 示例代码中 x-ti-app-id 非真实数据
        String appId = settings.getAppId();
        // 请登录后前往 “工作台-账号设置-开发者信息” 查看 x-ti-secret-code
        // 示例代码中 x-ti-secret-code 非真实数据
        String secretCode = settings.getSecret();
        BufferedReader in = null;
        DataOutputStream out = null;
        String resultString = "";
        //todo, log the input to thirdApi table
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setRequestProperty("x-ti-app-id", appId);
            conn.setRequestProperty("x-ti-secret-code", secretCode);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST"); // 设置请求方式
            out = new DataOutputStream(conn.getOutputStream());
            out.write(imgData);
            out.flush();
            out.close();
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                resultString += line;
            }

        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        //todo, log the resultString to thirdApi table
        Assert.isTrue(!resultString.isEmpty(), "");
        JSONObject result = JSON.parseObject(resultString);
        Integer code = result.getInteger("code");
        String message = result.getString("message");
        Assert.isTrue(code == 200, message);

        JSONObject resultData = result.getJSONObject("result");
        Assert.notNull(resultData, "Unexpected the result value");
        JSONArray itemList = resultData.getJSONArray("item_list");
        return new IdCardResult(parseJsonItem(itemList, "name"),
                parseJsonItem(itemList, "id_number"),
                parseJsonItem(itemList, "validate_date"),
                parseJsonItem(itemList, "birth"),
                parseJsonItem(itemList, "address"),
                parseJsonItem(itemList, "issue_authority"),
                parseJsonItem(itemList, "sex"),
                parseJsonItem(itemList, "nationality"));
    }

    private static final String parseJsonItem(JSONArray itemList, String key) {
        for (int i = 0; i < itemList.size(); i++) {
            JSONObject item = itemList.getJSONObject(i);
            String itemKey = item.getString("key");
            if (itemKey.equals(key)) {
                String value = item.getString("value");
                itemList.remove(item);
                return value;
            }
        }
        return null;
    }


    @Override
    public OcrResult extractBusinessLicense(OcrInput ocrInput) throws IOException {
        OcrSettings settings = ocrConfig.getOcrSettings();
        String appId = settings.getAppId();
        String secretCode = settings.getSecret();

        BusinessLicenseInput input = (BusinessLicenseInput) ocrInput;
        byte[] imgData = input.getImageData();
        String url = "https://api.textin.com/robot/v1.0/api/business_license";
        BufferedReader in = null;
        DataOutputStream out = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setRequestProperty("x-ti-app-id", appId);
            conn.setRequestProperty("x-ti-secret-code", secretCode);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            out = new DataOutputStream(conn.getOutputStream());
            out.write(imgData);
            out.flush();
            out.close();
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println(result);

        JSONObject resultData = JSONObject.parseObject(result).getJSONObject("result");
        Assert.notNull(resultData, "Unexpected the result value");
        JSONArray itemList = resultData.getJSONArray("item_list");

        BusinessLicenseResult businessResult = new BusinessLicenseResult();

        businessResult.setIsCopy(parseJsonItem(itemList, "BizLicenseIsCopy"));
        businessResult.setIsElectronic(parseJsonItem(itemList, "BizLicenseIsElectronic"));
        businessResult.setSerialNumber(parseJsonItem(itemList, "BizLicenseSerialNumber"));
        businessResult.setRegistrationCode(parseJsonItem(itemList, "BizLicenseRegistrationCode"));
        businessResult.setRegCapital(parseJsonItem(itemList, "BizLicenseRegCapital"));
        businessResult.setCreditCode(parseJsonItem(itemList, "BizLicenseCreditCode"));
        businessResult.setCompanyName(parseJsonItem(itemList, "BizLicenseCompanyName"));
        businessResult.setOwnerName(parseJsonItem(itemList, "BizLicenseOwnerName"));
        businessResult.setCompanyType(parseJsonItem(itemList, "BizLicenseCompanyType"));
        businessResult.setAddress(parseJsonItem(itemList, "BizLicenseAddress"));
        businessResult.setScope(parseJsonItem(itemList, "BizLicenseScope"));
        businessResult.setStartTime(parseJsonItem(itemList, "BizLicenseStartTime"));
        businessResult.setOperatingPeriod(parseJsonItem(itemList, "BizLicenseOperatingPeriod"));
        businessResult.setComposingForm(parseJsonItem(itemList, "BizLicenseComposingForm"));
        businessResult.setPaidInCapital(parseJsonItem(itemList, "BizLicensePaidInCapital"));
        businessResult.setRegistrationDate(parseJsonItem(itemList, "BizLicenseRegistrationDate"));

        return businessResult;
    }

    @Override
    public OcrResult extractCommon(OcrInput ocrInput) throws IOException {
        OcrSettings settings = ocrConfig.getOcrSettings();
        String appId = settings.getAppId();
        String secretCode = settings.getSecret();

        CommonInput input = (CommonInput) ocrInput;
        byte[] imgData = input.getImageData();
        String url = "https://api.textin.com/robot/v1.0/api/text_recognize_3d1";
        BufferedReader in = null;
        DataOutputStream out = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setRequestProperty("x-ti-app-id", appId);
            conn.setRequestProperty("x-ti-secret-code", secretCode);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            out = new DataOutputStream(conn.getOutputStream());
            out.write(imgData);
            out.flush();
            out.close();
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        Assert.isTrue(!result.isEmpty(), "");
        JSONObject resultJSON = JSON.parseObject(result);
        Integer code = resultJSON.getInteger("code");
        String message = resultJSON.getString("message");
        Assert.isTrue(code == 200, message);
        JSONObject resultData = resultJSON.getJSONObject("result");
        Assert.notNull(resultData, "Unexpected the result value");
        System.out.println(result);
        Assert.notNull(result, "Unexpected the result value");
        CommonResult commonResult = new CommonResult();
        commonResult.setResultJSON(resultData);
        return commonResult;
    }


//    public static void main(String[] args) {
//        // 身份证识别
//        String url = "https://api.textin.com/robot/v1.0/api/id_card?head_portrait=1";
//        // 请登录后前往 “工作台-账号设置-开发者信息” 查看 x-ti-app-id
//        // 示例代码中 x-ti-app-id 非真实数据
//        String appId = settings.getAppId();
//        // 请登录后前往 “工作台-账号设置-开发者信息” 查看 x-ti-secret-code
//        // 示例代码中 x-ti-secret-code 非真实数据
//        String secretCode = settings.getSecret();
//        BufferedReader in = null;
//        DataOutputStream out = null;
//        String resultString = "";
//        //todo, log the input to thirdApi table
//        try {
//            URL realUrl = new URL(url);
//            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
//            conn.setRequestProperty("connection", "Keep-Alive");
//            conn.setRequestProperty("Content-Type", "application/octet-stream");
//            conn.setRequestProperty("x-ti-app-id", appId);
//            conn.setRequestProperty("x-ti-secret-code", secretCode);
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//            conn.setRequestMethod("POST"); // 设置请求方式
//            out = new DataOutputStream(conn.getOutputStream());
//            out.write(imgData);
//            out.flush();
//            out.close();
//            in = new BufferedReader(
//                    new InputStreamReader(conn.getInputStream()));
//            String line;
//            while ((line = in.readLine()) != null) {
//                resultString += line;
//            }
//
//        } finally {
//            try {
//                if (out != null) {
//                    out.close();
//                }
//                if (in != null) {
//                    in.close();
//                }
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//
//        //todo, log the resultString to thirdApi table
//        Assert.isTrue(!resultString.isEmpty(), "");
//        JSONObject result = JSON.parseObject(resultString);
//        Integer code = result.getInteger("code");
//        String message = result.getString("message");
//        Assert.isTrue(code == 200, message);
//
//        JSONObject resultData = result.getJSONObject("result");
//        Assert.notNull(resultData, "Unexpected the result value");
//        JSONArray itemList = resultData.getJSONArray("item_list");
//        return new IdCardResult(parseJsonItem(itemList, "name"),
//                parseJsonItem(itemList, "id_number"),
//                parseJsonItem(itemList, "validate_date"),
//                parseJsonItem(itemList, "birth"),
//                parseJsonItem(itemList, "address"),
//                parseJsonItem(itemList, "issue_authority"),
//                parseJsonItem(itemList, "sex"),
//                parseJsonItem(itemList, "nationality"));
//    }

}
