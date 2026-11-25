package com.entrenador.pizarratactica.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.entrenador.pizarratactica.R;
import com.entrenador.pizarratactica.adapter.EjerciciosAdapter;
import com.entrenador.pizarratactica.adapter.PaletaAdapter;
import com.entrenador.pizarratactica.controller.EditorSesionController;
import com.entrenador.pizarratactica.model.pizarra.ElementoPizarra;
import com.entrenador.pizarratactica.model.pizarra.Ejercicio;
import com.entrenador.pizarratactica.model.pizarra.TipoElemento;
import com.entrenador.pizarratactica.model.pizarra.TipoTrazo;
import com.entrenador.pizarratactica.model.pizarra.Trazo;
import com.entrenador.pizarratactica.view.pizarra.Dialogos;
import com.entrenador.pizarratactica.view.pizarra.ExportadorSesion;
import com.entrenador.pizarratactica.view.pizarra.ItemPaleta;
import com.entrenador.pizarratactica.view.pizarra.PizarraView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.io.File;

/**
 * Vista del editor de sesiones: paleta de material, pizarra, herramientas,
 * barra del objeto seleccionado y tira de ejercicios.
 * <p>
 * Solo se encarga de la interfaz; toda la logica esta en
 * {@link EditorSesionController}, que tambien escucha los gestos de la pizarra.
 */
public class EditorSesionFragment extends Fragment {

    public static final String ARG_ID = "SESION_ID";

    // Elementos de la interfaz (UI)
    private View root;
    private PizarraView pizarra;
    private EditText etNumero, etFecha, etObjetivo;
    private TextView tvDuracionTotal, tvSeleccion, tvInfoEjercicio;
    private View barraSeleccion, vistaColorTrazo;
    private MaterialButton btnDeshacer, btnRehacer, btnEditar, btnDuplicar, btnGirarIzq, btnGirarDer;
    private MaterialButtonToggleGroup grupoHerramientas;
    private RecyclerView rvPaleta, rvEjercicios;

    // Disposicion segun la orientacion
    private boolean tiraVertical;
    private boolean paletaDebajo;
    private boolean cargandoCampos;

    // Adaptadores y controlador (MVC)
    private EjerciciosAdapter adaptadorEjercicios;
    private EditorSesionController controlador;

    /** Abre una sesion existente, o una nueva si {@code id} es null. */
    public static EditorSesionFragment abrir(@Nullable String id) {
        EditorSesionFragment f = new EditorSesionFragment();
        Bundle b = new Bundle();
        b.putString(ARG_ID, id);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_editor_sesion, container, false);

        // 1. Enlazamos los componentes del XML con Java
        recogerElementos();
        // 2. Colocamos paleta y tira segun la orientacion de la pantalla
        colocarPaneles();
        // 3. Creamos el controlador (MVC) y cargamos la sesion
        controlador = new EditorSesionController(this);
        controlador.cargar(getArguments() != null ? getArguments().getString(ARG_ID) : null, savedInstanceState);
        // 4. Adaptadores y escuchadores
        establecerEscuchadores();
        // 5. Pintamos la sesion
        controlador.iniciar();
        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle out) {
        super.onSaveInstanceState(out);
        if (controlador != null) controlador.guardarEstado(out);
    }

    @Override
    public void onResume() {
        super.onResume();
        pantallaCompleta(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        pantallaCompleta(false);
        controlador.alPausar();
    }

    @Override
    public void onStop() {
        super.onStop();
        ocultarTeclado();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (controlador != null) controlador.liberar();
    }

    // ═════════════════════════════════════════════════════════════════
    //  Preparacion de la interfaz
    // ═════════════════════════════════════════════════════════════════

    private void recogerElementos() {
        pizarra = root.findViewById(R.id.pizarra);
        etNumero = root.findViewById(R.id.et_numero);
        etFecha = root.findViewById(R.id.et_fecha);
        etObjetivo = root.findViewById(R.id.et_objetivo);
        tvInfoEjercicio = root.findViewById(R.id.tv_info_ejercicio);
        tvDuracionTotal = root.findViewById(R.id.tv_duracion_total);
        tvSeleccion = root.findViewById(R.id.tv_seleccion);
        barraSeleccion = root.findViewById(R.id.barra_seleccion);
        vistaColorTrazo = root.findViewById(R.id.vista_color_trazo);
        btnDeshacer = root.findViewById(R.id.btn_deshacer);
        btnRehacer = root.findViewById(R.id.btn_rehacer);
        btnEditar = root.findViewById(R.id.btn_editar);
        btnDuplicar = root.findViewById(R.id.btn_duplicar);
        btnGirarIzq = root.findViewById(R.id.btn_girar_izq);
        btnGirarDer = root.findViewById(R.id.btn_girar_der);
        grupoHerramientas = root.findViewById(R.id.grupo_herramientas);
        rvPaleta = root.findViewById(R.id.rv_paleta);
        rvEjercicios = root.findViewById(R.id.rv_ejercicios);
    }

    /**
     * Horizontal: la tira de ejercicios pasa a una columna a la derecha (sobra ancho junto al campo 4:3).
     * Vertical: la paleta pasa debajo del campo para que este ocupe todo el ancho.
     */
    private void colocarPaneles() {
        LinearLayout fila = root.findViewById(R.id.fila_central);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LinearLayout tira = root.findViewById(R.id.contenedor_tira);
            ((ViewGroup) tira.getParent()).removeView(tira);
            tira.setOrientation(LinearLayout.VERTICAL);
            int ancho = getResources().getDimensionPixelSize(R.dimen.ancho_tira_vertical);
            fila.addView(tira, new LinearLayout.LayoutParams(ancho, ViewGroup.LayoutParams.MATCH_PARENT));
            rvEjercicios.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
            tiraVertical = true;
        } else {
            View panelPaleta = fila.getChildAt(0);
            View zonaPizarra = fila.getChildAt(1);
            fila.removeView(panelPaleta);
            fila.setOrientation(LinearLayout.VERTICAL);
            zonaPizarra.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.25f));
            fila.addView(panelPaleta, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
            paletaDebajo = true;
        }
    }

    private void establecerEscuchadores() {
        // La pizarra avisa al controlador de cada gesto.
        pizarra.setListener(controlador);

        // Paleta
        int columnas = paletaDebajo
                ? Math.max(4, getResources().getConfiguration().screenWidthDp / 80)
                : getResources().getInteger(R.integer.columnas_paleta);
        GridLayoutManager glm = new GridLayoutManager(requireContext(), columnas);
        PaletaAdapter adaptadorPaleta = new PaletaAdapter(ItemPaleta.catalogo(), controlador);
        glm.setSpanSizeLookup(adaptadorPaleta.spanLookup(columnas));
        rvPaleta.setLayoutManager(glm);
        rvPaleta.setAdapter(adaptadorPaleta);

        // Tira de ejercicios
        adaptadorEjercicios = new EjerciciosAdapter(controlador, tiraVertical);
        rvEjercicios.setLayoutManager(new LinearLayoutManager(requireContext(),
                tiraVertical ? LinearLayoutManager.VERTICAL : LinearLayoutManager.HORIZONTAL, false));
        rvEjercicios.setAdapter(adaptadorEjercicios);
        root.findViewById(R.id.btn_nuevo_ejercicio).setOnClickListener(v -> controlador.nuevoEjercicio());

        // Barra de la sesion
        root.findViewById(R.id.btn_volver).setOnClickListener(v -> controlador.volver());
        root.findViewById(R.id.btn_guardar).setOnClickListener(v -> controlador.guardar(true));
        root.findViewById(R.id.btn_pdf).setOnClickListener(v -> controlador.exportarPdf());
        etNumero.addTextChangedListener(alCambiarTexto(t -> controlador.cambiarNumero(t)));
        etObjetivo.addTextChangedListener(alCambiarTexto(t -> controlador.cambiarObjetivo(t)));
        etFecha.setOnClickListener(v -> controlador.alPulsarFecha());
        tvInfoEjercicio.setOnClickListener(v -> controlador.alPulsarInfoEjercicio());

        // Herramientas
        grupoHerramientas.addOnButtonCheckedListener((g, id, marcado) -> {
            if (marcado) controlador.elegirHerramienta(herramientaDeBoton(id));
        });
        vistaColorTrazo.setOnClickListener(v -> controlador.alPulsarColorTrazo());
        btnDeshacer.setOnClickListener(v -> controlador.deshacer());
        btnRehacer.setOnClickListener(v -> controlador.rehacer());
        root.findViewById(R.id.btn_campo).setOnClickListener(v -> controlador.alPulsarCampo());
        root.findViewById(R.id.btn_limpiar).setOnClickListener(v -> controlador.alPulsarLimpiar());
        root.findViewById(R.id.btn_imagen).setOnClickListener(v -> controlador.exportarImagen());

        // Barra del objeto seleccionado
        btnGirarIzq.setOnClickListener(v -> controlador.girarSeleccion(-15));
        btnGirarDer.setOnClickListener(v -> controlador.girarSeleccion(15));
        root.findViewById(R.id.btn_menos).setOnClickListener(v -> controlador.escalarSeleccion(0.85f));
        root.findViewById(R.id.btn_mas).setOnClickListener(v -> controlador.escalarSeleccion(1.18f));
        root.findViewById(R.id.btn_color).setOnClickListener(v -> controlador.alPulsarColorSeleccion());
        btnEditar.setOnClickListener(v -> controlador.alPulsarEditarSeleccion());
        btnDuplicar.setOnClickListener(v -> controlador.duplicarSeleccion());
        root.findViewById(R.id.btn_eliminar).setOnClickListener(v -> controlador.eliminarSeleccion());
    }

    /** Traduce el boton de herramienta al tipo de flecha (null = Mover). */
    @Nullable
    private TipoTrazo herramientaDeBoton(int id) {
        if (id == R.id.btn_pase) return TipoTrazo.PASE;
        if (id == R.id.btn_sin_balon) return TipoTrazo.SIN_BALON;
        if (id == R.id.btn_conduccion) return TipoTrazo.CONDUCCION;
        if (id == R.id.btn_linea) return TipoTrazo.LINEA;
        if (id == R.id.btn_medida) return TipoTrazo.MEDIDA;
        if (id == R.id.btn_libre) return TipoTrazo.LIBRE;
        return null;
    }

    // ═════════════════════════════════════════════════════════════════
    //  MÉTODOS PÚBLICOS PARA COMUNICACIÓN CON EL CONTROLADOR
    // ═════════════════════════════════════════════════════════════════

    // ── Datos de la sesion ───────────────────────────────────────────

    public void pintarDatosSesion(String numero, String fecha, String objetivo) {
        cargandoCampos = true;
        etNumero.setText(numero);
        etFecha.setText(fecha);
        etObjetivo.setText(objetivo);
        cargandoCampos = false;
    }

    public void pintarFecha(String fecha) {
        etFecha.setText(fecha);
    }

    public void pintarDuracionTotal(int minutos) {
        tvDuracionTotal.setText(minutos > 0 ? "⏱ " + minutos + " min" : "");
    }

    // ── Ejercicios ───────────────────────────────────────────────────

    public void pintarEjercicio(Ejercicio ejercicio, int indice) {
        pizarra.setEjercicio(ejercicio);
        adaptadorEjercicios.setSeleccionado(indice);
        rvEjercicios.smoothScrollToPosition(indice);
        pintarBarraSeleccion(null, null);
    }

    public void pintarInfoEjercicio(String texto) {
        tvInfoEjercicio.setText(texto);
        tvInfoEjercicio.setContentDescription("Editar título, minutos y explicación del ejercicio");
    }

    public void ejercicioInsertado(int posicion) {
        adaptadorEjercicios.notifyItemInserted(posicion);
    }

    public void ejercicioMovido(int desde, int hasta) {
        adaptadorEjercicios.notifyItemMoved(desde, hasta);
        adaptadorEjercicios.notifyItemRangeChanged(Math.min(desde, hasta), 2);
    }

    public void ejercicioEliminado(int posicion, int quedan) {
        adaptadorEjercicios.notifyItemRemoved(posicion);
        adaptadorEjercicios.notifyItemRangeChanged(posicion, quedan - posicion);
    }

    public void refrescarMiniatura() {
        adaptadorEjercicios.refrescarActual();
    }

    public void mostrarMenuEjercicio(int i, View ancla, boolean puedeIzquierda, boolean puedeDerecha,
                                     boolean puedeEliminar) {
        PopupMenu menu = new PopupMenu(requireContext(), ancla);
        menu.getMenu().add(0, 1, 0, "Duplicar ejercicio");
        if (puedeIzquierda) menu.getMenu().add(0, 2, 1, "Mover a la izquierda");
        if (puedeDerecha) menu.getMenu().add(0, 3, 2, "Mover a la derecha");
        if (puedeEliminar) menu.getMenu().add(0, 4, 3, "Eliminar ejercicio");
        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 1: controlador.duplicarEjercicio(i); return true;
                case 2: controlador.moverEjercicio(i, i - 1); return true;
                case 3: controlador.moverEjercicio(i, i + 1); return true;
                case 4: controlador.alPulsarEliminarEjercicio(i); return true;
            }
            return false;
        });
        menu.show();
    }

    public void mostrarDialogoDatosEjercicio(int numero, String titulo, String minutos, String explicacion) {
        View v = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_datos_ejercicio, null);
        EditText etTitulo = v.findViewById(R.id.et_dialogo_titulo);
        EditText etMinutos = v.findViewById(R.id.et_dialogo_minutos);
        EditText etExplicacion = v.findViewById(R.id.et_dialogo_explicacion);
        etTitulo.setText(titulo);
        etMinutos.setText(minutos);
        etExplicacion.setText(explicacion);
        new AlertDialog.Builder(requireContext())
                .setTitle("Ejercicio " + numero)
                .setView(v)
                .setPositiveButton("Aceptar", (d, w) -> controlador.guardarDatosEjercicio(
                        etTitulo.getText().toString(), etMinutos.getText().toString(),
                        etExplicacion.getText().toString()))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ── Pizarra ──────────────────────────────────────────────────────

    public void refrescarPizarra() {
        pizarra.invalidate();
        adaptadorEjercicios.refrescarActual();
    }

    public void ponerModoMover() {
        pizarra.setModo(PizarraView.Modo.MOVER);
    }

    public void ponerModoDibujo(TipoTrazo tipo, int color) {
        pizarra.setTipoTrazo(tipo);
        pizarra.setColorTrazo(color);
        pizarra.setModo(PizarraView.Modo.DIBUJAR);
    }

    /** Selecciona la herramienta Mover en la barra (y la pizarra pasa a modo mover). */
    public void activarHerramientaMover() {
        if (grupoHerramientas.getCheckedButtonId() != R.id.btn_mover) grupoHerramientas.check(R.id.btn_mover);
    }

    public void ponerColorTrazo(int color) {
        pizarra.setColorTrazo(color);
    }

    public int getColorTrazo() {
        return pizarra.getColorTrazo();
    }

    public boolean dibujandoMedidas() {
        return pizarra.getModo() == PizarraView.Modo.DIBUJAR && pizarra.getTipoTrazo() == TipoTrazo.MEDIDA;
    }

    public void pintarColorTrazo(int color) {
        float dp = getResources().getDisplayMetrics().density;
        vistaColorTrazo.setBackground(Dialogos.circulo(color, 0xFFFFFFFF, (int) (2 * dp)));
    }

    public void pintarBotonesHistorial(boolean puedeDeshacer, boolean puedeRehacer) {
        btnDeshacer.setEnabled(puedeDeshacer);
        btnRehacer.setEnabled(puedeRehacer);
        btnDeshacer.setAlpha(puedeDeshacer ? 1f : 0.4f);
        btnRehacer.setAlpha(puedeRehacer ? 1f : 0.4f);
    }

    // ── Seleccion ────────────────────────────────────────────────────

    @Nullable
    public ElementoPizarra getElementoSeleccionado() {
        return pizarra.getElementoSeleccionado();
    }

    @Nullable
    public Trazo getTrazoSeleccionado() {
        return pizarra.getTrazoSeleccionado();
    }

    public void seleccionarElemento(long id) {
        pizarra.seleccionarElemento(id);
    }

    public void deseleccionar() {
        pizarra.deseleccionar();
    }

    /** Barra de acciones del objeto/flecha seleccionado (oculta si no hay seleccion). */
    public void pintarBarraSeleccion(@Nullable ElementoPizarra e, @Nullable Trazo t) {
        if (e == null && t == null) {
            barraSeleccion.setVisibility(View.GONE);
            return;
        }
        barraSeleccion.setVisibility(View.VISIBLE);

        // La barra se coloca en el lado opuesto al objeto para no taparlo.
        float y = e != null ? e.getY() : centroY(t);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) barraSeleccion.getLayoutParams();
        int gravedad = (y > 0.55f ? Gravity.TOP : Gravity.BOTTOM) | Gravity.CENTER_HORIZONTAL;
        if (lp.gravity != gravedad) {
            lp.gravity = gravedad;
            barraSeleccion.setLayoutParams(lp);
        }

        if (e != null) {
            String nombre = e.getTipo().getNombre();
            if (e.getTipo().admiteNumero() && e.getNumero() > 0) nombre += " " + e.getNumero();
            tvSeleccion.setText(nombre);
            btnGirarIzq.setVisibility(View.VISIBLE);
            btnGirarDer.setVisibility(View.VISIBLE);
            btnDuplicar.setVisibility(View.VISIBLE);
            boolean editable = e.getTipo() == TipoElemento.JUGADOR || e.getTipo() == TipoElemento.CAMISETA
                    || e.getTipo() == TipoElemento.TEXTO;
            btnEditar.setVisibility(editable ? View.VISIBLE : View.GONE);
            btnEditar.setText(e.getTipo() == TipoElemento.TEXTO ? "✏ Texto" : "✏ Dorsal");
        } else {
            tvSeleccion.setText(t.getTipo().getNombre());
            btnGirarIzq.setVisibility(View.GONE);
            btnGirarDer.setVisibility(View.GONE);
            btnDuplicar.setVisibility(View.GONE);
            btnEditar.setVisibility(t.getTipo() == TipoTrazo.MEDIDA ? View.VISIBLE : View.GONE);
            btnEditar.setText("✏ Medida");
        }
    }

    private static float centroY(Trazo t) {
        float suma = 0;
        for (float[] p : t.getPuntos()) suma += p[1];
        return t.numPuntos() == 0 ? 0.5f : suma / t.numPuntos();
    }

    // ── Dialogos ─────────────────────────────────────────────────────

    public void mostrarSelectorColor(String titulo, int actual, Dialogos.AlElegirColor alElegir) {
        Dialogos.elegirColor(requireContext(), titulo, actual, alElegir);
    }

    public void pedirTexto(String titulo, String valor, boolean multilinea, boolean numerico,
                           Dialogos.AlEscribir alEscribir) {
        Dialogos.pedirTexto(requireContext(), titulo, valor, multilinea, numerico, alEscribir);
    }

    public void mostrarSelectorCampo(String[] nombres, int actual) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Fondo del ejercicio")
                .setSingleChoiceItems(nombres, actual, (d, w) -> {
                    controlador.cambiarCampo(w);
                    d.dismiss();
                })
                .show();
    }

    public void mostrarSelectorFecha(int anio, int mes, int dia) {
        new DatePickerDialog(requireContext(), (v, a, m, d) -> controlador.fechaElegida(a, m, d),
                anio, mes, dia).show();
    }

    /** Dialogo de confirmacion generico (Cancelar / {@code boton}). */
    public void confirmar(String titulo, String mensaje, String boton, Runnable alAceptar) {
        new AlertDialog.Builder(requireContext())
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton(boton, (d, w) -> alAceptar.run())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ── Otros ────────────────────────────────────────────────────────

    public void compartirArchivo(File archivo, String tipo) {
        if (isAdded()) ExportadorSesion.compartir(requireContext(), archivo, tipo);
    }

    public void mostrarMensaje(String mensaje) {
        if (getContext() != null) Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

    /** Vuelve a la lista de sesiones (el guardado lo hace onPause). */
    public void cerrarEditor() {
        if (!isAdded()) return;
        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
        } else {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SesionesFragment())
                    .commit();
        }
    }

    // ═════════════════════════════════════════════════════════════════
    //  Ayudas de interfaz
    // ═════════════════════════════════════════════════════════════════

    /**
     * En el editor se ocultan la barra de la app y las del sistema para dar
     * todo el espacio al campo. Deslizando desde el borde vuelven a aparecer.
     */
    private void pantallaCompleta(boolean activar) {
        if (!(getActivity() instanceof AppCompatActivity)) return;
        AppCompatActivity a = (AppCompatActivity) getActivity();
        if (a.getSupportActionBar() != null) {
            if (activar) a.getSupportActionBar().hide();
            else a.getSupportActionBar().show();
        }
        WindowInsetsControllerCompat c = WindowCompat.getInsetsController(a.getWindow(), a.getWindow().getDecorView());
        if (activar) {
            c.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            c.hide(WindowInsetsCompat.Type.systemBars());
        } else {
            c.show(WindowInsetsCompat.Type.systemBars());
        }
    }

    private void ocultarTeclado() {
        View v = getView();
        if (v != null && getContext() != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private interface AlTexto { void cambiado(String t); }

    private TextWatcher alCambiarTexto(AlTexto cb) {
        return new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { }
            @Override
            public void afterTextChanged(Editable s) {
                if (!cargandoCampos) cb.cambiado(s.toString());
            }
        };
    }
}
