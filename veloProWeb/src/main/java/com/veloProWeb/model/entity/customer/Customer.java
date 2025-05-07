package com.veloProWeb.model.entity.customer;

import com.veloProWeb.model.Enum.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
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

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<PaymentCustomer> paymentCustomerList = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<TicketHistory> ticketHistoryList = new ArrayList<>();
}
