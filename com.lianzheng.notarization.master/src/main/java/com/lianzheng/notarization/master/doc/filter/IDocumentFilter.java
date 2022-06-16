package com.lianzheng.notarization.master.doc.filter;

import com.lianzheng.notarization.master.entity.DocumentEntity;
import com.lianzheng.notarization.master.form.MapCommonForm;

import java.util.List;

public interface IDocumentFilter {
    String getCategory();
    boolean isMatch(DocumentEntity doc);
    MapCommonForm appendDocumentMapEntity(List<MapCommonForm> documents, DocumentEntity doc, int index,Long notarialOfficeId);
}
