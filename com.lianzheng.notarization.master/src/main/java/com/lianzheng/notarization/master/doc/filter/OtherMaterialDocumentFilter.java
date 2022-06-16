package com.lianzheng.notarization.master.doc.filter;

import com.lianzheng.notarization.master.entity.DocumentEntity;
import com.lianzheng.notarization.master.enums.DocumentCategoryCode;

/**
 * 其他证明材料
 */
public class OtherMaterialDocumentFilter extends UserDocumentFilter{
    public OtherMaterialDocumentFilter(String category){
        super(category);
    }
    @Override
    public boolean isMatch(DocumentEntity doc) {
        return DocumentCategoryCode.MATERIAL_OT2.getCode().equals(doc.getCategoryCode());
    }
}
