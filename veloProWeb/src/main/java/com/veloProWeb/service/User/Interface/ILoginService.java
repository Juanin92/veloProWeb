package com.veloProWeb.service.User.Interface;

import org.springframework.security.core.userdetails.UserDetails;

public interface ILoginService {
    boolean isPasswordValid(String password, UserDetails userDetails);
    void validateSecurityToken(String username, String token);
    void sendSecurityTokenByEmail(String username);
}
