package com.lianzheng.h5.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
@TableName("user_notarization")
public class UserNotarization {
    @TableId
    private String id;
    private String userId;
    private String openId;
    private BigInteger notarialOfficeId;
    private Date createdTime;

}
