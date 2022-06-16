package com.lianzheng.notarization.master.form;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class FileFrom {
    private List<MultipartFile> files;
    private MultipartFile file;
    @NotEmpty(message = "类型数据不可为空")
    private String categoryCode;
    @NotEmpty(message = "主键数据不可为空")
    private String refererId;
    @NotEmpty(message = "表名数据不可为空")
    private String refererTableName;
}

