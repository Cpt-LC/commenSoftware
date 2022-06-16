package com.lianzheng.notarization.graduation.generate;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lianzheng.core.auth.mgmt.utils.DateUtils;
import com.lianzheng.core.log.SpringContextUtil;
import com.lianzheng.core.pdf.PdfVarEntity;
import com.lianzheng.notarization.graduation.dao.UserNotarzationGraduationDao;
import com.lianzheng.notarization.graduation.entity.NotarzationGraduationEntity;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.generate.PdfQuestionGenerate;

/**
 * 毕业证明询问笔录
 */
public class PdfQuestionGRGenerate extends PdfQuestionGenerate {

    private static final UserNotarzationGraduationDao userNotarzationGraduationDao= SpringContextUtil.getBean(UserNotarzationGraduationDao.class);

    public PdfQuestionGRGenerate(NotarzationMasterEntity notarzationMasterEntity){
        super(notarzationMasterEntity);
    }

    @Override
    public void getTemplateParam(){
        super.getTemplateParam();
        NotarzationGraduationEntity notarzationGraduationEntity=  userNotarzationGraduationDao.selectOne(
                new QueryWrapper<NotarzationGraduationEntity>().eq("id",notarzationMasterEntity.getId()).eq("isDeleted",0)
        );
        PdfVarEntity pdfVarEntity =null;
        pdfVarEntity=new PdfVarEntity("text", DateUtils.format(notarzationGraduationEntity.getGraduatedDate()),null);
        hashMap.put("graduatedDate",pdfVarEntity.getMap());
        pdfVarEntity=new PdfVarEntity("text",notarzationGraduationEntity.getGraduatedFrom(),null);
        hashMap.put("graduatedFrom",pdfVarEntity.getMap());

    }

    @Override
    public  void Generatefile(String sourceFile) throws Exception {
        super.Generatefile(sourceFile);
    }
}
