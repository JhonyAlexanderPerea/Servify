package co.edu.uniquindio.servify.ui.format

import java.util.Locale

private val colombia: Locale = Locale.forLanguageTag("es-CO")

/** 80000 → "$80.000" (pesos colombianos, separador de miles con punto). */
fun formatCop(amount: Number): String =
    "$" + String.format(colombia, "%,d", amount.toLong())
