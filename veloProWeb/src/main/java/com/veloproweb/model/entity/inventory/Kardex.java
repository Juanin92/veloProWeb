package com.veloproweb.model.entity.inventory;

import com.veloproweb.model.entity.product.Product;
import com.veloproweb.model.entity.User.User;
import com.veloproweb.model.Enum.MovementsType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Entity
@Data
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
