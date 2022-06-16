package com.lianzheng.h5.controller;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.lianzheng.h5.common.ApiResult;
import com.lianzheng.h5.dto.AddNotarizationDTO;
import com.lianzheng.h5.dto.UpdateNotarizationDTO;
import com.lianzheng.h5.entity.NotarzationMaster;
import com.lianzheng.h5.entity.User;
import com.lianzheng.h5.jwt.util.JwtUtils;
import com.lianzheng.h5.service.INotarizationMattersQuestionService;
import com.lianzheng.h5.service.IUserService;
import com.lianzheng.h5.util.FileStorageByNotaryUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 公证申请主体表 前端控制器
 * </p>
 *
 * @author 代树鸣
 * @since 2021-12-07
 */

@Slf4j
@RestController
@RequestMapping("notarization")
@Api(tags = "认证其他事项操作API")
public class NotarizationController {

    @Autowired
    private INotarizationMattersQuestionService mattersQuestionService;
    @Autowired
    IUserService userService;

    @PostMapping(value = "initRecord")
    @ApiOperation(value = "笔录的初始化数据")
    public ApiResult<?> initRecord() {
        String userId = JwtUtils.getTokenUserId();
        User user = userService.getById(userId);
        if (Objects.isNull(user)) {
            return ApiResult.error("没有该用户");
        }
        NotarzationMaster notarzationMaster = new NotarzationMaster();
        notarzationMaster.setRealName(user.getRealName());
        notarzationMaster.setIdCardType(user.getIdCardType());
        notarzationMaster.setIdCardNo(user.getIdCardNo());
        notarzationMaster.setGender(user.getGender());
        notarzationMaster.setIsAgent(0);
        notarzationMaster.setPhone(user.getPhone());
        notarzationMaster.setBirth(user.getBirth());
        notarzationMaster.setExpressModeToSEF("S");
        Map<String, Object> resultObj = (Map<String, Object>) JSON.toJSON(notarzationMaster);
        resultObj.put("avatarGatherUrl", "");
        resultObj.put("idCardFrontUrlAgent", "");
        resultObj.put("idCardReverseUrlAgent", "");
        resultObj.put("idCardFrontUrl", "");
        resultObj.put("idCardReverseUrl", "");
        resultObj.put("attorneyUrl", "");
        resultObj.put("declarationUrl", "");
        resultObj.put("householdUrl", "");
        resultObj.put("householdMainUrl", "");
        resultObj.put("materialUrlList", "");
        resultObj.put("idMaterialUrlList", "");
        resultObj.put("idMaterialUrls", "");
        // 事项新增字段
        // 营业执照
        resultObj.put("businessLicenseUrlList", "");
        // 其他资料
        resultObj.put("otherAppendixUrlList", "");
        // 问答集合
        resultObj.put("mattersQuestionList", "");
        // 房屋类型
        resultObj.put("roomType", "");
        // 受托人姓名
        resultObj.put("trusteeName", "");
        // 受托人性别
        resultObj.put("trusteeGender", "");
        // 受托人出生日期
        resultObj.put("trusteeBirthday", "");
        // 受托人身份证号
        resultObj.put("trusteeIdNum", "");
        //与委托人关系
        resultObj.put("trusteeRelation", "");
        // 房屋地址
        resultObj.put("roomAddress", null);
        // 不动产权证书编号/房屋所有权证
        resultObj.put("ownershipCertificate", null);
        //房屋所有权证
        resultObj.put("ownershipSCertificate","");
        // 国有土地使用证
        resultObj.put("landUseCertificate", "");
        // 企业类型
        resultObj.put("companyType", null);
        // 注册/成立日期
        resultObj.put("registerDate", null);
        // 公司委托书、法人身份证复印件
        resultObj.put("entrustUrlList", "");
        return ApiResult.success(resultObj);
    }


    @PostMapping(value = "createRecord")
    @ApiOperation(value = "新增认证")
    public ApiResult<?> createRecord(@RequestParam String contentJson) {
        AddNotarizationDTO addNotarizationDTO = JSONUtil.toBean(contentJson, AddNotarizationDTO.class,true);
        addNotarizationDTO.setUserId(JwtUtils.getTokenUserId());
        return ApiResult.success(mattersQuestionService.addRecord(addNotarizationDTO));
    }


    @GetMapping(value = "detail")
    @ApiOperation(value = "认证详情")
    public ApiResult<?> detail(@RequestParam("masterId") String masterId) {
        return ApiResult.success(mattersQuestionService.getDetail(masterId, JwtUtils.getTokenUserId()));
    }


    @PostMapping(value = "updateRecord")
    @ApiOperation(value = "认证更新")
    public ApiResult<?> updateRecord(@RequestParam String contentJson) {
        UpdateNotarizationDTO updateNotarizationDTO = JSONUtil.toBean(contentJson, UpdateNotarizationDTO.class);
        updateNotarizationDTO.setUserId(JwtUtils.getTokenUserId());
        return ApiResult.success(mattersQuestionService.updateRecord(updateNotarizationDTO));
    }


}
