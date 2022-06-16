package com.lianzheng.notarization.master.service.Impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianzheng.core.auth.mgmt.config.ShiroUtils;
import com.lianzheng.core.auth.mgmt.dao.SysNotarialOfficeDao;
import com.lianzheng.core.auth.mgmt.entity.SysNotarialOfficeEntity;
import com.lianzheng.core.auth.mgmt.entity.SysUserEntity;
import com.lianzheng.core.auth.mgmt.service.SysUserService;
import com.lianzheng.core.auth.mgmt.service.SysUserTokenService;
import com.lianzheng.core.auth.mgmt.utils.DateUtils;
import com.lianzheng.core.auth.mgmt.utils.FileTokenUtils;
import com.lianzheng.core.auth.mgmt.utils.RedisUtils;
import com.lianzheng.core.exceptionhandling.exception.COREException;
import com.lianzheng.core.pdf.PdfVarEntity;
import com.lianzheng.core.pdf.service.GeneratePdf;
import com.lianzheng.core.resource.FileContentUtil;
import com.lianzheng.core.server.PagesUtils;
import com.lianzheng.notarization.master.configParameter.param.CountryParam;
import com.lianzheng.notarization.master.configParameter.param.DocumentParam;
import com.lianzheng.notarization.master.configParameter.param.PayArrayParam;
import com.lianzheng.notarization.master.configParameter.param.SignUrlParam;
import com.lianzheng.notarization.master.configParameter.utils.ConfigParameterUtil;
import com.lianzheng.notarization.master.dao.*;
import com.lianzheng.notarization.master.doc.filter.DocumentFilterConfig;
import com.lianzheng.notarization.master.doc.filter.IDocumentFilter;
import com.lianzheng.notarization.master.entity.*;
import com.lianzheng.notarization.master.enums.*;
import com.lianzheng.notarization.master.form.*;
import com.lianzheng.notarization.master.service.NotarizationMattersQuestionService;
import com.lianzheng.notarization.master.service.UserDocumentService;
import com.lianzheng.notarization.master.service.UserNotarzationCertificateService;
import com.lianzheng.notarization.master.service.UserNotarzationMasterService;
import com.lianzheng.notarization.master.utils.FileStorageByNotaryUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.transaction.NotSupportedException;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@DS("h5")
public class UserNotarzationMasterServiceImpl extends ServiceImpl<UserNotarzationMasterDao, NotarzationMasterEntity> implements UserNotarzationMasterService {

    @Autowired
    private UserNotarzationMasterDao userNotarzationMasterDao;
    @Autowired
    private UserOrderDao userOrderDao;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private UserDocumentDao userDocumentDao;
    @Autowired
    private GeneratePdf generatePdf;
    @Autowired
    private UserDocumentService userDocumentService;

    @Autowired
    private UserDao userDao;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private SysNotarialOfficeDao sysNotarialOfficeDao;
    @Autowired
    private SysUserTokenService sysUserTokenService;
    @Autowired
    private UserNotarzationCertificateDao userNotarzationCertificateDao;
    @Autowired
    private FileTokenUtils fileTokenUtils;
    @Autowired
    private UserNotarzationCertificateService userNotarzationCertificateService;
    @Autowired
    private NotarizationMattersQuestionDao notarizationMattersQuestionDao;
    @Autowired
    private NotarizationMattersQuestionService notarizationMattersQuestionService;

    @Value("${fileUrl}")
    private String fileUrl;


    Path root = Paths.get("uploads");
    Path templatesRoot = Paths.get("templates");
    Path noticesRoot = Paths.get("notices");
    Path notarySigns = Paths.get("notarySigns");

    private static final List<Map<String, Object>> DetailInfoTemplate = new ArrayList<Map<String, Object>>();//模块数组

    static {
        DocumentParam documentParam =ConfigParameterUtil.getDocument();
        Map<String, Object> groupMap = null;//模块map
        List<Map<String, Object>> rowsList = null;//列数组
        Map<String, Object> columnsMap = null;//模块map
        List<MapCommonForm> columnsList = null;//字段对象

        List<Map<String, Object>> selectList = null;
        Map<String, Object> selectMap = null;

        //初始化
        groupMap = new HashMap<String, Object>();
        groupMap.put("group", "公证详情");
        rowsList = new ArrayList<Map<String, Object>>();
        columnsMap = new HashMap<String, Object>();
        columnsMap.put("index", 0);
        columnsList = new ArrayList<MapCommonForm>();


        //读取详情页面字段模板
        if(documentParam.getCsvTemplate()==null){}else
            try (Reader reader = FileContentUtil.getStreamReader("classpath:"+ documentParam.getCsvTemplate())) {
            Iterable<CSVRecord> records = new CSVParser(reader,
                    CSVFormat.EXCEL.withDelimiter(',')
                            .withHeader("group", "rowIndex", "span", "tableName", "key", "label", "type", "option", "disabled", "visable", "required", "ableToComment"));


            for (CSVRecord record : records) {
                int index = (int) record.getRecordNumber();

                if (index <= 1) {
                    continue;
                }
                // 每个组一定要连续的排在一起；
                // option:img的时候是文档类型,select的时候是枚举值的类型;
                // disabled如果为空字符串，意味着可动态修改
                String group = record.get("group");
                int rowIndex = Integer.parseInt(record.get("rowIndex"));
                if (!group.equals(groupMap.get("group"))) {
                    //进入下一组
                    columnsMap.put("columns", columnsList);
                    rowsList.add(columnsMap);

                    groupMap.put("rows", rowsList);
                    DetailInfoTemplate.add(groupMap);

                    groupMap = new HashMap<String, Object>();
                    groupMap.put("group", group);

                    rowsList = new ArrayList<Map<String, Object>>();
                    columnsMap = new HashMap<String, Object>();
                    columnsMap.put("index", 0);
                    columnsList = new ArrayList<MapCommonForm>();
                }
                if (rowIndex != (int) columnsMap.get("index")) {
                    //进入下一行
                    columnsMap.put("columns", columnsList);
                    rowsList.add(columnsMap);

                    columnsMap = new HashMap<String, Object>();
                    columnsMap.put("index", rowIndex);
                    columnsList = new ArrayList<MapCommonForm>();
                }

                selectList = null;

                int span = Integer.parseInt(record.get("span"));
                String tableName = record.get("tableName");
                String key = record.get("key");
                String label = record.get("label");
                String type = record.get("type");
                String option = record.get("option");
                String disabledStr = record.get("disabled");
                String visableStr = record.get("visable");
                String requiredStr = record.get("required");
                String ableToCommentStr = record.get("ableToComment");


                Boolean disabled = disabledStr.isEmpty() ? null : (disabledStr.equals("TRUE") ? true : false);
                Boolean visable = visableStr.isEmpty() ? null : (visableStr.equals("TRUE") ? true : false);
                Boolean required = requiredStr.isEmpty() ? null : (requiredStr.equals("TRUE") ? true : false);
                Boolean ableToComment = ableToCommentStr.isEmpty() ? null : (ableToCommentStr.equals("TRUE") ? true : false);

                switch (type) {
                    case "number":
                    case "money":
                    case "country":
                    case "language":
                    case "simple-area":
                    case "area":
                    case "date":
                    case "datetime":
                    case "textTra":
                    case "questions":
                        columnsList.add(new MapListForm(type, key, label, option, disabled, required, selectList, visable, group, index, tableName, ableToComment, span));
                        break;
                    case "text":
                    case "select":
                        if (option != null && !option.isEmpty()) {
                            selectList = new ArrayList<Map<String, Object>>();
                            if (!option.equals("user")) {
                                Class<?> enumClass = Class.forName(option);
                                Object[] objects = enumClass.getEnumConstants();
                                Method getCode = enumClass.getMethod("getCode");
                                Method getMsg = enumClass.getMethod("getMsg");
                                for (Object obj : objects) {
                                    // 3.调用对应方法，得到枚举常量中字段的值

                                    selectMap = new HashMap<String, Object>();
                                    Object val = getCode.invoke(obj);
                                    selectMap.put("key", option.contains("BooleanEnum") ||
                                            option.contains("SexEnum") ||
                                            option.contains("SignedEnum") ? (Integer.parseInt((String) val)) : val);
                                    selectMap.put("value", getMsg.invoke(obj));
                                    selectList.add(selectMap);
                                }
                            }
                        }


                        columnsList.add(new MapListForm(type, key, label, option, disabled, required, selectList, visable, group, index, tableName, ableToComment, span));
                        break;
                    case "img":
                        columnsList.add(new MapImgForm(type, key, label, option, "", "", group, "", option, index, ableToComment));

                        break;
                    case "fileList":
                        columnsList.add(new MapImgForm(type, key, label, option, "", "", group, "", option, index, ableToComment));
                        break;
                    default:
                        throw new NotSupportedException("Not support the type:" + type);
                }


            }

            columnsMap.put("columns", columnsList);
            rowsList.add(columnsMap);

            groupMap.put("rows", rowsList);
            DetailInfoTemplate.add(groupMap);
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | NotSupportedException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PagesUtils queryCertificateList(Map<String, Object> params) {
        Integer page = Integer.parseInt(params.get("page").toString());
        Integer limit = Integer.parseInt(params.get("limit").toString());
        List<NotarzationMasterForm> notarzationMasterFormList = userNotarzationMasterDao.queryCertificateList(params, (page - 1) * limit, limit);
        int count = userNotarzationMasterDao.countCertificateList(params);
        PagesUtils pageList = new PagesUtils(notarzationMasterFormList, count, limit, page);
        return pageList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void claimCertificate(NotarzationMasterEntity param) {
        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(param.getId());
        LambdaUpdateWrapper<NotarzationMasterEntity> wrapper = Wrappers.<NotarzationMasterEntity>lambdaUpdate()
                .eq(NotarzationMasterEntity::getId, param.getId());
        wrapper.set(NotarzationMasterEntity::getActionBy,param.getActionBy());
//        notarzationMasterEntity.setActionBy(param.getActionBy());
        wrapper.set(NotarzationMasterEntity::getUpdatedTime,new Date());
        wrapper.set(NotarzationMasterEntity::getUpdatedBy,ShiroUtils.getUserId().toString());
        userNotarzationMasterDao.update(notarzationMasterEntity, wrapper);
    }


    //计算应付金额
    public BigDecimal getRealAmount(OrderEntity orderEntity, NotarzationMasterEntity notarzationMasterEntity) {
        PayArrayParam payArrayFrom = ConfigParameterUtil.getPayArray();
        DocumentParam documentParam = ConfigParameterUtil.getDocument();
        BigDecimal realAmount = BigDecimal.ZERO;
        if (notarzationMasterEntity.getApplicantParty().equals(ApplicationPartyEnum.P.getCode())) {
            BigDecimal NotaryAmountP = payArrayFrom.getNotaryAmountP();
            switch (documentParam.getEnvironment()){
                case "zero":
                    Boolean isAgentP = this.isAgentP(notarzationMasterEntity);
                    if(isAgentP){
                        NotaryAmountP = NotaryAmountP.add(payArrayFrom.getNotaryAmountPAdd());
                    }
                    break;
            }
            realAmount = realAmount.add(NotaryAmountP);//计算总价//公证费

        } else {
            realAmount = realAmount.add(payArrayFrom.getNotaryAmountE());//计算总价//公证费
        }

        BigDecimal copyAmount = notarzationMasterEntity.getCopyNumber().subtract(BigDecimal.ONE);
        copyAmount = copyAmount.multiply(payArrayFrom.getCopyAmount());
        if (copyAmount.compareTo(BigDecimal.ZERO) > 0) {
            realAmount = realAmount.add(copyAmount);//计算总价//副本费
        }

        BigDecimal translationAmount = orderEntity.getTranslationAmount();
        if (translationAmount.compareTo(BigDecimal.ZERO) > 0) {
            realAmount = realAmount.add(translationAmount);//计算总价//翻译费
        }

        BigDecimal logisticsAmount = orderEntity.getLogisticsAmount();
        if (logisticsAmount.compareTo(BigDecimal.ZERO) > 0) {
            realAmount = realAmount.add(logisticsAmount);//计算总价//快递费
        }

        BigDecimal serviceAmount = orderEntity.getServiceAmount();
        if (serviceAmount.compareTo(BigDecimal.ZERO) > 0) {
            realAmount = realAmount.add(serviceAmount);//计算总价//公证服务费
        }

        int sentToStraitsExchangeFoundation = notarzationMasterEntity.getSentToStraitsExchangeFoundation();
        String sentToStraitsExchangeFoundationType = notarzationMasterEntity.getExpressModeToSEF();
        if (sentToStraitsExchangeFoundation > 0 && sentToStraitsExchangeFoundationType != null && sentToStraitsExchangeFoundationType.equals(MailModeSEFEnum.expressModeToSEFP.getCode())) {
            realAmount = realAmount.add(payArrayFrom.getModeToSEFP());//计算总价//寄台湾海基快递费
        }
        if (sentToStraitsExchangeFoundation > 0 && sentToStraitsExchangeFoundationType != null && sentToStraitsExchangeFoundationType.equals(MailModeSEFEnum.expressModeToSEFS.getCode())) {
            realAmount = realAmount.add(payArrayFrom.getModeToSEFS());//计算总价//寄台湾海基快递费
        }

        String hasMoreCert = notarzationMasterEntity.getHasMoreCert();
        if (hasMoreCert.equals("1")) {
            realAmount = realAmount.add(payArrayFrom.getDoubleCertificate());//计算总价//双证译文文本相符
            realAmount = realAmount.add(copyAmount);//计算总价//双证译文文本相符副本费
        }

        return realAmount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refuseCertificate(Map<String, Object> param) {
        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(param.get("id").toString());
        notarzationMasterEntity.update(param);
        notarzationMasterEntity.setStatus(NotarzationStatusEnum.REJECTED.getCode());
        notarzationMasterEntity.setUpdatedTime(new Date());
        notarzationMasterEntity.setUpdatedBy(ShiroUtils.getUserId().toString());

        userNotarzationMasterDao.updateById(notarzationMasterEntity);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitCertificate(Map<String, Object> param) {
        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(param.get("id").toString());
        notarzationMasterEntity.update(param);
        notarzationMasterEntity.setProcessStatus(ProcessStatusEnum.APPROVING.getCode());
        notarzationMasterEntity.setUpdatedTime(new Date());
        notarzationMasterEntity.setUpdatedBy(ShiroUtils.getUserId().toString());

        userNotarzationMasterDao.updateById(notarzationMasterEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void directorRefuseCertificate(Map<String, Object> param) {
        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(param.get("id").toString());
        notarzationMasterEntity.update(param);
        notarzationMasterEntity.setProcessStatus(ProcessStatusEnum.DOING.getCode());
        notarzationMasterEntity.setUpdatedTime(new Date());
        notarzationMasterEntity.setUpdatedBy(ShiroUtils.getUserId().toString());

        userNotarzationMasterDao.updateById(notarzationMasterEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void directorPassCertificate(Map<String, Object> param) {
        String id = param.get("id").toString();
        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(id);
        notarzationMasterEntity.update(param);
        notarzationMasterEntity.setStatus(NotarzationStatusEnum.PENDINGPICKUP.getCode());
        notarzationMasterEntity.setProcessStatus(ProcessStatusEnum.GENERATINGCERT.getCode());
        notarzationMasterEntity.setUpdatedTime(new Date());
        notarzationMasterEntity.setUpdatedBy(ShiroUtils.getUserId().toString());


        DocumentParam documentParam = ConfigParameterUtil.getDocument();
        List<NotarzationCertificateEntity> notarzationCertificateEntities= null;
        switch (documentParam.getEnvironment()){
            case "zero":  //求真获取手输入编号
                //插入公证书编号
                if(param.get("certificateIds")==null){
                    throw new  RuntimeException("请输入公证编号");
                }
                List<String> certificateIds = (List<String>)param.get("certificateIds");
                notarzationCertificateEntities =new ArrayList<>();
                for(String item: certificateIds){
                    NotarzationCertificateEntity checkItem = userNotarzationCertificateService.getOne(
                            new QueryWrapper<NotarzationCertificateEntity>().eq("notarialCertificateNo", item)
                    );
                    if(checkItem!=null){
                        throw new RuntimeException("该公证编号已存在");
                    }
                    notarzationCertificateEntities.add(new  NotarzationCertificateEntity(UUID.randomUUID().toString(),id,item));
                }
                userNotarzationCertificateService.saveBatch(notarzationCertificateEntities);
                break;
            case "one":
                notarzationMasterEntity.setStatus(NotarzationStatusEnum.COMPLETED.getCode());
                notarzationMasterEntity.setProcessStatus(ProcessStatusEnum.COMPLETED.getCode());
                break;
            default: //非求真  系统生成id

                break;
        }

        userNotarzationMasterDao.updateById(notarzationMasterEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pickUpCertificate(Map<String, Object> param) {
        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(param.get("id").toString());
        notarzationMasterEntity.update(param);
        notarzationMasterEntity.setStatus(NotarzationStatusEnum.COMPLETED.getCode());
        notarzationMasterEntity.setProcessStatus(NotarzationStatusEnum.COMPLETED.getCode());
        notarzationMasterEntity.setUpdatedTime(new Date());
        notarzationMasterEntity.setUpdatedBy(ShiroUtils.getUserId().toString());

        //特殊处理,发送地址
        List<String> sentToAddress = (List<String>) param.get("sentToProvince");
        param.remove("sentToProvince");

        if (sentToAddress != null) {
            param.put("sentToProvince", sentToAddress.size() > 0 ? sentToAddress.get(0) : "");
            param.put("sentToCity", sentToAddress.size() > 1 ? sentToAddress.get(1) : "");
            param.put("sentToArea", sentToAddress.size() > 2 ? sentToAddress.get(2) : "");
        }

        //更新订单表
        OrderEntity orderEntity = userOrderDao.selectOne(
                new QueryWrapper<OrderEntity>().eq("id", notarzationMasterEntity.getOrderId()).eq("isDeleted", 0)
        );
        orderEntity.update(param);//需要更新方法里加
        orderEntity.setUpdatedTime(new Date());
        orderEntity.setUpdatedBy(ShiroUtils.getUserId().toString());

        userOrderDao.updateById(orderEntity);
        userNotarzationMasterDao.updateById(notarzationMasterEntity);
    }

    public List<MapCommonForm>  parseQuestions(List<NotarizationMattersQuestionEntity> notarizationMattersQuestionEntities){
        List<MapCommonForm> questions  =new ArrayList<>();
        int i=0;
        for(NotarizationMattersQuestionEntity item:notarizationMattersQuestionEntities){
            MapCommonForm mapCommonForm = new MapListForm("questions", item.getId(), "其他问询", item, Boolean.FALSE, Boolean.FALSE, null, Boolean.TRUE, "其他问询", i++, "notarization_matters_question", Boolean.FALSE, 24);
            questions.add(mapCommonForm);
        }
        return questions;
    }

    @Override
    public Map<String, List<MapCommonForm>> parseDocuments(List<DocumentForm> documentFormList, Map<String, Object> param) {
        Boolean disabled = isDisabled(param,ShiroUtils.getUserId());
        Map<String, IDocumentFilter> filters = DocumentFilterConfig.getFilters();

        //文件归类
        Map<String, List<MapCommonForm>> docCategories = new HashMap<String, List<MapCommonForm>>();
        for (String category :
                filters.keySet()) {
            docCategories.put(category, new ArrayList<>());
        }
        Optional<DocumentForm> signedImg = documentFormList.stream().filter(d->d.getCategoryCode().toLowerCase(Locale.ROOT).equals(DocumentCategoryCode.SIGN.getCode().toLowerCase(Locale.ROOT))).findFirst();

        for (int i = 0; i < documentFormList.size(); i++) {
            DocumentEntity doc = documentFormList.get(i);
            for (String category :
                    docCategories.keySet()) {
                IDocumentFilter filter = filters.get(category);
                List<MapCommonForm> list = docCategories.get(category);
                MapCommonForm map = filter.appendDocumentMapEntity(list, doc, i,(Long)param.get("notarialOfficeId"));

                if(map!=null){
                    if(doc.getCategoryCode().contains("PDF")//非公证书和拟稿纸修改文件名
                            && !doc.getCategoryCode().equals(DocumentCategoryCode.PDF_NOTARIZATION.getCode())
                            && !doc.getCategoryCode().equals(DocumentCategoryCode.PDF_DRAFT.getCode())
                            && !doc.getCategoryCode().equals(DocumentCategoryCode.PDF_NOTARIZATION_CERT.getCode())
                            && !doc.getCategoryCode().equals(DocumentCategoryCode.PDF_DRAFT_CERT.getCode())
                            ){
                        map.setType("pdf");

                        if(!doc.getCategoryCode().equals(DocumentCategoryCode.PDF_PAY_NOTICE.getCode())&&!doc.getCategoryCode().equals(DocumentCategoryCode.PDF_HOME_ATTORNEY.getCode())){
                            boolean existedSignedImg = (signedImg != null && signedImg.isPresent());
                            ((MapImgForm)map).setSignedText(existedSignedImg ? "已签字" : "待签字");
                        }
                    }
                    else if(doc.getCategoryCode().equals(DocumentCategoryCode.PDF_NOTARIZATION.getCode())
                            || doc.getCategoryCode().equals(DocumentCategoryCode.PDF_DRAFT.getCode())
                            || doc.getCategoryCode().equals(DocumentCategoryCode.PDF_NOTARIZATION_CERT.getCode())
                            || doc.getCategoryCode().equals(DocumentCategoryCode.PDF_DRAFT_CERT.getCode())
                            ){
                        map.setType("pdf");
                    }
                    if(doc.getCategoryCode().equals(DocumentCategoryCode.PDF_RECEIPT.getCode())){
                        Object signed = param.get("signedReceipt");
                        if(signed!=null){
                            boolean existedSignedReceiptImg = ((int)signed) == 0 ? false : true;
                            ((MapImgForm)map).setSignedText(existedSignedReceiptImg ? "已签字" : "待签字");
                        }
                    }
                    map.setAbleToComment(!disabled);//判断文件是否可修改
                }
            }
        }
        return docCategories;
    }

    private static final boolean isDisabled(Map<String, Object> param, Long userId){

        String status = param.get("status").toString();
        Object actionBy = param.get("actionBy");

        Boolean disabled = (actionBy == null) || !actionBy.toString().equals(userId.toString()) || (!status.equals(NotarzationStatusEnum.PENDINGAPPROVED.getCode())
                && !status.equals(NotarzationStatusEnum.PENDINGCONFIRMED.getCode())
        );

        return disabled;
    }

    private Boolean overwriteDisabled(String key,Map<String, Object> param, MapCommonForm colObj, Long userId){
        String status = param.get("status").toString();
        Object actionBy = param.get("actionBy");

        Boolean disabled = isDisabled(param, userId);
        MapListForm listForm = (colObj instanceof MapListForm) ? (MapListForm) colObj : null;
        if(listForm ==null){
            return disabled;
        }
        if(listForm.getDisabled() !=null){
            return listForm.getDisabled();
        }

        listForm.setDisabled(disabled);
        //在允许编辑的时候，再根据是否需要邮寄判断邮寄信息是否可编辑
        Boolean isSend = (Integer) param.get("isSend") == 1;

        switch(key){
            case "sendtToName":
            case "sentToPhone":
            case "sentToProvince":
            case "sentToAddress":
            case "logisticsAmount":
                if(disabled){
                    return disabled;
                }

                listForm.setDisabled(!isSend);
                listForm.setRequired(isSend);
                break;
            case "translateTo":
            case "hasMoreCert":
            case "requiredVerification":
            case "translationAmount":
                if(disabled){
                    return disabled;
                }
                //在允许编辑的时候，再根据使用地是否是中国决定是否可编辑
                Boolean isChina = param.get("usedToCountry").toString().contains("中国");

                listForm.setDisabled(isChina);
                listForm.setRequired(!isChina);
                break;
            case "sentToStraitsExchangeFoundation":
            case "expressModeToSEF":
                if(disabled){
                    return disabled;
                }
                //在允许编辑的时候，再根据使用地是否是中国台湾决定是否可编辑
                Boolean isTaiwan = (param.get("usedToCountry").toString().equals("中国台湾"));

                listForm.setDisabled(!isTaiwan);
                listForm.setRequired(isTaiwan);
                break;
            case "logisticsCompany":
            case "logisticsNumber":
                disabled = (actionBy == null) || !actionBy.toString().equals(userId.toString()) || (!status.equals(NotarzationStatusEnum.PENDINGPICKUP.getCode()));
                if(disabled){
                    listForm.setDisabled(disabled);
                    listForm.setRequired(!disabled);
                }
                else{
                    listForm.setDisabled(!isSend);
                    listForm.setRequired(isSend);
                }
                break;
            case "staffRemark":
                disabled = (actionBy == null) || !actionBy.toString().equals(userId.toString()) || (status.equals(NotarzationStatusEnum.COMPLETED.getCode()));
                listForm.setDisabled(disabled);
                break;
            default:
                break;
        }

        return listForm.getDisabled();
    }

    /**
     * 拼装后台前端所需的数据格式
     *
     * @param param
     * @return
     */
    @Override
    public List<Map<String, Object>> editFrom(Map<String, Object> param) throws NotSupportedException, NoSuchMethodException, ClassNotFoundException, InvocationTargetException, IllegalAccessException {

        //判断是否接入求真
        DocumentParam documentParam = ConfigParameterUtil.getDocument();

        //如果 需要代理  加入代理人信息
        if((int)param.get("isAgent")==1){
            Map<String, Object> agentMap = userDao.getAgentInfo(param.get("userId").toString());
            param.putAll(agentMap);
        }

        List<Map<String, Object>> groupList = new ArrayList<Map<String, Object>>();//模块数组
        Map<String, Object> groupMap = null;//模块map
        List<Map<String, Object>> rowsList = null;//列数组
        Map<String, Object> columnsMap = null;//模块map
        List<MapCommonForm> columnsList = null;//字段对象

        List<Map<String, Object>> selectList = null;
        Map<String, Object> selectMap = null;
        Long userId = ShiroUtils.getUserId();

        String masterId = param.get("id").toString();
        String applicantParty = param.get("applicantParty").toString();//申请主体
        String status = param.get("status").toString();
        Object actionBy = param.get("actionBy");



        //检索该公证下的所有文件
        List<DocumentForm> documentFormList = userDocumentService.getlist(masterId);
        Map<String, List<MapCommonForm>> docCategories = this.parseDocuments(documentFormList, param);

        //获取所有其他询问
        List<NotarizationMattersQuestionEntity> notarizationMattersQuestionEntitys = notarizationMattersQuestionDao.selectList(
                new QueryWrapper<NotarizationMattersQuestionEntity>().eq("notarizationId",masterId).orderByAsc("sort")
        );
        List<MapCommonForm> questions = this.parseQuestions(notarizationMattersQuestionEntitys);

        //填充值
        for (Map<String, Object> group :
                DetailInfoTemplate) {
            String groupText = group.get("group").toString();

            //公证拟稿纸和公证书不同时出现
            if(status.equals(NotarzationStatusEnum.COMPLETED.getCode())
                    || status.equals(NotarzationStatusEnum.PENDINGPICKUP.getCode())){
                if(groupText.equals("公证拟稿")){
                    //已完成的时候，不展示公证拟稿纸
                    continue;
                }
            }
            else{
                if(groupText.equals("公证结果")){
                    //非已完成的时候，不展示公证书
                    continue;
                }
                else if(groupText.equals("快递信息")){
                    //非已完成的时候，不展示快递信息
                    continue;
                }
            }


            switch (documentParam.getEnvironment()){
                case "zero":
                    //接入求真系统去除以下三个模块
                    if(groupText.equals("公证拟稿")||groupText.equals("公证材料")||groupText.equals("公证结果")){
                        continue;
                    }
                    break;
                default:if(groupText.equals("公证拟稿")||groupText.equals("公证材料")||groupText.equals("公证结果")){
                    continue;
                }
                    break;
            }



            if((int)param.get("isAgent")==0&&groupText.equals("代理人信息")){
                continue;
            }

            groupMap = new HashMap<String, Object>();
            groupMap.put("group", groupText);
            rowsList = new ArrayList<Map<String, Object>>();

            for (Map<String, Object> row :
                    (List<Map<String, Object>>) (group.get("rows"))) {
                columnsMap = new HashMap<String, Object>();
                columnsMap.put("index", row.get("index"));
                columnsList = new ArrayList<MapCommonForm>();

                for (MapCommonForm col :
                        (List<MapCommonForm>) (row.get("columns"))) {
                    MapCommonForm colObj = col.deepClone();

                    //赋值
                    String key = colObj.getKey();
                    Boolean disabled = overwriteDisabled(key, param, colObj, userId);
                    Boolean ableToComment = colObj.getAbleToComment();
                    if (ableToComment == null && disabled != null) {
                        colObj.setAbleToComment(!disabled);
                    }

                    switch (colObj.getType()) {
                        case "text":
                            Object value = colObj.getValue();
                            if (key.contains("Time")) {

                                Object dateV = param.get(colObj.getKey());
                                String format = "yyyy-MM-dd HH:mm:ss";
                                String text = DateUtils.format((Date) dateV, format);
                                colObj.setValue(text);
                            } else {
                                colObj.setValue(param.get(colObj.getKey()));
                            }
                            if(key.equals("trusteeBirthday")&&documentParam.getEnvironment().equals("zero")){
                                colObj.setType("date");
                            }
                            if(key.equals("sentToProvinceJh")){ //中新独有的字段  其他项目不可使用   解决与中新区域配置不一致的问题
                                Boolean isSend = (Integer) param.get("isSend") == 1;
                                String  sentToProvinceJh =isSend ? (String)param.get("sentToProvince")+(String)param.get("sentToCity")+(String)param.get("sentToArea"):null;
                                colObj.setValue(sentToProvinceJh);
                            }
                            break;
                        case "number":
                        case "money":
                        case "country":
                        case "language":
                        case "simple-area":
                        case "textTra":
                            Object v = param.get(key);
                            colObj.setValue(v);
                            break;
                        case "area":
                            String[] vArray = null;
                            switch (key){
                                case "sentToProvince":
                                    vArray = new String[]{(String)param.get("sentToProvince"),(String)param.get("sentToCity"),(String)param.get("sentToArea")};
                                    break;
                                default:
                                    throw new NotSupportedException("Not support the key now:"+key);
                            }
                            colObj.setValue(vArray);
                            break;
                        case "select":
                            Object val = colObj.getValue();
                            if (val != null && val.toString().equals("user")) {
                                List<SysUserEntity> sysUserEntityList = sysUserService.getUser("",null);
                                selectList = new ArrayList<Map<String, Object>>();
                                for (SysUserEntity item : sysUserEntityList) {
                                    selectMap = new HashMap<String, Object>();
                                    selectMap.put("key", item.getId());
                                    selectMap.put("value", item.getRealName());
                                    selectList.add(selectMap);
                                }
                                ((MapListForm) colObj).setSelectList(selectList);
                            }
                            colObj.setValue(param.get(colObj.getKey()));

                            //昆山环境其他公证事项可编辑切换
                            if(key.equals("notarzationTypeCode")&&documentParam.getEnvironment().equals("zero")){
                                if(isDisabled(param,userId)){
                                    break;
                                }
                                if(OtherNotarizationTypeEnum.getEnumCode(colObj.getValue().toString())!=null){
                                    ((MapListForm) colObj).setDisabled(Boolean.FALSE);
                                }
                            }
                            break;
                        case "date":
                        case "datetime":
                            Object dateV = param.get(colObj.getKey());
                            String format = colObj.getType().equals("date") ? "yyyy-MM-dd" : "yyyy-MM-dd HH:mm:ss";
                            String text = DateUtils.format((Date) dateV, format);
                            colObj.setValue(text);
                            break;
                        case "img":
                            for (DocumentEntity doc :
                                    documentFormList) {
                                if (doc.getCategoryCode().equals(colObj.getValue())) {
                                    if(DocumentCategoryCode.HEAD.getCode().equals(colObj.getValue())){
                                        colObj.setType("IMG");//如果头像 将类型置为IMG  前端需要
                                    }
                                    String fileName = doc.getFileName() + doc.getFileExt();
                                    String uuid = userDocumentService.getToken((Long)param.get("notarialOfficeId"),doc.getUploadedAbsolutePath());
                                    MapImgForm imgForm = (MapImgForm) colObj;
                                    imgForm.setToken(uuid);
                                    imgForm.setCategoryCode(doc.getCategoryCode());
                                    imgForm.setDocId(doc.getId());
                                    imgForm.setFileName(fileName);
                                    imgForm.setValue(doc.getUploadedAbsolutePath());
                                    //如果代理单子  用户信息没有头像
                                    if((int)param.get("isAgent")==1&&groupText.equals("用户信息")){
                                        imgForm.setValue("");
                                    }
                                    break;
                                }
                            }

                            break;
                        case "fileList":
                            String label = colObj.getLabel();
                            List<MapCommonForm> documents = docCategories.get(label);
//                            documents.add(docCategories.get("身份证明材料").get(0));
                            for (MapCommonForm doc :
                                    documents) {
                                if (groupMap.get("group").equals("证明材料")||groupMap.get("group").equals("签字材料")) {
                                    if(documentParam.getEnvironment().equals("zero")){ //昆山环境
                                        doc.setAbleToComment(!disabled);
                                    }
                                }
                                columnsList.add(doc);
                            }
                            break;
                        case "questions":
                            //其他问询外最外层加一个字段判断是否可编辑
                            groupMap.put("disabled",disabled);
                            for(MapCommonForm doc:questions){
                                doc.setAbleToComment(!disabled);
                                ((MapListForm)doc).setDisabled(disabled);
                                columnsList.add(doc);
                            }
                            break;
                        default:
                            throw new NotSupportedException("Not support the type:" + colObj.getType());
                    }
                    //判断代理人企业和个人的页面展示
                    if(applicantParty.equals("P")){
                        switch (colObj.getKey()){
                            case "companyName":
                            case "legalRepresentative":
                            case "legalStatus":
                            case "socialCreditCode":
                            case "companyAddress":
                            case "companyType":
                            case "registerDate":
                                colObj.setVisible(Boolean.FALSE);
                                ((MapListForm) colObj).setRequired(Boolean.FALSE);
                        }
                    }else {
                        switch (colObj.getKey()){
                            case "phone":
                            case "realName":
                            case "idCardType":
                            case "gender":
                            case "idCardNo":
                            case "birth":
                            case "idCardAddress":
                                colObj.setVisible(Boolean.FALSE);
                                ((MapListForm) colObj).setRequired(Boolean.FALSE);
                        }
                    }

                    if (!colObj.getType().equals("fileList")&&!colObj.getType().equals("questions")) {
                        columnsList.add(colObj);
                    }
                }
                if (columnsList.size() > 0) {
                    columnsMap.put("columns", columnsList);
                    rowsList.add(columnsMap);
                }
            }
            groupMap.put("rows", rowsList);
            groupList.add(groupMap);

        }

        return groupList;
    }


    /**
     * 公共的文件生成
     * param  notarzationMasterEntity 公证信息
     * templates  需要生成的模板
     */
    @Override
    public void pdfGenerate(NotarzationMasterEntity notarzationMasterEntity) throws Exception {
        //公证类型
        String notarzationTypeCode = notarzationMasterEntity.getNotarzationTypeCode();
        String status = notarzationMasterEntity.getStatus();
        //公证的信息
        //公证人
        SysUserEntity sysUserEntity = sysUserService.queryByUserId(notarzationMasterEntity.getActionBy());
        File file = new File(this.notarySigns + "/" + sysUserEntity.getRealName() + ".png");
        if (!file.exists()) {
            throw new RuntimeException("没有公证员签名");
        }



        //读取模板并生成文件
        DocumentParam documentParam = ConfigParameterUtil.getDocument();
        String generateTemplates =documentParam.getGenerateTemplates();
        Reader reader = FileContentUtil.getStreamReader("classpath:generate-templates.csv");
        Iterable<CSVRecord> records = new CSVParser(reader,
                    CSVFormat.EXCEL.withDelimiter(',')
                            .withHeader("code", "path", "className", "suit", "category"));
        for(CSVRecord record:records){
            //非指定模版跳过
            if(!generateTemplates.contains(record.get("code"))||!record.get("category").equals("signFile")){
                continue;
            }
            //文件模板适用类型与公证类型不匹配跳过
            if(record.get("suit")!=null&&!record.get("suit").equals("")){

                String[] arry = record.get("suit").split("-");
                List<String> suitList = Arrays.asList(arry);
                if(!suitList.contains(notarzationTypeCode)){
                    continue;
                }

            }
            System.out.println(record.get("className"));
            //生成文件
            Class<?> c = Class.forName(record.get("className"));
            Constructor<?> con = c.getConstructor(NotarzationMasterEntity.class);
            Object o = con.newInstance(notarzationMasterEntity);
            switch (documentParam.getEnvironment()){
                case "one":
                case "zero":
                    if(status.equals(NotarzationStatusEnum.PENDINGAPPROVED.getCode())||
                            status.equals(NotarzationStatusEnum.PENDINGCONFIRMED.getCode())||
                            status.equals(NotarzationStatusEnum.PENDINGPAYMENT.getCode())){
                        Method method=c.getMethod("Generatefile",String.class);
                        method.invoke(o,record.get("path"));
                    }else {
                        Method method=c.getMethod("GeneratefileFinal");
                        method.invoke(o);
                    }
                    break;
            }

            //询问笔录生成后  结束不再执行
            if(record.get("path").contains("询问笔录")){
                break;
            }
        }

    }


    /**
     * 生成询问笔录公共的字段
     *
     * @param notarzationMasterEntity 公证信息
     * @return
     */
    @Override
    public ConcurrentHashMap<String, Map<String, Object>> getPublicParam(NotarzationMasterEntity notarzationMasterEntity) {
        //更改公证处
        //订单信息
        CountryParam countryParam =ConfigParameterUtil.getCountry();//获取国家参数
        OrderEntity orderEntity = userOrderDao.selectById(notarzationMasterEntity.getOrderId());
        SysUserEntity sysUserEntity = sysUserService.queryByUserId(notarzationMasterEntity.getActionBy());//获取公证员
        ConcurrentHashMap<String, Map<String, Object>> hashMap = new ConcurrentHashMap<>();
        String question =null;
        List<String> url = null;
        List<String> fileNameList = null;
        PdfVarEntity pdfVarEntity = null;
        List<Map<String, Object>> tableParam = null;//表格list往里面加入tableMap对象即可
        Map<String, Object> tableMap = null;//表格每一行对应一个tableMap

        //代理被询问人展示代理人信息
        if(notarzationMasterEntity.getIsAgent()==1){
            UserEntity userEntity = userDao.selectById(notarzationMasterEntity.getUserId());
            pdfVarEntity = new PdfVarEntity("text", userEntity.getRealName(), null);
            hashMap.put("realName", pdfVarEntity.getMap());
            String gender = userEntity.getGender()==1? "男" : "女";
            pdfVarEntity = new PdfVarEntity("text", gender, null);
            hashMap.put("gender", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", DateUtils.format(userEntity.getBirth()), null);
            hashMap.put("birth", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", userEntity.getPhone(), null);
            hashMap.put("phone", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", userEntity.getIdCardType() + "：" + userEntity.getIdCardNo(), null);
            hashMap.put("idCardNo", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", userEntity.getIdCardAddress(), null);
            hashMap.put("idCardAddress", pdfVarEntity.getMap());

            if(notarzationMasterEntity.getApplicantParty().equals(ApplicationPartyEnum.P.getCode())){
                gender = notarzationMasterEntity.getGender().equals("1") ? "男" : "女";
                String dateStr =  DateUtils.format(notarzationMasterEntity.getBirth(),"，yyyy年MM月dd日出生，");
                String agentQ = "不是，我替" + notarzationMasterEntity.getRealName() + "办理公证。<br>问：被代理人的基本情况？<br>答：" +
                        notarzationMasterEntity.getRealName() + "，" + gender + dateStr + "证件号码：" + notarzationMasterEntity.getIdCardNo() + "，住址：" + notarzationMasterEntity.getIdCardAddress() + "。";
                pdfVarEntity = new PdfVarEntity("text", agentQ, null);
                hashMap.put("agent", pdfVarEntity.getMap());
            }else {
                String agentQ = "不是，我替" + notarzationMasterEntity.getRealName() + "办理公证。<br>问：被代理人的基本情况？<br>答：企业名称：" +
                        notarzationMasterEntity.getRealName() + "，企业住所：" + notarzationMasterEntity.getIdCardAddress() + "，企业社会信用统一代码：" + notarzationMasterEntity.getSocialCreditCode() + "。";
                pdfVarEntity = new PdfVarEntity("text", agentQ, null);
                hashMap.put("agent", pdfVarEntity.getMap());
            }

        }else{
            pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getRealName(), null);
            hashMap.put("realName", pdfVarEntity.getMap());
            String gender = notarzationMasterEntity.getGender().equals("1") ? "男" : "女";
            pdfVarEntity = new PdfVarEntity("text", gender, null);
            hashMap.put("gender", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", DateUtils.format(notarzationMasterEntity.getBirth()), null);
            hashMap.put("birth", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getPhone(), null);
            hashMap.put("phone", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getIdCardType() + "：" + notarzationMasterEntity.getIdCardNo(), null);
            hashMap.put("idCardNo", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getIdCardAddress(), null);
            hashMap.put("idCardAddress", pdfVarEntity.getMap());
            String agentQ = "是。";
            pdfVarEntity = new PdfVarEntity("text", agentQ, null);
            hashMap.put("agent", pdfVarEntity.getMap());
        }

        //添加其他问询
        question = "";
        List<NotarizationMattersQuestionEntity> notarizationMattersQuestionEntities =  notarizationMattersQuestionDao.selectList(
                new QueryWrapper<NotarizationMattersQuestionEntity>().eq("notarizationId",notarzationMasterEntity.getId())
        );
        for(NotarizationMattersQuestionEntity item : notarizationMattersQuestionEntities){
            question = question + "<br>问：" + item.getQuestion();
            question = question + "<br>答：" + item.getAnswer();
        }
        pdfVarEntity = new PdfVarEntity("text", question, null);
        hashMap.put("questions", pdfVarEntity.getMap());

        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getUsedToCountry(), null);
        hashMap.put("usedToCountry", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", "", null);
        hashMap.put("usedToProvince", pdfVarEntity.getMap());
        if (countryParam.getTranslateLanguage().contains(notarzationMasterEntity.getUsedToCountry())) {
            pdfVarEntity = new PdfVarEntity("text", "", null);
            hashMap.put("translateTo", pdfVarEntity.getMap());
        } else {
            question = "<br>问：翻译成何种文字？<br>" + "答：" + notarzationMasterEntity.getTranslateTo()
                    + "<br>问：我们也是委托翻译社翻译，翻译费发票由翻译公司开具，你是否同意？<br>"
                    +"答：好的，我同意的。";
            pdfVarEntity = new PdfVarEntity("text", question, null);
            hashMap.put("translateTo", pdfVarEntity.getMap());
        }
//        pdfVarEntity = new PdfVarEntity("text", NotarizationTypeEnum.getEnumMsg(notarzationMasterEntity.getNotarzationTypeCode()), null);
//        hashMap.put("notarzationTypeCode", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getUsedFor(), null);
        hashMap.put("usedFor", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getCopyNumber(), null);
        hashMap.put("copyNumber", pdfVarEntity.getMap());

        question = "";
        if(countryParam.getCertCountry().contains(notarzationMasterEntity.getUsedToCountry())){
            question =question + "<br>问：根据美国、韩国、奥地利、哈萨克斯坦、土库曼斯坦、乌兹别克斯坦等国的要求，每份公证书译文还必须公证证明译文与原件相符，相应的公证费和译文费也会增加，您是否按规定办理？<br>";
            question =question + "答：我知道了，同时申办该公证。";
        }
        if(notarzationMasterEntity.getRequiredVerification()==1){
            question =question + "<br>问：是否需要做外事认证？<br>";
            String requiredVerification = notarzationMasterEntity.getRequiredVerification() == 0 ? "否。" : "是，单认证: 由外交部委托的地方外办（江苏省外事办）或外交部领事司办理领事认证。双认证:一般应先由外交部委托的地方外办（江苏省外事办）或外交部领事司办理领事认证，再由文书使用国驻华使领馆办理领事认证。";
            question =question + "答："+ requiredVerification;
        }
        pdfVarEntity = new PdfVarEntity("text", question, null);
        hashMap.put("requiredVerification", pdfVarEntity.getMap());

        if(notarzationMasterEntity.getUsedToCountry().equals("中国台湾")){
            //2.17日修改逻辑注释
//            question = "<br>问：是否需要寄台湾海基会？<br>";
//            int sentToStraitsNum = notarzationMasterEntity.getSentToStraitsExchangeFoundation();//寄台寄会的分数
//            String sentToStraitsExchangeFoundation = notarzationMasterEntity.getSentToStraitsExchangeFoundation() > 0 ? "需要" + sentToStraitsNum + "份" : "不需要";
//            question =question +  "答：" +sentToStraitsExchangeFoundation;
//            if (sentToStraitsNum > 0) {//邮寄有份数显示该问题
//                String expressModeToSEF = notarzationMasterEntity.getExpressModeToSEF();
//                String expressModeToSEFType = expressModeToSEF != null && expressModeToSEF.equals("P") ? "普通挂号邮寄" : "特快专递";
//                String expressModeToSEFQuestion = "<br>问：根据《汪辜协议》你拿到的公证书要到台湾海基会核对，核对后才能在台湾使用。而寄到海基会的副本需要你选择邮寄方式。现在把副本寄到台湾海基会所需的时间和邮寄审查费用跟你说一下，普通挂号邮寄到台湾大约40天左右，费用是65元，特快专递到台湾大约20天左右，费用120元。你选择普通挂号邮寄还是特快专递？<br>" +
//                        "答：" + expressModeToSEFType;
//                question =question + expressModeToSEFQuestion;
//            }

            String expressModeToSEF = notarzationMasterEntity.getExpressModeToSEF();
            String expressModeToSEFType = expressModeToSEF != null && expressModeToSEF.equals("P") ? "普通挂号邮寄" : "特快专递";
            String expressModeToSEFQuestion = "<br>问：根据《汪辜协议》，你拿到的公证书要到台湾海基会核对，核对后才能在台湾使用。而寄到海基会的副本需要你选择邮寄方式。现在把副本寄到台湾海基会所需的时间和邮寄审查费用跟你说一下，普通挂号邮寄到台湾大约40天左右，费用是65元，特快专递到台湾大约20天左右，费用120元。你选择普通挂号邮寄还是特快专递？<br>" +
                    "答：" + expressModeToSEFType;
            question =expressModeToSEFQuestion;

            pdfVarEntity = new PdfVarEntity("text", question, null);
            hashMap.put("sentToStraitsExchangeFoundation", pdfVarEntity.getMap());
        } else {
            pdfVarEntity = new PdfVarEntity("text", "", null);
            hashMap.put("sentToStraitsExchangeFoundation", pdfVarEntity.getMap());
        }

        pdfVarEntity = new PdfVarEntity("text", orderEntity.getIsSend() == 0 ? "自取" : "邮寄", null);
        hashMap.put("isSend", pdfVarEntity.getMap());

        pdfVarEntity = new PdfVarEntity("text", DateUtils.format(notarzationMasterEntity.getCreatedTime(),"yyyy年MM月dd日"), null);
        hashMap.put("createdTime", pdfVarEntity.getMap());

        pdfVarEntity = new PdfVarEntity("text", sysUserEntity.getRealName(), null);
        hashMap.put("actionName", pdfVarEntity.getMap());

        return hashMap;
    }



    @Override
    public List<MapCommonForm> previewNotarization(String id) throws Exception{
        List<MapCommonForm> mapCommonFormList = new ArrayList<>();
        List<String> filelist =new ArrayList<>();
        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(id);
        SysUserEntity sysUserEntity = sysUserService.getById(notarzationMasterEntity.getActionBy());
        String sourceFile = this.noticesRoot + "/PDF_NOTARIZATION_" + notarzationMasterEntity.getProcessNo() + ".docx";//公证书源文件
        String targetFile = this.noticesRoot + "/PDF_NOTARIZATION_PREVIEW_" + notarzationMasterEntity.getProcessNo() + ".pdf";//预览公证书

        Map<String, Object> map = new HashMap<>();
        map.put("imgSign", this.notarySigns+ "/" + sysUserEntity.getRealName() + ".png");
        map.put("imgStamp",  this.notarySigns+"/江苏省昆山市公证处.png");//todo  要配置
//        map.put("certificateId", "{此处为证书编号}");

        //生成公证书
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
        Map<String,String> mergeMap =new HashMap<>();//替换公证书文字
        mergeMap.put("签名","&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp");
        mergeMap.put("盖章","&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp");
        mergeMap.put("落款时间",df.format(new Date()));
        generatePdf.generateCertificate(sourceFile, targetFile, map,mergeMap);
        filelist.add(targetFile);

        //双证
        String targetFileCert = this.noticesRoot + "/PDF_NOTARIZATION_CERT_PREVIEW_" + notarzationMasterEntity.getProcessNo() + ".pdf";//预览公证书
        if(notarzationMasterEntity.getHasMoreCert().equals("1")){
            sourceFile = this.noticesRoot + "/PDF_NOTARIZATION_CERT_" + notarzationMasterEntity.getProcessNo() + ".docx";//公证书源文件

            //生成公证书
            generatePdf.generateCertificate(sourceFile, targetFileCert, map,mergeMap);
            filelist.add(targetFileCert);
        }

        String previewFile =this.noticesRoot + "/NOTARIZATION_PREVIEW_" + notarzationMasterEntity.getProcessNo() + ".pdf";//预览公证书
        generatePdf.addPdf((String[])filelist.toArray(new String[filelist.size()]),previewFile);
        String AbsolutePath =fileUrl + previewFile;//绝对路径
        String previewFileName="NOTARIZATION_PREVIEW_" + notarzationMasterEntity.getProcessNo();
        mapCommonFormList.add(new MapImgForm("img","", "公证书_预览",AbsolutePath,previewFileName,fileTokenUtils.fileToken(previewFileName), "", "",notarzationMasterEntity.getNotarzationTypeCode(),0,false));
        this.deleteSourceFile(targetFileCert);
        this.deleteSourceFile(targetFile);

        return mapCommonFormList;
    }

    /**
     * 生成公证书和送达通知书，拟稿纸
     *
     * @param id
     * @throws Exception
     */
    @Override
    public void generateNotarization(String id) throws Exception {

        DocumentParam documentParam =ConfigParameterUtil.getDocument();
        switch (documentParam.getEnvironment()){
            case "zero":
                break;
            case "one":
                return;
            default://不接求真
                generateGZS(id);
            break;
        }

        //生成送达回执
        this.generateRecipt(id);
    }


    //非求真   生成公证书
    public void generateGZS(String id) throws  Exception{
        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(id);
        SysUserEntity sysUserEntity = sysUserService.getById(notarzationMasterEntity.getActionBy());
        boolean isCert = notarzationMasterEntity.getHasMoreCert().equals("1");

        //插入公证编号
        List<NotarzationCertificateEntity> notarzationCertificateEntities= null;
        notarzationCertificateEntities =new ArrayList<>();
        notarzationCertificateEntities.add(new  NotarzationCertificateEntity(UUID.randomUUID().toString(),id,""));
        if(isCert){
            notarzationCertificateEntities.add(new  NotarzationCertificateEntity(UUID.randomUUID().toString(),id,""));
        }
        userNotarzationCertificateService.saveBatch(notarzationCertificateEntities);



        List<NotarzationCertificateEntity> notarzationCertificateEntityList = userNotarzationCertificateDao.selectList(
                new QueryWrapper<NotarzationCertificateEntity>().eq("businessId", id).orderByAsc("certificateId")
        );//获取最新的公证编号

        String sourceFile = this.noticesRoot + "/PDF_NOTARIZATION_" + notarzationMasterEntity.getProcessNo() + ".docx";//公证书源文件
        String targetDoc = this.noticesRoot + "/" + notarzationMasterEntity.getProcessNo() + ".docx";//doc中间文件（有双证时使用，将源文件文字替换插入公证编号）用于生成pdf添加水印
        String targetFile = this.noticesRoot + "/PDF_NOTARIZATION_" + notarzationMasterEntity.getProcessNo() + ".pdf";//最终的公证书
        String pdfFileName = "PDF_NOTARIZATION_" + notarzationMasterEntity.getProcessNo() + ".pdf";
        String sourceDraft = this.templatesRoot+"/拟稿纸.docx";//拟稿纸模板文件
        String targetDraft = this.noticesRoot + "/PDF_DRAFT_" + notarzationMasterEntity.getProcessNo() + ".docx";//拟稿纸最终文件
        String draftFileName = "PDF_DRAFT_" + notarzationMasterEntity.getProcessNo() + ".docx";

        //先生成拟稿纸所需的公证书docx(去掉签名和盖章的文字)
        Map<String,String> mergeMap =new HashMap<>();
        mergeMap.put("签名","&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp");
        mergeMap.put("盖章","&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp");
        mergeMap.put("公证编号","");
        mergeMap.put("落款时间",DateUtils.format(new Date(),"yyyy年MM月dd日"));
        String notarizationToDraft = this.noticesRoot +"/"+"notarizationToDraft_" + notarzationMasterEntity.getProcessNo()+".docx";
        String notarizationToDraftCert = this.noticesRoot +"/" +"notarizationToDraftCert_" + notarzationMasterEntity.getProcessNo()+".docx";
        generatePdf.rangePlace(sourceFile,notarizationToDraft,mergeMap);



        //生成拟稿纸
        ConcurrentHashMap<String, Map<String, Object>> paramsMap = new ConcurrentHashMap<>();
        PdfVarEntity pdfVar = null;
        pdfVar = new PdfVarEntity("text", notarzationMasterEntity.getProcessNo(), null);
        paramsMap.put("processNo", pdfVar.getMap());

        String usedTo = "涉外";
        if("中国台湾、中国香港、中国、中国澳门".contains(notarzationMasterEntity.getUsedToCountry())){
            usedTo = "境内";
        }
        if(notarzationMasterEntity.getUsedToCountry().equals("中国台湾")){
            usedTo= "涉台";
        }
        pdfVar = new PdfVarEntity("text",usedTo, null);
        paramsMap.put("type", pdfVar.getMap());
        pdfVar = new PdfVarEntity("text", notarzationMasterEntity.getUsedToCountry(), null);
        paramsMap.put("usedTo", pdfVar.getMap());
        pdfVar = new PdfVarEntity("text", notarzationMasterEntity.getTranslateTo(), null);
        paramsMap.put("translateTo", pdfVar.getMap());
        generatePdf.generatePdf(paramsMap,sourceDraft,targetDraft,null,false);
        generatePdf.insertOtherNodeInTable(notarizationToDraft,targetDraft,0,1,0,targetDraft);
        userDocumentService.deleteFileByCategoryCode(id, DocumentCategoryCode.PDF_DRAFT.getCode());
        userDocumentService.addFile(targetDraft, draftFileName, id, DocumentCategoryCode.PDF_DRAFT.getCode(),false);


        //生成公证书
        Map<String, Object> map = new HashMap<>();
        map.put("imgSign", this.notarySigns + "/" + sysUserEntity.getRealName() + ".png");
        map.put("imgStamp", this.notarySigns+"/江苏省昆山市公证处.png");//todo   做成配置
        String certificateId = DateUtils.format(new Date(), "(yyyy)") + "苏昆证字第" + notarzationCertificateEntityList.get(0).getCertificateId() + "号";


        ConcurrentHashMap<String, Map<String, Object>> paramsMapNotarization = new ConcurrentHashMap<>();
        PdfVarEntity pdfVarNotarization = null;
        pdfVarNotarization = new PdfVarEntity("text",certificateId, null);
        paramsMapNotarization.put("公证编号", pdfVarNotarization.getMap());
        generatePdf.generatePdf(paramsMapNotarization, sourceFile, targetDoc, null, false);//生成公证书中间件doc


        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
        Map<String,String> mergeMapParam =new HashMap<>();//替换公证书文字
        mergeMapParam.put("签名","&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp");
        mergeMapParam.put("盖章","&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp");
        mergeMapParam.put("落款时间",df.format(new Date()));
        generatePdf.generateCertificate(targetDoc, targetFile, map,mergeMapParam);
        userDocumentService.deleteFileByCategoryCode(id, DocumentCategoryCode.PDF_NOTARIZATION.getCode());
        userDocumentService.addFile(targetFile, pdfFileName, id, DocumentCategoryCode.PDF_NOTARIZATION.getCode(),false);
        this.deleteSourceFile(targetDoc);
        this.deleteSourceFile(notarizationToDraft);



        //双证生成第二份公证书
        if(isCert){
            sourceFile = this.noticesRoot + "/PDF_NOTARIZATION_CERT_" + notarzationMasterEntity.getProcessNo() + ".docx";//公证书源文件
            targetDoc = this.noticesRoot + "/" + notarzationMasterEntity.getProcessNo() + ".docx";//doc中间文件 用于生成pdf添加水印
            targetFile = this.noticesRoot + "/PDF_NOTARIZATION_CERT_" + notarzationMasterEntity.getProcessNo() + ".pdf";//最终的公证书
            pdfFileName = "PDF_NOTARIZATION_CERT_" + notarzationMasterEntity.getProcessNo() + ".pdf";

            sourceDraft = this.templatesRoot+"/拟稿纸.docx";//拟稿纸模板文件
            targetDraft = this.noticesRoot + "/PDF_DRAFT_CERT_" + notarzationMasterEntity.getProcessNo() + ".docx";//拟稿纸最终文件
            draftFileName = "PDF_DRAFT_CERT_" + notarzationMasterEntity.getProcessNo() + ".docx";

            //先生成拟稿纸所需的公证书docx(去掉签名和盖章的文字)
            mergeMap.put("certificateNo","&nbsp&nbsp&nbsp");
            generatePdf.rangePlace(sourceFile,notarizationToDraftCert,mergeMap);

            //生成拟稿纸
            generatePdf.generatePdf(paramsMap,sourceDraft,targetDraft,null,false);
            generatePdf.insertOtherNodeInTable(notarizationToDraftCert,targetDraft,0,1,0,targetDraft);
            userDocumentService.deleteFileByCategoryCode(id, DocumentCategoryCode.PDF_DRAFT_CERT.getCode());
            userDocumentService.addFile(targetDraft, draftFileName, id, DocumentCategoryCode.PDF_DRAFT_CERT.getCode(),false);


            //生成公证书/先加入公证编号
            paramsMapNotarization = new ConcurrentHashMap<String, Map<String, Object>>();
            pdfVarNotarization = new PdfVarEntity("text",certificateId, null);
            paramsMapNotarization.put("certificateNo", pdfVarNotarization.getMap());

            certificateId = DateUtils.format(new Date(), "(yyyy)") + "苏昆证字第" + notarzationCertificateEntityList.get(1).getCertificateId() + "号";
            pdfVarNotarization = new PdfVarEntity("text",certificateId, null);
            paramsMapNotarization.put("公证编号", pdfVarNotarization.getMap());
            generatePdf.generatePdf(paramsMapNotarization, sourceFile, targetDoc, null, false);//生成公证书中间件doc

            //生成公证书
            generatePdf.generateCertificate(targetDoc, targetFile, map,mergeMapParam);
            userDocumentService.deleteFileByCategoryCode(id, DocumentCategoryCode.PDF_NOTARIZATION_CERT.getCode());
            userDocumentService.addFile(targetFile, pdfFileName, id, DocumentCategoryCode.PDF_NOTARIZATION_CERT.getCode(),false);
            this.deleteSourceFile(targetDoc);
            this.deleteSourceFile(notarizationToDraftCert);
        }

    }

    public void  deleteSourceFile(String file){
        File sourceFile = new File(file);
        if (sourceFile.exists()) {
            sourceFile.delete();
        }
    }

    /*
    生成送达回执
     */
    @Override
    public void generateRecipt(String id) throws Exception {
        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(id);
        SysUserEntity sysUserEntity = sysUserService.getById(notarzationMasterEntity.getActionBy());

        //生成送达回执
        String sourceFile = this.templatesRoot + "/公证文书送达回执.docx";
        String destinationDocFile = this.noticesRoot + "/PDF_RECEIPT_" + notarzationMasterEntity.getProcessNo() + ".docx";
        String destinationPdfFile = this.noticesRoot + "/PDF_RECEIPT_" + notarzationMasterEntity.getProcessNo() + ".pdf";
        String pdfFileName = "PDF_RECEIPT_" + notarzationMasterEntity.getProcessNo() + ".pdf";


        //获取回执签名照
        List<DocumentEntity> signImg = userDocumentDao.selectList(
                new QueryWrapper<DocumentEntity>().eq("refererId", id).eq("categoryCode", DocumentCategoryCode.SIGN_RECEIPT.getCode()).eq("isDeleted", 0).orderByDesc("createdTime")
        );
        PdfVarEntity signImgEntity = null;
        //出证中之前的状态
        if (signImg == null || signImg.size() < 1
                || notarzationMasterEntity.getStatus().equals(NotarzationStatusEnum.PENDINGCONFIRMED.getCode())
                || notarzationMasterEntity.getStatus().equals(NotarzationStatusEnum.PENDINGAPPROVED.getCode())
                || notarzationMasterEntity.getStatus().equals(NotarzationStatusEnum.PENDINGPAYMENT.getCode())) {
            signImgEntity = new PdfVarEntity("text", "待签名", null);
        } else {
            DocumentEntity signDocumentEntity = signImg.get(0);
            List<String> signUrl = new ArrayList<>();
            String absolutePath = signDocumentEntity.getUploadedAbsolutePath();
            String fileName = absolutePath.substring(absolutePath.lastIndexOf("/")+1);
            String token = FileStorageByNotaryUtil.generateToken(fileName,sysNotarialOfficeDao.selectById(notarzationMasterEntity.getNotarialOfficeId()).getSecretKey());
            signUrl.add(absolutePath + "?token=" + token);
            signImgEntity = new PdfVarEntity("sign", signUrl, null);
        }

        ConcurrentHashMap<String, Map<String, Object>> hashMap = new ConcurrentHashMap<>();
        List<String> url = null;
        PdfVarEntity pdfVarEntity = null;


        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getRealName(), null);
        hashMap.put("realName", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getProcessNo(), null);
        hashMap.put("processNo", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", sysUserEntity.getRealName(), null);
        hashMap.put("actionName", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", NotarizationTypeEnum.getEnumMsg(notarzationMasterEntity.getNotarzationTypeCode()), null);//todo
        hashMap.put("notarzationTypeCode", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getCopyNumber(), null);
        hashMap.put("copyNumber", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", DateUtil.format(new Date(), "yyyy年MM月dd日"), null);
        hashMap.put("today", pdfVarEntity.getMap());


        List<NotarzationCertificateEntity> notarzationCertificateEntities = userNotarzationCertificateDao.selectList(
                new QueryWrapper<NotarzationCertificateEntity>().eq("businessId", notarzationMasterEntity.getId())
        );//查询证书编号
        String certificateId = "";
        for(NotarzationCertificateEntity item:notarzationCertificateEntities){
//            certificateId = certificateId + DateUtils.format(new Date(), "(yyyy)") + "苏昆证字第" + item.getCertificateId() + "号、";   //原有逻辑
            certificateId = certificateId + item.getNotarialCertificateNo()+"、";
        }
        certificateId = certificateId.substring(0,certificateId.length()-1);
        pdfVarEntity = new PdfVarEntity("text",certificateId, null);
        hashMap.put("certificateId", pdfVarEntity.getMap());


        hashMap.put("ApplicantSig", signImgEntity.getMap());
        generatePdf.generatePdf(hashMap, sourceFile, destinationPdfFile, null, false);
        userDocumentService.deleteFileByCategoryCode(id, DocumentCategoryCode.PDF_RECEIPT.getCode());
        userDocumentService.addFile(destinationPdfFile, pdfFileName, id, DocumentCategoryCode.PDF_RECEIPT.getCode(),false);
    }


    @Override
    public Boolean isAgentP(NotarzationMasterEntity notarzationMasterEntity){
        String applicantParty = notarzationMasterEntity.getApplicantParty();
        Integer isAgent =  notarzationMasterEntity.getIsAgent();
        String notarizationType = notarzationMasterEntity.getNotarzationTypeCode();
        //1、代理个人需要加价   2、委托公证需要加价
        boolean needAdd = (applicantParty.equals(ApplicationPartyEnum.P.getCode()) && isAgent == 1) || NotChangeNotarizationTypeEnum.getEnumCode(notarizationType)!=null;
        return needAdd;
    }


    /**
     * 获取支付明细
     *
     * @param param
     * @return
     */

    @Override
    public List<Map<String, Object>> getPaidDetail(Map<String, Object> param) {
        PayArrayParam payArrayFrom =ConfigParameterUtil.getPayArray();
        DocumentParam documentParam = ConfigParameterUtil.getDocument();
        NotarzationMasterEntity notarzationMasterEntity = new NotarzationMasterEntity();
        OrderEntity orderEntity = new OrderEntity();
        param.remove("sentToProvince");
        notarzationMasterEntity.update(param);
        orderEntity.update(param);
        Map<String, Object> tableMap = null;//表格每一行对应一个tableMap
        List<Map<String, Object>> tableParam = new ArrayList<Map<String, Object>>();
        BigDecimal totalPay = BigDecimal.ZERO;
        if (notarzationMasterEntity.getApplicantParty().equals(ApplicationPartyEnum.P.getCode())) {
            tableMap = new HashMap<>();
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("NotaryAmount"));//公证费
            BigDecimal NotaryAmountP =  payArrayFrom.getNotaryAmountP();
            tableMap.put("payAmount", NotaryAmountP.setScale(2));
            //昆山公证 公证费加价
            switch (documentParam.getEnvironment()){
                case "zero":
                    Boolean isAgentP = this.isAgentP(notarzationMasterEntity);
                    if(isAgentP){
                        NotaryAmountP = NotaryAmountP.add(payArrayFrom.getNotaryAmountPAdd());
                        tableMap.put("payAmount",NotaryAmountP.setScale(2));
                    }
                    break;
            }
            tableParam.add(tableMap);
            totalPay = totalPay.add(NotaryAmountP);//计算总价
        } else {
            tableMap = new HashMap<>();
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("NotaryAmount"));//公证费
            tableMap.put("payAmount", payArrayFrom.getNotaryAmountE().setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(payArrayFrom.getNotaryAmountE());//计算总价
        }


        BigDecimal copyAmount = notarzationMasterEntity.getCopyNumber().subtract(BigDecimal.ONE);
        copyAmount = copyAmount.multiply(payArrayFrom.getCopyAmount());
        if (copyAmount.compareTo(BigDecimal.ZERO) > 0) {
            tableMap = new HashMap<>();
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("CopyAmount"));//副本费
            tableMap.put("payAmount", copyAmount.setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(copyAmount);//计算总价
        }

        BigDecimal translationAmount = orderEntity.getTranslationAmount();
        if (translationAmount.compareTo(BigDecimal.ZERO) > 0) {
            tableMap = new HashMap<>();
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("TranslationAmount"));//翻译费
            tableMap.put("payAmount", translationAmount.setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(translationAmount);//计算总价
        }

        BigDecimal logisticsAmount = orderEntity.getLogisticsAmount();
        if (logisticsAmount.compareTo(BigDecimal.ZERO) > 0) {
            tableMap = new HashMap<>();
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("LogisticsAmount"));//快递费
            tableMap.put("payAmount", logisticsAmount.setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(logisticsAmount);//计算总价
        }

        BigDecimal serviceAmount = orderEntity.getServiceAmount();
        if (serviceAmount.compareTo(BigDecimal.ZERO) > 0) {
            tableMap = new HashMap<>();
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("ServiceAmount"));//公证服务费
            tableMap.put("payAmount", serviceAmount.setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(serviceAmount);//计算总价
        }

        int sentToStraitsExchangeFoundation = notarzationMasterEntity.getSentToStraitsExchangeFoundation();
        String sentToStraitsExchangeFoundationType = notarzationMasterEntity.getExpressModeToSEF();
        if (sentToStraitsExchangeFoundation > 0 && sentToStraitsExchangeFoundationType != null && sentToStraitsExchangeFoundationType.equals(MailModeSEFEnum.expressModeToSEFP.getCode())) {
            tableMap = new HashMap<>();
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("ModeToSEF"));//寄台湾海基快递费
            tableMap.put("payAmount", payArrayFrom.getModeToSEFP().setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(payArrayFrom.getModeToSEFP());//计算总价
        }
        if (sentToStraitsExchangeFoundation > 0 && sentToStraitsExchangeFoundationType != null && sentToStraitsExchangeFoundationType.equals(MailModeSEFEnum.expressModeToSEFS.getCode())) {
            tableMap = new HashMap<>();
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("ModeToSEF"));//寄台湾海基快递费
            tableMap.put("payAmount", payArrayFrom.getModeToSEFS().setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(payArrayFrom.getModeToSEFS());//计算总价
        }

        String hasMoreCert = notarzationMasterEntity.getHasMoreCert();
        if (hasMoreCert.equals("1")) {
            tableMap = new HashMap<>();
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("DoubleCertificate"));//双证译文文本相符
            tableMap.put("payAmount", payArrayFrom.getDoubleCertificate().setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(payArrayFrom.getDoubleCertificate());//计算总价

            tableMap = new HashMap<>();
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("DoubleCertificateCopy"));//双证译文文本相符副本费
            tableMap.put("payAmount", copyAmount.setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(copyAmount);//计算总价
        }
        tableMap = new HashMap<>();
        tableMap.put("payType", "总计");//双证译文文本相符副本费
        tableMap.put("payAmount", totalPay.setScale(2));
        tableParam.add(tableMap);
        return tableParam;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editMasterAndOrder(Map<String, Object> param) {
        String userId = ShiroUtils.getUserId().toString();

        String notarizationType = param.get("notarzationTypeCode").toString();
        DocumentParam documentParam = ConfigParameterUtil.getDocument();

        if((int)param.get("isAgent")==1&&documentParam.getEnvironment().equals("zero")&&NotChangeNotarizationTypeEnum.getEnumCode(notarizationType)!=null){
            throw new COREException("代理情况下不可切换该公证",8);
        }


        //更新基础表
        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(param.get("id").toString());

        //特殊处理,发送地址
        List<String> sentToAddress = (List<String>) param.get("sentToProvince");
        param.remove("sentToProvince");

        if (sentToAddress != null) {
            param.put("sentToProvince", sentToAddress.size() > 0 ? sentToAddress.get(0) : "");
            param.put("sentToCity", sentToAddress.size() > 1 ? sentToAddress.get(1) : "");
            param.put("sentToArea", sentToAddress.size() > 2 ? sentToAddress.get(2) : "");
        }


        notarzationMasterEntity.update(param);
        notarzationMasterEntity.setUpdatedTime(new Date());
        notarzationMasterEntity.setStatus(param.get("status").toString());
        notarzationMasterEntity.setUpdatedBy(ShiroUtils.getUserId().toString());
        if(notarzationMasterEntity.getApplicantParty().equals(ApplicationPartyEnum.E.getCode())){
            notarzationMasterEntity.setRealName(notarzationMasterEntity.getCompanyName());
            notarzationMasterEntity.setBirth(notarzationMasterEntity.getRegisterDate());
            notarzationMasterEntity.setIdCardNo(notarzationMasterEntity.getSocialCreditCode());
            notarzationMasterEntity.setIdCardAddress(notarzationMasterEntity.getCompanyAddress());
        }


        //更新订单表
        OrderEntity orderEntity = userOrderDao.selectOne(
                new QueryWrapper<OrderEntity>().eq("id", notarzationMasterEntity.getOrderId()).eq("isDeleted", 0)
        );
        orderEntity.update(param);//需要更新方法里加
        orderEntity.setRealAmount(this.getRealAmount(orderEntity, notarzationMasterEntity));
        orderEntity.setUpdatedTime(new Date());
        orderEntity.setUpdatedBy(ShiroUtils.getUserId().toString());


        //代理公证修改代理人信息并更新订单的电话号码
        if((int)param.get("isAgent")==1){
            UserEntity userEntity = userDao.selectById((String)param.get("userId"));
            userEntity.update(param);
            notarzationMasterEntity.setPhone(userEntity.getPhone());
            userDao.updateById(userEntity);
        }else {
            UserEntity userEntity = userDao.selectById((String)param.get("userId"));
            userEntity.updateNoAgent(param);
            userDao.updateById(userEntity);
        }




        userOrderDao.updateById(orderEntity);
        userNotarzationMasterDao.updateById(notarzationMasterEntity);

        //其他询问更新
        if(param.get("otherQuestion")!=null){
            List<Map<String,Object>> otherQuestion = (List<Map<String,Object>>)param.get("otherQuestion");
            List<NotarizationMattersQuestionEntity> notarizationMattersQuestionEntities = new ArrayList<>();
            for(Map<String,Object> map:otherQuestion){
                String id = (String)map.get("id");
                String question = (String)map.get("question");
                String answer = (String)map.get("answer");
                Integer sort = NumberUtils.createInteger(map.get("sort").toString());
                Date time =new Date();
                NotarizationMattersQuestionEntity notarizationMattersQuestionEntity = new NotarizationMattersQuestionEntity(id,notarzationMasterEntity.getId(),question,answer,notarzationMasterEntity.getNotarzationTypeCode(),sort,time,time,userId,userId);
                notarizationMattersQuestionEntities.add(notarizationMattersQuestionEntity);
            }
            notarizationMattersQuestionDao.delete(//删除所有问询
                    new QueryWrapper<NotarizationMattersQuestionEntity>().eq("notarizationId",notarzationMasterEntity.getId())
            );
            notarizationMattersQuestionService.saveBatch(notarizationMattersQuestionEntities);
        }





    }

    @Override
    public void  generatePreparePaperCert(NotarzationMasterEntity notarzationMasterEntity,ConcurrentHashMap<String, Map<String,Object>> hashMap) throws Exception{
        // 公证书模板的路径
        String sourceFile = this.templatesRoot + "/公证书_译文原本相符.docx";
        // 生成公证书的路径
        String docName ="PDF_NOTARIZATION_CERT_"+ notarzationMasterEntity.getProcessNo() + ".docx";
        String destinationDocFile = this.noticesRoot + "/" + docName;
        generatePdf.generatePdf(hashMap, sourceFile, destinationDocFile, null, false);
        userDocumentService.deleteFileByCategoryCode(notarzationMasterEntity.getId(), DocumentCategoryCode.PDF_NOTARIZATION_CERT.getCode());
        userDocumentService.addFile(destinationDocFile, docName, notarzationMasterEntity.getId(), DocumentCategoryCode.PDF_NOTARIZATION_CERT.getCode(),false);

    }

    @Override
    public boolean isForeign(String country){
        CountryParam countryParam =ConfigParameterUtil.getCountry();
        if(countryParam.getTranslateLanguage().contains(country)){
            return false;
        }
        return true;
    }


    @Override
    public ConcurrentHashMap<String, Map<String, Object>> getCommenHashMap(NotarzationMasterEntity notarzationMasterEntity){
        String id=notarzationMasterEntity.getId();
        Date date = DateUtil.date();
        int Year = DateUtil.year(date);
        int Month = DateUtil.month(date) + 1;
        int Day = DateUtil.dayOfMonth(date);
        ConcurrentHashMap<String, Map<String, Object>> hashMap = new ConcurrentHashMap<>();
        PdfVarEntity pdfVarEntity = null;

        hashMap = new ConcurrentHashMap<String, Map<String, Object>>();
        pdfVarEntity = new PdfVarEntity("text", Year, null);
        hashMap.put("Year", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", Month, null);
        hashMap.put("Month", pdfVarEntity.getMap());
        pdfVarEntity = new PdfVarEntity("text", Day, null);
        hashMap.put("Day", pdfVarEntity.getMap());


        return hashMap;
    }


    @Override
    public ConcurrentHashMap<String, Map<String, Object>> getSignHashMap(NotarzationMasterEntity notarzationMasterEntity){
        String id=notarzationMasterEntity.getId();
        ConcurrentHashMap<String, Map<String, Object>> hashMap = new ConcurrentHashMap<>();

        List<DocumentEntity> signImg = userDocumentDao.selectList(
                new QueryWrapper<DocumentEntity>().eq("refererId", id).eq("categoryCode", "sign").eq("isDeleted", 0).orderByDesc("createdTime")
        );
        PdfVarEntity signImgEntity = null;
        //出证中之前的状态
        if (signImg == null || signImg.size() < 1
                || notarzationMasterEntity.getStatus().equals(NotarzationStatusEnum.PENDINGAPPROVED.getCode())
                || notarzationMasterEntity.getStatus().equals(NotarzationStatusEnum.PENDINGCONFIRMED.getCode())
                || notarzationMasterEntity.getStatus().equals(NotarzationStatusEnum.PENDINGPAYMENT.getCode())) {
            signImgEntity = new PdfVarEntity("text", "待签名", null);
        } else {
            //获取签名文件
            DocumentEntity signDocumentEntity = signImg.get(0);
            List<String> signUrl = new ArrayList<>();
            String absolutePath = signDocumentEntity.getUploadedAbsolutePath();
            String fileName = absolutePath.substring(absolutePath.lastIndexOf("/")+1);
            String token = FileStorageByNotaryUtil.generateToken(fileName,sysNotarialOfficeDao.selectById(notarzationMasterEntity.getNotarialOfficeId()).getSecretKey());
            signUrl.add(absolutePath + "?token=" + token);
            signImgEntity = new PdfVarEntity("sign", signUrl, null);
        }

        //添加签名
        hashMap.put("ApplicantSig", signImgEntity.getMap());
        return hashMap;
    }
}
