package com.lianzheng.notarization.degree.generate;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lianzheng.core.auth.mgmt.utils.DateUtils;
import com.lianzheng.core.log.SpringContextUtil;
import com.lianzheng.core.pdf.PdfVarEntity;
import com.lianzheng.notarization.degree.dao.UserNotarizationDegreeDao;
import com.lianzheng.notarization.degree.entity.NotarizationDegreeEntity;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.generate.PdfQuestionGenerate;

public class PdfQuestionDCGenerate extends PdfQuestionGenerate {
    private static final UserNotarizationDegreeDao userNotarizationDegreeDao= SpringContextUtil.getBean(UserNotarizationDegreeDao.class);

    public PdfQuestionDCGenerate(NotarzationMasterEntity notarzationMasterEntity){
        super(notarzationMasterEntity);
    }

    @Override
    public void getTemplateParam(){
        super.getTemplateParam();
        NotarizationDegreeEntity notarizationDegreeEntity=  userNotarizationDegreeDao.selectOne(
                new QueryWrapper<NotarizationDegreeEntity>().eq("id",notarzationMasterEntity.getId()).eq("isDeleted",0)
        );
        PdfVarEntity pdfVarEntity =null;
        pdfVarEntity=new PdfVarEntity("text", DateUtils.format(notarizationDegreeEntity.getGrantTime()),null);
        hashMap.put("grantTime",pdfVarEntity.getMap());
        pdfVarEntity=new PdfVarEntity("text",notarizationDegreeEntity.getIssuingAuthority(),null);
        hashMap.put("issuingAuthority",pdfVarEntity.getMap());
        pdfVarEntity=new PdfVarEntity("text",notarizationDegreeEntity.getDegreeName(),null);
        hashMap.put("degreeName",pdfVarEntity.getMap());
    }

    @Override
    public  void Generatefile(String sourceFile) throws Exception {
        super.Generatefile(sourceFile);
    }
}
