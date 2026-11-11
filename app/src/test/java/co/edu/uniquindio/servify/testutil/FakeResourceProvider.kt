package co.edu.uniquindio.servify.testutil

import co.edu.uniquindio.servify.core.util.ResourceProvider

/**
 * ResourceProvider para pruebas unitarias (sin Android). Devuelve el texto configurado para cada id
 * o, si no hay, "string_<id>" para poder verificar qué mensaje pidió el ViewModel.
 */
class FakeResourceProvider(private val strings: Map<Int, String> = emptyMap()) : ResourceProvider {

    override fun getString(id: Int): String = strings[id] ?: "string_$id"

    override fun getString(id: Int, vararg formatArgs: Any): String =
        String.format(getString(id), *formatArgs)
}
