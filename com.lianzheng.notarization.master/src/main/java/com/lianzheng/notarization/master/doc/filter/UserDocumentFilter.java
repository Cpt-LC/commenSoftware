package com.lianzheng.notarization.master.doc.filter;

import com.lianzheng.common_service.file_storage_sdk.util.FileStorageUtil;
import com.lianzheng.core.auth.mgmt.entity.SysNotarialOfficeEntity;
import com.lianzheng.core.auth.mgmt.utils.FileTokenUtils;
import com.lianzheng.notarization.master.configParameter.param.DocumentParam;
import com.lianzheng.notarization.master.configParameter.utils.ConfigParameterUtil;
import com.lianzheng.core.log.SpringContextUtil;
import com.lianzheng.notarization.master.entity.DocumentEntity;
import com.lianzheng.notarization.master.enums.DocumentCategoryCode;
import com.lianzheng.notarization.master.form.MapCommonForm;
import com.lianzheng.notarization.master.form.MapImgForm;
import com.lianzheng.notarization.master.service.UserDocumentService;

import java.util.Arrays;
import java.util.List;

/**
 * @Description: 判断文件是否是用户上传的证明材料，包含签名图片
 * @author: 何江雁
 * @date: 2022年01月07日 20:49
 */
public class UserDocumentFilter implements IDocumentFilter {
    private static final UserDocumentService IuserDocumentService = SpringContextUtil.getBean(UserDocumentService.class);
    protected String category;

    public UserDocumentFilter(String category) {
        this.category = category;
    }


//    private static final String[] MaterialCategories = new String[]{
//            DocumentCategoryCode.MATERIAL_OT.getCode(),
//            DocumentCategoryCode.MATERIAL_OT2.getCode(),
//            DocumentCategoryCode.ID_BACK.getCode(),
//            DocumentCategoryCode.ID_FRONT.getCode()};

    @Override
    public String getCategory() {
        return this.category;
    }

    @Override
    public boolean isMatch(DocumentEntity doc) {
        DocumentParam documentParam = ConfigParameterUtil.getDocument();//获取文件展示配置
        String[] MaterialCategories = documentParam.getUserDocumentFilter();
        boolean isMatch = Arrays.stream(MaterialCategories).anyMatch(d -> d.equals(doc.getCategoryCode()));
        return isMatch;
    }

    @Override
    public MapCommonForm appendDocumentMapEntity(List<MapCommonForm> documents, DocumentEntity doc, int index,Long notarialOfficeId) {
        boolean isMatch = this.isMatch(doc);
        if (!isMatch) {
            return null;
        }

        //证明材料
        String uploadedAbsolutePath = doc.getUploadedAbsolutePath();
        String fileName = uploadedAbsolutePath.substring(uploadedAbsolutePath.lastIndexOf("/") + 1);
        String uuid = IuserDocumentService.getToken(notarialOfficeId,uploadedAbsolutePath);
        MapCommonForm map = new MapImgForm("img", doc.getId(), DocumentCategoryCode.getEnumMsg(doc.getCategoryCode()), uploadedAbsolutePath, fileName, uuid, this.category, doc.getId(), doc.getCategoryCode(), index, false);
        documents.add(map);
        return map;
    }
}
