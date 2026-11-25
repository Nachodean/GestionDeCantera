package com.entrenador.pizarratactica.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.entrenador.pizarratactica.R;
import com.entrenador.pizarratactica.adapter.EjerciciosFisicosAdapter;
import com.entrenador.pizarratactica.controller.PreparadorController;
import com.entrenador.pizarratactica.model.EjercicioFisico;

import java.util.List;

/**
 * Vista del Preparador Fisico: un carrusel por objetivo.
 * Los ejercicios los proporciona {@link PreparadorController}.
 */
public class PreparadorFragment extends Fragment {

    // Elementos de la interfaz (UI)
    private RecyclerView rvFuerza, rvVelocidad, rvResistencia, rvCore;

    // Controlador (MVC)
    private PreparadorController controlador;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_preparador, container, false);
        recogerElementos(root);
        establecerEscuchadores();
        controlador.cargarEjercicios();
        return root;
    }

    private void recogerElementos(View root) {
        rvFuerza = root.findViewById(R.id.rv_fuerza);
        rvVelocidad = root.findViewById(R.id.rv_velocidad);
        rvResistencia = root.findViewById(R.id.rv_resistencia);
        rvCore = root.findViewById(R.id.rv_core);
    }

    private void establecerEscuchadores() {
        controlador = new PreparadorController(this);
    }

    // --- MÉTODOS PÚBLICOS PARA COMUNICACIÓN CON EL CONTROLADOR ---

    public void pintarCategorias(List<EjercicioFisico> fuerza, List<EjercicioFisico> velocidad,
                                 List<EjercicioFisico> resistencia, List<EjercicioFisico> core) {
        pintarCarrusel(rvFuerza, fuerza);
        pintarCarrusel(rvVelocidad, velocidad);
        pintarCarrusel(rvResistencia, resistencia);
        pintarCarrusel(rvCore, core);
    }

    private void pintarCarrusel(RecyclerView rv, List<EjercicioFisico> ejercicios) {
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(new EjerciciosFisicosAdapter(ejercicios, controlador));
    }

    public void mostrarDetalleEjercicio(EjercicioFisico ej) {
        if (getContext() == null) return;
        View dialogo = LayoutInflater.from(getContext()).inflate(R.layout.dialog_detalle_ejercicio, null);
        TextView tvTitulo = dialogo.findViewById(R.id.tv_dialog_titulo);
        TextView tvFoco = dialogo.findViewById(R.id.tv_dialog_foco);
        TextView tvMaterial = dialogo.findViewById(R.id.tv_dialog_material);
        TextView tvCategoria = dialogo.findViewById(R.id.tv_dialog_categoria);
        ImageView ivFoto = dialogo.findViewById(R.id.iv_dialog_foto);

        tvCategoria.setVisibility(View.GONE);
        tvTitulo.setText(ej.getNombre());
        tvFoco.setText(ej.getFocoPrincipal());
        tvMaterial.setText("Material: " + ej.getMaterial());
        ivFoto.setImageResource(ej.getIdImagen());

        new AlertDialog.Builder(getContext())
                .setView(dialogo)
                .setPositiveButton("Cerrar", null)
                .show();
    }
}
