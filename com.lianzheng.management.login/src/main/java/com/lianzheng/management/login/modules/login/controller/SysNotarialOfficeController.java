package com.lianzheng.management.login.modules.login.controller;

import com.lianzheng.common_service.file_storage_sdk.entity.UploadFileRes;
import com.lianzheng.core.auth.mgmt.annotation.AuditLog;
import com.lianzheng.core.auth.mgmt.entity.SysNotarialOfficeEntity;
import com.lianzheng.core.auth.mgmt.service.SysNotarialOfficeService;
import com.lianzheng.core.server.ResponseBase;
import com.lianzheng.management.login.modules.login.util.FileStorageByNotaryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 公证处管理
 *
 * @author lxk
 **/
@RestController
@Slf4j
@RequestMapping("/sys/notarial")
public class SysNotarialOfficeController extends AbstractController {

    @Resource
    private SysNotarialOfficeService sysNotarialOfficeService;

    @Resource
    private FileStorageByNotaryUtil fileStorageByNotaryUtil;

    @GetMapping("/findList")
    public ResponseBase findList(SysNotarialOfficeEntity sysNotarialOfficeEntity) {
        return ResponseBase.ok().put("data", sysNotarialOfficeService.findList(sysNotarialOfficeEntity));
    }

    @AuditLog("增加公证处")
    @PostMapping("/insert")
    public ResponseBase insert(SysNotarialOfficeEntity sysNotarialOfficeEntity) {
        if (sysNotarialOfficeEntity.getNotaryOfficeName() == null) {
            return ResponseBase.error("notaryOfficeName不能为Null");
        }
        if (sysNotarialOfficeEntity.getSecretKey() == null) {
            return ResponseBase.error("secretKey不能为Null");
        }
        if (sysNotarialOfficeEntity.getOfficialSeal() != null){
            MultipartFile officialSeal = sysNotarialOfficeEntity.getOfficialSeal();
            try {
                UploadFileRes uploadFileRes = fileStorageByNotaryUtil.uploadFile(officialSeal,sysNotarialOfficeEntity);
                log.info(String.valueOf(uploadFileRes));
                sysNotarialOfficeEntity.setSealUrl(uploadFileRes.getFileUrl());
                sysNotarialOfficeService.insert(sysNotarialOfficeEntity);
                return ResponseBase.ok("添加成功");
            }catch (Exception e){
                return ResponseBase.error("文件上传出错");
            }
        }else {
            return ResponseBase.error("officialSeal文件不能为空");
        }
    }

    @AuditLog("更新公证处信息")
    @PostMapping("/update")
    public ResponseBase update(@RequestBody SysNotarialOfficeEntity sysNotarialOfficeEntity) {
        UploadFileRes uploadFileRes = null;
        if (sysNotarialOfficeEntity.getOfficialSeal() != null){
            MultipartFile officialSeal = sysNotarialOfficeEntity.getOfficialSeal();
            try {
                uploadFileRes = fileStorageByNotaryUtil.uploadFile(officialSeal,sysNotarialOfficeEntity);
                log.info(String.valueOf(uploadFileRes));
            }catch (Exception e){
                return ResponseBase.error("文件上传出错");
            }
        }
        if (uploadFileRes != null){
            sysNotarialOfficeEntity.setSealUrl(uploadFileRes.getFileUrl());
        }
        sysNotarialOfficeService.update(sysNotarialOfficeEntity);
        return ResponseBase.ok("更新成功");
    }

    @AuditLog("删除公证处")
    @GetMapping("/delete/{id}")
    public ResponseBase delete(@PathVariable("id") Long id) {
        SysNotarialOfficeEntity sysNotarialOfficeEntity = new SysNotarialOfficeEntity();
        sysNotarialOfficeEntity.setId(id);
        sysNotarialOfficeEntity.setFlag(1);
        sysNotarialOfficeService.update(sysNotarialOfficeEntity);
        return ResponseBase.ok("删除成功");
    }

}
