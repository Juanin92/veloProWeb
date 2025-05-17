package com.veloProWeb.model.entity.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la categoría es obligatoria")
    @Size(min = 3, message = "El nombre de la categoría debe tener al menos 2 caracteres")
    @Pattern(regexp = "^(?!null|NULL$)[\\p{L} ]+$", message = "El nombre solo puede contener letras")
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.EAGER)
    private List<SubcategoryProduct> subcategories = new ArrayList<>();

    @Override
    public String toString() {
        return name;
    }
}
