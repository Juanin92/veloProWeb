package com.veloProWeb.model.dto.user;

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

    @NotBlank(message = "La contraseña actual es obligatoria")
    @Size(min = 7, message = "La contraseña actual debe tener al menos 7 caracteres")
    private String currentPassword;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 7, message = "La nueva contraseña debe tener al menos 7 caracteres")
    private String newPassword;

    @NotBlank(message = "La contraseña de confirmación es obligatoria")
    @Size(min = 7, message = "La contraseña de confirmación debe tener al menos 7 caracteres")
    private String confirmPassword;
}
