package com.veloproweb.model.dto.purchase;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierRequestDTO {

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    @Size(min = 3, message = "El nombre debe tener al menos 3 caracteres")
    private String name;

    @NotBlank(message = "El rut es obligatorio")
    @Pattern(regexp = "^\\d{7,8}-[\\dKk]$", message = "El rut no tiene un formato válido")
    private String rut;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ingresar un email válido")
    private String email;

    @NotBlank(message = "El Teléfono es obligatorio")
    @Pattern(regexp = "^\\+569 \\d{8}$", message = "El número debe tener el formato: +569 12345678")
    private String phone;
}
