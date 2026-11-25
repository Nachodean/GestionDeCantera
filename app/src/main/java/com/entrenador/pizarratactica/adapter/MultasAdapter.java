package com.entrenador.pizarratactica.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.entrenador.pizarratactica.R;
import com.entrenador.pizarratactica.controller.MultasController;
import com.entrenador.pizarratactica.model.Multa;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Lista de multas. Marcar como pagada lo gestiona el controlador. */
public class MultasAdapter extends RecyclerView.Adapter<MultasAdapter.VH> {

    private final List<Multa> multas = new ArrayList<>();
    private final MultasController controlador;

    public MultasAdapter(MultasController controlador) {
        this.controlador = controlador;
    }

    public void actualizar(List<Multa> nuevas) {
        multas.clear();
        multas.addAll(nuevas);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multa, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Multa m = multas.get(position);
        holder.tvJugador.setText(m.getJugadorNombre());
        holder.tvMotivo.setText(m.getMotivo());
        holder.tvFecha.setText(m.getFecha());
        holder.tvCantidad.setText(String.format(Locale.getDefault(), "%.2f €", m.getCantidad()));

        holder.cbPagada.setOnCheckedChangeListener(null);
        holder.cbPagada.setChecked(m.isPagada());
        holder.cbPagada.setOnCheckedChangeListener((b, marcada) -> controlador.cambiarPagada(m, marcada));
    }

    @Override
    public int getItemCount() { return multas.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvJugador, tvMotivo, tvFecha, tvCantidad;
        final CheckBox cbPagada;

        VH(@NonNull View v) {
            super(v);
            tvJugador = v.findViewById(R.id.tv_jugador_multa);
            tvMotivo = v.findViewById(R.id.tv_motivo_multa);
            tvFecha = v.findViewById(R.id.tv_fecha_multa);
            tvCantidad = v.findViewById(R.id.tv_cantidad_multa);
            cbPagada = v.findViewById(R.id.cb_multa_pagada);
        }
    }
}
