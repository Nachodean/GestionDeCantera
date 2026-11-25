package com.entrenador.pizarratactica.model.pizarra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Sesion de entrenamiento: datos generales + lista ordenada de ejercicios.
 * Siempre tiene al menos un ejercicio.
 */
public class Sesion {

    private String id;
    private String numero = "";
    private String fecha = "";
    private String objetivo = "";
    private long creada;
    private long modificada;
    private final List<Ejercicio> ejercicios = new ArrayList<>();

    /** Crea una sesion nueva con un ejercicio vacio. */
    public static Sesion nueva(long ahora) {
        Sesion s = new Sesion(UUID.randomUUID().toString());
        s.creada = ahora;
        s.modificada = ahora;
        s.agregarEjercicio();
        return s;
    }

    public Sesion(String id) {
        this.id = id;
    }

    // ── Ejercicios ───────────────────────────────────────────────────

    public Ejercicio agregarEjercicio() {
        Ejercicio e = new Ejercicio();
        e.setTitulo("Ejercicio " + (ejercicios.size() + 1));
        ejercicios.add(e);
        return e;
    }

    public void agregarEjercicioExistente(Ejercicio e) {
        ejercicios.add(e);
    }

    /** Duplica el ejercicio y coloca la copia justo detras. */
    public Ejercicio duplicarEjercicio(int indice) {
        if (indice < 0 || indice >= ejercicios.size()) return null;
        Ejercicio copia = ejercicios.get(indice).duplicar();
        copia.setTitulo(copia.getTitulo() + " (copia)");
        ejercicios.add(indice + 1, copia);
        return copia;
    }

    /** Elimina un ejercicio. No deja la sesion sin ejercicios. */
    public boolean eliminarEjercicio(int indice) {
        if (ejercicios.size() <= 1 || indice < 0 || indice >= ejercicios.size()) return false;
        ejercicios.remove(indice);
        return true;
    }

    public boolean moverEjercicio(int desde, int hasta) {
        if (desde < 0 || desde >= ejercicios.size() || hasta < 0 || hasta >= ejercicios.size()) {
            return false;
        }
        Ejercicio e = ejercicios.remove(desde);
        ejercicios.add(hasta, e);
        return true;
    }

    public int duracionTotal() {
        int total = 0;
        for (Ejercicio e : ejercicios) total += e.getDuracionMin();
        return total;
    }

    /** Copia completa con id nuevo (boton "Duplicar sesion"). */
    public Sesion duplicar(long ahora) {
        Sesion c = new Sesion(UUID.randomUUID().toString());
        c.numero = numero;
        c.fecha = fecha;
        c.objetivo = objetivo;
        c.creada = ahora;
        c.modificada = ahora;
        for (Ejercicio e : ejercicios) c.ejercicios.add(e.duplicar());
        return c;
    }

    /** Titulo legible para listas y PDF. */
    public String getTituloVisible() {
        String n = numero == null || numero.trim().isEmpty() ? "sin número" : "Nº " + numero.trim();
        return "Sesión " + n;
    }

    // ── Getters / setters ────────────────────────────────────────────

    public String getId() { return id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero == null ? "" : numero; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha == null ? "" : fecha; }
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String o) { this.objetivo = o == null ? "" : o; }
    public long getCreada() { return creada; }
    public void setCreada(long creada) { this.creada = creada; }
    public long getModificada() { return modificada; }
    public void setModificada(long modificada) { this.modificada = modificada; }
    public List<Ejercicio> getEjercicios() { return Collections.unmodifiableList(ejercicios); }
    public Ejercicio getEjercicio(int i) { return ejercicios.get(i); }
    public int numEjercicios() { return ejercicios.size(); }
}
