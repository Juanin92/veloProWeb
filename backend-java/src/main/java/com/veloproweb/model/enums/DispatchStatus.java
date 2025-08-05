package com.veloproweb.model.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DispatchStatus {
    PREPARING("En Preparación"),
    IN_ROUTE("En Ruta"),
    DELIVERED("Entregado"),
    DELETED("Eliminado");

    private final String name;
}
