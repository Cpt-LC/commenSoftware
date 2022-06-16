package com.lianzheng.notarization.master.form;

import lombok.Data;

@Data
public class MapImgForm extends MapCommonForm {
        private String fileName;

        private String token;
        private String docId;
        private String categoryCode;
        private String signedText;

        public MapImgForm(String type,String key, String label, Object value, String fileName, String token, String group, String docId,String categoryCode, int index,Boolean ableToComment, int span){
            super(type,key,label,value,true,group,index,"document", ableToComment,span);
            this.fileName=fileName;
            this.token =token;
            this.docId = docId;
            this.categoryCode=categoryCode;
//            this.url =url;
        }
    public MapImgForm(String type,String key, String label, Object value, String fileName, String token, String group, String docId,String categoryCode, int index,Boolean ableToComment){
        this(type,key,label,value,fileName,token,group, docId,categoryCode,index, ableToComment,12);
//            this.url =url;
    }
        @Override
        public MapCommonForm deepClone(){
            MapImgForm obj = new MapImgForm(this.getType(), this.getKey(), this.getLabel(),this.getValue(),this.getFileName(), this.getToken(),this.getGroup(),this.getDocId(),this.getCategoryCode(), this.getIndex(), this.getAbleToComment(), this.getSpan());

            return obj;
        }
}
