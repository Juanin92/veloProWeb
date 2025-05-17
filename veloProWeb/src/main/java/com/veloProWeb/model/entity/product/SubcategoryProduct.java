package com.veloProWeb.model.entity.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class SubcategoryProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la subcategoría es obligatoria")
    @Size(min = 3, message = "El nombre de la subcategoría debe tener al menos 2 caracteres")
    @Pattern(regexp = "^(?!null|NULL$)[\\p{L} ]+$", message = "El nombre solo puede contener letras")
    private String name;

    @NotNull(message = "Debe seleccionar una categoría")
    @ManyToOne
    @JoinColumn(name = "id_category", nullable = false)
    private CategoryProduct category;

    @Override
    public String toString() {
        return name;
    }
}
