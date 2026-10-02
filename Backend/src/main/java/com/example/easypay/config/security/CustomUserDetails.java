package com.example.easypay.config.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Data
public class CustomUserDetails implements UserDetails {

    private Integer userId;
    private String username;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(
            Integer userId,
            String username,
            Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.authorities = authorities;
    }

    public Integer getUserId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return "OTP_AUTH";
    }

    @Override
    public String getUsername() {
        return username;
    }

}
