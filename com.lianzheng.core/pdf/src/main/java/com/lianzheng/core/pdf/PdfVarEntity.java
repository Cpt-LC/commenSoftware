package com.lianzheng.core.pdf;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class PdfVarEntity {
    private String type;
    private Object value;
    private List<String> fieldName;

    public PdfVarEntity(String type, Object value, List<String> fieldName){
        this.type =type;
        this.value=value;
        this.fieldName=fieldName;
    }
    public Map<String,Object> getMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("type",type);
        map.put("value",value);
        map.put("fieldName",fieldName);
        return map;
    }
}
