package com.veloProWeb.model.dto;

import com.veloProWeb.model.Enum.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String name;
    private String surname;
    private String username;
    private String rut;
    private String email;
    private Rol role;
    private boolean status;
    private LocalDate date;
}
