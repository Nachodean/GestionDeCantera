package com.entrenador.pizarratactica.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Informe de analisis de un rival (UEFA A).
 * Los campos se guardan con las mismas claves de siempre para no romper
 * los informes ya guardados ("sistema", "ofe_gen", "fortalezas"...).
 */
public class InformeRival {

    // Claves de los campos (no cambiar: son el esquema guardado)
    public static final String SISTEMA = "sistema";
    public static final String OFE_GEN = "ofe_gen", OFE_DEF = "ofe_def", OFE_MED = "ofe_med", OFE_DEL = "ofe_del";
    public static final String DEF_GEN = "def_gen", DEF_DEF = "def_def", DEF_MED = "def_med", DEF_DEL = "def_del";
    public static final String TRAN_DA = "tran_da", TRAN_AD = "tran_ad";
    public static final String ABP_OFE = "abp_ofe", ABP_DEF = "abp_def";
    public static final String FORTALEZAS = "fortalezas", DEBILIDADES = "debilidades";
    public static final String OBS_OFE = "obs_ofe", OBS_DEF = "obs_def", OBS_ABP = "obs_abp";
    public static final String JUGADOR_PELIGROSO = "jugador_peligroso", ESLABON_DEBIL = "eslabon_debil";

    private String id;
    private String rival;
    private String fecha;
    private final Map<String, String> campos = new LinkedHashMap<>();

    public InformeRival(String id, String rival, String fecha) {
        this.id = id;
        this.rival = rival;
        this.fecha = fecha;
    }

    public void setCampo(String clave, String valor) {
        campos.put(clave, valor == null ? "" : valor);
    }

    /** Valor de un campo o "-" si esta vacio. */
    public String getCampo(String clave) {
        String v = campos.get(clave);
        return v == null || v.isEmpty() ? "-" : v;
    }

    public Map<String, String> getCampos() { return campos; }
    public String getId() { return id; }
    public String getRival() { return rival; }
    public String getFecha() { return fecha; }

    /** Texto legible del informe completo para mostrarlo en pantalla. */
    public String resumen() {
        StringBuilder sb = new StringBuilder();
        sb.append("RIVAL: ").append(rival).append("\n");
        sb.append("FECHA: ").append(fecha).append("\n\n");
        sb.append("SISTEMA: ").append(getCampo(SISTEMA)).append("\n\n");
        sb.append("--- FASE OFENSIVA ---\n");
        sb.append("Tipo ataque: ").append(getCampo(OFE_GEN)).append("\n");
        sb.append("Defensas: ").append(getCampo(OFE_DEF)).append("\n");
        sb.append("Medios: ").append(getCampo(OFE_MED)).append("\n");
        sb.append("Delanteros: ").append(getCampo(OFE_DEL)).append("\n");
        sb.append("Obs: ").append(getCampo(OBS_OFE)).append("\n\n");
        sb.append("--- FASE DEFENSIVA ---\n");
        sb.append("Presion: ").append(getCampo(DEF_GEN)).append("\n");
        sb.append("Defensas: ").append(getCampo(DEF_DEF)).append("\n");
        sb.append("Medios: ").append(getCampo(DEF_MED)).append("\n");
        sb.append("Delanteros: ").append(getCampo(DEF_DEL)).append("\n");
        sb.append("Obs: ").append(getCampo(OBS_DEF)).append("\n\n");
        sb.append("--- TRANSICIONES Y ABP ---\n");
        sb.append("D->A: ").append(getCampo(TRAN_DA)).append("\n");
        sb.append("A->D: ").append(getCampo(TRAN_AD)).append("\n");
        sb.append("ABP Of: ").append(getCampo(ABP_OFE)).append("\n");
        sb.append("ABP Def: ").append(getCampo(ABP_DEF)).append("\n");
        sb.append("Obs: ").append(getCampo(OBS_ABP)).append("\n\n");
        sb.append("--- FORTALEZAS Y DEBILIDADES ---\n");
        sb.append("Fortalezas: ").append(getCampo(FORTALEZAS)).append("\n");
        sb.append("Debilidades: ").append(getCampo(DEBILIDADES)).append("\n\n");
        sb.append("--- JUGADORES CLAVE ---\n");
        sb.append("Mas peligroso: ").append(getCampo(JUGADOR_PELIGROSO)).append("\n");
        sb.append("Eslabon debil: ").append(getCampo(ESLABON_DEBIL)).append("\n");
        return sb.toString();
    }
}
