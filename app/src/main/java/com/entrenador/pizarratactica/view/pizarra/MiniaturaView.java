package com.entrenador.pizarratactica.view.pizarra;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.entrenador.pizarratactica.model.pizarra.ElementoPizarra;
import com.entrenador.pizarratactica.model.pizarra.Ejercicio;

/**
 * Dibuja en pequeno un objeto de la paleta o un ejercicio completo
 * (miniaturas de la lista de sesiones y de la tira de ejercicios).
 */
public class MiniaturaView extends View {

    private final RenderizadorPizarra render = new RenderizadorPizarra();
    private ElementoPizarra elemento;
    private Ejercicio ejercicio;
    private final RectF caja = new RectF();

    public MiniaturaView(Context context) { super(context); }

    public MiniaturaView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); }

    public void mostrarElemento(ElementoPizarra e) {
        this.elemento = e;
        this.ejercicio = null;
        invalidate();
    }

    public void mostrarEjercicio(Ejercicio e) {
        this.ejercicio = e;
        this.elemento = null;
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas c) {
        super.onDraw(c);
        caja.set(getPaddingLeft(), getPaddingTop(),
                getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        if (ejercicio != null) {
            RectF t = RenderizadorPizarra.calcularTablero(caja.width(), caja.height());
            t.offset(caja.left, caja.top);
            render.dibujarEjercicio(c, ejercicio, t);
        } else if (elemento != null) {
            render.dibujarIcono(c, elemento, caja);
        }
    }
}
