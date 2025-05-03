package com.veloProWeb.service.User.Interface;

import com.veloProWeb.model.dto.UpdateUserDTO;
import com.veloProWeb.model.dto.UserDTO;
import com.veloProWeb.model.entity.User.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface IUserService {
    void addUser(UserDTO dto);
    void updateUser(UserDTO dto);
    void deleteUser(String username);
    void activateUser(String username);
    List<UserDTO> getAllUser();
    User getUserWithUsername(String username);
    boolean getAuthUser(String password, UserDetails userDetails);
    void getAuthUserToken(String username, String token);
    void sendEmailCode(String username);
    void sendEmailUpdatePassword(User user);
    void updateUserData(UpdateUserDTO dto, String username);
    UserDTO getData(String username);
    boolean hasRequiredRole(UserDetails userDetails, String... roles);
}
