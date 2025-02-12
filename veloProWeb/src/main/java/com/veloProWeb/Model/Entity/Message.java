package com.veloProWeb.Model.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String context;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Temporal(TemporalType.DATE)
    @CreatedDate
    private LocalDate created;
    private boolean isRead;
    private boolean isDelete;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender_User;
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver_User;
}
