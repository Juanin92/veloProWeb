package com.veloProWeb.Model.Entity.Costumer;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "payment_costumer")
public class PaymentCostumer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int amount;
    private String comment;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Temporal(TemporalType.DATE)
    @CreatedDate
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "costumer_id", nullable = false)
    private Costumer costumer;

    @ManyToOne
    @JoinColumn(name = "ticketHistory_id", nullable = false)
    private TicketHistory document;
}