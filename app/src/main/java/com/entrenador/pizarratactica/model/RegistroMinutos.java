package com.entrenador.pizarratactica.model;

/**
 * Registro de minutos jugados por un jugador en una jornada concreta.
 */
public class RegistroMinutos {

    private String id;
    private String jugadorId;
    private String jugadorNombre;   // Desnormalizado para mostrar sin join
    private int jornada;
    private String rival;
    private String fecha;
    private int minutosJugados;     // 0–120 (incluye prórroga)

    public RegistroMinutos() {}

    public RegistroMinutos(String jugadorId, String jugadorNombre,
                           int jornada, String rival, String fecha,
                           int minutosJugados) {
        this.jugadorId = jugadorId;
        this.jugadorNombre = jugadorNombre;
        this.jornada = jornada;
        this.rival = rival;
        this.fecha = fecha;
        this.minutosJugados = minutosJugados;
    }

    // ── Getters ──────────────────────────────────────────────────────

    public String getId() { return id; }
    public String getJugadorId() { return jugadorId; }
    public String getJugadorNombre() { return jugadorNombre; }
    public int getJornada() { return jornada; }
    public String getRival() { return rival; }
    public String getFecha() { return fecha; }
    public int getMinutosJugados() { return minutosJugados; }

    // ── Setters ──────────────────────────────────────────────────────

    public void setId(String id) { this.id = id; }
    public void setJugadorId(String jugadorId) { this.jugadorId = jugadorId; }
    public void setJugadorNombre(String jugadorNombre) { this.jugadorNombre = jugadorNombre; }
    public void setJornada(int jornada) { this.jornada = jornada; }
    public void setRival(String rival) { this.rival = rival; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setMinutosJugados(int minutosJugados) { this.minutosJugados = minutosJugados; }
}
