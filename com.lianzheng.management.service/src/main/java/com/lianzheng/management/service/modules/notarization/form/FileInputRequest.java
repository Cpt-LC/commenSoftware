package com.lianzheng.management.service.modules.notarization.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class FileInputRequest {
    @NotEmpty(message = "类型数据不可为空")
    private String categoryCode;
    @NotEmpty(message = "类型数据不可为空")
    private String fileName;

}
