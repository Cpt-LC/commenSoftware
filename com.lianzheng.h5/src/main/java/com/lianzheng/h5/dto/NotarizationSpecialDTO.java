package com.lianzheng.h5.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 特殊事项字段
 * </p>
 *
 * @author JP
 * @since 2022-04-08
 */
@Data
public class NotarizationSpecialDTO {

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

}
