package com.veloProWeb.model.entity;

import com.veloProWeb.model.entity.Product.Product;
import com.veloProWeb.model.entity.User.User;
import com.veloProWeb.model.Enum.MovementsType;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Entity
@Data
public class Kardex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Temporal(TemporalType.DATE)
    @CreatedDate
    private LocalDate date;
    private int quantity;
    private int stock;
    private String comment;
    private int price;
    @Enumerated(EnumType.STRING)
    private MovementsType movementsType;

    @ManyToOne
    @JoinColumn(name = "id_product", nullable = true)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = true)
    private User user;
}
