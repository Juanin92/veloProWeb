package com.veloProWeb.model.Enum;

public enum Rol {

    MASTER("UsuarioMaestro"),
    ADMIN("Administrador"),
    GUEST("Invitado"),
    WAREHOUSE("Coordinador"),
    SELLER("vendedor");

    private final String displayName;
    Rol(String displayName) { this.displayName = displayName;}

    @Override
    public String toString() { return displayName; }
}
