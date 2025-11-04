package com.oauth2.security.email;

import com.oauth2.security.UserPrincipal;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * @author huangzhao
 * @date 2025/10/18
 */
public class EmailCodeAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;

    private Object token;

    public EmailCodeAuthenticationToken(String uid, String token) {
        super(null);
        this.principal = uid;
        this.token = token;
        setAuthenticated(false);
    }

    public EmailCodeAuthenticationToken(UserPrincipal user) {
        super(user.getAuthorities());
        this.principal = user;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
