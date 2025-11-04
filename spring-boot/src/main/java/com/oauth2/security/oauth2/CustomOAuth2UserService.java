package com.oauth2.security.oauth2;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oauth2.mapper.UserMapper;
import com.oauth2.mapper.UserProviderMapper;
import com.oauth2.model.entity.User;
import com.oauth2.model.entity.UserProvider;
import com.oauth2.retrofit.client.AppleClient;
import com.oauth2.security.oauth2.user.OAuth2UserInfo;
import com.oauth2.security.oauth2.user.OAuth2UserInfoFactory;
import com.oauth2.exception.CustomException;
import com.oauth2.exception.code.UserErrorCode;
import com.oauth2.model.cost.RedisKey;
import com.oauth2.model.enums.AuthProvider;
import com.oauth2.retrofit.entity.AppleAuthKey;
import com.oauth2.retrofit.entity.AppleAuthResp;
import com.oauth2.security.TokenProvider;
import com.oauth2.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserMapper userMapper;

    private final UserProviderMapper userProviderMapper;

    private final TokenProvider tokenProvider;

    private final AppleClient appleClient;

    private final RedissonClient redissonClient;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {

        try {
            OAuth2User oAuth2User;
            ClientRegistration clientRegistration = oAuth2UserRequest.getClientRegistration();
            String registrationId = clientRegistration.getRegistrationId();
            if (AuthProvider.apple.toString().equalsIgnoreCase(registrationId)) {

                Map<String, Object> additionalParameters = oAuth2UserRequest.getAdditionalParameters();
                String idToken = MapUtils.getString(additionalParameters, "id_token");
                String header = new String(Base64.getDecoder().decode(idToken.split("\\.")[0]));
                Map map = new ObjectMapper().readValue(header, Map.class);
                String kid = String.valueOf(map.get("kid"));
                AppleAuthKey appleAuthKey = getAppleKeys(kid);
                Claims claims = tokenProvider.parserAppleToken(idToken, appleAuthKey);
                oAuth2User = new DefaultOAuth2User(Collections.emptyList(), claims,
                        clientRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName());

            } else {
                oAuth2User = super.loadUser(oAuth2UserRequest);
            }
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private AppleAuthKey getAppleKeys(String kid) {

        RBucket<AppleAuthResp> bucket = redissonClient.getBucket(RedisKey.APPLE_KEYS);
        AppleAuthResp appleAuthResp;
        if (bucket.isExists()) {
            appleAuthResp = bucket.get();
        } else {
            appleAuthResp = appleClient.authKeys();
            bucket.set(appleAuthResp, Duration.ofHours(12));
        }

        int cnt = 0;
        do {
            for (AppleAuthKey key : appleAuthResp.getKeys()) {
                if (kid.equals(key.getKid())) {
                    return key;
                }
            }
            appleAuthResp = appleClient.authKeys();
            bucket.set(appleAuthResp, Duration.ofHours(12));
        } while (++cnt <= 1);

        return null;
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new CustomException(UserErrorCode.EMAIL_NOT_FOUND);
        }

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, oAuth2UserInfo.getEmail()));

        String provider = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        String providerId = oAuth2UserInfo.getId();
        if (user == null) {
            UserProvider userProvider = getUserProvider(provider, providerId);
            if (userProvider != null) {
                user = userMapper.selectById(userProvider.getUserId());
            }
        }

        if(user != null) {
            updateExistingUser(user, oAuth2UserInfo);
            UserProvider userProvider = getUserProvider(provider, providerId);
            if (userProvider == null) {
                addUserProvider(user.getId(), provider, providerId);
            }
        } else {
            user = registerNewUser(provider, oAuth2UserInfo);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private UserProvider getUserProvider(String provider, String providerId) {
        return userProviderMapper.selectOne(new LambdaQueryWrapper<UserProvider>()
                .eq(UserProvider::getProvider, provider)
                .eq(UserProvider::getProviderId, providerId));
    }

    private User registerNewUser(String provider, OAuth2UserInfo oAuth2UserInfo) {

        User user = new User();
        user.setNickname(oAuth2UserInfo.getName());
        user.setUsername(oAuth2UserInfo.getEmail());
        user.setAvatar(oAuth2UserInfo.getImageUrl());
        userMapper.insert(user);

        addUserProvider(user.getId(), provider, oAuth2UserInfo.getId());

        return user;
    }

    private void addUserProvider(Integer userId, String provider, String providerId) {
        UserProvider userProvider = new UserProvider();
        userProvider.setUserId(userId);
        userProvider.setProvider(provider);
        userProvider.setProviderId(providerId);
        userProviderMapper.insert(userProvider);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setNickname(oAuth2UserInfo.getName());
        existingUser.setAvatar(oAuth2UserInfo.getImageUrl());
        userMapper.insertOrUpdate(existingUser);
        return existingUser;
    }

}
