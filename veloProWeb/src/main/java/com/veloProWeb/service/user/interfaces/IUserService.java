package com.veloProWeb.service.user.interfaces;

import com.veloProWeb.model.dto.user.UpdateUserDTO;
import com.veloProWeb.model.dto.user.UserRequestDTO;
import com.veloProWeb.model.dto.user.UserResponseDTO;
import com.veloProWeb.model.entity.User.User;

import java.util.List;

public interface IUserService {
    void createUser(UserRequestDTO dto);
    void updateUser(UserRequestDTO dto);
    void deleteUser(UserRequestDTO dto);
    void activateUser(UserRequestDTO dto);
    List<UserResponseDTO> getAllUsers();
    User getUserByUsername(String username);
    void updateOwnData(UpdateUserDTO dto, String username);
    UserResponseDTO getUserProfile(String username);
}
