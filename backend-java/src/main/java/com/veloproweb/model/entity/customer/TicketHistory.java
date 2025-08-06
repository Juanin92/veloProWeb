package com.veloproweb.model.entity.customer;

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
public class TicketHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String document;
    private int total;
    private boolean status;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Temporal(TemporalType.DATE)
    @CreatedDate
    private LocalDate date;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Temporal(TemporalType.DATE)
    @CreatedDate
    private LocalDate notificationsDate;

    @ManyToOne
    @JoinColumn(name = "id_customer")
    private Customer customer;

    @Override
    public String toString() {
        return document;
    }
}
