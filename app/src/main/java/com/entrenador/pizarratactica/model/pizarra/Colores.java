package com.entrenador.pizarratactica.model.pizarra;

/**
 * Paleta de colores del material de entrenamiento (valores ARGB).
 * Coinciden con los colores de los chinos, conos y petos de las diapositivas.
 */
public final class Colores {

    private Colores() {}

    public static final int ROJO = 0xFFE53935;
    public static final int ROJO_OSCURO = 0xFFB71C1C;
    public static final int NARANJA = 0xFFFF7043;
    public static final int AMARILLO = 0xFFFFEB3B;
    public static final int AZUL = 0xFF29B6F6;
    public static final int AZUL_OSCURO = 0xFF1565C0;
    public static final int VERDE = 0xFF8BC34A;
    public static final int VERDE_OSCURO = 0xFF2E7D32;
    public static final int BLANCO = 0xFFFFFFFF;
    public static final int NEGRO = 0xFF212121;
    public static final int GRIS = 0xFF9E9E9E;
    public static final int MORADO = 0xFF7E57C2;
    public static final int MARRON = 0xFFC08A4B;

    /** Colores disponibles para cambiar el color de un objeto o flecha. */
    public static final int[] PALETA = {
            ROJO, NARANJA, AMARILLO, VERDE, AZUL, AZUL_OSCURO,
            MORADO, ROJO_OSCURO, BLANCO, GRIS, NEGRO, MARRON
    };
}
