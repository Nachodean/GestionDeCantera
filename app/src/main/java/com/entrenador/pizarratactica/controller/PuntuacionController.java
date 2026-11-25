package com.entrenador.pizarratactica.controller;

import com.entrenador.pizarratactica.model.EvaluacionEntrenamiento;
import com.entrenador.pizarratactica.model.Jugador;
import com.entrenador.pizarratactica.model.JugadorRepository;
import com.entrenador.pizarratactica.view.PuntuacionFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador de la seccion Puntuacion: guarda la nota (1-5 estrellas)
 * y las observaciones de cada jugador en un entrenamiento.
 */
public class PuntuacionController {

    private static final int PUNTUACION_INICIAL = 3;

    private final PuntuacionFragment vista;
    private final JugadorRepository repositorio;
    private final List<Jugador> jugadores = new ArrayList<>();
    private final List<Integer> puntuaciones = new ArrayList<>();
    private final List<String> observaciones = new ArrayList<>();

    public PuntuacionController(PuntuacionFragment vista) {
        this.vista = vista;
        this.repositorio = new JugadorRepository(vista.requireContext());
    }

    public void cargarJugadores() {
        repositorio.listarJugadores(new JugadorRepository.ListaJugadoresCallback() {
            @Override
            public void onJugadoresObtenidos(List<Jugador> lista) {
                jugadores.clear();
                puntuaciones.clear();
                observaciones.clear();
                jugadores.addAll(lista);
                for (int i = 0; i < lista.size(); i++) {
                    puntuaciones.add(PUNTUACION_INICIAL);
                    observaciones.add("");
                }
                vista.pintarEvaluaciones();
            }

            @Override
            public void onError(String error) {
                vista.mostrarMensaje(error);
            }
        });
    }

    // ── Datos para el adaptador ──────────────────────────────────────

    public int numJugadores() { return jugadores.size(); }
    public Jugador getJugador(int i) { return jugadores.get(i); }
    public int getPuntuacion(int i) { return puntuaciones.get(i); }
    public String getObservacion(int i) { return observaciones.get(i); }

    public void cambiarPuntuacion(int i, int valor) {
        if (i >= 0 && i < puntuaciones.size()) puntuaciones.set(i, valor);
    }

    public void cambiarObservacion(int i, String texto) {
        if (i >= 0 && i < observaciones.size()) observaciones.set(i, texto);
    }

    // ── Guardar ──────────────────────────────────────────────────────

    public void guardarEvaluaciones(String fecha) {
        if (fecha.isEmpty()) {
            vista.mostrarMensaje("Introduce la fecha.");
            return;
        }
        for (int i = 0; i < jugadores.size(); i++) {
            Jugador j = jugadores.get(i);
            EvaluacionEntrenamiento eval = new EvaluacionEntrenamiento(
                    j.getId(), j.getNombreCompleto(), fecha, puntuaciones.get(i), observaciones.get(i));
            repositorio.guardarEvaluacion(eval, new JugadorRepository.ResultadoCallback() {
                @Override public void onExito(String mensaje) { }
                @Override public void onError(String error) { vista.mostrarMensaje(error); }
            });
        }
        vista.mostrarMensaje(jugadores.size() + " evaluaciones guardadas.");
    }
}
