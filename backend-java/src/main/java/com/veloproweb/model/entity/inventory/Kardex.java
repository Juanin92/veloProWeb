package com.veloproweb.model.entity.inventory;

import com.veloproweb.model.entity.product.Product;
import com.veloproweb.model.entity.user.User;
import com.veloproweb.model.enums.MovementsType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    @JoinColumn(name = "id_product")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;
}
