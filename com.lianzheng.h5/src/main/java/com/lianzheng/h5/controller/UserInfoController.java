package com.lianzheng.h5.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lianzheng.h5.common.ApiResult;
import com.lianzheng.h5.common.RedisHelper;
import com.lianzheng.h5.dto.UserDTO;
import com.lianzheng.h5.entity.User;
import com.lianzheng.h5.entity.UserNotarization;
import com.lianzheng.h5.enums.IdCardTypeEnum;
import com.lianzheng.h5.form.IdentifyForm;
import com.lianzheng.h5.helper.UserInfoHelper;
import com.lianzheng.h5.helper.WxHelper;
import com.lianzheng.h5.jwt.IgnoreAuth;
import com.lianzheng.h5.jwt.util.JwtUtils;
import com.lianzheng.h5.service.IUserNotarizationService;
import com.lianzheng.h5.service.impl.UserServiceImpl;
import com.lianzheng.h5.util.IdCardUtil;
import com.lianzheng.h5.util.MessageUtils;
import com.lianzheng.h5.util.WxUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "用户接口")
public class UserInfoController {

    @Autowired
    UserServiceImpl userService;
    @Autowired
    private IdentifyForm identifyForm;
    @Value("${wx.appid}")
    private String appid;
    @Value("${wx.secret}")
    private String secret;
    @Autowired
    private IUserNotarizationService userNotarizationService;
    @Autowired
    private RedisHelper redisHelper;

    @Value("${messages.signName}")
    private String signName;

    @Autowired
    private  WxUtils wxUtils;


    @ResponseBody
    @RequestMapping(value = "/login_callback", method = RequestMethod.POST)
    @ApiOperation(value = "传入code 自动登录后的回调")
    @IgnoreAuth
    public ApiResult<?> loginCallback(@RequestParam("code")String code) {
        JSONObject responseObject = UserInfoHelper.getInfo(code,identifyForm);
        log.info(responseObject.toJSONString());

        if (responseObject.getInteger("result") != 1) {
            return ApiResult.error("登录失败" + responseObject);
        }

        ///在数据库中添加或者刷新这条记录
        JSONObject responseUserInfo = responseObject.getJSONObject("data");
        String userId = responseUserInfo.getString("userid");

        if(responseUserInfo.getString("certno")==null||responseUserInfo.getString("certno").equals("")){
            return ApiResult.error("鹿路通未认证，登录失败");
        }

        if(responseUserInfo.getString("certlevel")!=null&&responseUserInfo.getString("certlevel").equals("L2")){
            return ApiResult.error("鹿路通认证等级不足L3，登录失败");
        }
        String mobile = responseUserInfo.getString("mobile");
        User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getPhone, mobile), false);
        if (user == null) {
            user = new User();
            user.setId(userId);
            user.setCreatedTime(LocalDateTime.now());
            user.setGender(responseUserInfo.getBoolean("sex")==null?1:responseUserInfo.getBoolean("sex")?1:2);//性别第一次记录后不再更新
        }
        user.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        user.setRealName(responseUserInfo.getString("certname"));
        user.setIdCardNo(responseUserInfo.getString("certno"));
        user.setPhone(responseUserInfo.getString("mobile"));
        user.setAvatarUrl(responseUserInfo.getString("headimg"));
        Integer certnotype = responseUserInfo.getInteger("certnotype");
        user.setIdCardType(IdCardTypeEnum.getEnumMsg(certnotype.toString()));
        user.setLastLoginTime(LocalDateTime.now());

        if(certnotype==1){//如果是身份证更新性别和生日
            Map<String,String> idCardMessage =  IdCardUtil.getBirthdayAgeSex(responseUserInfo.getString("certno"));
            user.setBirth(LocalDate.parse(idCardMessage.get("birthday")));
            user.setGender(idCardMessage.get("sex").equals("1")?1:2);
        }

        userService.saveOrUpdate(user);

        String token = JwtUtils.getToken(user.getId(),user.getPassword(),user.getRealName());
        //用户密码字段不返回！
        user.setPassword("");
        Map<String,Object> resultObj= (Map<String,Object>) JSON.toJSON(user);
        resultObj.put("token" ,token);
        return ApiResult.success(resultObj);
    }


    @ResponseBody
    @RequestMapping(value = "/login_accounts", method = RequestMethod.POST)
    @ApiOperation(value = "传入code和公证处id")
    @IgnoreAuth
    public ApiResult<?> loginAccounts(@RequestParam("code")String code,@RequestParam("index")String index) {
        JSONObject jscode = WxHelper.access_token(appid,secret,code);
        if (StringUtils.contains(jscode.toJSONString(), "errcode")) {
            //校验出错
            return ApiResult.error("微信授权失败");
        }

        String openid = jscode.getString("openid");
        UserNotarization userNotarization = userNotarizationService.getOne(
                new QueryWrapper<UserNotarization>().eq("openId",openid).eq("notarialOfficeId",index)
        );

        if(Objects.isNull(userNotarization)){
            JSONObject object =new  JSONObject();
            object.put("openid",openid);
            return ApiResult.errorData(233,object,"请注册或登录");
        }


        User user = userService.getById(userNotarization.getUserId());
        user.setLastLoginTime(LocalDateTime.now());
        userService.saveOrUpdate(user);


        user.setPassword("");
        Map<String,Object> resultObj= (Map<String,Object>) JSON.toJSON(user);
        String token = JwtUtils.getToken(user.getId(),user.getId(),user.getId());
        resultObj.put("token" ,token);
        return ApiResult.success(resultObj);

    }


    @ResponseBody
    @RequestMapping(value = "/login_phone", method = RequestMethod.POST)
    @ApiOperation(value = "手机号登录")
    @IgnoreAuth
    public ApiResult<?> loginPhone(@RequestParam("code")String code,@RequestParam("phone")String phone,@RequestParam("index")BigInteger index,@RequestParam("openid")String openid){
        String codePhone = redisHelper.get(phone);
        if(StringUtils.isEmpty(codePhone)||!code.equals(codePhone)){
            return ApiResult.error("验证码错误");
        }


        User user = userService.getOne(
                new QueryWrapper<User>().eq("phone",phone)
        );

        //如果手机没注册过  直接注册
        if (user == null ){
            user = new User();
            user.setId(UUID.randomUUID().toString());
            user.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
            user.setLastLoginTime(LocalDateTime.now());
            user.setPhone(phone);
            userService.save(user);
        }


        //需要登录代表没有关联表
        UserNotarization userNotarization = new UserNotarization();
        userNotarization.setId(UUID.randomUUID().toString());
        userNotarization.setUserId(user.getId());
        userNotarization.setOpenId(openid);
        userNotarization.setNotarialOfficeId(index);
        userNotarization.setCreatedTime(new Date());
        userNotarizationService.remove(
                new QueryWrapper<UserNotarization>().eq("openId",openid).eq("notarialOfficeId",index)
        );
        userNotarizationService.save(userNotarization);


        //注册过直接返回信息
        user.setPassword("");
        Map<String,Object> resultObj= (Map<String,Object>) JSON.toJSON(user);
        String token = JwtUtils.getToken(user.getId(),user.getId(),user.getId());
        resultObj.put("token" ,token);
        return ApiResult.success(resultObj);
    }


    @ResponseBody
    @RequestMapping(value = "/getPhoneCode", method = RequestMethod.POST)
    @ApiOperation(value = "手机号登录")
    @IgnoreAuth
    public ApiResult<?> getPhoneCode(@RequestParam("phone")String phone){
        Random random = new Random();
        String smsCode="";
        for (int i=0;i<4;i++){
            smsCode += random.nextInt(10);
        }
        Map<String,Object> sendMap =new HashMap<>();
        sendMap.put("code",smsCode);
        MessageUtils.doSendMessage("loginCode",signName,new String[]{phone},sendMap);
        redisHelper.put(phone,smsCode,600);
        return ApiResult.success("发送成功");
    }


    @ResponseBody
    @RequestMapping(value = "/getSignature", method = RequestMethod.POST)
    @ApiOperation(value = "获取jssdk密钥")
    @IgnoreAuth
    public ApiResult<?> getSignature(@RequestParam("url")String url){
        return ApiResult.success(wxUtils.sign(url));
    }


    @ResponseBody
    @RequestMapping(value = "/updateUser", method = RequestMethod.POST)
    @ApiOperation(value = "实名认证")
    public ApiResult<?> getPhoneCode(@RequestBody UserDTO userDTO){
        User user = userService.getById(JwtUtils.getTokenUserId());
        user.setRealName(userDTO.getRealName());
        user.setIdCardNo(userDTO.getIdCardNo());
        user.setIdCardType("身份证");
        Map<String, String> cardMap= IdCardUtil.getBirthdayAgeSex(userDTO.getIdCardNo());
        System.out.println(cardMap);
        user.setBirth(LocalDate.parse(cardMap.get("birthday"), java.time.format.DateTimeFormatter.ISO_DATE));
        user.setGender(cardMap.get("sex").equals("1")?1:2);
        user.setUpdatedTime(LocalDateTime.now());
        userService.saveOrUpdate(user);
        String token = JwtUtils.getToken(user.getId(),user.getPassword(),user.getRealName());
        //用户密码字段不返回！
        user.setPassword("");
        Map<String,Object> resultObj= (Map<String,Object>) JSON.toJSON(user);
        resultObj.put("token" ,token);
        return ApiResult.success(resultObj);
    }



    @ResponseBody
    @RequestMapping(value = "/userinfo", method = RequestMethod.POST)
    @ApiOperation(value = "通过userId获取用户信息")
    @IgnoreAuth
    public ApiResult<?> getUserInfo(@RequestParam("userId") String userId) {
        User user = userService.getOne(Wrappers.<User>lambdaQuery()
                .eq(User::getId, userId),false);
        if(user == null){
            return ApiResult.error("找不到这个用户信息");
        }
        String token = JwtUtils.getToken(user.getId(),user.getPassword(),user.getRealName());
        user.setPassword("");

        Map<String,Object> resultObj= (Map<String,Object>) JSON.toJSON(user);
        resultObj.put("token" ,token);
        return ApiResult.success(resultObj);
    }

}
