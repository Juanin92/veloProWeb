package com.veloProWeb.model.entity.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
public class BrandProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la marca es obligatorio")
    @Size(min = 2, message = "El nombre de la marca debe tener al menos 2 caracteres")
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
