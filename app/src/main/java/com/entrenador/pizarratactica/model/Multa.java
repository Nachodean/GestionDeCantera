package com.entrenador.pizarratactica.model;

/**
 * Modelo que representa una multa o sanción a un jugador.
 */
public class Multa {

    private String id;
    private String jugadorId;
    private String jugadorNombre;
    private String fecha;
    private String motivo;
    private double cantidad;    // Euros
    private boolean pagada;

    public Multa() {}

    public Multa(String jugadorId, String jugadorNombre,
                 String fecha, String motivo, double cantidad) {
        this.jugadorId = jugadorId;
        this.jugadorNombre = jugadorNombre;
        this.fecha = fecha;
        this.motivo = motivo;
        this.cantidad = cantidad;
        this.pagada = false;
    }

    public Multa(String jugadorId, String jugadorNombre,
                 String fecha, String motivo, double cantidad, boolean pagada) {
        this.jugadorId = jugadorId;
        this.jugadorNombre = jugadorNombre;
        this.fecha = fecha;
        this.motivo = motivo;
        this.cantidad = cantidad;
        this.pagada = pagada;
    }

    // ── Getters ──────────────────────────────────────────────────────

    public String getId() { return id; }
    public String getJugadorId() { return jugadorId; }
    public String getJugadorNombre() { return jugadorNombre; }
    public String getFecha() { return fecha; }
    public String getMotivo() { return motivo; }
    public double getCantidad() { return cantidad; }
    public boolean isPagada() { return pagada; }

    /** Resumen para listas: "2.00€ — Llegar tarde" */
    public String getResumen() {
        return String.format("%.2f€ — %s", cantidad, motivo);
    }

    // ── Setters ──────────────────────────────────────────────────────

    public void setId(String id) { this.id = id; }
    public void setJugadorId(String jugadorId) { this.jugadorId = jugadorId; }
    public void setJugadorNombre(String jugadorNombre) { this.jugadorNombre = jugadorNombre; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }
    public void setPagada(boolean pagada) { this.pagada = pagada; }
}
