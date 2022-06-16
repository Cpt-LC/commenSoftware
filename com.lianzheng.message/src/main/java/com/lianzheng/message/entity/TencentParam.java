package com.lianzheng.message.entity;

import lombok.Data;

@Data
public class TencentParam {

    private String templateName;
    private String templateId;
    private String[] templateParam;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String[] getTemplateParam() {
        return templateParam;
    }

    public void setTemplateParam(String[] templateParam) {
        this.templateParam = templateParam;
    }
}
