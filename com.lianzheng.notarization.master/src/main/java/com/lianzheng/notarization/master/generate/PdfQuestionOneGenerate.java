package com.lianzheng.notarization.master.generate;

import com.lianzheng.core.log.SpringContextUtil;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.service.UserNotarzationMasterOneService;
import com.lianzheng.notarization.master.service.UserNotarzationMasterService;

public class PdfQuestionOneGenerate extends PdfQuestionGenerate {

    private static final UserNotarzationMasterOneService IuserNotarzationMasterOneService= SpringContextUtil.getBean(UserNotarzationMasterOneService.class);
    public PdfQuestionOneGenerate(NotarzationMasterEntity notarzationMasterEntity) {
        super(notarzationMasterEntity);
    }

    public void getTemplateParam(){
        hashMap.putAll(IuserNotarzationMasterOneService.getPublicParam(notarzationMasterEntity));
    }

    @Override
    public void Generatefile(String sourceFile) throws Exception {
        super.Generatefile(sourceFile);
    }
}
