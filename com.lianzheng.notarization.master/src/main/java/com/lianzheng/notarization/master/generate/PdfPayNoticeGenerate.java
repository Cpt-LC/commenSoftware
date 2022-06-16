package com.lianzheng.notarization.master.generate;

import com.lianzheng.core.auth.mgmt.entity.SysUserEntity;
import com.lianzheng.core.auth.mgmt.service.SysUserService;
import com.lianzheng.notarization.master.configParameter.param.DocumentParam;
import com.lianzheng.notarization.master.configParameter.param.PayArrayParam;
import com.lianzheng.notarization.master.configParameter.utils.ConfigParameterUtil;
import com.lianzheng.core.log.SpringContextUtil;
import com.lianzheng.core.pdf.PdfVarEntity;
import com.lianzheng.core.pdf.service.GeneratePdf;
import com.lianzheng.notarization.master.configParameter.utils.GetNotarizationTypeUtil;
import com.lianzheng.notarization.master.dao.UserOrderDao;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.entity.OrderEntity;
import com.lianzheng.notarization.master.enums.*;
import com.lianzheng.notarization.master.service.UserDocumentService;
import com.lianzheng.notarization.master.service.UserNotarzationMasterService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缴费通知书生成类
 */
public class PdfPayNoticeGenerate implements IGenerate {

    private static final UserNotarzationMasterService IuserNotarzationMasterService=SpringContextUtil.getBean(UserNotarzationMasterService.class);

    private static final GeneratePdf IgeneratePdf = SpringContextUtil.getBean(GeneratePdf.class);

    private static final UserDocumentService IuserDocumentService =SpringContextUtil.getBean(UserDocumentService.class);

    private static final SysUserService IsysUserService = SpringContextUtil.getBean(SysUserService.class);

    private static final UserOrderDao IuserOrderDao = SpringContextUtil.getBean(UserOrderDao.class);


    protected NotarzationMasterEntity notarzationMasterEntity;

    protected ConcurrentHashMap<String, Map<String, Object>> hashMap;


    //初始化参数
    public PdfPayNoticeGenerate(NotarzationMasterEntity notarzationMasterEntity){
        this.notarzationMasterEntity=notarzationMasterEntity;
        this.hashMap = IuserNotarzationMasterService.getCommenHashMap(notarzationMasterEntity);
        getTemplateParam();
    }

    @Override
    public void getTemplateParam(){

        PayArrayParam payArrayFrom = ConfigParameterUtil.getPayArray();
        DocumentParam documentParam = ConfigParameterUtil.getDocument();
        List<String> fileNameList = null;
        PdfVarEntity pdfVarEntity = null;
        List<Map<String, Object>> tableParam = null;//表格list往里面加入tableMap对象即可
        Map<String, Object> tableMap = null;//表格每一行对应一个tableMap
        List<String> url = null;
        SysUserEntity sysUserEntity = IsysUserService.queryByUserId(notarzationMasterEntity.getActionBy());
        OrderEntity orderEntity = IuserOrderDao.selectById(notarzationMasterEntity.getOrderId());

        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getRealName(), null);
        hashMap.put("realName", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getProcessNo(), null);
        hashMap.put("processNo", pdfVarEntity.getMap());
        url = new ArrayList<String>();
        url.add(IuserDocumentService.getUrl(notarzationMasterEntity.getNotarialOfficeId(),sysUserEntity.getSignUrl()));
        pdfVarEntity = new PdfVarEntity("img", url, null);
        hashMap.put("actionName", pdfVarEntity.getMap());


        //加入支付列表
        fileNameList = new ArrayList<String>();
        fileNameList.add("notarzationTypeCode");
        fileNameList.add("payType");
        fileNameList.add("payAmount");
        String notarzationType = GetNotarizationTypeUtil.getNotarizationType(notarzationMasterEntity.getNotarzationTypeCode());
        BigDecimal totalPay = BigDecimal.ZERO;//计算总价
        BigDecimal hundred = new BigDecimal("100");
        tableParam = new ArrayList<Map<String, Object>>();
        if (notarzationMasterEntity.getApplicantParty().equals(ApplicationPartyEnum.P.getCode())) {
            tableMap = new HashMap<>();
            tableMap.put("notarzationTypeCode", notarzationType);
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("NotaryAmount"));//公证费
            BigDecimal NotaryAmountP = payArrayFrom.getNotaryAmountP();
            tableMap.put("payAmount", NotaryAmountP.divide(hundred).setScale(2));
            //昆山项目  加价
            switch (documentParam.getEnvironment()){
                case "zero":
                    Boolean isAgentP = IuserNotarzationMasterService.isAgentP(notarzationMasterEntity);
                    if(isAgentP){
                        NotaryAmountP = NotaryAmountP.add(payArrayFrom.getNotaryAmountPAdd());
                        tableMap.put("payAmount",NotaryAmountP.divide(hundred).setScale(2));
                    }
            }
            tableParam.add(tableMap);
            totalPay = totalPay.add(NotaryAmountP);//计算总价

        } else {
            tableMap = new HashMap<>();
            tableMap.put("notarzationTypeCode", notarzationType);
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("NotaryAmount"));//公证费
            tableMap.put("payAmount", payArrayFrom.getNotaryAmountE().divide(hundred).setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(payArrayFrom.getNotaryAmountE());//计算总价
        }


        BigDecimal copyAmount = notarzationMasterEntity.getCopyNumber().subtract(BigDecimal.ONE);
        copyAmount = copyAmount.multiply(payArrayFrom.getCopyAmount());
        if (copyAmount.compareTo(BigDecimal.ZERO) > 0) {
            tableMap = new HashMap<>();
            tableMap.put("notarzationTypeCode", notarzationType);
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("CopyAmount"));//副本费
            tableMap.put("payAmount", copyAmount.divide(hundred).setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(copyAmount);//计算总价
        }

        BigDecimal translationAmount = orderEntity.getTranslationAmount();
        if (translationAmount.compareTo(BigDecimal.ZERO) > 0) {
            tableMap = new HashMap<>();
            tableMap.put("notarzationTypeCode", notarzationType);
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("TranslationAmount"));//翻译费
            tableMap.put("payAmount", translationAmount.divide(hundred).setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(translationAmount);//计算总价
        }

        BigDecimal logisticsAmount = orderEntity.getLogisticsAmount();
        if (logisticsAmount.compareTo(BigDecimal.ZERO) > 0) {
            tableMap = new HashMap<>();
            tableMap.put("notarzationTypeCode", notarzationType);
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("LogisticsAmount"));//快递费
            tableMap.put("payAmount", logisticsAmount.divide(hundred).setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(logisticsAmount);//计算总价
        }

        BigDecimal serviceAmount = orderEntity.getServiceAmount();
        if (serviceAmount.compareTo(BigDecimal.ZERO) > 0) {
            tableMap = new HashMap<>();
            tableMap.put("notarzationTypeCode", notarzationType);
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("ServiceAmount"));//公证服务费
            tableMap.put("payAmount", serviceAmount.divide(hundred).setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(serviceAmount);//计算总价
        }

        int sentToStraitsExchangeFoundation = notarzationMasterEntity.getSentToStraitsExchangeFoundation();
        String sentToStraitsExchangeFoundationType = notarzationMasterEntity.getExpressModeToSEF();
        if (sentToStraitsExchangeFoundation > 0 && sentToStraitsExchangeFoundationType != null && sentToStraitsExchangeFoundationType.equals(MailModeSEFEnum.expressModeToSEFP.getCode())) {
            tableMap = new HashMap<>();
            tableMap.put("notarzationTypeCode", notarzationType);
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("ModeToSEF"));//寄台湾海基快递费
            tableMap.put("payAmount", payArrayFrom.getModeToSEFP().divide(hundred).setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(payArrayFrom.getModeToSEFP());//计算总价
        }
        if (sentToStraitsExchangeFoundation > 0 && sentToStraitsExchangeFoundationType != null && sentToStraitsExchangeFoundationType.equals(MailModeSEFEnum.expressModeToSEFS.getCode())) {
            tableMap = new HashMap<>();
            tableMap.put("notarzationTypeCode", notarzationType);
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("ModeToSEF"));//寄台湾海基快递费
            tableMap.put("payAmount", payArrayFrom.getModeToSEFS().divide(hundred).setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(payArrayFrom.getModeToSEFS());//计算总价
        }

        String hasMoreCert = notarzationMasterEntity.getHasMoreCert();
        if (hasMoreCert.equals("1")) {
            tableMap = new HashMap<>();
            tableMap.put("notarzationTypeCode", notarzationType);
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("DoubleCertificate"));//双证译文文本相符
            tableMap.put("payAmount", payArrayFrom.getDoubleCertificate().divide(hundred).setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(payArrayFrom.getDoubleCertificate());//计算总价

            tableMap = new HashMap<>();
            tableMap.put("notarzationTypeCode", notarzationType);
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("DoubleCertificateCopy"));//双证译文文本相符副本费
            tableMap.put("payAmount", copyAmount.divide(hundred).setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(copyAmount);//计算总价
        }
        pdfVarEntity = new PdfVarEntity("table", tableParam, fileNameList);
        hashMap.put("payment", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", totalPay.divide(hundred).setScale(2), null);
        hashMap.put("totalPay", pdfVarEntity.getMap());
    }

    @Override
    public  void Generatefile(String sourceFile) throws Exception {
        String destinationDocFile= "notices/PDF_PAY_NOTICE_" + notarzationMasterEntity.getProcessNo() + ".docx";
        String destinationPdfFile= "notices/PDF_PAY_NOTICE_" + notarzationMasterEntity.getProcessNo() + ".pdf";
        String pdfFileName="PDF_PAY_NOTICE_" + notarzationMasterEntity.getProcessNo() + ".pdf";

        //生成文件
        String id =notarzationMasterEntity.getId();
        IgeneratePdf.generatePdf(hashMap, sourceFile, destinationDocFile, destinationPdfFile, true);
        IuserDocumentService.deleteFileByCategoryCode(id, DocumentCategoryCode.PDF_PAY_NOTICE.getCode());
        IuserDocumentService.addFile(destinationPdfFile, pdfFileName, id, DocumentCategoryCode.PDF_PAY_NOTICE.getCode(),true);

    }


    @Override
    public  void GeneratefileFinal() throws Exception {
        String id =notarzationMasterEntity.getId();
        String destinationDocFile= IuserDocumentService.getDocx(id,DocumentCategoryCode.PDF_PAY_NOTICE.getCode());
        String destinationPdfFile= "notices/PDF_PAY_NOTICE_" + notarzationMasterEntity.getProcessNo() + ".pdf";
        String pdfFileName="PDF_PAY_NOTICE_" + notarzationMasterEntity.getProcessNo() + ".pdf";

        //生成文件
        hashMap = IuserNotarzationMasterService.getSignHashMap(notarzationMasterEntity);
        IgeneratePdf.generatePdf(hashMap, destinationDocFile, destinationPdfFile, "", false);
        IuserDocumentService.deleteFileByCategoryCode(id, DocumentCategoryCode.PDF_PAY_NOTICE.getCode());
        IuserDocumentService.addFile(destinationPdfFile, pdfFileName, id, DocumentCategoryCode.PDF_PAY_NOTICE.getCode(),false);
    }
}
