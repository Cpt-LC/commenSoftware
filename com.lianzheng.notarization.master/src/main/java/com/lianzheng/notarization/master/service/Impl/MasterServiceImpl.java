package com.lianzheng.notarization.master.service.Impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lianzheng.core.auth.mgmt.config.ShiroUtils;
import com.lianzheng.core.auth.mgmt.entity.SysUserEntity;
import com.lianzheng.core.auth.mgmt.service.SysUserService;
import com.lianzheng.core.auth.mgmt.utils.FileTokenUtils;
import com.lianzheng.core.pdf.PdfVarEntity;
import com.lianzheng.core.pdf.service.GeneratePdf;
import com.lianzheng.notarization.master.dao.UserDao;
import com.lianzheng.notarization.master.dao.UserDocumentDao;
import com.lianzheng.notarization.master.dao.UserNotarzationMasterDao;
import com.lianzheng.notarization.master.dao.UserOrderDao;
import com.lianzheng.notarization.master.entity.DocumentEntity;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.entity.OrderEntity;
import com.lianzheng.notarization.master.entity.UserEntity;
import com.lianzheng.notarization.master.enums.*;

import com.lianzheng.notarization.master.form.MapCommonForm;
import com.lianzheng.notarization.master.service.MasterService;
import com.lianzheng.notarization.master.service.UserDocumentService;
import com.lianzheng.notarization.master.service.UserNotarzationMasterService;
import com.lianzheng.notarization.master.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.transaction.NotSupportedException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service("masterNotarization")
@DS("h5")
public class MasterServiceImpl implements MasterService {
    @Autowired
    private UserNotarzationMasterDao userNotarzationMasterDao;
    @Autowired
    private UserNotarzationMasterService userNotarzationMasterService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private UserDocumentDao userDocumentDao;
    @Autowired
    private GeneratePdf generatePdf;
    @Autowired
    private UserDocumentService userDocumentService;
    @Autowired
    private UserOrderDao userOrderDao;


    Path root = Paths.get("uploads");
    Path templatesRoot = Paths.get("templates");
    Path noticesRoot = Paths.get("notices");

    @Override
    public List<Map<String,Object>> getCertificateInfo(Map<String,Object> param) throws NotSupportedException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Map<String,Object> map= userNotarzationMasterDao.getCertificateInfo(param.get("id").toString());
        NotarzationMasterEntity notarzationMasterEntity =  userNotarzationMasterDao.selectById(param.get("id").toString());
        Map<String, Object> mapOrder = userOrderDao.getOrderInfo(notarzationMasterEntity.getOrderId());
        map.putAll(mapOrder);

        List<Map<String,Object>> maplist = userNotarzationMasterService.editFrom(map);
        return maplist;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editCertificateInfo(Map<String, Object> param) {
        //更新基础表和订单表
        userNotarzationMasterService.editMasterAndOrder(param);
    }

    @Override
    public void generatePdf(String id) throws Exception {
        //公证的信息
        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(id);
        userNotarzationMasterService.pdfGenerate(notarzationMasterEntity);
    }

    @Override
    public void generatePreparePaper(NotarzationMasterEntity notarzationMasterEntity) throws Exception {
        ConcurrentHashMap<String, Map<String, Object>> paramsMap = new ConcurrentHashMap<>();
        PdfVarEntity pdfVar = null;

        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", notarzationMasterEntity.getUserId());

        StringBuffer sbf = new StringBuffer();
        sbf.append(notarzationMasterEntity.getRealName() + "，");
        if (notarzationMasterEntity.getGender() != null) {
            sbf.append("1".equals(notarzationMasterEntity.getGender()) ? "男，" : "女，");
        }
        sbf.append(DateUtil.format(notarzationMasterEntity.getBirth(), "yyyy年MM月dd日") + "出生，");
        sbf.append(notarzationMasterEntity.getIdCardType()+"号码："+notarzationMasterEntity.getIdCardNo()+"。");

        // 拟稿纸模板的路径
        String sourceFile = this.templatesRoot + "/公证书_税收证明.docx";
        // 生成拟稿纸的路径
        String docName ="PDF_NOTARIZATION_"+ notarzationMasterEntity.getProcessNo() + ".docx";
        String destinationDocFile = this.noticesRoot + "/" + docName;

        pdfVar = new PdfVarEntity("text", DateUtil.format(new Date(),"yyyy年MM月dd日"), null);
        paramsMap.put("today", pdfVar.getMap());
        // 古世杰，男，1982年3月8日出生，台湾居民来往大陆通行证号码：06069484。
        pdfVar = new PdfVarEntity("text", sbf.toString(), null);
        paramsMap.put("personInfo", pdfVar.getMap());
        // 古世杰
        pdfVar = new PdfVarEntity("text", notarzationMasterEntity.getRealName(), null);
        paramsMap.put("name", pdfVar.getMap());

        pdfVar = new PdfVarEntity("text", "江苏省昆山市公证处", null);
        if(userNotarzationMasterService.isForeign(notarzationMasterEntity.getUsedToCountry())){
            pdfVar = new PdfVarEntity("text", "中华人民共和国江苏省昆山市公证处", null);
        }
        paramsMap.put("inscribe", pdfVar.getMap());




        generatePdf.generatePdf(paramsMap, sourceFile, destinationDocFile, null, false);
        userDocumentService.deleteFileByCategoryCode(notarzationMasterEntity.getId(), DocumentCategoryCode.PDF_NOTARIZATION.getCode());
        userDocumentService.addFile(destinationDocFile, docName, notarzationMasterEntity.getId(), DocumentCategoryCode.PDF_NOTARIZATION.getCode(),false);

        //生成译文相符
        if(notarzationMasterEntity.getHasMoreCert().equals("1")){
            userNotarzationMasterService.generatePreparePaperCert(notarzationMasterEntity,paramsMap);
        }


        //1月17日修改   注释掉先生成拟稿纸
//        ConcurrentHashMap<String, Map<String, Object>> paramsMap = new ConcurrentHashMap<>();
//        PdfVarEntity pdfVar = null;
//
//        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("id", notarzationMasterEntity.getUserId());
//
//        StringBuffer sbf = new StringBuffer();
//        sbf.append(notarzationMasterEntity.getRealName() + "，");
//        if (notarzationMasterEntity.getGender() != null) {
//            sbf.append("1".equals(notarzationMasterEntity.getGender()) ? "男，" : "女，");
//        }
//        sbf.append(DateUtil.format(notarzationMasterEntity.getBirth(), "yyyy年MM月dd日") + "出生，");
//        sbf.append(notarzationMasterEntity.getIdCardType()+"号码："+notarzationMasterEntity.getIdCardNo()+"。");
//
//        // 拟稿纸模板的路径
//        String sourceFile = this.templatesRoot + "/拟稿纸_税收证明.docx";
//        // 生成拟稿纸的路径
//        String docName ="PDF_DRAFT_"+ notarzationMasterEntity.getProcessNo() + ".docx";
//        String destinationDocFile = this.noticesRoot + "/" + docName;
//
//        pdfVar = new PdfVarEntity("text", DateUtil.format(new Date(),"yyyy年MM月dd日"), null);
//        paramsMap.put("today", pdfVar.getMap());
//        // 古世杰，男，1982年3月8日出生，台湾居民来往大陆通行证号码：06069484。
//        pdfVar = new PdfVarEntity("text", sbf.toString(), null);
//        paramsMap.put("personInfo", pdfVar.getMap());
//        // 古世杰
//        pdfVar = new PdfVarEntity("text", notarzationMasterEntity.getRealName(), null);
//        paramsMap.put("name", pdfVar.getMap());
//        pdfVar = new PdfVarEntity("text", notarzationMasterEntity.getProcessNo(), null);
//        paramsMap.put("processNo", pdfVar.getMap());
//        pdfVar = new PdfVarEntity("text", notarzationMasterEntity.getUsedToCountry().equals("中国台湾")?"涉台":"涉外", null);
//        paramsMap.put("type", pdfVar.getMap());
//        pdfVar = new PdfVarEntity("text", notarzationMasterEntity.getUsedToCountry(), null);
//        paramsMap.put("usedTo", pdfVar.getMap());
//        pdfVar = new PdfVarEntity("text", notarzationMasterEntity.getTranslateTo(), null);
//        paramsMap.put("translateTo", pdfVar.getMap());
//
//
//        generatePdf.generatePdf(paramsMap, sourceFile, destinationDocFile, null, false);
//        userDocumentService.deleteFileByCategoryCode(notarzationMasterEntity.getId(), DocumentCategoryCode.PDF_DRAFT.getCode());
//        userDocumentService.addFile(destinationDocFile, docName, notarzationMasterEntity.getId(), DocumentCategoryCode.PDF_DRAFT.getCode());
//
//        //生成译文相符
//        if(notarzationMasterEntity.getHasMoreCert().equals("1")){
//            userNotarzationMasterService.generatePreparePaperCert(notarzationMasterEntity,paramsMap);
//        }
    }

}
