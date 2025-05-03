package com.veloProWeb.model.entity.Product;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class BrandProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
