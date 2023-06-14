package com.alevya.authsber.dto;

public class AuthDtoRequest {
    private String phoneEmail;
    private String password;

    public AuthDtoRequest() {
    }

    public AuthDtoRequest(String phoneEmail,
                          String password) {
        this.phoneEmail = phoneEmail;
        this.password = password;
    }

    public String getPhoneEmail() {
        return phoneEmail;
    }

    public void setPhoneEmail(String phoneEmail) {
        this.phoneEmail = phoneEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
