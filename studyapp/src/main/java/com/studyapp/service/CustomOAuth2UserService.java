package com.studyapp.service;

import com.studyapp.model.User;
import com.studyapp.repository.UserRepository;
import com.studyapp.util.OAuth2UserInfo;
import com.studyapp.util.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // google/kakao/naver
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, attributes);

        // DB에서 기존 소셜 계정 조회 또는 신규 생성
        User user = userRepository.findByProviderAndProviderId(provider, userInfo.getId())
                .orElseGet(() -> createSocialUser(provider, userInfo));

        // 프로필 이미지 최신화
        if (userInfo.getImageUrl() != null && !userInfo.getImageUrl().equals(user.getProfileImage())) {
            user.setProfileImage(userInfo.getImageUrl());
            userRepository.save(user);
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole())),
                attributes,
                userNameAttributeName
        );
    }

    private User createSocialUser(String provider, OAuth2UserInfo userInfo) {
        User user = new User();
        user.setProvider(provider);
        user.setProviderId(userInfo.getId());
        user.setName(userInfo.getName() != null ? userInfo.getName() : "사용자");
        user.setEmail(userInfo.getEmail());
        user.setProfileImage(userInfo.getImageUrl());
        user.setRole("ROLE_USER");
        // 소셜 로그인은 username을 provider_id 조합으로 생성
        user.setUsername(provider + "_" + userInfo.getId());
        return userRepository.save(user);
    }
}
