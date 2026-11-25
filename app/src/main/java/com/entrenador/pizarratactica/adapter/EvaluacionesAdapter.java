package com.entrenador.pizarratactica.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.entrenador.pizarratactica.R;
import com.entrenador.pizarratactica.controller.PuntuacionController;

/**
 * Fila de evaluacion por jugador (estrellas + observaciones).
 * Los datos viven en el controlador; el adaptador solo los muestra y le avisa de cambios.
 */
public class EvaluacionesAdapter extends RecyclerView.Adapter<EvaluacionesAdapter.VH> {

    private final PuntuacionController controlador;

    public EvaluacionesAdapter(PuntuacionController controlador) {
        this.controlador = controlador;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_evaluacion, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.tvJugador.setText(controlador.getJugador(position).getNombreCompleto());
        holder.ratingBar.setOnRatingBarChangeListener(null);
        holder.ratingBar.setRating(controlador.getPuntuacion(position));
        holder.etObs.setText(controlador.getObservacion(position));

        holder.ratingBar.setOnRatingBarChangeListener((rb, valor, delUsuario) -> {
            if (delUsuario) controlador.cambiarPuntuacion(holder.getBindingAdapterPosition(), (int) valor);
        });
        holder.etObs.setOnFocusChangeListener((v, conFoco) -> {
            if (!conFoco) {
                controlador.cambiarObservacion(holder.getBindingAdapterPosition(),
                        holder.etObs.getText().toString().trim());
            }
        });
    }

    @Override
    public int getItemCount() { return controlador.numJugadores(); }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvJugador;
        final RatingBar ratingBar;
        final EditText etObs;

        VH(@NonNull View v) {
            super(v);
            tvJugador = v.findViewById(R.id.tv_jugador_eval);
            ratingBar = v.findViewById(R.id.rating_eval);
            etObs = v.findViewById(R.id.et_observaciones);
        }
    }
}
