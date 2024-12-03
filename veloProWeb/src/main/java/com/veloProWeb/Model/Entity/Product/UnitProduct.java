package com.veloProWeb.Model.Entity.Product;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class UnitProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nameUnit;

    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.EAGER)
    private List<Product> products = new ArrayList<>();

    @Override
    public String toString() {
        return nameUnit;
    }
}
