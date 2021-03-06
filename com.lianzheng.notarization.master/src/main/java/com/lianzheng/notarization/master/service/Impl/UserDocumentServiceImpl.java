package com.lianzheng.notarization.master.service.Impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianzheng.common_service.file_storage_sdk.entity.UploadFileRes;
import com.lianzheng.core.auth.mgmt.dao.SysNotarialOfficeDao;
import com.lianzheng.core.auth.mgmt.entity.SysNotarialOfficeEntity;
import com.lianzheng.core.auth.mgmt.utils.FileTokenUtils;
import com.lianzheng.core.exceptionhandling.exception.COREException;
import com.lianzheng.core.pdf.service.GeneratePdf;
import com.lianzheng.notarization.master.common.CommonConstant;
import com.lianzheng.notarization.master.dao.UserDocumentDao;
import com.lianzheng.notarization.master.dao.UserNotarzationMasterDao;
import com.lianzheng.notarization.master.entity.DocumentEntity;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.enums.DocumentCategoryCode;
import com.lianzheng.notarization.master.form.DocumentForm;
import com.lianzheng.notarization.master.form.FileFrom;
import com.lianzheng.notarization.master.service.UserDocumentService;
import com.lianzheng.notarization.master.utils.FileStorageByNotaryUtil;
import lombok.SneakyThrows;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@DS("h5")
public class UserDocumentServiceImpl  extends ServiceImpl<UserDocumentDao,DocumentEntity> implements UserDocumentService {

    @Autowired
    private UserDocumentDao userDocumentDao;
    @Autowired
    private FileTokenUtils fileTokenUtils;
    @Autowired
    private UserNotarzationMasterDao userNotarzationMasterDao;
    @Autowired
    private  GeneratePdf generatePdf;
    @Autowired
    private SysNotarialOfficeDao sysNotarialOfficeDao;


    Path root = Paths.get("uploads");
    Path templatesRoot = Paths.get("templates");
    Path noticesRoot = Paths.get("notices");

    @Value("${fileUrl}")
    private String url;

    @Autowired
    private FileStorageByNotaryUtil fileStorageByNotaryUtil;

    @Override
    public List<DocumentForm> getlist(String id) {
        NotarzationMasterEntity notarzationMasterEntity =userNotarzationMasterDao.selectById(id);
        QueryWrapper<DocumentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("refererId",id).eq("isDeleted",0).ne("refererTableName",CommonConstant.Defult_Docx_Table);
        List<DocumentEntity> documentEntityList =  userDocumentDao.selectList(queryWrapper);
        List<DocumentForm> documentFormList =new ArrayList<>();
        if(documentEntityList==null||documentEntityList.size()<1){
            throw new RuntimeException("????????????????????????");
        }
        for(DocumentEntity item :documentEntityList){
            DocumentForm DocumentFormBean = new DocumentForm();
            BeanUtils.copyProperties(item,DocumentFormBean);
            String uuid = this.getUrl(notarzationMasterEntity.getNotarialOfficeId(),DocumentFormBean.getUploadedAbsolutePath());
            DocumentFormBean.setToken(uuid);
            documentFormList.add(DocumentFormBean);
        }
        return documentFormList;
    }


    /**
     *
     * @param id  ?????????id
     * @param categoryCode  ????????????
     * @return
     */
    @Override
    public  String getDocx(String id,String categoryCode){
        DocumentEntity documentEntity = getOne(
                new QueryWrapper<DocumentEntity>().eq("refererId",id).eq("isDeleted",0).eq("categoryCode",categoryCode).eq("refererTableName",CommonConstant.Defult_Docx_Table)
        );

        return getUrl(userNotarzationMasterDao.selectById(id).getNotarialOfficeId(),documentEntity.getUploadedAbsolutePath());
    }

    /**
     *
     * @param officeId  ?????????id
     * @param url
     * @return
     */
    @Override
    public  String getUrl(Long officeId,String url){
        SysNotarialOfficeEntity sysNotarialOfficeEntity = sysNotarialOfficeDao.selectById(officeId);
        String fileName=  url.substring(url.lastIndexOf("/")+1);
        String token = FileStorageByNotaryUtil.generateToken(fileName,sysNotarialOfficeEntity.getSecretKey());
        return url + "?token=" + token;
    }

    @Override
    public  String getToken(Long officeId,String url){
        SysNotarialOfficeEntity sysNotarialOfficeEntity = sysNotarialOfficeDao.selectById(officeId);
        String fileName=  url.substring(url.lastIndexOf("/")+1);
        String token = FileStorageByNotaryUtil.generateToken(fileName,sysNotarialOfficeEntity.getSecretKey());
        return token;
    }




    /**
     *
     * @param fileSource ??????????????????
     * @param filename ?????????
     * @param id ?????????id
     * @param categoryCode  ????????????
     * @param uploadDocx  ?????????????????????docx
     */
    @Override
//    @Transactional(rollbackFor = Exception.class)
    public void addFile(String fileSource, String filename,String id,String categoryCode,Boolean uploadDocx) {
        createDocument(fileSource,filename,id,categoryCode,CommonConstant.Defult_Table);

        //??????????????????  ???????????????docx
        if (uploadDocx){
            fileSource = fileSource.replace(".pdf",".docx");
            filename = filename.replace(".pdf",".docx");
            createDocument(fileSource,filename,id,categoryCode,CommonConstant.Defult_Docx_Table);
        }
    }


    public void createDocument(String fileSource, String filename,String id,String categoryCode,String refererTable){
        File file =new File(fileSource);
        String type=filename.indexOf(".")!=-1?filename.substring(filename.lastIndexOf("."), filename.length()):null;//????????????
        String Name = filename.indexOf(".")!=-1?filename.substring(0,filename.lastIndexOf(".")):null; //?????????????????????
        BigDecimal size = new BigDecimal(file.length());
        BigDecimal unit = new BigDecimal(1024.0*1024.0);
        BigDecimal sizeM = size.divide(unit);//????????????MB


        UploadFileRes uploadFileRes = fileStorageByNotaryUtil.uploadFile(file,userNotarzationMasterDao.selectById(id).getNotarialOfficeId());
        String AbsolutePath =removeToken(uploadFileRes.getFileUrl());//????????????
        String RelativePath = fileSource;//????????????
        String fileHash = SecureUtil.md5(file);


        DocumentEntity documentEntity =new DocumentEntity();
        documentEntity.setId(UUID.randomUUID().toString());
        documentEntity.setCategoryCode(categoryCode);
        documentEntity.setCreatedTime(new Date());
        documentEntity.setStorageType("local");
        documentEntity.setFileName(Name);
        documentEntity.setFileExt(type);
        documentEntity.setUploadedAbsolutePath(AbsolutePath);
        documentEntity.setUploadedRelativePath(RelativePath);
        documentEntity.setRefererTableName(refererTable);
        documentEntity.setRefererId(id);
        documentEntity.setFileSize(size);
        documentEntity.setFileSizeM(sizeM);
        documentEntity.setFileHash(fileHash);
        documentEntity.setStorePath(uploadFileRes.getStorePath());
        documentEntity.setStoreGroup(uploadFileRes.getStoreGroup());
        //todo ??????hash
        userDocumentDao.insert(documentEntity);
        file.delete();
    }


    public static String removeToken(String urlPath) {
        if (urlPath.contains("?token")) {
            return urlPath.substring(0, urlPath.indexOf("?token"));
        }
        return urlPath;
    }

    @Override
    public void deleteFileByCategoryCode(String id, String CategoryCode) {
        userDocumentDao.deleteFileByCategoryCode(id, CategoryCode);

    }

    @Override
    public void saveDocument(String url, String categoryCode, String masterTableId) {
        try {
            //??????????????????
            DocumentEntity document = new DocumentEntity();
            document.setId(UUID.randomUUID().toString());
            extraFileInformation(url, categoryCode, masterTableId, document);
            this.save(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void extraFileInformation(String url, String categoryCode, String masterTableId, DocumentEntity document) {
        String fileNameAndSuffix = url.substring(url.lastIndexOf("/") + 1);
        String dirPath = this.root + "/" + fileNameAndSuffix;
        File localFile = new File(dirPath);

        document.setFileName(fileNameAndSuffix.split("\\.")[0]);
        document.setFileSize(new BigDecimal(localFile.length()));
        document.setFileExt(fileNameAndSuffix.split("\\.")[1]);

        BigDecimal bigDecimal = BigDecimal.valueOf(localFile.length() / 1024.0 / 1024.0);
        document.setFileSizeM(bigDecimal);
        document.setRefererTableName("notarzation_master");
        document.setRefererId(masterTableId);
        document.setCategoryCode(categoryCode);
        document.setUploadedAbsolutePath(url);
        document.setUploadedRelativePath(dirPath);
    }

    @Override
    public String saveDocument(MultipartFile file) throws Exception {
        //????????????,?????????????????????
        String prefix;
        try {
            prefix = Objects.requireNonNull(file.getOriginalFilename())
                    .substring(file.getOriginalFilename().lastIndexOf("."));
        } catch (Exception e) {
            prefix = ".png";
        }
        String uuid =  UUID.randomUUID().toString();
        String fileName = uuid + prefix;
        String newFilePath = this.root + "/" + fileName;
        file.transferTo(this.root.resolve(fileName));
        String fileUrl =  url + newFilePath;
//        fileUrl = fileUrl + "?token=" + fileTokenUtils.fileToken(uuid);

        return fileUrl;
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public String save(MultipartFile file, FileFrom fileFrom) {
        String categoryCode= fileFrom.getCategoryCode();
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String filename = timeStamp + '_' + file.getOriginalFilename();
//            String timeStamp = UUID.randomUUID().toString();
//            String origin = file.getOriginalFilename();
//            String type=origin.indexOf(".")!=-1?origin.substring(origin.lastIndexOf("."), origin.length()):null;//????????????
//            String filename = timeStamp + type;
            //?????????????????????
            if(categoryCode.equals(DocumentCategoryCode.PDF_NOTARIZATION.getCode())||categoryCode.equals(DocumentCategoryCode.PDF_NOTARIZATION_CERT.getCode())){
                NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(fileFrom.getRefererId());
                filename ="PDF_NOTARIZATION_"+ notarzationMasterEntity.getProcessNo() + ".docx";
                Map<String,String> mergeMap =new HashMap<>();
                mergeMap.put("??????","");
                mergeMap.put("??????","");
                mergeMap.put("????????????","");
                mergeMap.put("????????????","");
                if(categoryCode.equals(DocumentCategoryCode.PDF_NOTARIZATION_CERT.getCode())){
                    filename ="PDF_NOTARIZATION_CERT_"+ notarzationMasterEntity.getProcessNo() + ".docx";
                    mergeMap.put("certificateNo","");
                }
                File delPdf = new File(this.noticesRoot+"/"+filename);
                if(delPdf.exists()){
                    delPdf.delete();
                }
                GeneratePdf.rangeMilmerge(file.getInputStream(),mergeMap);//???????????????????????????
                Files.copy(file.getInputStream(), this.noticesRoot.resolve(filename));
                return filename;
            }

            Files.copy(file.getInputStream(), this.root.resolve(filename));
            return filename;
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }


    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public void saveSignFile(MultipartFile file, FileFrom fileFrom, ConcurrentHashMap<String, Map<String,Object>> hashMap){
        String categoryCode= fileFrom.getCategoryCode();
        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(fileFrom.getRefererId());
        String processNo = notarzationMasterEntity.getProcessNo();
        String filename = "";

        //??????????????????????????????????????????????????????
        switch (categoryCode){
            case "PDF-NOTICE":
                filename = "PDF_NOTICE_" + processNo + ".docx";break;
            case "PDF-PAY-NOTICE":
                filename = "PDF_PAY_NOTICE_" + processNo + ".docx";break;
            case "PDF-APPLICATION":
                filename = "PDF_APPLICATION_" + processNo + ".docx";break;
            case "PDF-AC-NOTICE":
                filename = "PDF_AC_NOTICE_" + processNo + ".docx";break;
            case "PDF-QUESTION":
                filename = "PDF_QUESTION_" + processNo + ".docx";break;
            case "PDF-FO-NOTICE":
                filename = "PDF_FO_NOTICE_" + processNo + ".docx";break;
            case "PDF-HOME-ATTORNEY":
                filename = "PDF_HOME_ATTORNEY_" + processNo + ".docx";break;
            case "PDF-NOTICE-ENTRUSTED":
                filename ="PDF_NOTICE_ENTRUSTED_" + processNo + ".docx";break;
            case  "PDF-NOTICE-HANDLE" :
                filename = "PDF_NOTICE_HANDLE_" +  processNo + ".docx";break;
            case "PDF-NOTICE-DECLARATION":
                filename = "PDF_NOTICE_DECLARATION_"+  processNo + ".docx";break;
            default:
                throw new COREException("???????????????????????????",1);
        }

        //?????????????????? ?????????????????????
        if(!categoryCode.equals("PDF-PAY-NOTICE")&&!categoryCode.equals("PDF-HOME-ATTORNEY")){
            Map<String,String> mergeMap =new HashMap<>();
            mergeMap.put("ApplicantSig","");
            GeneratePdf.rangeMilmerge(file.getInputStream(),mergeMap);
            //???????????????????????????
        }
        file.transferTo(this.noticesRoot.resolve(filename));
        String destinationDocFile = this.noticesRoot + "/" + filename;
        String destinationPdfFile = destinationDocFile.replace(".docx",".pdf");
        generatePdf.generatePdf(hashMap, destinationDocFile, destinationPdfFile,"", false);
        this.addFile(destinationPdfFile,file.getName(),notarzationMasterEntity.getId(),categoryCode,true);
        File file1 = new File(destinationDocFile);
        File file2 = new File(destinationPdfFile);
        file1.delete();
        file2.delete();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentForm addFile(MultipartFile file, FileFrom fileFrom,String filename) {
        String categoryCode= fileFrom.getCategoryCode();
        String type=filename.indexOf(".")!=-1?filename.substring(filename.lastIndexOf("."), filename.length()):null;//????????????
        String Name = filename.indexOf(".")!=-1?filename.substring(0,filename.lastIndexOf(".")):null; //?????????????????????
        BigDecimal size = new BigDecimal(file.getSize());
        BigDecimal unit = new BigDecimal(1024.0*1024.0);
        BigDecimal sizeM = size.divide(unit);//????????????MB


        String AbsolutePath =url + this.root+"/"+filename;//????????????
        String RelativePath = this.root+"/"+filename;//????????????


        //?????????????????????
        if(categoryCode.equals(DocumentCategoryCode.PDF_NOTARIZATION.getCode())||categoryCode.equals(DocumentCategoryCode.PDF_NOTARIZATION_CERT.getCode())){
            userDocumentDao.deleteFileByCategoryCode(fileFrom.getRefererId(),categoryCode);//?????????????????????
            AbsolutePath = url + this.noticesRoot+"/"+filename;//?????????????????????
            RelativePath = this.noticesRoot+"/"+filename;//????????????
        }
        String fileHash = SecureUtil.md5(new File(RelativePath));
        DocumentEntity documentEntity =new DocumentEntity();
        documentEntity.setId(UUID.randomUUID().toString());
        documentEntity.setCategoryCode(fileFrom.getCategoryCode());
        documentEntity.setCreatedTime(new Date());
        documentEntity.setStorageType("local");
        documentEntity.setFileName(Name);
        documentEntity.setFileExt(type);
        documentEntity.setUploadedAbsolutePath(AbsolutePath);
        documentEntity.setUploadedRelativePath(RelativePath);
        documentEntity.setRefererTableName(fileFrom.getRefererTableName());
        documentEntity.setRefererId(fileFrom.getRefererId());
        documentEntity.setFileSize(size);
        documentEntity.setFileSizeM(sizeM);
        documentEntity.setFileHash(fileHash);
        //todo ??????hash
//        documentEntity.setChainHash();

        userDocumentDao.insert(documentEntity);


        DocumentForm form = new DocumentForm();
        BeanUtils.copyProperties(documentEntity,form);
        String uuid = fileTokenUtils.fileToken(documentEntity.getFileName());
        form.setToken(uuid);
        return form;

    }

    @Override
    public void deleteFile(String id) {
        DocumentEntity documentEntity = userDocumentDao.selectOne(
                new QueryWrapper<DocumentEntity>().eq("id",id).eq("isDeleted",0)
        );
        if(documentEntity==null){
            throw new RuntimeException("???????????????");
        }
        documentEntity.setIsDeleted(1);
        documentEntity.setDeletedTime(new Date());
        userDocumentDao.updateById(documentEntity);
        File file = new File(documentEntity.getUploadedRelativePath());
        if(file.exists()){
            file.delete();
        }
    }

    @Override
    public void downloadZip(String id, HttpServletResponse response) throws Exception {

        List<DocumentEntity> documentEntities = userDocumentDao.selectList(
                new QueryWrapper<DocumentEntity>().eq("refererId",id).eq("isDeleted",0).and(
                        QueryWrapper -> QueryWrapper.eq("categoryCode","PDF_DRAFT")//todo  ??????????????????????????????
                )
        );
        if(documentEntities==null||documentEntities.size()<1){
            throw new RuntimeException("????????????");
        }
        String zipPath = UUID.randomUUID().toString();
        // ??????????????????ids,??????????????????url
        List<String> paths = new ArrayList<>();
        for(DocumentEntity item:documentEntities){
            paths.add(item.getUploadedRelativePath());
        }

        if (paths.size() != 0) {
            // ??????????????????,??????????????????
            String zipFilePath = this.root+"/"+zipPath+".zip";
            // ???????????????,?????????,??????????????????????????????????????????,??????????????????????????????,??????zip???
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFilePath));
            // ??????????????????????????????,?????????????????????????????????????????????????????????
            for (String path : paths) {
                fileToZip(path, zipOut);
            }
            // ???????????????,???????????????
            zipOut.close();

            //?????????????????????????????????ISO-8859-1??????
            String fileName = new String(("??????????????????.zip").getBytes(),"ISO-8859-1");
            response.setHeader("Content-Disposition", "attchment;filename="+fileName);

            //???????????????????????????,??????????????????????????????,??????????????????????????????
            ServletOutputStream outputStream = response.getOutputStream();
            FileInputStream inputStream = new FileInputStream(zipFilePath);
            // copy?????????????????????,????????????????????????????????????
            IOUtils.copy(inputStream, outputStream);

            // ???????????????
            inputStream.close();

            //?????????????????????????????????zip???
            File fileTempZip = new File(zipFilePath);
            fileTempZip.delete();
        }
    }


    public static void fileToZip(String filePath,ZipOutputStream zipOut) throws IOException {
        // ?????????????????????
        File file = new File(filePath);
        // ??????????????????,???????????????????????????,???????????????????????????,???fileName
        String fileName = file.getName();
        FileInputStream fileInput = new FileInputStream(filePath);
        // ??????
        byte[] bufferArea = new byte[1024 * 10];
        BufferedInputStream bufferStream = new BufferedInputStream(fileInput, 1024 * 10);
        // ???????????????????????????zip?????????????????????,fileName????????????????????????????????????
        zipOut.putNextEntry(new ZipEntry(fileName));
        int length = 0;
        while ((length = bufferStream.read(bufferArea, 0, 1024 * 10)) != -1) {
            zipOut.write(bufferArea, 0, length);
        }
        //?????????
        fileInput.close();
        // ?????????????????????????????????????????????,??????????????????
        bufferStream.close();
    }




}
