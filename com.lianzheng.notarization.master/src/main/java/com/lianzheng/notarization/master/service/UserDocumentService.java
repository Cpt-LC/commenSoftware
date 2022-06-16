package com.lianzheng.notarization.master.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lianzheng.notarization.master.entity.DocumentEntity;
import com.lianzheng.notarization.master.form.DocumentForm;
import com.lianzheng.notarization.master.form.FileFrom;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public interface UserDocumentService extends IService<DocumentEntity> {
    List<DocumentForm> getlist(String id);

    //获取签字材料对应的word文档
    String getDocx(String id,String categoryCode);

    //使用公证处Wenjian url 和公证处的id获取直接访问的路径
    String getUrl(Long officeId,String url);
    public  String getToken(Long officeId,String url);

    public void addFile(String fileSource, String filename,String id,String categoryCode,Boolean uploadDocx);
    //删除指定类型文件
    public void deleteFileByCategoryCode(String id,String CategoryCode);

    /**
     * h5、建行保存材料信息
     * @param url
     * @param categoryCode
     * @param masterTableId
     */
    void saveDocument(String url, String categoryCode, String masterTableId);

    /**
     * 建行接口上传证明材料
     * @param file
     * @return
     * @throws Exception
     */
    String saveDocument(MultipartFile file) throws Exception;


    String save(MultipartFile file, FileFrom fileFrom);

    /**
     * 签字文件上传 并生成 pdf
     * @param file
     * @param fileFrom
     * @return
     */
    void saveSignFile(MultipartFile file, FileFrom fileFrom,ConcurrentHashMap<String, Map<String,Object>> hashMap);


    DocumentForm addFile(MultipartFile file, FileFrom fileFrom, String filename);

    void deleteFile(String id);

    void downloadZip(String id, HttpServletResponse response) throws Exception;
}
