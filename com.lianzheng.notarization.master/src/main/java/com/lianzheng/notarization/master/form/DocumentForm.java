package com.lianzheng.notarization.master.form;


import com.lianzheng.notarization.master.entity.DocumentEntity;
import lombok.Data;
/*
存储图片的token
 */
@Data
public class DocumentForm extends DocumentEntity {
    private String token;
}
