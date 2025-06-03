package com.veloProWeb.model.entity.Sale;

import com.veloProWeb.model.entity.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    private int price;
    private int tax;
    private int total;

    @ManyToOne
    @JoinColumn(name = "id_sale", nullable = true)
    private Sale sale;

    @ManyToOne
    @JoinColumn(name = "id_product", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "dispatch_id")
    private Dispatch dispatch;
}
