# MEMORY.md — Pizarra Tactica

Memoria del proyecto entre sesiones. Maximo ~50 lineas.

## Estado Actual
- Version Android (Android Studio):
  - Modulo Pizarra Tactica: rediseñado con sidebar de tablet, tokens circulares de equipo A, equipo B y balon, DrawingView para trazos tacticos y botonera de limpieza/deshacer.
  - Modulo Preparador Fisico: adaptado segun la web de referencia (entrenamientosdefutbol.es) en 4 categorias por objetivo (Fuerza tren inferior, Velocidad, Resistencia anaerobica, Core/Prevencion) con detalles y badges con/sin material.
  - Modulo Analisis Rival (UEFA A): selector con 24 sistemas tacticos y persistencia.
  - Modulo Plantilla: gestion de jugadores, minutos por jornada, evaluaciones y multas.
  - Resolucion del fallo historico: saneamiento de codificacion UTF-8 sin BOM para evitar errores de compilacion de aapt2 y javac.
  - Preparado para GitHub (2026-11-25): .gitignore completo, README.md, google-services.json fuera del repo (plugin Firebase opcional en app/build.gradle), plantilla por defecto con nombres ficticios, tildes '?' corruptas reparadas.
  - OJO: Firebase declarado pero NO usado: toda la persistencia es local (SharedPreferences).
  - Editor de sesiones rehecho (2026-11-25): sesion con varios ejercicios (como el PPT del entrenador), paleta con todo el material de su foto, flechas (pase/sin balon/conduccion/linea/medida/libre), deshacer, autoguardado 1,5 s, exportar PDF/PNG. Sustituye a PlanificacionFragment/Controller/DrawingView (borrados; copia en ../PizarraTactica_backup_antes_editor_sesiones_2026-11-25.zip).
  - Refactor MVC estilo Dermapharma (D:\dermapharma) (2026-11-25): MainActivity -> view/PizarraTacticaActivity, un XxxController por pantalla en controller/, adaptadores en adapter/, datos sacados de las vistas a model/ (TipoMulta, InformeRival(+Repository), OpcionesAnalisis, EjercicioFisico). Copia previa: ../PizarraTactica_backup_antes_mvc_2026-11-25.zip.
  - Probado en emulador movil (Medium_Phone) en horizontal y vertical; NO probado en tablet real ni el gesto de dos dedos (adb no lo simula).

## Decisiones (y por que)
- Persistencia hibrida (SharedPreferences + Firebase): permite operar sin conexion de forma inmediata y sincronizar cuando hay red.
- DrawingView desacoplado: capa transparente de dibujo superpuesta al campo de futbol para no interferir con el touch de los tokens de jugadores.
- Adopcion formal de SDD: incorporacion de `docs/constitution.md`, `specs/`, `AGENTS.md` y suite de tests unitarios JUnit.

## Aprendizajes y Errores a Evitar
- Atencion con PowerShell en Windows: nunca usar `Set-Content` con codificaciones que introduzcan BOM (`\ufeff`) o caracteres corruptos en XML/Java.
- No asumir dependencias de Google Play Services activas en emuladores basicos; mantener fallbacks locales en repositorios.
- El JDK ya no va en gradle.properties (rompe otras maquinas): en CLI usar `$env:JAVA_HOME = "$env:USERPROFILE\.jdks\jbr-17.0.14"`.
- Nunca meter datos reales de jugadores (menores) en el codigo: el repo es publicable.
- En el emulador: capturas con `adb shell screencap` + `adb pull` (la redireccion `>` de PowerShell corrompe el PNG).
- PDFs sin poppler: renderizar con la API Windows.Data.Pdf desde PowerShell.

## Proximos Pasos
- Instalar Git, git init y primer commit; elegir licencia.
- Probar el editor en tablet real (es su dispositivo principal) y ajustar tamanos si hace falta.
- Posibles mejoras: alineacion de la plantilla (pag. 1-2 del PPT), plantillas de ejercicios reutilizables.
- Ampliar cobertura de pruebas unitarias JUnit para los modelos y controladores de dominio.
- Construir y validar la version gemela completa en Python con soporte de ejecucion agil mediante `uv`.
