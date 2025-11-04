package com.oauth2.model.vo;

import lombok.Data;

/**
 * 用户实体类
 *
 * @author huangzhao
 * @date 2025/9/22
 */
@Data
public class UserVO {

    private Integer id;

    private String username;

    private String nickname;

    private String avatar;

    private String firstName;

    private String lastName;

    private String country;

    private String city;

    private String token;
}
