package com.smartstudy.oauth2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.ArrayList;
import java.util.List;

/**
 * OAuth2 소셜 로그인 등록을 동적으로 구성합니다.
 * 환경변수가 설정된 제공사만 활성화되며, 없으면 빈 repository로 앱이 정상 기동됩니다.
 */
@Configuration
public class OAuth2ClientRegistrationConfig {

    @Value("${MICROSOFT_CLIENT_ID:}")     private String msId;
    @Value("${MICROSOFT_CLIENT_SECRET:}") private String msSecret;

    @Value("${GOOGLE_CLIENT_ID:}")     private String googleId;
    @Value("${GOOGLE_CLIENT_SECRET:}") private String googleSecret;

    @Bean
    ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = new ArrayList<>();

        if (hasValue(msId) && hasValue(msSecret)) {
            registrations.add(ClientRegistration.withRegistrationId("microsoft")
                    .clientId(msId)
                    .clientSecret(msSecret)
                    .redirectUri("{baseUrl}/login/oauth2/code/microsoft")
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationUri("https://login.microsoftonline.com/common/oauth2/v2.0/authorize")
                    .tokenUri("https://login.microsoftonline.com/common/oauth2/v2.0/token")
                    .jwkSetUri("https://login.microsoftonline.com/common/discovery/v2.0/keys")
                    .userInfoUri("https://graph.microsoft.com/oidc/userinfo")
                    .userNameAttributeName("sub")
                    .scope("openid", "profile", "email")
                    .clientName("Microsoft")
                    .build());
        }

        if (hasValue(googleId) && hasValue(googleSecret)) {
            registrations.add(ClientRegistration.withRegistrationId("google")
                    .clientId(googleId)
                    .clientSecret(googleSecret)
                    .redirectUri("{baseUrl}/login/oauth2/code/google")
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                    .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                    .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                    .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                    .userNameAttributeName("sub")
                    .scope("openid", "profile", "email")
                    .clientName("Google")
                    .build());
        }

        if (registrations.isEmpty()) {
            // 소셜 로그인 미설정 — 이메일 로그인만 동작
            return registrationId -> null;
        }
        return new InMemoryClientRegistrationRepository(registrations);
    }

    @Bean
    OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository repo) {
        return new InMemoryOAuth2AuthorizedClientService(repo);
    }

    @Bean
    OAuth2AuthorizedClientRepository authorizedClientRepository(OAuth2AuthorizedClientService service) {
        return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(service);
    }

    private boolean hasValue(String s) {
        return s != null && !s.isBlank();
    }
}
