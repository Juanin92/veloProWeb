package com.veloProWeb.model.entity.Sale;

import com.veloProWeb.model.entity.User.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
