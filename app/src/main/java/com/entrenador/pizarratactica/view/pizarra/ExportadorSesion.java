package com.entrenador.pizarratactica.view.pizarra;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import androidx.core.content.FileProvider;

import com.entrenador.pizarratactica.model.pizarra.Ejercicio;
import com.entrenador.pizarratactica.model.pizarra.Sesion;
import com.entrenador.pizarratactica.model.pizarra.TipoElemento;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Exporta una sesion a PDF (una pagina por ejercicio, como el PowerPoint)
 * o un ejercicio suelto a imagen PNG, y abre el menu de compartir.
 */
public final class ExportadorSesion {

    private static final int PAG_ANCHO = 842;  // A4 horizontal en puntos
    private static final int PAG_ALTO = 595;
    private static final int VERDE = 0xFF1B5E20;

    private ExportadorSesion() {}

    // ── PDF ──────────────────────────────────────────────────────────

    public static File exportarPdf(Context ctx, Sesion s) throws IOException {
        PdfDocument doc = new PdfDocument();
        RenderizadorPizarra render = new RenderizadorPizarra();
        try {
            for (int i = 0; i < s.numEjercicios(); i++) {
                PdfDocument.PageInfo info = new PdfDocument.PageInfo.Builder(PAG_ANCHO, PAG_ALTO, i + 1).create();
                PdfDocument.Page pagina = doc.startPage(info);
                dibujarPagina(pagina.getCanvas(), s, i, render);
                doc.finishPage(pagina);
            }
            File archivo = new File(carpeta(ctx), nombreArchivo(s) + ".pdf");
            try (FileOutputStream out = new FileOutputStream(archivo)) {
                doc.writeTo(out);
            }
            return archivo;
        } finally {
            doc.close();
        }
    }

    private static void dibujarPagina(Canvas c, Sesion s, int indice, RenderizadorPizarra render) {
        Ejercicio ej = s.getEjercicio(indice);
        c.drawColor(Color.WHITE);

        // Cabecera
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(VERDE);
        c.drawRect(0, 0, PAG_ANCHO, 44, p);
        TextPaint tp = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        tp.setColor(Color.WHITE);
        tp.setTextSize(16);
        tp.setTypeface(Typeface.DEFAULT_BOLD);
        String cabecera = s.getTituloVisible()
                + (s.getFecha().isEmpty() ? "" : "  ·  " + s.getFecha())
                + (s.getObjetivo().isEmpty() ? "" : "  ·  " + s.getObjetivo());
        c.drawText(recortar(cabecera, tp, PAG_ANCHO - 140), 20, 28, tp);
        tp.setTypeface(Typeface.DEFAULT);
        tp.setTextSize(12);
        String pag = "Ejercicio " + (indice + 1) + " de " + s.numEjercicios();
        c.drawText(pag, PAG_ANCHO - 20 - tp.measureText(pag), 28, tp);

        // Campo (4:3) a la izquierda
        RectF tablero = new RectF(20, 58, 20 + 520, 58 + 390);
        render.dibujarEjercicio(c, ej, tablero);
        Paint borde = new Paint(Paint.ANTI_ALIAS_FLAG);
        borde.setStyle(Paint.Style.STROKE);
        borde.setColor(0xFF9E9E9E);
        c.drawRect(tablero, borde);

        // Columna derecha: titulo, duracion, descripcion, material
        float x = 560, ancho = PAG_ANCHO - x - 20, y = 70;
        tp.setColor(0xFF212121);
        tp.setTypeface(Typeface.DEFAULT_BOLD);
        tp.setTextSize(16);
        y = bloque(c, ej.getTitulo().isEmpty() ? "Ejercicio " + (indice + 1) : ej.getTitulo(), tp, x, y, ancho);
        if (ej.getDuracionMin() > 0) {
            tp.setTypeface(Typeface.DEFAULT);
            tp.setTextSize(12);
            tp.setColor(VERDE);
            y = bloque(c, "Duración: " + ej.getDuracionMin() + " min", tp, x, y + 4, ancho);
        }
        if (!ej.getDescripcion().isEmpty()) {
            tp.setColor(0xFF424242);
            tp.setTypeface(Typeface.DEFAULT);
            tp.setTextSize(11);
            y = bloque(c, ej.getDescripcion(), tp, x, y + 10, ancho);
        }
        String material = textoMaterial(ej);
        if (!material.isEmpty()) {
            tp.setColor(0xFF212121);
            tp.setTypeface(Typeface.DEFAULT_BOLD);
            tp.setTextSize(11);
            y = bloque(c, "Material", tp, x, y + 14, ancho);
            tp.setTypeface(Typeface.DEFAULT);
            bloque(c, material, tp, x, y + 2, ancho);
        }

        // Pie: duracion total de la sesion
        if (s.duracionTotal() > 0) {
            tp.setColor(0xFF757575);
            tp.setTextSize(10);
            c.drawText("Duración total de la sesión: " + s.duracionTotal() + " min", 20, PAG_ALTO - 20, tp);
        }
    }

    /** "12 Chino · 2 Portería · 1 Escalera" */
    public static String textoMaterial(Ejercicio ej) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<TipoElemento, Integer> m : ej.recuentoMaterial().entrySet()) {
            if (sb.length() > 0) sb.append("\n");
            sb.append("• ").append(m.getValue()).append(" × ").append(m.getKey().getNombre());
        }
        return sb.toString();
    }

    private static float bloque(Canvas c, String texto, TextPaint tp, float x, float y, float ancho) {
        StaticLayout sl = new StaticLayout(texto, tp, (int) ancho, Layout.Alignment.ALIGN_NORMAL, 1.15f, 0, false);
        c.save();
        c.translate(x, y);
        sl.draw(c);
        c.restore();
        return y + sl.getHeight();
    }

    private static String recortar(String t, Paint p, float max) {
        if (p.measureText(t) <= max) return t;
        while (t.length() > 1 && p.measureText(t + "…") > max) t = t.substring(0, t.length() - 1);
        return t + "…";
    }

    // ── Imagen ───────────────────────────────────────────────────────

    public static File exportarImagen(Context ctx, Sesion s, int indice) throws IOException {
        int w = 1600, h = 1200;
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        new RenderizadorPizarra().dibujarEjercicio(c, s.getEjercicio(indice), new RectF(0, 0, w, h));
        File archivo = new File(carpeta(ctx), nombreArchivo(s) + "_ej" + (indice + 1) + ".png");
        try (FileOutputStream out = new FileOutputStream(archivo)) {
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        } finally {
            bmp.recycle();
        }
        return archivo;
    }

    // ── Compartir ────────────────────────────────────────────────────

    public static void compartir(Context ctx, File archivo, String mime) {
        Uri uri = FileProvider.getUriForFile(ctx, ctx.getPackageName() + ".archivos", archivo);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType(mime);
        i.putExtra(Intent.EXTRA_STREAM, uri);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        ctx.startActivity(Intent.createChooser(i, "Compartir sesión"));
    }

    private static File carpeta(Context ctx) {
        File dir = new File(ctx.getCacheDir(), "exportaciones");
        if (!dir.exists()) //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        return dir;
    }

    private static String nombreArchivo(Sesion s) {
        String base = "sesion_" + (s.getNumero().isEmpty() ? "sin_numero" : s.getNumero());
        return base.replaceAll("[^A-Za-z0-9_-]", "_");
    }
}
