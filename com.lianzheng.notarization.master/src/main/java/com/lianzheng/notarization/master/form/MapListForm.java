package com.lianzheng.notarization.master.form;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MapListForm extends MapCommonForm {
    //类型

    private Boolean disabled;
    private Boolean required;
    private List<Map<String,Object>> selectList;

    public MapListForm(String type, String key, String label, Object value, Boolean disabled, Boolean required, List<Map<String,Object>> selectList, Boolean visible, String group, int index, String tableName, Boolean ableToComment, int span){
        super(type,key,label,value,visible,group,index, tableName,ableToComment,span);
        this.disabled=disabled;
        this.required=required;
        this.selectList=selectList;

    };
    public MapListForm(String type, String key, String label, Object value, Boolean disabled, Boolean required, List<Map<String,Object>> selectList, Boolean visible, String group, int index, Boolean ableToComment, int span){
        this(type,key,label,value, disabled, required, selectList,visible,group,index,"notarzation_master", ableToComment,span);

    };

    public MapListForm(String type, String key, String label, Object value, Boolean disabled, Boolean required, List<Map<String,Object>> selectList, Boolean visible, String group, int index, String tableName, Boolean ableToComment){
        this(type,key,label,value, disabled, required, selectList,visible,group,index,tableName, ableToComment,12);

    };
    public MapListForm(String type, String key, String label, Object value, Boolean disabled, Boolean required, List<Map<String,Object>> selectList, Boolean visible, String group, int index, Boolean ableToComment){
        this(type,key,label,value, disabled, required, selectList,visible,group,index,"notarzation_master", ableToComment);
    };


    @Override
    public MapCommonForm deepClone(){
        MapListForm obj = new MapListForm(this.getType(), this.getKey(), this.getLabel(),this.getValue(),this.getDisabled(),this.getRequired(),this.selectList,this.getVisible(), this.getGroup(), this.getIndex(), this.getTableName(), this.getAbleToComment(), this.getSpan());

        return obj;
    }
}
