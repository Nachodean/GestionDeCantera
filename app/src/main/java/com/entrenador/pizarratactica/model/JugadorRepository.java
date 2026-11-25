package com.entrenador.pizarratactica.model;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JugadorRepository {
    private static final String PREFS_NAME = "pizarra_tactica_jugadores";
    private static final String KEY_JUGADORES = "lista_jugadores";
    private static final String KEY_MINUTOS = "lista_minutos";
    private static final String KEY_EVALUACIONES = "lista_evaluaciones";
    private static final String KEY_MULTAS = "lista_multas";
    
    private SharedPreferences prefs;

    public interface ResultadoCallback {
        void onExito(String mensaje);
        void onError(String error);
    }

    public interface ListaJugadoresCallback {
        void onJugadoresObtenidos(List<Jugador> jugadores);
        void onError(String error);
    }

    public interface ListaMinutosCallback {
        void onMinutosObtenidos(List<RegistroMinutos> registros);
        void onError(String error);
    }

    public interface ListaEvaluacionesCallback {
        void onEvaluacionesObtenidas(List<EvaluacionEntrenamiento> evaluaciones);
        void onError(String error);
    }

    public interface ListaMultasCallback {
        void onMultasObtenidas(List<Multa> multas);
        void onError(String error);
    }

    public JugadorRepository(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        inicializarPlantillaPorDefecto();
    }

    private void inicializarPlantillaPorDefecto() {
        if (!prefs.contains(KEY_JUGADORES) || prefs.getString(KEY_JUGADORES, "[]").equals("[]")) {
            try {
                JSONArray array = new JSONArray();
                // Plantilla por defecto anonima: "Jugador 1" ... "Jugador 19".
                // El entrenador la sustituye por la suya desde la seccion Plantilla.
                final int numJugadores = 19;

                for (int dorsal = 1; dorsal <= numJugadores; dorsal++) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", UUID.randomUUID().toString());
                    obj.put("nombre", "Jugador");
                    obj.put("apellidos", String.valueOf(dorsal));
                    obj.put("dorsal", dorsal);
                    obj.put("posicion", "MED");
                    obj.put("activo", true);
                    array.put(obj);
                }
                prefs.edit().putString(KEY_JUGADORES, array.toString()).apply();
            } catch (Exception ignored) {
            }
        }
    }

    // ── JUGADORES ───────────────────────────────────────────────────

    public void agregarJugador(Jugador jugador, ResultadoCallback callback) {
        try {
            JSONArray array = new JSONArray(prefs.getString(KEY_JUGADORES, "[]"));
            JSONObject obj = new JSONObject();
            String id = UUID.randomUUID().toString();
            jugador.setId(id);
            
            obj.put("id", id);
            obj.put("nombre", jugador.getNombre());
            obj.put("apellidos", jugador.getApellidos());
            obj.put("dorsal", jugador.getDorsal());
            obj.put("posicion", jugador.getPosicion().name());
            obj.put("activo", jugador.isActivo());
            
            array.put(obj);
            prefs.edit().putString(KEY_JUGADORES, array.toString()).apply();
            callback.onExito("Jugador añadido localmente.");
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    public void listarJugadores(ListaJugadoresCallback callback) {
        try {
            JSONArray array = new JSONArray(prefs.getString(KEY_JUGADORES, "[]"));
            List<Jugador> lista = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Jugador j = new Jugador();
                j.setId(obj.getString("id"));
                j.setNombre(obj.getString("nombre"));
                j.setApellidos(obj.getString("apellidos"));
                j.setDorsal(obj.getInt("dorsal"));
                j.setPosicion(Jugador.Posicion.valueOf(obj.getString("posicion")));
                j.setActivo(obj.getBoolean("activo"));
                lista.add(j);
            }
            // Ordenar por dorsal
            lista.sort((j1, j2) -> Integer.compare(j1.getDorsal(), j2.getDorsal()));
            callback.onJugadoresObtenidos(lista);
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    public void eliminarJugador(String jugadorId, ResultadoCallback callback) {
        try {
            JSONArray array = new JSONArray(prefs.getString(KEY_JUGADORES, "[]"));
            JSONArray nuevoArray = new JSONArray();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (!obj.getString("id").equals(jugadorId)) {
                    nuevoArray.put(obj);
                }
            }
            prefs.edit().putString(KEY_JUGADORES, nuevoArray.toString()).apply();
            callback.onExito("Jugador eliminado.");
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    // ── EVALUACIONES ─────────────────────────────────────────────────

    public void guardarEvaluacion(EvaluacionEntrenamiento eval, ResultadoCallback callback) {
        try {
            JSONArray array = new JSONArray(prefs.getString(KEY_EVALUACIONES, "[]"));
            JSONObject obj = new JSONObject();
            obj.put("id", UUID.randomUUID().toString());
            obj.put("jugadorId", eval.getJugadorId());
            obj.put("jugadorNombre", eval.getJugadorNombre());
            obj.put("fecha", eval.getFecha());
            obj.put("puntuacion", eval.getPuntuacion());
            obj.put("observaciones", eval.getObservaciones());
            
            array.put(obj);
            prefs.edit().putString(KEY_EVALUACIONES, array.toString()).apply();
            callback.onExito("Evaluación guardada localmente.");
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    // ── MULTAS ───────────────────────────────────────────────────────

    public void agregarMulta(Multa multa, ResultadoCallback callback) {
        try {
            JSONArray array = new JSONArray(prefs.getString(KEY_MULTAS, "[]"));
            JSONObject obj = new JSONObject();
            obj.put("id", UUID.randomUUID().toString());
            obj.put("jugadorId", multa.getJugadorId());
            obj.put("jugadorNombre", multa.getJugadorNombre());
            obj.put("fecha", multa.getFecha());
            obj.put("motivo", multa.getMotivo());
            obj.put("cantidad", multa.getCantidad());
            obj.put("pagada", multa.isPagada());
            
            array.put(obj);
            prefs.edit().putString(KEY_MULTAS, array.toString()).apply();
            callback.onExito("Multa registrada.");
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    public void marcarMultaPagada(String multaId, boolean pagada, ResultadoCallback callback) {
        try {
            JSONArray array = new JSONArray(prefs.getString(KEY_MULTAS, "[]"));
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (obj.getString("id").equals(multaId)) {
                    obj.put("pagada", pagada);
                    break;
                }
            }
            prefs.edit().putString(KEY_MULTAS, array.toString()).apply();
            callback.onExito(pagada ? "Multa pagada." : "Multa pendiente.");
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    public void listarMultas(ListaMultasCallback callback) {
        try {
            JSONArray array = new JSONArray(prefs.getString(KEY_MULTAS, "[]"));
            List<Multa> lista = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Multa m = new Multa();
                m.setId(obj.getString("id"));
                m.setJugadorId(obj.getString("jugadorId"));
                m.setJugadorNombre(obj.getString("jugadorNombre"));
                m.setFecha(obj.getString("fecha"));
                m.setMotivo(obj.getString("motivo"));
                m.setCantidad(obj.getDouble("cantidad"));
                m.setPagada(obj.getBoolean("pagada"));
                lista.add(m);
            }
            callback.onMultasObtenidas(lista);
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }
}
