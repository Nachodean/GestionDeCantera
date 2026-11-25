package com.entrenador.pizarratactica.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class MultaTest {

    @Test
    public void testCreacionMultaYEstadoInicial() {
        Multa m = new Multa("jug_1", "Jugador 1", "12/10/2026", "Retraso entrenamiento", 5.0);

        assertEquals("jug_1", m.getJugadorId());
        assertEquals("Jugador 1", m.getJugadorNombre());
        assertEquals("12/10/2026", m.getFecha());
        assertEquals("Retraso entrenamiento", m.getMotivo());
        assertEquals(5.0, m.getCantidad(), 0.001);
        assertFalse(m.isPagada());
    }

    @Test
    public void testCambioEstadoPago() {
        Multa m = new Multa("jug_2", "Jugador 2", "14/10/2026", "No llevar espinilleras", 3.5);
        assertFalse(m.isPagada());

        m.setPagada(true);
        assertTrue(m.isPagada());

        m.setPagada(false);
        assertFalse(m.isPagada());
    }

    @Test
    public void testCalculoBalanceMultas() {
        Multa m1 = new Multa("j1", "Jugador 1", "01/10/2026", "Retraso", 5.0, false);
        Multa m2 = new Multa("j2", "Jugador 2", "02/10/2026", "Ropa inadecuada", 10.0, true);
        Multa m3 = new Multa("j1", "Jugador 1", "03/10/2026", "Falta no justificada", 15.0, false);

        Multa[] multas = new Multa[]{m1, m2, m3};

        double total = 0.0;
        double pendiente = 0.0;
        for (Multa m : multas) {
            total += m.getCantidad();
            if (!m.isPagada()) {
                pendiente += m.getCantidad();
            }
        }

        assertEquals(30.0, total, 0.001);
        assertEquals(20.0, pendiente, 0.001);
    }
}
