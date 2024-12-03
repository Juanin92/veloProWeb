package com.veloProWeb.Model.Entity.Product;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class CategoryProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.EAGER)
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.EAGER)
    private List<SubcategoryProduct> subcategories = new ArrayList<>();

    @Override
    public String toString() {
        return name;
    }
}
