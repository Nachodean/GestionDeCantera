package com.entrenador.pizarratactica.model.pizarra;

import org.junit.Test;

import static org.junit.Assert.*;

public class SesionTest {

    @Test
    public void sesionNuevaTieneUnEjercicio() {
        Sesion s = Sesion.nueva(1000L);
        assertEquals(1, s.numEjercicios());
        assertEquals(1000L, s.getCreada());
        assertEquals("Ejercicio 1", s.getEjercicio(0).getTitulo());
    }

    @Test
    public void noSePuedeQuedarSinEjercicios() {
        Sesion s = Sesion.nueva(0);
        assertFalse(s.eliminarEjercicio(0));
        s.agregarEjercicio();
        assertTrue(s.eliminarEjercicio(0));
        assertEquals(1, s.numEjercicios());
    }

    @Test
    public void duplicarYMoverEjercicios() {
        Sesion s = Sesion.nueva(0);
        s.getEjercicio(0).setTitulo("Rondo");
        s.getEjercicio(0).agregarElemento(TipoElemento.CHINO, 0.5f, 0.5f, Colores.ROJO);
        Ejercicio copia = s.duplicarEjercicio(0);
        assertEquals(2, s.numEjercicios());
        assertEquals("Rondo (copia)", copia.getTitulo());
        assertEquals(1, copia.getElementos().size());
        assertNotEquals(s.getEjercicio(0).getId(), copia.getId());

        Ejercicio tercero = s.agregarEjercicio();
        assertTrue(s.moverEjercicio(2, 0));
        assertSame(tercero, s.getEjercicio(0));
        assertFalse(s.moverEjercicio(0, 7));
    }

    @Test
    public void duracionTotalSumaEjercicios() {
        Sesion s = Sesion.nueva(0);
        s.getEjercicio(0).setDuracionMin(10);
        s.agregarEjercicio().setDuracionMin(15);
        s.agregarEjercicio().setDuracionMin(-5); // se ignora el negativo
        assertEquals(25, s.duracionTotal());
    }

    @Test
    public void duplicarSesionCreaCopiaIndependiente() {
        Sesion s = Sesion.nueva(0);
        s.setNumero("12");
        s.getEjercicio(0).agregarElemento(TipoElemento.CONO, 0.5f, 0.5f, Colores.AMARILLO);
        Sesion c = s.duplicar(5L);
        assertNotEquals(s.getId(), c.getId());
        assertEquals("12", c.getNumero());
        c.getEjercicio(0).limpiar();
        assertEquals(1, s.getEjercicio(0).getElementos().size());
    }

    @Test
    public void tituloVisible() {
        Sesion s = Sesion.nueva(0);
        assertEquals("Sesión sin número", s.getTituloVisible());
        s.setNumero(" 7 ");
        assertEquals("Sesión Nº 7", s.getTituloVisible());
    }

    @Test
    public void historialDeshaceYRehace() {
        Ejercicio ej = new Ejercicio();
        Historial h = new Historial();
        assertFalse(h.puedeDeshacer());

        h.registrar(ej);
        ElementoPizarra e = ej.agregarElemento(TipoElemento.CHINO, 0.2f, 0.2f, Colores.ROJO);
        h.registrar(ej);
        e.mover(0.8f, 0.8f);

        assertTrue(h.deshacer(ej));
        assertEquals(0.2f, ej.getElementos().get(0).getX(), 0.0001f);
        assertTrue(h.deshacer(ej));
        assertTrue(ej.getElementos().isEmpty());
        assertFalse(h.deshacer(ej));

        assertTrue(h.rehacer(ej));
        assertEquals(1, ej.getElementos().size());
        assertTrue(h.rehacer(ej));
        assertEquals(0.8f, ej.getElementos().get(0).getX(), 0.0001f);
        assertFalse(h.rehacer(ej));
    }

    @Test
    public void nuevoCambioBorraRehacer() {
        Ejercicio ej = new Ejercicio();
        Historial h = new Historial();
        h.registrar(ej);
        ej.agregarElemento(TipoElemento.CHINO, 0.2f, 0.2f, Colores.ROJO);
        h.deshacer(ej);
        assertTrue(h.puedeRehacer());
        h.registrar(ej);
        assertFalse(h.puedeRehacer());
    }
}
