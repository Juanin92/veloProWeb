package com.veloProWeb.Model.Entity.Costumer;

import com.veloProWeb.Model.Enum.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
public class Costumer {

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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<PaymentCostumer> paymentCostumerList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<TicketHistory> ticketHistoryList = new ArrayList<>();

    @Override
    public String toString() {
        return name + " " + surname;
    }
}
