package com.entrenador.pizarratactica.view;

import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.entrenador.pizarratactica.R;
import com.entrenador.pizarratactica.controller.PizarraTacticaController;
import com.google.android.material.navigation.NavigationView;

/**
 * Vista principal de la app: barra superior, menu lateral y contenedor
 * donde se muestran las secciones (Sesiones, Plantilla, Multas...).
 * La navegacion la decide {@link PizarraTacticaController}.
 */
public class PizarraTacticaActivity extends AppCompatActivity {

    // Elementos de la interfaz (UI)
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navView;

    // Controlador (MVC)
    private PizarraTacticaController controlador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizarra_tactica);

        // 1. Enlazamos los componentes del XML con Java
        recogerElementos();

        // 2. Creamos el controlador y conectamos los escuchadores
        establecerEscuchadores();

        // 3. Primera pantalla: la lista de sesiones
        if (savedInstanceState == null) {
            controlador.iniciar();
        }
    }

    /** Busca los elementos del layout por su ID. */
    private void recogerElementos() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
    }

    /** Crea el controlador y le asigna los eventos del menu lateral. */
    private void establecerEscuchadores() {
        controlador = new PizarraTacticaController(this);
        navView.setNavigationItemSelectedListener(controlador);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_abrir, R.string.nav_cerrar);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // --- MÉTODOS PÚBLICOS PARA COMUNICACIÓN CON EL CONTROLADOR ---

    /** Sustituye la seccion visible por {@code fragmento} y cambia el titulo. */
    public void mostrarSeccion(Fragment fragmento, String titulo) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragmento)
                .commit();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titulo);
        }
    }

    /** Marca en el menu lateral la seccion activa. */
    public void marcarOpcionMenu(int idOpcion) {
        navView.setCheckedItem(idOpcion);
    }

    public void cerrarMenu() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    public boolean menuAbierto() {
        return drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        if (menuAbierto()) {
            cerrarMenu();
        } else {
            super.onBackPressed();
        }
    }
}
