package com.entrenador.pizarratactica.adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.entrenador.pizarratactica.R;
import com.entrenador.pizarratactica.controller.EditorSesionController;
import com.entrenador.pizarratactica.view.pizarra.ItemPaleta;
import com.entrenador.pizarratactica.view.pizarra.MiniaturaView;

import java.util.ArrayList;
import java.util.List;

/**
 * Paleta de objetos agrupada por secciones (Material, Jugadores, Textos).
 * Cada objeto se puede tocar (se anade en el centro) o arrastrar al campo.
 */
public class PaletaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TIPO_SECCION = 0;
    private static final int TIPO_ITEM = 1;

    /** Cada fila es un String (titulo de seccion) o un ItemPaleta. */
    private final List<Object> filas = new ArrayList<>();
    private final EditorSesionController controlador;

    public PaletaAdapter(List<ItemPaleta> items, EditorSesionController controlador) {
        this.controlador = controlador;
        String seccionActual = null;
        for (ItemPaleta i : items) {
            if (!i.seccion.equals(seccionActual)) {
                seccionActual = i.seccion;
                filas.add(seccionActual);
            }
            filas.add(i);
        }
    }

    /** Las secciones ocupan toda la fila de la rejilla. */
    public GridLayoutManager.SpanSizeLookup spanLookup(final int columnas) {
        return new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int pos) {
                return getItemViewType(pos) == TIPO_SECCION ? columnas : 1;
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        return filas.get(position) instanceof String ? TIPO_SECCION : TIPO_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (viewType == TIPO_SECCION) {
            return new RecyclerView.ViewHolder(inf.inflate(R.layout.item_paleta_seccion, parent, false)) {};
        }
        return new ItemVH(inf.inflate(R.layout.item_paleta, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        Object fila = filas.get(position);
        if (fila instanceof String) {
            ((TextView) h.itemView.findViewById(R.id.tv_seccion)).setText((String) fila);
        } else {
            ((ItemVH) h).bind((ItemPaleta) fila);
        }
    }

    @Override
    public int getItemCount() { return filas.size(); }

    class ItemVH extends RecyclerView.ViewHolder {
        final MiniaturaView mini;
        final TextView nombre;
        ItemPaleta item;
        float bajadaX, bajadaY;
        boolean arrastrando;

        @SuppressLint("ClickableViewAccessibility")
        ItemVH(View v) {
            super(v);
            mini = v.findViewById(R.id.mini_item);
            nombre = v.findViewById(R.id.tv_item);
            final int umbral = ViewConfiguration.get(v.getContext()).getScaledTouchSlop();

            v.setOnClickListener(x -> {
                if (item != null) controlador.agregarDesdePaleta(item);
            });
            v.setOnLongClickListener(x -> empezarArrastre(x));
            // Arrastrar hacia el campo (movimiento horizontal) inicia el drag sin esperar
            // al toque largo; el movimiento vertical sigue desplazando la paleta.
            v.setOnTouchListener((x, ev) -> {
                switch (ev.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        bajadaX = ev.getRawX();
                        bajadaY = ev.getRawY();
                        arrastrando = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = ev.getRawX() - bajadaX, dy = ev.getRawY() - bajadaY;
                        if (!arrastrando && Math.abs(dx) > umbral * 1.5f && Math.abs(dx) > Math.abs(dy) * 1.2f) {
                            arrastrando = true;
                            x.cancelLongPress();
                            empezarArrastre(x);
                            return true;
                        }
                        break;
                }
                return false;
            });
        }

        void bind(ItemPaleta i) {
            this.item = i;
            mini.mostrarElemento(i.prototipo());
            nombre.setText(i.nombre);
            itemView.setContentDescription("Añadir " + i.nombre);
        }

        boolean empezarArrastre(View v) {
            if (item == null) return false;
            ClipData datos = ClipData.newPlainText("objeto", item.nombre);
            View.DragShadowBuilder sombra = new View.DragShadowBuilder(mini);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                v.startDragAndDrop(datos, sombra, item, 0);
            } else {
                //noinspection deprecation
                v.startDrag(datos, sombra, item, 0);
            }
            return true;
        }
    }
}
