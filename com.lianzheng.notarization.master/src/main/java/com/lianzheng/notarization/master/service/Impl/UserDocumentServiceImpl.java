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
            throw new RuntimeException("文件资源获取失败");
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
     * @param id  公证的id
     * @param categoryCode  文件类型
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
     * @param officeId  公证处id
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
     * @param fileSource 文件相对路径
     * @param filename 文件名
     * @param id 公证的id
     * @param categoryCode  文件分类
     * @param uploadDocx  是否上传同路径docx
     */
    @Override
//    @Transactional(rollbackFor = Exception.class)
    public void addFile(String fileSource, String filename,String id,String categoryCode,Boolean uploadDocx) {
        createDocument(fileSource,filename,id,categoryCode,CommonConstant.Defult_Table);

        //如果签字材料  插入对应的docx
        if (uploadDocx){
            fileSource = fileSource.replace(".pdf",".docx");
            filename = filename.replace(".pdf",".docx");
            createDocument(fileSource,filename,id,categoryCode,CommonConstant.Defult_Docx_Table);
        }
    }


    public void createDocument(String fileSource, String filename,String id,String categoryCode,String refererTable){
        File file =new File(fileSource);
        String type=filename.indexOf(".")!=-1?filename.substring(filename.lastIndexOf("."), filename.length()):null;//文件后缀
        String Name = filename.indexOf(".")!=-1?filename.substring(0,filename.lastIndexOf(".")):null; //文件前面的名字
        BigDecimal size = new BigDecimal(file.length());
        BigDecimal unit = new BigDecimal(1024.0*1024.0);
        BigDecimal sizeM = size.divide(unit);//文件大小MB


        UploadFileRes uploadFileRes = fileStorageByNotaryUtil.uploadFile(file,userNotarzationMasterDao.selectById(id).getNotarialOfficeId());
        String AbsolutePath =removeToken(uploadFileRes.getFileUrl());//绝对路径
        String RelativePath = fileSource;//相对路径
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
        //todo 链的hash
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
            //图片信息保存
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
        //保存文件,并返回文件地址
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
//            String type=origin.indexOf(".")!=-1?origin.substring(origin.lastIndexOf("."), origin.length()):null;//文件后缀
//            String filename = timeStamp + type;
            //拟稿纸单独处理
            if(categoryCode.equals(DocumentCategoryCode.PDF_NOTARIZATION.getCode())||categoryCode.equals(DocumentCategoryCode.PDF_NOTARIZATION_CERT.getCode())){
                NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(fileFrom.getRefererId());
                filename ="PDF_NOTARIZATION_"+ notarzationMasterEntity.getProcessNo() + ".docx";
                Map<String,String> mergeMap =new HashMap<>();
                mergeMap.put("签名","");
                mergeMap.put("盖章","");
                mergeMap.put("公证编号","");
                mergeMap.put("落款时间","");
                if(categoryCode.equals(DocumentCategoryCode.PDF_NOTARIZATION_CERT.getCode())){
                    filename ="PDF_NOTARIZATION_CERT_"+ notarzationMasterEntity.getProcessNo() + ".docx";
                    mergeMap.put("certificateNo","");
                }
                File delPdf = new File(this.noticesRoot+"/"+filename);
                if(delPdf.exists()){
                    delPdf.delete();
                }
                GeneratePdf.rangeMilmerge(file.getInputStream(),mergeMap);//验证是否有坐标标签
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

        //文件重新上传新增文件类型须在下面增加
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
                throw new COREException("无法上传该类型文件",1);
        }

        //非缴费通知单 验证是否有签名
        if(!categoryCode.equals("PDF-PAY-NOTICE")&&!categoryCode.equals("PDF-HOME-ATTORNEY")){
            Map<String,String> mergeMap =new HashMap<>();
            mergeMap.put("ApplicantSig","");
            GeneratePdf.rangeMilmerge(file.getInputStream(),mergeMap);
            //验证是否有坐标标签
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
        String type=filename.indexOf(".")!=-1?filename.substring(filename.lastIndexOf("."), filename.length()):null;//文件后缀
        String Name = filename.indexOf(".")!=-1?filename.substring(0,filename.lastIndexOf(".")):null; //文件前面的名字
        BigDecimal size = new BigDecimal(file.getSize());
        BigDecimal unit = new BigDecimal(1024.0*1024.0);
        BigDecimal sizeM = size.divide(unit);//文件大小MB


        String AbsolutePath =url + this.root+"/"+filename;//绝对路径
        String RelativePath = this.root+"/"+filename;//相对路径


        //拟稿纸单独处理
        if(categoryCode.equals(DocumentCategoryCode.PDF_NOTARIZATION.getCode())||categoryCode.equals(DocumentCategoryCode.PDF_NOTARIZATION_CERT.getCode())){
            userDocumentDao.deleteFileByCategoryCode(fileFrom.getRefererId(),categoryCode);//删除所有拟稿纸
            AbsolutePath = url + this.noticesRoot+"/"+filename;//可访问绝对路径
            RelativePath = this.noticesRoot+"/"+filename;//相对路径
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
        //todo 链的hash
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
            throw new RuntimeException("文件不存在");
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
                        QueryWrapper -> QueryWrapper.eq("categoryCode","PDF_DRAFT")//todo  公证员上传的证明材料
                )
        );
        if(documentEntities==null||documentEntities.size()<1){
            throw new RuntimeException("无文件！");
        }
        String zipPath = UUID.randomUUID().toString();
        // 此处模拟处理ids,拿到文件下载url
        List<String> paths = new ArrayList<>();
        for(DocumentEntity item:documentEntities){
            paths.add(item.getUploadedRelativePath());
        }

        if (paths.size() != 0) {
            // 创建临时路径,存放压缩文件
            String zipFilePath = this.root+"/"+zipPath+".zip";
            // 压缩输出流,包装流,将临时文件输出流包装成压缩流,将所有文件输出到这里,打成zip包
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFilePath));
            // 循环调用压缩文件方法,将一个一个需要下载的文件打入压缩文件包
            for (String path : paths) {
                fileToZip(path, zipOut);
            }
            // 压缩完成后,关闭压缩流
            zipOut.close();

            //拼接下载默认名称并转为ISO-8859-1格式
            String fileName = new String(("我的压缩文件.zip").getBytes(),"ISO-8859-1");
            response.setHeader("Content-Disposition", "attchment;filename="+fileName);

            //该流不可以手动关闭,手动关闭下载会出问题,下载完成后会自动关闭
            ServletOutputStream outputStream = response.getOutputStream();
            FileInputStream inputStream = new FileInputStream(zipFilePath);
            // copy方法为文件复制,在这里直接实现了下载效果
            IOUtils.copy(inputStream, outputStream);

            // 关闭输入流
            inputStream.close();

            //下载完成之后，删掉这个zip包
            File fileTempZip = new File(zipFilePath);
            fileTempZip.delete();
        }
    }


    public static void fileToZip(String filePath,ZipOutputStream zipOut) throws IOException {
        // 需要压缩的文件
        File file = new File(filePath);
        // 获取文件名称,如果有特殊命名需求,可以将参数列表拓展,传fileName
        String fileName = file.getName();
        FileInputStream fileInput = new FileInputStream(filePath);
        // 缓冲
        byte[] bufferArea = new byte[1024 * 10];
        BufferedInputStream bufferStream = new BufferedInputStream(fileInput, 1024 * 10);
        // 将当前文件作为一个zip实体写入压缩流,fileName代表压缩文件中的文件名称
        zipOut.putNextEntry(new ZipEntry(fileName));
        int length = 0;
        while ((length = bufferStream.read(bufferArea, 0, 1024 * 10)) != -1) {
            zipOut.write(bufferArea, 0, length);
        }
        //关闭流
        fileInput.close();
        // 需要注意的是缓冲流必须要关闭流,否则输出无效
        bufferStream.close();
    }




}
