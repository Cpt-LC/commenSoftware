package com.lianzheng.core.ocr;

import com.lianzheng.core.ocr.config.OcrConfig;
import com.lianzheng.core.ocr.config.OcrSettings;
import com.lianzheng.core.ocr.provider.OcrBaseService;
import com.lianzheng.core.ocr.provider.TextinService;
import com.lianzheng.core.ocr.utils.SpringContextOcrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * @Description: OCR相应服务的工厂，负责根据需要选取合适的ocr供应商
 * @author: 何江雁
 * @date: 2022年02月16日 17:04
 */
public class OcrFactory {
    public OcrBaseService getService(OcrConfig ocrConfig) throws IOException {
        OcrSettings settings = ocrConfig.getOcrSettings();
        switch (settings.getType()){
            case "textin":
                return (OcrBaseService)SpringContextOcrUtil.getBean("TextinService");
        }
        Assert.isTrue(false, "Not suupport the type:"+settings.getType());
        return null;
    }
}
