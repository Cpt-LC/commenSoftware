package com.lianzheng.core.ocr.entity;

import lombok.Data;

/**
 * @author kk
 * @date 2022/4/6 15:42
 * @describe
 * @remark
 */
@Data
public class BusinessLicenseResult extends OcrResult{

    private String isCopy;
    private String isElectronic;
    private String serialNumber;
    private String registrationCode;
    private String regCapital;
    private String creditCode;
    private String companyName;
    private String ownerName;
    private String companyType;
    private String address;
    private String scope;
    private String startTime;
    private String operatingPeriod;
    private String composingForm;
    private String paidInCapital;
    private String registrationDate;

}
