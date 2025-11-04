package com.oauth2.exception.code;

/**
 * @author huangzhao
 * @date 2025/9/24
 */
public interface ErrorCode {

    int getCode();

    String getDesc();

    String getDescCn();
}
