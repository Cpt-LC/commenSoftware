package com.lianzheng.core.ocr.entity;

import lombok.Data;

/**
 * @author kk
 * @date 2022/4/6 16:02
 * @describe
 * @remark
 */
@Data
public class BusinessLicenseInput extends OcrInput{
    private byte[] imageData;
}
