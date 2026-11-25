package com.entrenador.pizarratactica.model.pizarra;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class EjercicioTest {

    @Test
    public void agregarYBuscarElementos() {
        Ejercicio ej = new Ejercicio();
        ElementoPizarra a = ej.agregarElemento(TipoElemento.CHINO, 0.2f, 0.3f, Colores.ROJO);
        ElementoPizarra b = ej.agregarElemento(TipoElemento.CONO, 0.5f, 0.5f, Colores.AZUL);
        assertNotEquals(a.getId(), b.getId());
        assertSame(a, ej.buscarElemento(a.getId()));
        assertNull(ej.buscarElemento(999));
        assertEquals(2, ej.getElementos().size());
    }

    @Test
    public void posicionSeLimitaAlCampo() {
        Ejercicio ej = new Ejercicio();
        ElementoPizarra e = ej.agregarElemento(TipoElemento.BALON, -0.5f, 1.7f, Colores.BLANCO);
        assertEquals(0f, e.getX(), 0.0001f);
        assertEquals(1f, e.getY(), 0.0001f);
        e.mover(0.4f, 0.6f);
        assertEquals(0.4f, e.getX(), 0.0001f);
    }

    @Test
    public void dorsalesConsecutivosPorColor() {
        Ejercicio ej = new Ejercicio();
        assertEquals(1, ej.siguienteNumero(Colores.ROJO, ElementoPizarra.SIN_COLOR));
        ElementoPizarra j1 = ej.agregarElemento(TipoElemento.JUGADOR, 0.1f, 0.1f, Colores.ROJO);
        j1.setNumero(ej.siguienteNumero(Colores.ROJO, ElementoPizarra.SIN_COLOR));
        ElementoPizarra j2 = ej.agregarElemento(TipoElemento.JUGADOR, 0.2f, 0.1f, Colores.ROJO);
        j2.setNumero(ej.siguienteNumero(Colores.ROJO, ElementoPizarra.SIN_COLOR));
        assertEquals(1, j1.getNumero());
        assertEquals(2, j2.getNumero());
        // Otro color empieza otra vez en 1
        assertEquals(1, ej.siguienteNumero(Colores.AMARILLO, ElementoPizarra.SIN_COLOR));
        // El equipo bicolor es distinto del blanco liso
        assertEquals(1, ej.siguienteNumero(Colores.BLANCO, Colores.NEGRO));
    }

    @Test
    public void duplicarElementoDesplazaYRenumera() {
        Ejercicio ej = new Ejercicio();
        ElementoPizarra j = ej.agregarElemento(TipoElemento.JUGADOR, 0.5f, 0.5f, Colores.AZUL);
        j.setNumero(1);
        ElementoPizarra copia = ej.duplicarElemento(j.getId());
        assertNotNull(copia);
        assertNotEquals(j.getId(), copia.getId());
        assertEquals(2, copia.getNumero());
        assertTrue(copia.getX() > j.getX());
    }

    @Test
    public void eliminarYTraerAlFrente() {
        Ejercicio ej = new Ejercicio();
        ElementoPizarra a = ej.agregarElemento(TipoElemento.CHINO, 0.1f, 0.1f, Colores.ROJO);
        ElementoPizarra b = ej.agregarElemento(TipoElemento.CHINO, 0.2f, 0.2f, Colores.ROJO);
        ej.traerAlFrente(a.getId());
        assertSame(a, ej.getElementos().get(1));
        assertTrue(ej.eliminarElemento(b.getId()));
        assertFalse(ej.eliminarElemento(b.getId()));
        assertEquals(1, ej.getElementos().size());
    }

    @Test
    public void recuentoMaterialIgnoraJugadoresYTextos() {
        Ejercicio ej = new Ejercicio();
        for (int i = 0; i < 12; i++) ej.agregarElemento(TipoElemento.CHINO, 0.1f, 0.1f, Colores.AZUL);
        ej.agregarElemento(TipoElemento.PORTERIA, 0.5f, 0.5f, Colores.BLANCO);
        ej.agregarElemento(TipoElemento.JUGADOR, 0.5f, 0.5f, Colores.ROJO);
        ej.agregarElemento(TipoElemento.TEXTO, 0.5f, 0.5f, Colores.NEGRO);
        Map<TipoElemento, Integer> m = ej.recuentoMaterial();
        assertEquals(Integer.valueOf(12), m.get(TipoElemento.CHINO));
        assertEquals(Integer.valueOf(1), m.get(TipoElemento.PORTERIA));
        assertFalse(m.containsKey(TipoElemento.JUGADOR));
        assertFalse(m.containsKey(TipoElemento.TEXTO));
    }

    @Test
    public void copiarEsProfunda() {
        Ejercicio ej = new Ejercicio();
        ElementoPizarra e = ej.agregarElemento(TipoElemento.CONO, 0.3f, 0.3f, Colores.ROJO);
        Trazo t = ej.agregarTrazo(TipoTrazo.PASE, Colores.NEGRO);
        t.setExtremos(0.1f, 0.1f, 0.4f, 0.4f);
        Ejercicio copia = ej.copiar();
        e.mover(0.9f, 0.9f);
        t.desplazar(0.1f, 0f);
        assertEquals(0.3f, copia.getElementos().get(0).getX(), 0.0001f);
        assertEquals(0.1f, copia.getTrazos().get(0).getPuntos().get(0)[0], 0.0001f);
        assertEquals(ej.getId(), copia.getId());
        assertNotEquals(ej.getId(), ej.duplicar().getId());
    }

    @Test
    public void rotacionYEscalaAcotadas() {
        ElementoPizarra e = new ElementoPizarra(1, TipoElemento.PORTERIA, 0.5f, 0.5f, Colores.BLANCO);
        e.girar(-15);
        assertEquals(345f, e.getRotacion(), 0.0001f);
        e.girar(30);
        assertEquals(15f, e.getRotacion(), 0.0001f);
        e.setEscala(100f);
        assertEquals(4f, e.getEscala(), 0.0001f);
        e.setEscala(0.01f);
        assertEquals(0.4f, e.getEscala(), 0.0001f);
    }

    @Test
    public void trazoNoSeSaleDelCampoAlDesplazar() {
        Trazo t = new Trazo(1, TipoTrazo.PASE, Colores.NEGRO);
        t.setExtremos(0.8f, 0.5f, 0.95f, 0.5f);
        t.desplazar(0.5f, 0f);
        assertEquals(1f, t.getPuntos().get(1)[0], 0.0001f);
        assertEquals(0.85f, t.getPuntos().get(0)[0], 0.0001f);
        assertEquals(0.15f, t.longitud(), 0.0001f);
    }
}
