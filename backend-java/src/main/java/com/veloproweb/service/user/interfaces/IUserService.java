package com.veloproweb.service.user.interfaces;

import com.veloproweb.model.dto.user.UpdateUserDTO;
import com.veloproweb.model.dto.user.UserRequestDTO;
import com.veloproweb.model.dto.user.UserResponseDTO;
import com.veloproweb.model.entity.user.User;

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
