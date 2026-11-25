package com.entrenador.pizarratactica.view.pizarra;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.entrenador.pizarratactica.model.pizarra.ElementoPizarra;
import com.entrenador.pizarratactica.model.pizarra.Ejercicio;
import com.entrenador.pizarratactica.model.pizarra.TipoTrazo;
import com.entrenador.pizarratactica.model.pizarra.Trazo;

import java.util.List;

/**
 * Pizarra interactiva de un ejercicio.
 * <ul>
 *   <li>Modo MOVER: arrastrar objetos y flechas, girar/escalar con dos dedos,
 *       mover los extremos de una flecha seleccionada, doble toque para editar.</li>
 *   <li>Modo DIBUJAR: crear flechas, lineas, medidas o dibujo libre.</li>
 *   <li>Recibe objetos arrastrados desde la paleta (drag and drop).</li>
 * </ul>
 * Los cambios se notifican al {@link Listener} para guardar el historial.
 */
public class PizarraView extends View {

    public enum Modo { MOVER, DIBUJAR }

    public interface Listener {
        /** Se va a modificar el ejercicio: guardar instantanea para deshacer. */
        void antesDeCambiar();
        /** El ejercicio ha cambiado. */
        void despuesDeCambiar();
        /** Cambio de seleccion (ambos null = nada seleccionado). */
        void alSeleccionar(@Nullable ElementoPizarra elemento, @Nullable Trazo trazo);
        /** Doble toque sobre un objeto o flecha (editar texto, dorsal...). */
        void alDobleToque(@Nullable ElementoPizarra elemento, @Nullable Trazo trazo);
        /** Nueva medida dibujada: pedir la etiqueta ("35 m"). */
        void alCrearMedida(Trazo trazo);
        /** Objeto soltado desde la paleta en coordenadas normalizadas. */
        void alSoltarDesdePaleta(ItemPaleta item, float x, float y);
    }

    private final RenderizadorPizarra render = new RenderizadorPizarra();
    private final Paint paintSeleccion = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintAsa = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final GestureDetector gestos;
    private final float dp;

    private Ejercicio ejercicio;
    private RectF tablero = new RectF();
    private Listener listener;

    private Modo modo = Modo.MOVER;
    private TipoTrazo tipoTrazo = TipoTrazo.PASE;
    private int colorTrazo = Color.BLACK;

    private long idElementoSel = -1;
    private long idTrazoSel = -1;

    // Estado del gesto en curso
    private enum Gesto { NINGUNO, ARRASTRAR_ELEMENTO, ARRASTRAR_TRAZO, MOVER_EXTREMO, PELLIZCO, DIBUJANDO }
    private Gesto gesto = Gesto.NINGUNO;
    private boolean cambioRegistrado;
    private float ultimoX, ultimoY;        // normalizados
    private float offX, offY;              // dedo -> centro del objeto
    private int extremo;                   // indice del punto que se arrastra
    private float distInicial, anguloInicial, escalaInicial, rotacionInicial;
    private Trazo trazoEnCurso;
    private float bajadaX, bajadaY;        // pixeles, para distinguir toque de arrastre

    // Objeto que se esta arrastrando desde la paleta (para pintar la sombra de destino)
    private boolean recibiendoArrastre;

    public PizarraView(Context context) { this(context, null); }

    public PizarraView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        dp = getResources().getDisplayMetrics().density;
        paintSeleccion.setStyle(Paint.Style.STROKE);
        paintSeleccion.setStrokeWidth(2f * dp);
        paintSeleccion.setPathEffect(new DashPathEffect(new float[]{6 * dp, 4 * dp}, 0));
        paintAsa.setStyle(Paint.Style.FILL);
        gestos = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (modo != Modo.MOVER || listener == null) return false;
                ElementoPizarra el = elementoEn(e.getX(), e.getY());
                if (el != null) {
                    listener.alDobleToque(el, null);
                    return true;
                }
                Trazo t = trazoEn(e.getX(), e.getY());
                if (t != null) {
                    listener.alDobleToque(null, t);
                    return true;
                }
                return false;
            }
        });
        setOnDragListener(this::alArrastrarDesdePaleta);
        setContentDescription("Pizarra del ejercicio");
    }

    // ── API publica ──────────────────────────────────────────────────

    public void setListener(Listener l) { this.listener = l; }

    public void setEjercicio(Ejercicio ej) {
        this.ejercicio = ej;
        deseleccionar();
        invalidate();
    }

    public Ejercicio getEjercicio() { return ejercicio; }

    public void setModo(Modo modo) {
        this.modo = modo;
        if (modo == Modo.DIBUJAR) deseleccionar();
        invalidate();
    }

    public Modo getModo() { return modo; }
    public void setTipoTrazo(TipoTrazo t) { this.tipoTrazo = t; }
    public TipoTrazo getTipoTrazo() { return tipoTrazo; }
    public void setColorTrazo(int c) { this.colorTrazo = c; }
    public int getColorTrazo() { return colorTrazo; }

    public void seleccionarElemento(long id) {
        idElementoSel = id;
        idTrazoSel = -1;
        notificarSeleccion();
        invalidate();
    }

    public void seleccionarTrazo(long id) {
        idTrazoSel = id;
        idElementoSel = -1;
        notificarSeleccion();
        invalidate();
    }

    public void deseleccionar() {
        boolean habia = idElementoSel != -1 || idTrazoSel != -1;
        idElementoSel = -1;
        idTrazoSel = -1;
        if (habia) notificarSeleccion();
        invalidate();
    }

    @Nullable
    public ElementoPizarra getElementoSeleccionado() {
        return ejercicio == null ? null : ejercicio.buscarElemento(idElementoSel);
    }

    @Nullable
    public Trazo getTrazoSeleccionado() {
        return ejercicio == null ? null : ejercicio.buscarTrazo(idTrazoSel);
    }

    /** Centro visible del campo en coordenadas normalizadas (siempre 0.5, 0.5). */
    public float[] centro() { return new float[]{0.5f, 0.5f}; }

    // ── Dibujo ───────────────────────────────────────────────────────

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        tablero = RenderizadorPizarra.calcularTablero(w, h);
    }

    @Override
    protected void onDraw(@NonNull Canvas c) {
        super.onDraw(c);
        c.drawColor(0xFF2B3A2C);
        if (ejercicio == null) return;
        render.dibujarEjercicio(c, ejercicio, tablero);
        if (trazoEnCurso != null) render.dibujarTrazo(c, trazoEnCurso, tablero);
        dibujarSeleccion(c);
        if (recibiendoArrastre) {
            paintSeleccion.setColor(0xFFFFEB3B);
            c.drawRect(tablero, paintSeleccion);
        }
    }

    private void dibujarSeleccion(Canvas c) {
        ElementoPizarra e = getElementoSeleccionado();
        if (e != null) {
            float[] m = render.medidas(e, tablero);
            float cx = tablero.left + e.getX() * tablero.width();
            float cy = tablero.top + e.getY() * tablero.height();
            float pad = 6 * dp;
            RectF r = new RectF(-m[0] / 2f - pad, -m[1] / 2f - pad, m[0] / 2f + pad, m[1] / 2f + pad);
            c.save();
            c.translate(cx, cy);
            c.rotate(e.getRotacion());
            paintSeleccion.setColor(0xFF000000);
            paintSeleccion.setPathEffect(null);
            paintSeleccion.setStrokeWidth(4f * dp);
            c.drawRoundRect(r, 6 * dp, 6 * dp, paintSeleccion);
            paintSeleccion.setColor(0xFFFFEB3B);
            paintSeleccion.setStrokeWidth(2f * dp);
            paintSeleccion.setPathEffect(new DashPathEffect(new float[]{6 * dp, 4 * dp}, 0));
            c.drawRoundRect(r, 6 * dp, 6 * dp, paintSeleccion);
            c.restore();
        }
        Trazo t = getTrazoSeleccionado();
        if (t != null) {
            List<float[]> pts = t.getPuntos();
            paintAsa.setColor(0xFFFFEB3B);
            if (t.getTipo().esLibre()) {
                for (int i = 0; i < pts.size(); i += Math.max(1, pts.size() / 12)) {
                    float[] p = aPixel(pts.get(i));
                    c.drawCircle(p[0], p[1], 3 * dp, paintAsa);
                }
            } else {
                for (float[] pn : pts) {
                    float[] p = aPixel(pn);
                    paintAsa.setColor(0xFF000000);
                    c.drawCircle(p[0], p[1], 10 * dp, paintAsa);
                    paintAsa.setColor(0xFFFFEB3B);
                    c.drawCircle(p[0], p[1], 8 * dp, paintAsa);
                }
            }
        }
    }

    // ── Tactil ───────────────────────────────────────────────────────

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ejercicio == null) return false;
        gestos.onTouchEvent(ev);
        getParent().requestDisallowInterceptTouchEvent(true);
        if (modo == Modo.DIBUJAR) return tocarDibujando(ev);
        return tocarMoviendo(ev);
    }

    private boolean tocarMoviendo(MotionEvent ev) {
        float x = ev.getX(), y = ev.getY();
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                cambioRegistrado = false;
                bajadaX = x;
                bajadaY = y;
                float[] n = aNormal(x, y);
                ultimoX = n[0];
                ultimoY = n[1];

                // 1) Extremo de la flecha seleccionada
                Trazo sel = getTrazoSeleccionado();
                if (sel != null && !sel.getTipo().esLibre()) {
                    int idx = extremoCercano(sel, x, y);
                    if (idx >= 0) {
                        extremo = idx;
                        gesto = Gesto.MOVER_EXTREMO;
                        return true;
                    }
                }
                // 2) Objetos (el de arriba primero)
                ElementoPizarra e = elementoEn(x, y);
                if (e != null) {
                    if (e.getId() != idElementoSel) seleccionarElemento(e.getId());
                    offX = e.getX() - n[0];
                    offY = e.getY() - n[1];
                    gesto = Gesto.ARRASTRAR_ELEMENTO;
                    return true;
                }
                // 3) Flechas
                Trazo t = trazoEn(x, y);
                if (t != null) {
                    if (t.getId() != idTrazoSel) seleccionarTrazo(t.getId());
                    gesto = Gesto.ARRASTRAR_TRAZO;
                    return true;
                }
                deseleccionar();
                gesto = Gesto.NINGUNO;
                return true;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                ElementoPizarra e = getElementoSeleccionado();
                if (e != null && ev.getPointerCount() == 2) {
                    registrarCambio();
                    distInicial = distancia(ev);
                    anguloInicial = angulo(ev);
                    escalaInicial = e.getEscala();
                    rotacionInicial = e.getRotacion();
                    gesto = Gesto.PELLIZCO;
                }
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                float[] n = aNormal(x, y);
                boolean seMovio = Math.hypot(x - bajadaX, y - bajadaY) > 6 * dp;
                switch (gesto) {
                    case ARRASTRAR_ELEMENTO: {
                        if (!seMovio && !cambioRegistrado) return true;
                        ElementoPizarra e = getElementoSeleccionado();
                        if (e == null) return true;
                        registrarCambio();
                        e.mover(n[0] + offX, n[1] + offY);
                        break;
                    }
                    case ARRASTRAR_TRAZO: {
                        if (!seMovio && !cambioRegistrado) return true;
                        Trazo t = getTrazoSeleccionado();
                        if (t == null) return true;
                        registrarCambio();
                        t.desplazar(n[0] - ultimoX, n[1] - ultimoY);
                        break;
                    }
                    case MOVER_EXTREMO: {
                        Trazo t = getTrazoSeleccionado();
                        if (t == null) return true;
                        registrarCambio();
                        t.moverPunto(extremo, n[0], n[1]);
                        break;
                    }
                    case PELLIZCO: {
                        ElementoPizarra e = getElementoSeleccionado();
                        if (e == null || ev.getPointerCount() < 2 || distInicial <= 0) return true;
                        e.setEscala(escalaInicial * distancia(ev) / distInicial);
                        e.setRotacion(rotacionInicial + angulo(ev) - anguloInicial);
                        break;
                    }
                    default:
                        return true;
                }
                ultimoX = n[0];
                ultimoY = n[1];
                invalidate();
                return true;
            }
            case MotionEvent.ACTION_POINTER_UP:
                if (gesto == Gesto.PELLIZCO) gesto = Gesto.NINGUNO;
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (cambioRegistrado && listener != null) listener.despuesDeCambiar();
                cambioRegistrado = false;
                gesto = Gesto.NINGUNO;
                invalidate();
                return true;
        }
        return true;
    }

    private boolean tocarDibujando(MotionEvent ev) {
        float[] n = aNormal(ev.getX(), ev.getY());
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                trazoEnCurso = new Trazo(0, tipoTrazo, colorTrazo);
                trazoEnCurso.agregarPunto(n[0], n[1]);
                trazoEnCurso.agregarPunto(n[0], n[1]);
                gesto = Gesto.DIBUJANDO;
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (trazoEnCurso == null) return true;
                if (tipoTrazo.esLibre()) {
                    float[] ult = trazoEnCurso.getPuntos().get(trazoEnCurso.numPuntos() - 1);
                    if (Math.hypot(n[0] - ult[0], n[1] - ult[1]) > 0.004) {
                        trazoEnCurso.agregarPunto(n[0], n[1]);
                    }
                } else {
                    trazoEnCurso.moverPunto(1, n[0], n[1]);
                }
                invalidate();
                return true;
            case MotionEvent.ACTION_UP: {
                Trazo t = trazoEnCurso;
                trazoEnCurso = null;
                gesto = Gesto.NINGUNO;
                if (t != null && t.longitud() > 0.015f) {
                    if (listener != null) listener.antesDeCambiar();
                    ejercicio.agregarTrazoExistente(t);
                    if (listener != null) {
                        listener.despuesDeCambiar();
                        if (t.getTipo() == TipoTrazo.MEDIDA) listener.alCrearMedida(t);
                    }
                }
                invalidate();
                return true;
            }
            case MotionEvent.ACTION_CANCEL:
                trazoEnCurso = null;
                gesto = Gesto.NINGUNO;
                invalidate();
                return true;
        }
        return true;
    }

    private void registrarCambio() {
        if (!cambioRegistrado) {
            cambioRegistrado = true;
            if (listener != null) listener.antesDeCambiar();
        }
    }

    // ── Arrastrar desde la paleta ────────────────────────────────────

    private boolean alArrastrarDesdePaleta(View v, DragEvent ev) {
        Object estado = ev.getLocalState();
        if (!(estado instanceof ItemPaleta)) return false;
        switch (ev.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
                recibiendoArrastre = true;
                invalidate();
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
            case DragEvent.ACTION_DRAG_ENDED:
                recibiendoArrastre = false;
                invalidate();
                return true;
            case DragEvent.ACTION_DROP: {
                recibiendoArrastre = false;
                float[] n = aNormal(ev.getX(), ev.getY());
                if (listener != null) listener.alSoltarDesdePaleta((ItemPaleta) estado, n[0], n[1]);
                invalidate();
                return true;
            }
        }
        return true;
    }

    // ── Busqueda bajo el dedo ────────────────────────────────────────

    @Nullable
    private ElementoPizarra elementoEn(float x, float y) {
        List<ElementoPizarra> lista = ejercicio.getElementos();
        float minimo = 22 * dp; // area tactil minima
        for (int i = lista.size() - 1; i >= 0; i--) {
            ElementoPizarra e = lista.get(i);
            float[] m = render.medidas(e, tablero);
            float cx = tablero.left + e.getX() * tablero.width();
            float cy = tablero.top + e.getY() * tablero.height();
            double rad = Math.toRadians(-e.getRotacion());
            float dx = x - cx, dy = y - cy;
            float lx = (float) (dx * Math.cos(rad) - dy * Math.sin(rad));
            float ly = (float) (dx * Math.sin(rad) + dy * Math.cos(rad));
            float mw = Math.max(m[0] / 2f, minimo / 2f) + 4 * dp;
            float mh = Math.max(m[1] / 2f, minimo / 2f) + 4 * dp;
            if (Math.abs(lx) <= mw && Math.abs(ly) <= mh) return e;
        }
        return null;
    }

    @Nullable
    private Trazo trazoEn(float x, float y) {
        List<Trazo> lista = ejercicio.getTrazos();
        float tolerancia = 14 * dp;
        for (int i = lista.size() - 1; i >= 0; i--) {
            Trazo t = lista.get(i);
            List<float[]> pts = t.getPuntos();
            for (int k = 1; k < pts.size(); k++) {
                float[] a = aPixel(pts.get(k - 1)), b = aPixel(pts.get(k));
                if (distanciaASegmento(x, y, a, b) <= tolerancia) return t;
            }
        }
        return null;
    }

    private int extremoCercano(Trazo t, float x, float y) {
        List<float[]> pts = t.getPuntos();
        int[] indices = {pts.size() - 1, 0};
        for (int i : indices) {
            float[] p = aPixel(pts.get(i));
            if (Math.hypot(x - p[0], y - p[1]) <= 20 * dp) return i;
        }
        return -1;
    }

    static float distanciaASegmento(float px, float py, float[] a, float[] b) {
        float dx = b[0] - a[0], dy = b[1] - a[1];
        float l2 = dx * dx + dy * dy;
        float t = l2 == 0 ? 0 : Math.max(0, Math.min(1, ((px - a[0]) * dx + (py - a[1]) * dy) / l2));
        float cx = a[0] + t * dx, cy = a[1] + t * dy;
        return (float) Math.hypot(px - cx, py - cy);
    }

    // ── Conversiones ─────────────────────────────────────────────────

    private float[] aNormal(float x, float y) {
        if (tablero.width() <= 0) return new float[]{0.5f, 0.5f};
        return new float[]{
                Math.max(0, Math.min(1, (x - tablero.left) / tablero.width())),
                Math.max(0, Math.min(1, (y - tablero.top) / tablero.height()))};
    }

    private float[] aPixel(float[] n) {
        return new float[]{tablero.left + n[0] * tablero.width(), tablero.top + n[1] * tablero.height()};
    }

    private static float distancia(MotionEvent ev) {
        return (float) Math.hypot(ev.getX(0) - ev.getX(1), ev.getY(0) - ev.getY(1));
    }

    private static float angulo(MotionEvent ev) {
        return (float) Math.toDegrees(Math.atan2(ev.getY(1) - ev.getY(0), ev.getX(1) - ev.getX(0)));
    }

    private void notificarSeleccion() {
        if (listener != null) listener.alSeleccionar(getElementoSeleccionado(), getTrazoSeleccionado());
    }
}
