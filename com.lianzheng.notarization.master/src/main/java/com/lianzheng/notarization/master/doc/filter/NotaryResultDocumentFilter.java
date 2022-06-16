package com.lianzheng.notarization.master.doc.filter;

import com.lianzheng.notarization.master.entity.DocumentEntity;
import com.lianzheng.notarization.master.enums.DocumentCategoryCode;

/**
 * @Description: TODO
 * @author: 何江雁
 * @date: 2022年01月07日 20:50
 */
public class NotaryResultDocumentFilter extends UserDocumentFilter{

    public NotaryResultDocumentFilter(String category){
        super(category);
    }
    @Override
    public boolean isMatch(DocumentEntity doc) {
        return DocumentCategoryCode.PDF_DRAFT.getCode().equals(doc.getCategoryCode())
                || DocumentCategoryCode.PDF_DRAFT_CERT.getCode().equals(doc.getCategoryCode())
                || DocumentCategoryCode.PDF_NOTARIZATION.getCode().equals(doc.getCategoryCode())
                || DocumentCategoryCode.PDF_NOTARIZATION_CERT.getCode().equals(doc.getCategoryCode());
    }

}
