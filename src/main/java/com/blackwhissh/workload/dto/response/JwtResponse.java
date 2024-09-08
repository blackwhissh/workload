package com.blackwhissh.workload.dto.response;

public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private Integer id;
    private String email;
    private String workId;
    private String role;

    public JwtResponse(String accessToken, String refreshToken, Integer id,String email, String workId, String role) {
        this.accessToken = accessToken;
        this.id = id;
        this.email = email;
        this.workId = workId;
        this.role = role;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getType() {
        return "Bearer";
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
