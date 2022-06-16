package com.lianzheng.notarization.master.generate;

import com.lianzheng.core.auth.mgmt.entity.SysUserEntity;
import com.lianzheng.core.auth.mgmt.service.SysUserService;
import com.lianzheng.core.auth.mgmt.utils.DateUtils;
import com.lianzheng.notarization.master.common.CommonConstant;
import com.lianzheng.core.log.SpringContextUtil;
import com.lianzheng.core.pdf.PdfVarEntity;
import com.lianzheng.core.pdf.service.GeneratePdf;
import com.lianzheng.notarization.master.configParameter.utils.GetNotarizationTypeUtil;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.enums.DocumentCategoryCode;
import com.lianzheng.notarization.master.service.UserDocumentService;
import com.lianzheng.notarization.master.service.UserNotarzationMasterService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 受理单通知单回执生成类
 */
public class PdfAcNoticeGenerate implements IGenerate{
    private static final UserNotarzationMasterService IuserNotarzationMasterService= SpringContextUtil.getBean(UserNotarzationMasterService.class);

    private static final GeneratePdf IgeneratePdf = SpringContextUtil.getBean(GeneratePdf.class);

    private static final UserDocumentService IuserDocumentService =SpringContextUtil.getBean(UserDocumentService.class);

    private static final SysUserService IsysUserService = SpringContextUtil.getBean(SysUserService.class);

    protected NotarzationMasterEntity notarzationMasterEntity;

    protected ConcurrentHashMap<String, Map<String, Object>> hashMap;


    //初始化参数
    public PdfAcNoticeGenerate(NotarzationMasterEntity notarzationMasterEntity){
        this.notarzationMasterEntity=notarzationMasterEntity;
        this.hashMap = IuserNotarzationMasterService.getCommenHashMap(notarzationMasterEntity);
        getTemplateParam();
    }

    @Override
    public void getTemplateParam(){
        PdfVarEntity pdfVarEntity = null;
        List<String> url = null;
        SysUserEntity sysUserEntity = IsysUserService.queryByUserId(notarzationMasterEntity.getActionBy());

        String notarzationTypeCode =notarzationMasterEntity.getNotarzationTypeCode();

        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getProcessNo(), null);
        hashMap.put("processNo", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getRealName(), null);
        hashMap.put("realName", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getUserRemark(), null);
        hashMap.put("userRemark", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", GetNotarizationTypeUtil.getNotarizationType(notarzationTypeCode), null);
        hashMap.put("notarzationTypeCode", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", sysUserEntity.getRealName(), null);
        hashMap.put("actionName", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", DateUtils.format(notarzationMasterEntity.getCreatedTime()), null);
        hashMap.put("createdTime", pdfVarEntity.getMap());
    }

    @Override
    public  void Generatefile(String sourceFile) throws Exception {
        String destinationDocFile= "notices/PDF_AC_NOTICE_" + notarzationMasterEntity.getProcessNo() + ".docx";
        String destinationPdfFile= "notices/PDF_AC_NOTICE_" + notarzationMasterEntity.getProcessNo() + ".pdf";
        String pdfFileName="PDF_AC_NOTICE_" + notarzationMasterEntity.getProcessNo() + ".pdf";

        //生成文件  先生成word 后生成有签名的pdf
        String id =notarzationMasterEntity.getId();
        IgeneratePdf.generatePdf(hashMap, sourceFile, destinationDocFile, "", false);
        hashMap = IuserNotarzationMasterService.getSignHashMap(notarzationMasterEntity);
        IgeneratePdf.generatePdf(hashMap, destinationDocFile, destinationPdfFile,"", false);
        IuserDocumentService.deleteFileByCategoryCode(id, DocumentCategoryCode.PDF_AC_NOTICE.getCode());
        IuserDocumentService.addFile(destinationPdfFile, pdfFileName, id, DocumentCategoryCode.PDF_AC_NOTICE.getCode(), true);
    }

    @Override
    public  void GeneratefileFinal() throws Exception {
        String id =notarzationMasterEntity.getId();
        String destinationDocFile= IuserDocumentService.getDocx(id,DocumentCategoryCode.PDF_AC_NOTICE.getCode());
        String destinationPdfFile= "notices/PDF_AC_NOTICE_" + notarzationMasterEntity.getProcessNo() + ".pdf";
        String pdfFileName="PDF_AC_NOTICE_" + notarzationMasterEntity.getProcessNo() + ".pdf";


        //生成文件
        hashMap = IuserNotarzationMasterService.getSignHashMap(notarzationMasterEntity);
        IgeneratePdf.generatePdf(hashMap, destinationDocFile, destinationPdfFile,"", false);
        IuserDocumentService.deleteFileByCategoryCode(id, DocumentCategoryCode.PDF_AC_NOTICE.getCode());
        IuserDocumentService.addFile(destinationPdfFile, pdfFileName, id, DocumentCategoryCode.PDF_AC_NOTICE.getCode(),false);
    }
}
