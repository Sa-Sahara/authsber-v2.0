package com.alevya.authsber.dto;


import java.util.Objects;

public class AuthDtoRequest {
    private String phoneEmail;
    private String password;

    public AuthDtoRequest() {
    }

    public AuthDtoRequest(String phoneEmail
            , String password) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthDtoRequest that = (AuthDtoRequest) o;

        if (!Objects.equals(phoneEmail, that.phoneEmail)) return false;
        return Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        int result = phoneEmail != null ? phoneEmail.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AuthDtoRequest{" +
                "phoneEmail='" + phoneEmail + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
