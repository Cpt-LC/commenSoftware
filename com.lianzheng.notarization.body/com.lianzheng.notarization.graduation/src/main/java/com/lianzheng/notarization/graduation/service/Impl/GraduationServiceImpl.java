package com.lianzheng.notarization.graduation.service.Impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lianzheng.core.auth.mgmt.config.ShiroUtils;
import com.lianzheng.core.auth.mgmt.entity.SysUserEntity;
import com.lianzheng.core.auth.mgmt.service.SysUserService;
import com.lianzheng.core.auth.mgmt.utils.DateUtils;
import com.lianzheng.core.auth.mgmt.utils.FileTokenUtils;
import com.lianzheng.core.pdf.PdfVarEntity;
import com.lianzheng.core.pdf.service.GeneratePdf;
import com.lianzheng.notarization.graduation.dao.UserNotarzationGraduationDao;
import com.lianzheng.notarization.graduation.entity.NotarzationGraduationEntity;
import com.lianzheng.notarization.master.dao.UserDocumentDao;
import com.lianzheng.notarization.master.dao.UserNotarzationMasterDao;
import com.lianzheng.notarization.master.dao.UserOrderDao;
import com.lianzheng.notarization.master.entity.DocumentEntity;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.entity.OrderEntity;
import com.lianzheng.notarization.master.entity.UserEntity;
import com.lianzheng.notarization.master.enums.*;
import com.lianzheng.notarization.master.form.MapCommonForm;
import com.lianzheng.notarization.master.form.MapListForm;
import com.lianzheng.notarization.master.service.MasterService;

import com.lianzheng.notarization.master.service.UserDocumentService;
import com.lianzheng.notarization.master.service.UserNotarzationMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.transaction.NotSupportedException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service("GR")
@DS("h5")
public class GraduationServiceImpl implements MasterService {
    @Autowired
    private UserNotarzationGraduationDao userNotarzationGraduationDao;
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
    private UserOrderDao userOrderDao;
    @Autowired
    private UserDocumentService userDocumentService;
    @Autowired
    private FileTokenUtils fileTokenUtils;

    Path root = Paths.get("uploads");
    Path templatesRoot = Paths.get("templates");
    Path noticesRoot = Paths.get("notices");



    @Override
    public List<Map<String,Object>> getCertificateInfo(Map<String, Object> param) throws NotSupportedException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Map<String, Object> map  =  userNotarzationGraduationDao.getCertificateInfo(param.get("id").toString());
        NotarzationMasterEntity notarzationMasterEntity =  userNotarzationMasterDao.selectById(param.get("id").toString());
        Map<String, Object> mapOrder = userOrderDao.getOrderInfo(notarzationMasterEntity.getOrderId());
        map.putAll(mapOrder);
        List<Map<String,Object>> maplist = userNotarzationMasterService.editFrom(map);

        for (Map<String, Object> group :
                maplist) {
            if(!group.get("group").toString().equals("????????????")){
                continue;
            }

            for (Map<String, Object> row :
                    (List<Map<String, Object>>) (group.get("rows"))) {

                for (MapCommonForm col :
                        (List<MapCommonForm>) (row.get("columns"))) {

                    //?????????????????????  ??????
                    if(!col.getTableName().equals("notarzation_graduation")){
                        continue;
                    }

                    String key = col.getKey();
                    switch (key){
                        case "graduatedDate":
                        case "graduatedFrom":
                            col.setVisible(true);
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        return maplist;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editCertificateInfo(Map<String, Object> param){
        //????????????????????????
        userNotarzationMasterService.editMasterAndOrder(param);
        //????????????
        NotarzationGraduationEntity notarzationGraduationEntity =  userNotarzationGraduationDao.selectOne(
                new QueryWrapper<NotarzationGraduationEntity>().eq("id",param.get("id").toString()).eq("isDeleted",0)
        );
        //??????????????? ????????????
        if(notarzationGraduationEntity == null){
            notarzationGraduationEntity = new NotarzationGraduationEntity();
            notarzationGraduationEntity.setId(param.get("id").toString());
            notarzationGraduationEntity.update(param);
            notarzationGraduationEntity.setUpdatedTime(new Date());
            notarzationGraduationEntity.setCreatedTime(new Date());
            notarzationGraduationEntity.setUpdatedBy(ShiroUtils.getUserId().toString());
            notarzationGraduationEntity.setCreatedBy(ShiroUtils.getUserId().toString());
            userNotarzationGraduationDao.insert(notarzationGraduationEntity);
            return;
        }
        notarzationGraduationEntity.update(param);
        notarzationGraduationEntity.setUpdatedTime(new Date());
        notarzationGraduationEntity.setUpdatedBy(ShiroUtils.getUserId().toString());
        userNotarzationGraduationDao.updateById(notarzationGraduationEntity);
    }

    @Override
    public void generatePdf(String id) throws Exception {
        //???????????????
        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(id);
        userNotarzationMasterService.pdfGenerate(notarzationMasterEntity);
    }


    @Override
    public void generatePreparePaper(NotarzationMasterEntity notarzationMasterEntity) throws Exception {
        NotarzationGraduationEntity notarzationGraduationEntity = userNotarzationGraduationDao.selectById(notarzationMasterEntity.getId());
        ConcurrentHashMap<String, Map<String, Object>> paramsMap = new ConcurrentHashMap<>();
        PdfVarEntity pdfVar = null;

        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", notarzationMasterEntity.getUserId());

        StringBuffer sbf = new StringBuffer();
        sbf.append(notarzationMasterEntity.getRealName() + "???");
        if (notarzationMasterEntity.getGender() != null) {
            sbf.append("1".equals(notarzationMasterEntity.getGender()) ? "??????" : "??????");
        }
        sbf.append(DateUtil.format(notarzationMasterEntity.getBirth(), "yyyy???MM???dd???") + "?????????");
        sbf.append("????????????????????????"+notarzationMasterEntity.getIdCardNo()+"?????????????????????"+notarzationMasterEntity.getIdCardAddress()+"???");

        // ????????????????????????
        String sourceFile = this.templatesRoot + "/?????????_????????????.docx";
        // ????????????????????????
        String docName ="PDF_NOTARIZATION_"+ notarzationMasterEntity.getProcessNo() + ".docx";
        String destinationDocFile = this.noticesRoot + "/" + docName;

        pdfVar = new PdfVarEntity("text", DateUtil.format(new Date(),"yyyy???MM???dd???"), null);
        paramsMap.put("today", pdfVar.getMap());
        // ??????????????????1982???3???8??????????????????????????????????????????????????????06069484???
        pdfVar = new PdfVarEntity("text", sbf.toString(), null);
        paramsMap.put("personInfo", pdfVar.getMap());
        // ?????????
        pdfVar = new PdfVarEntity("text", notarzationMasterEntity.getRealName(), null);
        paramsMap.put("name", pdfVar.getMap());
        pdfVar = new PdfVarEntity("text", notarzationGraduationEntity.getGraduatedFrom(), null);
        paramsMap.put("graduatedFrom", pdfVar.getMap());
        pdfVar = new PdfVarEntity("text",DateUtils.format(notarzationGraduationEntity.getGraduatedDate(),"yyyy???MM???dd???") , null);
        paramsMap.put("graduatedDate", pdfVar.getMap());
        pdfVar = new PdfVarEntity("text", "???????????????????????????", null);
        if(userNotarzationMasterService.isForeign(notarzationMasterEntity.getUsedToCountry())){
            pdfVar = new PdfVarEntity("text", "????????????????????????????????????????????????", null);
        }
        paramsMap.put("inscribe", pdfVar.getMap());


        generatePdf.generatePdf(paramsMap, sourceFile, destinationDocFile, null, false);
        userDocumentService.deleteFileByCategoryCode(notarzationMasterEntity.getId(), DocumentCategoryCode.PDF_NOTARIZATION.getCode());
        userDocumentService.addFile(destinationDocFile, docName, notarzationMasterEntity.getId(), DocumentCategoryCode.PDF_NOTARIZATION.getCode(),false);

        //??????????????????
        if(notarzationMasterEntity.getHasMoreCert().equals("1")){
            userNotarzationMasterService.generatePreparePaperCert(notarzationMasterEntity,paramsMap);
        }

    }
}
