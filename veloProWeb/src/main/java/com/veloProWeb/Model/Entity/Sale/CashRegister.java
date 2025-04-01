package com.veloProWeb.Model.Entity.Sale;

import com.veloProWeb.Model.Entity.User.User;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
public class CashRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private LocalDateTime dateOpening;
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private LocalDateTime dateClosing;
    private int amountOpening;
    private int amountClosingCash;
    private int amountClosingPos;
    private String status;
    private String comment;
    private boolean alert;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = true)
    private User user;
}
