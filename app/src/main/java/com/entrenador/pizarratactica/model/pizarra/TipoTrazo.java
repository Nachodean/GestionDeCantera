package com.entrenador.pizarratactica.model.pizarra;

/** Tipos de linea que se pueden dibujar sobre el campo. */
public enum TipoTrazo {
    PASE("Pase / tiro", true),
    SIN_BALON("Desplazamiento sin balón", true),
    CONDUCCION("Conducción", true),
    LINEA("Línea", false),
    MEDIDA("Medida", false),
    LIBRE("Dibujo libre", false);

    private final String nombre;
    private final boolean conFlecha;

    TipoTrazo(String nombre, boolean conFlecha) {
        this.nombre = nombre;
        this.conFlecha = conFlecha;
    }

    public String getNombre() { return nombre; }
    public boolean tieneFlecha() { return conFlecha; }

    /** El dibujo libre guarda todos los puntos; el resto solo inicio y fin. */
    public boolean esLibre() { return this == LIBRE; }

    public static TipoTrazo desdeNombre(String nombre, TipoTrazo porDefecto) {
        if (nombre == null) return porDefecto;
        for (TipoTrazo t : values()) {
            if (t.name().equals(nombre)) return t;
        }
        return porDefecto;
    }
}
