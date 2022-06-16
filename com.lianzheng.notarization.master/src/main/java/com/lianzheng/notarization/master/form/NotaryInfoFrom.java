package com.lianzheng.notarization.master.form;


import lombok.Data;

/**
 * 公证信息（求真接口使用）
 */
@Data
public class NotaryInfoFrom {
    //公证处名称
    private String notaryOfficeName;
    //公证处机构编码
    private String notaryOfficeId;
    //公证员姓名
    private String notaryName;
    //公证员执业证编码
    private String notaryId;
    public NotaryInfoFrom(String notaryOfficeName,String notaryOfficeId,String notaryName,String notaryId){
        this.notaryOfficeName = notaryOfficeName;
        this.notaryOfficeId=notaryOfficeId;
        this.notaryName = notaryName;
        this.notaryId =notaryId;
    }

}
