# Plan 001 — Pizarra Tactica y Gestion Integral de Entrenador

## Arquitectura y Componentes
1. **Modelos de Dominio (Puros y Testeables)**:
   - `ElementoTactico`: Tipo de material (JUGADOR_LOCAL, JUGADOR_VISITANTE, BALON, CONO, VALLA, etc.), coordenadas `(x, y)`, identificador unico.
   - `SesionEntrenamiento`: Contenedor de elementos, pila de deshacer/rehacer, metadatos de sesion.
   - `Jugador`: Nombre, apellidos, dorsal, posicion (`POR`, `DEF`, `MED`, `DEL`), estado activo.
   - `Multa`: Jugador, motivo, fecha, importe numerico, estado de pago (`pagada: boolean`).
   - `RegistroMinutos`: Jugador, jornada, rival, fecha, minutos acumulados.
   - `EjercicioFisico`: Categoria/objetivo, titulo, foco principal, material requerido, indicador con/sin material.

2. **Capa de Control e Interfaz (Android)**:
   - `PlanificacionFragment` y `DrawingView`: Gestion de eventos multitouch, arrastre con `MotionEvent.ACTION_MOVE`, renderizado de lineas con `Path` y `Paint`.
   - `PreparadorFragment`: Carruseles horizontales agrupados por objetivos fisicos, dialog modal de detalle.
   - `AnalisisRivalFragment`: Desplegable de 24 sistemas tacticos UEFA A, campos de texto y persistencia.
   - `PlantillaFragment` (con sub-fragmentos): Tabs para Jugadores, Minutos, Evaluaciones y Multas.

3. **Capa de Control e Interfaz (Python)**:
   - Equivalente modular en Python con estructura limpia:
     - `models/`: Clases de dominio dataclass / Pydantic.
     - `services/`: Logica de calculo de multas, filtrado de ejercicios, persistencia en JSON/SQLite.
     - `ui/`: Interfaz interactiva para el usuario.
     - `tests/`: Suite `pytest` que valida las reglas de negocio.

## Estrategia de Tests
- `SesionEntrenamientoTest`:
  - Agregar tokens y comprobar recuento y posiciones (cubre RF-1, RF-4).
  - Pila deshacer/rehacer (cubre RF-1).
  - Limpieza de sesion (cubre RF-4).
- `JugadorTest`:
  - Creacion y validacion de datos (cubre RF-8).
- `MultaTest`:
  - Calculo de totales y pendientes antes y despues del pago (cubre RF-9).
- `RegistroMinutosTest`:
  - Validacion y sumatorio de minutos por jornada (cubre RF-10).
