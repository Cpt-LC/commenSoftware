package com.lianzheng.notarization.master.generate;

import com.lianzheng.core.auth.mgmt.service.SysUserService;
import com.lianzheng.notarization.master.configParameter.param.DocumentParam;
import com.lianzheng.notarization.master.configParameter.utils.ConfigParameterUtil;
import com.lianzheng.core.log.SpringContextUtil;
import com.lianzheng.core.pdf.PdfVarEntity;
import com.lianzheng.core.pdf.service.GeneratePdf;
import com.lianzheng.notarization.master.dao.UserOrderDao;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.enums.DocumentCategoryCode;
import com.lianzheng.notarization.master.enums.NotarizationTypeEnum;
import com.lianzheng.notarization.master.enums.NotarizationTypeOneEnum;
import com.lianzheng.notarization.master.service.UserDocumentService;
import com.lianzheng.notarization.master.service.UserNotarzationMasterService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 询问笔录生成类
 */
public class PdfQuestionGenerate implements IGenerate{
    private static final UserNotarzationMasterService IuserNotarzationMasterService= SpringContextUtil.getBean(UserNotarzationMasterService.class);

    private static final GeneratePdf IgeneratePdf = SpringContextUtil.getBean(GeneratePdf.class);

    private static final UserDocumentService IuserDocumentService =SpringContextUtil.getBean(UserDocumentService.class);

    private static final SysUserService IsysUserService = SpringContextUtil.getBean(SysUserService.class);

    private static final UserOrderDao IuserOrderDao = SpringContextUtil.getBean(UserOrderDao.class);


    protected NotarzationMasterEntity notarzationMasterEntity;

    protected ConcurrentHashMap<String, Map<String, Object>> hashMap;


    //初始化参数
    public PdfQuestionGenerate(NotarzationMasterEntity notarzationMasterEntity){
        this.notarzationMasterEntity=notarzationMasterEntity;
        this.hashMap = IuserNotarzationMasterService.getCommenHashMap(notarzationMasterEntity);
        getTemplateParam();
    }

    @Override
    public void getTemplateParam(){
        hashMap.putAll(IuserNotarzationMasterService.getPublicParam(notarzationMasterEntity));
        PdfVarEntity pdfVarEntity =null;
        DocumentParam documentParam = ConfigParameterUtil.getDocument();
        String environment = documentParam.getEnvironment();//获取项目环境
        String notarzationTypeCode =notarzationMasterEntity.getNotarzationTypeCode();
        pdfVarEntity = new PdfVarEntity("text", environment.equals("zero")?NotarizationTypeEnum.getEnumMsg(notarzationTypeCode): NotarizationTypeOneEnum.getEnumMsg(notarzationTypeCode), null);
        hashMap.put("notarzationTypeCode", pdfVarEntity.getMap());//todo 该字段不同的项目枚举值不同 提取成公共方法
    }

    @Override
    public void Generatefile(String sourceFile) throws Exception {
        String destinationDocFile= "notices/PDF_QUESTION_" + notarzationMasterEntity.getProcessNo() + ".docx";
        String destinationPdfFile= "notices/PDF_QUESTION_" + notarzationMasterEntity.getProcessNo() + ".pdf";
        String pdfFileName="PDF_QUESTION_" + notarzationMasterEntity.getProcessNo() + ".pdf";

        //生成文件
        String id = notarzationMasterEntity.getId();
        IgeneratePdf.generatePdf(hashMap, sourceFile, destinationDocFile, "", false);
        hashMap = IuserNotarzationMasterService.getSignHashMap(notarzationMasterEntity);
        IgeneratePdf.generatePdf(hashMap, destinationDocFile, destinationPdfFile,"", false);
        IuserDocumentService.deleteFileByCategoryCode(id, DocumentCategoryCode.PDF_QUESTION.getCode());
        IuserDocumentService.addFile(destinationPdfFile, pdfFileName, id, DocumentCategoryCode.PDF_QUESTION.getCode(),true);
    }

    @Override
    public  void GeneratefileFinal() throws Exception {
        String id = notarzationMasterEntity.getId();
        String destinationDocFile= IuserDocumentService.getDocx(id,DocumentCategoryCode.PDF_QUESTION.getCode());
        String destinationPdfFile= "notices/PDF_QUESTION_" + notarzationMasterEntity.getProcessNo() + ".pdf";
        String pdfFileName="PDF_QUESTION_" + notarzationMasterEntity.getProcessNo() + ".pdf";

        //生成文件
        hashMap = IuserNotarzationMasterService.getSignHashMap(notarzationMasterEntity);
        IgeneratePdf.generatePdf(hashMap, destinationDocFile, destinationPdfFile, "", false);
        IuserDocumentService.deleteFileByCategoryCode(id, DocumentCategoryCode.PDF_QUESTION.getCode());
        IuserDocumentService.addFile(destinationPdfFile, pdfFileName, id, DocumentCategoryCode.PDF_QUESTION.getCode(),false);
    }
}
