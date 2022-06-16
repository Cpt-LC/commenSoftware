package com.lianzheng.notarization.master.form;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;

/**
 * 详情表单公共父类
 */

@Data
public abstract  class MapCommonForm {
    private String type;
    private String key;
    private String label;
    private Object value;
    private String group;
    private String tableName;
    private int index;
    private Boolean ableToComment;
    private Boolean visible;
    private int span;
    private String text; //为前端提前加好属性，避免后添加的属性无法触发视图更新

    public MapCommonForm(String type, String key, String label, Object value, Boolean visible, String group, int index, String tableName, Boolean ableToComment, int span){
        this.type =type;
        this.key = key;
        this.label=label;
        this.value=value;
        this.group = group;
        this.tableName=tableName;
        this.index =index;
        this.ableToComment =ableToComment;
        this.visible=visible;
        this.span = span;
    }
    public MapCommonForm(String type, String key, String label, Object value, Boolean visible, String group, int index, Boolean ableToComment){
        this(type,key,label,value,visible,group,index, "notarzation_master", ableToComment, 12);
    }

    public abstract MapCommonForm deepClone();
}
