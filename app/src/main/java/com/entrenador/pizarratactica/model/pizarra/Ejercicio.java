package com.entrenador.pizarratactica.model.pizarra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Un ejercicio de la sesion: equivale a una diapositiva del PowerPoint
 * (campo + material + jugadores + flechas + textos).
 */
public class Ejercicio {

    private String id;
    private String titulo = "";
    private String descripcion = "";
    private int duracionMin;
    private TipoCampo campo = TipoCampo.COMPLETO;
    private final List<ElementoPizarra> elementos = new ArrayList<>();
    private final List<Trazo> trazos = new ArrayList<>();
    private long contadorIds;

    public Ejercicio() {
        this(UUID.randomUUID().toString());
    }

    public Ejercicio(String id) {
        this.id = id;
    }

    // ── Elementos ────────────────────────────────────────────────────

    public ElementoPizarra agregarElemento(TipoElemento tipo, float x, float y, int color) {
        ElementoPizarra e = new ElementoPizarra(nuevoId(), tipo, x, y, color);
        elementos.add(e);
        return e;
    }

    /** Inserta un elemento ya construido (p. ej. al cargar desde JSON). */
    public void agregarElementoExistente(ElementoPizarra e) {
        if (e.getId() <= 0 || buscarElemento(e.getId()) != null) {
            e.setId(nuevoId());
        }
        contadorIds = Math.max(contadorIds, e.getId());
        elementos.add(e);
    }

    public ElementoPizarra buscarElemento(long id) {
        for (ElementoPizarra e : elementos) {
            if (e.getId() == id) return e;
        }
        return null;
    }

    public boolean eliminarElemento(long id) {
        ElementoPizarra e = buscarElemento(id);
        return e != null && elementos.remove(e);
    }

    /** Duplica un elemento desplazandolo un poco para que se vea la copia. */
    public ElementoPizarra duplicarElemento(long id) {
        ElementoPizarra original = buscarElemento(id);
        if (original == null) return null;
        ElementoPizarra copia = original.copiar();
        copia.setId(nuevoId());
        copia.mover(original.getX() + 0.03f, original.getY() + 0.03f);
        if (copia.getTipo().admiteNumero() && copia.getNumero() > 0) {
            copia.setNumero(siguienteNumero(copia.getColor(), copia.getColor2()));
        }
        elementos.add(copia);
        return copia;
    }

    /** Pone el elemento por encima de los demas. */
    public void traerAlFrente(long id) {
        ElementoPizarra e = buscarElemento(id);
        if (e != null) {
            elementos.remove(e);
            elementos.add(e);
        }
    }

    /**
     * Siguiente dorsal libre para jugadores de ese color
     * (1, 2, 3... por equipo, como en las diapositivas).
     */
    public int siguienteNumero(int color, int color2) {
        int max = 0;
        for (ElementoPizarra e : elementos) {
            if (e.getTipo() == TipoElemento.JUGADOR
                    && e.getColor() == color && e.getColor2() == color2) {
                max = Math.max(max, e.getNumero());
            }
        }
        return max + 1;
    }

    // ── Trazos ───────────────────────────────────────────────────────

    public Trazo agregarTrazo(TipoTrazo tipo, int color) {
        Trazo t = new Trazo(nuevoId(), tipo, color);
        trazos.add(t);
        return t;
    }

    public void agregarTrazoExistente(Trazo t) {
        if (t.getId() <= 0 || buscarTrazo(t.getId()) != null) {
            t.setId(nuevoId());
        }
        contadorIds = Math.max(contadorIds, t.getId());
        trazos.add(t);
    }

    public Trazo buscarTrazo(long id) {
        for (Trazo t : trazos) {
            if (t.getId() == id) return t;
        }
        return null;
    }

    public boolean eliminarTrazo(long id) {
        Trazo t = buscarTrazo(id);
        return t != null && trazos.remove(t);
    }

    // ── Utilidades ───────────────────────────────────────────────────

    public void limpiar() {
        elementos.clear();
        trazos.clear();
    }

    public boolean estaVacio() {
        return elementos.isEmpty() && trazos.isEmpty();
    }

    /**
     * Recuento del material necesario (sin jugadores ni textos),
     * en el orden en que aparece. Util para preparar el material antes del entreno.
     */
    public Map<TipoElemento, Integer> recuentoMaterial() {
        Map<TipoElemento, Integer> mapa = new LinkedHashMap<>();
        for (ElementoPizarra e : elementos) {
            if (e.getTipo().getCategoria() != TipoElemento.Categoria.MATERIAL) continue;
            Integer n = mapa.get(e.getTipo());
            mapa.put(e.getTipo(), n == null ? 1 : n + 1);
        }
        return mapa;
    }

    /** Copia profunda conservando el id (para deshacer). */
    public Ejercicio copiar() {
        Ejercicio c = new Ejercicio(id);
        copiarDatosEn(c);
        return c;
    }

    /** Copia profunda con id nuevo (para duplicar el ejercicio). */
    public Ejercicio duplicar() {
        Ejercicio c = new Ejercicio();
        copiarDatosEn(c);
        return c;
    }

    private void copiarDatosEn(Ejercicio c) {
        c.titulo = titulo;
        c.descripcion = descripcion;
        c.duracionMin = duracionMin;
        c.campo = campo;
        c.contadorIds = contadorIds;
        for (ElementoPizarra e : elementos) c.elementos.add(e.copiar());
        for (Trazo t : trazos) c.trazos.add(t.copiar());
    }

    /** Sustituye todo el contenido por el de otro ejercicio (restaurar deshacer). */
    public void restaurarDesde(Ejercicio otro) {
        elementos.clear();
        trazos.clear();
        otro.copiarDatosEn(this);
    }

    private long nuevoId() {
        return ++contadorIds;
    }

    // ── Getters / setters ────────────────────────────────────────────

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo == null ? "" : titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String d) { this.descripcion = d == null ? "" : d; }
    public int getDuracionMin() { return duracionMin; }
    public void setDuracionMin(int m) { this.duracionMin = Math.max(0, m); }
    public TipoCampo getCampo() { return campo; }
    public void setCampo(TipoCampo campo) { this.campo = campo == null ? TipoCampo.COMPLETO : campo; }
    public List<ElementoPizarra> getElementos() { return Collections.unmodifiableList(elementos); }
    public List<Trazo> getTrazos() { return Collections.unmodifiableList(trazos); }
}
