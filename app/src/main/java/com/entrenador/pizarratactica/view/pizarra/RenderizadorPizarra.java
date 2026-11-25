package com.entrenador.pizarratactica.view.pizarra;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;

import com.entrenador.pizarratactica.model.pizarra.Colores;
import com.entrenador.pizarratactica.model.pizarra.ElementoPizarra;
import com.entrenador.pizarratactica.model.pizarra.Ejercicio;
import com.entrenador.pizarratactica.model.pizarra.TipoCampo;
import com.entrenador.pizarratactica.model.pizarra.TipoElemento;
import com.entrenador.pizarratactica.model.pizarra.Trazo;

import java.util.List;

/**
 * Dibuja el campo, el material, los jugadores y las flechas sobre un Canvas.
 * <p>
 * Se usa en la pizarra interactiva, en las miniaturas de la lista de sesiones
 * y en la exportacion a PDF/imagen, asi que todo se dibuja igual en todos sitios.
 * Todas las medidas se calculan a partir del ancho del tablero ({@code u}).
 */
public class RenderizadorPizarra {

    /** Proporcion del tablero (como las diapositivas: 4:3). */
    public static final float PROPORCION = 4f / 3f;

    private final Paint relleno = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint trazo = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint texto = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private final RectF rect = new RectF();
    private final Rect bounds = new Rect();

    public RenderizadorPizarra() {
        relleno.setStyle(Paint.Style.FILL);
        trazo.setStyle(Paint.Style.STROKE);
        trazo.setStrokeCap(Paint.Cap.ROUND);
        trazo.setStrokeJoin(Paint.Join.ROUND);
        texto.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        texto.setTextAlign(Paint.Align.CENTER);
    }

    // ═════════════════════════════════════════════════════════════════
    //  Tablero
    // ═════════════════════════════════════════════════════════════════

    /** Rectangulo 4:3 mas grande que cabe en w x h, centrado. */
    public static RectF calcularTablero(float w, float h) {
        float tw = w, th = w / PROPORCION;
        if (th > h) {
            th = h;
            tw = h * PROPORCION;
        }
        float left = (w - tw) / 2f, top = (h - th) / 2f;
        return new RectF(left, top, left + tw, top + th);
    }

    /** Dibuja el ejercicio completo: campo, luego lineas y encima los objetos. */
    public void dibujarEjercicio(Canvas c, Ejercicio ej, RectF tablero) {
        dibujarCampo(c, ej.getCampo(), tablero);
        for (Trazo t : ej.getTrazos()) dibujarTrazo(c, t, tablero);
        for (ElementoPizarra e : ej.getElementos()) dibujarElemento(c, e, tablero);
    }

    // ═════════════════════════════════════════════════════════════════
    //  Campo
    // ═════════════════════════════════════════════════════════════════

    public void dibujarCampo(Canvas c, TipoCampo tipo, RectF t) {
        float u = t.width();
        boolean franjasVerticales = tipo != TipoCampo.MEDIO;
        int franjas = 18;
        relleno.setShader(null);
        for (int i = 0; i < franjas; i++) {
            relleno.setColor(i % 2 == 0 ? 0xFF4E8B2E : 0xFF5C9A38);
            if (franjasVerticales) {
                float a = t.left + t.width() * i / franjas;
                c.drawRect(a, t.top, t.left + t.width() * (i + 1) / franjas, t.bottom, relleno);
            } else {
                float a = t.top + t.height() * i / franjas;
                c.drawRect(t.left, a, t.right, t.top + t.height() * (i + 1) / franjas, relleno);
            }
        }
        if (tipo == TipoCampo.SIN_LINEAS) return;

        trazo.setPathEffect(null);
        trazo.setShader(null);
        trazo.setColor(0xE6F5F5F5);
        trazo.setStrokeWidth(Math.max(1f, u * 0.0035f));
        relleno.setColor(0xE6F5F5F5);

        float m = u * 0.022f;
        RectF f = new RectF(t.left + m, t.top + m, t.right - m, t.bottom - m);
        if (tipo == TipoCampo.COMPLETO) dibujarCampoCompleto(c, f);
        else dibujarMedioCampo(c, f);
    }

    /** Campo entero en horizontal (105 x 68 m). */
    private void dibujarCampoCompleto(Canvas c, RectF f) {
        float sx = f.width() / 105f, sy = f.height() / 68f;
        float cx = f.centerX(), cy = f.centerY();
        float punto = Math.max(1.5f, f.width() * 0.0035f);
        c.drawRect(f, trazo);
        c.drawLine(cx, f.top, cx, f.bottom, trazo);
        rect.set(cx - 9.15f * sx, cy - 9.15f * sy, cx + 9.15f * sx, cy + 9.15f * sy);
        c.drawOval(rect, trazo);
        c.drawCircle(cx, cy, punto, relleno);

        for (int lado = 0; lado < 2; lado++) {
            boolean izq = lado == 0;
            float linea = izq ? f.left : f.right;
            float dir = izq ? 1 : -1;
            // Area grande, area pequena, punto de penalti y porteria.
            rectArea(linea, dir, 16.5f * sx, 40.32f * sy, cy);
            c.drawRect(rect, trazo);
            rectArea(linea, dir, 5.5f * sx, 18.32f * sy, cy);
            c.drawRect(rect, trazo);
            float penalti = linea + dir * 11f * sx;
            c.drawCircle(penalti, cy, punto, relleno);
            rect.set(penalti - 9.15f * sx, cy - 9.15f * sy, penalti + 9.15f * sx, cy + 9.15f * sy);
            // Semicirculo fuera del area: ~53 grados a cada lado.
            if (izq) c.drawArc(rect, -53, 106, false, trazo);
            else c.drawArc(rect, 127, 106, false, trazo);
            rectArea(linea, -dir, 2f * sx, 7.32f * sy, cy);
            c.drawRect(rect, trazo);
        }
        dibujarCorners(c, f, sx, sy);
    }

    /** Medio campo en vertical, porteria arriba (como las diapositivas de area). */
    private void dibujarMedioCampo(Canvas c, RectF f) {
        float sx = f.width() / 68f, sy = f.height() / 52.5f;
        float cx = f.centerX();
        float punto = Math.max(1.5f, f.width() * 0.0035f);
        c.drawRect(f, trazo);
        // Area grande y pequena.
        rect.set(cx - 20.16f * sx, f.top, cx + 20.16f * sx, f.top + 16.5f * sy);
        c.drawRect(rect, trazo);
        rect.set(cx - 9.16f * sx, f.top, cx + 9.16f * sx, f.top + 5.5f * sy);
        c.drawRect(rect, trazo);
        float penalti = f.top + 11f * sy;
        c.drawCircle(cx, penalti, punto, relleno);
        rect.set(cx - 9.15f * sx, penalti - 9.15f * sy, cx + 9.15f * sx, penalti + 9.15f * sy);
        c.drawArc(rect, 37, 106, false, trazo);
        // Porteria por fuera de la linea de fondo.
        rect.set(cx - 3.66f * sx, f.top - 2f * sy, cx + 3.66f * sx, f.top);
        c.drawRect(rect, trazo);
        // Circulo central (mitad) en la linea de medio campo.
        rect.set(cx - 9.15f * sx, f.bottom - 9.15f * sy, cx + 9.15f * sx, f.bottom + 9.15f * sy);
        c.drawArc(rect, 180, 180, false, trazo);
        c.drawCircle(cx, f.bottom, punto, relleno);
        // Corners superiores.
        float r = 1.2f * Math.min(sx, sy);
        rect.set(f.left - r, f.top - r, f.left + r, f.top + r);
        c.drawArc(rect, 0, 90, false, trazo);
        rect.set(f.right - r, f.top - r, f.right + r, f.top + r);
        c.drawArc(rect, 90, 90, false, trazo);
    }

    private void rectArea(float linea, float dir, float fondo, float ancho, float cy) {
        float a = linea, b = linea + dir * fondo;
        rect.set(Math.min(a, b), cy - ancho / 2f, Math.max(a, b), cy + ancho / 2f);
    }

    private void dibujarCorners(Canvas c, RectF f, float sx, float sy) {
        float r = 1.2f * Math.min(sx, sy);
        rect.set(f.left - r, f.top - r, f.left + r, f.top + r);
        c.drawArc(rect, 0, 90, false, trazo);
        rect.set(f.right - r, f.top - r, f.right + r, f.top + r);
        c.drawArc(rect, 90, 90, false, trazo);
        rect.set(f.left - r, f.bottom - r, f.left + r, f.bottom + r);
        c.drawArc(rect, 270, 90, false, trazo);
        rect.set(f.right - r, f.bottom - r, f.right + r, f.bottom + r);
        c.drawArc(rect, 180, 90, false, trazo);
    }

    // ═════════════════════════════════════════════════════════════════
    //  Elementos
    // ═════════════════════════════════════════════════════════════════

    /** Tamano base en pixeles de un elemento dentro del tablero. */
    public static float tamano(ElementoPizarra e, RectF tablero) {
        return e.getTipo().getTamanoBase() * e.getEscala() * tablero.width();
    }

    /**
     * Ancho y alto (sin rotar) que ocupa el elemento, para seleccionarlo con el dedo.
     */
    public float[] medidas(ElementoPizarra e, RectF tablero) {
        float s = tamano(e, tablero);
        switch (e.getTipo()) {
            case PICA: return new float[]{s * 0.25f, s};
            case PICA_BASE: return new float[]{s * 0.6f, s};
            case PORTERIA: return new float[]{s, s * 0.25f};
            case ESCALERA: return new float[]{s, s * 0.2f};
            case BOSU: return new float[]{s, s * 0.55f};
            case CAMISETA: return new float[]{s, s};
            case BALONES: return new float[]{s, s * 0.7f};
            case TEXTO: return medidasTexto(e, s);
            default: return new float[]{s, s};
        }
    }

    private float[] medidasTexto(ElementoPizarra e, float s) {
        configurarTexto(e, s);
        String[] lineas = lineas(e);
        float ancho = 0;
        for (String l : lineas) ancho = Math.max(ancho, texto.measureText(l));
        float alto = lineas.length * texto.getFontSpacing();
        float pad = esCajaTexto(e) ? s * 0.5f : s * 0.15f;
        return new float[]{ancho + pad * 2, alto + pad};
    }

    public void dibujarElemento(Canvas c, ElementoPizarra e, RectF t) {
        float cx = t.left + e.getX() * t.width();
        float cy = t.top + e.getY() * t.height();
        float s = tamano(e, t);
        c.save();
        c.translate(cx, cy);
        c.rotate(e.getRotacion());
        relleno.setShader(null);
        trazo.setShader(null);
        trazo.setPathEffect(null);
        switch (e.getTipo()) {
            case CHINO: chino(c, s, e.getColor()); break;
            case CONO: conoTriangulo(c, s, e.getColor()); break;
            case CONO_ALTO: conoAlto(c, s, e.getColor()); break;
            case PICA: pica(c, s, e.getColor(), false); break;
            case PICA_BASE: pica(c, s, e.getColor(), true); break;
            case PORTERIA: porteria(c, s, e.getColor()); break;
            case ESCALERA: escalera(c, s, e.getColor()); break;
            case VALLA: valla(c, s, e.getColor()); break;
            case ARO: aro(c, s, e.getColor()); break;
            case BALON: balon(c, s); break;
            case BALONES: balones(c, s); break;
            case BOSU: bosu(c, s, e.getColor()); break;
            case BALON_MEDICINAL: balonMedicinal(c, s, e.getColor()); break;
            case FITBALL: fitball(c, s, e.getColor()); break;
            case JUGADOR: jugador(c, s, e); break;
            case CAMISETA: camiseta(c, s, e); break;
            case TEXTO: textoLibre(c, s, e); break;
        }
        c.restore();
    }

    /** Dibuja un elemento centrado en un recuadro (miniaturas de la paleta). */
    public void dibujarIcono(Canvas c, ElementoPizarra e, RectF caja) {
        // Se usa un tablero ficticio para que el objeto ocupe ~70% de la caja.
        float[] m = medidas(e, new RectF(0, 0, 1000, 750));
        // Encaja el objeto en la caja aprovechando ancho y alto (porterias, escaleras...).
        float factor = Math.min(caja.width() * 0.86f / m[0], caja.height() * 0.78f / m[1]);
        RectF tableroFicticio = new RectF(0, 0, 1000 * factor, 750 * factor);
        c.save();
        c.translate(caja.centerX() - tableroFicticio.width() * e.getX(),
                caja.centerY() - tableroFicticio.height() * e.getY());
        dibujarElemento(c, e, tableroFicticio);
        c.restore();
    }

    // ── Material ─────────────────────────────────────────────────────

    private void chino(Canvas c, float s, int color) {
        float r = s / 2f;
        sombra(c, r);
        relleno.setColor(color);
        c.drawCircle(0, 0, r, relleno);
        contorno(c, r, oscurecer(color, 0.6f), s * 0.05f);
        relleno.setColor(0xCCFFFFFF);
        c.drawCircle(0, 0, r * 0.32f, relleno);
        relleno.setColor(oscurecer(color, 0.45f));
        c.drawCircle(0, 0, r * 0.13f, relleno);
    }

    private void conoTriangulo(Canvas c, float s, int color) {
        float h = s * 0.9f;
        path.reset();
        path.moveTo(0, -h / 2f);
        path.lineTo(s / 2f, h / 2f);
        path.lineTo(-s / 2f, h / 2f);
        path.close();
        relleno.setColor(color);
        c.drawPath(path, relleno);
        trazo.setColor(oscurecer(color, 0.6f));
        trazo.setStrokeWidth(s * 0.05f);
        c.drawPath(path, trazo);
    }

    private void conoAlto(Canvas c, float s, int color) {
        float h = s;
        // Base
        relleno.setColor(oscurecer(color, 0.8f));
        rect.set(-s * 0.48f, h * 0.36f, s * 0.48f, h * 0.5f);
        c.drawRoundRect(rect, s * 0.04f, s * 0.04f, relleno);
        // Cuerpo
        path.reset();
        path.moveTo(-s * 0.08f, -h * 0.5f);
        path.lineTo(s * 0.08f, -h * 0.5f);
        path.lineTo(s * 0.34f, h * 0.38f);
        path.lineTo(-s * 0.34f, h * 0.38f);
        path.close();
        relleno.setColor(color);
        c.drawPath(path, relleno);
        // Franjas blancas
        relleno.setColor(0xF0FFFFFF);
        franjaCono(c, s, h, -0.18f, -0.05f);
        franjaCono(c, s, h, 0.08f, 0.2f);
        trazo.setColor(oscurecer(color, 0.6f));
        trazo.setStrokeWidth(s * 0.03f);
        c.drawPath(path, trazo);
    }

    private void franjaCono(Canvas c, float s, float h, float y1, float y2) {
        // Ancho del cono a una altura dada (interpolando entre punta y base).
        float a1 = anchoCono(s, y1), a2 = anchoCono(s, y2);
        Path p = new Path();
        p.moveTo(-a1, y1 * h);
        p.lineTo(a1, y1 * h);
        p.lineTo(a2, y2 * h);
        p.lineTo(-a2, y2 * h);
        p.close();
        c.drawPath(p, relleno);
    }

    private float anchoCono(float s, float yRel) {
        float t = (yRel + 0.5f) / 0.88f; // 0 en la punta, 1 en la base
        return s * (0.08f + (0.34f - 0.08f) * t);
    }

    private void pica(Canvas c, float s, int color, boolean conBase) {
        float ancho = Math.max(2f, s * 0.07f);
        float arriba = -s / 2f, abajo = conBase ? s * 0.2f : s / 2f;
        relleno.setColor(oscurecer(color, 0.55f));
        rect.set(-ancho / 2f - 1, arriba - 1, ancho / 2f + 1, abajo + 1);
        c.drawRoundRect(rect, ancho, ancho, relleno);
        relleno.setColor(color);
        rect.set(-ancho / 2f, arriba, ancho / 2f, abajo);
        c.drawRoundRect(rect, ancho, ancho, relleno);
        if (conBase) {
            path.reset();
            path.moveTo(0, s * 0.12f);
            path.lineTo(s * 0.3f, s * 0.5f);
            path.lineTo(-s * 0.3f, s * 0.5f);
            path.close();
            c.drawPath(path, relleno);
            trazo.setColor(oscurecer(color, 0.6f));
            trazo.setStrokeWidth(s * 0.025f);
            c.drawPath(path, trazo);
        }
    }

    private void porteria(Canvas c, float s, int color) {
        float w = s, h = s * 0.22f;
        rect.set(-w / 2f, -h / 2f, w / 2f, h / 2f);
        relleno.setColor(0xFFF5F5F5);
        c.drawRect(rect, relleno);
        // Red
        trazo.setColor(0xFF555555);
        trazo.setStrokeWidth(Math.max(1f, s * 0.008f));
        float paso = Math.max(3f, s * 0.045f);
        for (float x = -w / 2f + paso; x < w / 2f; x += paso) c.drawLine(x, -h / 2f, x, h / 2f, trazo);
        for (float y = -h / 2f + paso * 0.8f; y < h / 2f; y += paso * 0.8f) c.drawLine(-w / 2f, y, w / 2f, y, trazo);
        // Postes y larguero (color del objeto, por defecto negro/blanco)
        trazo.setColor(color == Colores.BLANCO ? Colores.NEGRO : color);
        trazo.setStrokeWidth(Math.max(2f, s * 0.035f));
        c.drawRect(rect, trazo);
    }

    private void escalera(Canvas c, float s, int color) {
        float w = s, h = s * 0.18f;
        float g = Math.max(1.5f, s * 0.018f);
        // Contorno oscuro para que se vea sobre el cesped
        trazo.setColor(0xAA000000);
        trazo.setStrokeWidth(g * 2.2f);
        escaleraLineas(c, w, h);
        trazo.setColor(color);
        trazo.setStrokeWidth(g);
        escaleraLineas(c, w, h);
    }

    private void escaleraLineas(Canvas c, float w, float h) {
        c.drawLine(-w / 2f, -h / 2f, w / 2f, -h / 2f, trazo);
        c.drawLine(-w / 2f, h / 2f, w / 2f, h / 2f, trazo);
        int peldanos = 9;
        for (int i = 0; i < peldanos; i++) {
            float x = -w / 2f + w * i / (peldanos - 1);
            c.drawLine(x, -h / 2f, x, h / 2f, trazo);
        }
    }

    private void valla(Canvas c, float s, int color) {
        float w = s, h = s * 0.75f;
        path.reset();
        path.moveTo(-w / 2f, h / 2f);
        path.lineTo(-w / 2f, -h / 2f);
        path.lineTo(w / 2f, -h / 2f);
        path.lineTo(w / 2f, h / 2f);
        trazo.setStrokeCap(Paint.Cap.SQUARE);
        trazo.setColor(0x99000000);
        trazo.setStrokeWidth(s * 0.2f);
        c.drawPath(path, trazo);
        trazo.setColor(color);
        trazo.setStrokeWidth(s * 0.12f);
        c.drawPath(path, trazo);
        trazo.setStrokeCap(Paint.Cap.ROUND);
    }

    private void aro(Canvas c, float s, int color) {
        float r = s / 2f;
        trazo.setColor(0x66000000);
        trazo.setStrokeWidth(s * 0.13f);
        c.drawCircle(0, 0, r * 0.92f, trazo);
        trazo.setColor(color);
        trazo.setStrokeWidth(s * 0.08f);
        c.drawCircle(0, 0, r * 0.92f, trazo);
    }

    private void balon(Canvas c, float s) {
        float r = s / 2f;
        sombra(c, r);
        relleno.setColor(Color.WHITE);
        c.drawCircle(0, 0, r, relleno);
        // Pentagono central y costuras
        relleno.setColor(0xFF263238);
        path.reset();
        float rp = r * 0.38f;
        for (int i = 0; i < 5; i++) {
            double a = Math.toRadians(-90 + i * 72);
            float x = (float) (Math.cos(a) * rp), y = (float) (Math.sin(a) * rp);
            if (i == 0) path.moveTo(x, y); else path.lineTo(x, y);
        }
        path.close();
        c.drawPath(path, relleno);
        trazo.setColor(0xFF263238);
        trazo.setStrokeWidth(Math.max(1f, s * 0.05f));
        for (int i = 0; i < 5; i++) {
            double a = Math.toRadians(-90 + i * 72);
            c.drawLine((float) (Math.cos(a) * rp), (float) (Math.sin(a) * rp),
                    (float) (Math.cos(a) * r * 0.85f), (float) (Math.sin(a) * r * 0.85f), trazo);
        }
        contorno(c, r, 0xFF263238, s * 0.05f);
    }

    private void balones(Canvas c, float s) {
        float b = s / 3.2f;
        float[][] pos = {{-b, -b * 0.55f}, {0, -b * 0.6f}, {b, -b * 0.5f},
                {-b * 0.5f, b * 0.45f}, {b * 0.5f, b * 0.5f}, {0, b * 0.05f}};
        for (float[] p : pos) {
            c.save();
            c.translate(p[0], p[1]);
            balon(c, b * 1.15f);
            c.restore();
        }
    }

    private void bosu(Canvas c, float s, int color) {
        float w = s, h = s * 0.5f;
        relleno.setColor(0xFF263238);
        rect.set(-w / 2f, h * 0.25f, w / 2f, h * 0.55f);
        c.drawOval(rect, relleno);
        rect.set(-w / 2f, -h * 0.55f, w / 2f, h * 0.95f);
        relleno.setShader(new LinearGradient(0, -h / 2f, 0, h / 2f,
                aclarar(color, 0.35f), color, Shader.TileMode.CLAMP));
        c.drawArc(rect, 180, 180, true, relleno);
        relleno.setShader(null);
    }

    private void balonMedicinal(Canvas c, float s, int color) {
        float r = s / 2f;
        sombra(c, r);
        relleno.setColor(color);
        c.drawCircle(0, 0, r, relleno);
        trazo.setColor(Colores.ROJO);
        trazo.setStrokeWidth(s * 0.08f);
        rect.set(-r * 1.6f, -r * 0.6f, r * 0.4f, r * 1.4f);
        c.drawArc(rect, -60, 80, false, trazo);
        rect.set(-r * 0.4f, -r * 1.4f, r * 1.6f, r * 0.6f);
        c.drawArc(rect, 120, 80, false, trazo);
        contorno(c, r, 0xFF000000, s * 0.04f);
    }

    private void fitball(Canvas c, float s, int color) {
        float r = s / 2f;
        sombra(c, r);
        relleno.setColor(Color.BLACK); // opaco: el degradado hereda el alfa del pincel
        relleno.setShader(new RadialGradient(-r * 0.35f, -r * 0.35f, r * 1.3f,
                aclarar(color, 0.5f), oscurecer(color, 0.7f), Shader.TileMode.CLAMP));
        c.drawCircle(0, 0, r, relleno);
        relleno.setShader(null);
    }

    // ── Jugadores ────────────────────────────────────────────────────

    private void jugador(Canvas c, float s, ElementoPizarra e) {
        float r = s / 2f;
        int color = e.getColor();
        boolean bicolor = e.getColor2() != ElementoPizarra.SIN_COLOR;
        sombra(c, r);
        relleno.setColor(Color.BLACK); // opaco: el degradado hereda el alfa del pincel
        if (bicolor) {
            relleno.setShader(new LinearGradient(-r, 0, r, 0,
                    new int[]{color, color, e.getColor2(), e.getColor2()},
                    new float[]{0f, 0.35f, 0.65f, 1f}, Shader.TileMode.CLAMP));
        } else {
            relleno.setShader(new RadialGradient(-r * 0.3f, -r * 0.3f, r * 1.4f,
                    aclarar(color, 0.25f), oscurecer(color, 0.8f), Shader.TileMode.CLAMP));
        }
        c.drawCircle(0, 0, r, relleno);
        relleno.setShader(null);
        contorno(c, r, 0xFF333333, s * 0.05f);

        if (e.getNumero() > 0) {
            String n = String.valueOf(e.getNumero());
            texto.setTextSize(r * (n.length() > 1 ? 0.95f : 1.1f));
            texto.setTextAlign(Paint.Align.CENTER);
            texto.getTextBounds(n, 0, n.length(), bounds);
            float y = bounds.height() / 2f;
            int colorTexto = bicolor ? 0xFFFFD600 : contraste(color);
            // Borde del numero para que se lea siempre
            texto.setStyle(Paint.Style.STROKE);
            texto.setStrokeWidth(s * 0.07f);
            texto.setColor(colorTexto == Color.WHITE || bicolor ? 0xCC000000 : 0x66FFFFFF);
            c.drawText(n, 0, y, texto);
            texto.setStyle(Paint.Style.FILL);
            texto.setColor(colorTexto);
            c.drawText(n, 0, y, texto);
        }
    }

    private void camiseta(Canvas c, float s, ElementoPizarra e) {
        float w = s, h = s;
        int color = e.getColor();
        int color2 = e.getColor2() == ElementoPizarra.SIN_COLOR ? color : e.getColor2();
        Path p = new Path();
        p.moveTo(-w * 0.18f, -h * 0.45f);   // cuello izq
        p.lineTo(-w * 0.48f, -h * 0.3f);    // hombro izq
        p.lineTo(-w * 0.48f, -h * 0.05f);   // manga
        p.lineTo(-w * 0.3f, -h * 0.05f);
        p.lineTo(-w * 0.3f, h * 0.48f);     // bajo izq
        p.lineTo(w * 0.3f, h * 0.48f);
        p.lineTo(w * 0.3f, -h * 0.05f);
        p.lineTo(w * 0.48f, -h * 0.05f);
        p.lineTo(w * 0.48f, -h * 0.3f);
        p.lineTo(w * 0.18f, -h * 0.45f);
        p.quadTo(0, -h * 0.3f, -w * 0.18f, -h * 0.45f);
        p.close();
        relleno.setColor(color);
        c.drawPath(p, relleno);
        if (color2 != color) {
            // Mangas y franja central del segundo color
            c.save();
            c.clipPath(p);
            relleno.setColor(color2);
            c.drawRect(-w * 0.5f, -h * 0.5f, -w * 0.3f, h * 0.1f, relleno);
            c.drawRect(w * 0.3f, -h * 0.5f, w * 0.5f, h * 0.1f, relleno);
            c.drawRect(-w * 0.08f, -h * 0.5f, w * 0.08f, h * 0.5f, relleno);
            c.restore();
        }
        trazo.setColor(0xFF333333);
        trazo.setStrokeWidth(s * 0.03f);
        c.drawPath(p, trazo);
        if (e.getNumero() > 0) {
            String n = String.valueOf(e.getNumero());
            texto.setTextSize(h * 0.32f);
            texto.setStyle(Paint.Style.FILL);
            texto.setColor(contraste(color));
            c.drawText(n, 0, h * 0.28f, texto);
        }
    }

    // ── Textos ───────────────────────────────────────────────────────

    private boolean esCajaTexto(ElementoPizarra e) {
        return e.getColor2() != ElementoPizarra.SIN_COLOR;
    }

    private String[] lineas(ElementoPizarra e) {
        String t = e.getTexto().isEmpty() ? "Texto" : e.getTexto();
        return t.split("\n");
    }

    private void configurarTexto(ElementoPizarra e, float s) {
        texto.setTextSize(s);
        texto.setStyle(Paint.Style.FILL);
        texto.setTextAlign(Paint.Align.CENTER);
    }

    private void textoLibre(Canvas c, float s, ElementoPizarra e) {
        configurarTexto(e, s);
        String[] lineas = lineas(e);
        float alto = lineas.length * texto.getFontSpacing();
        if (esCajaTexto(e)) {
            float[] m = medidasTexto(e, s);
            configurarTexto(e, s);
            rect.set(-m[0] / 2f, -m[1] / 2f, m[0] / 2f, m[1] / 2f);
            relleno.setColor(e.getColor2());
            c.drawRoundRect(rect, s * 0.25f, s * 0.25f, relleno);
            trazo.setColor(oscurecer(e.getColor2(), 0.7f));
            trazo.setStrokeWidth(s * 0.06f);
            c.drawRoundRect(rect, s * 0.25f, s * 0.25f, trazo);
        }
        texto.setColor(e.getColor());
        float y = -alto / 2f - texto.ascent();
        for (String l : lineas) {
            c.drawText(l, 0, y, texto);
            y += texto.getFontSpacing();
        }
    }

    // ═════════════════════════════════════════════════════════════════
    //  Trazos (flechas y lineas)
    // ═════════════════════════════════════════════════════════════════

    public static float grosorBase(Trazo t, RectF tablero) {
        return Math.max(1.5f, tablero.width() * 0.004f * t.getGrosor());
    }

    public void dibujarTrazo(Canvas c, Trazo t, RectF tab) {
        List<float[]> pts = t.getPuntos();
        if (pts.size() < 2) return;
        float g = grosorBase(t, tab);
        trazo.setShader(null);
        trazo.setColor(t.getColor());
        trazo.setStrokeWidth(g);
        trazo.setPathEffect(null);

        float[] a = px(pts.get(0), tab);
        float[] b = px(pts.get(pts.size() - 1), tab);

        switch (t.getTipo()) {
            case LIBRE: {
                path.reset();
                float[] p0 = px(pts.get(0), tab);
                path.moveTo(p0[0], p0[1]);
                for (int i = 1; i < pts.size(); i++) {
                    float[] p = px(pts.get(i), tab);
                    float[] prev = px(pts.get(i - 1), tab);
                    path.quadTo(prev[0], prev[1], (prev[0] + p[0]) / 2f, (prev[1] + p[1]) / 2f);
                }
                path.lineTo(b[0], b[1]);
                c.drawPath(path, trazo);
                break;
            }
            case LINEA:
                c.drawLine(a[0], a[1], b[0], b[1], trazo);
                break;
            case PASE:
                lineaConFlecha(c, a, b, g, t.getColor());
                break;
            case SIN_BALON:
                trazo.setPathEffect(new DashPathEffect(new float[]{g * 3.5f, g * 2.5f}, 0));
                lineaConFlecha(c, a, b, g, t.getColor());
                trazo.setPathEffect(null);
                break;
            case CONDUCCION:
                ondulada(c, a, b, g, t.getColor(), tab.width());
                break;
            case MEDIDA:
                medida(c, a, b, g, t, tab.width());
                break;
        }
    }

    private float[] px(float[] p, RectF t) {
        return new float[]{t.left + p[0] * t.width(), t.top + p[1] * t.height()};
    }

    private void lineaConFlecha(Canvas c, float[] a, float[] b, float g, int color) {
        float largo = Math.max(g * 4.5f, 10f);
        double ang = Math.atan2(b[1] - a[1], b[0] - a[0]);
        // La linea termina en la base de la punta para que no asome.
        float fx = b[0] - (float) Math.cos(ang) * largo * 0.8f;
        float fy = b[1] - (float) Math.sin(ang) * largo * 0.8f;
        c.drawLine(a[0], a[1], fx, fy, trazo);
        punta(c, b, ang, largo, color);
    }

    private void punta(Canvas c, float[] b, double ang, float largo, int color) {
        float ancho = largo * 0.55f;
        path.reset();
        path.moveTo(b[0], b[1]);
        path.lineTo(b[0] - (float) (Math.cos(ang) * largo - Math.sin(ang) * ancho),
                b[1] - (float) (Math.sin(ang) * largo + Math.cos(ang) * ancho));
        path.lineTo(b[0] - (float) (Math.cos(ang) * largo + Math.sin(ang) * ancho),
                b[1] - (float) (Math.sin(ang) * largo - Math.cos(ang) * ancho));
        path.close();
        relleno.setShader(null);
        relleno.setColor(color);
        c.drawPath(path, relleno);
    }

    private void ondulada(Canvas c, float[] a, float[] b, float g, int color, float u) {
        float dx = b[0] - a[0], dy = b[1] - a[1];
        float L = (float) Math.sqrt(dx * dx + dy * dy);
        if (L < 1) return;
        double ang = Math.atan2(dy, dx);
        float largoPunta = Math.max(g * 4.5f, 10f);
        float onda = u * 0.022f, amp = u * 0.006f;
        float tramo = Math.max(0, L - largoPunta * 1.6f);
        c.save();
        c.translate(a[0], a[1]);
        c.rotate((float) Math.toDegrees(ang));
        path.reset();
        path.moveTo(0, 0);
        for (float x = 0; x <= tramo; x += 2f) {
            path.lineTo(x, (float) Math.sin(x / onda * 2 * Math.PI) * amp);
        }
        path.lineTo(L - largoPunta * 0.8f, 0);
        c.drawPath(path, trazo);
        c.restore();
        punta(c, b, ang, largoPunta, color);
    }

    private void medida(Canvas c, float[] a, float[] b, float g, Trazo t, float u) {
        c.drawLine(a[0], a[1], b[0], b[1], trazo);
        relleno.setColor(t.getColor());
        c.drawCircle(a[0], a[1], g * 1.6f, relleno);
        c.drawCircle(b[0], b[1], g * 1.6f, relleno);
        String etiqueta = t.getTexto().isEmpty() ? "" : t.getTexto();
        if (etiqueta.isEmpty()) return;
        double ang = Math.toDegrees(Math.atan2(b[1] - a[1], b[0] - a[0]));
        // Que el texto nunca quede boca abajo.
        if (ang > 90) ang -= 180;
        if (ang < -90) ang += 180;
        c.save();
        c.translate((a[0] + b[0]) / 2f, (a[1] + b[1]) / 2f);
        c.rotate((float) ang);
        texto.setTextSize(u * 0.026f);
        // Separada de la linea para que no se monte encima.
        float desplazamiento = -(g * 2.5f + u * 0.008f);
        texto.setStyle(Paint.Style.STROKE);
        texto.setStrokeWidth(u * 0.005f);
        texto.setColor(0xCCFFFFFF);
        c.drawText(etiqueta, 0, desplazamiento, texto);
        texto.setStyle(Paint.Style.FILL);
        texto.setColor(t.getColor());
        c.drawText(etiqueta, 0, desplazamiento, texto);
        c.restore();
    }

    // ═════════════════════════════════════════════════════════════════
    //  Ayudas
    // ═════════════════════════════════════════════════════════════════

    private void sombra(Canvas c, float r) {
        relleno.setShader(null);
        relleno.setColor(0x40000000);
        c.drawCircle(r * 0.08f, r * 0.12f, r, relleno);
    }

    private void contorno(Canvas c, float r, int color, float grosor) {
        trazo.setColor(color);
        trazo.setStrokeWidth(Math.max(1f, grosor));
        c.drawCircle(0, 0, r - grosor / 2f, trazo);
    }

    public static int oscurecer(int color, float f) {
        return Color.argb(Color.alpha(color), (int) (Color.red(color) * f),
                (int) (Color.green(color) * f), (int) (Color.blue(color) * f));
    }

    public static int aclarar(int color, float f) {
        return Color.argb(Color.alpha(color),
                (int) (Color.red(color) + (255 - Color.red(color)) * f),
                (int) (Color.green(color) + (255 - Color.green(color)) * f),
                (int) (Color.blue(color) + (255 - Color.blue(color)) * f));
    }

    /** Blanco o negro, el que mejor se lea sobre {@code fondo}. */
    public static int contraste(int fondo) {
        double l = (0.299 * Color.red(fondo) + 0.587 * Color.green(fondo)
                + 0.114 * Color.blue(fondo)) / 255;
        return l > 0.6 ? 0xFF212121 : Color.WHITE;
    }
}
