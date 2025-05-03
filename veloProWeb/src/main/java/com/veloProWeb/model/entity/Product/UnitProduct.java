package com.veloProWeb.model.entity.Product;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UnitProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nameUnit;

    @Override
    public String toString() {
        return nameUnit;
    }
}
