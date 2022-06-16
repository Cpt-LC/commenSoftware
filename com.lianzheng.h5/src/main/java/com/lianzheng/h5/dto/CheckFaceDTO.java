package com.lianzheng.h5.dto;

import com.drew.lang.annotations.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author kk
 * @date 2022/6/13 22:29
 * @describe 人脸识别请求参数
 * @remark
 */
@Data
public class CheckFaceDTO {

    @NotNull
    private String name;

    @NotNull
    private String cardNo;

    @NotNull
    private MultipartFile faceImgFile;

}
