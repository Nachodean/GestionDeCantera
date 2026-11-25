package com.entrenador.pizarratactica.model.pizarra;

/**
 * Catalogo de objetos que se pueden colocar en la pizarra.
 * <p>
 * {@code tamanoBase} es el tamano del objeto como fraccion del ancho del campo,
 * de modo que el dibujo se ve igual en una tablet, un movil o el PDF exportado.
 */
public enum TipoElemento {

    // ── Material ─────────────────────────────────────────────────────
    CHINO("Chino", Categoria.MATERIAL, 0.026f),
    CONO("Cono", Categoria.MATERIAL, 0.032f),
    CONO_ALTO("Cono alto", Categoria.MATERIAL, 0.040f),
    PICA("Pica", Categoria.MATERIAL, 0.055f),
    PICA_BASE("Pica con base", Categoria.MATERIAL, 0.060f),
    PORTERIA("Portería", Categoria.MATERIAL, 0.110f),
    ESCALERA("Escalera", Categoria.MATERIAL, 0.130f),
    VALLA("Valla", Categoria.MATERIAL, 0.032f),
    ARO("Aro", Categoria.MATERIAL, 0.038f),
    BALON("Balón", Categoria.MATERIAL, 0.026f),
    BALONES("Grupo de balones", Categoria.MATERIAL, 0.060f),
    BOSU("Bosu", Categoria.MATERIAL, 0.050f),
    BALON_MEDICINAL("Balón medicinal", Categoria.MATERIAL, 0.032f),
    FITBALL("Fitball", Categoria.MATERIAL, 0.065f),

    // ── Personas ─────────────────────────────────────────────────────
    JUGADOR("Jugador", Categoria.JUGADORES, 0.034f),
    CAMISETA("Camiseta", Categoria.JUGADORES, 0.055f),

    // ── Anotaciones ──────────────────────────────────────────────────
    TEXTO("Texto", Categoria.TEXTO, 0.030f);

    public enum Categoria { MATERIAL, JUGADORES, TEXTO }

    private final String nombre;
    private final Categoria categoria;
    private final float tamanoBase;

    TipoElemento(String nombre, Categoria categoria, float tamanoBase) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.tamanoBase = tamanoBase;
    }

    public String getNombre() { return nombre; }
    public Categoria getCategoria() { return categoria; }
    public float getTamanoBase() { return tamanoBase; }

    /** Los jugadores llevan dorsal; el resto de objetos no. */
    public boolean admiteNumero() { return this == JUGADOR; }

    /** Busca un tipo por nombre de enum sin lanzar excepcion. */
    public static TipoElemento desdeNombre(String nombre, TipoElemento porDefecto) {
        if (nombre == null) return porDefecto;
        for (TipoElemento t : values()) {
            if (t.name().equals(nombre)) return t;
        }
        return porDefecto;
    }
}
