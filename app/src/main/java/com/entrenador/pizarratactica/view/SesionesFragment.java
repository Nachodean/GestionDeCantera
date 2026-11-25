package com.entrenador.pizarratactica.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.entrenador.pizarratactica.R;
import com.entrenador.pizarratactica.adapter.SesionesAdapter;
import com.entrenador.pizarratactica.controller.SesionesController;
import com.entrenador.pizarratactica.model.pizarra.Sesion;
import com.entrenador.pizarratactica.view.pizarra.ExportadorSesion;

import java.io.File;
import java.util.List;

/**
 * Vista de la lista de sesiones guardadas.
 * La logica esta en {@link SesionesController}.
 */
public class SesionesFragment extends Fragment {

    // Elementos de la interfaz (UI)
    private RecyclerView rvSesiones;
    private TextView tvVacio, tvContador;
    private View btnNuevaSesion;

    // Adaptador y controlador (MVC)
    private SesionesAdapter adaptador;
    private SesionesController controlador;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sesiones, container, false);
        recogerElementos(root);
        establecerEscuchadores();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity a = (AppCompatActivity) getActivity();
            if (a.getSupportActionBar() != null) a.getSupportActionBar().setTitle("Sesiones");
        }
        controlador.cargarSesiones();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (controlador != null) controlador.liberar();
    }

    private void recogerElementos(View root) {
        rvSesiones = root.findViewById(R.id.rv_sesiones);
        tvVacio = root.findViewById(R.id.tv_vacio_sesiones);
        tvContador = root.findViewById(R.id.tv_contador_sesiones);
        btnNuevaSesion = root.findViewById(R.id.btn_nueva_sesion);
    }

    private void establecerEscuchadores() {
        controlador = new SesionesController(this);
        adaptador = new SesionesAdapter(controlador);
        // Dos columnas en tablet, una en movil.
        int columnas = getResources().getConfiguration().screenWidthDp >= 840 ? 2 : 1;
        rvSesiones.setLayoutManager(new GridLayoutManager(requireContext(), columnas));
        rvSesiones.setAdapter(adaptador);
        btnNuevaSesion.setOnClickListener(v -> controlador.nuevaSesion());
    }

    // --- MÉTODOS PÚBLICOS PARA COMUNICACIÓN CON EL CONTROLADOR ---

    public void pintarSesiones(List<Sesion> sesiones) {
        adaptador.actualizar(sesiones);
        boolean vacio = sesiones.isEmpty();
        tvVacio.setVisibility(vacio ? View.VISIBLE : View.GONE);
        rvSesiones.setVisibility(vacio ? View.GONE : View.VISIBLE);
        tvContador.setText(vacio ? "" : sesiones.size() + (sesiones.size() == 1 ? " sesión" : " sesiones"));
    }

    /** Abre el editor de una sesion (id null = sesion nueva). */
    public void abrirEditor(@Nullable String idSesion) {
        if (getActivity() == null) return;
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, EditorSesionFragment.abrir(idSesion))
                .addToBackStack("editor")
                .commit();
    }

    public void confirmarEliminarSesion(Sesion s) {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar sesión")
                .setMessage("¿Eliminar «" + s.getTituloVisible() + "» y sus "
                        + s.numEjercicios() + " ejercicios? No se puede deshacer.")
                .setPositiveButton("Eliminar", (d, w) -> controlador.eliminarSesion(s))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void compartirArchivo(File archivo, String tipo) {
        if (isAdded()) ExportadorSesion.compartir(requireContext(), archivo, tipo);
    }

    public void mostrarMensaje(String mensaje) {
        if (getContext() != null) Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
    }
}
