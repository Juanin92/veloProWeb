package com.veloProWeb.Model.DTO;

import com.veloProWeb.Model.Enum.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
