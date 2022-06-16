package com.lianzheng.notarization.tax.generate;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lianzheng.core.auth.mgmt.utils.DateUtils;
import com.lianzheng.core.log.SpringContextUtil;
import com.lianzheng.core.pdf.PdfVarEntity;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.generate.PdfQuestionGenerate;
import com.lianzheng.notarization.tax.dao.UserNotarizationTaxDao;
import com.lianzheng.notarization.tax.entity.NotarizationTaxEntity;

public class PdfQuestionTaxGenerate  extends PdfQuestionGenerate {
    private static final UserNotarizationTaxDao userNotarizationTaxDao= SpringContextUtil.getBean(UserNotarizationTaxDao.class);

    public PdfQuestionTaxGenerate(NotarzationMasterEntity notarzationMasterEntity){
        super(notarzationMasterEntity);
    }

    @Override
    public void getTemplateParam(){
        super.getTemplateParam();
        NotarizationTaxEntity notarizationTaxEntity=  userNotarizationTaxDao.selectOne(
                new QueryWrapper<NotarizationTaxEntity>().eq("id",notarzationMasterEntity.getId()).eq("isDeleted",0)
        );
        PdfVarEntity pdfVarEntity =null;
        pdfVarEntity=new PdfVarEntity("text", DateUtils.format(notarizationTaxEntity.getIssuingTime()),null);
        hashMap.put("issuingTime",pdfVarEntity.getMap());
        pdfVarEntity=new PdfVarEntity("text",notarizationTaxEntity.getIssuingAuthority(),null);
        hashMap.put("issuingAuthority",pdfVarEntity.getMap());
    }

    @Override
    public  void Generatefile(String sourceFile) throws Exception {
        super.Generatefile(sourceFile);
    }


}
