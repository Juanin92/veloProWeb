package com.veloproweb.model.entity.purchase;

import com.veloproweb.model.entity.product.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
