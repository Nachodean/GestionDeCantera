# Constitucion — Pizarra Tactica y Gestion de Entrenador

Principios innegociables. Toda especificacion, plan, tarea y linea de codigo debe cumplirlos.

1. **Simplicidad y Robustez**: Arquitectura MVC limpia en Android (Java) y modular en Python. Sin dependencias innecesarias ni configuraciones fragiles.
2. **La Spec Manda**: Todo cambio debe estar especificado con claridad. Se describen los requisitos en sintaxis EARS verificable. Si algo es ambiguo, se clarifica antes de implementar.
3. **Logica Separada de la Interfaz**: Los modelos y reglas de negocio (elementos tacticos, plantilla, minutos, multas, calculos de balance) deben ser clases puras, independientes del ciclo de vida visual del framework.
4. **Tests como Puerta**: Toda logica critica debe contar con pruebas unitarias automaticas. Prohibido dar por finalizada una tarea con tests en rojo o sin verificar la compilacion.
5. **Persistencia e Integridad de Datos**: Los datos del entrenador (sesiones, jugadores, minutos, multas) son sagrados. La persistencia local y remota debe garantizar retrocompatibilidad y nunca corromper informacion.
6. **Idioma y Usabilidad Profesional**: Codigo y nombres de simbolos en ingles/espanol coherente y autodocumentado; interfaz de usuario y documentacion 100% en espanol orientada al entrenador de futbol.
