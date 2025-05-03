package com.veloProWeb.model.entity.Product;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class SubcategoryProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "id_category", nullable = false)
    private CategoryProduct category;

    @Override
    public String toString() {
        return name;
    }
}
