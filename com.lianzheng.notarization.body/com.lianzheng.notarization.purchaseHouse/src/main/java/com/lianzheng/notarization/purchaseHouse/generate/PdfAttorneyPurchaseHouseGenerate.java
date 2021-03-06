package com.lianzheng.notarization.purchaseHouse.generate;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lianzheng.core.auth.mgmt.utils.DateUtils;
import com.lianzheng.core.log.SpringContextUtil;
import com.lianzheng.core.pdf.PdfVarEntity;
import com.lianzheng.core.pdf.service.GeneratePdf;
import com.lianzheng.notarization.master.entity.NotarizationMattersSpecialEntity;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.enums.DocumentCategoryCode;
import com.lianzheng.notarization.master.enums.RoomTypeEnum;
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

public class PdfAttorneyPurchaseHouseGenerate implements IGenerate {

    private static final UserNotarzationMasterService IuserNotarzationMasterService= SpringContextUtil.getBean(UserNotarzationMasterService.class);

    private static final GeneratePdf IgeneratePdf = SpringContextUtil.getBean(GeneratePdf.class);

    private static final UserDocumentService IuserDocumentService =SpringContextUtil.getBean(UserDocumentService.class);


    private static final NotarizationMattersSpecialService InotarizationMattersSpecialService = SpringContextUtil.getBean(NotarizationMattersSpecialService.class);

    protected NotarzationMasterEntity notarzationMasterEntity;

    protected ConcurrentHashMap<String, Map<String, Object>> hashMap;

    public PdfAttorneyPurchaseHouseGenerate(NotarzationMasterEntity notarzationMasterEntity){
        this.notarzationMasterEntity=notarzationMasterEntity;
        this.hashMap = IuserNotarzationMasterService.getCommenHashMap(notarzationMasterEntity);
        getTemplateParam();
    }

    @Override
    public void getTemplateParam() {
        //????????????
        List<NotarizationMattersSpecialEntity> notarizationMattersSpecialEntityList = InotarizationMattersSpecialService.list(
                new QueryWrapper<NotarizationMattersSpecialEntity>().eq("notarizationId",notarzationMasterEntity.getId())
        );
        Map<String,NotarizationMattersSpecialEntity> map = notarizationMattersSpecialEntityList.stream().collect(Collectors.toMap(NotarizationMattersSpecialEntity::getEntryKey, notarizationMattersSpecialEntity -> notarizationMattersSpecialEntity));

        PdfVarEntity pdfVarEntity = null;
        pdfVarEntity = new PdfVarEntity("text", map.get("trusteeName").getEntryValue(), null);
        hashMap.put("name", pdfVarEntity.getMap());
        String userInfo = notarzationMasterEntity.getRealName() +"???"+ SexEnum.getEnumMsg(notarzationMasterEntity.getGender()) +"???"+
                DateUtils.format(notarzationMasterEntity.getBirth(),"yyyy???MM???dd????????????") +  "?????????????????????" + notarzationMasterEntity.getIdCardNo() + "???";
        pdfVarEntity = new PdfVarEntity("text", userInfo, null);
        hashMap.put("userInfo",pdfVarEntity.getMap());
        String name = map.get("trusteeName").getEntryValue();
        String gender = SexEnum.getEnumMsg(map.get("trusteeGender").getEntryValue());
        Date birth = DateUtils.stringToDate(map.get("trusteeBirthday").getEntryValue(),DateUtils.DATE_PATTERN);
        String birthString = DateUtils.format(birth,"yyyy???MM???dd????????????");
        String idNo = map.get("trusteeIdNum").getEntryValue();
        String trusteeInfo = name +"???"+ gender  +"???"+  birthString +  "?????????????????????" + idNo + "???";
        pdfVarEntity = new PdfVarEntity("text", trusteeInfo, null);
        hashMap.put("trusteeInfo",pdfVarEntity.getMap());

        pdfVarEntity = new PdfVarEntity("text", map.get("roomAddress").getEntryValue(), null);
        hashMap.put("roomAddress",pdfVarEntity.getMap());

        String ownershipCertificate = null;
        if(map.get("ownershipCertificate")==null || StringUtils.isEmpty(map.get("ownershipCertificate").getEntryValue())){ //???????????????????????????????????????
            ownershipCertificate = "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
        }else {
            ownershipCertificate = map.get("ownershipCertificate").getEntryValue();
        }
        pdfVarEntity = new PdfVarEntity("text", ownershipCertificate, null);
        hashMap.put("ownershipCertificate",pdfVarEntity.getMap());


        String landUseCertificate = null;
        if(map.get("landUseCertificate")==null || StringUtils.isEmpty(map.get("landUseCertificate").getEntryValue())){ //??????????????????????????????????????????
            landUseCertificate = "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
        }else {
            landUseCertificate = map.get("landUseCertificate").getEntryValue();
        }
        pdfVarEntity = new PdfVarEntity("text", landUseCertificate, null);
        hashMap.put("landUseCertificate",pdfVarEntity.getMap());
    }

    @Override
    public void Generatefile(String sourceFile) throws Exception {
        String destinationDocFile= "notices/PDF_HOME_ATTORNEY_" + notarzationMasterEntity.getProcessNo() + ".docx";
        String destinationPdfFile= "notices/PDF_HOME_ATTORNEY_" + notarzationMasterEntity.getProcessNo() + ".pdf";
        String pdfFileName="PDF_HOME_ATTORNEY_" + notarzationMasterEntity.getProcessNo() + ".pdf";


        List<NotarizationMattersSpecialEntity> notarizationMattersSpecialEntityList = InotarizationMattersSpecialService.list(
                new QueryWrapper<NotarizationMattersSpecialEntity>().eq("notarizationId",notarzationMasterEntity.getId())
        );
        Map<String,NotarizationMattersSpecialEntity> map = notarizationMattersSpecialEntityList.stream().collect(Collectors.toMap(NotarizationMattersSpecialEntity::getEntryKey, notarizationMattersSpecialEntity -> notarizationMattersSpecialEntity));
        String roomType = map.get("roomType").getEntryValue();
        //?????????????????????????????????
        if((roomType.equals(RoomTypeEnum.NEW.getCode())&&sourceFile.contains("?????????"))
                ||(roomType.equals(RoomTypeEnum.OLD.getCode())&&sourceFile.contains("??????"))){
            return;
        }

        //????????????  ???????????????????????????????????????word ????????????pdf
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

        //????????????

        IgeneratePdf.generatePdf(hashMap, destinationDocFile, destinationPdfFile,"", false);
        IuserDocumentService.deleteFileByCategoryCode(id, DocumentCategoryCode.PDF_HOME_ATTORNEY.getCode());
        IuserDocumentService.addFile(destinationPdfFile, pdfFileName, id, DocumentCategoryCode.PDF_HOME_ATTORNEY.getCode(),false);
    }
}
