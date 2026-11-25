package com.entrenador.pizarratactica.model;

/**
 * Opciones de los desplegables del analisis de rival (UEFA A).
 */
public final class OpcionesAnalisis {

    private OpcionesAnalisis() {}

    public static final String[] SISTEMAS = {
            "1-4-4-2 (Clasico)", "1-4-4-2 (Diamante)", "1-4-3-3 (Clasico)",
            "1-4-3-3 (Falso 9)", "1-4-2-3-1", "1-4-1-4-1", "1-4-2-2-2",
            "1-4-5-1", "1-4-6-0", "1-3-5-2", "1-3-4-3 (Clasico)",
            "1-3-4-3 (Rombo)", "1-3-4-2-1", "1-3-6-1", "1-3-3-3-1",
            "1-3-1-4-2", "1-3-2-3-2", "1-5-3-2", "1-5-4-1", "1-5-2-3",
            "1-5-2-2-1", "1-4-3-2-1 (Arbol de Navidad)",
            "1-4-2-4 (Cuadrado)", "1-2-3-5 (WM historico)"
    };

    // Fase ofensiva
    public static final String[] OFE_GEN = {"Ataque Combinativo (posicional, en corto)",
            "Ataque Directo (buscan referencia arriba)",
            "Contraataque rapido por bandas",
            "Juego de banda y centros laterales"};
    public static final String[] OFE_DEF = {"Centrales habiles en salida (juego corto)",
            "Laterales muy profundos, juegan en vertical",
            "Saltan la primera linea con largo",
            "Portero participa activamente en la salida"};
    public static final String[] OFE_MED = {"MC incrustado entre centrales (Lavolpiana)",
            "MC por delante de la defensa (organizador)",
            "Interiores buscan el carril entre lineas",
            "Mucho juego por banda, poco interior"};
    public static final String[] OFE_DEL = {"Profundidad pura, atacan la espalda",
            "Movimientos de ruptura interior (diagonales)",
            "Extremos que cierran dentro",
            "Fijadores fisicos estaticos (referencias)"};

    // Fase defensiva
    public static final String[] DEF_GEN = {"Presion Alta al hombre",
            "Presion Alta zonal (zona de inicio)",
            "Bloque Medio (esperan en su campo)",
            "Bloque Bajo / Repliegue intensivo"};
    public static final String[] DEF_DEF = {"Linea adelantada, trampa del fuera de juego",
            "Linea profunda, sin riesgo de espalda",
            "Fuertes ayudas y coberturas constantes",
            "Salen con fuerza al rechace"};
    public static final String[] DEF_MED = {"Tapan bien el carril interior",
            "Salen a presionar al mediocentro rival",
            "Contemporizan y cortan lineas de pase",
            "Faltas tacticas sistematicas"};
    public static final String[] DEF_DEL = {"Presionan activamente los centrales",
            "Canalizan la salida solo a un lado",
            "Saltan solo a un central (lado fuerte)",
            "No presionan, solo tapan lineas de pase"};

    // Transiciones
    public static final String[] TRAN_DA = {"Buscan directamente jugadores adelantados",
            "Pase seguro y conservan balon",
            "Contraataque rapido segun la zona"};
    public static final String[] TRAN_AD = {"Presion inmediata tras perdida (pressing)",
            "Repliegue intensivo a su campo",
            "Los cercanos presionan, los lejanos repliegan"};

    // Acciones a balon parado
    public static final String[] ABP_OFE = {"Corner cerrado (pierna cambiada)",
            "Corner abierto (pierna natural)",
            "Corner en corto buscando superioridad",
            "Faltas directas con tirador fijo",
            "Siempre dejan hombres para la contra"};
    public static final String[] ABP_DEF = {"Marcaje en zona puro",
            "Marcaje al hombre",
            "Marcaje mixto (zona + hombre clave)",
            "Dejan 1-2 jugadores para el contragolpe"};
}
