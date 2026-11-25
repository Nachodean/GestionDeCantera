package com.entrenador.pizarratactica.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.entrenador.pizarratactica.model.pizarra.Sesion;
import com.entrenador.pizarratactica.model.pizarra.SesionJson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Persistencia local de las sesiones de entrenamiento (SharedPreferences).
 * <p>
 * Las sesiones se guardan en la clave {@code sesiones_v2}. La clave antigua
 * {@code lista_sesiones} NO se borra: la primera vez se copian sus sesiones al
 * formato nuevo, de forma que nunca se pierden datos.
 */
public class SesionRepository {

    private static final String TAG = "SesionRepository";
    private static final String PREFS_NAME = "pizarra_tactica_sesiones";
    private static final String KEY_LEGADO = "lista_sesiones";
    private static final String KEY_SESIONES = "sesiones_v2";
    private static final String KEY_MIGRADO = "migracion_v2_hecha";

    private final SharedPreferences prefs;

    public SesionRepository(Context context) {
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        migrarSiHaceFalta();
    }

    /** Todas las sesiones, la ultima modificada primero. */
    public List<Sesion> listar() {
        List<Sesion> lista = new ArrayList<>();
        JSONArray array = leerArray();
        for (int i = 0; i < array.length(); i++) {
            try {
                lista.add(SesionJson.desdeJson(array.getJSONObject(i)));
            } catch (Exception e) {
                // Una sesion corrupta no debe impedir ver el resto.
                Log.w(TAG, "Sesion ilegible en la posicion " + i, e);
            }
        }
        Collections.sort(lista, (a, b) -> Long.compare(b.getModificada(), a.getModificada()));
        return lista;
    }

    public Sesion obtener(String id) {
        if (id == null) return null;
        for (Sesion s : listar()) {
            if (s.getId().equals(id)) return s;
        }
        return null;
    }

    /** Inserta o actualiza la sesion. Devuelve false si no se pudo guardar. */
    public boolean guardar(Sesion sesion) {
        try {
            sesion.setModificada(System.currentTimeMillis());
            if (sesion.getCreada() == 0) sesion.setCreada(sesion.getModificada());
            JSONArray array = leerArray();
            JSONArray nuevo = new JSONArray();
            boolean reemplazada = false;
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.optJSONObject(i);
                if (o != null && sesion.getId().equals(o.optString("id"))) {
                    nuevo.put(SesionJson.aJson(sesion));
                    reemplazada = true;
                } else if (o != null) {
                    nuevo.put(o);
                }
            }
            if (!reemplazada) nuevo.put(SesionJson.aJson(sesion));
            return prefs.edit().putString(KEY_SESIONES, nuevo.toString()).commit();
        } catch (Exception e) {
            Log.e(TAG, "No se pudo guardar la sesion", e);
            return false;
        }
    }

    public boolean eliminar(String id) {
        JSONArray array = leerArray();
        JSONArray nuevo = new JSONArray();
        for (int i = 0; i < array.length(); i++) {
            JSONObject o = array.optJSONObject(i);
            if (o != null && !id.equals(o.optString("id"))) nuevo.put(o);
        }
        return prefs.edit().putString(KEY_SESIONES, nuevo.toString()).commit();
    }

    /** Crea una copia de la sesion y la guarda. Devuelve la copia o null. */
    public Sesion duplicar(String id) {
        Sesion original = obtener(id);
        if (original == null) return null;
        Sesion copia = original.duplicar(System.currentTimeMillis());
        return guardar(copia) ? copia : null;
    }

    // ── Interno ──────────────────────────────────────────────────────

    private JSONArray leerArray() {
        try {
            return new JSONArray(prefs.getString(KEY_SESIONES, "[]"));
        } catch (Exception e) {
            Log.e(TAG, "Datos de sesiones corruptos", e);
            return new JSONArray();
        }
    }

    /** Copia las sesiones del formato antiguo al nuevo una sola vez. */
    private void migrarSiHaceFalta() {
        if (prefs.getBoolean(KEY_MIGRADO, false)) return;
        try {
            JSONArray legado = new JSONArray(prefs.getString(KEY_LEGADO, "[]"));
            JSONArray actual = leerArray();
            for (int i = 0; i < legado.length(); i++) {
                Sesion s = SesionJson.desdeJsonLegado(legado.getJSONObject(i));
                long ahora = System.currentTimeMillis() - i; // conserva el orden
                s.setCreada(ahora);
                s.setModificada(ahora);
                actual.put(SesionJson.aJson(s));
            }
            prefs.edit()
                    .putString(KEY_SESIONES, actual.toString())
                    .putBoolean(KEY_MIGRADO, true)
                    .apply();
        } catch (Exception e) {
            // Si falla, no se marca como migrado y se reintentara; el legado sigue intacto.
            Log.e(TAG, "Error migrando sesiones antiguas", e);
        }
    }
}
