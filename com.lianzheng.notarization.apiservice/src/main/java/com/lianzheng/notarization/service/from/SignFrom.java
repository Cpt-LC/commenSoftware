package com.lianzheng.notarization.service.from;

import lombok.Data;

@Data
public class SignFrom {
    private String appId;
    private String nonce;
    private String token;
}
