package com.veloProWeb.Model.Entity.Product;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class SubcategoryProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "subcategoryProduct", cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.EAGER)
    private List<Product> products = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "id_category", nullable = false)
    private CategoryProduct category;

    @Override
    public String toString() {
        return name;
    }
}
