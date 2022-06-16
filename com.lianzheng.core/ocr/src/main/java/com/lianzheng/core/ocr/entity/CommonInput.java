package com.lianzheng.core.ocr.entity;

import lombok.Data;

/**
 * @author kk
 * @date 2022/4/15 14:10
 * @describe
 * @remark
 */
@Data
public class CommonInput extends OcrInput{
    private byte[] imageData;
}
