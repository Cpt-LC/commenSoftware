package com.lianzheng.notarization.master.generate;

import com.lianzheng.core.log.SpringContextUtil;
import com.lianzheng.core.pdf.service.GeneratePdf;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.enums.DocumentCategoryCode;
import com.lianzheng.notarization.master.service.UserDocumentService;
import com.lianzheng.notarization.master.service.UserNotarzationMasterService;

/**
 * 声明告知书
 */
public class PdfNoticeDeclarationGenerate extends PdfNoticeGenerate{
    private static final UserNotarzationMasterService IuserNotarzationMasterService= SpringContextUtil.getBean(UserNotarzationMasterService.class);

    private static final GeneratePdf IgeneratePdf = SpringContextUtil.getBean(GeneratePdf.class);

    private static final UserDocumentService IuserDocumentService =SpringContextUtil.getBean(UserDocumentService.class);

    public PdfNoticeDeclarationGenerate(NotarzationMasterEntity notarzationMasterEntity) {
        super(notarzationMasterEntity);
    }

    @Override
    public  void Generatefile(String sourceFile) throws Exception {
        String destinationDocFile= "notices/PDF_NOTICE_DECLARATION_" + notarzationMasterEntity.getProcessNo() + ".docx";
        String destinationPdfFile= "notices/PDF_NOTICE_DECLARATION_" + notarzationMasterEntity.getProcessNo() + ".pdf";
        String pdfFileName="PDF_NOTICE_DECLARATION_" + notarzationMasterEntity.getProcessNo() + ".pdf";

        //生成文件
        String id =notarzationMasterEntity.getId();
        IgeneratePdf.generatePdf(hashMap, sourceFile, destinationDocFile, "", false);
        hashMap = IuserNotarzationMasterService.getSignHashMap(notarzationMasterEntity);
        IgeneratePdf.generatePdf(hashMap, destinationDocFile, destinationPdfFile,"", false);
        IuserDocumentService.deleteFileByCategoryCode(id, DocumentCategoryCode.PDF_NOTICE_DECLARATION.getCode());
        IuserDocumentService.addFile(destinationPdfFile, pdfFileName, id, DocumentCategoryCode.PDF_NOTICE_DECLARATION.getCode(),true);

    }


    @Override
    public  void GeneratefileFinal() throws Exception {
        String id =notarzationMasterEntity.getId();
        String destinationDocFile= IuserDocumentService.getDocx(id,DocumentCategoryCode.PDF_NOTICE_DECLARATION.getCode());
        String destinationPdfFile= "notices/PDF_NOTICE_DECLARATION_" + notarzationMasterEntity.getProcessNo() + ".pdf";
        String pdfFileName="PDF_NOTICE_DECLARATION_" + notarzationMasterEntity.getProcessNo() + ".pdf";

        //生成文件
        hashMap = IuserNotarzationMasterService.getSignHashMap(notarzationMasterEntity);
        IgeneratePdf.generatePdf(hashMap, destinationDocFile, destinationPdfFile, "", false);
        IuserDocumentService.deleteFileByCategoryCode(id, DocumentCategoryCode.PDF_NOTICE_DECLARATION.getCode());
        IuserDocumentService.addFile(destinationPdfFile, pdfFileName, id, DocumentCategoryCode.PDF_NOTICE_DECLARATION.getCode(),false);
    }
}
