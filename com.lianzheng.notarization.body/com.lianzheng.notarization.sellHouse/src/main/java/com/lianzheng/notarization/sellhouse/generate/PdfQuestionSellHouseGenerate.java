package com.lianzheng.notarization.sellhouse.generate;

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

public class PdfQuestionSellHouseGenerate extends PdfQuestionGenerate {
    private static final NotarizationMattersSpecialService InotarizationMattersSpecialService = SpringContextUtil.getBean(NotarizationMattersSpecialService.class);

    public PdfQuestionSellHouseGenerate(NotarzationMasterEntity notarzationMasterEntity){
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


        String ownershipSCertificate = null;
        if(map.get("ownershipSCertificate")==null || StringUtils.isEmpty(map.get("ownershipSCertificate").getEntryValue())){ //不动产证若不存在插入空格
            ownershipSCertificate = "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
        }else {
            ownershipSCertificate = map.get("ownershipSCertificate").getEntryValue();
        }
        pdfVarEntity = new PdfVarEntity("text", ownershipSCertificate, null);
        hashMap.put("ownershipSCertificate",pdfVarEntity.getMap());

    }

    @Override
    public  void Generatefile(String sourceFile) throws Exception {
        super.Generatefile(sourceFile);
    }
}
