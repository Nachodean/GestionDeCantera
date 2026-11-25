package com.entrenador.pizarratactica.controller;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.entrenador.pizarratactica.model.SesionRepository;
import com.entrenador.pizarratactica.model.pizarra.Colores;
import com.entrenador.pizarratactica.model.pizarra.ElementoPizarra;
import com.entrenador.pizarratactica.model.pizarra.Ejercicio;
import com.entrenador.pizarratactica.model.pizarra.Historial;
import com.entrenador.pizarratactica.model.pizarra.Sesion;
import com.entrenador.pizarratactica.model.pizarra.SesionJson;
import com.entrenador.pizarratactica.model.pizarra.TipoCampo;
import com.entrenador.pizarratactica.model.pizarra.TipoElemento;
import com.entrenador.pizarratactica.model.pizarra.TipoTrazo;
import com.entrenador.pizarratactica.model.pizarra.Trazo;
import com.entrenador.pizarratactica.view.EditorSesionFragment;
import com.entrenador.pizarratactica.view.pizarra.ExportadorSesion;
import com.entrenador.pizarratactica.view.pizarra.ItemPaleta;
import com.entrenador.pizarratactica.view.pizarra.PizarraView;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controlador del editor de sesiones.
 * <p>
 * Contiene toda la logica: la sesion y sus ejercicios, deshacer/rehacer,
 * autoguardado, seleccion de objetos y exportacion. Escucha los gestos de la
 * pizarra ({@link PizarraView.Listener}) y le dice a la vista
 * ({@link EditorSesionFragment}) que pintar.
 */
public class EditorSesionController implements PizarraView.Listener {

    private static final String TAG = "EditorSesionController";
    private static final String FORMATO_FECHA = "dd/MM/yyyy";
    private static final long RETRASO_AUTOGUARDADO_MS = 1500;
    private static final String ESTADO_SESION = "estado_sesion";
    private static final String ESTADO_INDICE = "estado_indice";
    private static final String ESTADO_SUCIO = "estado_sucio";
    private static final String ESTADO_NUEVA = "estado_nueva";

    private final EditorSesionFragment vista;
    private final SesionRepository repositorio;
    private final Context contextoApp;
    private final Map<String, Historial> historiales = new HashMap<>();
    private final ExecutorService hilo = Executors.newSingleThreadExecutor();
    private final Handler principal = new Handler(Looper.getMainLooper());

    private Sesion sesion;
    private int indice;
    private boolean sucio;      // hay cambios sin guardar
    private boolean esNueva;    // sesion recien creada que aun no se ha guardado
    private int colorFlechas = Colores.NEGRO;
    private int colorMedida = Colores.AZUL;

    /** Guarda poco despues del ultimo cambio: si la app se cierra de golpe no se pierde nada. */
    private final Runnable autoguardado = () -> {
        if (sucio && hayQueGuardar()) guardar(false);
    };

    public EditorSesionController(EditorSesionFragment vista) {
        this.vista = vista;
        this.contextoApp = vista.requireContext().getApplicationContext();
        this.repositorio = new SesionRepository(contextoApp);
    }

    // ═════════════════════════════════════════════════════════════════
    //  Carga, estado y guardado
    // ═════════════════════════════════════════════════════════════════

    /** Carga la sesion {@code id} (null = nueva) o la recupera tras girar la pantalla. */
    public void cargar(@Nullable String id, @Nullable Bundle estado) {
        if (estado != null && estado.getString(ESTADO_SESION) != null) {
            try {
                sesion = SesionJson.desdeJson(new JSONObject(estado.getString(ESTADO_SESION)));
                indice = estado.getInt(ESTADO_INDICE, 0);
                sucio = estado.getBoolean(ESTADO_SUCIO, false);
                esNueva = estado.getBoolean(ESTADO_NUEVA, false);
                return;
            } catch (Exception e) {
                Log.w(TAG, "No se pudo restaurar el estado", e);
            }
        }
        sesion = repositorio.obtener(id);
        if (sesion == null) {
            sesion = Sesion.nueva(System.currentTimeMillis());
            sesion.setFecha(new SimpleDateFormat(FORMATO_FECHA, Locale.getDefault()).format(new Date()));
            sesion.setNumero(String.valueOf(repositorio.listar().size() + 1));
            sucio = true;
            esNueva = true;
        }
        indice = 0;
    }

    /** Guarda en el Bundle lo que no se haya guardado (giro de pantalla). */
    public void guardarEstado(Bundle salida) {
        try {
            salida.putString(ESTADO_SESION, SesionJson.aJson(sesion).toString());
            salida.putInt(ESTADO_INDICE, indice);
            salida.putBoolean(ESTADO_SUCIO, sucio);
            salida.putBoolean(ESTADO_NUEVA, esNueva);
        } catch (Exception e) {
            Log.w(TAG, "No se pudo guardar el estado", e);
        }
    }

    /** Pinta la sesion cargada en la vista. */
    public void iniciar() {
        vista.pintarDatosSesion(sesion.getNumero(), sesion.getFecha(), sesion.getObjetivo());
        vista.pintarColorTrazo(colorFlechas);
        elegirEjercicio(Math.min(indice, sesion.numEjercicios() - 1));
    }

    public Sesion getSesion() { return sesion; }

    public boolean guardar(boolean avisar) {
        boolean ok = repositorio.guardar(sesion);
        if (ok) {
            sucio = false;
            esNueva = false;
        }
        if (avisar) vista.mostrarMensaje(ok ? "Sesión guardada" : "No se pudo guardar la sesión");
        return ok;
    }

    /** Al salir de la pantalla: guardado automatico. */
    public void alPausar() {
        if (sucio && hayQueGuardar()) guardar(false);
    }

    /** Una sesion nueva que no se ha tocado no se guarda (evita sesiones vacias). */
    private boolean hayQueGuardar() {
        if (!esNueva) return true;
        if (!sesion.getObjetivo().isEmpty() || sesion.numEjercicios() > 1) return true;
        for (Ejercicio e : sesion.getEjercicios()) {
            if (!e.estaVacio() || !e.getDescripcion().isEmpty() || e.getDuracionMin() > 0) return true;
        }
        return false;
    }

    public void volver() {
        vista.cerrarEditor();
    }

    public void liberar() {
        principal.removeCallbacks(autoguardado);
        hilo.shutdown();
    }

    private void marcarSucio() {
        sucio = true;
        principal.removeCallbacks(autoguardado);
        principal.postDelayed(autoguardado, RETRASO_AUTOGUARDADO_MS);
    }

    // ═════════════════════════════════════════════════════════════════
    //  Datos de la sesion
    // ═════════════════════════════════════════════════════════════════

    public void cambiarNumero(String numero) {
        sesion.setNumero(numero.trim());
        marcarSucio();
    }

    public void cambiarObjetivo(String objetivo) {
        sesion.setObjetivo(objetivo.trim());
        marcarSucio();
    }

    public void alPulsarFecha() {
        Calendar cal = Calendar.getInstance();
        try {
            Date d = new SimpleDateFormat(FORMATO_FECHA, Locale.getDefault()).parse(sesion.getFecha());
            if (d != null) cal.setTime(d);
        } catch (Exception ignored) { }
        vista.mostrarSelectorFecha(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    public void fechaElegida(int anio, int mes, int dia) {
        Calendar elegido = Calendar.getInstance();
        elegido.set(anio, mes, dia);
        String f = new SimpleDateFormat(FORMATO_FECHA, Locale.getDefault()).format(elegido.getTime());
        sesion.setFecha(f);
        vista.pintarFecha(f);
        marcarSucio();
    }

    // ═════════════════════════════════════════════════════════════════
    //  Herramientas
    // ═════════════════════════════════════════════════════════════════

    /** {@code tipo} null = herramienta Mover. */
    public void elegirHerramienta(@Nullable TipoTrazo tipo) {
        if (tipo == null) {
            vista.ponerModoMover();
            return;
        }
        // Las medidas tienen su propio color (azul, como el "35 m" de las diapositivas).
        int color = tipo == TipoTrazo.MEDIDA ? colorMedida : colorFlechas;
        vista.ponerModoDibujo(tipo, color);
        vista.pintarColorTrazo(color);
    }

    public void alPulsarColorTrazo() {
        vista.mostrarSelectorColor("Color de las flechas", vista.getColorTrazo(), color -> {
            if (vista.dibujandoMedidas()) colorMedida = color;
            else colorFlechas = color;
            vista.ponerColorTrazo(color);
            vista.pintarColorTrazo(color);
        });
    }

    public void deshacer() {
        if (historial().deshacer(ejercicioActual())) despuesDeRestaurar();
    }

    public void rehacer() {
        if (historial().rehacer(ejercicioActual())) despuesDeRestaurar();
    }

    public void alPulsarCampo() {
        TipoCampo[] tipos = TipoCampo.values();
        String[] nombres = new String[tipos.length];
        for (int i = 0; i < tipos.length; i++) nombres[i] = tipos[i].getNombre();
        vista.mostrarSelectorCampo(nombres, ejercicioActual().getCampo().ordinal());
    }

    public void cambiarCampo(int posicion) {
        TipoCampo nuevo = TipoCampo.values()[posicion];
        if (nuevo == ejercicioActual().getCampo()) return;
        antesDeCambiar();
        ejercicioActual().setCampo(nuevo);
        despuesDeCambiar();
    }

    public void alPulsarLimpiar() {
        if (ejercicioActual().estaVacio()) return;
        vista.confirmar("Limpiar pizarra",
                "Se quitarán todos los objetos y flechas de este ejercicio. Puedes recuperarlos con Deshacer.",
                "Limpiar", () -> {
                    antesDeCambiar();
                    ejercicioActual().limpiar();
                    vista.deseleccionar();
                    despuesDeCambiar();
                });
    }

    // ═════════════════════════════════════════════════════════════════
    //  Paleta
    // ═════════════════════════════════════════════════════════════════

    /** Toque en la paleta: el objeto aparece cerca del centro. */
    public void agregarDesdePaleta(ItemPaleta item) {
        // Pequeno desplazamiento para que los objetos no queden uno encima de otro.
        int n = ejercicioActual().getElementos().size();
        float dx = ((n % 5) - 2) * 0.03f;
        float dy = (((n / 5) % 5) - 2) * 0.03f;
        agregarObjeto(item, 0.5f + dx, 0.5f + dy);
    }

    private void agregarObjeto(ItemPaleta item, float x, float y) {
        Ejercicio ej = ejercicioActual();
        antesDeCambiar();
        ElementoPizarra e = ej.agregarElemento(item.tipo, x, y, item.color);
        e.setColor2(item.color2);
        if (item.tipo.admiteNumero()) e.setNumero(ej.siguienteNumero(item.color, item.color2));
        if (item.tipo == TipoElemento.TEXTO) e.setTexto("Texto");
        despuesDeCambiar();
        vista.activarHerramientaMover();
        vista.seleccionarElemento(e.getId());
        if (item.tipo == TipoElemento.TEXTO) editarTexto(e);
    }

    // ═════════════════════════════════════════════════════════════════
    //  Objeto o flecha seleccionados
    // ═════════════════════════════════════════════════════════════════

    private interface CambioElemento { void aplicar(ElementoPizarra e); }

    private void modificarElemento(CambioElemento cambio) {
        ElementoPizarra e = vista.getElementoSeleccionado();
        if (e == null) return;
        antesDeCambiar();
        cambio.aplicar(e);
        despuesDeCambiar();
    }

    public void girarSeleccion(float grados) {
        modificarElemento(e -> e.girar(grados));
    }

    /** Agranda/encoge el objeto, o engorda/adelgaza la flecha. */
    public void escalarSeleccion(float factor) {
        ElementoPizarra e = vista.getElementoSeleccionado();
        Trazo t = vista.getTrazoSeleccionado();
        if (e == null && t == null) return;
        antesDeCambiar();
        if (e != null) e.setEscala(e.getEscala() * factor);
        else t.setGrosor(t.getGrosor() * factor);
        despuesDeCambiar();
    }

    public void alPulsarColorSeleccion() {
        ElementoPizarra e = vista.getElementoSeleccionado();
        Trazo t = vista.getTrazoSeleccionado();
        if (e != null) {
            boolean caja = e.getTipo() == TipoElemento.TEXTO && e.getColor2() != ElementoPizarra.SIN_COLOR;
            int actual = caja ? e.getColor2() : e.getColor();
            vista.mostrarSelectorColor(caja ? "Color de la etiqueta" : "Color", actual, c ->
                    modificarElemento(el -> {
                        if (caja) {
                            el.setColor2(c);
                        } else {
                            el.setColor(c);
                            // Un jugador bicolor pasa a ser de un solo color.
                            if (el.getTipo() == TipoElemento.JUGADOR) el.setColor2(ElementoPizarra.SIN_COLOR);
                        }
                    }));
        } else if (t != null) {
            vista.mostrarSelectorColor("Color de la flecha", t.getColor(), c -> {
                antesDeCambiar();
                t.setColor(c);
                despuesDeCambiar();
            });
        }
    }

    public void alPulsarEditarSeleccion() {
        alDobleToque(vista.getElementoSeleccionado(), vista.getTrazoSeleccionado());
    }

    public void duplicarSeleccion() {
        ElementoPizarra e = vista.getElementoSeleccionado();
        if (e == null) return;
        antesDeCambiar();
        ElementoPizarra copia = ejercicioActual().duplicarElemento(e.getId());
        despuesDeCambiar();
        if (copia != null) vista.seleccionarElemento(copia.getId());
    }

    public void eliminarSeleccion() {
        ElementoPizarra e = vista.getElementoSeleccionado();
        Trazo t = vista.getTrazoSeleccionado();
        if (e == null && t == null) return;
        antesDeCambiar();
        if (e != null) ejercicioActual().eliminarElemento(e.getId());
        else ejercicioActual().eliminarTrazo(t.getId());
        vista.deseleccionar();
        despuesDeCambiar();
    }

    private void editarTexto(ElementoPizarra e) {
        vista.pedirTexto("Texto", e.getTexto(), true, false, texto -> {
            if (texto.trim().isEmpty()) return;
            modificarElemento(el -> el.setTexto(texto.trim()));
        });
    }

    // ═════════════════════════════════════════════════════════════════
    //  Ejercicios de la sesion
    // ═════════════════════════════════════════════════════════════════

    private Ejercicio ejercicioActual() {
        return sesion.getEjercicio(indice);
    }

    private Historial historial() {
        Ejercicio e = ejercicioActual();
        Historial h = historiales.get(e.getId());
        if (h == null) {
            h = new Historial();
            historiales.put(e.getId(), h);
        }
        return h;
    }

    public void elegirEjercicio(int i) {
        indice = Math.max(0, Math.min(i, sesion.numEjercicios() - 1));
        vista.pintarEjercicio(ejercicioActual(), indice);
        vista.pintarInfoEjercicio(textoInfoEjercicio());
        actualizarBotonesHistorial();
        vista.pintarDuracionTotal(sesion.duracionTotal());
    }

    public void nuevoEjercicio() {
        Ejercicio nuevo = sesion.agregarEjercicio();
        nuevo.setCampo(ejercicioActual().getCampo());
        vista.ejercicioInsertado(sesion.numEjercicios() - 1);
        marcarSucio();
        elegirEjercicio(sesion.numEjercicios() - 1);
    }

    /** Pulsacion larga en la miniatura de un ejercicio. */
    public void alMantenerEjercicio(int i, View ancla) {
        int n = sesion.numEjercicios();
        vista.mostrarMenuEjercicio(i, ancla, i > 0, i < n - 1, n > 1);
    }

    public void duplicarEjercicio(int i) {
        if (sesion.duplicarEjercicio(i) == null) return;
        vista.ejercicioInsertado(i + 1);
        marcarSucio();
        elegirEjercicio(i + 1);
    }

    public void moverEjercicio(int desde, int hasta) {
        if (!sesion.moverEjercicio(desde, hasta)) return;
        vista.ejercicioMovido(desde, hasta);
        marcarSucio();
        elegirEjercicio(hasta);
    }

    public void alPulsarEliminarEjercicio(int i) {
        Ejercicio e = sesion.getEjercicio(i);
        String nombre = e.getTitulo().isEmpty() ? "Ejercicio " + (i + 1) : e.getTitulo();
        vista.confirmar("Eliminar ejercicio", "¿Eliminar «" + nombre + "»? No se puede deshacer.",
                "Eliminar", () -> {
                    if (sesion.eliminarEjercicio(i)) {
                        historiales.remove(e.getId());
                        vista.ejercicioEliminado(i, sesion.numEjercicios());
                        marcarSucio();
                        elegirEjercicio(Math.min(i, sesion.numEjercicios() - 1));
                    }
                });
    }

    /** Toque en la etiqueta del ejercicio: titulo, minutos y explicacion. */
    public void alPulsarInfoEjercicio() {
        Ejercicio ej = ejercicioActual();
        vista.mostrarDialogoDatosEjercicio(indice + 1, ej.getTitulo(),
                ej.getDuracionMin() > 0 ? String.valueOf(ej.getDuracionMin()) : "", ej.getDescripcion());
    }

    public void guardarDatosEjercicio(String titulo, String minutos, String explicacion) {
        Ejercicio ej = ejercicioActual();
        ej.setTitulo(titulo.trim());
        int min = 0;
        try { min = Integer.parseInt(minutos.trim()); } catch (NumberFormatException ignored) { }
        ej.setDuracionMin(min);
        ej.setDescripcion(explicacion.trim());
        marcarSucio();
        vista.refrescarMiniatura();
        vista.pintarInfoEjercicio(textoInfoEjercicio());
        vista.pintarDuracionTotal(sesion.duracionTotal());
    }

    private String textoInfoEjercicio() {
        Ejercicio ej = ejercicioActual();
        String titulo = ej.getTitulo().isEmpty() ? "Ejercicio " + (indice + 1) : ej.getTitulo();
        String min = ej.getDuracionMin() > 0 ? "  ·  " + ej.getDuracionMin() + "'" : "";
        return "✏  " + (indice + 1) + ". " + titulo + min;
    }

    // ═════════════════════════════════════════════════════════════════
    //  Exportar
    // ═════════════════════════════════════════════════════════════════

    public void exportarPdf() {
        guardar(false);
        final Sesion copia = sesion.duplicar(sesion.getModificada());
        vista.mostrarMensaje("Generando PDF…");
        hilo.execute(() -> {
            try {
                File f = ExportadorSesion.exportarPdf(contextoApp, copia);
                principal.post(() -> vista.compartirArchivo(f, "application/pdf"));
            } catch (Exception ex) {
                Log.e(TAG, "Error exportando PDF", ex);
                principal.post(() -> vista.mostrarMensaje("No se pudo crear el PDF"));
            }
        });
    }

    public void exportarImagen() {
        guardar(false);
        try {
            File f = ExportadorSesion.exportarImagen(contextoApp, sesion, indice);
            vista.compartirArchivo(f, "image/png");
        } catch (Exception ex) {
            Log.e(TAG, "Error exportando imagen", ex);
            vista.mostrarMensaje("No se pudo crear la imagen");
        }
    }

    // ═════════════════════════════════════════════════════════════════
    //  PizarraView.Listener (gestos sobre la pizarra)
    // ═════════════════════════════════════════════════════════════════

    @Override
    public void antesDeCambiar() {
        historial().registrar(ejercicioActual());
    }

    @Override
    public void despuesDeCambiar() {
        marcarSucio();
        vista.refrescarPizarra();
        actualizarBotonesHistorial();
        vista.pintarBarraSeleccion(vista.getElementoSeleccionado(), vista.getTrazoSeleccionado());
    }

    @Override
    public void alSeleccionar(@Nullable ElementoPizarra elemento, @Nullable Trazo trazo) {
        vista.pintarBarraSeleccion(elemento, trazo);
    }

    @Override
    public void alDobleToque(@Nullable ElementoPizarra e, @Nullable Trazo t) {
        if (e != null) {
            if (e.getTipo() == TipoElemento.TEXTO) {
                editarTexto(e);
            } else if (e.getTipo().admiteNumero() || e.getTipo() == TipoElemento.CAMISETA) {
                vista.pedirTexto("Dorsal (vacío = sin número)",
                        e.getNumero() > 0 ? String.valueOf(e.getNumero()) : "", false, true, texto -> {
                            int n = 0;
                            try { n = Integer.parseInt(texto.trim()); } catch (NumberFormatException ignored) { }
                            final int dorsal = Math.min(n, 99);
                            modificarElemento(el -> el.setNumero(dorsal));
                        });
            }
        } else if (t != null && t.getTipo() == TipoTrazo.MEDIDA) {
            alCrearMedida(t);
        }
    }

    @Override
    public void alCrearMedida(Trazo trazo) {
        vista.pedirTexto("Medida (p. ej. 35 m)", trazo.getTexto(), false, false, texto -> {
            antesDeCambiar();
            trazo.setTexto(texto.trim());
            despuesDeCambiar();
        });
    }

    @Override
    public void alSoltarDesdePaleta(ItemPaleta item, float x, float y) {
        agregarObjeto(item, x, y);
    }

    // ── Ayudas ───────────────────────────────────────────────────────

    private void despuesDeRestaurar() {
        vista.deseleccionar();
        marcarSucio();
        vista.refrescarPizarra();
        actualizarBotonesHistorial();
    }

    private void actualizarBotonesHistorial() {
        vista.pintarBotonesHistorial(historial().puedeDeshacer(), historial().puedeRehacer());
    }
}
