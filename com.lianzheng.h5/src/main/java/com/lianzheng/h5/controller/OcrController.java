package com.lianzheng.h5.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.lianzheng.core.ocr.OcrFactory;
import com.lianzheng.core.ocr.config.OcrConfig;
import com.lianzheng.core.ocr.config.OcrSettings;
import com.lianzheng.core.ocr.entity.BusinessLicenseInput;
import com.lianzheng.core.ocr.entity.BusinessLicenseResult;
import com.lianzheng.core.ocr.entity.IdCardInput;
import com.lianzheng.core.ocr.entity.OcrResult;
import com.lianzheng.core.ocr.provider.OcrBaseService;
import com.lianzheng.h5.common.ApiResult;
import com.lianzheng.h5.dto.CheckFaceDTO;
import com.lianzheng.h5.service.IOcrService;
import com.lianzheng.h5.service.impl.OcrServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: OCR接口调用
 * @author: 何江雁
 * @date: 2022年02月15日 21:04
 */
@Log
@RestController
@RequestMapping("/ocr")
@Api(tags = "OCR接口调用")
public class OcrController {

    @Autowired
    private OcrConfig ocrConfig;

    @Autowired
    private IOcrService ocrService;

    private  OcrBaseService getOcrService() throws IOException {
        OcrFactory ocrFactory =new OcrFactory();
        return ocrFactory.getService(ocrConfig);
    }


    @ResponseBody
    @RequestMapping(value = "/extract", method = RequestMethod.POST)
    @ApiOperation(value = "ocr识别")
    public ApiResult<?> extract(@RequestParam("backImage") MultipartFile backImageFile
            ,@RequestParam("frontImage") MultipartFile frontImageFile) throws IOException {
        if (backImageFile.isEmpty()
                || frontImageFile.isEmpty()) {
            return ApiResult.error("识别失败，请选择文件");
        }
        log.info("进入");
        byte[] backImage = backImageFile.getBytes();
        byte[] frontImage = frontImageFile.getBytes();

        IdCardInput input = new IdCardInput();
        input.setBackImage(backImage);
        input.setFrontImage(frontImage);

        log.info("开始");
        OcrResult result = getOcrService().extract(input);
        log.info("结束");
        return ApiResult.success(result);
    }

    @PostMapping("ocrBusinessLicense")
    @ResponseBody
    @ApiOperation(value = "ocr识别:营业执照")
    public ApiResult<?> ocrBusinessLicense(@RequestParam("imageFile") MultipartFile imageFile) throws Exception {
        if (imageFile.isEmpty()) {
            return ApiResult.error("识别失败，请选择文件");
        }
        log.info("进入");
        byte[] image = imageFile.getBytes();
        BusinessLicenseInput input = new BusinessLicenseInput();
        input.setImageData(image);
        log.info("开始");
        OcrResult ocrResult =getOcrService().extractBusinessLicense(input);
        log.info("结束");

        BusinessLicenseResult businessResult = (BusinessLicenseResult)ocrResult;
        Map<String, Object> result = new HashMap<>();
        if (StringUtils.isBlank(businessResult.getOperatingPeriod())) {
            businessResult.setOperatingPeriod("长期有效");
        }
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(businessResult);
        jsonObject.put("startTimeStr", DateUtil.format(DateUtil.parse(getDateFromStr(businessResult.getStartTime())), "yyyy-MM-dd"));
        jsonObject.put("registrationDateStr", DateUtil.format(DateUtil.parse(getDateFromStr(businessResult.getRegistrationDate())), "yyyy-MM-dd"));
        result.put("ocr", transOperatingPeriod(businessResult, jsonObject));
        return ApiResult.success(result);
    }

    /**
     * 处理商标注册证的有效期
     *
     * @param ocrResult
     * @param jsonObject
     * @return
     */
    static JSONObject transOperatingPeriod(BusinessLicenseResult ocrResult, JSONObject jsonObject) {
        if (StringUtils.isBlank(ocrResult.getOperatingPeriod()) || ocrResult.getOperatingPeriod().contains("长期")) {
            jsonObject.put("operatingPeriodFrom", "长期有效");
            jsonObject.put("operatingPeriodTo", "长期有效");
            return jsonObject;
        }
        String[] operatingPeriods = ocrResult.getOperatingPeriod().replaceAll("自", "").split("至");
        if (operatingPeriods.length == 2) {
            String from = DateUtil.format(DateUtil.parse(getDateFromStr(operatingPeriods[0])), "yyyy-MM-dd");
            String to = DateUtil.format(DateUtil.parse(getDateFromStr(operatingPeriods[1])), "yyyy-MM-dd");
            if (StringUtils.isNotBlank(from) && StringUtils.isNotBlank(to)) {
                jsonObject.put("operatingPeriodFrom", from);
                jsonObject.put("operatingPeriodTo", to);
                return jsonObject;
            }
            jsonObject.put("operatingPeriodFrom", "长期有效");
            jsonObject.put("operatingPeriodTo", "长期有效");
            return jsonObject;
        }
        return jsonObject;
    }

    /**
     * 从字符串中取出日期
     *
     * @param str 例如：自2022年5月6日
     * @return 2022年5月6日
     */
    public static String getDateFromStr(String str) {
        String s = "(\\d{4})年(\\d{1,2})月(\\d{1,2})日";
        Pattern p = Pattern.compile(s);
        Matcher m = p.matcher(str);
        while (m.find()) {
            return m.group(0);
        }
        return "";
    }


    @PostMapping("checkFace")
    @ResponseBody
    @ApiOperation(value = "人脸识别")
    public ApiResult<?> checkFace(CheckFaceDTO dto) throws Exception {
        return ApiResult.success(ocrService.checkFace(dto));
    }

}
