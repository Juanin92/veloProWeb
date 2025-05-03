package com.veloProWeb.model.entity.Purchase;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Purchase{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String document;
    private String documentType;
    private int iva;
    private int purchaseTotal;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Temporal(TemporalType.DATE)
    @CreatedDate
    private LocalDate date;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.EAGER)
    private List<PurchaseDetail> purchaseDetails = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "id_supplier")
    private Supplier supplier;
}
