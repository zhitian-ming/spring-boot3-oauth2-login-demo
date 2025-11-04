package com.oauth2.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * @author huangzhao
 * @date 2025/9/22
 */
@Data
@TableName("t_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String username;

    private String password;

    private String nickname;

    private String avatar;

    private String firstName;

    private String lastName;

    private String country;

    private String city;

    private String language;

    private String registrationChannel;

    private String lastLoginIp;

    private LocalDateTime lastLoginTime;

    private Integer status;

    private LocalDateTime createdTime;

    private LocalDateTime modifiedTime;
}
