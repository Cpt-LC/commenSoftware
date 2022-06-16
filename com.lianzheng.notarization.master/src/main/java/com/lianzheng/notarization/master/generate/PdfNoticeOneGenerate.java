package com.lianzheng.notarization.master.generate;

import com.lianzheng.core.pdf.PdfVarEntity;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;


public class PdfNoticeOneGenerate extends PdfNoticeGenerate {

    public PdfNoticeOneGenerate(NotarzationMasterEntity notarzationMasterEntity) {
        super(notarzationMasterEntity);
    }

    @Override
    public void getTemplateParam(){
    }

    @Override
    public  void Generatefile(String sourceFile) throws Exception {
            super.Generatefile(sourceFile);
    }
}
