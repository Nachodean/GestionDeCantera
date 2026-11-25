# Pizarra Táctica ⚽

Aplicación Android para entrenadores de fútbol: pizarra táctica interactiva, preparación física por objetivos, análisis del rival y gestión de la plantilla. Pensada sobre todo para tablet.

## Funcionalidades

| Módulo | Qué hace |
|---|---|
| **Sesiones** | Cada sesión tiene varios ejercicios, como las diapositivas de un PowerPoint. En cada ejercicio se arrastran al campo chinos, conos, picas, porterías, escaleras, vallas, aros, bosu, balones, jugadores numerados por color, camisetas y etiquetas de texto. Se dibujan flechas de pase, desplazamiento sin balón (discontinua), conducción (ondulada), líneas, medidas («35 m») y trazos libres. Los objetos se mueven, giran y cambian de tamaño (también con dos dedos). Tiene deshacer y rehacer, se guarda sola y se exporta a **PDF** (una página por ejercicio, con explicación y lista de material) o a imagen para compartir. |
| **Plantilla** | Alta y baja de jugadores con dorsal y posición, registro de minutos por jornada y evaluaciones. |
| **Puntuación** | Valoración de los jugadores en cada entrenamiento. |
| **Multas** | Catálogo de sanciones internas del equipo, con el bote recaudado y lo pendiente de pago. |
| **Análisis rival** | Selector con 24 sistemas tácticos y notas sobre el rival, que se guardan en el dispositivo. |
| **Preparador físico** | Ejercicios agrupados por objetivo (fuerza del tren inferior, velocidad, resistencia, core y prevención), con indicador de si necesitan material. |

Todos los datos se guardan en el propio dispositivo (`SharedPreferences`), así que la app funciona sin conexión.

## Requisitos

- Android Studio (Hedgehog o posterior)
- JDK 17
- Android SDK 34 (`minSdk` 24)

## Compilar y ejecutar

```powershell
git clone https://github.com/<tu-usuario>/PizarraTactica.git
cd PizarraTactica
.\gradlew assembleDebug
```

El APK de depuración se genera en `app/build/outputs/apk/debug/`. También puedes abrir la carpeta con Android Studio y pulsar **Run**.

### Tests

```powershell
.\gradlew testDebugUnitTest
```

Las pruebas unitarias JUnit de la lógica de dominio están en `app/src/test/`.

### Firebase (opcional)

El proyecto incluye las dependencias de Firebase, pero **compila sin configurarlo**. Para usar tu propio proyecto Firebase:

1. Crea un proyecto en la [consola de Firebase](https://console.firebase.google.com/) y registra una app Android con el paquete `com.entrenador.pizarratactica`.
2. Descarga `google-services.json` y colócalo en `app/` (en `app/google-services.json.example` tienes la estructura).
3. Antes de guardar datos reales, revisa las reglas de seguridad de Realtime Database.

`google-services.json` está en `.gitignore`: no lo subas al repositorio.

## Arquitectura

Arquitectura MVC con repositorios:

```
app/src/main/java/com/entrenador/pizarratactica/
├── model/           Modelo: entidades y repositorios (Jugador, Multa, InformeRival...)
│   └── pizarra/     Sesión → Ejercicio → Elementos y Trazos, deshacer y JSON
├── controller/      Controlador: un XxxController por pantalla (lógica y acceso a datos)
├── view/            Vista: PizarraTacticaActivity y un Fragment por sección
│   └── pizarra/     Vistas propias: PizarraView, RenderizadorPizarra, exportación PDF
└── adapter/         Adaptadores de las listas (reciben el controlador)
```

Cada pantalla sigue el mismo patrón MVC: la vista enlaza la interfaz (`recogerElementos()`), crea su controlador en `establecerEscuchadores()` y expone métodos `pintarX()` / `mostrarMensaje()`. El controlador recibe la vista, hace la lógica con los repositorios del modelo y le dice a la vista qué mostrar.

Todo el material se dibuja en código (`RenderizadorPizarra`), así que la pizarra, las miniaturas y el PDF se ven exactamente igual. Las posiciones se guardan normalizadas (0–1), por lo que un ejercicio se ve igual en móvil, tablet o PDF.

- `docs/constitution.md`: principios del proyecto.
- `specs/`: especificaciones (desarrollo guiado por especificaciones).
- `AGENTS.md` / `MEMORY.md`: contexto para asistentes de IA.

## Estado del proyecto

Proyecto en desarrollo activo. Pendiente:

- Sincronizar con Firebase (de momento solo se guarda en local).
- Ampliar la cobertura de tests.
