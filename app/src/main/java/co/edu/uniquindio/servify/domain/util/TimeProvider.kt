package co.edu.uniquindio.servify.domain.util

/**
 * Fuente de la hora actual. Se inyecta (en vez de llamar a System.currentTimeMillis() dentro de
 * los casos de uso) para que las pruebas puedan fijar el tiempo y no dependan del reloj real.
 */
fun interface TimeProvider {
    fun now(): Long
}
