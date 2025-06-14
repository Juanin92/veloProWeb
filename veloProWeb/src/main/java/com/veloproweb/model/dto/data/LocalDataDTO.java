package com.veloproweb.model.dto.data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocalDataDTO {

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    private String name;

    @NotBlank(message = "El Teléfono es obligatorio")
    @Pattern(regexp = "^\\+569 \\d{8}$", message = "El número debe tener el formato: +569 12345678")
    private String phone;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ingresar un email válido")
    private String email;

    private String emailSecurityApp;

    @NotBlank(message = "La dirección de la empresa es obligatoria")
    private String address;
}
