# AGENTS.md — Pizarra Tactica (Android Studio)

Aplicacion Android profesional para entrenadores de futbol: pizarra tactica interactiva, preparacion fisica por objetivos, analisis rival UEFA A y gestion integral de plantilla.

## Stack y Estructura
- Android SDK 34, Java 8/17, Gradle 8.1.4, Material Components, Firebase / SharedPreferences.
- `app/src/main/java/com/entrenador/pizarratactica/`:
  - `model/`: Entidades y repositorios (Jugador, Multa, TipoMulta, InformeRival, EjercicioFisico, *Repository...).
  - `model/pizarra/`: Sesion, Ejercicio, ElementoPizarra, Trazo, Historial (deshacer) y SesionJson.
  - `controller/`: Un `XxxController` por pantalla (PizarraTacticaController, SesionesController, EditorSesionController, PlantillaController...).
  - `view/`: `PizarraTacticaActivity` (pantalla principal) y los Fragments de cada seccion.
  - `view/pizarra/`: Vistas propias de la pizarra: PizarraView (tactil), RenderizadorPizarra (dibujo), MiniaturaView, Dialogos, ExportadorSesion (PDF/PNG).
  - `adapter/`: Adaptadores de RecyclerView; reciben el controlador para los clics.
- `app/src/test/`: Pruebas unitarias JUnit para logica de dominio.
- `docs/constitution.md` y `specs/`: Documentacion y especificaciones SDD.

## Comandos
- Compilar: `.\gradlew assembleDebug`
- Limpiar: `.\gradlew clean`
- Ejecutar tests: `.\gradlew testDebugUnitTest`

## Convenciones
- Interfaz y comentarios en espanol. Codigo estructurado bajo patron MVC y Repository (mismo estilo que el proyecto Dermapharma):
  - Nombres: Activity principal `PizarraTacticaActivity` (nunca MainActivity); controladores `XxxController`; vistas `XxxFragment`/`XxxActivity`; adaptadores `XxxAdapter`.
  - Vista: `recogerElementos()` + `establecerEscuchadores()` (crea `new XxxController(this)`), y metodos publicos `pintarX()`, `mostrarMensaje()`, dialogos... que llama el controlador. Sin logica ni acceso a datos.
  - Controlador: recibe la vista en el constructor, usa los repositorios del modelo y llama a la vista. Puede ser el escuchador (p. ej. `EditorSesionController implements PizarraView.Listener`).
  - Logica pura (calculos, catalogos, formatos) en `model/` para poder testearla.
- Formatos de archivo siempre en UTF-8 estricto sin BOM para evitar errores en javac/aapt2.
- Manejo seguro de insets y themes evitando pantallas negras en emuladores o tablets.

## Reglas de Dominio / Trampas Conocidas
- Sesiones/pizarra: `Sesion` -> lista de `Ejercicio` (uno por "diapositiva") -> `ElementoPizarra` + `Trazo` (en `model/pizarra/`, Java puro y testeado).
  - Coordenadas SIEMPRE normalizadas 0..1 sobre un tablero 4:3; nunca guardar pixeles.
  - Todo se dibuja en codigo con `RenderizadorPizarra` (pizarra, miniaturas y PDF usan el mismo); no anadir imagenes para el material.
  - Persistencia en SharedPreferences clave `sesiones_v2` (`SesionJson`). La clave antigua `lista_sesiones` se migra una vez y NO se borra.
  - Material nuevo: anadir al enum `TipoElemento`, dibujarlo en `RenderizadorPizarra` y meterlo en `ItemPaleta.catalogo()`.
- Preparador fisico: ejercicios organizados por objetivos (Fuerza tren inferior, Velocidad, Resistencia, Core/Prevencion) con badge visual de material.
- No guardar nunca datos sensibles ni romper el esquema de datos en `SharedPreferences` o Firebase.

## Memoria
- Al empezar, lee `MEMORY.md` para conocer el estado y decisiones.
- Al terminar cada tarea, actualiza `MEMORY.md`.

## Limites
- ✅ Siempre: ejecutar `.\gradlew testDebugUnitTest` y verificar compilacion antes de dar por cerrada una tarea.
- ⚠️ Pregunta antes: agregar dependencias pesadas en `build.gradle` o cambiar esquemas de datos.
- 🚫 Nunca: escribir archivos con codificaciones corruptas o BOM que rompan el compilador.
