package com.lianzheng.notarization.master.doc.filter;

import com.lianzheng.core.auth.mgmt.utils.FileTokenUtils;
import com.lianzheng.notarization.master.entity.DocumentEntity;
import com.lianzheng.notarization.master.enums.DocumentCategoryCode;
import com.lianzheng.notarization.master.form.MapCommonForm;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: TODO
 * @author: 何江雁
 * @date: 2022年01月07日 20:50
 */
public class NotaryDocumentFilter extends UserDocumentFilter{

    public NotaryDocumentFilter(String category){
        super(category);
    }
    @Override
    public boolean isMatch(DocumentEntity doc) {
        return DocumentCategoryCode.NOTARY_MATERIAL.getCode().equals(doc.getCategoryCode());
    }

}
