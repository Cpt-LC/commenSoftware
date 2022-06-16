package com.lianzheng.notarization.master.doc.filter;

import com.lianzheng.notarization.master.configParameter.param.DocumentParam;
import com.lianzheng.notarization.master.configParameter.utils.ConfigParameterUtil;
import com.lianzheng.notarization.master.entity.DocumentEntity;
import com.lianzheng.notarization.master.enums.DocumentCategoryCode;

import java.util.Arrays;

/**
 * 证明材料
 */
public class MaterialDocumentFilter extends UserDocumentFilter{
    public MaterialDocumentFilter(String category){
        super(category);
    }
    @Override
    public boolean isMatch(DocumentEntity doc) {
        DocumentParam documentParam = ConfigParameterUtil.getDocument();//获取文件展示配置
        String[] MaterialCategories = documentParam.getMaterialDocumentFilter();
        boolean isMatch = Arrays.stream(MaterialCategories).anyMatch(d -> d.equals(doc.getCategoryCode()));
        return isMatch;
    }
}
