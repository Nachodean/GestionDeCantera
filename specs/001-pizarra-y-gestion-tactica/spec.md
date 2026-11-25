# Spec 001 — Pizarra Tactica y Gestion Integral de Entrenador

Estado: aprobada

## Contexto y Objetivo
Facilitar al entrenador de futbol una aplicacion completa para preparar partidos, diseñar sesiones tacticas en pizarra digital interactiva, planificar entrenamientos fisicos orientados a objetivos reales de rendimiento y rehabilitacion, y gestionar su plantilla (asistencia, minutos de competicion, evaluacion de rendimiento y regimen disciplinario de multas).

## Usuarios / Actores
- Entrenador principal y segundo entrenador de futbol base o semiprofesional.
- Preparador fisico del cuerpo tecnico.

## Historias de Usuario
- HU-1: Como entrenador, quiero una pizarra digital interactiva para posicionar y mover jugadores de ambos equipos y el balon de forma fluida.
- HU-2: Como entrenador, quiero dibujar jugadas, trayectorias y zonas tacticas sobre el campo con opciones de borrado.
- HU-3: Como preparador fisico, quiero consultar y organizar ejercicios por objetivos especificos (fuerza tren inferior, velocidad, resistencia, core/prevencion) conociendo el material necesario.
- HU-4: Como analista o entrenador, quiero registrar informes y sistemas tacticos del equipo rival de cara a la jornada.
- HU-5: Como entrenador, quiero gestionar los jugadores de la plantilla, registrar los minutos jugados por jornada, calificar su rendimiento y controlar las multas.

## Requisitos Funcionales (EARS)
- RF-1: CUANDO el usuario arrastra un token (Equipo A, Equipo B, Balon) en la pizarra, EL SISTEMA debe actualizar su posicion visual y coordenadas continuas en tiempo real.
- RF-2: CUANDO el usuario activa el modo de dibujo tactico, EL SISTEMA debe permitir trazar lineas continuas sobre el campo sin desplazar accidentalmente los tokens.
- RF-3: CUANDO el usuario pulsa 'Borrar Lineas', EL SISTEMA debe limpiar unicamente las lineas dibujadas conservando los jugadores en su posicion.
- RF-4: CUANDO el usuario pulsa 'Limpiar Todo', EL SISTEMA debe reiniciar la pizarra dejando el campo despejado.
- RF-5: CUANDO el usuario selecciona un ejercicio del preparador fisico, EL SISTEMA debe mostrar una ficha tecnica con titulo, foco de trabajo, material necesario y categorizacion (Con/Sin material).
- RF-6: EL SISTEMA debe categorizar los ejercicios fisicos en al menos: Fuerza y Rehabilitacion Tren Inferior, Velocidad y Agilidad, Resistencia Anaerobica, y Prevencion de Lesiones/Core.
- RF-7: CUANDO el usuario guarda un informe de analisis rival, EL SISTEMA debe persistir el sistema seleccionado (entre 24 sistemas tacticos estandar), fortalezas, debilidades y plan de partido.
- RF-8: CUANDO se añade o elimina un jugador, EL SISTEMA debe actualizar la plantilla persistiendo nombre, apellidos, dorsal y posicion.
- RF-9: CUANDO se registra una multa, EL SISTEMA debe computar el balance total y el importe pendiente de cobro en tiempo real.
- RF-10: CUANDO se guardan minutos de competicion por jornada, EL SISTEMA debe validar que el tiempo sea mayor o igual a cero y registrarlo por cada jugador participante.

## Requisitos No Funcionales
- RNF-1: Rendimiento visual sin parpadeos ni tirones (60 FPS estables al arrastrar tokens).
- RNF-2: Compatibilidad multiplataforma en conceptos (disponible en Android Studio y disponible en version Python).
- RNF-3: Interfaz visual adaptada a tablets y pantallas de escritorio.
- RNF-4: Suite de tests unitarios que verifique modelos y reglas de negocio.

## Casos Limite
- Pizarra sin elementos: el boton de limpiar no debe provocar excepciones.
- Minutos negativos o vacios: deben ser ignorados o forzados a cero.
- Dorsales duplicados o datos incompletos: deben validarse correctamente.
- Balances de multas con 0 euros pendientes.

## Criterios de Finalizacion
- Todos los requisitos cubiertos por implementacion y pruebas unitarias en verde.
- Compilacion sin errores (`BUILD SUCCESSFUL`).
