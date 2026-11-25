package com.entrenador.pizarratactica.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class JugadorTest {

    @Test
    public void testCreacionJugador() {
        Jugador j = new Jugador("Jugador", "7", 7, Jugador.Posicion.DEL);

        assertEquals("Jugador", j.getNombre());
        assertEquals("7", j.getApellidos());
        assertEquals(7, j.getDorsal());
        assertEquals(Jugador.Posicion.DEL, j.getPosicion());
        assertTrue(j.isActivo());
        assertEquals("#7 Jugador 7", j.getNombreCompleto());
    }

    @Test
    public void testPosicionesNombre() {
        assertEquals("Portero", Jugador.Posicion.POR.getNombre());
        assertEquals("Defensa", Jugador.Posicion.DEF.getNombre());
        assertEquals("Centrocampista", Jugador.Posicion.MED.getNombre());
        assertEquals("Delantero", Jugador.Posicion.DEL.getNombre());
    }

    @Test
    public void testModificacionDatos() {
        Jugador j = new Jugador();
        j.setId("id_123");
        j.setNombre("Jugador");
        j.setApellidos("6");
        j.setDorsal(6);
        j.setPosicion(Jugador.Posicion.MED);
        j.setActivo(false);

        assertEquals("id_123", j.getId());
        assertEquals("Jugador", j.getNombre());
        assertEquals("6", j.getApellidos());
        assertEquals(6, j.getDorsal());
        assertEquals(Jugador.Posicion.MED, j.getPosicion());
        assertFalse(j.isActivo());
        assertEquals("#6 Jugador 6", j.getNombreCompleto());
    }
}
