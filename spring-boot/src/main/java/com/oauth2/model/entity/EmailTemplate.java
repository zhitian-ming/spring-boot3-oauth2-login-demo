package com.oauth2.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author huangzhao
 * @date 2025/9/28
 */
@Data
@TableName("t_email_template")
public class EmailTemplate {

    @TableId
    private Integer id;

    private String code;

    private String email;

    private String userName;

    private String password;

    private String hostName;

    private String subject;

    private String templateText;

    private Integer status;

    private LocalDateTime createdTime;

    private LocalDateTime modifiedTime;
}
