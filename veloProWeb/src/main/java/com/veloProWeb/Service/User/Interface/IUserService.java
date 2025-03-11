package com.veloProWeb.Service.User.Interface;

import com.veloProWeb.Model.DTO.UpdateUserDTO;
import com.veloProWeb.Model.DTO.UserDTO;
import com.veloProWeb.Model.Entity.User.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface IUserService {
    void addUser(User user);
    void updateUser(User user);
    void deleteUser(User user);
    void activateUser(User user);
    List<User> getAllUser();
    User getUserWithUsername(String username);
    User getAuthUser(String username, String pass);
    User getAuthUserToken(String username, String token);
    void sendEmailCode(User user);
    void sendEmailUpdatePassword(User user);
    void updateUserData(UpdateUserDTO dto, String username);
    UserDTO getData(String username);
}
