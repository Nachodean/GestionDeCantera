package com.entrenador.pizarratactica.view.pizarra;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;

import androidx.appcompat.app.AlertDialog;

import com.entrenador.pizarratactica.model.pizarra.Colores;

/** Dialogos pequenos del editor: elegir color y escribir texto. */
public final class Dialogos {

    public interface AlElegirColor { void elegido(int color); }
    public interface AlEscribir { void escrito(String texto); }

    private Dialogos() {}

    public static void elegirColor(Context ctx, String titulo, int actual, AlElegirColor cb) {
        float dp = ctx.getResources().getDisplayMetrics().density;
        GridLayout rejilla = new GridLayout(ctx);
        rejilla.setColumnCount(6);
        int pad = (int) (16 * dp);
        rejilla.setPadding(pad, pad, pad, 0);
        final AlertDialog[] dialogo = new AlertDialog[1];
        for (int color : Colores.PALETA) {
            View muestra = new View(ctx);
            int lado = (int) (40 * dp);
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = lado;
            lp.height = lado;
            lp.setMargins((int) (5 * dp), (int) (5 * dp), (int) (5 * dp), (int) (5 * dp));
            muestra.setLayoutParams(lp);
            muestra.setBackground(circulo(color, color == actual ? 0xFF000000 : 0xFF90A4AE,
                    (int) ((color == actual ? 4 : 1) * dp)));
            muestra.setContentDescription("Color");
            muestra.setOnClickListener(v -> {
                cb.elegido(color);
                if (dialogo[0] != null) dialogo[0].dismiss();
            });
            rejilla.addView(muestra);
        }
        dialogo[0] = new AlertDialog.Builder(ctx)
                .setTitle(titulo)
                .setView(rejilla)
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public static void pedirTexto(Context ctx, String titulo, String valor, boolean multilinea,
                                  boolean numerico, AlEscribir cb) {
        float dp = ctx.getResources().getDisplayMetrics().density;
        EditText et = new EditText(ctx);
        et.setText(valor);
        et.setSelectAllOnFocus(true);
        // En horizontal, que el teclado no tape el dialogo a pantalla completa.
        et.setImeOptions(android.view.inputmethod.EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        if (numerico) {
            et.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (multilinea) {
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                    | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            et.setMinLines(3);
        } else {
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        }
        FrameLayout marco = new FrameLayout(ctx);
        int pad = (int) (20 * dp);
        marco.setPadding(pad, (int) (8 * dp), pad, 0);
        marco.addView(et, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        AlertDialog d = new AlertDialog.Builder(ctx)
                .setTitle(titulo)
                .setView(marco)
                .setPositiveButton("Aceptar", (di, w) -> cb.escrito(et.getText().toString()))
                .setNegativeButton("Cancelar", null)
                .create();
        if (d.getWindow() != null) {
            d.getWindow().setSoftInputMode(
                    android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        d.show();
        et.requestFocus();
    }

    public static GradientDrawable circulo(int color, int borde, int grosor) {
        GradientDrawable g = new GradientDrawable();
        g.setShape(GradientDrawable.OVAL);
        g.setColor(color);
        g.setStroke(grosor, borde);
        return g;
    }
}
