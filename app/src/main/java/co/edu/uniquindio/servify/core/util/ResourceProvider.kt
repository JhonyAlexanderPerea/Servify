package co.edu.uniquindio.servify.core.util

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Acceso a los textos de res/values desde los ViewModels sin depender del Context.
 * En los tests se reemplaza por un fake (ver FakeResourceProvider).
 */
interface ResourceProvider {
    fun getString(@StringRes id: Int): String

    fun getString(@StringRes id: Int, vararg formatArgs: Any): String
}

class ResourceProviderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ResourceProvider {

    override fun getString(@StringRes id: Int): String = context.getString(id)

    override fun getString(@StringRes id: Int, vararg formatArgs: Any): String =
        context.getString(id, *formatArgs)
}
