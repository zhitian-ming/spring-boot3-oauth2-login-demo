package com.oauth2.retrofit.client;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import com.oauth2.retrofit.entity.AppleAuthResp;
import retrofit2.http.GET;

/**
 * @author huangzhao
 * @date 2025/10/23
 */
@RetrofitClient(baseUrl = "https://appleid.apple.com")
public interface AppleClient {

    @GET("/auth/keys")
    AppleAuthResp authKeys();
}
