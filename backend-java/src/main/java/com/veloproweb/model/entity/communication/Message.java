package com.veloproweb.model.entity.communication;

import com.veloproweb.model.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    private User senderUser;
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiverUser;
}
