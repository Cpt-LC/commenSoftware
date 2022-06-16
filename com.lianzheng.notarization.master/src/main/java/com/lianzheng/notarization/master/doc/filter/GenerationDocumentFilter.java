package com.lianzheng.notarization.master.doc.filter;

import com.lianzheng.core.auth.mgmt.utils.FileTokenUtils;
import com.lianzheng.notarization.master.entity.DocumentEntity;
import com.lianzheng.notarization.master.enums.DocumentCategoryCode;
import com.lianzheng.notarization.master.form.MapCommonForm;
import com.lianzheng.notarization.master.form.MapImgForm;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: TODO
 * @author: 何江雁
 * @date: 2022年01月07日 20:51
 */
public class GenerationDocumentFilter extends UserDocumentFilter{

    //todo,下面的documentCategory列表需做成可配置的
    private static final String[] MaterialCategories = new String[]{
            DocumentCategoryCode.PDF_NOTICE.getCode(),
            DocumentCategoryCode.PDF_PAY_NOTICE.getCode(),
            DocumentCategoryCode.PDF_APPLICATION.getCode(),
            DocumentCategoryCode.PDF_RECEIPT.getCode(),
            DocumentCategoryCode.PDF_QUESTION.getCode(),
            DocumentCategoryCode.PDF_AC_NOTICE.getCode(),
            DocumentCategoryCode.PDF_FO_NOTICE.getCode(),
            DocumentCategoryCode.PDF_HOME_ATTORNEY.getCode(),
            DocumentCategoryCode.PDF_NOTICE_ENTRUSTED.getCode(),
            DocumentCategoryCode.PDF_NOTICE_HANDLE.getCode(),
            DocumentCategoryCode.PDF_NOTICE_DECLARATION.getCode()
    };

    public GenerationDocumentFilter(String category){
        super(category);
    }

    @Override
    public boolean isMatch(DocumentEntity doc) {
        boolean isMatch = Arrays.stream(MaterialCategories).anyMatch(d -> d.equals(doc.getCategoryCode()));
        return isMatch;
    }
}
