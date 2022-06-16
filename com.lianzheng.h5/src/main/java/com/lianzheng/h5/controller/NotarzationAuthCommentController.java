package com.lianzheng.h5.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lianzheng.h5.common.ApiResult;
import com.lianzheng.h5.entity.NotarzationAuthComment;
import com.lianzheng.h5.service.impl.NotarzationAuthCommentServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 公证申请修改内容表 前端控制器
 * </p>
 *
 * @author 代树鸣
 * @since 2021-12-07
 */
@Slf4j
@RestController
@RequestMapping("/notarzation-auth-comment")
@Api(tags = "有问题的")
public class NotarzationAuthCommentController {

    @Autowired
    NotarzationAuthCommentServiceImpl authCommentService;

    @Autowired
    NotarzationMasterController masterController;

    /**
     * [
     * {
     * materUrl: "",
     * authId: "",//这条记录的id
     * }
     * ]
     */
//    @ResponseBody
//    @RequestMapping(value = "/uploadMaterials", method = RequestMethod.POST)
//    @ApiOperation(value = "上传材料")
//    public ApiResult<?> homeList(@RequestParam("userId") String userId,
//                                 @RequestParam("masterId") String masterId,
//                                 @RequestParam("contentJson") String contentJson) {
//
//        JSONArray objects = JSONArray.parseArray(contentJson);
//        for (Object obj : objects) {
//            JSONObject itemJsonObj = (JSONObject) obj;
//
//            String materialUrl = itemJsonObj.getString("materUrl");
//            masterController.fileUrlParamsToDocument(materialUrl, "MATERIAL_OT", masterId);
//
//            String authId = itemJsonObj.getString("authId");
//            NotarzationAuthComment notarzationAuthComment = authCommentService.getOne(
//                    Wrappers.<NotarzationAuthComment>lambdaQuery()
//                            .eq(NotarzationAuthComment::getId, authId), false);
//            notarzationAuthComment.setStatus(1);
//            authCommentService.saveOrUpdate(notarzationAuthComment);
//        }
//        return ApiResult.success("");
//    }
}
