package com.lianzheng.management.service.modules.notarization.form;


import com.lianzheng.notarization.master.entity.NotarzationAuthCommentEntity;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GreffierOperationForm {
    private Map<String,Object> notarzationForm;
    private List<NotarzationAuthCommentEntity> notarzationAuthCommentEntityList;
}
