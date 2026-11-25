package com.entrenador.pizarratactica.view.pizarra;

import com.entrenador.pizarratactica.model.pizarra.Colores;
import com.entrenador.pizarratactica.model.pizarra.ElementoPizarra;
import com.entrenador.pizarratactica.model.pizarra.TipoElemento;

import java.util.ArrayList;
import java.util.List;

/**
 * Objeto de la paleta lateral que se arrastra al campo.
 * Guarda un "prototipo" con el tipo y los colores del objeto.
 */
public class ItemPaleta {

    public final String seccion;
    public final String nombre;
    public final TipoElemento tipo;
    public final int color;
    public final int color2;
    public final String texto;

    private ItemPaleta(String seccion, String nombre, TipoElemento tipo, int color, int color2, String texto) {
        this.seccion = seccion;
        this.nombre = nombre;
        this.tipo = tipo;
        this.color = color;
        this.color2 = color2;
        this.texto = texto;
    }

    /** Elemento de muestra para dibujar el icono de la paleta. */
    public ElementoPizarra prototipo() {
        ElementoPizarra e = new ElementoPizarra(0, tipo, 0.5f, 0.5f, color);
        e.setColor2(color2);
        e.setTexto(texto);
        if (tipo.admiteNumero()) e.setNumero(1);
        if (tipo == TipoElemento.TEXTO) e.setTexto("Aa");
        return e;
    }

    // ── Catalogo (basado en el material que se usa en las sesiones) ──

    public static final String MATERIAL = "Material";
    public static final String JUGADORES = "Jugadores";
    public static final String TEXTOS = "Textos";

    public static List<ItemPaleta> catalogo() {
        List<ItemPaleta> l = new ArrayList<>();
        int sin = ElementoPizarra.SIN_COLOR;

        // Chinos
        add(l, MATERIAL, "Chino rojo", TipoElemento.CHINO, Colores.ROJO, sin);
        add(l, MATERIAL, "Chino azul", TipoElemento.CHINO, Colores.AZUL, sin);
        add(l, MATERIAL, "Chino naranja", TipoElemento.CHINO, Colores.NARANJA, sin);
        add(l, MATERIAL, "Chino amarillo", TipoElemento.CHINO, Colores.AMARILLO, sin);
        add(l, MATERIAL, "Chino blanco", TipoElemento.CHINO, Colores.BLANCO, sin);
        add(l, MATERIAL, "Chino verde", TipoElemento.CHINO, Colores.VERDE, sin);
        // Conos
        add(l, MATERIAL, "Cono alto", TipoElemento.CONO_ALTO, 0xFFFF6D00, sin);
        add(l, MATERIAL, "Cono amarillo", TipoElemento.CONO, Colores.AMARILLO, sin);
        add(l, MATERIAL, "Cono rojo", TipoElemento.CONO, Colores.ROJO, sin);
        add(l, MATERIAL, "Cono azul", TipoElemento.CONO, Colores.AZUL, sin);
        add(l, MATERIAL, "Cono verde", TipoElemento.CONO, Colores.VERDE, sin);
        // Picas
        add(l, MATERIAL, "Pica roja", TipoElemento.PICA, Colores.ROJO, sin);
        add(l, MATERIAL, "Pica azul", TipoElemento.PICA, Colores.AZUL, sin);
        add(l, MATERIAL, "Pica blanca", TipoElemento.PICA, Colores.BLANCO, sin);
        add(l, MATERIAL, "Pica amarilla", TipoElemento.PICA, Colores.AMARILLO, sin);
        add(l, MATERIAL, "Pica madera", TipoElemento.PICA, Colores.MARRON, sin);
        add(l, MATERIAL, "Pica base amarilla", TipoElemento.PICA_BASE, Colores.AMARILLO, sin);
        add(l, MATERIAL, "Pica base roja", TipoElemento.PICA_BASE, Colores.ROJO, sin);
        add(l, MATERIAL, "Pica base azul", TipoElemento.PICA_BASE, Colores.AZUL, sin);
        add(l, MATERIAL, "Pica base verde", TipoElemento.PICA_BASE, Colores.VERDE, sin);
        // Resto de material
        add(l, MATERIAL, "Portería", TipoElemento.PORTERIA, Colores.BLANCO, sin);
        add(l, MATERIAL, "Balón", TipoElemento.BALON, Colores.BLANCO, sin);
        add(l, MATERIAL, "Balones", TipoElemento.BALONES, Colores.BLANCO, sin);
        add(l, MATERIAL, "Escalera", TipoElemento.ESCALERA, Colores.AMARILLO, sin);
        add(l, MATERIAL, "Valla", TipoElemento.VALLA, Colores.AMARILLO, sin);
        add(l, MATERIAL, "Aro rojo", TipoElemento.ARO, Colores.ROJO, sin);
        add(l, MATERIAL, "Aro verde", TipoElemento.ARO, Colores.VERDE, sin);
        add(l, MATERIAL, "Aro amarillo", TipoElemento.ARO, Colores.AMARILLO, sin);
        add(l, MATERIAL, "Aro azul", TipoElemento.ARO, Colores.AZUL, sin);
        add(l, MATERIAL, "Bosu", TipoElemento.BOSU, Colores.AZUL, sin);
        add(l, MATERIAL, "Balón medicinal", TipoElemento.BALON_MEDICINAL, Colores.NEGRO, sin);
        add(l, MATERIAL, "Fitball", TipoElemento.FITBALL, Colores.AZUL_OSCURO, sin);

        // Jugadores (el dorsal se pone solo: 1, 2, 3... por color)
        add(l, JUGADORES, "Nuestro equipo", TipoElemento.JUGADOR, Colores.BLANCO, Colores.NEGRO);
        add(l, JUGADORES, "Peto rojo", TipoElemento.JUGADOR, Colores.ROJO_OSCURO, sin);
        add(l, JUGADORES, "Peto amarillo", TipoElemento.JUGADOR, Colores.AMARILLO, sin);
        add(l, JUGADORES, "Peto azul", TipoElemento.JUGADOR, Colores.AZUL, sin);
        add(l, JUGADORES, "Peto verde", TipoElemento.JUGADOR, Colores.VERDE, sin);
        add(l, JUGADORES, "Peto morado", TipoElemento.JUGADOR, Colores.MORADO, sin);
        add(l, JUGADORES, "Negro", TipoElemento.JUGADOR, Colores.NEGRO, sin);
        add(l, JUGADORES, "Gris", TipoElemento.JUGADOR, Colores.GRIS, sin);
        add(l, JUGADORES, "Camiseta local", TipoElemento.CAMISETA, Colores.BLANCO, Colores.NEGRO);
        add(l, JUGADORES, "Camiseta visitante", TipoElemento.CAMISETA, 0xFFFF6D00, Colores.NEGRO);

        // Textos
        add(l, TEXTOS, "Texto negro", TipoElemento.TEXTO, Colores.NEGRO, sin);
        add(l, TEXTOS, "Texto blanco", TipoElemento.TEXTO, Colores.BLANCO, sin);
        add(l, TEXTOS, "Etiqueta roja", TipoElemento.TEXTO, Colores.BLANCO, 0xFFC0504D);
        add(l, TEXTOS, "Etiqueta morada", TipoElemento.TEXTO, Colores.BLANCO, 0xFF8064A2);
        return l;
    }

    private static void add(List<ItemPaleta> l, String seccion, String nombre,
                            TipoElemento tipo, int color, int color2) {
        l.add(new ItemPaleta(seccion, nombre, tipo, color, color2, ""));
    }
}
