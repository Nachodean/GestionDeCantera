package com.entrenador.pizarratactica.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.entrenador.pizarratactica.model.SesionRepository;
import com.entrenador.pizarratactica.model.pizarra.Sesion;
import com.entrenador.pizarratactica.view.SesionesFragment;
import com.entrenador.pizarratactica.view.pizarra.ExportadorSesion;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controlador de la lista de sesiones: abrir, crear, duplicar, eliminar y exportar a PDF.
 */
public class SesionesController {

    private static final String TAG = "SesionesController";

    private final SesionesFragment vista;
    private final SesionRepository repositorio;
    private final Context contextoApp;
    private final ExecutorService hilo = Executors.newSingleThreadExecutor();
    private final Handler principal = new Handler(Looper.getMainLooper());

    public SesionesController(SesionesFragment vista) {
        this.vista = vista;
        this.contextoApp = vista.requireContext().getApplicationContext();
        this.repositorio = new SesionRepository(contextoApp);
    }

    public void cargarSesiones() {
        vista.pintarSesiones(repositorio.listar());
    }

    public void nuevaSesion() {
        vista.abrirEditor(null);
    }

    public void abrirSesion(Sesion sesion) {
        vista.abrirEditor(sesion.getId());
    }

    public void duplicarSesion(Sesion sesion) {
        if (repositorio.duplicar(sesion.getId()) != null) {
            vista.mostrarMensaje("Sesión duplicada");
            cargarSesiones();
        } else {
            vista.mostrarMensaje("No se pudo duplicar la sesión");
        }
    }

    /** Boton Eliminar: la vista pide confirmacion. */
    public void alPulsarEliminar(Sesion sesion) {
        vista.confirmarEliminarSesion(sesion);
    }

    public void eliminarSesion(Sesion sesion) {
        repositorio.eliminar(sesion.getId());
        cargarSesiones();
    }

    /** Genera el PDF en segundo plano y lo comparte. */
    public void exportarPdf(Sesion sesion) {
        vista.mostrarMensaje("Generando PDF…");
        hilo.execute(() -> {
            try {
                File archivo = ExportadorSesion.exportarPdf(contextoApp, sesion);
                principal.post(() -> vista.compartirArchivo(archivo, "application/pdf"));
            } catch (Exception e) {
                Log.e(TAG, "Error exportando PDF", e);
                principal.post(() -> vista.mostrarMensaje("No se pudo crear el PDF"));
            }
        });
    }

    /** Liberar el hilo al cerrar la pantalla. */
    public void liberar() {
        hilo.shutdown();
    }
}
