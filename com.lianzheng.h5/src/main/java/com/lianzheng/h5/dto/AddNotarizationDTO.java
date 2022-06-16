package com.lianzheng.h5.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 事项新增
 * </p>
 *
 * @author JP
 * @since 2022-04-08
 */
@Data
public class AddNotarizationDTO {

    private Integer requiredVerification;

    private String usedToProvince;

    private String authComment;

    private String processStatus;

    private String usedToCity;

    private String orderId;

    private String idCardReverseUrl;

    private Integer signedReceipt;

    private String expressModeToSEF;

    private String idCardType;

    private Integer hasMoreCert;

    private Integer isAgent;

    private String staffRemark;

    private LocalDate createdTime;

    private String id;

    private Integer isRepeat;

    private String householdUrl;

    private String householdMainUrl;

    private String translateTo;

    private String updatedTime;

    private String avatarGatherUrl;

    private String updatedBy;

    private Integer sentToStraitsExchangeFoundation;

    private List<String> attorneyUrl;

    private List<String> declarationUrl;

    private LocalDate birth;

    private String processNo;

    private String pinyin;

    private String nationality;

    private String phone;

    private String notarzationTypeCode;

    private Integer copyNumber;

    private String idCardAddress;

    private String hash;

    private List<String> idMaterialUrls;

    private String status;

    private Integer gender;

    private String idCardNo;

    private Integer isDeleted;

    private String recordBy;

    private List<String> materialUrlList;

    private String email;

    private String usedToCountry;

    private String idCardFrontUrlAgent;

    private String actionBy;

    private String idCardReverseUrlAgent;

    private String applicantParty;

    private String userId;

    private String userRemark;

    private String realName;

    private String idCardFrontUrl;

    private List<String> idMaterialUrlList;

    private String createdBy;

    private String usedFor;

    private Integer genderAgent;

    private String idCardTypeAgent;

    private String graduatedDate;
    private String graduatedFrom;
    private String issuingAuthority;
    private String issuingTime;
    private String degreeName;
    private String grantTime;

    private Integer isSend;
    private String sendtToName;
    private String sentToPhone;
    private String sentToProvince;
    private String sentToCity;
    private String sentToArea;
    private String sentToAddress;

    private String realNameAgent;
    private LocalDate birthAgent;
    private String idCardNoAgent;
    private String idCardAddressAgent;


    @ApiModelProperty(value = "营业执照图片")
    private List<String> businessLicenseUrlList;

    @ApiModelProperty(value = "企业名称")
    private String companyName;

    @ApiModelProperty(value = "社会信用代码")
    private String socialCreditCode;

    @ApiModelProperty(value = "企业法人代表")
    private String legalRepresentative;

    @ApiModelProperty(value = "企业地址")
    private String companyAddress;

    @ApiModelProperty(value = "办理人是否是法人代表：0-不是，1-是")
    private Integer legalStatus;

    @ApiModelProperty(value = "企业法人代表")
    private String companyType;

    @ApiModelProperty(value = "成立日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private LocalDate registerDate;

    @ApiModelProperty(value = "使用地说明")
    private String usedToRemark;

    @ApiModelProperty(value = "用途说明")
    private String usedForRemark;

    /********************** 特殊事项字段 ***********************/
    @ApiModelProperty(value = "房屋类型：1-新房，2-二手房")
    private String roomType;

    @ApiModelProperty(value = "受托人姓名")
    private String trusteeName;

    @ApiModelProperty(value = "受托人性别")
    private String trusteeGender;

    @ApiModelProperty(value = "受托人出生日期")
    private String trusteeBirthday;

    @ApiModelProperty(value = "受托人身份证号")
    private String trusteeIdNum;

    @ApiModelProperty(value = "房屋地址")
    private String roomAddress;

    @ApiModelProperty(value = "与受托人关系")
    private String trusteeRelation;

    @ApiModelProperty(value = "不动产权证书编号")
    private String ownershipCertificate;

    @ApiModelProperty(value = "房屋所有权证")
    private String ownershipSCertificate;

    @ApiModelProperty(value = "国有土地使用证")
    private String landUseCertificate;
    /*********************************************************/

    // 问答的集合
    private List<MatterQuestion> mattersQuestionList;
    // 其他材料
    private List<String> otherAppendixUrlList;


    @Data
    public static class MatterQuestion {
        private String question;
        private String answer;
        private Integer sort;
    }
}
