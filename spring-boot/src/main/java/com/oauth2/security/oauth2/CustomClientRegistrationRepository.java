package com.oauth2.security.oauth2;

import com.oauth2.security.TokenProvider;
import com.oauth2.model.enums.AuthProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Supplier;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * @author huangzhao
 * @date 2025/10/23
 */
@Component
@Slf4j
@EnableConfigurationProperties(OAuth2ClientProperties.class)
public class CustomClientRegistrationRepository implements ClientRegistrationRepository, Iterable<ClientRegistration> {

    private final Map<String, ClientRegistration> clientRegistrationMap;

    private final TokenProvider tokenProvider;

    private final ClientRegistration appleClientRegistration;

    private final static ClientRegistrationBucket BUCKET = new ClientRegistrationBucket();

    private final static long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    public CustomClientRegistrationRepository(OAuth2ClientProperties properties,
                                              TokenProvider tokenProvider) {
        this.clientRegistrationMap = new OAuth2ClientPropertiesMapper(properties).asClientRegistrations();
        this.appleClientRegistration = this.clientRegistrationMap.remove(AuthProvider.apple.toString());
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Iterator<ClientRegistration> iterator() {
        ArrayList<ClientRegistration> list = new ArrayList<>(clientRegistrationMap.values());
        list.add(BUCKET.computeIfAbsent(this::getAppleClientRegistration));
        return list.iterator();
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        if (StringUtils.equalsIgnoreCase(registrationId, AuthProvider.apple.toString())) {
            return BUCKET.computeIfAbsent(this::getAppleClientRegistration);
        } else {
            return clientRegistrationMap.get(registrationId);
        }
    }

    private ClientRegistration getAppleClientRegistration() {
        String clientSecret = tokenProvider.generateAppleClientSecret(EXPIRATION_TIME);
        log.info("生成apple client secret: {}", clientSecret);
        return ClientRegistration
                .withClientRegistration(appleClientRegistration)
                .clientSecret(clientSecret)
                .build();
    }

    private static class ClientRegistrationBucket {

        private ClientRegistration clientRegistration;

        private long expirationTime;

        public ClientRegistration computeIfAbsent(Supplier<ClientRegistration> supplier) {
            long currentTimeMillis = System.currentTimeMillis();
            if (clientRegistration == null || expirationTime < currentTimeMillis) {
                clientRegistration = supplier.get();
                expirationTime = currentTimeMillis + EXPIRATION_TIME - 5000;
            }
            return clientRegistration;
        }
    }
}
