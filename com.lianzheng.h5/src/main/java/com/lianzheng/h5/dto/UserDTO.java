package com.lianzheng.h5.dto;

import com.drew.lang.annotations.NotNull;
import lombok.Data;
import lombok.NonNull;


@Data
public class UserDTO {
    @NotNull
    private String realName;

    @NotNull
    private String idCardNo;




}
