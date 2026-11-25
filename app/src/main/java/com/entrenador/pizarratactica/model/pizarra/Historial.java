package com.entrenador.pizarratactica.model.pizarra;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Deshacer / rehacer basado en instantaneas del ejercicio.
 * Antes de cada cambio se guarda una copia del estado anterior.
 */
public class Historial {

    private static final int MAX_PASOS = 60;

    private final Deque<Ejercicio> deshacer = new ArrayDeque<>();
    private final Deque<Ejercicio> rehacer = new ArrayDeque<>();

    /** Guardar el estado ANTES de modificar el ejercicio. */
    public void registrar(Ejercicio estadoAnterior) {
        deshacer.push(estadoAnterior.copiar());
        if (deshacer.size() > MAX_PASOS) deshacer.removeLast();
        rehacer.clear();
    }

    /** Devuelve true si se ha deshecho algo; el ejercicio queda modificado. */
    public boolean deshacer(Ejercicio actual) {
        if (deshacer.isEmpty()) return false;
        rehacer.push(actual.copiar());
        actual.restaurarDesde(deshacer.pop());
        return true;
    }

    public boolean rehacer(Ejercicio actual) {
        if (rehacer.isEmpty()) return false;
        deshacer.push(actual.copiar());
        actual.restaurarDesde(rehacer.pop());
        return true;
    }

    public boolean puedeDeshacer() { return !deshacer.isEmpty(); }
    public boolean puedeRehacer() { return !rehacer.isEmpty(); }

    public void limpiar() {
        deshacer.clear();
        rehacer.clear();
    }
}
