package com.lianzheng.notarization.purchaseHouse.generate;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lianzheng.core.auth.mgmt.utils.DateUtils;
import com.lianzheng.core.log.SpringContextUtil;
import com.lianzheng.core.pdf.PdfVarEntity;
import com.lianzheng.notarization.master.entity.NotarizationMattersSpecialEntity;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.enums.RoomTypeEnum;
import com.lianzheng.notarization.master.enums.SexEnum;
import com.lianzheng.notarization.master.generate.PdfQuestionGenerate;
import com.lianzheng.notarization.master.service.NotarizationMattersSpecialService;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PdfQuestionPurchaseHouseGenerate   extends PdfQuestionGenerate {
    private static final NotarizationMattersSpecialService InotarizationMattersSpecialService = SpringContextUtil.getBean(NotarizationMattersSpecialService.class);

    public PdfQuestionPurchaseHouseGenerate(NotarzationMasterEntity notarzationMasterEntity){
        super(notarzationMasterEntity);
    }

    @Override
    public void getTemplateParam(){
        super.getTemplateParam();
        //获取参数
        List<NotarizationMattersSpecialEntity> notarizationMattersSpecialEntityList = InotarizationMattersSpecialService.list(
                new QueryWrapper<NotarizationMattersSpecialEntity>().eq("notarizationId",notarzationMasterEntity.getId())
        );
        Map<String,NotarizationMattersSpecialEntity> map = notarizationMattersSpecialEntityList.stream().collect(Collectors.toMap(NotarizationMattersSpecialEntity::getEntryKey, notarizationMattersSpecialEntity -> notarizationMattersSpecialEntity));

        PdfVarEntity pdfVarEntity =null;
        String name = map.get("trusteeName").getEntryValue();
        pdfVarEntity = new PdfVarEntity("text", name, null);
        hashMap.put("trusteeName",pdfVarEntity.getMap());

        String gender = SexEnum.getEnumMsg(map.get("trusteeGender").getEntryValue());
        pdfVarEntity = new PdfVarEntity("text", gender, null);
        hashMap.put("trusteeGender",pdfVarEntity.getMap());

        Date birth = DateUtils.stringToDate(map.get("trusteeBirthday").getEntryValue(),DateUtils.DATE_PATTERN);
        String birthString = DateUtils.format(birth,"yyyy年MM月dd日出生，");
        pdfVarEntity = new PdfVarEntity("text", birthString, null);
        hashMap.put("trusteeBirthday",pdfVarEntity.getMap());


        String idNo = map.get("trusteeIdNum").getEntryValue();
        pdfVarEntity = new PdfVarEntity("text", idNo, null);
        hashMap.put("trusteeIdNum",pdfVarEntity.getMap());


        String trusteeRelation = map.get("trusteeRelation").getEntryValue();
        pdfVarEntity = new PdfVarEntity("text", trusteeRelation, null);
        hashMap.put("trusteeRelation",pdfVarEntity.getMap());


        pdfVarEntity = new PdfVarEntity("text", map.get("roomAddress").getEntryValue(), null);
        hashMap.put("roomAddress",pdfVarEntity.getMap());


        String ownershipCertificate = null;
        if(map.get("ownershipCertificate")==null || StringUtils.isEmpty(map.get("ownershipCertificate").getEntryValue())){ //若不存在插入空格
            ownershipCertificate = "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
        }else {
            ownershipCertificate = map.get("ownershipCertificate").getEntryValue();
        }
        pdfVarEntity = new PdfVarEntity("text", ownershipCertificate, null);
        hashMap.put("ownershipCertificate",pdfVarEntity.getMap());


        String landUseCertificate = null;
        if(map.get("landUseCertificate")==null || StringUtils.isEmpty(map.get("landUseCertificate").getEntryValue())){ //国家土地证若不存在插入空格
            landUseCertificate = "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
        }else {
            landUseCertificate = map.get("landUseCertificate").getEntryValue();
        }
        pdfVarEntity = new PdfVarEntity("text", landUseCertificate, null);
        hashMap.put("landUseCertificate",pdfVarEntity.getMap());


        pdfVarEntity = new PdfVarEntity("text", RoomTypeEnum.getEnumMsg(map.get("roomType").getEntryValue()), null);
        hashMap.put("roomType",pdfVarEntity.getMap());
    }

    @Override
    public  void Generatefile(String sourceFile) throws Exception {
        super.Generatefile(sourceFile);
    }


}
