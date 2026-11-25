package com.entrenador.pizarratactica.model;

/**
 * Modelo que representa a un jugador del equipo cadete.
 */
public class Jugador {

    public enum Posicion {
        POR("Portero"), DEF("Defensa"), MED("Centrocampista"), DEL("Delantero");

        private final String nombre;

        Posicion(String nombre) {
            this.nombre = nombre;
        }

        public String getNombre() {
            return nombre;
        }
    }

    private String id;          // Clave Firebase (push key)
    private String nombre;
    private String apellidos;
    private int dorsal;
    private Posicion posicion;
    private boolean activo;

    /** Constructor vacío requerido para deserialización de Firebase. */
    public Jugador() {
        this.activo = true;
    }

    public Jugador(String nombre, String apellidos, int dorsal, Posicion posicion) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dorsal = dorsal;
        this.posicion = posicion;
        this.activo = true;
    }

    // ── Getters ──────────────────────────────────────────────────────

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public int getDorsal() { return dorsal; }
    public Posicion getPosicion() { return posicion; }
    public boolean isActivo() { return activo; }

    /** Nombre completo con dorsal para mostrar en listas. */
    public String getNombreCompleto() {
        return "#" + dorsal + " " + nombre + " " + apellidos;
    }

    // ── Setters ──────────────────────────────────────────────────────

    public void setId(String id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setDorsal(int dorsal) { this.dorsal = dorsal; }
    public void setPosicion(Posicion posicion) { this.posicion = posicion; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return getNombreCompleto();
    }
}
