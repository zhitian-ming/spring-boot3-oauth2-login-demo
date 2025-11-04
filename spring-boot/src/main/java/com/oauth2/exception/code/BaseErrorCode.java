package com.oauth2.exception.code;

import lombok.Getter;

/**
 * @author huangzhao
 * @date 2025/9/24
 */
@Getter
public enum BaseErrorCode implements ErrorCode {

    OK(0, "成功", "ok"),
    SYSTEM_ERROR(1, "系统错误", "system error"),
    INVALID_PARAMETERS(2, "参数无效", "invalid parameters"),
    ;

    private final int code;

    private final String desc;

    private final String descCn;

    BaseErrorCode(int code, String descCn, String desc) {
        this.code = code;
        this.descCn = descCn;
        this.desc = desc;
    }
}
