package com.entrenador.pizarratactica.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.entrenador.pizarratactica.R;
import com.entrenador.pizarratactica.controller.PreparadorController;
import com.entrenador.pizarratactica.model.EjercicioFisico;

import java.util.List;

/** Carrusel de ejercicios del preparador fisico. */
public class EjerciciosFisicosAdapter extends RecyclerView.Adapter<EjerciciosFisicosAdapter.VH> {

    private final List<EjercicioFisico> ejercicios;
    private final PreparadorController controlador;

    public EjerciciosFisicosAdapter(List<EjercicioFisico> ejercicios, PreparadorController controlador) {
        this.ejercicios = ejercicios;
        this.controlador = controlador;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ejercicio, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        EjercicioFisico ej = ejercicios.get(position);
        holder.tvNombre.setText(ej.getNombre());
        holder.tvCategoria.setVisibility(View.GONE);
        holder.ivFoto.setImageResource(ej.getIdImagen());
        if (ej.isSinMaterial()) {
            holder.tvBadgeMaterial.setText("SIN MATERIAL");
            holder.tvBadgeMaterial.setBackgroundColor(Color.parseColor("#4CAF50"));
        } else {
            holder.tvBadgeMaterial.setText("CON MATERIAL");
            holder.tvBadgeMaterial.setBackgroundColor(Color.parseColor("#F44336"));
        }
        holder.itemView.setOnClickListener(v -> controlador.alPulsarEjercicio(ej));
    }

    @Override
    public int getItemCount() { return ejercicios.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvNombre, tvCategoria, tvBadgeMaterial;
        final ImageView ivFoto;

        VH(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre_ejercicio);
            tvCategoria = itemView.findViewById(R.id.tv_categoria_ejercicio);
            ivFoto = itemView.findViewById(R.id.iv_foto_ejercicio);
            tvBadgeMaterial = itemView.findViewById(R.id.tv_badge_material);
        }
    }
}
