package com.veloproweb.model.entity.purchase;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String rut;
    private String email;
    private String phone;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    List<Purchase> purchaseList = new ArrayList<>();

    @Override
    public String toString() {
        return name;
    }
}
