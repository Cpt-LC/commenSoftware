package com.lianzheng.h5.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianzheng.common_service.file_storage_sdk.entity.UploadFileReq;
import com.lianzheng.common_service.file_storage_sdk.entity.UploadFileRes;
import com.lianzheng.h5.common.ApiResult;
import com.lianzheng.h5.common.RedisHelper;
import com.lianzheng.h5.entity.Document;
import com.lianzheng.h5.jwt.util.JwtUtils;
import com.lianzheng.h5.mapper.DocumentMapper;
import com.lianzheng.h5.service.IDocumentService;
import com.lianzheng.h5.util.FileStorageByNotaryUtil;
import com.lianzheng.h5.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;

/**
 * <p>
 * 上传文件 服务实现类
 * </p>
 *
 * @author 代树鸣
 * @since 2021-12-07
 */
@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document> implements IDocumentService {

    public ApiResult<HashMap<String, String>> fileUrlResult(String fileName ,String url) {

        String token = FileStorageByNotaryUtil.generateToken(fileName, JwtUtils.getNotarialOfficeSecretKey());
        HashMap<String, String> responseMap = new HashMap<>();
        responseMap.put("url", url + "?token=" + token);
        return ApiResult.success(responseMap);
    }

}
