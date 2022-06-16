package com.lianzheng.management.service.modules.notarization.quartz.service;

public interface JobService {
    void  generatePdfAfterPaidJob() throws Exception;

    void resetCertificateId();

    void cancelNotarization();
}
