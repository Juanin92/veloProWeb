package com.veloproweb.model.entity.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.veloproweb.model.entity.inventory.Kardex;
import com.veloproweb.model.Enum.StatusProduct;
import jakarta.persistence.*;
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
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private int salePrice;
    private int buyPrice;
    private int stock;
    private int reserve;
    private int threshold;
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

    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Kardex> kardexList = new ArrayList<>();
}
