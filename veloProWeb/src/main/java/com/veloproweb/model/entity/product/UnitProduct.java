package com.veloproweb.model.entity.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnitProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la unidad de medida es obligatoria")
    @Size(min = 2, message = "El nombre de la unidad de medida debe tener al menos 3 caracteres")
    @Pattern(regexp = "^[0-9]+ [a-zA-Z]+$",
            message = "El nombre debe tener un número seguido de un espacio y letras. Ejemplo: '10 gr'")
    private String nameUnit;

    @Override
    public String toString() {
        return nameUnit;
    }
}
