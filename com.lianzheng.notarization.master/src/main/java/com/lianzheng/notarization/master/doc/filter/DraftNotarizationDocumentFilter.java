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
 * @date: 2022年01月07日 20:51
 */
public class DraftNotarizationDocumentFilter extends UserDocumentFilter {

    public DraftNotarizationDocumentFilter(String category) {
        super(category);
    }

    @Override
    public boolean isMatch(DocumentEntity doc) {
        //公证拟稿纸的section改为展示生成好的公证书，以供修改
        return DocumentCategoryCode.PDF_NOTARIZATION.getCode().equals(doc.getCategoryCode())
                || DocumentCategoryCode.PDF_NOTARIZATION_CERT.getCode().equals(doc.getCategoryCode());
    }

    @Override
    public MapCommonForm appendDocumentMapEntity(List<MapCommonForm> documents, DocumentEntity doc, int index, Long notarialOfficeId) {
        MapCommonForm map = super.appendDocumentMapEntity(documents, doc, index, notarialOfficeId);
        if (map != null) {
            map.setType("pdf");
        }
        return map;
    }
}
