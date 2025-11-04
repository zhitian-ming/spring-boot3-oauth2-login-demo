package com.oauth2.model.entity;

import com.oauth2.exception.code.BaseErrorCode;
import com.oauth2.exception.code.ErrorCode;
import lombok.Data;

/**
 * @author huangzhao
 * @date 2025/9/24
 */
@Data
public class RspEntity<T> {

    private int code;

    private String desc;

    private T message;

    public RspEntity() {
        this(BaseErrorCode.OK);
    }

    public RspEntity(T message) {
        this(BaseErrorCode.OK);
        this.message = message;
    }

    public RspEntity(ErrorCode code) {
        this(code.getCode(), code.getDesc());
    }

    public RspEntity(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
