package com.alevya.authsber.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthDtoResponse {
    private String token;
    private String error;

    public AuthDtoResponse() {
    }

    public AuthDtoResponse(String token,
             String error) {
        this.token = token;
        this.error = error;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthDtoResponse that = (AuthDtoResponse) o;

        if (!Objects.equals(token, that.token)) return false;
        return Objects.equals(error, that.error);
    }

    @Override
    public int hashCode() {
        int result = token != null ? token.hashCode() : 0;
        result = 31 * result + (error != null ? error.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AuthDtoResponse{" +
                "token='" + token + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
