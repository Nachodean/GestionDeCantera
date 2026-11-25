package com.entrenador.pizarratactica.model;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TipoMultaTest {

    @Test
    public void catalogoTieneLasSancionesDelEquipo() {
        List<TipoMulta> catalogo = TipoMulta.catalogo();
        assertEquals(13, catalogo.size());
        assertEquals(1.0, catalogo.get(0).getCantidad(), 0.001);
        assertTrue(catalogo.get(0).getDescripcion().startsWith("Puntualidad"));
    }

    @Test
    public void totalesSeparanBoteYPendiente() {
        List<Multa> multas = Arrays.asList(
                new Multa("j1", "Jugador 1", "01/10/2026", "Retraso", 5.0, true),
                new Multa("j2", "Jugador 2", "02/10/2026", "Móvil", 2.0, false),
                new Multa("j1", "Jugador 1", "03/10/2026", "Ropa", 1.5, false));
        double[] t = TipoMulta.calcularTotales(multas);
        assertEquals(5.0, t[0], 0.001);
        assertEquals(3.5, t[1], 0.001);
    }

    @Test
    public void sinMultasTotalesACero() {
        double[] t = TipoMulta.calcularTotales(Collections.<Multa>emptyList());
        assertEquals(0.0, t[0], 0.001);
        assertEquals(0.0, t[1], 0.001);
    }
}
