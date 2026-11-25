package com.entrenador.pizarratactica.model.pizarra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Linea dibujada en la pizarra: pase, desplazamiento, conduccion, medida...
 * Los puntos estan normalizados (0..1) igual que los elementos.
 */
public class Trazo {

    private long id;
    private TipoTrazo tipo;
    private int color;
    private float grosor = 1f;       // multiplicador del grosor base
    private String texto = "";       // etiqueta (p. ej. "35 m" en medidas)
    private final List<float[]> puntos = new ArrayList<>();

    public Trazo(long id, TipoTrazo tipo, int color) {
        this.id = id;
        this.tipo = tipo;
        this.color = color;
    }

    public Trazo copiar() {
        Trazo c = new Trazo(id, tipo, color);
        c.grosor = grosor;
        c.texto = texto;
        for (float[] p : puntos) c.puntos.add(new float[]{p[0], p[1]});
        return c;
    }

    public void agregarPunto(float x, float y) {
        puntos.add(new float[]{limitar(x), limitar(y)});
    }

    /** Fija inicio y fin (para trazos rectos). */
    public void setExtremos(float x1, float y1, float x2, float y2) {
        puntos.clear();
        agregarPunto(x1, y1);
        agregarPunto(x2, y2);
    }

    /** Mueve un punto concreto (p. ej. al arrastrar el extremo de una flecha). */
    public void moverPunto(int indice, float x, float y) {
        if (indice < 0 || indice >= puntos.size()) return;
        puntos.set(indice, new float[]{limitar(x), limitar(y)});
    }

    /** Desplaza todo el trazo. */
    public void desplazar(float dx, float dy) {
        // No dejar que el trazo se salga del campo.
        float minX = 1, minY = 1, maxX = 0, maxY = 0;
        for (float[] p : puntos) {
            minX = Math.min(minX, p[0]); maxX = Math.max(maxX, p[0]);
            minY = Math.min(minY, p[1]); maxY = Math.max(maxY, p[1]);
        }
        dx = Math.max(-minX, Math.min(1 - maxX, dx));
        dy = Math.max(-minY, Math.min(1 - maxY, dy));
        for (float[] p : puntos) {
            p[0] += dx;
            p[1] += dy;
        }
    }

    /** Longitud total en coordenadas normalizadas. */
    public float longitud() {
        float total = 0;
        for (int i = 1; i < puntos.size(); i++) {
            float dx = puntos.get(i)[0] - puntos.get(i - 1)[0];
            float dy = puntos.get(i)[1] - puntos.get(i - 1)[1];
            total += (float) Math.sqrt(dx * dx + dy * dy);
        }
        return total;
    }

    private static float limitar(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public TipoTrazo getTipo() { return tipo; }
    public void setTipo(TipoTrazo tipo) { this.tipo = tipo; }
    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }
    public float getGrosor() { return grosor; }
    public void setGrosor(float grosor) { this.grosor = Math.max(0.5f, Math.min(3f, grosor)); }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto == null ? "" : texto; }
    public List<float[]> getPuntos() { return Collections.unmodifiableList(puntos); }
    public int numPuntos() { return puntos.size(); }
}
