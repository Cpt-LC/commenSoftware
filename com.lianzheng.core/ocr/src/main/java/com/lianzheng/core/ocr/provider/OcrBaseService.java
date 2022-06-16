package com.lianzheng.core.ocr.provider;

import com.lianzheng.core.ocr.entity.OcrInput;
import com.lianzheng.core.ocr.entity.OcrResult;
import sun.security.provider.certpath.OCSP;

import java.io.IOException;

/**
 * @Description: OCR的公共接口类
 * @author: 何江雁
 * @date: 2022年02月15日 21:22
 */
public abstract class OcrBaseService {

    /**
     * fetch data by rule id
     *
     * @param ocrInput ocr所需参数
     * @return OcrResult
     */
    public abstract OcrResult extract(OcrInput ocrInput) throws IOException;

    public abstract OcrResult extractBusinessLicense(OcrInput ocrInput) throws IOException;

    public abstract OcrResult extractCommon(OcrInput ocrInput) throws IOException;
}
