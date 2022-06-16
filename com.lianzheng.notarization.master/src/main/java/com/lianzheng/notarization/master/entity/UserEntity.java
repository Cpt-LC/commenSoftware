package com.lianzheng.notarization.master.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lianzheng.core.auth.mgmt.utils.DateUtils;
import lombok.Data;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.Map;

@Data
@TableName("user")
public class UserEntity {
    @TableId
    private  String id;
    //用户名
    private  String realName;
    //密码
    private  String password;
    //头像
    private  String avatarUrl;
    //手机号
    private  String phone;
    //上次登陆的时间
    private Date lastLoginTime;
    //邮箱
    private String  email;
    //性别
    private Integer gender;
    //生日
    private Date  birth ;
    //省份证类型
     private String idCardType;
    //证件号码
     private String idCardNo;
    //证件地址
    private String idCardAddress;
     //国籍
     private String nationality;
     //项目索引号
     private Integer  appidIndex;
    //登录失败次数
     private Integer loginFailedCount;
     //创建时间
     private Date createdTime;
    //更新时间
      private Date  updatedTime;

    public void update(Map<String, Object> param){
        if(param.containsKey("realNameAgent")&&param.get("realNameAgent")!=null){ this.realName = (String)param.get("realNameAgent"); }
        if(param.containsKey("birthAgent")&&param.get("birthAgent")!=null){ this.birth = DateUtils.stringToDate(param.get("birthAgent").toString(),"yyyy-MM-dd");}
        if(param.containsKey("phoneAgent")&&param.get("phoneAgent")!=null){ this.phone = (String)param.get("phoneAgent"); }
        if(param.containsKey("genderAgent")&&param.get("genderAgent")!=null){ this.gender = NumberUtils.createInteger(param.get("genderAgent").toString()); }
        if(param.containsKey("idCardTypeAgent")&&param.get("idCardTypeAgent")!=null){ this.idCardType = (String)param.get("idCardTypeAgent"); }
        if(param.containsKey("idCardNoAgent")&&param.get("idCardNoAgent")!=null){ this.idCardNo = (String)param.get("idCardNoAgent"); }
        if(param.containsKey("idCardAddressAgent")&&param.get("idCardAddressAgent")!=null){ this.idCardAddress = (String)param.get("idCardAddressAgent"); }
    }

    public void updateNoAgent(Map<String, Object> param){
        if(param.containsKey("realName")&&param.get("realName")!=null){ this.realName = (String)param.get("realName"); }
        if(param.containsKey("birth")&&param.get("birth")!=null){ this.birth = DateUtils.stringToDate(param.get("birth").toString(),"yyyy-MM-dd");}
        if(param.containsKey("phone")&&param.get("phone")!=null){ this.phone = (String)param.get("phone"); }
        if(param.containsKey("gender")&&param.get("gender")!=null){ this.gender = NumberUtils.createInteger(param.get("gender").toString()); }
        if(param.containsKey("idCardType")&&param.get("idCardType")!=null){ this.idCardType = (String)param.get("idCardType"); }
        if(param.containsKey("idCardNo")&&param.get("idCardNo")!=null){ this.idCardNo = (String)param.get("idCardNo"); }
        if(param.containsKey("idCardAddress")&&param.get("idCardAddress")!=null){ this.idCardAddress = (String)param.get("idCardAddress"); }
    }
}
