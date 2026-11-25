package com.entrenador.pizarratactica.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.entrenador.pizarratactica.R;
import com.entrenador.pizarratactica.controller.EditorSesionController;
import com.entrenador.pizarratactica.model.pizarra.Ejercicio;
import com.entrenador.pizarratactica.view.pizarra.MiniaturaView;

/** Tira con las miniaturas de los ejercicios de la sesion. */
public class EjerciciosAdapter extends RecyclerView.Adapter<EjerciciosAdapter.VH> {

    private final EditorSesionController controlador;
    private final boolean vertical;
    private int seleccionado;

    /** @param vertical true si la tira es una columna (pantalla en horizontal). */
    public EjerciciosAdapter(EditorSesionController controlador, boolean vertical) {
        this.controlador = controlador;
        this.vertical = vertical;
    }

    public void setSeleccionado(int i) {
        int antes = seleccionado;
        seleccionado = i;
        notifyItemChanged(antes);
        notifyItemChanged(i);
    }

    /** Redibuja la miniatura del ejercicio actual (tras editarlo). */
    public void refrescarActual() {
        notifyItemChanged(seleccionado);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ejercicio_tira, parent, false);
        if (vertical) {
            // En columna: ocupa el ancho y alto proporcional al campo (4:3) + texto.
            float dp = parent.getResources().getDisplayMetrics().density;
            int ancho = parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight() - (int) (8 * dp);
            if (ancho <= 0) ancho = (int) (100 * dp);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = (int) (ancho * 0.75f + 26 * dp);
            v.setLayoutParams(lp);
        }
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Ejercicio e = controlador.getSesion().getEjercicio(position);
        h.mini.mostrarEjercicio(e);
        String titulo = e.getTitulo().isEmpty() ? "Ejercicio" : e.getTitulo();
        String dur = e.getDuracionMin() > 0 ? " · " + e.getDuracionMin() + "'" : "";
        h.texto.setText((position + 1) + ". " + titulo + dur);
        h.itemView.setSelected(position == seleccionado);
        h.itemView.setContentDescription("Ejercicio " + (position + 1) + ": " + titulo);
    }

    @Override
    public int getItemCount() { return controlador.getSesion().numEjercicios(); }

    class VH extends RecyclerView.ViewHolder {
        final MiniaturaView mini;
        final TextView texto;

        VH(View v) {
            super(v);
            mini = v.findViewById(R.id.mini_ejercicio);
            texto = v.findViewById(R.id.tv_ejercicio);
            v.setOnClickListener(x -> {
                int p = getBindingAdapterPosition();
                if (p != RecyclerView.NO_POSITION) controlador.elegirEjercicio(p);
            });
            v.setOnLongClickListener(x -> {
                int p = getBindingAdapterPosition();
                if (p != RecyclerView.NO_POSITION) controlador.alMantenerEjercicio(p, x);
                return true;
            });
        }
    }
}
