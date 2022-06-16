package com.lianzheng.h5.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.lianzheng.h5.common.CommonConstant;
import com.lianzheng.h5.common.GlobalException;
import com.lianzheng.h5.common.RedisHelper;
import com.lianzheng.h5.dto.AddNotarizationDTO;
import com.lianzheng.h5.dto.NotarizationSpecialDTO;
import com.lianzheng.h5.dto.UpdateNotarizationDTO;
import com.lianzheng.h5.entity.*;
import com.lianzheng.h5.enums.NotarizationStatusEnum;
import com.lianzheng.h5.enums.NotarizationTypeEnum;
import com.lianzheng.h5.enums.StatusEnum;
import com.lianzheng.h5.jwt.util.JwtUtils;
import com.lianzheng.h5.mapper.NotarizationMattersQuestionMapper;
import com.lianzheng.h5.pay.WxPayHelper;
import com.lianzheng.h5.service.*;
import com.lianzheng.h5.util.DateUtils;
import com.lianzheng.h5.util.GenerateStrUtil;
import com.lianzheng.h5.util.StringUtil;
import com.lianzheng.h5.vo.AddNotarizationVO;
import com.lianzheng.h5.vo.MattersQuestionVO;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.lianzheng.h5.enums.NotarizationTypeEnum.WTMFB01;
import static com.lianzheng.h5.enums.NotarizationTypeEnum.getEnumByCode;

/**
 * <p>
 * 事项问题 服务实现类
 * </p>
 *
 * @author 代树鸣
 * @since 2021-12-07
 */
@Service
public class NotarizationMattersQuestionServiceImpl extends ServiceImpl<NotarizationMattersQuestionMapper, NotarizationMattersQuestion> implements INotarizationMattersQuestionService {

    @Autowired
    private DocumentServiceImpl documentService;
    @Autowired
    RedisHelper redisHelper;
    @Autowired
    private NotarzationMasterServiceImpl masterService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private INotarzationGraduationService notarzationGraduationService;
    @Autowired
    private INotarizationTaxService notarizationTaxService;
    @Autowired
    private INotarizationDegreeService notarizationDegreeService;
    @Autowired
    private INotarizationDriverLicenseService notarizationDriverLicenseService;
    @Autowired
    private INotarzationAuthCommentService authCommentService;
    @Autowired
    private INotarizationMattersSpecialService mattersSpecialService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public AddNotarizationVO addRecord(AddNotarizationDTO dto) {
        User user = userService.getById(dto.getUserId());
        if (Objects.isNull(user)) {
            throw new GlobalException("用户信息异常，请尝试重新登录！");
        }
        // 更新用户信息
        user.setBirth(dto.getBirth());
        user.setGender(dto.getGender() == 1 ? 1 : 2);
        user.setIdCardAddress(dto.getIdCardAddress());
        if (user.getBirth().until(LocalDate.now()).getYears() < CommonConstant.EIGHTEEN) {
            throw new GlobalException("未满18岁，无法办理该公证！");
        }
        userService.updateById(user);
        // 订单表里面包含的快递信息
        Order order = new Order();
        Integer youJiType = dto.getIsSend();
        if (youJiType != null && youJiType == 1) {
            order.setIsSend(1);
            BeanUtils.copyProperties(dto, order);
        } else {
            order.setIsSend(0);
        }
        String orderNo = WxPayHelper.generateOrderNo();
        order.setId(UUID.randomUUID().toString().replace("-", ""));
        order.setOrderNo(orderNo);
        order.setUserId(dto.getUserId());
        order.setOuttradeNo("");
        order.setCreatedBy(dto.getUserId());
        order.setUpdatedBy(dto.getUserId());
        orderService.save(order);
        // 公证主表信息赋值
        NotarzationMaster notarzationMaster = this.formatNotarizationInfo(dto, order, user);
        // 保存主记录到公证表
        masterService.save(notarzationMaster);
        // 保存各种事项问题
        this.saveMattersInfo(notarzationMaster, dto, user);
        return AddNotarizationVO.builder()
                .recordId(notarzationMaster.getId())
                .build();
    }


    @Override
    public JSONObject getDetail(String masterId, String userId) {
        List<JSONObject> documents = documentService.getBaseMapper().associationDocument(masterId);
        // 主表
        NotarzationMaster master = masterService.getById(masterId);
        if (Objects.isNull(master)) {
            throw new GlobalException("参数异常，获取信息失败");
        }
        if (!Objects.equals(master.getUserId(), userId)) {
            throw new GlobalException("账号异常，获取信息失败");
        }
        // 资源
        Map<String, String> urlMap = new HashMap<>();
        List<String> supplementaryMaterialUrls = new ArrayList<>();
        // 被代理人证件材料
        List<String> supplementaryIdMaterialUrls = new ArrayList<>();
        // 当事人证件材料
        List<String> supplementaryIdMaterialUrlList= new ArrayList<>();
        // 营业执照
        List<String> businessLicenseUrlList = new ArrayList<>();
        // 其他资料
        List<String> otherAppendixUrlList = new ArrayList<>();
        // 声明书
        List<String> declarationUrlList =new ArrayList<>();
        // 委托书
        List<String> attorneyUrlList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(documents)) {
            for (JSONObject itemObj : documents) {
                String categoryCod = itemObj.getString("categoryCode");
                String uploadedAbsolutePath = itemObj.getString("uploadedAbsolutePath");
                uploadedAbsolutePath = StringUtil.tokenImgUrl(uploadedAbsolutePath);
                if ("MATERIAL-OT".equals(categoryCod)) {
                    supplementaryMaterialUrls.add(uploadedAbsolutePath);
                } else if ("ID-OTHER-CER".equals(categoryCod) && master.getIsAgent() == 0) {
                    supplementaryIdMaterialUrlList.add(uploadedAbsolutePath);
                } else if ("ID-OTHER-CER".equals(categoryCod)) {
                    supplementaryIdMaterialUrls.add(uploadedAbsolutePath);
                } else if ("ID-OTHER-CER-AGENT".equals(categoryCod)) {
                    supplementaryIdMaterialUrlList.add(uploadedAbsolutePath);
                } else if (Objects.equals("BUSINESS-LICENSE", categoryCod)) {
                    businessLicenseUrlList.add(uploadedAbsolutePath);
                } else if (Objects.equals("MATERIAL-OT2", categoryCod)) {
                    otherAppendixUrlList.add(uploadedAbsolutePath);
                } else if (Objects.equals("ATTORNEY", categoryCod)) {
                    attorneyUrlList.add(uploadedAbsolutePath);
                } else if (Objects.equals("DECLARATION", categoryCod)) {
                    declarationUrlList.add(uploadedAbsolutePath);
                } else {
                    urlMap.put(categoryCod, uploadedAbsolutePath);
                }
            }
        }
        // 订单信息
        Order order = orderService.getById(master.getOrderId());
        // 用户
        User user = userService.getById(userId);

        JSONObject resultObj=null;
        // 如果代理
        if (Objects.equals(master.getIsAgent(), StatusEnum.VALID.getCode())) {
            JSONObject resultObjAgent = new JSONObject();
            if (Objects.equals(master.getApplicantParty(), "P")) {
                resultObjAgent.put("realNameAgent", master.getRealName());
                resultObjAgent.put("birthAgent", master.getBirth());
                resultObjAgent.put("phoneAgent", master.getPhone());
                resultObjAgent.put("idCardTypeAgent", master.getIdCardType());
                resultObjAgent.put("idCardNoAgent", master.getIdCardNo());
                resultObjAgent.put("idCardAddressAgent", master.getIdCardAddress());
                resultObjAgent.put("genderAgent", master.getGender());
            }
            master.setRealName(user.getRealName());
            master.setBirth(user.getBirth());
            master.setPhone(user.getPhone());
            master.setIdCardType(user.getIdCardType());
            master.setIdCardNo(user.getIdCardNo());
            master.setIdCardAddress(user.getIdCardAddress());
            master.setGender(user.getGender());
            resultObj = JSONObject.parseObject(JSONObject.toJSONString(master));
            resultObj.putAll(resultObjAgent);
        } else {
            resultObj = JSONObject.parseObject(JSONObject.toJSONString(master));
        }

        NotarizationTypeEnum notarizationEnum = getEnumByCode(master.getNotarzationTypeCode());
        switch(notarizationEnum){
            case GR:
                //毕业信息学校
                NotarzationGraduation graduation = notarzationGraduationService.getById( masterId);
                if (graduation != null) {
                    resultObj.put("graduatedDate", graduation.getGraduatedDate());
                    resultObj.put("graduatedFrom", graduation.getGraduatedFrom());
                }
                break;
            case TAX:
                NotarizationTax notarizationTax = notarizationTaxService.getById(masterId);
                if (notarizationTax != null) {
                    resultObj.put("issuingAuthority", notarizationTax.getIssuingAuthority());
                    resultObj.put("issuingTime", DateUtils.format(notarizationTax.getIssuingTime(),"yyyy-MM-dd"));
                }
                break;
            case DC:
                NotarizationDegree notarizationDegree = notarizationDegreeService.getById(masterId);
                if (notarizationDegree != null) {
                    resultObj.put("issuingAuthority", notarizationDegree.getIssuingAuthority());
                    resultObj.put("grantTime", DateUtils.format(notarizationDegree.getGrantTime(),"yyyy-MM-dd"));
                    resultObj.put("degreeName", notarizationDegree.getDegreeName());
                }
                break;
            case DL:
                NotarizationDriverLicense notarizationDriverLicense = notarizationDriverLicenseService.getById(masterId);
                if (notarizationDriverLicense != null) {
                    resultObj.put("issuingAuthority", notarizationDriverLicense.getIssuingAuthority());
                    resultObj.put("issuingTime", DateUtils.format(notarizationDriverLicense.getIssuingTime(),"yyyy-MM-dd"));
                }
                break;
            // 委托买房
            case WTMFB01:
            // 委托卖房（直系亲属之间）
            case WTMFS01:
                List<NotarizationMattersSpecial> mattersSpecialList = mattersSpecialService.lambdaQuery()
                        .eq(NotarizationMattersSpecial::getNotarizationId, masterId)
                        .list();
                if (CollectionUtil.isNotEmpty(mattersSpecialList)) {
                    NotarizationSpecialDTO specialDTO = new NotarizationSpecialDTO();
                    // 输入项key、value转map
                    Map<String, String> entryMap = mattersSpecialList.parallelStream().collect(Collectors.toMap(NotarizationMattersSpecial::getEntryKey, NotarizationMattersSpecial::getEntryValue));
                    // 根据反射获取要返回前端的输入项的值
                    for (Field field : specialDTO.getClass().getDeclaredFields()) {
                        field.setAccessible(true);
                        resultObj.put(field.getName(), entryMap.get(field.getName()));
                    }
                }
                break;
            default:
                break;
        }

        //图片信息
        if(master.getIsAgent()==1){
            resultObj.put("avatarGatherUrl", urlMap.get("HEAD"));
            resultObj.put("idCardFrontUrlAgent", urlMap.get("ID-FRONT"));
            resultObj.put("idCardReverseUrlAgent", urlMap.get("ID-BACK"));
            resultObj.put("idCardFrontUrl", urlMap.get("ID-FRONT-AGENT"));
            resultObj.put("idCardReverseUrl", urlMap.get("ID-BACK-AGENT"));

        }else {
            resultObj.put("avatarGatherUrl", urlMap.get("HEAD"));
            resultObj.put("idCardFrontUrl", urlMap.get("ID-FRONT"));
            resultObj.put("idCardReverseUrl", urlMap.get("ID-BACK"));
        }
        //纳税个人
        if(master.getApplicantParty().equals("P")&&master.getNotarzationTypeCode().equals("TAX")){
            resultObj.put("householdUrl", urlMap.get("HOUSEHOLD"));
            resultObj.put("householdMainUrl", urlMap.get("HOUSEHOLD-MAIN"));
        }
        resultObj.put("materialUrlList", supplementaryMaterialUrls);
        resultObj.put("idMaterialUrlList", supplementaryIdMaterialUrls);
        resultObj.put("idMaterialUrls", supplementaryIdMaterialUrlList);
        resultObj.put("businessLicenseUrlList", businessLicenseUrlList);
        resultObj.put("otherAppendixUrlList", otherAppendixUrlList);
        resultObj.put("declarationUrl", declarationUrlList);
        resultObj.put("attorneyUrl", attorneyUrlList);

        //用户信息
        resultObj.put("userId", userId);
        resultObj.put("createdBy", user.getId());

        //订单信息
        resultObj.put("isSend", order.getIsSend());
        resultObj.put("sendtToName", order.getSendtToName());
        resultObj.put("sentToPhone", order.getSentToPhone());
        resultObj.put("sentToProvince", order.getSentToProvince());
        resultObj.put("sentToCity", order.getSentToCity());
        resultObj.put("sentToArea", order.getSentToArea());
        resultObj.put("sentToAddress", order.getSentToAddress());

        // 问答列表信息获取
        List<NotarizationMattersQuestion> mattersQuestionList = this.lambdaQuery()
                .eq(NotarizationMattersQuestion::getNotarizationId, masterId)
                .orderByAsc(NotarizationMattersQuestion::getSort)
                .list();
        // 被拒绝的组信息
        List<NotarzationAuthComment> commentList = authCommentService.lambdaQuery()
                .eq(NotarzationAuthComment::getNotarzationId, masterId)
                .list();
        List<MattersQuestionVO> mattersQuestion = Lists.newArrayList();
        if (CollectionUtil.isNotEmpty(commentList)) {
            commentList.forEach(model -> {
                Document itemDocument = documentService.getById(model.getReferrerId());
                if (Objects.nonNull(itemDocument)) {
                    model.setFieldName(itemDocument.getCategoryCode());
                }
                if (Objects.equals(model.getTableName(), "notarization_matters_question")) {
                    MattersQuestionVO mattersQuestionVO = new MattersQuestionVO();
                    mattersQuestionVO.setId(model.getReferrerId());
                    mattersQuestionVO.setComment(model.getComment());
                    mattersQuestion.add(mattersQuestionVO);
                }
            });
        }
        resultObj.put("mattersQuestionList", mattersQuestionList);
        resultObj.put("question", commentList);
        // 问题回答项有误的问题id集合
        resultObj.put("mattersQuestion", mattersQuestion);
        return resultObj;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateRecord(UpdateNotarizationDTO dto) {
        String masterId = dto.getId();
        User user = userService.getById(dto.getUserId());

        // 更新用户信息
        user.setBirth(dto.getBirth());
        user.setGender(dto.getGender() == 1 ? 1 : 2);
        user.setIdCardAddress(dto.getIdCardAddress());
        if (user.getBirth().until(LocalDate.now()).getYears() < CommonConstant.EIGHTEEN) {
            throw new GlobalException("未满18岁，无法办理该公证！");
        }
        userService.updateById(user);

        // 主表信息
        NotarzationMaster notarzationMaster = new NotarzationMaster();
        BeanUtils.copyProperties(dto, notarzationMaster);
        notarzationMaster.setStatus(NotarizationStatusEnum.PENDINGAPPROVED.getCode());
        notarzationMaster.setUpdatedTime(LocalDateTime.now());
        if (Objects.equals(dto.getIsAgent(), StatusEnum.VALID.getCode())) {
            this.formatAgentInfo(notarzationMaster, dto);
        }
        //如果代理企业更新主表
        if(dto.getApplicantParty().equals("E")){
            this.formatAgentEInfo(notarzationMaster, dto);
        }
        boolean flag = masterService.saveOrUpdate(notarzationMaster);
        // 快递信息
        Order order = orderService.getById(dto.getOrderId());
        if (Objects.nonNull(order)) {
            if (Objects.nonNull(dto.getIsSend()) && Objects.equals(dto.getIsSend(), StatusEnum.VALID.getCode())) {
                order.setIsSend(1);
                BeanUtils.copyProperties(dto, order);
            } else {
                order.setIsSend(0);
            }
            order.setUpdatedBy(dto.getUserId());
            orderService.saveOrUpdate(order);
        }
        // 四个事项特殊处理
        NotarizationTypeEnum notarizationEnum = getEnumByCode(notarzationMaster.getNotarzationTypeCode());
        switch (notarizationEnum){
            case GR:
                // 学校信息
                NotarzationGraduation notarzationGraduation = notarzationGraduationService.getById(masterId);
                if (Objects.nonNull(notarzationGraduation)) {
                    notarzationGraduation.setGraduatedFrom(dto.getGraduatedFrom());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    notarzationGraduation.setGraduatedDate(LocalDate.parse(dto.getGraduatedDate(), df));
                }
                notarzationGraduationService.saveOrUpdate(notarzationGraduation);
                break;
            case TAX:
                // 完税
                NotarizationTax notarizationTax = notarizationTaxService.getById(masterId);
                if (Objects.nonNull(notarizationTax)) {
                    notarizationTax.setIssuingAuthority(dto.getIssuingAuthority());
                    notarizationTax.setIssuingTime(DateUtils.stringToDate(dto.getIssuingTime(), "yyyy-MM-dd"));
                }
                notarizationTaxService.saveOrUpdate(notarizationTax);
                break;
            case DC:
                // 学位
                NotarizationDegree notarizationDegree = notarizationDegreeService.getById(masterId);
                if (Objects.nonNull(notarizationDegree)) {
                    notarizationDegree.setIssuingAuthority(dto.getIssuingAuthority());
                    notarizationDegree.setDegreeName(dto.getDegreeName());
                    notarizationDegree.setGrantTime(DateUtils.stringToDate(dto.getGrantTime(), "yyyy-MM-dd"));
                }
                notarizationDegreeService.saveOrUpdate(notarizationDegree);
                break;
            case DL:
                //驾驶
                NotarizationDriverLicense notarizationDriverLicense = notarizationDriverLicenseService.getById(masterId);
                if (Objects.nonNull(notarizationDriverLicense)) {
                    notarizationDriverLicense.setIssuingAuthority(dto.getIssuingAuthority());
                    notarizationDriverLicense.setIssuingTime(DateUtils.stringToDate(dto.getIssuingTime(), "yyyy-MM-dd"));
                }
                notarizationDriverLicenseService.saveOrUpdate(notarizationDriverLicense);
                break;
            // 委托买房
            case WTMFB01:
            // 委托卖房（直系亲属之间）
            case WTMFS01:
                mattersSpecialService.remove(new LambdaQueryWrapper<>(NotarizationMattersSpecial.class).eq(NotarizationMattersSpecial::getNotarizationId, masterId));
                this.saveSpecialMatters(notarzationMaster, dto, notarizationEnum);
                break;
            default:
                break;
        }
        // 更新事项问答结果
        // 先删除旧的问答
        this.remove(new LambdaQueryWrapper<>(NotarizationMattersQuestion.class).eq(NotarizationMattersQuestion::getNotarizationId, masterId));
        List<AddNotarizationDTO.MatterQuestion> mattersQuestionList = dto.getMattersQuestionList();
        if (CollectionUtil.isNotEmpty(mattersQuestionList)) {
            List<NotarizationMattersQuestion> questionList = Lists.newArrayList();
            mattersQuestionList.forEach(model -> {
                NotarizationMattersQuestion question = new NotarizationMattersQuestion();
                BeanUtils.copyProperties(model, question);
                question.setId(UUID.randomUUID().toString().replace("-", ""));
                question.setType(notarzationMaster.getNotarzationTypeCode());
                question.setNotarizationId(notarzationMaster.getId());
                question.setCreatedBy(dto.getUserId());
                question.setUpdatedBy(dto.getUserId());
                questionList.add(question);
            });
            this.saveBatch(questionList);
        }
        // 对于附件材料，先删除该公证事项中所有的附件，再新增当前所有的
        documentService.remove(new LambdaQueryWrapper<>(Document.class).eq(Document::getRefererId, masterId));
        this.saveDocuments(notarzationMaster, dto);
        // 将评论表里面的数据进行删除
        authCommentService.remove(Wrappers.<NotarzationAuthComment>query().lambda()
                .eq(NotarzationAuthComment::getNotarzationId, masterId));
        return flag;
    }

    /**
     * 公证主表信息赋值
     *
     * @param dto
     * @param order
     * @param user
     * @return
     */
    private NotarzationMaster formatNotarizationInfo(AddNotarizationDTO dto, Order order, User user) {
        NotarzationMaster notarzationMaster = new NotarzationMaster();
        BeanUtils.copyProperties(dto, notarzationMaster);
        notarzationMaster.setId(UUID.randomUUID().toString().replace("-", ""));
        notarzationMaster.setUserId(dto.getUserId());
        notarzationMaster.setOrderId(order.getId());
        notarzationMaster.setRealName(user.getRealName());
        notarzationMaster.setBirth(user.getBirth());
        notarzationMaster.setPhone(user.getPhone());
        notarzationMaster.setCreatedBy(user.getId());
        notarzationMaster.setProcessNo(GenerateStrUtil.getProcessNo());
        notarzationMaster.setUpdatedBy(dto.getUserId());
        notarzationMaster.setNotarialOfficeId(JwtUtils.getSysNotarialOffice().getId());
        // 因为不允许为空,所以给一些默认数据
        notarzationMaster.setUsedToProvince("");
        notarzationMaster.setUsedToCity("");
        // 保存附件信息
        this.saveDocuments(notarzationMaster, dto);
        // 判断是否一年内提交
        List<NotarzationMaster> notarizationMasters = masterService.getBaseMapper().getIsRepeat(dto.getUserId(), notarzationMaster.getNotarzationTypeCode(), notarzationMaster.getIsAgent());
        if (CollectionUtil.isNotEmpty(notarizationMasters)) {
            notarzationMaster.setIsRepeat(1);
        }
        return notarzationMaster;
    }


    /**
     * 事项明细问题保存
     *
     * @param notarzationMaster
     * @param dto
     * @param user
     */
    @SneakyThrows
    private void saveMattersInfo(NotarzationMaster notarzationMaster, AddNotarizationDTO dto, User user) {
        NotarizationTypeEnum notarizationEnum = getEnumByCode(notarzationMaster.getNotarzationTypeCode());
        switch (notarizationEnum) {
            case GR:
                // 生成学校信息
                NotarzationGraduation notarzationGraduation = new NotarzationGraduation();
                notarzationGraduation.setId(notarzationMaster.getId());
                notarzationGraduation.setGraduatedFrom(dto.getGraduatedFrom());
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                notarzationGraduation.setGraduatedDate(LocalDate.parse(dto.getGraduatedDate(), df));
                notarzationGraduation.setCreatedBy(user.getId());
                notarzationGraduation.setUpdatedBy(user.getId());
                notarzationGraduationService.save(notarzationGraduation);
                break;
            case TAX:
                // 生成完税
                NotarizationTax notarizationTax = new NotarizationTax();
                notarizationTax.setId(notarzationMaster.getId());
                notarizationTax.setIssuingAuthority(dto.getIssuingAuthority());
                notarizationTax.setIssuingTime(DateUtils.stringToDate(dto.getIssuingTime(), "yyyy-MM-dd"));
                notarizationTax.setUpdatedTime(new Date());
                notarizationTax.setCreatedTime(new Date());
                notarizationTax.setUpdatedBy(user.getId());
                notarizationTax.setCreatedBy(user.getId());
                notarizationTaxService.save(notarizationTax);
                break;
            case DC:
                // 学位
                NotarizationDegree notarizationDegree = new NotarizationDegree();
                notarizationDegree.setId(notarzationMaster.getId());
                notarizationDegree.setIssuingAuthority(dto.getIssuingAuthority());
                notarizationDegree.setDegreeName(dto.getDegreeName());
                notarizationDegree.setGrantTime(DateUtils.stringToDate(dto.getGrantTime(), "yyyy-MM-dd"));
                notarizationDegree.setUpdatedTime(new Date());
                notarizationDegree.setCreatedTime(new Date());
                notarizationDegree.setUpdatedBy(user.getId());
                notarizationDegree.setCreatedBy(user.getId());
                notarizationDegreeService.save(notarizationDegree);
                break;
            case DL:
                // 驾驶
                NotarizationDriverLicense notarizationDriverLicense = new NotarizationDriverLicense();
                notarizationDriverLicense.setId(notarzationMaster.getId());
                notarizationDriverLicense.setIssuingAuthority(dto.getIssuingAuthority());
                notarizationDriverLicense.setIssuingTime(DateUtils.stringToDate(dto.getIssuingTime(), "yyyy-MM-dd"));
                notarizationDriverLicense.setUpdatedTime(new Date());
                notarizationDriverLicense.setCreatedTime(new Date());
                notarizationDriverLicense.setUpdatedBy(user.getId());
                notarizationDriverLicense.setCreatedBy(user.getId());
                notarizationDriverLicenseService.save(notarizationDriverLicense);
                break;
            // 委托买房
            case WTMFB01:
            // 委托卖房（直系亲属之间）
            case WTMFS01:
                this.saveSpecialMatters(notarzationMaster, dto, notarizationEnum);
                break;
            default:
                break;
        }
        // 保存事项问答结果
        List<AddNotarizationDTO.MatterQuestion> mattersQuestionList = dto.getMattersQuestionList();
        if (CollectionUtil.isNotEmpty(mattersQuestionList)) {
            List<NotarizationMattersQuestion> questionList = Lists.newArrayList();
            mattersQuestionList.forEach(model -> {
                NotarizationMattersQuestion question = new NotarizationMattersQuestion();
                BeanUtils.copyProperties(model, question);
                question.setId(UUID.randomUUID().toString().replace("-", ""));
                question.setType(notarzationMaster.getNotarzationTypeCode());
                question.setNotarizationId(notarzationMaster.getId());
                question.setCreatedBy(dto.getUserId());
                question.setUpdatedBy(dto.getUserId());
                questionList.add(question);
            });
            this.saveBatch(questionList);
        }
    }


    /**
     * 代理信息赋值
     *
     * @param notarzationMaster
     * @param dto
     */
    private void formatAgentInfo(NotarzationMaster notarzationMaster, AddNotarizationDTO dto) {
        notarzationMaster.setRealName(dto.getRealNameAgent());
        notarzationMaster.setBirth(dto.getBirthAgent());
        notarzationMaster.setIdCardType(dto.getIdCardTypeAgent());
        notarzationMaster.setIdCardNo(dto.getIdCardNoAgent());
        notarzationMaster.setIdCardAddress(dto.getIdCardAddressAgent());
        notarzationMaster.setGender(dto.getGenderAgent());
    }


    /**
     * 代理企业信息赋值
     *
     * @param notarzationMaster
     * @param dto
     */
    private void formatAgentEInfo(NotarzationMaster notarzationMaster, AddNotarizationDTO dto) {
        notarzationMaster.setRealName(dto.getCompanyName());
        notarzationMaster.setBirth(dto.getRegisterDate());
        notarzationMaster.setIdCardNo(dto.getSocialCreditCode());
        notarzationMaster.setIdCardAddress(dto.getCompanyAddress());
    }



    /**
     * 附件保存，及NotarzationMaster部分字段赋值
     *
     * @param notarzationMaster
     * @param dto
     */
    private void saveDocuments(NotarzationMaster notarzationMaster, AddNotarizationDTO dto){
        // 如果是代理则更新主表
        if (Objects.equals(dto.getIsAgent(), StatusEnum.VALID.getCode())) {
            // 插入文档几条数据 ,代理数据
            // 代理的情况  源文件都是代理人证件
            if (Objects.equals(dto.getIdCardType(), CommonConstant.ID_NAME)) {
                masterService.saveDocument(dto.getIdCardFrontUrl(), "ID-FRONT-AGENT", notarzationMaster.getId());
                masterService.saveDocument(dto.getIdCardReverseUrl(), "ID-BACK-AGENT", notarzationMaster.getId());
            } else {
                List<String> materialUrlList = dto.getIdMaterialUrls();
                if (CollectionUtil.isNotEmpty(materialUrlList)) {
                    materialUrlList.forEach(model -> {
                        String url = StringUtil.removeToken(model);
                        // 代理人身份材料
                        masterService.saveDocument(url, "ID-OTHER-CER-AGENT", notarzationMaster.getId());
                    });
                }
            }
            switch (dto.getApplicantParty()) {
                // 代理者是个人
                case "P":
                    // 字段赋值
                    this.formatAgentInfo(notarzationMaster, dto);
                    // 上传身份证 则正反面   否则  统一其他身份材料
                    if (Objects.equals(dto.getIdCardTypeAgent(), CommonConstant.ID_NAME)) {
                        masterService.saveDocument(dto.getIdCardFrontUrlAgent(), "ID-FRONT", notarzationMaster.getId());
                        masterService.saveDocument(dto.getIdCardReverseUrlAgent(), "ID-BACK", notarzationMaster.getId());
                    } else {
                        // 得到上传的素材List
                        List<String> materialUrlList = dto.getIdMaterialUrlList();
                        if (CollectionUtil.isNotEmpty(materialUrlList)) {
                            materialUrlList.forEach(model -> {
                                String url = StringUtil.removeToken(model);
                                // 其他身份材料
                                masterService.saveDocument(url, "ID-OTHER-CER", notarzationMaster.getId());
                            });
                        }
                    }
                    if (Objects.equals(dto.getNotarzationTypeCode(),"TAX")) {
                        masterService.saveDocument(dto.getHouseholdUrl(), "HOUSEHOLD", notarzationMaster.getId());
                        masterService.saveDocument(dto.getHouseholdMainUrl(), "HOUSEHOLD-MAIN", notarzationMaster.getId());
                    }
                    masterService.batchSaveDocument(dto.getAttorneyUrl(), "ATTORNEY", notarzationMaster.getId());
                    break;
                // 代理者是企业
                case "E":
                    this.formatAgentEInfo(notarzationMaster, dto);
                    masterService.batchSaveDocument(dto.getBusinessLicenseUrlList(), "BUSINESS-LICENSE", notarzationMaster.getId());
                    //代理企业非法人上传委托书
                    if(notarzationMaster.getLegalStatus()==0){
                        masterService.batchSaveDocument(dto.getAttorneyUrl(), "ATTORNEY", notarzationMaster.getId());
                    }
                    break;
                default:
                    break;
            }
            // 头像和声明书
            masterService.saveDocument(dto.getAvatarGatherUrl(), "HEAD", notarzationMaster.getId());
            masterService.batchSaveDocument(dto.getDeclarationUrl(), "DECLARATION", notarzationMaster.getId());
        } else {
            masterService.saveDocument(dto.getAvatarGatherUrl(), "HEAD", notarzationMaster.getId());
            masterService.batchSaveDocument(dto.getDeclarationUrl(), "DECLARATION", notarzationMaster.getId());
            if (Objects.equals(dto.getIdCardType(), CommonConstant.ID_NAME)) {
                masterService.saveDocument(dto.getIdCardFrontUrl(), "ID-FRONT", notarzationMaster.getId());
                masterService.saveDocument(dto.getIdCardReverseUrl(), "ID-BACK", notarzationMaster.getId());
            } else {
                List<String> materialUrlList = dto.getIdMaterialUrls();
                if (CollectionUtil.isNotEmpty(materialUrlList)) {
                    materialUrlList.forEach(model -> {
                        String url = StringUtil.removeToken(model);
                        //身份材料
                        masterService.saveDocument(url, "ID-OTHER-CER", notarzationMaster.getId());
                    });
                }
            }
        }
        // 材料与其他材料
        List<String> materialUrlList = dto.getMaterialUrlList();
        if (CollectionUtil.isNotEmpty(materialUrlList)) {
            materialUrlList.forEach(model -> {
                String url = StringUtil.removeToken(model);
                masterService.saveDocument(url, "MATERIAL-OT", notarzationMaster.getId());
            });
        }
        List<String> otherAppendixUrlList = dto.getOtherAppendixUrlList();
        if (CollectionUtil.isNotEmpty(otherAppendixUrlList)) {
            otherAppendixUrlList.forEach(model -> {
                String url = StringUtil.removeToken(model);
                masterService.saveDocument(url, "MATERIAL-OT2", notarzationMaster.getId());
            });
        }
    }


    /**
     * 利用反射保存特殊事项的特殊字段
     *
     * @param notarzationMaster
     * @param dto
     * @param notarizationEnum
     */
    @SneakyThrows
    private void saveSpecialMatters(NotarzationMaster notarzationMaster, AddNotarizationDTO dto, NotarizationTypeEnum notarizationEnum) {
        List<NotarizationMattersSpecial> mattersSpecialList = Lists.newArrayList();
        NotarizationSpecialDTO specialDTO = new NotarizationSpecialDTO();
        BeanUtils.copyProperties(dto, specialDTO);
        // 用反射进行集体赋值
        for (Field field : specialDTO.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(specialDTO);
            if (Objects.nonNull(value)) {
                NotarizationMattersSpecial mattersSpecial = new NotarizationMattersSpecial();
                mattersSpecial.setId(UUID.randomUUID().toString().replace("-", ""));
                mattersSpecial.setNotarizationId(notarzationMaster.getId());
                mattersSpecial.setNotarizationType(notarizationEnum.getCode());
                mattersSpecial.setEntryKey(field.getName());
                mattersSpecial.setEntryValue(value.toString());
                mattersSpecial.setEntryType("string");
                mattersSpecial.setCreatedBy(dto.getUserId());
                mattersSpecial.setUpdatedBy(dto.getUserId());
                mattersSpecialList.add(mattersSpecial);
            }
        }
        if (CollectionUtil.isNotEmpty(mattersSpecialList)) {
            mattersSpecialService.saveBatch(mattersSpecialList);
        }
    }
}
