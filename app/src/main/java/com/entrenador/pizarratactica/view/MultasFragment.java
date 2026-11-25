package com.entrenador.pizarratactica.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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
import com.entrenador.pizarratactica.adapter.MultasAdapter;
import com.entrenador.pizarratactica.controller.MultasController;
import com.entrenador.pizarratactica.model.Multa;
import com.entrenador.pizarratactica.model.TipoMulta;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;

/**
 * Vista de la seccion Multas (codigo interno del equipo).
 * La logica esta en {@link MultasController}.
 */
public class MultasFragment extends Fragment {

    // Elementos de la interfaz (UI)
    private TextView tvTotalBote, tvTotalPendiente;
    private RecyclerView rvMultas;
    private FloatingActionButton fabAgregar;

    // Adaptador y controlador (MVC)
    private MultasAdapter adaptador;
    private MultasController controlador;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_multas, container, false);
        recogerElementos(root);
        establecerEscuchadores();
        controlador.cargarDatos();
        return root;
    }

    private void recogerElementos(View root) {
        tvTotalBote = root.findViewById(R.id.tv_total_bote);
        tvTotalPendiente = root.findViewById(R.id.tv_total_pendiente);
        rvMultas = root.findViewById(R.id.rv_multas);
        fabAgregar = root.findViewById(R.id.fab_agregar_multa);
    }

    private void establecerEscuchadores() {
        controlador = new MultasController(this);
        adaptador = new MultasAdapter(controlador);
        rvMultas.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMultas.setAdapter(adaptador);
        fabAgregar.setOnClickListener(v -> controlador.alPulsarAgregar());
    }

    // --- MÉTODOS PÚBLICOS PARA COMUNICACIÓN CON EL CONTROLADOR ---

    public void pintarMultas(List<Multa> multas) {
        adaptador.actualizar(multas);
    }

    public void pintarTotales(double bote, double pendiente) {
        tvTotalBote.setText(String.format(Locale.getDefault(), "Bote Recaudado: %.2f €", bote));
        tvTotalPendiente.setText(String.format(Locale.getDefault(), "Pendiente de pago: %.2f €", pendiente));
    }

    public void mostrarDialogoAgregar(List<String> jugadores, List<TipoMulta> tipos, String fechaHoy) {
        if (getContext() == null) return;
        View dialogo = LayoutInflater.from(getContext()).inflate(R.layout.dialog_agregar_multa, null);
        Spinner spJugador = dialogo.findViewById(R.id.sp_jugador_multa);
        Spinner spTipo = dialogo.findViewById(R.id.sp_tipo_multa);
        EditText etFecha = dialogo.findViewById(R.id.et_fecha_multa);
        CheckBox cbPagado = dialogo.findViewById(R.id.cb_pagado);

        spJugador.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, jugadores));
        spTipo.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, tipos));
        etFecha.setText(fechaHoy);

        new AlertDialog.Builder(getContext())
                .setTitle("Añadir Sanción")
                .setView(dialogo)
                .setPositiveButton("Guardar", (d, w) -> controlador.agregarMulta(
                        spJugador.getSelectedItemPosition(),
                        spTipo.getSelectedItemPosition(),
                        etFecha.getText().toString().trim(),
                        cbPagado.isChecked()))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void mostrarMensaje(String mensaje) {
        if (getContext() != null) Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
    }
}
