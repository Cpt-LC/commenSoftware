package com.lianzheng.notarization.sellhouse.generate;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lianzheng.core.auth.mgmt.service.SysUserService;
import com.lianzheng.core.auth.mgmt.utils.DateUtils;
import com.lianzheng.core.log.SpringContextUtil;
import com.lianzheng.core.pdf.PdfVarEntity;
import com.lianzheng.core.pdf.service.GeneratePdf;
import com.lianzheng.notarization.master.entity.NotarizationMattersSpecialEntity;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.enums.DocumentCategoryCode;
import com.lianzheng.notarization.master.enums.SexEnum;
import com.lianzheng.notarization.master.generate.IGenerate;
import com.lianzheng.notarization.master.service.NotarizationMattersSpecialService;
import com.lianzheng.notarization.master.service.UserDocumentService;
import com.lianzheng.notarization.master.service.UserNotarzationMasterService;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PdfAttorneySellHouseGenerate implements IGenerate {
    private static final UserNotarzationMasterService IuserNotarzationMasterService= SpringContextUtil.getBean(UserNotarzationMasterService.class);

    private static final GeneratePdf IgeneratePdf = SpringContextUtil.getBean(GeneratePdf.class);

    private static final UserDocumentService IuserDocumentService =SpringContextUtil.getBean(UserDocumentService.class);


    private static final NotarizationMattersSpecialService InotarizationMattersSpecialService = SpringContextUtil.getBean(NotarizationMattersSpecialService.class);

    protected NotarzationMasterEntity notarzationMasterEntity;

    protected ConcurrentHashMap<String, Map<String, Object>> hashMap;

    //初始化参数
    public PdfAttorneySellHouseGenerate(NotarzationMasterEntity notarzationMasterEntity){
        this.notarzationMasterEntity=notarzationMasterEntity;
        this.hashMap = IuserNotarzationMasterService.getCommenHashMap(notarzationMasterEntity);
        getTemplateParam();
    }

    @Override
    public void getTemplateParam() {
        //获取参数
        List<NotarizationMattersSpecialEntity> notarizationMattersSpecialEntityList = InotarizationMattersSpecialService.list(
                new QueryWrapper<NotarizationMattersSpecialEntity>().eq("notarizationId",notarzationMasterEntity.getId())
        );
        Map<String,NotarizationMattersSpecialEntity> map = notarizationMattersSpecialEntityList.stream().collect(Collectors.toMap(NotarizationMattersSpecialEntity::getEntryKey, notarizationMattersSpecialEntity -> notarizationMattersSpecialEntity));

        PdfVarEntity pdfVarEntity = null;
        pdfVarEntity = new PdfVarEntity("text",map.get("trusteeName").getEntryValue(), null);
        hashMap.put("name", pdfVarEntity.getMap());
        String userInfo = notarzationMasterEntity.getRealName() +"，"+ SexEnum.getEnumMsg(notarzationMasterEntity.getGender()) +"，"+
                DateUtils.format(notarzationMasterEntity.getBirth(),"yyyy年MM月dd日出生，") +  "公民身份号码：" + notarzationMasterEntity.getIdCardNo() + "。";
        pdfVarEntity = new PdfVarEntity("text", userInfo, null);
        hashMap.put("userInfo",pdfVarEntity.getMap());

        String name = map.get("trusteeName").getEntryValue();
        String gender = SexEnum.getEnumMsg(map.get("trusteeGender").getEntryValue());
        Date birth = DateUtils.stringToDate(map.get("trusteeBirthday").getEntryValue(),DateUtils.DATE_PATTERN);
        String birthString = DateUtils.format(birth,"yyyy年MM月dd日出生，");
        String idNo = map.get("trusteeIdNum").getEntryValue();
        String trusteeInfo = name +"，"+ gender  +"，"+  birthString +  "公民身份号码：" + idNo + "。";
        pdfVarEntity = new PdfVarEntity("text", trusteeInfo, null);
        hashMap.put("trusteeInfo",pdfVarEntity.getMap());

        pdfVarEntity = new PdfVarEntity("text", map.get("roomAddress").getEntryValue(), null);
        hashMap.put("roomAddress",pdfVarEntity.getMap());

        String ownershipSCertificate = null;
        if(map.get("ownershipSCertificate")==null || StringUtils.isEmpty(map.get("ownershipSCertificate").getEntryValue())){ //若不存在插入空格
            ownershipSCertificate = "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
        }else {
            ownershipSCertificate = map.get("ownershipSCertificate").getEntryValue();
        }
        pdfVarEntity = new PdfVarEntity("text", ownershipSCertificate, null);
        hashMap.put("ownershipSCertificate",pdfVarEntity.getMap());
    }

    @Override
    public void Generatefile(String sourceFile) throws Exception {
        String destinationDocFile= "notices/PDF_HOME_ATTORNEY_" + notarzationMasterEntity.getProcessNo() + ".docx";
        String destinationPdfFile= "notices/PDF_HOME_ATTORNEY_" + notarzationMasterEntity.getProcessNo() + ".pdf";
        String pdfFileName="PDF_HOME_ATTORNEY_" + notarzationMasterEntity.getProcessNo() + ".pdf";

        //生成文件  房产委托书线下签名无需生成word 后在生成pdf
        String id =notarzationMasterEntity.getId();
        IgeneratePdf.generatePdf(hashMap, sourceFile, destinationDocFile, destinationPdfFile, true);
        IuserDocumentService.deleteFileByCategoryCode(id, DocumentCategoryCode.PDF_HOME_ATTORNEY.getCode());
        IuserDocumentService.addFile(destinationPdfFile, pdfFileName, id, DocumentCategoryCode.PDF_HOME_ATTORNEY.getCode(),true);
    }

    @Override
    public void GeneratefileFinal() throws Exception {
        String id =notarzationMasterEntity.getId();
        String destinationDocFile= IuserDocumentService.getDocx(id,DocumentCategoryCode.PDF_HOME_ATTORNEY.getCode());
        String destinationPdfFile= "notices/PDF_HOME_ATTORNEY_" + notarzationMasterEntity.getProcessNo() + ".pdf";
        String pdfFileName="PDF_HOME_ATTORNEY_" + notarzationMasterEntity.getProcessNo() + ".pdf";


        //生成文件
        IgeneratePdf.generatePdf(hashMap, destinationDocFile, destinationPdfFile,"", false);
        IuserDocumentService.deleteFileByCategoryCode(id, DocumentCategoryCode.PDF_HOME_ATTORNEY.getCode());
        IuserDocumentService.addFile(destinationPdfFile, pdfFileName, id, DocumentCategoryCode.PDF_HOME_ATTORNEY.getCode(),false);
    }
}
