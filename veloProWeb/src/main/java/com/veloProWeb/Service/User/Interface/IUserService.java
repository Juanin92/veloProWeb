package com.veloProWeb.Service.User.Interface;

import com.veloProWeb.Model.DTO.UpdateUserDTO;
import com.veloProWeb.Model.DTO.UserDTO;
import com.veloProWeb.Model.Entity.User.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface IUserService {
    void addUser(UserDTO dto);
    void updateUser(User user);
    void deleteUser(String username);
    void activateUser(String username);
    List<UserDTO> getAllUser();
    User getUserWithUsername(String username);
    User getAuthUser(String username, String pass);
    User getAuthUserToken(String username, String token);
    void sendEmailCode(User user);
    void sendEmailUpdatePassword(User user);
    void updateUserData(UpdateUserDTO dto, String username);
    UserDTO getData(String username);
    boolean hasRequiredRole(UserDetails userDetails, String... roles);
}
