package com.lianzheng.h5.controller;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lianzheng.h5.common.ApiResult;
import com.lianzheng.h5.common.RedisHelper;
import com.lianzheng.h5.entity.*;
import com.lianzheng.h5.jwt.util.JwtUtils;
import com.lianzheng.h5.mapper.NotarizationDegreeMapper;
import com.lianzheng.h5.mapper.NotarizationDriverLicenseMapper;
import com.lianzheng.h5.mapper.NotarizationTaxMapper;
import com.lianzheng.h5.pay.WxPayHelper;
import com.lianzheng.h5.service.impl.*;
import com.lianzheng.h5.util.DateUtils;
import com.lianzheng.h5.util.GenerateStrUtil;
import com.lianzheng.h5.util.ListUtil;
import com.lianzheng.h5.util.StringUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
@RequestMapping("/notarzation-master")
@Api(tags = "证书认证主表")
public class NotarzationMasterController {

    @Autowired
    RedisHelper redisHelper;

    @Autowired
    NotarzationMasterServiceImpl masterService;

    @Autowired
    NotarzationAuthCommentServiceImpl authCommentServiceImpl;

    @Autowired
    DocumentServiceImpl documentService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    OrderServiceImpl orderService;

    @Autowired
    NotarzationGraduationServiceImpl notarzationGraduationService;
    @Autowired
    private  NotarizationTaxMapper  notarizationTaxMapper;
    @Autowired
    private NotarizationDegreeMapper notarizationDegreeMapper;
    @Autowired
    private NotarizationDriverLicenseMapper notarizationDriverLicenseMapper;

    @Value("${ks.generateReceipt}")
    String generateReceipt;

    @ResponseBody
    @RequestMapping(value = "/homeList", method = RequestMethod.POST)
    @ApiOperation(value = "首页列表")
    public ApiResult<?> homeList(@RequestParam("page")Integer page,@RequestParam("limit")Integer limit) {
        String userId = JwtUtils.getTokenUserId();
        User user = userService.getOne(Wrappers.<User>lambdaQuery()
                .eq(User::getId, userId), false);
        if (user == null) {
            return ApiResult.error("没有该用户");
        }
        IPage iPage = new Page(page,limit);
        IPage<Map<String, Object>> jsonObjects = masterService.getBaseMapper().homeList(iPage,userId,JwtUtils.getSysNotarialOffice().getId());

        return ApiResult.success(jsonObjects);
    }


    @ResponseBody
    @PostMapping("/generate-receipt")
    @ApiOperation(value = "通知已经签名,需要生成回执订单")
    public ApiResult<?> generateReceipt(@RequestParam("masterId") String masterId) {
        NotarzationMaster master = masterService.getOne(
                Wrappers.<NotarzationMaster>lambdaQuery().eq(NotarzationMaster::getId, masterId));
        master.setSignedReceipt(1);
        masterService.saveOrUpdate(master);

        String generateReceiptUrl = generateReceipt + masterId;
        log.info(generateReceiptUrl);
        String response = HttpUtil.get(generateReceiptUrl);
        log.info(response);

        return ApiResult.success("成功");
    }

    @ResponseBody
    @PostMapping("/get-used-to-country")
    @ApiOperation(value = "得到使用地是不是中国")
    public ApiResult<?> getUsedToCountry(@RequestParam("masterId") String masterId) {

        //检查当前记录使用地是否是国外或者港澳台地区
        NotarzationMaster master = masterService.getOne(
                Wrappers.<NotarzationMaster>lambdaQuery()
                        .eq(NotarzationMaster::getId, masterId)
        );
        String usedCountry = master.getUsedToCountry();

        boolean isChinaMainland = false;
        if ("中国".equals(usedCountry)) {
            isChinaMainland = true;
        }

        Map<String, Object> mapObj = new HashMap<>();
        mapObj.put("isChinaMainland", isChinaMainland);
        return ApiResult.success(mapObj);
    }


    @ResponseBody
    @PostMapping("/isRepeat")
    @ApiOperation(value = "判断近期有没有重复提交过")
    public ApiResult<?> isRepeat(@RequestParam String contentJson){
        String userId = JwtUtils.getTokenUserId();
        NotarzationMaster notarzationMaster = JSON.parseObject(contentJson, NotarzationMaster.class);
        List<NotarzationMaster> notarzationMasters = masterService.getBaseMapper().getIsRepeat(userId,notarzationMaster.getNotarzationTypeCode(),notarzationMaster.getIsAgent());
        if (notarzationMasters!=null&&notarzationMasters.size()>0&&notarzationMaster.getIsAgent()==0){
            return ApiResult.success(true);
        }else {
            return ApiResult.success(false);
        }
    }


    public static void main(String[] args) {
        List<String> AList = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f","i"));
        List<String> BList = new ArrayList<>(Arrays.asList("i"));

        List<String> diffAList = ListUtil.getNeedAddOpenidList(AList, BList);

        List<String> diffBList = ListUtil.getNeedAddOpenidList(BList, AList);
        System.out.println("需要删除:" + diffAList);
        System.out.println("需要增加:" + diffBList);
    }

}
