package com.entrenador.pizarratactica.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class InformeRivalTest {

    @Test
    public void camposVaciosSeMuestranConGuion() {
        InformeRival inf = new InformeRival("informe_1", "Rival FC", "25/11/2026 10:00");
        inf.setCampo(InformeRival.SISTEMA, "1-4-3-3 (Clasico)");
        inf.setCampo(InformeRival.FORTALEZAS, "");
        assertEquals("1-4-3-3 (Clasico)", inf.getCampo(InformeRival.SISTEMA));
        assertEquals("-", inf.getCampo(InformeRival.FORTALEZAS));
        assertEquals("-", inf.getCampo(InformeRival.ESLABON_DEBIL));
    }

    @Test
    public void resumenIncluyeRivalSistemaYJugadores() {
        InformeRival inf = new InformeRival("informe_1", "Rival FC", "25/11/2026 10:00");
        inf.setCampo(InformeRival.SISTEMA, "1-4-4-2 (Diamante)");
        inf.setCampo(InformeRival.JUGADOR_PELIGROSO, "El 9");
        String r = inf.resumen();
        assertTrue(r.contains("RIVAL: Rival FC"));
        assertTrue(r.contains("SISTEMA: 1-4-4-2 (Diamante)"));
        assertTrue(r.contains("Mas peligroso: El 9"));
    }

    @Test
    public void clavesDelEsquemaNoCambian() {
        // Son las claves con las que ya hay informes guardados en el movil.
        assertEquals("sistema", InformeRival.SISTEMA);
        assertEquals("ofe_gen", InformeRival.OFE_GEN);
        assertEquals("abp_def", InformeRival.ABP_DEF);
        assertEquals("jugador_peligroso", InformeRival.JUGADOR_PELIGROSO);
        assertEquals("eslabon_debil", InformeRival.ESLABON_DEBIL);
    }
}
