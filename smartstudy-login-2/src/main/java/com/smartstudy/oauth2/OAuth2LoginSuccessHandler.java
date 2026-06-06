package com.smartstudy.oauth2;

import com.smartstudy.security.JwtProvider;
import com.smartstudy.user.User;
import com.smartstudy.user.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public OAuth2LoginSuccessHandler(UserRepository userRepository, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String registrationId = token.getAuthorizedClientRegistrationId(); // naver / apple / microsoft
        OAuth2User oAuth2User  = token.getPrincipal();

        String email = extractEmail(oAuth2User, registrationId);
        String name  = extractName(oAuth2User, registrationId);

        if (email == null) {
            response.sendRedirect("/auth/login?error=oauth_no_email");
            return;
        }

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            // 이메일 도메인 제거 + provider suffix로 unique username 생성
            String base   = email.split("@")[0].replaceAll("[^a-zA-Z0-9_]", "_");
            String uname  = base + "_" + registrationId;
            return userRepository.save(
                User.builder()
                    .email(email)
                    .username(uname)
                    .name(name != null ? name.trim() : base)
                    .password(null)         // 소셜 계정은 비밀번호 없음
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build()
            );
        });

        String jwt = jwtProvider.generateAccessToken(user.getUsername(), user.getRoles());

        // HttpOnly 쿠키로 JWT 전달 (XSS 방어)
        Cookie cookie = new Cookie("access_token", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtProvider.getAccessExpiration() / 1000));
        response.addCookie(cookie);

        response.sendRedirect("/dashboard");
    }

    @SuppressWarnings("unchecked")
    private String extractEmail(OAuth2User oAuth2User, String registrationId) {
        if ("naver".equals(registrationId)) {
            // 네이버: {"resultcode":"00","message":"success","response":{...}}
            Map<String, Object> resp = (Map<String, Object>) oAuth2User.getAttribute("response");
            return resp != null ? (String) resp.get("email") : null;
        }
        // Apple / Microsoft / 기타 OIDC 모두 "email" 클레임 사용
        return oAuth2User.getAttribute("email");
    }

    @SuppressWarnings("unchecked")
    private String extractName(OAuth2User oAuth2User, String registrationId) {
        if ("naver".equals(registrationId)) {
            Map<String, Object> resp = (Map<String, Object>) oAuth2User.getAttribute("response");
            return resp != null ? (String) resp.get("name") : null;
        }
        if ("apple".equals(registrationId)) {
            // Apple은 최초 인증 시에만 name 제공
            Map<String, Object> nameMap = (Map<String, Object>) oAuth2User.getAttribute("name");
            if (nameMap != null) {
                String first = (String) nameMap.get("firstName");
                String last  = (String) nameMap.get("lastName");
                return (first != null ? first : "") + " " + (last != null ? last : "");
            }
            return null;
        }
        // Microsoft: "name" or "displayName"
        String n = oAuth2User.getAttribute("name");
        return n != null ? n : oAuth2User.getAttribute("displayName");
    }
}
