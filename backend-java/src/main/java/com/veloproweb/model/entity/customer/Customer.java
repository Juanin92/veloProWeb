package com.veloproweb.model.entity.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.veloproweb.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    private String phone;
    private String email;
    private int debt;
    private int totalDebt;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private boolean account;

    @JsonIgnore
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<PaymentCustomer> paymentCustomerList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<TicketHistory> ticketHistoryList = new ArrayList<>();
}
