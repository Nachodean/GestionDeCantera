package com.entrenador.pizarratactica.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.entrenador.pizarratactica.R;
import com.entrenador.pizarratactica.controller.PlantillaController;
import com.entrenador.pizarratactica.model.Jugador;

import java.util.ArrayList;
import java.util.List;

/** Lista de jugadores de la plantilla. Los clics los gestiona el controlador. */
public class JugadoresAdapter extends RecyclerView.Adapter<JugadoresAdapter.VH> {

    private final List<Jugador> jugadores = new ArrayList<>();
    private final PlantillaController controlador;

    public JugadoresAdapter(PlantillaController controlador) {
        this.controlador = controlador;
    }

    public void actualizar(List<Jugador> nuevos) {
        jugadores.clear();
        jugadores.addAll(nuevos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jugador, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Jugador j = jugadores.get(position);
        holder.tvDorsal.setText(String.valueOf(j.getDorsal()));
        holder.tvNombre.setText(j.getNombre() + " " + j.getApellidos());
        holder.tvPosicion.setText(j.getPosicion().getNombre());
        holder.btnEliminar.setOnClickListener(v -> controlador.alPulsarEliminar(j));
    }

    @Override
    public int getItemCount() { return jugadores.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvDorsal, tvNombre, tvPosicion;
        final View btnEliminar;

        VH(@NonNull View itemView) {
            super(itemView);
            tvDorsal = itemView.findViewById(R.id.tv_dorsal);
            tvNombre = itemView.findViewById(R.id.tv_nombre_jugador);
            tvPosicion = itemView.findViewById(R.id.tv_posicion);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar_jugador);
        }
    }
}
