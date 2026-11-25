package com.entrenador.pizarratactica.model.pizarra;

/**
 * Objeto colocado en la pizarra (chino, cono, jugador, texto...).
 * <p>
 * Las coordenadas {@code x}/{@code y} estan normalizadas entre 0 y 1 respecto
 * al campo dibujado, para que el ejercicio se vea igual en cualquier pantalla.
 * Clase pura de Java: sin dependencias de Android para poder testearla.
 */
public class ElementoPizarra {

    /** Sin segundo color (objeto de un solo color). */
    public static final int SIN_COLOR = 0;

    private long id;
    private TipoElemento tipo;
    private float x;
    private float y;
    private float rotacion;     // grados
    private float escala = 1f;  // 1 = tamano base del tipo
    private int color;          // ARGB
    private int color2 = SIN_COLOR; // jugadores bicolor / camisetas
    private int numero;         // dorsal (0 = sin numero)
    private String texto = "";

    public ElementoPizarra(long id, TipoElemento tipo, float x, float y, int color) {
        this.id = id;
        this.tipo = tipo;
        this.x = limitar(x);
        this.y = limitar(y);
        this.color = color;
    }

    /** Copia profunda (para deshacer/duplicar). */
    public ElementoPizarra copiar() {
        ElementoPizarra c = new ElementoPizarra(id, tipo, x, y, color);
        c.rotacion = rotacion;
        c.escala = escala;
        c.color2 = color2;
        c.numero = numero;
        c.texto = texto;
        return c;
    }

    public void mover(float nx, float ny) {
        this.x = limitar(nx);
        this.y = limitar(ny);
    }

    /** Gira el objeto y deja el angulo entre 0 y 360. */
    public void girar(float grados) {
        float r = (rotacion + grados) % 360f;
        if (r < 0) r += 360f;
        rotacion = r;
    }

    /** Cambia el tamano dentro de unos limites razonables. */
    public void setEscala(float escala) {
        this.escala = Math.max(0.4f, Math.min(4f, escala));
    }

    private static float limitar(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    // ── Getters / setters ────────────────────────────────────────────

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public TipoElemento getTipo() { return tipo; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getRotacion() { return rotacion; }
    public void setRotacion(float rotacion) { this.rotacion = 0; girar(rotacion); }
    public float getEscala() { return escala; }
    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }
    public int getColor2() { return color2; }
    public void setColor2(int color2) { this.color2 = color2; }
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = Math.max(0, numero); }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto == null ? "" : texto; }
}
