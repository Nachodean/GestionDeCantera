package com.entrenador.pizarratactica.controller;

import com.entrenador.pizarratactica.model.Jugador;
import com.entrenador.pizarratactica.model.JugadorRepository;
import com.entrenador.pizarratactica.model.Multa;
import com.entrenador.pizarratactica.model.TipoMulta;
import com.entrenador.pizarratactica.view.MultasFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Controlador de la seccion Multas: alta de sanciones, pagos y totales del bote.
 */
public class MultasController {

    private final MultasFragment vista;
    private final JugadorRepository repositorio;
    private final List<Jugador> jugadores = new ArrayList<>();
    private final List<Multa> multas = new ArrayList<>();
    private final List<TipoMulta> catalogo = TipoMulta.catalogo();

    public MultasController(MultasFragment vista) {
        this.vista = vista;
        this.repositorio = new JugadorRepository(vista.requireContext());
    }

    /** Carga jugadores (para el formulario) y multas. */
    public void cargarDatos() {
        repositorio.listarJugadores(new JugadorRepository.ListaJugadoresCallback() {
            @Override
            public void onJugadoresObtenidos(List<Jugador> lista) {
                jugadores.clear();
                jugadores.addAll(lista);
            }

            @Override
            public void onError(String error) {
                vista.mostrarMensaje(error);
            }
        });
        cargarMultas();
    }

    private void cargarMultas() {
        repositorio.listarMultas(new JugadorRepository.ListaMultasCallback() {
            @Override
            public void onMultasObtenidas(List<Multa> lista) {
                multas.clear();
                multas.addAll(lista);
                vista.pintarMultas(multas);
                actualizarTotales();
            }

            @Override
            public void onError(String error) {
                vista.mostrarMensaje(error);
            }
        });
    }

    private void actualizarTotales() {
        double[] totales = TipoMulta.calcularTotales(multas);
        vista.pintarTotales(totales[0], totales[1]);
    }

    /** Boton "+": abre el formulario si hay jugadores. */
    public void alPulsarAgregar() {
        if (jugadores.isEmpty()) {
            vista.mostrarMensaje("Primero debes añadir jugadores en la Plantilla.");
            return;
        }
        List<String> nombres = new ArrayList<>();
        for (Jugador j : jugadores) nombres.add(j.getNombreCompleto());
        String hoy = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        vista.mostrarDialogoAgregar(nombres, catalogo, hoy);
    }

    public void agregarMulta(int indiceJugador, int indiceTipo, String fecha, boolean pagada) {
        if (fecha.isEmpty()) {
            vista.mostrarMensaje("Introduce la fecha.");
            return;
        }
        if (indiceJugador < 0 || indiceJugador >= jugadores.size()
                || indiceTipo < 0 || indiceTipo >= catalogo.size()) return;

        Jugador j = jugadores.get(indiceJugador);
        TipoMulta tipo = catalogo.get(indiceTipo);
        Multa nueva = new Multa(j.getId(), j.getNombreCompleto(), fecha,
                tipo.getDescripcion(), tipo.getCantidad(), pagada);

        repositorio.agregarMulta(nueva, new JugadorRepository.ResultadoCallback() {
            @Override
            public void onExito(String mensaje) {
                cargarMultas();
                vista.mostrarMensaje(mensaje);
            }

            @Override
            public void onError(String error) {
                vista.mostrarMensaje(error);
            }
        });
    }

    /** Casilla "pagada" de una multa de la lista. */
    public void cambiarPagada(Multa multa, boolean pagada) {
        multa.setPagada(pagada);
        repositorio.marcarMultaPagada(multa.getId(), pagada, new JugadorRepository.ResultadoCallback() {
            @Override
            public void onExito(String mensaje) {
                actualizarTotales();
            }

            @Override
            public void onError(String error) {
                vista.mostrarMensaje(error);
            }
        });
    }
}
