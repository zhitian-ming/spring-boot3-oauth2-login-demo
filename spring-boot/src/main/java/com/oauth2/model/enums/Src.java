package com.oauth2.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * @author huangzhao
 * @date 2025/10/11
 */
@Getter
public enum Src {

    WEB(1, "", "web"),
    ANDROID(2, "", "android"),
    IOS(3, "", "ios"),

    ;

    private final int src;

    private final String md5Key;

    private final String program;

    Src(int src, String md5Key, String program) {
        this.src = src;
        this.md5Key = md5Key;
        this.program = program;
    }

    public static Src getSrc(String src) {
        if (!NumberUtils.isDigits(src)) {
            return null;
        }
        return getSrc(Integer.parseInt(src));
    }

    public static Src getSrc(int src) {
        for (Src value : values()) {
            if (value.src == src) {
                return value;
            }
        }
        return null;
    }
}
