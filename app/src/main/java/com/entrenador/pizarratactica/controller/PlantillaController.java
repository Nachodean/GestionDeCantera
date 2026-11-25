package com.entrenador.pizarratactica.controller;

import com.entrenador.pizarratactica.model.Jugador;
import com.entrenador.pizarratactica.model.JugadorRepository;
import com.entrenador.pizarratactica.view.PlantillaFragment;

import java.util.List;

/**
 * Controlador de la seccion Plantilla: carga, alta y baja de jugadores.
 */
public class PlantillaController {

    private final PlantillaFragment vista;
    private final JugadorRepository repositorio;

    public PlantillaController(PlantillaFragment vista) {
        this.vista = vista;
        this.repositorio = new JugadorRepository(vista.requireContext());
    }

    public void cargarJugadores() {
        repositorio.listarJugadores(new JugadorRepository.ListaJugadoresCallback() {
            @Override
            public void onJugadoresObtenidos(List<Jugador> jugadores) {
                vista.pintarJugadores(jugadores);
            }

            @Override
            public void onError(String error) {
                vista.mostrarMensaje(error);
            }
        });
    }

    /** Boton "+": la vista abre el formulario. */
    public void alPulsarAgregar() {
        vista.mostrarDialogoAgregarJugador(Jugador.Posicion.values());
    }

    /** Valida los datos del formulario y guarda el jugador. */
    public void agregarJugador(String nombre, String apellidos, String dorsalTexto, int indicePosicion) {
        if (nombre.isEmpty() || dorsalTexto.isEmpty()) {
            vista.mostrarMensaje("Rellena nombre y dorsal.");
            return;
        }
        int dorsal;
        try {
            dorsal = Integer.parseInt(dorsalTexto);
        } catch (NumberFormatException e) {
            vista.mostrarMensaje("El dorsal debe ser un número.");
            return;
        }
        Jugador.Posicion[] posiciones = Jugador.Posicion.values();
        Jugador.Posicion pos = posiciones[Math.max(0, Math.min(indicePosicion, posiciones.length - 1))];
        Jugador nuevo = new Jugador(nombre, apellidos, dorsal, pos);

        repositorio.agregarJugador(nuevo, new JugadorRepository.ResultadoCallback() {
            @Override
            public void onExito(String mensaje) {
                cargarJugadores();
                vista.mostrarMensaje(mensaje);
            }

            @Override
            public void onError(String error) {
                vista.mostrarMensaje(error);
            }
        });
    }

    /** Boton papelera de un jugador: la vista pide confirmacion. */
    public void alPulsarEliminar(Jugador jugador) {
        vista.confirmarEliminarJugador(jugador);
    }

    public void eliminarJugador(Jugador jugador) {
        repositorio.eliminarJugador(jugador.getId(), new JugadorRepository.ResultadoCallback() {
            @Override
            public void onExito(String mensaje) {
                cargarJugadores();
            }

            @Override
            public void onError(String error) {
                vista.mostrarMensaje(error);
            }
        });
    }
}
