package com.entrenador.pizarratactica.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Tipo de sancion del codigo interno del equipo (descripcion + importe).
 */
public class TipoMulta {

    private final String descripcion;
    private final double cantidad;

    public TipoMulta(String descripcion, double cantidad) {
        this.descripcion = descripcion;
        this.cantidad = cantidad;
    }

    public String getDescripcion() { return descripcion; }
    public double getCantidad() { return cantidad; }

    /** Texto para el desplegable: "Descripcion (2,00€)". */
    @Override
    public String toString() {
        return descripcion + " (" + String.format(Locale.getDefault(), "%.2f", cantidad) + "€)";
    }

    /** Catalogo de multas del equipo. */
    public static List<TipoMulta> catalogo() {
        return Collections.unmodifiableList(Arrays.asList(
                new TipoMulta("Puntualidad: 1.ª Falta injustificada", 1.0),
                new TipoMulta("Puntualidad: 2.ª Falta injustificada", 2.0),
                new TipoMulta("Puntualidad: 3.ª Falta injustificada", 3.0),
                new TipoMulta("Puntualidad: 4.ª Falta injustificada", 5.0),
                new TipoMulta("No avisar ausencia con 24h (incluye exámenes)", 2.0),
                new TipoMulta("Olvidar material o documentación (DNI, etc.)", 1.0),
                new TipoMulta("No acudir con ropa de paseo oficial", 1.0),
                new TipoMulta("Indumentaria incorrecta / Joyas / Piercings", 1.0),
                new TipoMulta("Móvil en vestuario / Fotos o vídeos", 2.0),
                new TipoMulta("No ducharse tras entreno o partido", 1.0),
                new TipoMulta("Ir a Coordinación sin pasar por cuerpo técnico", 3.0),
                new TipoMulta("Tarjeta por protesta o menosprecio a árbitro", 5.0),
                new TipoMulta("Falta de respeto a compañeros/rivales/redes", 5.0)
        ));
    }

    /**
     * Suma lo recaudado (pagadas) y lo pendiente.
     * @return {bote, pendiente}
     */
    public static double[] calcularTotales(List<Multa> multas) {
        double bote = 0, pendiente = 0;
        for (Multa m : multas) {
            if (m.isPagada()) bote += m.getCantidad();
            else pendiente += m.getCantidad();
        }
        return new double[]{bote, pendiente};
    }
}
