package com.veloproweb.model.dto.user;

import com.veloproweb.model.enums.Rol;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "El nombre del usuario es obligatorio")
    @Size(min = 3, message = "El nombre debe tener al menos 3 caracteres")
    @Pattern(regexp = "^(?!null|NULL$)[\\p{L} ]+$", message = "El nombre solo puede contener letras")
    private String name;

    @NotBlank(message = "El apellido del usuario es obligatorio")
    @Size(min = 3, message = "El apellido debe tener al menos 3 caracteres")
    @Pattern(regexp = "^(?!null|NULL$)[\\p{L} ]+$", message = "El apellido solo puede contener letras")
    private String surname;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, message = "El nombre de usuario debe tener al menos 3 caracteres")
    private String username;

    @NotBlank(message = "El rut del usuario es obligatorio")
    @Pattern(regexp = "^\\d{7,8}-[\\dKk]$", message = "El rut no tiene un formato válido")
    private String rut;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ingresar un email válido")
    private String email;

    @NotNull(message = "Debe seleccionar un rol para el usuario")
    private Rol role;
}
