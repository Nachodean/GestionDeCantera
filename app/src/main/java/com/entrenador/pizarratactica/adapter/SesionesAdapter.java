package com.entrenador.pizarratactica.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.entrenador.pizarratactica.R;
import com.entrenador.pizarratactica.controller.SesionesController;
import com.entrenador.pizarratactica.model.pizarra.Ejercicio;
import com.entrenador.pizarratactica.model.pizarra.Sesion;
import com.entrenador.pizarratactica.view.pizarra.MiniaturaView;

import java.util.ArrayList;
import java.util.List;

/** Tarjetas de la lista de sesiones (miniatura + datos + acciones). */
public class SesionesAdapter extends RecyclerView.Adapter<SesionesAdapter.VH> {

    private final List<Sesion> sesiones = new ArrayList<>();
    private final SesionesController controlador;

    public SesionesAdapter(SesionesController controlador) {
        this.controlador = controlador;
    }

    public void actualizar(List<Sesion> nuevas) {
        sesiones.clear();
        sesiones.addAll(nuevas);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sesion, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Sesion s = sesiones.get(position);
        h.mini.mostrarEjercicio(s.getEjercicio(0));
        h.titulo.setText(s.getTituloVisible());
        h.fecha.setText(s.getFecha().isEmpty() ? "Sin fecha" : s.getFecha());
        h.objetivo.setText(s.getObjetivo());
        h.objetivo.setVisibility(s.getObjetivo().isEmpty() ? View.GONE : View.VISIBLE);
        h.resumen.setText(resumen(s));

        h.itemView.setOnClickListener(v -> controlador.abrirSesion(s));
        h.btnPdf.setOnClickListener(v -> controlador.exportarPdf(s));
        h.btnDuplicar.setOnClickListener(v -> controlador.duplicarSesion(s));
        h.btnEliminar.setOnClickListener(v -> controlador.alPulsarEliminar(s));
    }

    /** "3 ejercicios · 45 min" + titulos de los ejercicios. */
    private String resumen(Sesion s) {
        StringBuilder sb = new StringBuilder();
        sb.append(s.numEjercicios()).append(s.numEjercicios() == 1 ? " ejercicio" : " ejercicios");
        if (s.duracionTotal() > 0) sb.append(" · ").append(s.duracionTotal()).append(" min");
        StringBuilder titulos = new StringBuilder();
        for (Ejercicio e : s.getEjercicios()) {
            if (e.getTitulo().isEmpty()) continue;
            if (titulos.length() > 0) titulos.append(", ");
            titulos.append(e.getTitulo());
        }
        if (titulos.length() > 0) sb.append("\n").append(titulos);
        return sb.toString();
    }

    @Override
    public int getItemCount() { return sesiones.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final MiniaturaView mini;
        final TextView titulo, fecha, objetivo, resumen;
        final View btnPdf, btnDuplicar, btnEliminar;

        VH(@NonNull View v) {
            super(v);
            mini = v.findViewById(R.id.mini_sesion);
            titulo = v.findViewById(R.id.tv_titulo_sesion);
            fecha = v.findViewById(R.id.tv_fecha_sesion);
            objetivo = v.findViewById(R.id.tv_objetivo_sesion);
            resumen = v.findViewById(R.id.tv_resumen_sesion);
            btnPdf = v.findViewById(R.id.btn_pdf_sesion);
            btnDuplicar = v.findViewById(R.id.btn_duplicar_sesion);
            btnEliminar = v.findViewById(R.id.btn_eliminar_sesion);
        }
    }
}
