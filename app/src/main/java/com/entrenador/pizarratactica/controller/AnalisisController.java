package com.entrenador.pizarratactica.controller;

import com.entrenador.pizarratactica.model.InformeRival;
import com.entrenador.pizarratactica.model.InformeRivalRepository;
import com.entrenador.pizarratactica.view.AnalisisFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Controlador del Analisis Rival: guarda informes y muestra los ya guardados.
 */
public class AnalisisController {

    private final AnalisisFragment vista;
    private final InformeRivalRepository repositorio;
    private List<InformeRival> informesListados;

    public AnalisisController(AnalisisFragment vista) {
        this.vista = vista;
        this.repositorio = new InformeRivalRepository(vista.requireContext());
    }

    /** Boton Guardar: primero se pide el nombre del rival. */
    public void alPulsarGuardar() {
        vista.pedirNombreRival();
    }

    public void guardarInforme(String rival) {
        if (rival == null || rival.trim().isEmpty()) rival = "Rival desconocido";
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        InformeRival informe = new InformeRival("informe_" + System.currentTimeMillis(), rival.trim(), fecha);
        for (Map.Entry<String, String> campo : vista.recogerDatosFormulario().entrySet()) {
            informe.setCampo(campo.getKey(), campo.getValue());
        }
        if (repositorio.guardar(informe)) {
            vista.mostrarMensaje("Informe de " + informe.getRival() + " guardado");
        } else {
            vista.mostrarMensaje("Error al guardar el informe");
        }
    }

    /** Boton Ver informes: lista con el mas reciente primero. */
    public void alPulsarVerInformes() {
        informesListados = repositorio.listar();
        if (informesListados.isEmpty()) {
            vista.mostrarMensaje("No hay informes guardados aun");
            return;
        }
        String[] titulos = new String[informesListados.size()];
        for (int i = 0; i < informesListados.size(); i++) {
            InformeRival inf = informesListados.get(i);
            titulos[i] = inf.getRival() + "  —  " + inf.getFecha();
        }
        vista.mostrarListaInformes(titulos);
    }

    public void abrirInforme(int indice) {
        if (informesListados == null || indice < 0 || indice >= informesListados.size()) return;
        InformeRival inf = informesListados.get(indice);
        vista.mostrarDetalleInforme("Informe: " + inf.getRival(), inf.resumen());
    }
}
