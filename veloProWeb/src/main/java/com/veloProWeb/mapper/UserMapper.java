package com.veloProWeb.mapper;

import com.veloProWeb.model.dto.user.UserRequestDTO;
import com.veloProWeb.model.dto.user.UserResponseDTO;
import com.veloProWeb.model.entity.User.User;
import com.veloProWeb.util.TextFormatter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserMapper {

    public User toEntity(UserRequestDTO dto){
        return User.builder()
                .name(TextFormatter.capitalize(dto.getName()))
                .surname(TextFormatter.capitalize(dto.getSurname()))
                .username(dto.getUsername())
                .password(dto.getUsername() + dto.getRut().substring(0,5))
                .rut(dto.getRut())
                .email(dto.getEmail())
                .role(dto.getRole())
                .date(LocalDate.now())
                .status(true)
                .token(null)
                .build();
    }

    public UserResponseDTO toResponseDTO(User user){
        return UserResponseDTO.builder()
                .date(user.getDate())
                .name(user.getName())
                .surname(user.getSurname())
                .username(user.getUsername())
                .rut(user.getRut())
                .email(user.getEmail())
                .status(user.isStatus())
                .role(user.getRole())
                .build();
    }
}
