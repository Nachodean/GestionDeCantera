package com.entrenador.pizarratactica.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class RegistroMinutosTest {

    @Test
    public void testRegistroMinutosCreacion() {
        RegistroMinutos reg = new RegistroMinutos("j1", "Pedri", 4, "Real Madrid", "20/10/2026", 85);

        assertEquals("j1", reg.getJugadorId());
        assertEquals("Pedri", reg.getJugadorNombre());
        assertEquals(4, reg.getJornada());
        assertEquals("Real Madrid", reg.getRival());
        assertEquals("20/10/2026", reg.getFecha());
        assertEquals(85, reg.getMinutosJugados());
    }

    @Test
    public void testAcumulacionMinutos() {
        RegistroMinutos r1 = new RegistroMinutos("j1", "Pedri", 1, "Rival A", "01/10/2026", 90);
        RegistroMinutos r2 = new RegistroMinutos("j1", "Pedri", 2, "Rival B", "08/10/2026", 75);
        RegistroMinutos r3 = new RegistroMinutos("j1", "Pedri", 3, "Rival C", "15/10/2026", 45);

        int total = r1.getMinutosJugados() + r2.getMinutosJugados() + r3.getMinutosJugados();
        assertEquals(210, total);
    }
}
