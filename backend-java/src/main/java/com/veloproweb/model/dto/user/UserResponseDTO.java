package com.veloproweb.model.dto.user;

import com.veloproweb.model.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    private LocalDate date;
    private String name;
    private String surname;
    private String username;
    private String rut;
    private String email;
    private boolean status;
    private Rol role;
}
