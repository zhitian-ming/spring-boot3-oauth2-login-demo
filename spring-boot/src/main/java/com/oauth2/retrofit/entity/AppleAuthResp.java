package com.oauth2.retrofit.entity;

import lombok.Data;

import java.util.List;

/**
 * @author huangzhao
 * @date 2025/10/23
 */
@Data
public class AppleAuthResp {

    private List<AppleAuthKey> keys;
}
