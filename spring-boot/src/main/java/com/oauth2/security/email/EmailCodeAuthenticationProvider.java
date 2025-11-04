package com.oauth2.security.email;

import com.oauth2.exception.CustomException;
import com.oauth2.exception.code.UserErrorCode;
import com.oauth2.model.entity.User;
import com.oauth2.service.UserService;
import com.oauth2.model.cost.RedisKey;
import com.oauth2.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * @author huangzhao
 * @date 2025/10/18
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailCodeAuthenticationProvider implements AuthenticationProvider {

    private final RedissonClient redissonClient;

    private final UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String uid = (String) authentication.getPrincipal();
        String token = (String) authentication.getCredentials();

        var verifyKey = String.format(RedisKey.EMAIL_VERIFY_TOKEN_KEY, uid);
        RMap<String, String> map = redissonClient.getMap(verifyKey);
        String email = map.remove(token);
        if (StringUtils.isBlank(email)) {
            throw new CustomException(UserErrorCode.VERIFY_TOKEN_EXPIRED);
        }

        User user = userService.getUserOrRegisterUser(email);
        return new EmailCodeAuthenticationToken(UserPrincipal.create(user));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return EmailCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
