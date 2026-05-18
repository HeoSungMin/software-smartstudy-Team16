package com.example.auth.dto;

public class LoginResponse {

    private String token;
    private String username;
    private String message;

    public LoginResponse(String token, String username) {
        this.token = token;
        this.username = username;
        this.message = "로그인에 성공했습니다.";
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getMessage() { return message; }
}
