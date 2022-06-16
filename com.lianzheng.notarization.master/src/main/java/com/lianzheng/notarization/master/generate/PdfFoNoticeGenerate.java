package com.lianzheng.notarization.master.generate;

import com.lianzheng.core.log.SpringContextUtil;
import com.lianzheng.core.pdf.service.GeneratePdf;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.enums.DocumentCategoryCode;
import com.lianzheng.notarization.master.service.UserDocumentService;
import com.lianzheng.notarization.master.service.UserNotarzationMasterService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 涉外，涉港澳台告知书生成类
 */

public class PdfFoNoticeGenerate implements IGenerate{
    private static final UserNotarzationMasterService IuserNotarzationMasterService= SpringContextUtil.getBean(UserNotarzationMasterService.class);

    private static final GeneratePdf IgeneratePdf = SpringContextUtil.getBean(GeneratePdf.class);

    private static final UserDocumentService IuserDocumentService =SpringContextUtil.getBean(UserDocumentService.class);

    protected NotarzationMasterEntity notarzationMasterEntity;

    protected ConcurrentHashMap<String, Map<String, Object>> hashMap;


    //初始化参数
    public PdfFoNoticeGenerate(NotarzationMasterEntity notarzationMasterEntity){
        this.notarzationMasterEntity=notarzationMasterEntity;
        this.hashMap = IuserNotarzationMasterService.getCommenHashMap(notarzationMasterEntity);
        getTemplateParam();
    }

    @Override
    public void getTemplateParam(){
    }

    @Override
    public  void Generatefile(String sourceFile) throws Exception {
        String destinationDocFile= "notices/PDF_FO_NOTICE_" + notarzationMasterEntity.getProcessNo() + ".docx";
        String destinationPdfFile= "notices/PDF_FO_NOTICE_" + notarzationMasterEntity.getProcessNo() + ".pdf";
        String pdfFileName="PDF_FO_NOTICE_" + notarzationMasterEntity.getProcessNo() + ".pdf";

        String id =notarzationMasterEntity.getId();
        //防止使用地从中国修改后无法删除原有告知书
        IuserDocumentService.deleteFileByCategoryCode(id, DocumentCategoryCode.PDF_FO_NOTICE.getCode());
        if(!notarzationMasterEntity.getUsedToCountry().equals("中国")){
            //生成文件
            IgeneratePdf.generatePdf(hashMap, sourceFile, destinationDocFile, "", false);
            hashMap = IuserNotarzationMasterService.getSignHashMap(notarzationMasterEntity);
            IgeneratePdf.generatePdf(hashMap, destinationDocFile, destinationPdfFile,"", false);
            IuserDocumentService.addFile(destinationPdfFile, pdfFileName, id, DocumentCategoryCode.PDF_FO_NOTICE.getCode(),true);
        }
    }

    @Override
    public  void GeneratefileFinal() throws Exception {
        String id =notarzationMasterEntity.getId();
        String destinationDocFile= IuserDocumentService.getDocx(id,DocumentCategoryCode.PDF_FO_NOTICE.getCode());
        String destinationPdfFile= "notices/PDF_FO_NOTICE_" + notarzationMasterEntity.getProcessNo() + ".pdf";
        String pdfFileName="PDF_FO_NOTICE_" + notarzationMasterEntity.getProcessNo() + ".pdf";

        //生成文件
        IuserDocumentService.deleteFileByCategoryCode(id, DocumentCategoryCode.PDF_FO_NOTICE.getCode());
        if(!notarzationMasterEntity.getUsedToCountry().equals("中国")) {
            hashMap = IuserNotarzationMasterService.getSignHashMap(notarzationMasterEntity);
            IgeneratePdf.generatePdf(hashMap, destinationDocFile, destinationPdfFile, "", false);
            IuserDocumentService.addFile(destinationPdfFile, pdfFileName, id, DocumentCategoryCode.PDF_FO_NOTICE.getCode(),false);
        }
    }
}
