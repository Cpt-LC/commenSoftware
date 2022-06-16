package com.lianzheng.notarization.master.generate;

import com.lianzheng.core.auth.mgmt.dao.SysNotarialOfficeDao;
import com.lianzheng.core.auth.mgmt.entity.SysNotarialOfficeEntity;
import com.lianzheng.core.auth.mgmt.entity.SysUserEntity;
import com.lianzheng.core.auth.mgmt.service.SysNotarialOfficeService;
import com.lianzheng.core.auth.mgmt.service.SysUserService;
import com.lianzheng.core.auth.mgmt.utils.DateUtils;
import com.lianzheng.notarization.master.configParameter.param.DocumentParam;
import com.lianzheng.notarization.master.configParameter.utils.ConfigParameterUtil;
import com.lianzheng.core.log.SpringContextUtil;
import com.lianzheng.core.pdf.PdfVarEntity;
import com.lianzheng.core.pdf.service.GeneratePdf;
import com.lianzheng.notarization.master.configParameter.utils.GetNotarizationTypeUtil;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.entity.UserEntity;
import com.lianzheng.notarization.master.enums.*;
import com.lianzheng.notarization.master.service.UserDocumentService;
import com.lianzheng.notarization.master.service.UserNotarzationMasterService;
import com.lianzheng.notarization.master.service.UserService;
import com.lianzheng.notarization.master.utils.FileStorageByNotaryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 申请表生成类
 */
public class PdfApplicationGenerate implements IGenerate{
    private static final UserNotarzationMasterService IuserNotarzationMasterService= SpringContextUtil.getBean(UserNotarzationMasterService.class);

    private static final GeneratePdf IgeneratePdf = SpringContextUtil.getBean(GeneratePdf.class);

    private static final UserDocumentService IuserDocumentService =SpringContextUtil.getBean(UserDocumentService.class);

    private static final SysUserService IsysUserService = SpringContextUtil.getBean(SysUserService.class);

    private static final SysNotarialOfficeDao IsysNotarialOfficeDao = SpringContextUtil.getBean(SysNotarialOfficeDao.class);

    private static final UserService IUserService = SpringContextUtil.getBean(UserService.class);

    protected NotarzationMasterEntity notarzationMasterEntity;

    protected ConcurrentHashMap<String, Map<String, Object>> hashMap;


    //初始化参数
    public PdfApplicationGenerate(NotarzationMasterEntity notarzationMasterEntity){
        this.notarzationMasterEntity=notarzationMasterEntity;
        this.hashMap = IuserNotarzationMasterService.getCommenHashMap(notarzationMasterEntity);
        getTemplateParam();
    }

    @Override
    public void getTemplateParam(){
        PdfVarEntity pdfVarEntity = null;
        List<String> url = null;
        SysUserEntity sysUserEntity = IsysUserService.queryByUserId(notarzationMasterEntity.getActionBy());
        SysNotarialOfficeEntity sysNotarialOfficeEntity = IsysNotarialOfficeDao.findOneByUserId(sysUserEntity.getId());
        String notarzationTypeCode =notarzationMasterEntity.getNotarzationTypeCode();

        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getProcessNo(), null);
        hashMap.put("processNo", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getRealName(), null);
        hashMap.put("realName", pdfVarEntity.getMap());
        String gender = notarzationMasterEntity.getGender().equals("1") ? "男" : "女";
        pdfVarEntity = new PdfVarEntity("text", gender, null);
        hashMap.put("gender", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", DateUtils.format(notarzationMasterEntity.getBirth()), null);
        hashMap.put("birth", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getIdCardAddress(), null);
        hashMap.put("address", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getPhone(), null);
        hashMap.put("phone", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getUsedFor(), null);
        hashMap.put("usedFor", pdfVarEntity.getMap());
        if(notarzationMasterEntity.getApplicantParty().equals(ApplicationPartyEnum.E.getCode())){
            pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getSocialCreditCode(), null);
            hashMap.put("socailId",pdfVarEntity.getMap());
        }

        //代理填写代理信息
        if(notarzationMasterEntity.getIsAgent()==1){
            UserEntity userEntity = IUserService.getById(notarzationMasterEntity.getUserId());
            pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getRealName(), null);
            hashMap.put("realNameAgent", pdfVarEntity.getMap());
            String genderAgent = userEntity.getGender()==1 ? "男" : "女";
            pdfVarEntity = new PdfVarEntity("text", genderAgent, null);
            hashMap.put("genderAgent", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", userEntity.getIdCardAddress(), null);
            hashMap.put("addressAgent", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", userEntity.getPhone(), null);
            hashMap.put("phoneAgent", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", userEntity.getRealName(), null);
            hashMap.put("agentName", pdfVarEntity.getMap());
        }else {
            pdfVarEntity = new PdfVarEntity("text", "", null);
            hashMap.put("realNameAgent", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", "", null);
            hashMap.put("genderAgent", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", "", null);
            hashMap.put("addressAgent", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", "", null);
            hashMap.put("phoneAgent", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", "", null);
            hashMap.put("agentName", pdfVarEntity.getMap());
        }

        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getCopyNumber(), null);
        hashMap.put("copyNumber", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getUsedToCountry(), null);
        hashMap.put("usedToCountry", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getUsedToProvince(), null);
        hashMap.put("usedToProvince", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getTranslateTo(), null);
        hashMap.put("translateTo", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getUserRemark(), null);
        hashMap.put("userRemark", pdfVarEntity.getMap());
        url = new ArrayList<String>();
        url.add(IuserDocumentService.getUrl(notarzationMasterEntity.getNotarialOfficeId(),sysUserEntity.getSignUrl()));
        pdfVarEntity = new PdfVarEntity("img", url, null);
        hashMap.put("actionName", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", GetNotarizationTypeUtil.getNotarizationType(notarzationTypeCode), null);
        hashMap.put("notarzationTypeCode", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text",GetNotarizationTypeUtil.getMaterialMsg(notarzationTypeCode) , null);
        hashMap.put("notarzationTypeCodeMsg", pdfVarEntity.getMap());
    }

    @Override
    public  void Generatefile(String sourceFile) throws Exception {
        String destinationDocFile= "notices/PDF_APPLICATION_" + notarzationMasterEntity.getProcessNo() + ".docx";
        String destinationPdfFile= "notices/PDF_APPLICATION_" + notarzationMasterEntity.getProcessNo() + ".pdf";
        String pdfFileName="PDF_APPLICATION_" + notarzationMasterEntity.getProcessNo() + ".pdf";

        if(notarzationMasterEntity.getApplicantParty().equals(ApplicationPartyEnum.E.getCode())){
            sourceFile = sourceFile.replace(".","企业.");
        }
        //生成文件
        String id =notarzationMasterEntity.getId();
        IgeneratePdf.generatePdf(hashMap, sourceFile, destinationDocFile, "", false);
        hashMap = IuserNotarzationMasterService.getSignHashMap(notarzationMasterEntity);
        IgeneratePdf.generatePdf(hashMap, destinationDocFile, destinationPdfFile,"", false);
        IuserDocumentService.deleteFileByCategoryCode(id, DocumentCategoryCode.PDF_APPLICATION.getCode());
        IuserDocumentService.addFile(destinationPdfFile, pdfFileName, id, DocumentCategoryCode.PDF_APPLICATION.getCode(),true);
    }

    @Override
    public  void GeneratefileFinal() throws Exception {
        String id =notarzationMasterEntity.getId();
        String destinationDocFile= IuserDocumentService.getDocx(id,DocumentCategoryCode.PDF_APPLICATION.getCode());
        String destinationPdfFile= "notices/PDF_APPLICATION_" + notarzationMasterEntity.getProcessNo() + ".pdf";
        String pdfFileName="PDF_APPLICATION_" + notarzationMasterEntity.getProcessNo() + ".pdf";

        //生成文件
        hashMap = IuserNotarzationMasterService.getSignHashMap(notarzationMasterEntity);
        IgeneratePdf.generatePdf(hashMap, destinationDocFile, destinationPdfFile, "", false);
        IuserDocumentService.deleteFileByCategoryCode(id, DocumentCategoryCode.PDF_APPLICATION.getCode());
        IuserDocumentService.addFile(destinationPdfFile, pdfFileName, id, DocumentCategoryCode.PDF_APPLICATION.getCode(),false);
    }
}
