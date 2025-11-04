package com.oauth2.model.enums;

import lombok.Getter;

/**
 * @author huangzhao
 * @date 2025/9/28
 */
public class UserEnum {

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
