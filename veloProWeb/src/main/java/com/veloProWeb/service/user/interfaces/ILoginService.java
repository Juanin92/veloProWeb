package com.veloProWeb.service.user.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

public interface ILoginService {
    boolean isPasswordValid(String password, UserDetails userDetails);
    void validateSecurityToken(String username, String token);
    void sendSecurityTokenByEmail(String username);
    void validateLoginAccess(String username);
}
