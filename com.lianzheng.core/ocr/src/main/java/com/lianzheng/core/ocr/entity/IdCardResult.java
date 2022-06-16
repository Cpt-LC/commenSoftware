package com.lianzheng.core.ocr.entity;

import lombok.Data;

/**
 * @Description: TODO
 * @author: 何江雁
 * @date: 2022年02月17日 16:40
 */
public class IdCardResult extends OcrResult{
    private String address;
    private String birth;
    private String idNumber;
    private String issueAuthority;
    private String name;
    private String sex;
    private String validateDate;
    private String nationality;

    public IdCardResult(String name, String idNumber, String validateDate, String birth, String address, String issueAuthority, String sex, String nationality){
        this.name = name;
        this.idNumber = idNumber;
        this.validateDate = validateDate;
        this.birth = birth;
        this.address = address;
        this.issueAuthority = issueAuthority;
        this.sex = sex;
        this.nationality = nationality;
    }

    public void setAddress(String address){
    	this.address = address;
    }

    public String getAddress(){
    	return this.address;
    }

    public void setBirth(String birth){
    	this.birth = birth;
    }

    public String getBirth(){
    	return this.birth;
    }

    public void setIdNumber(String idNumber){
    	this.idNumber = idNumber;
    }

    public String getIdNumber(){
    	return this.idNumber;
    }

    public void setIssueAuthority(String issueAuthority){
    	this.issueAuthority = issueAuthority;
    }

    public String getIssueAuthority(){
    	return this.issueAuthority;
    }

    public void setName(String name){
    	this.name = name;
    }

    public String getName(){
    	return this.name;
    }

    public void setSex(String sex){
    	this.sex = sex;
    }

    public String getSex(){
    	return this.sex;
    }

    public void setValidateDate(String validateDate){
    	this.validateDate = validateDate;
    }

    public String getValidateDate(){
    	return this.validateDate;
    }

    public void setNationality(String nationality){
    	this.nationality = nationality;
    }

    public String getNationality(){
    	return this.nationality;
    }

}
