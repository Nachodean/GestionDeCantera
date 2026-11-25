package com.entrenador.pizarratactica.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Persistencia local de los informes de rival (SharedPreferences).
 * Mismo almacen y formato que la version anterior: no se pierde nada.
 */
public class InformeRivalRepository {

    private static final String TAG = "InformeRivalRepository";
    private static final String PREFS_NAME = "informes_rival";
    private static final String KEY_INFORMES = "lista_informes";

    private final SharedPreferences prefs;

    public InformeRivalRepository(Context context) {
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean guardar(InformeRival informe) {
        try {
            JSONArray array = new JSONArray(prefs.getString(KEY_INFORMES, "[]"));
            JSONObject obj = new JSONObject();
            obj.put("id", informe.getId());
            obj.put("rival", informe.getRival());
            obj.put("fecha", informe.getFecha());
            for (Map.Entry<String, String> c : informe.getCampos().entrySet()) {
                obj.put(c.getKey(), c.getValue());
            }
            array.put(obj);
            return prefs.edit().putString(KEY_INFORMES, array.toString()).commit();
        } catch (Exception e) {
            Log.e(TAG, "No se pudo guardar el informe", e);
            return false;
        }
    }

    /** Informes guardados, el mas reciente primero. */
    public List<InformeRival> listar() {
        List<InformeRival> lista = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(prefs.getString(KEY_INFORMES, "[]"));
            for (int i = array.length() - 1; i >= 0; i--) {
                JSONObject obj = array.getJSONObject(i);
                InformeRival inf = new InformeRival(obj.optString("id"),
                        obj.optString("rival", "-"), obj.optString("fecha", "-"));
                Iterator<String> claves = obj.keys();
                while (claves.hasNext()) {
                    String k = claves.next();
                    if (!k.equals("id") && !k.equals("rival") && !k.equals("fecha")) {
                        inf.setCampo(k, obj.optString(k, ""));
                    }
                }
                lista.add(inf);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error leyendo informes", e);
        }
        return lista;
    }
}
