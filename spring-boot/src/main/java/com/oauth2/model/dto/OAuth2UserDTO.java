package com.oauth2.model.dto;

import lombok.Data;

/**
 * OAuth2用户信息DTO
 */
@Data
public class OAuth2UserDTO {

    /**
     * 第三方平台 (google, facebook, apple)
     */
    private String provider;

    /**
     * 第三方平台用户唯一标识
     */
    private String providerId;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 姓名
     */
    private String fullName;

    /**
     * 名
     */
    private String firstName;

    /**
     * 姓
     */
    private String lastName;
}