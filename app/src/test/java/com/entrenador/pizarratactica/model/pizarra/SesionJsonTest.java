package com.entrenador.pizarratactica.model.pizarra;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class SesionJsonTest {

    private Sesion sesionDeEjemplo() {
        Sesion s = Sesion.nueva(1234L);
        s.setNumero("14");
        s.setFecha("25/11/2026");
        s.setObjetivo("Finalización y centros laterales");
        Ejercicio ej = s.getEjercicio(0);
        ej.setTitulo("Centro lateral 3vs2");
        ej.setDescripcion("El extremo da la bola al lateral\ny centra al área.");
        ej.setDuracionMin(15);
        ej.setCampo(TipoCampo.MEDIO);

        ElementoPizarra j = ej.agregarElemento(TipoElemento.JUGADOR, 0.25f, 0.75f, Colores.BLANCO);
        j.setColor2(Colores.NEGRO);
        j.setNumero(2);
        j.setRotacion(30);
        j.setEscala(1.5f);
        ElementoPizarra txt = ej.agregarElemento(TipoElemento.TEXTO, 0.6f, 0.2f, Colores.BLANCO);
        txt.setColor2(0xFFC0504D);
        txt.setTexto("Tiro");

        Trazo pase = ej.agregarTrazo(TipoTrazo.PASE, Colores.ROJO);
        pase.setExtremos(0.1f, 0.2f, 0.3f, 0.4f);
        Trazo medida = ej.agregarTrazo(TipoTrazo.MEDIDA, Colores.AZUL);
        medida.setExtremos(0.7f, 0.1f, 0.7f, 0.6f);
        medida.setTexto("35 m");
        Trazo libre = ej.agregarTrazo(TipoTrazo.LIBRE, Colores.AMARILLO);
        libre.agregarPunto(0.1f, 0.1f);
        libre.agregarPunto(0.15f, 0.12f);
        libre.agregarPunto(0.2f, 0.18f);

        Ejercicio ej2 = s.agregarEjercicio();
        ej2.setTitulo("Rondo");
        ej2.agregarElemento(TipoElemento.CHINO, 0.5f, 0.5f, Colores.AMARILLO);
        return s;
    }

    @Test
    public void idaYVueltaConservaTodo() throws Exception {
        Sesion original = sesionDeEjemplo();
        JSONObject json = SesionJson.aJson(original);
        Sesion leida = SesionJson.desdeJson(new JSONObject(json.toString()));

        assertEquals(original.getId(), leida.getId());
        assertEquals("14", leida.getNumero());
        assertEquals("25/11/2026", leida.getFecha());
        assertEquals("Finalización y centros laterales", leida.getObjetivo());
        assertEquals(1234L, leida.getCreada());
        assertEquals(2, leida.numEjercicios());

        Ejercicio ej = leida.getEjercicio(0);
        assertEquals("Centro lateral 3vs2", ej.getTitulo());
        assertEquals("El extremo da la bola al lateral\ny centra al área.", ej.getDescripcion());
        assertEquals(15, ej.getDuracionMin());
        assertEquals(TipoCampo.MEDIO, ej.getCampo());
        assertEquals(2, ej.getElementos().size());
        assertEquals(3, ej.getTrazos().size());

        ElementoPizarra j = ej.getElementos().get(0);
        assertEquals(TipoElemento.JUGADOR, j.getTipo());
        assertEquals(0.25f, j.getX(), 0.0001f);
        assertEquals(0.75f, j.getY(), 0.0001f);
        assertEquals(Colores.BLANCO, j.getColor());
        assertEquals(Colores.NEGRO, j.getColor2());
        assertEquals(2, j.getNumero());
        assertEquals(30f, j.getRotacion(), 0.0001f);
        assertEquals(1.5f, j.getEscala(), 0.0001f);
        assertEquals("Tiro", ej.getElementos().get(1).getTexto());

        Trazo medida = ej.getTrazos().get(1);
        assertEquals(TipoTrazo.MEDIDA, medida.getTipo());
        assertEquals("35 m", medida.getTexto());
        assertEquals(Colores.AZUL, medida.getColor());
        assertEquals(3, ej.getTrazos().get(2).numPuntos());
    }

    @Test
    public void idsNuevosNoChocanTrasCargar() throws Exception {
        Sesion leida = SesionJson.desdeJson(SesionJson.aJson(sesionDeEjemplo()));
        Ejercicio ej = leida.getEjercicio(0);
        ElementoPizarra nuevo = ej.agregarElemento(TipoElemento.CONO, 0.5f, 0.5f, Colores.ROJO);
        for (ElementoPizarra e : ej.getElementos()) {
            if (e != nuevo) assertNotEquals(e.getId(), nuevo.getId());
        }
        for (Trazo t : ej.getTrazos()) assertNotEquals(t.getId(), nuevo.getId());
    }

    @Test
    public void tiposDesconocidosNoRompenLaCarga() throws Exception {
        JSONObject json = SesionJson.aJson(sesionDeEjemplo());
        JSONArray elementos = json.getJSONArray("ejercicios").getJSONObject(0).getJSONArray("elementos");
        elementos.put(new JSONObject().put("tipo", "PLATILLO_VOLADOR").put("x", 0.5).put("y", 0.5));
        Sesion leida = SesionJson.desdeJson(json);
        assertEquals(2, leida.getEjercicio(0).getElementos().size());
    }

    @Test
    public void sesionSinEjerciciosRecibeUno() throws Exception {
        JSONObject json = new JSONObject().put("id", "abc").put("numero", "3");
        Sesion s = SesionJson.desdeJson(json);
        assertEquals(1, s.numEjercicios());
        assertEquals("3", s.getNumero());
    }

    @Test
    public void convierteFormatoAntiguo() throws Exception {
        JSONObject legado = new JSONObject()
                .put("id", "sesion_1700000000000")
                .put("numeroSesion", "5")
                .put("fecha", "20/10/2026")
                .put("elementos", new JSONArray()
                        .put(new JSONObject().put("tipo", "CHINO_ROJO").put("x", 600.0).put("y", 450.0).put("rotacion", 0.0))
                        .put(new JSONObject().put("tipo", "PORTERIA").put("x", 120.0).put("y", 90.0).put("rotacion", 90.0))
                        .put(new JSONObject().put("tipo", "JUGADOR_AZUL").put("x", 5000.0).put("y", -10.0)));
        Sesion s = SesionJson.desdeJsonLegado(legado);
        assertEquals("sesion_1700000000000", s.getId());
        assertEquals("5", s.getNumero());
        assertEquals("20/10/2026", s.getFecha());
        assertEquals(1, s.numEjercicios());

        Ejercicio ej = s.getEjercicio(0);
        assertEquals(3, ej.getElementos().size());
        ElementoPizarra chino = ej.getElementos().get(0);
        assertEquals(TipoElemento.CHINO, chino.getTipo());
        assertEquals(Colores.ROJO, chino.getColor());
        assertEquals(0.5f, chino.getX(), 0.0001f);
        assertEquals(0.5f, chino.getY(), 0.0001f);
        assertEquals(90f, ej.getElementos().get(1).getRotacion(), 0.0001f);
        // Coordenadas fuera de rango quedan dentro del campo
        assertEquals(1f, ej.getElementos().get(2).getX(), 0.0001f);
        assertEquals(0f, ej.getElementos().get(2).getY(), 0.0001f);
    }
}
