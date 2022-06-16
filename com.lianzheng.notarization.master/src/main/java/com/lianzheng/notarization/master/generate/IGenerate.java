package com.lianzheng.notarization.master.generate;

import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//生成签字文件
public interface IGenerate {
        // 获取各模板参数
        void getTemplateParam();
        void Generatefile(String sourceFile) throws Exception;
        void GeneratefileFinal() throws Exception;
}
