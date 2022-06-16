package com.lianzheng.notarization.driverLicense.generate;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lianzheng.core.auth.mgmt.utils.DateUtils;
import com.lianzheng.core.log.SpringContextUtil;
import com.lianzheng.core.pdf.PdfVarEntity;
import com.lianzheng.notarization.driverLicense.dao.UserNotarizationDriverLicenseDao;
import com.lianzheng.notarization.driverLicense.entity.NotarizationDriverLicenseEntity;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.generate.PdfQuestionGenerate;

public class PdfQuestionDLGenerate  extends PdfQuestionGenerate {
    private static final UserNotarizationDriverLicenseDao userNotarizationDriverLicenseDao= SpringContextUtil.getBean(UserNotarizationDriverLicenseDao.class);

    public PdfQuestionDLGenerate(NotarzationMasterEntity notarzationMasterEntity){
        super(notarzationMasterEntity);
    }

    @Override
    public void getTemplateParam(){
        super.getTemplateParam();
        NotarizationDriverLicenseEntity notarizationDriverLicenseEntity=  userNotarizationDriverLicenseDao.selectOne(
                new QueryWrapper<NotarizationDriverLicenseEntity>().eq("id",notarzationMasterEntity.getId()).eq("isDeleted",0)
        );
        PdfVarEntity pdfVarEntity =null;
        pdfVarEntity=new PdfVarEntity("text", DateUtils.format(notarizationDriverLicenseEntity.getIssuingTime()),null);
        hashMap.put("issuingTime",pdfVarEntity.getMap());
        pdfVarEntity=new PdfVarEntity("text",notarizationDriverLicenseEntity.getIssuingAuthority(),null);
        hashMap.put("issuingAuthority",pdfVarEntity.getMap());
    }

    @Override
    public  void Generatefile(String sourceFile) throws Exception {
        super.Generatefile(sourceFile);
    }

}
