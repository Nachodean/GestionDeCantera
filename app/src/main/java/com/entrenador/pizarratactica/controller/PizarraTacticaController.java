package com.entrenador.pizarratactica.controller;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.entrenador.pizarratactica.R;
import com.entrenador.pizarratactica.view.AnalisisFragment;
import com.entrenador.pizarratactica.view.MultasFragment;
import com.entrenador.pizarratactica.view.PizarraTacticaActivity;
import com.entrenador.pizarratactica.view.PlantillaFragment;
import com.entrenador.pizarratactica.view.PreparadorFragment;
import com.entrenador.pizarratactica.view.PuntuacionFragment;
import com.entrenador.pizarratactica.view.SesionesFragment;
import com.google.android.material.navigation.NavigationView;

/**
 * Controlador de la pantalla principal: decide que seccion se muestra
 * al pulsar cada opcion del menu lateral.
 */
public class PizarraTacticaController implements NavigationView.OnNavigationItemSelectedListener {

    private final PizarraTacticaActivity vista;

    public PizarraTacticaController(PizarraTacticaActivity vista) {
        this.vista = vista;
    }

    /** Pantalla de inicio de la app. */
    public void iniciar() {
        vista.mostrarSeccion(new SesionesFragment(), "Sesiones");
        vista.marcarOpcionMenu(R.id.nav_sesiones);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragmento;
        String titulo;
        int id = item.getItemId();

        if (id == R.id.nav_sesiones) {
            fragmento = new SesionesFragment();
            titulo = "Sesiones";
        } else if (id == R.id.nav_plantilla) {
            fragmento = new PlantillaFragment();
            titulo = "Plantilla";
        } else if (id == R.id.nav_multas) {
            fragmento = new MultasFragment();
            titulo = "Multas";
        } else if (id == R.id.nav_puntuacion) {
            fragmento = new PuntuacionFragment();
            titulo = "Puntuación";
        } else if (id == R.id.nav_analisis) {
            fragmento = new AnalisisFragment();
            titulo = "Análisis Rival";
        } else if (id == R.id.nav_preparador) {
            fragmento = new PreparadorFragment();
            titulo = "Preparador Físico";
        } else {
            return false;
        }

        vista.mostrarSeccion(fragmento, titulo);
        vista.cerrarMenu();
        return true;
    }
}
