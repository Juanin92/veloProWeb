package com.veloProWeb.Model.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private String status;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Temporal(TemporalType.DATE)
    @CreatedDate
    private LocalDate created;

    @ManyToOne
    @JoinColumn(name = "sale_id", nullable = false)
    private User user;
}
