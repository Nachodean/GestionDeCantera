package com.entrenador.pizarratactica.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.entrenador.pizarratactica.R;
import com.entrenador.pizarratactica.adapter.JugadoresAdapter;
import com.entrenador.pizarratactica.controller.PlantillaController;
import com.entrenador.pizarratactica.model.Jugador;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Vista de la seccion Plantilla: lista de jugadores y formulario de alta.
 * La logica esta en {@link PlantillaController}.
 */
public class PlantillaFragment extends Fragment {

    // Elementos de la interfaz (UI)
    private RecyclerView rvJugadores;
    private TextView tvVacio;
    private FloatingActionButton fabAgregar;

    // Adaptador y controlador (MVC)
    private JugadoresAdapter adaptador;
    private PlantillaController controlador;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_jugadores, container, false);
        recogerElementos(root);
        establecerEscuchadores();
        controlador.cargarJugadores();
        return root;
    }

    private void recogerElementos(View root) {
        rvJugadores = root.findViewById(R.id.rv_jugadores);
        tvVacio = root.findViewById(R.id.tv_vacio_jugadores);
        fabAgregar = root.findViewById(R.id.fab_agregar_jugador);
    }

    private void establecerEscuchadores() {
        controlador = new PlantillaController(this);
        adaptador = new JugadoresAdapter(controlador);
        rvJugadores.setLayoutManager(new LinearLayoutManager(getContext()));
        rvJugadores.setAdapter(adaptador);
        fabAgregar.setOnClickListener(v -> controlador.alPulsarAgregar());
    }

    // --- MÉTODOS PÚBLICOS PARA COMUNICACIÓN CON EL CONTROLADOR ---

    public void pintarJugadores(List<Jugador> jugadores) {
        adaptador.actualizar(jugadores);
        boolean vacio = jugadores.isEmpty();
        rvJugadores.setVisibility(vacio ? View.GONE : View.VISIBLE);
        tvVacio.setVisibility(vacio ? View.VISIBLE : View.GONE);
    }

    public void mostrarDialogoAgregarJugador(Jugador.Posicion[] posiciones) {
        if (getContext() == null) return;
        View dialogo = LayoutInflater.from(getContext()).inflate(R.layout.dialog_agregar_jugador, null);
        EditText etNombre = dialogo.findViewById(R.id.et_nombre);
        EditText etApellidos = dialogo.findViewById(R.id.et_apellidos);
        EditText etDorsal = dialogo.findViewById(R.id.et_dorsal);
        Spinner spPosicion = dialogo.findViewById(R.id.sp_posicion);

        String[] nombres = new String[posiciones.length];
        for (int i = 0; i < posiciones.length; i++) nombres[i] = posiciones[i].getNombre();
        spPosicion.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, nombres));

        new AlertDialog.Builder(getContext())
                .setTitle("Añadir Jugador")
                .setView(dialogo)
                .setPositiveButton("Añadir", (d, w) -> controlador.agregarJugador(
                        etNombre.getText().toString().trim(),
                        etApellidos.getText().toString().trim(),
                        etDorsal.getText().toString().trim(),
                        spPosicion.getSelectedItemPosition()))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void confirmarEliminarJugador(Jugador jugador) {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar jugador")
                .setMessage("¿Eliminar a " + jugador.getNombreCompleto() + "?")
                .setPositiveButton("Sí", (d, w) -> controlador.eliminarJugador(jugador))
                .setNegativeButton("No", null)
                .show();
    }

    public void mostrarMensaje(String mensaje) {
        if (getContext() != null) Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
    }
}
