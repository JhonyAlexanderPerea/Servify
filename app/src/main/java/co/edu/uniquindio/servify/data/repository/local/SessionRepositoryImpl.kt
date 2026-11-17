package co.edu.uniquindio.servify.data.repository.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import co.edu.uniquindio.servify.domain.model.UserRole
import co.edu.uniquindio.servify.domain.model.UserSession
import co.edu.uniquindio.servify.domain.repository.SessionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@Singleton
class SessionRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : SessionRepository {

    private object Keys {
        val USER_ID = stringPreferencesKey("user_id")
        val ROLE = stringPreferencesKey("role")
    }

    // Se emite de nuevo cada vez que cambian las preferencias guardadas
    override val session: Flow<UserSession?> = context.dataStore.data.map { prefs ->
        val userId = prefs[Keys.USER_ID]
        val role = prefs[Keys.ROLE]?.let { name -> runCatching { UserRole.valueOf(name) }.getOrNull() }

        // Si falta alguno de los datos (o el rol es desconocido), no hay sesión
        if (userId.isNullOrBlank() || role == null) null else UserSession(userId = userId, role = role)
    }

    override suspend fun saveSession(userId: String, role: UserRole) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_ID] = userId
            prefs[Keys.ROLE] = role.name
        }
    }

    override suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}
