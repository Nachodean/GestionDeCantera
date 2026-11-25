package com.entrenador.pizarratactica.controller;

import com.entrenador.pizarratactica.R;
import com.entrenador.pizarratactica.model.EjercicioFisico;
import com.entrenador.pizarratactica.view.PreparadorFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador del Preparador Fisico: ejercicios agrupados por objetivo
 * (fuerza tren inferior, velocidad, resistencia y core/prevencion).
 */
public class PreparadorController {

    private final PreparadorFragment vista;

    public PreparadorController(PreparadorFragment vista) {
        this.vista = vista;
    }

    public void cargarEjercicios() {
        vista.pintarCategorias(fuerza(), velocidad(), resistencia(), core());
    }

    /** Pulsar una tarjeta abre su detalle. */
    public void alPulsarEjercicio(EjercicioFisico ejercicio) {
        vista.mostrarDetalleEjercicio(ejercicio);
    }

    // ── Catalogo ─────────────────────────────────────────────────────

    private List<EjercicioFisico> fuerza() {
        List<EjercicioFisico> l = new ArrayList<>();
        l.add(new EjercicioFisico("Sentadilla Bulgara", "Fortalecimiento unilateral de cuadriceps y gluteo.", "Pesas o peso corporal", R.drawable.ej_fuerza, false));
        l.add(new EjercicioFisico("Saltos Pliometricos", "Potencia y reactividad en cajon.", "Cajon", R.drawable.ej_pliometria, false));
        l.add(new EjercicioFisico("Isometrico en Pared", "Trabajo del tendon rotuliano sin impacto.", "Ninguno", R.drawable.ej_isometricos, true));
        l.add(new EjercicioFisico("Elevacion de Gemelos", "Fortalecimiento de la cadena posterior.", "Ninguno", R.drawable.ej_fuerza, true));
        return l;
    }

    private List<EjercicioFisico> velocidad() {
        List<EjercicioFisico> l = new ArrayList<>();
        l.add(new EjercicioFisico("Agilidad en escalera", "Footwork rapido y coordinacion antes de arrancar.", "Escalera", R.drawable.ej_escalera, false));
        l.add(new EjercicioFisico("Sprint Resistido", "Potencia de arrancada arrastrando resistencia.", "Gomas elasticas", R.drawable.ej_resistencia, false));
        l.add(new EjercicioFisico("Salidas desde el suelo", "Reaccion al pitido, sprint 10m sin fatiga.", "Ninguno", R.drawable.ej_velocidad, true));
        l.add(new EjercicioFisico("Multi-saltos con vallas", "Pliometria con obstaculos minimizando contacto.", "Vallas", R.drawable.ej_vallas, false));
        return l;
    }

    private List<EjercicioFisico> resistencia() {
        List<EjercicioFisico> l = new ArrayList<>();
        l.add(new EjercicioFisico("Suicidios / Linea a linea", "Trabajo anaerobico lactico puro, frenadas y sprints.", "Ninguno", R.drawable.ej_anaerobicos, true));
        l.add(new EjercicioFisico("Rondos de alta presion", "Compensacion de carga cardiovascular e intensidad.", "Ninguno", R.drawable.ej_anaerobicos, true));
        l.add(new EjercicioFisico("Circuit training aerobico", "Circuito continuo de mantenimiento sin picos.", "Ninguno", R.drawable.ej_pliometria, true));
        return l;
    }

    private List<EjercicioFisico> core() {
        List<EjercicioFisico> l = new ArrayList<>();
        l.add(new EjercicioFisico("Plancha Abdominal", "Activacion core y resistencia isometrica.", "Ninguno", R.drawable.ej_core, true));
        l.add(new EjercicioFisico("Giros con Balon Medicinal", "Fuerza rotacional para estabilizar caderas.", "Balon Medicinal", R.drawable.ej_balon_med, false));
        l.add(new EjercicioFisico("Liberacion Miofascial", "Masaje de descarga en rodillo sobre isquios y gemelos.", "Foam Roller", R.drawable.ej_fuerza, false));
        return l;
    }
}
