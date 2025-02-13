package com.veloProWeb.Model.Entity;

import com.veloProWeb.Model.Entity.User.User;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private LocalDateTime entryDate;
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private LocalDateTime endaDate;
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private LocalDateTime actionDate;
    private String action;
    private String comment;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = true)
    private User user;
}
