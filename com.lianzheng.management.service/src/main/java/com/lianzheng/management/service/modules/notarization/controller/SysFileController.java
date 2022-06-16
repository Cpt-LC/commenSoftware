package com.lianzheng.management.service.modules.notarization.controller;


import com.alibaba.fastjson.JSONObject;
import com.lianzheng.core.auth.mgmt.annotation.AuditLog;
import com.lianzheng.core.exceptionhandling.exception.COREException;
import com.lianzheng.core.server.ResponseBase;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.enums.DocumentCategoryCode;
import com.lianzheng.notarization.master.form.DocumentForm;
import com.lianzheng.notarization.master.form.FileFrom;
import com.lianzheng.notarization.master.form.MapCommonForm;
import com.lianzheng.notarization.master.service.UserDocumentService;
import com.lianzheng.notarization.master.service.UserNotarzationMasterService;
import lombok.extern.java.Log;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log
@RestController
@RequestMapping("sys/file")
public class SysFileController {

    private final Path root = Paths.get("uploads");
    private Path templatesRoot = Paths.get("templates");
    private Path noticesRoot = Paths.get("notices");
    @Autowired
    private UserNotarzationMasterService userNotarzationMasterService;
    @Autowired
    private UserDocumentService userDocumentService;

    @AuditLog("文件上传")
    @PostMapping("/upload")
    public ResponseBase uploadFile(FileFrom fileFrom) {
        List<MultipartFile> files = fileFrom.getFiles();
        MultipartFile file = fileFrom.getFile();
        files = files == null ? new ArrayList<>() : files;
        if(file!=null){
            files.add(file);
        }
        //验证文件有效性
        for (MultipartFile checkItem : files) {
            String type = null;
            String fileName = checkItem.getOriginalFilename();// 文件原名称
            type = fileName.indexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()) : null;

            if (type != null && !"GIF".equals(type.toUpperCase()) && !"PNG".equals(type.toUpperCase()) && !"JPG".equals(type.toUpperCase()) && !"JPEG".equals(type.toUpperCase()) && !"BMP".equals(type.toUpperCase())
                    &&!"DOCX".equals(type.toUpperCase())&&!"DOC".equals(type.toUpperCase())) {
                return ResponseBase.error(fileName + "文件类型错误，请重试");
            }
            //无后缀文件
            if(type==null){
                return ResponseBase.error(fileName + "文件类型错误，请重试");
            }
            //非公证书不可为word文件
            String categoryCode =fileFrom.getCategoryCode();
            if(categoryCode.equals(DocumentCategoryCode.PDF_NOTARIZATION.getCode())||categoryCode.equals(DocumentCategoryCode.PDF_NOTARIZATION_CERT.getCode())){
                if(!"DOCX".equals(type.toUpperCase())){
                    return ResponseBase.error("公证书应为docx文件");
                }
            }else if("DOCX".equals(type.toUpperCase())){
                return ResponseBase.error(fileName + "文件类型错误，请重试");
            }
        }


        for (MultipartFile Item : files) {
            String message = "";
//                try {
            String filename = userDocumentService.save(Item,fileFrom);
            message = "Uploaded the file successfully: " + filename;
            // 插入数据库
            DocumentForm entity = userDocumentService.addFile(Item, fileFrom, filename);

            log.info(message);
//                } catch (Exception e) {
//                    message = "Could not upload the file: " + Item.getOriginalFilename() + "!";
//                    return ResponseBase.error(message);
//                }
        }
        List<DocumentForm> documentFormList = userDocumentService.getlist(fileFrom.getRefererId());
        NotarzationMasterEntity notarzationMasterEntity=userNotarzationMasterService.getById(fileFrom.getRefererId());
        HashMap<String,Object> hashMap = JSONObject.parseObject(JSONObject.toJSONString(notarzationMasterEntity), HashMap.class);
        Map<String, List<MapCommonForm>> docCategories = userNotarzationMasterService.parseDocuments(documentFormList, hashMap);
        List<MapCommonForm> forms = docCategories.get("签字材料");
        return ResponseBase.ok("上传成功").put("docs", forms);
    }


    @AuditLog("签字文件文件上传")
    @PostMapping("/uploadSignFile")
    public ResponseBase uploadSignFile(FileFrom fileFrom) {
        MultipartFile file = fileFrom.getFile();
        if(file.isEmpty()){
           throw new COREException("文件不可为空",1);
        }
        if(StringUtils.isEmpty(fileFrom.getRefererId())){
            throw new COREException("数据错误，请联系管理员",1);
        }
        String fileName = file.getOriginalFilename();// 文件原名称
        String type = fileName.indexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()) : null;
        if(type==null||!"DOCX".equals(type.toUpperCase())){
            throw new COREException("请上传docx文件",1);
        }
        NotarzationMasterEntity notarzationMasterEntity=userNotarzationMasterService.getById(fileFrom.getRefererId());
        ConcurrentHashMap<String, Map<String, Object>> hashMap = userNotarzationMasterService.getSignHashMap(notarzationMasterEntity);
        userDocumentService.saveSignFile(file,fileFrom,hashMap);

        return ResponseBase.ok("上传成功");
    }

    @AuditLog("签字文件下载")
    @GetMapping("/downloadSignFile")
    public ResponseBase downloadSignFile(String id,String categoryCode){
        String docxUrl = userDocumentService.getDocx(id, categoryCode);
        return ResponseBase.ok().put("data",docxUrl);
    }


    @AuditLog("文件删除")
    @PostMapping("/delete")
    public ResponseBase deleteFile(@RequestParam("id") String id) {
        if (id == null || id.equals("")) {
            return ResponseBase.error("未选择文件删除失败，请联系管理员");
        }

        userDocumentService.deleteFile(id);

        return ResponseBase.ok("删除成功");
    }


    /**
     * 文件下载
     * @param fileName
     * @param categoryCode
     * @return
     * @throws Exception
     */
    @AuditLog("文件下载")
    @GetMapping("/download")
    public ResponseEntity<Resource> downLoad(@RequestParam("fileName")String fileName,@RequestParam("categoryCode")String categoryCode, HttpServletResponse response) throws Exception {
        Path filePath = null;
        switch (categoryCode){
            case "PDF-NOTARIZATION-CERT":
            case "PDF-NOTARIZATION":
            case "PDF-DRAFT-CERT":
            case "PDF-DRAFT":filePath=this.noticesRoot;break;
            default:filePath=this.root;break;
        }
        String path =filePath+"/"+fileName;
        File f = new File(path);
        if (!f.exists()) {
            response.sendError(404, "File not found!");
            throw new RuntimeException("Could not read the file!" + fileName);
        }
        String filename=new String(f.getName().getBytes("utf-8"),"iso-8859-1");
        Path file = filePath.resolve(fileName);

        String metaType = "application/msword";// MediaType.;
        response.setHeader(HttpHeaders.CONTENT_TYPE, metaType); //通知LogResponseBodyAdvice
        Resource resource = new UrlResource(file.toUri());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, metaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"").body(resource);
    }


    /**
     * 单文件下载支持在线预览
     * @param fileName
     * @param response
     * @param isOnLine
     * @throws Exception
     */
    @GetMapping("/downLoadfile")
    public void downLoadfile(@RequestParam("fileName")String fileName,@RequestParam("categoryCode")String categoryCode, HttpServletResponse response, boolean isOnLine) throws Exception {
        Path filePath = null;
        switch (categoryCode){
            case "PDF-DRAFT":filePath=this.noticesRoot;break;
            default:filePath=this.root;break;
        }
        String path = filePath+"/"+fileName;
        File f = new File(path);
        if (!f.exists()) {
            response.sendError(404, "File not found!");
            return;
        }
        String filename=new String(f.getName().getBytes("utf-8"),"iso-8859-1");
        BufferedInputStream br = new BufferedInputStream(new FileInputStream(f));
        byte[] buf = new byte[1024];
        int len = 0;

        response.reset(); // 非常重要
        if (isOnLine) { // 在线打开方式
            URL u = new URL("file:\\"+System.getProperty("user.dir")+"\\" + filePath+"\\"+fileName);

            response.setContentType(u.openConnection().getContentType());
            response.setHeader("Content-Disposition", "inline; filename=" + filename);
            // 文件名应该编码成UTF-8
        } else { // 纯下载方式
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        }
        OutputStream out = response.getOutputStream();
        while ((len = br.read(buf)) > 0)
            out.write(buf, 0, len);
        br.close();
        out.close();
    }


    /**
     * 批量下载
     * @param id
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/downloadZip", method = RequestMethod.GET)
    public void downloadZip(@RequestParam("id") String id, HttpServletResponse response) throws Exception {
        userDocumentService.downloadZip(id,response);
    }




}
