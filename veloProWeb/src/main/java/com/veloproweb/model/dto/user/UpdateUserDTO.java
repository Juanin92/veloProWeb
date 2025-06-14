package com.veloproweb.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, message = "El nombre de usuario debe tener al menos 3 caracteres")
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ingresar un email válido")
    private String email;

    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
