-- ============================================================
--  User 테이블 생성 SQL  (MySQL / MariaDB 기준)
--  H2 개발 환경에서는 application.properties 의 ddl-auto=create-drop 으로 자동 생성됨
-- ============================================================

CREATE TABLE users (
    id          BIGINT          NOT NULL AUTO_INCREMENT   COMMENT '사용자 고유 ID',
    email       VARCHAR(100)    NOT NULL                  COMMENT '이메일 (고유)',
    name        VARCHAR(50)     NOT NULL                  COMMENT '사용자 이름',
    username    VARCHAR(50)     NOT NULL                  COMMENT '아이디 (고유)',
    password    VARCHAR(255)    NOT NULL                  COMMENT 'BCrypt 암호화된 비밀번호',
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '가입일시',

    PRIMARY KEY (id),
    UNIQUE KEY uq_users_email    (email),
    UNIQUE KEY uq_users_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='회원 테이블';
