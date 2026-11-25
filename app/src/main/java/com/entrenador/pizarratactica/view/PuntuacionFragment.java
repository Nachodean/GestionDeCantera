package com.entrenador.pizarratactica.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.entrenador.pizarratactica.R;
import com.entrenador.pizarratactica.adapter.EvaluacionesAdapter;
import com.entrenador.pizarratactica.controller.PuntuacionController;

/**
 * Vista de la seccion Puntuacion: valoracion de cada jugador en un entrenamiento.
 * La logica esta en {@link PuntuacionController}.
 */
public class PuntuacionFragment extends Fragment {

    // Elementos de la interfaz (UI)
    private EditText etFechaEval;
    private RecyclerView rvEvaluaciones;
    private Button btnGuardar;

    // Adaptador y controlador (MVC)
    private EvaluacionesAdapter adaptador;
    private PuntuacionController controlador;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_evaluacion, container, false);
        recogerElementos(root);
        establecerEscuchadores();
        controlador.cargarJugadores();
        return root;
    }

    private void recogerElementos(View root) {
        etFechaEval = root.findViewById(R.id.et_fecha_eval);
        rvEvaluaciones = root.findViewById(R.id.rv_evaluaciones);
        btnGuardar = root.findViewById(R.id.btn_guardar_evaluaciones);
    }

    private void establecerEscuchadores() {
        controlador = new PuntuacionController(this);
        adaptador = new EvaluacionesAdapter(controlador);
        rvEvaluaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        rvEvaluaciones.setAdapter(adaptador);
        btnGuardar.setOnClickListener(v -> {
            // Quitar el foco para que se guarde la observacion que se estaba escribiendo.
            View enFoco = rvEvaluaciones.findFocus();
            if (enFoco != null) enFoco.clearFocus();
            controlador.guardarEvaluaciones(etFechaEval.getText().toString().trim());
        });
    }

    // --- MÉTODOS PÚBLICOS PARA COMUNICACIÓN CON EL CONTROLADOR ---

    public void pintarEvaluaciones() {
        adaptador.notifyDataSetChanged();
    }

    public void mostrarMensaje(String mensaje) {
        if (getContext() != null) Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
    }
}
