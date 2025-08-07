package com.veloproweb.model.dto.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerRequestDTO {

    @NotNull(message = "El id es obligatorio")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, message = "El nombre debe tener al menos 3 caracteres")
    @Pattern(regexp = "^(?!^(null|NULL)$)[\\p{L} ]+$", message = "El nombre solo puede contener letras")
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 3, message = "El apellido debe tener al menos 3 caracteres")
    @Pattern(regexp = "^(?!^(null|NULL)$)[\\p{L} ]+$", message = "El apellido solo puede contener letras")
    private String surname;

    @NotBlank(message = "El Teléfono es obligatorio")
    @Pattern(regexp = "^\\+569 \\d{8}$", message = "El número debe tener el formato: +569 12345678")
    private String phone;

    private String email;
}
