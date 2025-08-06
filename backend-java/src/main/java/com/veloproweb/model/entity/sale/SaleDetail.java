package com.veloproweb.model.entity.sale;

import com.veloproweb.model.entity.product.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
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
    @JoinColumn(name = "id_sale", nullable = false)
    private Sale sale;

    @ManyToOne
    @JoinColumn(name = "id_product", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "dispatch_id")
    private Dispatch dispatch;
}
