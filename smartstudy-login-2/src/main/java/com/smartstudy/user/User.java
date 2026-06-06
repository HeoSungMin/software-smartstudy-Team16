package com.smartstudy.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private String password;

    @Indexed(unique = true)
    private String email;

    private String name;
    private String refreshToken;
    private int loginFailCount = 0;
    private boolean accountLocked = false;
    private boolean emailVerified = false;
    private List<String> roles = new ArrayList<>(List.of("ROLE_USER"));
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {}

    private User(Builder b) {
        this.username = b.username; this.password = b.password;
        this.email = b.email; this.name = b.name;
        this.roles = b.roles; this.createdAt = b.createdAt;
        this.updatedAt = b.updatedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String username, password, email, name;
        private List<String> roles = new ArrayList<>(List.of("ROLE_USER"));
        private LocalDateTime createdAt, updatedAt;

        public Builder username(String v) { this.username = v; return this; }
        public Builder password(String v) { this.password = v; return this; }
        public Builder email(String v)    { this.email = v;    return this; }
        public Builder name(String v)     { this.name = v;     return this; }
        public Builder roles(List<String> v) { this.roles = v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v) { this.updatedAt = v; return this; }
        public User build() { return new User(this); }
    }

    public String getId()            { return id; }
    public String getUsername()      { return username; }
    public String getPassword()      { return password; }
    public String getEmail()         { return email; }
    public String getName()          { return name; }
    public String getRefreshToken()  { return refreshToken; }
    public int    getLoginFailCount(){ return loginFailCount; }
    public boolean isAccountLocked() { return accountLocked; }
    public boolean isEmailVerified() { return emailVerified; }
    public List<String> getRoles()   { return roles; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(String v)             { this.id = v; }
    public void setUsername(String v)       { this.username = v; }
    public void setPassword(String v)       { this.password = v; }
    public void setEmail(String v)          { this.email = v; }
    public void setName(String v)           { this.name = v; }
    public void setRefreshToken(String v)   { this.refreshToken = v; }
    public void setLoginFailCount(int v)    { this.loginFailCount = v; }
    public void setAccountLocked(boolean v) { this.accountLocked = v; }
    public void setEmailVerified(boolean v) { this.emailVerified = v; }
    public void setRoles(List<String> v)    { this.roles = v; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
}
