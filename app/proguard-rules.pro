# Reglas de ProGuard/R8 para Pizarra Tactica.
# Ahora mismo minifyEnabled = false en release, asi que este archivo no se aplica.
# Si se activa la minificacion, conservar los modelos que se serializan (Firebase/JSON):
# -keep class com.entrenador.pizarratactica.model.** { *; }
