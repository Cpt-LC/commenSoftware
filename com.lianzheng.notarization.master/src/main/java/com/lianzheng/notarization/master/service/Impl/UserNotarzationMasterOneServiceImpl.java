package com.lianzheng.notarization.master.service.Impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianzheng.core.auth.mgmt.utils.DateUtils;
import com.lianzheng.core.pdf.PdfVarEntity;
import com.lianzheng.notarization.master.configParameter.param.CountryParam;
import com.lianzheng.notarization.master.configParameter.utils.ConfigParameterUtil;
import com.lianzheng.notarization.master.dao.UserDao;
import com.lianzheng.notarization.master.dao.UserNotarzationMasterDao;
import com.lianzheng.notarization.master.dao.UserOrderDao;
import com.lianzheng.notarization.master.entity.NotarzationMasterEntity;
import com.lianzheng.notarization.master.entity.OrderEntity;
import com.lianzheng.notarization.master.entity.UserEntity;
import com.lianzheng.notarization.master.enums.NotarizationTypeEnum;
import com.lianzheng.notarization.master.enums.NotarizationTypeOneEnum;
import com.lianzheng.notarization.master.service.UserNotarzationMasterOneService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@DS("h5")
public class UserNotarzationMasterOneServiceImpl  extends ServiceImpl<UserNotarzationMasterDao, NotarzationMasterEntity> implements UserNotarzationMasterOneService {
    @Autowired
    private UserOrderDao userOrderDao;
    @Autowired
    private UserDao userDao;


    @Override
    public ConcurrentHashMap<String, Map<String, Object>> getPublicParam(NotarzationMasterEntity notarzationMasterEntity) {

        CountryParam countryParam = ConfigParameterUtil.getCountry();//获取国家参数
        OrderEntity orderEntity = userOrderDao.selectById(notarzationMasterEntity.getOrderId());//订单信息
        ConcurrentHashMap<String, Map<String, Object>> hashMap = new ConcurrentHashMap<>();
        String question =null;
        List<String> url = null;
        List<String> fileNameList = null;
        PdfVarEntity pdfVarEntity = null;
        List<Map<String, Object>> tableParam = null;//表格list往里面加入tableMap对象即可
        Map<String, Object> tableMap = null;//表格每一行对应一个tableMap

        pdfVarEntity = new PdfVarEntity("text", DateUtils.format(notarzationMasterEntity.getCreatedTime(),"yyyy-MM-dd"), null);
        hashMap.put("today", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getRealName(), null);
        hashMap.put("realName", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getIdCardType() + "：" + notarzationMasterEntity.getIdCardNo(), null);
        hashMap.put("idCardNo", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", NotarizationTypeOneEnum.getEnumMsg(notarzationMasterEntity.getNotarzationTypeCode()), null);
        hashMap.put("notarzationTypeCode", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getUsedFor(), null);
        hashMap.put("usedFor", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getUsedToCountry(), null);
        hashMap.put("usedToCountry", pdfVarEntity.getMap());

        if (countryParam.getTranslateLanguage().contains(notarzationMasterEntity.getUsedToCountry())) {
            pdfVarEntity = new PdfVarEntity("text", "", null);
            hashMap.put("translateTo", pdfVarEntity.getMap());
        } else {
            question = "<br>问：是否需要翻译？<br>" + "答：需要。"
                    + "<br>问：翻译成什么语种？<br>"
                    +"答："+notarzationMasterEntity.getTranslateTo()+"。";
            pdfVarEntity = new PdfVarEntity("text", question, null);
            hashMap.put("translateTo", pdfVarEntity.getMap());
        }

        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getCopyNumber(), null);
        hashMap.put("copyNumber", pdfVarEntity.getMap());
        if(orderEntity.getIsSend() == 1){
            question = "纸质公证书邮寄至"+orderEntity.getSentToProvince()+orderEntity.getSentToCity()+orderEntity.getSentToArea()+orderEntity.getSentToAddress()+"（顺丰到付）";
            pdfVarEntity = new PdfVarEntity("text", question, null);
            hashMap.put("isSend", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", "", null);
            hashMap.put("email", pdfVarEntity.getMap());
        }else {
            question = "电子公证书发送至"+ notarzationMasterEntity.getEmail()+"邮箱（不需要纸质公证书）";
            pdfVarEntity = new PdfVarEntity("text", question, null);
            hashMap.put("email", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", "", null);
            hashMap.put("isSend", pdfVarEntity.getMap());
        }

        if(StringUtils.isBlank(notarzationMasterEntity.getPinyin())){
            pdfVarEntity = new PdfVarEntity("text", "", null);
            hashMap.put("pinyin", pdfVarEntity.getMap());
        }else {
            question ="如有多音字，请注明："+ notarzationMasterEntity.getPinyin()+"。";
            pdfVarEntity = new PdfVarEntity("text", question, null);
            hashMap.put("pinyin", pdfVarEntity.getMap());
        }

        return hashMap;
    }
}
