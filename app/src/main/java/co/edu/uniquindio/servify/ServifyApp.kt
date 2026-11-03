package co.edu.uniquindio.servify

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/** Punto de entrada de Hilt: genera el contenedor de dependencias de toda la app. */
@HiltAndroidApp
class ServifyApp : Application()
