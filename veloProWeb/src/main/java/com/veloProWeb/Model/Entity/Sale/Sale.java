package com.veloProWeb.Model.Entity.Sale;

import com.veloProWeb.Model.Entity.Costumer.Costumer;
import com.veloProWeb.Model.Enum.PaymentMethod;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Temporal(TemporalType.DATE)
    @CreatedDate
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    private String document;
    private String comment;
    private int discount;
    private int tax;
    private int totalSale;
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "id_costumer", nullable = true)
    private Costumer costumer;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.EAGER)
    private List<SaleDetail> saleDetails = new ArrayList<>();
}
