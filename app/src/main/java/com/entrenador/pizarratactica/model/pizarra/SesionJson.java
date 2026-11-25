package com.entrenador.pizarratactica.model.pizarra;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Conversion Sesion <-> JSON para guardarla en SharedPreferences.
 * <p>
 * Esquema "v2". Las sesiones del formato antiguo (una sola pizarra con
 * coordenadas en pixeles) se convierten con {@link #desdeJsonLegado}.
 */
public final class SesionJson {

    public static final int VERSION = 2;

    private SesionJson() {}

    // ── Escritura ────────────────────────────────────────────────────

    public static JSONObject aJson(Sesion s) throws JSONException {
        JSONObject o = new JSONObject();
        o.put("version", VERSION);
        o.put("id", s.getId());
        o.put("numero", s.getNumero());
        o.put("fecha", s.getFecha());
        o.put("objetivo", s.getObjetivo());
        o.put("creada", s.getCreada());
        o.put("modificada", s.getModificada());
        JSONArray ejs = new JSONArray();
        for (Ejercicio e : s.getEjercicios()) ejs.put(ejercicioAJson(e));
        o.put("ejercicios", ejs);
        return o;
    }

    static JSONObject ejercicioAJson(Ejercicio e) throws JSONException {
        JSONObject o = new JSONObject();
        o.put("id", e.getId());
        o.put("titulo", e.getTitulo());
        o.put("descripcion", e.getDescripcion());
        o.put("duracion", e.getDuracionMin());
        o.put("campo", e.getCampo().name());

        JSONArray els = new JSONArray();
        for (ElementoPizarra el : e.getElementos()) {
            JSONObject j = new JSONObject();
            j.put("id", el.getId());
            j.put("tipo", el.getTipo().name());
            j.put("x", (double) el.getX());
            j.put("y", (double) el.getY());
            j.put("rot", (double) el.getRotacion());
            j.put("esc", (double) el.getEscala());
            j.put("color", el.getColor());
            if (el.getColor2() != ElementoPizarra.SIN_COLOR) j.put("color2", el.getColor2());
            if (el.getNumero() > 0) j.put("numero", el.getNumero());
            if (!el.getTexto().isEmpty()) j.put("texto", el.getTexto());
            els.put(j);
        }
        o.put("elementos", els);

        JSONArray trs = new JSONArray();
        for (Trazo t : e.getTrazos()) {
            JSONObject j = new JSONObject();
            j.put("id", t.getId());
            j.put("tipo", t.getTipo().name());
            j.put("color", t.getColor());
            j.put("grosor", (double) t.getGrosor());
            if (!t.getTexto().isEmpty()) j.put("texto", t.getTexto());
            JSONArray pts = new JSONArray();
            for (float[] p : t.getPuntos()) {
                pts.put((double) p[0]);
                pts.put((double) p[1]);
            }
            j.put("puntos", pts);
            trs.put(j);
        }
        o.put("trazos", trs);
        return o;
    }

    // ── Lectura ──────────────────────────────────────────────────────

    public static Sesion desdeJson(JSONObject o) throws JSONException {
        Sesion s = new Sesion(o.getString("id"));
        s.setNumero(o.optString("numero", ""));
        s.setFecha(o.optString("fecha", ""));
        s.setObjetivo(o.optString("objetivo", ""));
        s.setCreada(o.optLong("creada", 0));
        s.setModificada(o.optLong("modificada", 0));
        JSONArray ejs = o.optJSONArray("ejercicios");
        if (ejs != null) {
            for (int i = 0; i < ejs.length(); i++) {
                s.agregarEjercicioExistente(ejercicioDesdeJson(ejs.getJSONObject(i)));
            }
        }
        if (s.numEjercicios() == 0) s.agregarEjercicio();
        return s;
    }

    static Ejercicio ejercicioDesdeJson(JSONObject o) throws JSONException {
        Ejercicio e = new Ejercicio(o.optString("id", java.util.UUID.randomUUID().toString()));
        e.setTitulo(o.optString("titulo", ""));
        e.setDescripcion(o.optString("descripcion", ""));
        e.setDuracionMin(o.optInt("duracion", 0));
        e.setCampo(TipoCampo.desdeNombre(o.optString("campo", null)));

        JSONArray els = o.optJSONArray("elementos");
        if (els != null) {
            for (int i = 0; i < els.length(); i++) {
                JSONObject j = els.getJSONObject(i);
                TipoElemento tipo = TipoElemento.desdeNombre(j.optString("tipo"), null);
                if (tipo == null) continue; // tipo desconocido: se ignora sin romper la carga
                ElementoPizarra el = new ElementoPizarra(j.optLong("id", 0), tipo,
                        (float) j.optDouble("x", 0.5), (float) j.optDouble("y", 0.5),
                        j.optInt("color", 0xFFFFFFFF));
                el.setRotacion((float) j.optDouble("rot", 0));
                el.setEscala((float) j.optDouble("esc", 1));
                el.setColor2(j.optInt("color2", ElementoPizarra.SIN_COLOR));
                el.setNumero(j.optInt("numero", 0));
                el.setTexto(j.optString("texto", ""));
                e.agregarElementoExistente(el);
            }
        }

        JSONArray trs = o.optJSONArray("trazos");
        if (trs != null) {
            for (int i = 0; i < trs.length(); i++) {
                JSONObject j = trs.getJSONObject(i);
                TipoTrazo tipo = TipoTrazo.desdeNombre(j.optString("tipo"), null);
                if (tipo == null) continue;
                Trazo t = new Trazo(j.optLong("id", 0), tipo, j.optInt("color", 0xFF000000));
                t.setGrosor((float) j.optDouble("grosor", 1));
                t.setTexto(j.optString("texto", ""));
                JSONArray pts = j.optJSONArray("puntos");
                if (pts != null) {
                    for (int k = 0; k + 1 < pts.length(); k += 2) {
                        t.agregarPunto((float) pts.getDouble(k), (float) pts.getDouble(k + 1));
                    }
                }
                if (t.numPuntos() >= 2) e.agregarTrazoExistente(t);
            }
        }
        return e;
    }

    // ── Formato antiguo ──────────────────────────────────────────────

    /** Tamano de referencia aproximado de la pizarra antigua, en pixeles. */
    static final float ANCHO_LEGADO = 1200f;
    static final float ALTO_LEGADO = 900f;

    /**
     * Convierte una sesion del formato antiguo
     * ({@code id, numeroSesion, fecha, elementos[tipo,x,y,rotacion]}) en una
     * sesion nueva con un unico ejercicio. No se pierde ningun elemento.
     */
    public static Sesion desdeJsonLegado(JSONObject o) throws JSONException {
        Sesion s = new Sesion(o.getString("id"));
        s.setNumero(o.optString("numeroSesion", ""));
        s.setFecha(o.optString("fecha", ""));
        Ejercicio e = new Ejercicio();
        e.setTitulo("Ejercicio 1");
        JSONArray els = o.optJSONArray("elementos");
        if (els != null) {
            for (int i = 0; i < els.length(); i++) {
                JSONObject j = els.getJSONObject(i);
                float x = (float) j.optDouble("x", 0) / ANCHO_LEGADO;
                float y = (float) j.optDouble("y", 0) / ALTO_LEGADO;
                ElementoPizarra el = convertirTipoLegado(j.optString("tipo", ""), x, y);
                el.setRotacion((float) j.optDouble("rotacion", 0));
                e.agregarElementoExistente(el);
            }
        }
        s.agregarEjercicioExistente(e);
        return s;
    }

    private static ElementoPizarra convertirTipoLegado(String tipo, float x, float y) {
        TipoElemento t;
        int color;
        switch (tipo) {
            case "CONO": t = TipoElemento.CONO_ALTO; color = Colores.NARANJA; break;
            case "CHINO_ROJO": t = TipoElemento.CHINO; color = Colores.ROJO; break;
            case "CHINO_AZUL": t = TipoElemento.CHINO; color = Colores.AZUL; break;
            case "CHINO_AMARILLO": t = TipoElemento.CHINO; color = Colores.AMARILLO; break;
            case "CHINO_VERDE": t = TipoElemento.CHINO; color = Colores.VERDE; break;
            case "VALLA": t = TipoElemento.VALLA; color = Colores.AMARILLO; break;
            case "PICA": t = TipoElemento.PICA; color = Colores.AMARILLO; break;
            case "PORTERIA": t = TipoElemento.PORTERIA; color = Colores.BLANCO; break;
            case "BALON": t = TipoElemento.BALON; color = Colores.BLANCO; break;
            case "ESCALERA": t = TipoElemento.ESCALERA; color = Colores.AMARILLO; break;
            case "ARO": t = TipoElemento.ARO; color = Colores.ROJO; break;
            case "BALON_MEDICINAL": t = TipoElemento.BALON_MEDICINAL; color = Colores.NEGRO; break;
            case "JUGADOR_BLANCO": t = TipoElemento.JUGADOR; color = Colores.BLANCO; break;
            case "JUGADOR_NEGRO": t = TipoElemento.JUGADOR; color = Colores.NEGRO; break;
            case "JUGADOR_ROJO": t = TipoElemento.JUGADOR; color = Colores.ROJO_OSCURO; break;
            case "JUGADOR_AMARILLO": t = TipoElemento.JUGADOR; color = Colores.AMARILLO; break;
            case "JUGADOR_AZUL": t = TipoElemento.JUGADOR; color = Colores.AZUL; break;
            case "FLECHA":
                ElementoPizarra flecha = new ElementoPizarra(0, TipoElemento.TEXTO, x, y, Colores.NEGRO);
                flecha.setTexto("→");
                return flecha;
            default: t = TipoElemento.CHINO; color = Colores.NARANJA; break;
        }
        return new ElementoPizarra(0, t, x, y, color);
    }
}
