package com.oauth2.util;

import com.oauth2.security.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author huangzhao
 * @date 2025/9/24
 */
public class UserUtils {

    public static UserPrincipal getUser() {
        return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static Integer getUserId() {
        return getUser().getId();
    }
}
