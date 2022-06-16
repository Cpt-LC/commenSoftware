package com.lianzheng.notarization.master.doc.filter;

import com.lianzheng.notarization.master.entity.DocumentEntity;
import com.lianzheng.notarization.master.form.MapCommonForm;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: TODO,改成从配置文件读取
 * @author: 何江雁
 * @date: 2022年01月07日 20:53
 */
public class DocumentFilterConfig {

    private static final Map<String,IDocumentFilter> Filters = new HashMap<String, IDocumentFilter>(){{
        put("身份证明材料", new UserDocumentFilter("身份证明材料"));
        put("具体材料", new MaterialDocumentFilter("具体材料"));
        put("其他证明材料", new OtherMaterialDocumentFilter("其他证明材料"));
        put("公证材料", new NotaryDocumentFilter("公证材料"));
        put("签字材料", new GenerationDocumentFilter("签字材料"));
        put("公证拟稿", new DraftNotarizationDocumentFilter("公证拟稿"));
        put("公证结果", new NotaryResultDocumentFilter("公证结果"));
    }};

    public static final Map<String,IDocumentFilter> getFilters(){
        return Filters;
    }

}
