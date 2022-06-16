package com.lianzheng.h5.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.file.IOUtils;
import com.lianzheng.h5.common.ApiResult;
import com.lianzheng.h5.common.RedisHelper;
import com.lianzheng.h5.service.impl.DocumentServiceImpl;
import com.lianzheng.h5.service.impl.NotarzationMasterServiceImpl;
import com.lianzheng.h5.util.FileStorageByNotaryUtil;
import com.lianzheng.h5.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/document")
@Api(tags = "文档接口")
public class DocumentController {

    @Value("${file.path}")
    private String dirPath;

    @Autowired
    RedisHelper redisHelper;

    @Autowired
    DocumentServiceImpl documentService;

    @Autowired
    NotarzationMasterServiceImpl masterService;

    @Autowired
    private FileStorageByNotaryUtil fileStorageByNotaryUtil;

    /**
     * @param file
     * @return
     */
    @ResponseBody
    @PostMapping("/upload")
    @ApiOperation(value = "文档/文件上传")
    public ApiResult<?> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResult.error("上传失败，请选择文件");
        }
        File defFileDir = new File(dirPath);
        if (!defFileDir.exists()) {
            boolean isMkSuccess = defFileDir.mkdirs();
        }

        String prefix;
        try {
            prefix = Objects.requireNonNull(file.getOriginalFilename())
                    .substring(file.getOriginalFilename().lastIndexOf("."));
        } catch (Exception e) {
            prefix = ".png";
        }
        String fileName = UUID.randomUUID() + prefix;
        String newFilePath = dirPath + File.separator + fileName;
        File newFile = new File(newFilePath);
        try {
            FileUtils.copyInputStreamToFile(file.getInputStream(), newFile);
            return documentService.fileUrlResult(fileName,fileStorageByNotaryUtil.uploadFile(newFile).getFileUrl());
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResult.error("上传失败");
        }finally {
            //上传完毕后删除文件
            if (newFile.exists()){
                newFile.delete();
            }
        }
    }


    @ResponseBody
    @PostMapping("/uploadBase64")
    @ApiOperation(value = "文档/文件上传/Base64")
    public ApiResult<HashMap<String, String>> uploadBase64(@RequestParam("base64") String base64Data) {
        File defFileDir = new File(dirPath);
        if (!defFileDir.exists()) {
            boolean isMkSuccess = defFileDir.mkdirs();
        }

        String fileName = UUID.randomUUID() + ".png";
        String newFilePath = dirPath + File.separator + fileName;

        try {
            base64Data = base64Data.replaceAll(" ", "+");
            String[] arr = base64Data.split("base64,");
            OutputStream out = null;
            try {
                byte[] buffer = Base64.decodeBase64(arr[1]);
                out = new FileOutputStream(newFilePath);
                out.write(buffer);
            } catch (IOException e) {
                log.error("解析Base64图片信息并保存到某目录下出错!", e);
            } finally {
                IOUtils.closeQuietly(out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        File newFile = new File(newFilePath);
        ApiResult apiResult = documentService.fileUrlResult(fileName,fileStorageByNotaryUtil.uploadFile(newFile).getFileUrl());
        //上传完毕后删除文件
        if (newFile.exists()){
            newFile.delete();
        }
        return apiResult;
    }

    @ResponseBody
    @PostMapping("/uploadBase64Signature")
    @ApiOperation(value = "文档/文件上传/Base64/签名文件")
    public ApiResult<?> uploadSignatureBase64(
            @RequestParam("masterId") String masterId,
            @RequestParam("base64") String base64Data,
            @RequestParam("categoryCode") String categoryCode) {
        ApiResult<HashMap<String, String>> apiResult = this.uploadBase64(base64Data);
        Map<String, String> urlMap = apiResult.getData();
        String urlPath = urlMap.get("url");
        masterService.saveDocument(urlPath, categoryCode, masterId);
        return ApiResult.success();
    }

    @ResponseBody
    @PostMapping("/findRecordDocument")
    @ApiOperation(value = "查询记录对应的文档信息")
    public ApiResult<?> findRecordDocument(@RequestParam("masterId") String masterId,
                                           @RequestParam(value = "categoryCodes", required = false) String categoryCodes) {
        List<JSONObject> docList = documentService.getBaseMapper().recordDocument(masterId, categoryCodes);

        if (docList != null) {
            for (JSONObject jsonObject : docList) {
                String fileUrl = jsonObject.getString("uploadedAbsolutePath");
                jsonObject.replace("uploadedAbsolutePath", StringUtil.tokenPdfUrl(fileUrl));
            }
        }

        return ApiResult.success(docList);
    }
}
