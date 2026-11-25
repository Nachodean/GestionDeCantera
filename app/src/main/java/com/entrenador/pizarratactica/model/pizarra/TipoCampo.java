package com.entrenador.pizarratactica.model.pizarra;

/** Fondo del ejercicio. */
public enum TipoCampo {
    COMPLETO("Campo completo"),
    MEDIO("Medio campo"),
    SIN_LINEAS("Césped sin líneas");

    private final String nombre;

    TipoCampo(String nombre) { this.nombre = nombre; }

    public String getNombre() { return nombre; }

    public static TipoCampo desdeNombre(String nombre) {
        if (nombre == null) return COMPLETO;
        for (TipoCampo t : values()) {
            if (t.name().equals(nombre)) return t;
        }
        return COMPLETO;
    }
}
