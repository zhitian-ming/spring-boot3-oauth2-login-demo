package com.oauth2.model.enums;

import lombok.Getter;

/**
 * @author huangzhao
 * @date 2025/9/28
 */
public class EmailTemplateEnum {

    @Getter
    public enum Code {

        LOGIN("login", "登录"),
        APPRAISAL_REPORT("appraisal_report", "鉴定报告"),
        ;

        private final String code;

        private final String desc;

        Code(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    @Getter
    public enum Status {

        ENABLE(1, "启用"),

        DISABLE(0, "禁用"),
        ;

        private final int status;

        private final String desc;

        Status(int status, String desc) {
            this.status = status;
            this.desc = desc;
        }
    }
}
