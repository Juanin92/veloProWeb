package com.veloProWeb.Model.Entity.Product;

import com.veloProWeb.Model.Entity.Kardex;
import com.veloProWeb.Model.Enum.StatusProduct;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private int salePrice;
    private int buyPrice;
    private int stock;
    private boolean status;
    @Enumerated(EnumType.STRING)
    private StatusProduct statusProduct;

    @ManyToOne
    @JoinColumn(name = "id_brand", nullable = false)
    private BrandProduct brand;

    @ManyToOne
    @JoinColumn(name = "id_unit", nullable = false)
    private UnitProduct unit;

    @ManyToOne
    @JoinColumn(name = "id_subcategory", nullable = false)
    private SubcategoryProduct subcategoryProduct;

    @ManyToOne
    @JoinColumn(name = "id_category", nullable = false)
    private CategoryProduct category;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Kardex> kardexList = new ArrayList<>();
}
