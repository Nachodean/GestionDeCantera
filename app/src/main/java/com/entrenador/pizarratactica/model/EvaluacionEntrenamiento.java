package com.entrenador.pizarratactica.model;

/**
 * Evaluación de un jugador en una sesión de entrenamiento.
 * La puntuación va de 1 (mal) a 5 (excelente).
 */
public class EvaluacionEntrenamiento {

    private String id;
    private String jugadorId;
    private String jugadorNombre;
    private String fecha;
    private int puntuacion;         // 1–5 estrellas
    private String observaciones;   // Notas opcionales del entrenador

    public EvaluacionEntrenamiento() {}

    public EvaluacionEntrenamiento(String jugadorId, String jugadorNombre,
                                   String fecha, int puntuacion,
                                   String observaciones) {
        this.jugadorId = jugadorId;
        this.jugadorNombre = jugadorNombre;
        this.fecha = fecha;
        this.puntuacion = puntuacion;
        this.observaciones = observaciones;
    }

    // ── Getters ──────────────────────────────────────────────────────

    public String getId() { return id; }
    public String getJugadorId() { return jugadorId; }
    public String getJugadorNombre() { return jugadorNombre; }
    public String getFecha() { return fecha; }
    public int getPuntuacion() { return puntuacion; }
    public String getObservaciones() { return observaciones; }

    /** Devuelve estrellas visuales: ★★★☆☆ */
    public String getEstrellas() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            sb.append(i <= puntuacion ? "★" : "☆");
        }
        return sb.toString();
    }

    // ── Setters ──────────────────────────────────────────────────────

    public void setId(String id) { this.id = id; }
    public void setJugadorId(String jugadorId) { this.jugadorId = jugadorId; }
    public void setJugadorNombre(String jugadorNombre) { this.jugadorNombre = jugadorNombre; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setPuntuacion(int puntuacion) { this.puntuacion = Math.max(1, Math.min(5, puntuacion)); }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
