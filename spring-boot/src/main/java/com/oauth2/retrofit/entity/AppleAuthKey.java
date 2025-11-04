package com.oauth2.retrofit.entity;

import lombok.Data;

/**
 * @author huangzhao
 * @date 2025/10/23
 */
@Data
public class AppleAuthKey {

    private String kty;

    private String kid;

    private String use;

    private String alg;

    private String n;

    private String e;
}
