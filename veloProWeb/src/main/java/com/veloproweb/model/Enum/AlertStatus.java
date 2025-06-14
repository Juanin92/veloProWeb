package com.veloproweb.model.Enum;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AlertStatus {
    ALERT("Alerta"),
    CHECKED("Revisado"),
    PENDING("Pendiente");

    private final String name;
}
