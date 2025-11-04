package com.oauth2.exception.code;

import lombok.Getter;

/**
 * @author huangzhao
 * @date 2025/9/24
 */
@Getter
public enum UserErrorCode implements ErrorCode {

    UNAUTHORIZED(10000, "未登录", "unauthorized"),
    USER_NOT_EXISTS(10001, "用户不存在", "user not exists"),
    EMAIL_SEND_LIMIT(10002, "同一邮箱2分钟以内只能发送一次", "Please wait 2 minutes before resending."),
    VERIFY_TOKEN_EXPIRED(10003, "令牌已过期", "token expired"),
    VERIFY_TOKEN_ERROR(10004, "令牌错误", "token error"),
    PROVIDER_NOT_EXISTS(10005, "provider不存在", "provider not exists"),
    USER_DISABLE(10006, "用户已禁用", "User Disabled"),

    UNAUTHORIZED_REDIRECT_URI(11001, "Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication", ""),
    PROVIDER_NOT_SUPPORTED(11002, "Sorry! Login with provider is not supported yet.", ""),
    EMAIL_NOT_FOUND(11003, "Email not found from OAuth2 provider", ""),
    ;

    private final int code;

    private final String desc;

    private final String descCn;

    UserErrorCode(int code, String descCn, String desc) {
        this.code = code;
        this.descCn = descCn;
        this.desc = desc;
    }
}
