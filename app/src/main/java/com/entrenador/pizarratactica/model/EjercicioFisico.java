package com.entrenador.pizarratactica.model;

/**
 * Ejercicio del preparador fisico (nombre, objetivo, material y foto).
 */
public class EjercicioFisico {

    private final String nombre;
    private final String focoPrincipal;
    private final String material;
    private final int idImagen;
    private final boolean sinMaterial;

    public EjercicioFisico(String nombre, String focoPrincipal, String material,
                           int idImagen, boolean sinMaterial) {
        this.nombre = nombre;
        this.focoPrincipal = focoPrincipal;
        this.material = material;
        this.idImagen = idImagen;
        this.sinMaterial = sinMaterial;
    }

    public String getNombre() { return nombre; }
    public String getFocoPrincipal() { return focoPrincipal; }
    public String getMaterial() { return material; }
    public int getIdImagen() { return idImagen; }
    public boolean isSinMaterial() { return sinMaterial; }
}
