package com.veloProWeb.Model.Entity;

import com.veloProWeb.Model.Entity.Sale.CashRegister;
import com.veloProWeb.Model.Enum.Rol;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Temporal(TemporalType.DATE)
    @CreatedDate
    private LocalDate date;

    private String name;
    private String surname;
    private String username;
    private String rut;
    private String email;
    private String password;
    private String token;
    private boolean status;

    @Enumerated(EnumType.STRING)
    private Rol role;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Kardex> kardexList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CashRegister> cashRegisterList = new ArrayList<>();
}
