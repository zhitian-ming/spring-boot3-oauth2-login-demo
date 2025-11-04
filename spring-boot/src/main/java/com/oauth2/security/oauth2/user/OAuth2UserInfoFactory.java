package com.oauth2.security.oauth2.user;

import com.oauth2.exception.CustomException;
import com.oauth2.exception.code.UserErrorCode;
import com.oauth2.model.enums.AuthProvider;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if(registrationId.equalsIgnoreCase(AuthProvider.google.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.facebook.toString())) {
            return new FacebookOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.github.toString())) {
            return new GithubOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.apple.toString())) {
            return new AppleOAuth2UserInfo(attributes);
        } else {
            throw new CustomException(UserErrorCode.PROVIDER_NOT_SUPPORTED);
        }
    }
}
