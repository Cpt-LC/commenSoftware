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

    private static final List<Map<String, Object>> DetailInfoTemplate = new ArrayList<Map<String, Object>>();//????????????

    static {
        DocumentParam documentParam =ConfigParameterUtil.getDocument();
        Map<String, Object> groupMap = null;//??????map
        List<Map<String, Object>> rowsList = null;//?????????
        Map<String, Object> columnsMap = null;//??????map
        List<MapCommonForm> columnsList = null;//????????????

        List<Map<String, Object>> selectList = null;
        Map<String, Object> selectMap = null;

        //?????????
        groupMap = new HashMap<String, Object>();
        groupMap.put("group", "????????????");
        rowsList = new ArrayList<Map<String, Object>>();
        columnsMap = new HashMap<String, Object>();
        columnsMap.put("index", 0);
        columnsList = new ArrayList<MapCommonForm>();


        //??????????????????????????????
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
                // ??????????????????????????????????????????
                // option:img????????????????????????,select??????????????????????????????;
                // disabled????????????????????????????????????????????????
                String group = record.get("group");
                int rowIndex = Integer.parseInt(record.get("rowIndex"));
                if (!group.equals(groupMap.get("group"))) {
                    //???????????????
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
                    //???????????????
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
                                    // 3.??????????????????????????????????????????????????????

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


    //??????????????????
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
            realAmount = realAmount.add(NotaryAmountP);//????????????//?????????

        } else {
            realAmount = realAmount.add(payArrayFrom.getNotaryAmountE());//????????????//?????????
        }

        BigDecimal copyAmount = notarzationMasterEntity.getCopyNumber().subtract(BigDecimal.ONE);
        copyAmount = copyAmount.multiply(payArrayFrom.getCopyAmount());
        if (copyAmount.compareTo(BigDecimal.ZERO) > 0) {
            realAmount = realAmount.add(copyAmount);//????????????//?????????
        }

        BigDecimal translationAmount = orderEntity.getTranslationAmount();
        if (translationAmount.compareTo(BigDecimal.ZERO) > 0) {
            realAmount = realAmount.add(translationAmount);//????????????//?????????
        }

        BigDecimal logisticsAmount = orderEntity.getLogisticsAmount();
        if (logisticsAmount.compareTo(BigDecimal.ZERO) > 0) {
            realAmount = realAmount.add(logisticsAmount);//????????????//?????????
        }

        BigDecimal serviceAmount = orderEntity.getServiceAmount();
        if (serviceAmount.compareTo(BigDecimal.ZERO) > 0) {
            realAmount = realAmount.add(serviceAmount);//????????????//???????????????
        }

        int sentToStraitsExchangeFoundation = notarzationMasterEntity.getSentToStraitsExchangeFoundation();
        String sentToStraitsExchangeFoundationType = notarzationMasterEntity.getExpressModeToSEF();
        if (sentToStraitsExchangeFoundation > 0 && sentToStraitsExchangeFoundationType != null && sentToStraitsExchangeFoundationType.equals(MailModeSEFEnum.expressModeToSEFP.getCode())) {
            realAmount = realAmount.add(payArrayFrom.getModeToSEFP());//????????????//????????????????????????
        }
        if (sentToStraitsExchangeFoundation > 0 && sentToStraitsExchangeFoundationType != null && sentToStraitsExchangeFoundationType.equals(MailModeSEFEnum.expressModeToSEFS.getCode())) {
            realAmount = realAmount.add(payArrayFrom.getModeToSEFS());//????????????//????????????????????????
        }

        String hasMoreCert = notarzationMasterEntity.getHasMoreCert();
        if (hasMoreCert.equals("1")) {
            realAmount = realAmount.add(payArrayFrom.getDoubleCertificate());//????????????//????????????????????????
            realAmount = realAmount.add(copyAmount);//????????????//?????????????????????????????????
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
            case "zero":  //???????????????????????????
                //?????????????????????
                if(param.get("certificateIds")==null){
                    throw new  RuntimeException("?????????????????????");
                }
                List<String> certificateIds = (List<String>)param.get("certificateIds");
                notarzationCertificateEntities =new ArrayList<>();
                for(String item: certificateIds){
                    NotarzationCertificateEntity checkItem = userNotarzationCertificateService.getOne(
                            new QueryWrapper<NotarzationCertificateEntity>().eq("notarialCertificateNo", item)
                    );
                    if(checkItem!=null){
                        throw new RuntimeException("????????????????????????");
                    }
                    notarzationCertificateEntities.add(new  NotarzationCertificateEntity(UUID.randomUUID().toString(),id,item));
                }
                userNotarzationCertificateService.saveBatch(notarzationCertificateEntities);
                break;
            case "one":
                notarzationMasterEntity.setStatus(NotarzationStatusEnum.COMPLETED.getCode());
                notarzationMasterEntity.setProcessStatus(ProcessStatusEnum.COMPLETED.getCode());
                break;
            default: //?????????  ????????????id

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

        //????????????,????????????
        List<String> sentToAddress = (List<String>) param.get("sentToProvince");
        param.remove("sentToProvince");

        if (sentToAddress != null) {
            param.put("sentToProvince", sentToAddress.size() > 0 ? sentToAddress.get(0) : "");
            param.put("sentToCity", sentToAddress.size() > 1 ? sentToAddress.get(1) : "");
            param.put("sentToArea", sentToAddress.size() > 2 ? sentToAddress.get(2) : "");
        }

        //???????????????
        OrderEntity orderEntity = userOrderDao.selectOne(
                new QueryWrapper<OrderEntity>().eq("id", notarzationMasterEntity.getOrderId()).eq("isDeleted", 0)
        );
        orderEntity.update(param);//????????????????????????
        orderEntity.setUpdatedTime(new Date());
        orderEntity.setUpdatedBy(ShiroUtils.getUserId().toString());

        userOrderDao.updateById(orderEntity);
        userNotarzationMasterDao.updateById(notarzationMasterEntity);
    }

    public List<MapCommonForm>  parseQuestions(List<NotarizationMattersQuestionEntity> notarizationMattersQuestionEntities){
        List<MapCommonForm> questions  =new ArrayList<>();
        int i=0;
        for(NotarizationMattersQuestionEntity item:notarizationMattersQuestionEntities){
            MapCommonForm mapCommonForm = new MapListForm("questions", item.getId(), "????????????", item, Boolean.FALSE, Boolean.FALSE, null, Boolean.TRUE, "????????????", i++, "notarization_matters_question", Boolean.FALSE, 24);
            questions.add(mapCommonForm);
        }
        return questions;
    }

    @Override
    public Map<String, List<MapCommonForm>> parseDocuments(List<DocumentForm> documentFormList, Map<String, Object> param) {
        Boolean disabled = isDisabled(param,ShiroUtils.getUserId());
        Map<String, IDocumentFilter> filters = DocumentFilterConfig.getFilters();

        //????????????
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
                    if(doc.getCategoryCode().contains("PDF")//???????????????????????????????????????
                            && !doc.getCategoryCode().equals(DocumentCategoryCode.PDF_NOTARIZATION.getCode())
                            && !doc.getCategoryCode().equals(DocumentCategoryCode.PDF_DRAFT.getCode())
                            && !doc.getCategoryCode().equals(DocumentCategoryCode.PDF_NOTARIZATION_CERT.getCode())
                            && !doc.getCategoryCode().equals(DocumentCategoryCode.PDF_DRAFT_CERT.getCode())
                            ){
                        map.setType("pdf");

                        if(!doc.getCategoryCode().equals(DocumentCategoryCode.PDF_PAY_NOTICE.getCode())&&!doc.getCategoryCode().equals(DocumentCategoryCode.PDF_HOME_ATTORNEY.getCode())){
                            boolean existedSignedImg = (signedImg != null && signedImg.isPresent());
                            ((MapImgForm)map).setSignedText(existedSignedImg ? "?????????" : "?????????");
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
                            ((MapImgForm)map).setSignedText(existedSignedReceiptImg ? "?????????" : "?????????");
                        }
                    }
                    map.setAbleToComment(!disabled);//???????????????????????????
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
        //???????????????????????????????????????????????????????????????????????????????????????
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
                //?????????????????????????????????????????????????????????????????????????????????
                Boolean isChina = param.get("usedToCountry").toString().contains("??????");

                listForm.setDisabled(isChina);
                listForm.setRequired(!isChina);
                break;
            case "sentToStraitsExchangeFoundation":
            case "expressModeToSEF":
                if(disabled){
                    return disabled;
                }
                //???????????????????????????????????????????????????????????????????????????????????????
                Boolean isTaiwan = (param.get("usedToCountry").toString().equals("????????????"));

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
     * ???????????????????????????????????????
     *
     * @param param
     * @return
     */
    @Override
    public List<Map<String, Object>> editFrom(Map<String, Object> param) throws NotSupportedException, NoSuchMethodException, ClassNotFoundException, InvocationTargetException, IllegalAccessException {

        //????????????????????????
        DocumentParam documentParam = ConfigParameterUtil.getDocument();

        //?????? ????????????  ?????????????????????
        if((int)param.get("isAgent")==1){
            Map<String, Object> agentMap = userDao.getAgentInfo(param.get("userId").toString());
            param.putAll(agentMap);
        }

        List<Map<String, Object>> groupList = new ArrayList<Map<String, Object>>();//????????????
        Map<String, Object> groupMap = null;//??????map
        List<Map<String, Object>> rowsList = null;//?????????
        Map<String, Object> columnsMap = null;//??????map
        List<MapCommonForm> columnsList = null;//????????????

        List<Map<String, Object>> selectList = null;
        Map<String, Object> selectMap = null;
        Long userId = ShiroUtils.getUserId();

        String masterId = param.get("id").toString();
        String applicantParty = param.get("applicantParty").toString();//????????????
        String status = param.get("status").toString();
        Object actionBy = param.get("actionBy");



        //?????????????????????????????????
        List<DocumentForm> documentFormList = userDocumentService.getlist(masterId);
        Map<String, List<MapCommonForm>> docCategories = this.parseDocuments(documentFormList, param);

        //????????????????????????
        List<NotarizationMattersQuestionEntity> notarizationMattersQuestionEntitys = notarizationMattersQuestionDao.selectList(
                new QueryWrapper<NotarizationMattersQuestionEntity>().eq("notarizationId",masterId).orderByAsc("sort")
        );
        List<MapCommonForm> questions = this.parseQuestions(notarizationMattersQuestionEntitys);

        //?????????
        for (Map<String, Object> group :
                DetailInfoTemplate) {
            String groupText = group.get("group").toString();

            //??????????????????????????????????????????
            if(status.equals(NotarzationStatusEnum.COMPLETED.getCode())
                    || status.equals(NotarzationStatusEnum.PENDINGPICKUP.getCode())){
                if(groupText.equals("????????????")){
                    //?????????????????????????????????????????????
                    continue;
                }
            }
            else{
                if(groupText.equals("????????????")){
                    //??????????????????????????????????????????
                    continue;
                }
                else if(groupText.equals("????????????")){
                    //?????????????????????????????????????????????
                    continue;
                }
            }


            switch (documentParam.getEnvironment()){
                case "zero":
                    //??????????????????????????????????????????
                    if(groupText.equals("????????????")||groupText.equals("????????????")||groupText.equals("????????????")){
                        continue;
                    }
                    break;
                default:if(groupText.equals("????????????")||groupText.equals("????????????")||groupText.equals("????????????")){
                    continue;
                }
                    break;
            }



            if((int)param.get("isAgent")==0&&groupText.equals("???????????????")){
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

                    //??????
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
                            if(key.equals("sentToProvinceJh")){ //?????????????????????  ????????????????????????   ?????????????????????????????????????????????
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

                            //?????????????????????????????????????????????
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
                                        colObj.setType("IMG");//???????????? ???????????????IMG  ????????????
                                    }
                                    String fileName = doc.getFileName() + doc.getFileExt();
                                    String uuid = userDocumentService.getToken((Long)param.get("notarialOfficeId"),doc.getUploadedAbsolutePath());
                                    MapImgForm imgForm = (MapImgForm) colObj;
                                    imgForm.setToken(uuid);
                                    imgForm.setCategoryCode(doc.getCategoryCode());
                                    imgForm.setDocId(doc.getId());
                                    imgForm.setFileName(fileName);
                                    imgForm.setValue(doc.getUploadedAbsolutePath());
                                    //??????????????????  ????????????????????????
                                    if((int)param.get("isAgent")==1&&groupText.equals("????????????")){
                                        imgForm.setValue("");
                                    }
                                    break;
                                }
                            }

                            break;
                        case "fileList":
                            String label = colObj.getLabel();
                            List<MapCommonForm> documents = docCategories.get(label);
//                            documents.add(docCategories.get("??????????????????").get(0));
                            for (MapCommonForm doc :
                                    documents) {
                                if (groupMap.get("group").equals("????????????")||groupMap.get("group").equals("????????????")) {
                                    if(documentParam.getEnvironment().equals("zero")){ //????????????
                                        doc.setAbleToComment(!disabled);
                                    }
                                }
                                columnsList.add(doc);
                            }
                            break;
                        case "questions":
                            //????????????????????????????????????????????????????????????
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
                    //?????????????????????????????????????????????
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
     * ?????????????????????
     * param  notarzationMasterEntity ????????????
     * templates  ?????????????????????
     */
    @Override
    public void pdfGenerate(NotarzationMasterEntity notarzationMasterEntity) throws Exception {
        //????????????
        String notarzationTypeCode = notarzationMasterEntity.getNotarzationTypeCode();
        String status = notarzationMasterEntity.getStatus();
        //???????????????
        //?????????
        SysUserEntity sysUserEntity = sysUserService.queryByUserId(notarzationMasterEntity.getActionBy());
        File file = new File(this.notarySigns + "/" + sysUserEntity.getRealName() + ".png");
        if (!file.exists()) {
            throw new RuntimeException("?????????????????????");
        }



        //???????????????????????????
        DocumentParam documentParam = ConfigParameterUtil.getDocument();
        String generateTemplates =documentParam.getGenerateTemplates();
        Reader reader = FileContentUtil.getStreamReader("classpath:generate-templates.csv");
        Iterable<CSVRecord> records = new CSVParser(reader,
                    CSVFormat.EXCEL.withDelimiter(',')
                            .withHeader("code", "path", "className", "suit", "category"));
        for(CSVRecord record:records){
            //?????????????????????
            if(!generateTemplates.contains(record.get("code"))||!record.get("category").equals("signFile")){
                continue;
            }
            //??????????????????????????????????????????????????????
            if(record.get("suit")!=null&&!record.get("suit").equals("")){

                String[] arry = record.get("suit").split("-");
                List<String> suitList = Arrays.asList(arry);
                if(!suitList.contains(notarzationTypeCode)){
                    continue;
                }

            }
            System.out.println(record.get("className"));
            //????????????
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

            //?????????????????????  ??????????????????
            if(record.get("path").contains("????????????")){
                break;
            }
        }

    }


    /**
     * ?????????????????????????????????
     *
     * @param notarzationMasterEntity ????????????
     * @return
     */
    @Override
    public ConcurrentHashMap<String, Map<String, Object>> getPublicParam(NotarzationMasterEntity notarzationMasterEntity) {
        //???????????????
        //????????????
        CountryParam countryParam =ConfigParameterUtil.getCountry();//??????????????????
        OrderEntity orderEntity = userOrderDao.selectById(notarzationMasterEntity.getOrderId());
        SysUserEntity sysUserEntity = sysUserService.queryByUserId(notarzationMasterEntity.getActionBy());//???????????????
        ConcurrentHashMap<String, Map<String, Object>> hashMap = new ConcurrentHashMap<>();
        String question =null;
        List<String> url = null;
        List<String> fileNameList = null;
        PdfVarEntity pdfVarEntity = null;
        List<Map<String, Object>> tableParam = null;//??????list???????????????tableMap????????????
        Map<String, Object> tableMap = null;//???????????????????????????tableMap

        //???????????????????????????????????????
        if(notarzationMasterEntity.getIsAgent()==1){
            UserEntity userEntity = userDao.selectById(notarzationMasterEntity.getUserId());
            pdfVarEntity = new PdfVarEntity("text", userEntity.getRealName(), null);
            hashMap.put("realName", pdfVarEntity.getMap());
            String gender = userEntity.getGender()==1? "???" : "???";
            pdfVarEntity = new PdfVarEntity("text", gender, null);
            hashMap.put("gender", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", DateUtils.format(userEntity.getBirth()), null);
            hashMap.put("birth", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", userEntity.getPhone(), null);
            hashMap.put("phone", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", userEntity.getIdCardType() + "???" + userEntity.getIdCardNo(), null);
            hashMap.put("idCardNo", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", userEntity.getIdCardAddress(), null);
            hashMap.put("idCardAddress", pdfVarEntity.getMap());

            if(notarzationMasterEntity.getApplicantParty().equals(ApplicationPartyEnum.P.getCode())){
                gender = notarzationMasterEntity.getGender().equals("1") ? "???" : "???";
                String dateStr =  DateUtils.format(notarzationMasterEntity.getBirth(),"???yyyy???MM???dd????????????");
                String agentQ = "???????????????" + notarzationMasterEntity.getRealName() + "???????????????<br>????????????????????????????????????<br>??????" +
                        notarzationMasterEntity.getRealName() + "???" + gender + dateStr + "???????????????" + notarzationMasterEntity.getIdCardNo() + "????????????" + notarzationMasterEntity.getIdCardAddress() + "???";
                pdfVarEntity = new PdfVarEntity("text", agentQ, null);
                hashMap.put("agent", pdfVarEntity.getMap());
            }else {
                String agentQ = "???????????????" + notarzationMasterEntity.getRealName() + "???????????????<br>????????????????????????????????????<br>?????????????????????" +
                        notarzationMasterEntity.getRealName() + "??????????????????" + notarzationMasterEntity.getIdCardAddress() + "????????????????????????????????????" + notarzationMasterEntity.getSocialCreditCode() + "???";
                pdfVarEntity = new PdfVarEntity("text", agentQ, null);
                hashMap.put("agent", pdfVarEntity.getMap());
            }

        }else{
            pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getRealName(), null);
            hashMap.put("realName", pdfVarEntity.getMap());
            String gender = notarzationMasterEntity.getGender().equals("1") ? "???" : "???";
            pdfVarEntity = new PdfVarEntity("text", gender, null);
            hashMap.put("gender", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", DateUtils.format(notarzationMasterEntity.getBirth()), null);
            hashMap.put("birth", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getPhone(), null);
            hashMap.put("phone", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getIdCardType() + "???" + notarzationMasterEntity.getIdCardNo(), null);
            hashMap.put("idCardNo", pdfVarEntity.getMap());
            pdfVarEntity = new PdfVarEntity("text", notarzationMasterEntity.getIdCardAddress(), null);
            hashMap.put("idCardAddress", pdfVarEntity.getMap());
            String agentQ = "??????";
            pdfVarEntity = new PdfVarEntity("text", agentQ, null);
            hashMap.put("agent", pdfVarEntity.getMap());
        }

        //??????????????????
        question = "";
        List<NotarizationMattersQuestionEntity> notarizationMattersQuestionEntities =  notarizationMattersQuestionDao.selectList(
                new QueryWrapper<NotarizationMattersQuestionEntity>().eq("notarizationId",notarzationMasterEntity.getId())
        );
        for(NotarizationMattersQuestionEntity item : notarizationMattersQuestionEntities){
            question = question + "<br>??????" + item.getQuestion();
            question = question + "<br>??????" + item.getAnswer();
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
            question = "<br>??????????????????????????????<br>" + "??????" + notarzationMasterEntity.getTranslateTo()
                    + "<br>???????????????????????????????????????????????????????????????????????????????????????????????????<br>"
                    +"??????????????????????????????";
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
            question =question + "<br>????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????<br>";
            question =question + "?????????????????????????????????????????????";
        }
        if(notarzationMasterEntity.getRequiredVerification()==1){
            question =question + "<br>????????????????????????????????????<br>";
            String requiredVerification = notarzationMasterEntity.getRequiredVerification() == 0 ? "??????" : "???????????????: ????????????????????????????????????????????????????????????????????????????????????????????????????????????:????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????";
            question =question + "??????"+ requiredVerification;
        }
        pdfVarEntity = new PdfVarEntity("text", question, null);
        hashMap.put("requiredVerification", pdfVarEntity.getMap());

        if(notarzationMasterEntity.getUsedToCountry().equals("????????????")){
            //2.17?????????????????????
//            question = "<br>???????????????????????????????????????<br>";
//            int sentToStraitsNum = notarzationMasterEntity.getSentToStraitsExchangeFoundation();//?????????????????????
//            String sentToStraitsExchangeFoundation = notarzationMasterEntity.getSentToStraitsExchangeFoundation() > 0 ? "??????" + sentToStraitsNum + "???" : "?????????";
//            question =question +  "??????" +sentToStraitsExchangeFoundation;
//            if (sentToStraitsNum > 0) {//??????????????????????????????
//                String expressModeToSEF = notarzationMasterEntity.getExpressModeToSEF();
//                String expressModeToSEFType = expressModeToSEF != null && expressModeToSEF.equals("P") ? "??????????????????" : "????????????";
//                String expressModeToSEFQuestion = "<br>??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????40?????????????????????65?????????????????????????????????20??????????????????120??????????????????????????????????????????????????????<br>" +
//                        "??????" + expressModeToSEFType;
//                question =question + expressModeToSEFQuestion;
//            }

            String expressModeToSEF = notarzationMasterEntity.getExpressModeToSEF();
            String expressModeToSEFType = expressModeToSEF != null && expressModeToSEF.equals("P") ? "??????????????????" : "????????????";
            String expressModeToSEFQuestion = "<br>?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????40?????????????????????65?????????????????????????????????20??????????????????120??????????????????????????????????????????????????????<br>" +
                    "??????" + expressModeToSEFType;
            question =expressModeToSEFQuestion;

            pdfVarEntity = new PdfVarEntity("text", question, null);
            hashMap.put("sentToStraitsExchangeFoundation", pdfVarEntity.getMap());
        } else {
            pdfVarEntity = new PdfVarEntity("text", "", null);
            hashMap.put("sentToStraitsExchangeFoundation", pdfVarEntity.getMap());
        }

        pdfVarEntity = new PdfVarEntity("text", orderEntity.getIsSend() == 0 ? "??????" : "??????", null);
        hashMap.put("isSend", pdfVarEntity.getMap());

        pdfVarEntity = new PdfVarEntity("text", DateUtils.format(notarzationMasterEntity.getCreatedTime(),"yyyy???MM???dd???"), null);
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
        String sourceFile = this.noticesRoot + "/PDF_NOTARIZATION_" + notarzationMasterEntity.getProcessNo() + ".docx";//??????????????????
        String targetFile = this.noticesRoot + "/PDF_NOTARIZATION_PREVIEW_" + notarzationMasterEntity.getProcessNo() + ".pdf";//???????????????

        Map<String, Object> map = new HashMap<>();
        map.put("imgSign", this.notarySigns+ "/" + sysUserEntity.getRealName() + ".png");
        map.put("imgStamp",  this.notarySigns+"/???????????????????????????.png");//todo  ?????????
//        map.put("certificateId", "{?????????????????????}");

        //???????????????
        SimpleDateFormat df = new SimpleDateFormat("yyyy???MM???dd???");
        Map<String,String> mergeMap =new HashMap<>();//?????????????????????
        mergeMap.put("??????","&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp");
        mergeMap.put("??????","&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp");
        mergeMap.put("????????????",df.format(new Date()));
        generatePdf.generateCertificate(sourceFile, targetFile, map,mergeMap);
        filelist.add(targetFile);

        //??????
        String targetFileCert = this.noticesRoot + "/PDF_NOTARIZATION_CERT_PREVIEW_" + notarzationMasterEntity.getProcessNo() + ".pdf";//???????????????
        if(notarzationMasterEntity.getHasMoreCert().equals("1")){
            sourceFile = this.noticesRoot + "/PDF_NOTARIZATION_CERT_" + notarzationMasterEntity.getProcessNo() + ".docx";//??????????????????

            //???????????????
            generatePdf.generateCertificate(sourceFile, targetFileCert, map,mergeMap);
            filelist.add(targetFileCert);
        }

        String previewFile =this.noticesRoot + "/NOTARIZATION_PREVIEW_" + notarzationMasterEntity.getProcessNo() + ".pdf";//???????????????
        generatePdf.addPdf((String[])filelist.toArray(new String[filelist.size()]),previewFile);
        String AbsolutePath =fileUrl + previewFile;//????????????
        String previewFileName="NOTARIZATION_PREVIEW_" + notarzationMasterEntity.getProcessNo();
        mapCommonFormList.add(new MapImgForm("img","", "?????????_??????",AbsolutePath,previewFileName,fileTokenUtils.fileToken(previewFileName), "", "",notarzationMasterEntity.getNotarzationTypeCode(),0,false));
        this.deleteSourceFile(targetFileCert);
        this.deleteSourceFile(targetFile);

        return mapCommonFormList;
    }

    /**
     * ?????????????????????????????????????????????
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
            default://????????????
                generateGZS(id);
            break;
        }

        //??????????????????
        this.generateRecipt(id);
    }


    //?????????   ???????????????
    public void generateGZS(String id) throws  Exception{
        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(id);
        SysUserEntity sysUserEntity = sysUserService.getById(notarzationMasterEntity.getActionBy());
        boolean isCert = notarzationMasterEntity.getHasMoreCert().equals("1");

        //??????????????????
        List<NotarzationCertificateEntity> notarzationCertificateEntities= null;
        notarzationCertificateEntities =new ArrayList<>();
        notarzationCertificateEntities.add(new  NotarzationCertificateEntity(UUID.randomUUID().toString(),id,""));
        if(isCert){
            notarzationCertificateEntities.add(new  NotarzationCertificateEntity(UUID.randomUUID().toString(),id,""));
        }
        userNotarzationCertificateService.saveBatch(notarzationCertificateEntities);



        List<NotarzationCertificateEntity> notarzationCertificateEntityList = userNotarzationCertificateDao.selectList(
                new QueryWrapper<NotarzationCertificateEntity>().eq("businessId", id).orderByAsc("certificateId")
        );//???????????????????????????

        String sourceFile = this.noticesRoot + "/PDF_NOTARIZATION_" + notarzationMasterEntity.getProcessNo() + ".docx";//??????????????????
        String targetDoc = this.noticesRoot + "/" + notarzationMasterEntity.getProcessNo() + ".docx";//doc?????????????????????????????????????????????????????????????????????????????????????????????pdf????????????
        String targetFile = this.noticesRoot + "/PDF_NOTARIZATION_" + notarzationMasterEntity.getProcessNo() + ".pdf";//??????????????????
        String pdfFileName = "PDF_NOTARIZATION_" + notarzationMasterEntity.getProcessNo() + ".pdf";
        String sourceDraft = this.templatesRoot+"/?????????.docx";//?????????????????????
        String targetDraft = this.noticesRoot + "/PDF_DRAFT_" + notarzationMasterEntity.getProcessNo() + ".docx";//?????????????????????
        String draftFileName = "PDF_DRAFT_" + notarzationMasterEntity.getProcessNo() + ".docx";

        //????????????????????????????????????docx(??????????????????????????????)
        Map<String,String> mergeMap =new HashMap<>();
        mergeMap.put("??????","&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp");
        mergeMap.put("??????","&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp");
        mergeMap.put("????????????","");
        mergeMap.put("????????????",DateUtils.format(new Date(),"yyyy???MM???dd???"));
        String notarizationToDraft = this.noticesRoot +"/"+"notarizationToDraft_" + notarzationMasterEntity.getProcessNo()+".docx";
        String notarizationToDraftCert = this.noticesRoot +"/" +"notarizationToDraftCert_" + notarzationMasterEntity.getProcessNo()+".docx";
        generatePdf.rangePlace(sourceFile,notarizationToDraft,mergeMap);



        //???????????????
        ConcurrentHashMap<String, Map<String, Object>> paramsMap = new ConcurrentHashMap<>();
        PdfVarEntity pdfVar = null;
        pdfVar = new PdfVarEntity("text", notarzationMasterEntity.getProcessNo(), null);
        paramsMap.put("processNo", pdfVar.getMap());

        String usedTo = "??????";
        if("???????????????????????????????????????????????????".contains(notarzationMasterEntity.getUsedToCountry())){
            usedTo = "??????";
        }
        if(notarzationMasterEntity.getUsedToCountry().equals("????????????")){
            usedTo= "??????";
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


        //???????????????
        Map<String, Object> map = new HashMap<>();
        map.put("imgSign", this.notarySigns + "/" + sysUserEntity.getRealName() + ".png");
        map.put("imgStamp", this.notarySigns+"/???????????????????????????.png");//todo   ????????????
        String certificateId = DateUtils.format(new Date(), "(yyyy)") + "???????????????" + notarzationCertificateEntityList.get(0).getCertificateId() + "???";


        ConcurrentHashMap<String, Map<String, Object>> paramsMapNotarization = new ConcurrentHashMap<>();
        PdfVarEntity pdfVarNotarization = null;
        pdfVarNotarization = new PdfVarEntity("text",certificateId, null);
        paramsMapNotarization.put("????????????", pdfVarNotarization.getMap());
        generatePdf.generatePdf(paramsMapNotarization, sourceFile, targetDoc, null, false);//????????????????????????doc


        SimpleDateFormat df = new SimpleDateFormat("yyyy???MM???dd???");
        Map<String,String> mergeMapParam =new HashMap<>();//?????????????????????
        mergeMapParam.put("??????","&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp");
        mergeMapParam.put("??????","&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp");
        mergeMapParam.put("????????????",df.format(new Date()));
        generatePdf.generateCertificate(targetDoc, targetFile, map,mergeMapParam);
        userDocumentService.deleteFileByCategoryCode(id, DocumentCategoryCode.PDF_NOTARIZATION.getCode());
        userDocumentService.addFile(targetFile, pdfFileName, id, DocumentCategoryCode.PDF_NOTARIZATION.getCode(),false);
        this.deleteSourceFile(targetDoc);
        this.deleteSourceFile(notarizationToDraft);



        //??????????????????????????????
        if(isCert){
            sourceFile = this.noticesRoot + "/PDF_NOTARIZATION_CERT_" + notarzationMasterEntity.getProcessNo() + ".docx";//??????????????????
            targetDoc = this.noticesRoot + "/" + notarzationMasterEntity.getProcessNo() + ".docx";//doc???????????? ????????????pdf????????????
            targetFile = this.noticesRoot + "/PDF_NOTARIZATION_CERT_" + notarzationMasterEntity.getProcessNo() + ".pdf";//??????????????????
            pdfFileName = "PDF_NOTARIZATION_CERT_" + notarzationMasterEntity.getProcessNo() + ".pdf";

            sourceDraft = this.templatesRoot+"/?????????.docx";//?????????????????????
            targetDraft = this.noticesRoot + "/PDF_DRAFT_CERT_" + notarzationMasterEntity.getProcessNo() + ".docx";//?????????????????????
            draftFileName = "PDF_DRAFT_CERT_" + notarzationMasterEntity.getProcessNo() + ".docx";

            //????????????????????????????????????docx(??????????????????????????????)
            mergeMap.put("certificateNo","&nbsp&nbsp&nbsp");
            generatePdf.rangePlace(sourceFile,notarizationToDraftCert,mergeMap);

            //???????????????
            generatePdf.generatePdf(paramsMap,sourceDraft,targetDraft,null,false);
            generatePdf.insertOtherNodeInTable(notarizationToDraftCert,targetDraft,0,1,0,targetDraft);
            userDocumentService.deleteFileByCategoryCode(id, DocumentCategoryCode.PDF_DRAFT_CERT.getCode());
            userDocumentService.addFile(targetDraft, draftFileName, id, DocumentCategoryCode.PDF_DRAFT_CERT.getCode(),false);


            //???????????????/?????????????????????
            paramsMapNotarization = new ConcurrentHashMap<String, Map<String, Object>>();
            pdfVarNotarization = new PdfVarEntity("text",certificateId, null);
            paramsMapNotarization.put("certificateNo", pdfVarNotarization.getMap());

            certificateId = DateUtils.format(new Date(), "(yyyy)") + "???????????????" + notarzationCertificateEntityList.get(1).getCertificateId() + "???";
            pdfVarNotarization = new PdfVarEntity("text",certificateId, null);
            paramsMapNotarization.put("????????????", pdfVarNotarization.getMap());
            generatePdf.generatePdf(paramsMapNotarization, sourceFile, targetDoc, null, false);//????????????????????????doc

            //???????????????
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
    ??????????????????
     */
    @Override
    public void generateRecipt(String id) throws Exception {
        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(id);
        SysUserEntity sysUserEntity = sysUserService.getById(notarzationMasterEntity.getActionBy());

        //??????????????????
        String sourceFile = this.templatesRoot + "/????????????????????????.docx";
        String destinationDocFile = this.noticesRoot + "/PDF_RECEIPT_" + notarzationMasterEntity.getProcessNo() + ".docx";
        String destinationPdfFile = this.noticesRoot + "/PDF_RECEIPT_" + notarzationMasterEntity.getProcessNo() + ".pdf";
        String pdfFileName = "PDF_RECEIPT_" + notarzationMasterEntity.getProcessNo() + ".pdf";


        //?????????????????????
        List<DocumentEntity> signImg = userDocumentDao.selectList(
                new QueryWrapper<DocumentEntity>().eq("refererId", id).eq("categoryCode", DocumentCategoryCode.SIGN_RECEIPT.getCode()).eq("isDeleted", 0).orderByDesc("createdTime")
        );
        PdfVarEntity signImgEntity = null;
        //????????????????????????
        if (signImg == null || signImg.size() < 1
                || notarzationMasterEntity.getStatus().equals(NotarzationStatusEnum.PENDINGCONFIRMED.getCode())
                || notarzationMasterEntity.getStatus().equals(NotarzationStatusEnum.PENDINGAPPROVED.getCode())
                || notarzationMasterEntity.getStatus().equals(NotarzationStatusEnum.PENDINGPAYMENT.getCode())) {
            signImgEntity = new PdfVarEntity("text", "?????????", null);
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
        pdfVarEntity = new PdfVarEntity("text", DateUtil.format(new Date(), "yyyy???MM???dd???"), null);
        hashMap.put("today", pdfVarEntity.getMap());


        List<NotarzationCertificateEntity> notarzationCertificateEntities = userNotarzationCertificateDao.selectList(
                new QueryWrapper<NotarzationCertificateEntity>().eq("businessId", notarzationMasterEntity.getId())
        );//??????????????????
        String certificateId = "";
        for(NotarzationCertificateEntity item:notarzationCertificateEntities){
//            certificateId = certificateId + DateUtils.format(new Date(), "(yyyy)") + "???????????????" + item.getCertificateId() + "??????";   //????????????
            certificateId = certificateId + item.getNotarialCertificateNo()+"???";
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
        //1???????????????????????????   2???????????????????????????
        boolean needAdd = (applicantParty.equals(ApplicationPartyEnum.P.getCode()) && isAgent == 1) || NotChangeNotarizationTypeEnum.getEnumCode(notarizationType)!=null;
        return needAdd;
    }


    /**
     * ??????????????????
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
        Map<String, Object> tableMap = null;//???????????????????????????tableMap
        List<Map<String, Object>> tableParam = new ArrayList<Map<String, Object>>();
        BigDecimal totalPay = BigDecimal.ZERO;
        if (notarzationMasterEntity.getApplicantParty().equals(ApplicationPartyEnum.P.getCode())) {
            tableMap = new HashMap<>();
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("NotaryAmount"));//?????????
            BigDecimal NotaryAmountP =  payArrayFrom.getNotaryAmountP();
            tableMap.put("payAmount", NotaryAmountP.setScale(2));
            //???????????? ???????????????
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
            totalPay = totalPay.add(NotaryAmountP);//????????????
        } else {
            tableMap = new HashMap<>();
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("NotaryAmount"));//?????????
            tableMap.put("payAmount", payArrayFrom.getNotaryAmountE().setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(payArrayFrom.getNotaryAmountE());//????????????
        }


        BigDecimal copyAmount = notarzationMasterEntity.getCopyNumber().subtract(BigDecimal.ONE);
        copyAmount = copyAmount.multiply(payArrayFrom.getCopyAmount());
        if (copyAmount.compareTo(BigDecimal.ZERO) > 0) {
            tableMap = new HashMap<>();
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("CopyAmount"));//?????????
            tableMap.put("payAmount", copyAmount.setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(copyAmount);//????????????
        }

        BigDecimal translationAmount = orderEntity.getTranslationAmount();
        if (translationAmount.compareTo(BigDecimal.ZERO) > 0) {
            tableMap = new HashMap<>();
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("TranslationAmount"));//?????????
            tableMap.put("payAmount", translationAmount.setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(translationAmount);//????????????
        }

        BigDecimal logisticsAmount = orderEntity.getLogisticsAmount();
        if (logisticsAmount.compareTo(BigDecimal.ZERO) > 0) {
            tableMap = new HashMap<>();
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("LogisticsAmount"));//?????????
            tableMap.put("payAmount", logisticsAmount.setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(logisticsAmount);//????????????
        }

        BigDecimal serviceAmount = orderEntity.getServiceAmount();
        if (serviceAmount.compareTo(BigDecimal.ZERO) > 0) {
            tableMap = new HashMap<>();
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("ServiceAmount"));//???????????????
            tableMap.put("payAmount", serviceAmount.setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(serviceAmount);//????????????
        }

        int sentToStraitsExchangeFoundation = notarzationMasterEntity.getSentToStraitsExchangeFoundation();
        String sentToStraitsExchangeFoundationType = notarzationMasterEntity.getExpressModeToSEF();
        if (sentToStraitsExchangeFoundation > 0 && sentToStraitsExchangeFoundationType != null && sentToStraitsExchangeFoundationType.equals(MailModeSEFEnum.expressModeToSEFP.getCode())) {
            tableMap = new HashMap<>();
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("ModeToSEF"));//????????????????????????
            tableMap.put("payAmount", payArrayFrom.getModeToSEFP().setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(payArrayFrom.getModeToSEFP());//????????????
        }
        if (sentToStraitsExchangeFoundation > 0 && sentToStraitsExchangeFoundationType != null && sentToStraitsExchangeFoundationType.equals(MailModeSEFEnum.expressModeToSEFS.getCode())) {
            tableMap = new HashMap<>();
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("ModeToSEF"));//????????????????????????
            tableMap.put("payAmount", payArrayFrom.getModeToSEFS().setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(payArrayFrom.getModeToSEFS());//????????????
        }

        String hasMoreCert = notarzationMasterEntity.getHasMoreCert();
        if (hasMoreCert.equals("1")) {
            tableMap = new HashMap<>();
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("DoubleCertificate"));//????????????????????????
            tableMap.put("payAmount", payArrayFrom.getDoubleCertificate().setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(payArrayFrom.getDoubleCertificate());//????????????

            tableMap = new HashMap<>();
            tableMap.put("payType", AmountMsgEnum.getEnumMsg("DoubleCertificateCopy"));//?????????????????????????????????
            tableMap.put("payAmount", copyAmount.setScale(2));
            tableParam.add(tableMap);
            totalPay = totalPay.add(copyAmount);//????????????
        }
        tableMap = new HashMap<>();
        tableMap.put("payType", "??????");//?????????????????????????????????
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
            throw new COREException("????????????????????????????????????",8);
        }


        //???????????????
        NotarzationMasterEntity notarzationMasterEntity = userNotarzationMasterDao.selectById(param.get("id").toString());

        //????????????,????????????
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


        //???????????????
        OrderEntity orderEntity = userOrderDao.selectOne(
                new QueryWrapper<OrderEntity>().eq("id", notarzationMasterEntity.getOrderId()).eq("isDeleted", 0)
        );
        orderEntity.update(param);//????????????????????????
        orderEntity.setRealAmount(this.getRealAmount(orderEntity, notarzationMasterEntity));
        orderEntity.setUpdatedTime(new Date());
        orderEntity.setUpdatedBy(ShiroUtils.getUserId().toString());


        //???????????????????????????????????????????????????????????????
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

        //??????????????????
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
            notarizationMattersQuestionDao.delete(//??????????????????
                    new QueryWrapper<NotarizationMattersQuestionEntity>().eq("notarizationId",notarzationMasterEntity.getId())
            );
            notarizationMattersQuestionService.saveBatch(notarizationMattersQuestionEntities);
        }





    }

    @Override
    public void  generatePreparePaperCert(NotarzationMasterEntity notarzationMasterEntity,ConcurrentHashMap<String, Map<String,Object>> hashMap) throws Exception{
        // ????????????????????????
        String sourceFile = this.templatesRoot + "/?????????_??????????????????.docx";
        // ????????????????????????
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
        //????????????????????????
        if (signImg == null || signImg.size() < 1
                || notarzationMasterEntity.getStatus().equals(NotarzationStatusEnum.PENDINGAPPROVED.getCode())
                || notarzationMasterEntity.getStatus().equals(NotarzationStatusEnum.PENDINGCONFIRMED.getCode())
                || notarzationMasterEntity.getStatus().equals(NotarzationStatusEnum.PENDINGPAYMENT.getCode())) {
            signImgEntity = new PdfVarEntity("text", "?????????", null);
        } else {
            //??????????????????
            DocumentEntity signDocumentEntity = signImg.get(0);
            List<String> signUrl = new ArrayList<>();
            String absolutePath = signDocumentEntity.getUploadedAbsolutePath();
            String fileName = absolutePath.substring(absolutePath.lastIndexOf("/")+1);
            String token = FileStorageByNotaryUtil.generateToken(fileName,sysNotarialOfficeDao.selectById(notarzationMasterEntity.getNotarialOfficeId()).getSecretKey());
            signUrl.add(absolutePath + "?token=" + token);
            signImgEntity = new PdfVarEntity("sign", signUrl, null);
        }

        //????????????
        hashMap.put("ApplicantSig", signImgEntity.getMap());
        return hashMap;
    }
}
