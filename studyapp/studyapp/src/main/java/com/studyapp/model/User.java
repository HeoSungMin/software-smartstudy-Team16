package com.studyapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true, sparse = true)
    private String username;        // 일반 로그인용 아이디 (소셜은 null 가능)

    @Indexed(unique = true, sparse = true)
    private String email;

    private String password;        // BCrypt 해시 (소셜 로그인은 null)
    private String name;
    private String role = "ROLE_USER";

    // ── 소셜 로그인 필드 ─────────────────────
    private String provider;        // "local", "google", "kakao", "naver"
    private String providerId;      // 소셜 공급자의 고유 사용자 ID
    private String profileImage;    // 소셜 프로필 이미지 URL

    // ── 비밀번호 찾기 필드 ───────────────────
    private String passwordResetToken;
    private LocalDateTime passwordResetTokenExpiry;

    private LocalDateTime createdAt = LocalDateTime.now();
}
