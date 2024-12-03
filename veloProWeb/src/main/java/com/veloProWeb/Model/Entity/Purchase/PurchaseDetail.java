package com.veloProWeb.Model.Entity.Purchase;

import com.veloProWeb.Model.Entity.Product.Product;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PurchaseDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    private int price;
    private int tax;
    private int total;

    @ManyToOne
    @JoinColumn(name = "id_purchase", nullable = false)
    private Purchase purchase;

    @ManyToOne
    @JoinColumn(name = "id_product", nullable = false)
    private Product product;
}
