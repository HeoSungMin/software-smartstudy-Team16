package com.studyapp.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.studyapp.repository.UserRepository;
import com.studyapp.util.OAuth2UserInfo;
import com.studyapp.util.OAuth2UserInfoFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String provider = token.getAuthorizedClientRegistrationId();
        OAuth2User oAuth2User = token.getPrincipal();

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());

        // 세션에 사용자 정보 저장 (대시보드에서 활용)
        userRepository.findByProviderAndProviderId(provider, userInfo.getId())
                .ifPresent(user -> request.getSession().setAttribute("socialUserId", user.getId()));

        getRedirectStrategy().sendRedirect(request, response, "/dashboard");
    }
}
