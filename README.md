# Servify

Servify es una aplicación Android nativa para conectar clientes con proveedores de servicios locales (hogar, educación, mascotas, tecnología, transporte y salud). El proyecto nació como trabajo académico de la Universidad del Quindío y hoy funciona como un marketplace completo: publicación de ofertas, búsqueda geolocalizada, solicitudes de servicio, reseñas, moderación, notificaciones en tiempo real y un sistema de reputación con niveles e insignias.

- **Paquete:** `co.edu.uniquindio.servify`
- **Versión:** `1.1.0` (`versionCode 2`)
- **Plataforma:** Android `minSdk 28` · `compileSdk 37` · `targetSdk 37`
- **Idiomas:** inglés (por defecto) y español (`values` / `values-es`)
- **Backend:** Firebase Authentication + Cloud Firestore
- **Distribución:** APK de release vía Firebase App Distribution (no Play Store)

---

## Tabla de contenido

1. [Características](#características)
2. [Stack tecnológico](#stack-tecnológico)
3. [Arquitectura](#arquitectura)
4. [Estructura del proyecto](#estructura-del-proyecto)
5. [Capa de dominio](#capa-de-dominio)
6. [Capa de datos](#capa-de-datos)
7. [Modelo de datos en Firestore](#modelo-de-datos-en-firestore)
8. [Inyección de dependencias](#inyección-de-dependencias)
9. [Navegación y pantallas](#navegación-y-pantallas)
10. [Flujos funcionales](#flujos-funcionales)
11. [Gamificación y reputación](#gamificación-y-reputación)
12. [Moderación](#moderación)
13. [Notificaciones](#notificaciones)
14. [Modo offline](#modo-offline)
15. [Seguridad](#seguridad)
16. [Configuración del entorno](#configuración-del-entorno)
17. [Compilar, ejecutar y probar](#compilar-ejecutar-y-probar)
18. [Optimización del APK](#optimización-del-apk)
19. [Internacionalización y diseño](#internacionalización-y-diseño)
20. [Limitaciones conocidas](#limitaciones-conocidas)
21. [Solución de problemas](#solución-de-problemas)
22. [Convenciones de contribución](#convenciones-de-contribución)

---

## Características

### Autenticación y cuenta
- Registro con nombre, apellido, correo, contraseña y ciudad.
- Inicio de sesión con correo/contraseña y con Google Sign-In.
- Verificación de correo electrónico: el login se rechaza con `EMAIL_NOT_VERIFIED` hasta verificar (los moderadores están exentos).
- Recuperación de contraseña por correo con **deep link** (`https://<proyecto>.firebaseapp.com/__/auth/action`).
- Sesión persistente en `DataStore` (usuario y rol) con restauración automática.
- Edición de perfil, foto de avatar (Cloudinary) y eliminación de cuenta con confirmación por contraseña.

### Publicaciones (ofertas de servicio)
- Asistente de creación por pasos: categoría → subcategoría → datos → ubicación → fotos → vista previa.
- **Clasificación automática con IA** del título/descripción para sugerir categoría (OpenRouter con fallback local por palabras clave).
- Selección de ubicación con **mapa Mapbox**, GPS y geocodificación inversa (Geocoder nativo con fallback a Mapbox).
- Carga de hasta varias imágenes a **Cloudinary** mediante subida sin firma.
- Estados: `PENDIENTE`, `VERIFICADA`, `APPROVED` (legado), `RECHAZADA`, `FINALIZADA`, `DRAFT`, `PAUSED`.
- Búsqueda, filtros por categoría/precio/calificación, orden y vista de mapa.
- Catálogo inicial: **Hogar, Educación, Mascotas, Tecnología, Transporte y Salud** (`SeedCategories`).
- Acción "Me interesa" (voto) con contador y IDs de interesados.
- Comentarios/reseñas con calificación de 1 a 5; recálculo del promedio de la publicación.

### Solicitudes de servicio
- Reserva de un cupo para una publicación con fecha/hora, nota, dirección y ubicación.
- **ID determinista** `req_<publicacion>_<timestamp>` para evitar doble reserva del mismo cupo.
- Validación de que el usuario no solicite su propio servicio.
- Estados: `PENDING`, `ACCEPTED`, `REJECTED`, `CANCELLED`, `COMPLETED`.
- Notificación al proveedor al recibir la solicitud y al cliente al ser aceptada.

### Moderación
- Panel exclusivo para rol `MODERATOR` con métricas (pendientes, verificadas hoy, rechazadas, finalizadas).
- Cola FIFO de publicaciones pendientes.
- Aprobación/rechazo con motivo obligatorio; notificación automática al autor.
- Finalización de publicaciones (marcar como resueltas/finalizadas).
- Restricciones de estado aplicadas en el dominio, no solo ocultando botones en la UI.

### Reputación y logros
- Puntos por crear publicaciones, recibir votos y publicaciones verificadas.
- Niveles: **Principiante**, **Profesional**, **Experto**, **Maestro**.
- 7 insignias evaluadas con funciones puras y progreso visual.
- Notificación automática al desbloquear una insignia.
- Pantallas de estadísticas, logros y confianza/reputación.

### Tiempo real y offline
- Flujos reactivos basados en `callbackFlow` + `addSnapshotListener` sobre Firestore.
- Caché offline de Firestore habilitada por defecto.
- Monitor de conectividad con banner global de "sin conexión".

---

## Stack tecnológico

| Área | Tecnología | Versión |
|---|---|---|
| Lenguaje | Kotlin | 2.3.20 |
| Build | Android Gradle Plugin | 9.4.1 |
| UI | Jetpack Compose + Material 3 | BOM 2026.02.01 |
| Navegación | Navigation Compose | 2.9.6 |
| DI | Hilt + KSP | 2.59.2 |
| Async | Coroutines + Flow | — |
| Auth/DB | Firebase Auth + Cloud Firestore | BOM 35.1.0 |
| Red | Retrofit + OkHttp + Gson | 3.0.0 / 5.2.1 / — |
| Mapas | Mapbox Maps SDK + Geocoding | 11.22.0 |
| Imágenes | Coil (Compose) | 3.4.0 |
| Preferencias | DataStore Preferences | 1.2.1 |
| Ubicación | Play Services Location | 21.3.0 |
| IA | OpenRouter API (modelo `:free`) | — |
| Imágenes en la nube | Cloudinary (unsigned upload) | — |
| Pruebas | JUnit4, coroutines-test, Compose UI Test, Hilt Testing | — |
| Serialización | kotlinx.serialization | 1.9.0 |

Dependencias declaradas en `gradle/libs.versions.toml`; el repositorio de Mapbox se agrega en `settings.gradle.kts`. Las versiones de SDK se fijan en el bloque `android` de `app/build.gradle.kts`.

---

## Arquitectura

Servify sigue **Clean Architecture** combinada con **MVVM** y **Unidirectional Data Flow** (UDF) sobre Compose.

```
┌─────────────────────────────────────────────────────────────┐
│ Presentación (features/*)                                   │
│  Compose Screen  ──  ViewModel (StateFlow<UiState>)          │
│         │                    │                              │
│         └──── eventos ───────┘                              │
└────────────────────────────┬────────────────────────────────┘
                             │ casos de uso (suspend / Flow)
┌────────────────────────────▼────────────────────────────────┐
│ Dominio (domain/*)                                          │
│  model · rules (puras) · service · usecase · repository      │
└────────────────────────────┬────────────────────────────────┘
                             │ interfaces de repositorio
┌────────────────────────────▼────────────────────────────────┐
│ Datos (data/*)                                              │
│  Firestore · remote (OpenRouter/Mapbox/Cloudinary)          │
│  memory (tests) · seed · local (DataStore) · util           │
└────────────────────────────┬────────────────────────────────┘
                             │
                    Firebase / APIs externas
```

Principios aplicados:

- **Dominio independiente de Android** en modelos, reglas y contratos de repositorio.
- **Reglas de negocio puras y testeables** en `domain/rules` (sin IO, sin UI).
- **Casos de uso** como única puerta de entrada a la lógica; los ViewModels no hablan con Firestore.
- **Repositorios como interfaces** en `domain/repository`, implementados en `data`.
- **DTO ↔ dominio** con mapeos explícitos; los DTO nunca llegan a la UI.
- **Estado inmutable** con `data class` + `StateFlow`; eventos puntuales como `SharedFlow`/`Channel` cuando aplica.
- **Hilt** para composición de dependencias en todas las capas.

### Flujo típico de una acción

```
Screen.onClick()
  → ViewModel.función()
    → UseCase (suspend)
      → Repository (interfaz)
        → Implementación Firestore / API
    ← Resultado
  ← ViewModel actualiza StateFlow
← Compose recompone
```

---

## Estructura del proyecto

```
Servify/
├── app/
│   ├── build.gradle.kts            # SDKs, firma release, R8, ABIs, tokens
│   ├── google-services.json        # NO versionado (credenciales Firebase)
│   ├── release.jks                 # NO versionado (keystore de release)
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/co/edu/uniquindio/servify/
│       │   │   ├── MainActivity.kt
│       │   │   ├── ServifyApp.kt
│       │   │   ├── core/               # utilidades transversales (Result, etc.)
│       │   │   ├── di/                 # módulos Hilt
│       │   │   ├── domain/
│       │   │   │   ├── model/          # entidades y enums
│       │   │   │   ├── repository/     # contratos
│       │   │   │   ├── rules/          # lógica pura (puntos, insignias, estados)
│       │   │   │   ├── service/        # GamificationService, etc.
│       │   │   │   ├── usecase/        # casos de uso por área
│       │   │   │   └── util/           # TimeProvider, distancias, etc.
│       │   │   ├── data/
│       │   │   │   ├── model/dto/      # DTOs Firestore
│       │   │   │   ├── repository/
│       │   │   │   │   ├── firestore/  # implementaciones reales
│       │   │   │   │   ├── memory/     # implementaciones para tests
│       │   │   │   │   └── remote/     # clasificador OpenRouter
│       │   │   │   ├── remote/         # user ext, geocoding, AI, Cloudinary
│       │   │   │   ├── local/          # DataStore (sesión)
│       │   │   │   ├── seed/           # datos semilla para tests
│       │   │   │   └── util/           # mapeos y helpers
│       │   │   ├── features/           # pantalla + ViewModel por funcionalidad
│       │   │   ├── navigation/         # rutas, grafo, bottom bar, sesión
│       │   │   └── ui/                 # tema, componentes, íconos
│       │   └── res/
│       │       ├── values/             # inglés (default)
│       │       ├── values-es/          # español
│       │       └── drawable/, mipmap/…
│       ├── test/                       # pruebas unitarias JVM
│       └── androidTest/                # pruebas instrumentadas + HiltTestRunner
├── docs/
│   ├── OFFLINE_PERSISTENCE.md
│   ├── tareas/                         # enunciados de las actividades
│   └── guias-profesor/                 # guías de referencia de Firestore
├── firebase.json
├── firestore.rules
├── firestore.indexes.json
├── gradle/libs.versions.toml
├── local.properties.example
└── README.md
```

---

## Capa de dominio

### Modelos (`domain/model`)

| Modelo | Descripción |
|---|---|
| `User` | Usuario con rol, puntos, insignias, nivel de reputación, índice de confianza y datos de contacto. |
| `UserRole` | `USER`, `CLIENT`, `PROVIDER`, `MODERATOR`. |
| `Publication` | Oferta de servicio: título, precios, imágenes, ubicación, estado, autor/proveedor, rating y votos. |
| `PublicationStatus` | `PENDIENTE`, `VERIFICADA`, `APPROVED`, `RECHAZADA`, `FINALIZADA`, `DRAFT`, `PAUSED`. |
| `ServiceRequest` | Solicitud/cita: publicación, cliente, proveedor, fecha, estado, nota, ubicación y total. |
| `RequestStatus` | `PENDING`, `ACCEPTED`, `REJECTED`, `CANCELLED`, `COMPLETED`. |
| `Comment` | Reseña con autor, calificación (1-5), texto y fecha. |
| `Category` / `Subcategory` | Catálogo de categorías con ícono y colores. |
| `Notification` / `NotificationType` | Notificación interna y su tipo. |
| `Location` | Latitud/longitud con utilidades de distancia. |
| `Badge` | 7 insignias con título y requisito. |
| `Level` | Niveles por puntos: 0, 200, 600, 1500. |
| `ReputationLevel` | `BRONZE`, `SILVER`, `GOLD`, `PLATINUM`, `DIAMOND`. |
| `AuthResult`, `ModerationResult`, `DeleteAccountResult` | Resultados tipados de operaciones. |

### Reglas puras (`domain/rules`)

| Regla | Responsabilidad |
|---|---|
| `ReputationRules` | Suma/resta de puntos por `PointEvent`, nivel por puntos y progreso. Los puntos nunca bajan de 0. |
| `BadgeEvaluator` | Calcula progreso y desbloqueo de las 7 insignias; una insignia no se pierde. |
| `VoteRules` | Decide si un voto se agrega o revierte y qué evento de puntos aplica. |
| `PublicationRules` | Validaciones de publicación (campos, precios, propiedad, visibilidad). |
| `FeedRules` | Feed público: solo `VERIFICADA`/`APPROVED`, cálculo de distancias y filtro "cerca de mí" (50 km por defecto; fallback a Armenia, Quindío). |
| `ModerationRules` | Transiciones permitidas, cola FIFO, métricas y validación del motivo de rechazo. |
| `CredentialRules` | Validación de correo/contraseña. |
| `KeywordCategoryRules` | Clasificador local de categorías por palabras clave (fallback de la IA). |

Las reglas no dependen de Android ni de Firestore, por lo que se prueban con JUnit puro.

### Servicios (`domain/service`)

- **`GamificationService`**: orquesta puntos, evaluación de insignias, persistencia y notificaciones. Es la pieza que usan `AwardPointsUseCase` y `EvaluateAchievementsUseCase`.
- **`AwardResult`**, **`UserProgress`**: resultados tipados del servicio.

### Casos de uso (`domain/usecase`)

| Área | Casos de uso destacados |
|---|---|
| `auth` | `LoginUserUseCase`, `LoginModeratorUseCase`, `LoginWithGoogleUseCase`, `LogoutUseCase`, `ObserveSessionUseCase`, `ObserveUserSessionUseCase`, `SendPasswordResetUseCase`, `ConfirmPasswordResetUseCase`. |
| `user` | `RegisterUserUseCase`, `GetCurrentUserUseCase`, `ObserveCurrentUserUseCase`, `UpdateUserProfileUseCase`, `UploadProfilePhotoUseCase`, `RemoveProfilePhotoUseCase`, `DeleteUserAccountUseCase`. |
| `publication` | `ObserveServicesUseCase`, `GetPublicationByIdUseCase`, `GetServiceDetailUseCase`, `CreatePublicationUseCase`, `UpdatePublicationUseCase`, `DeletePublicationUseCase`, `ToggleInterestUseCase`, `ObserveMyPublicationsUseCase`, `MarkPublicationCompletedUseCase`, `ClassifyServiceWithAiUseCase`. |
| `request` | `CreateServiceRequestUseCase`, `ObserveMyRequestsUseCase`, `UpdateRequestStatusUseCase`. |
| `trust` | `CalculateTrustIndexUseCase`. |
| `comment` | `AddCommentUseCase`, `ObserveCommentsUseCase`. |
| `notification` | `GenerateNotificationUseCase`, `ObserveNotificationsUseCase`, `MarkNotificationReadUseCase`, `MarkAllNotificationsReadUseCase`. |
| `moderation` | `ObservePendingPublicationsUseCase`, `ObserveModerationMetricsUseCase`, `ApprovePublicationUseCase`, `RejectPublicationUseCase`, `FinalizePublicationByModeratorUseCase`. |
| `reputation` | `AwardPointsUseCase`, `EvaluateAchievementsUseCase`, `GetUserStatsUseCase`, `GetLevelProgressUseCase`, `ObserveAchievementsUseCase`, `ObserveUserProgressUseCase`. |
| `achievement` | `UnlockAchievementUseCase`. |
| `location` | `GetAddressFromLocationUseCase`, `ReverseGeocodeUseCase`. |
| `category` | `ObserveCategoriesUseCase`. |

### Utilidades (`domain/util`)

- **`TimeProvider`**: reloj inyectable para tests deterministas.
- **`GeoDistanceCalculator`** y utilidades de `Location` para búsquedas por cercanía.

---

## Capa de datos

### Repositorios

Los contratos viven en `domain/repository`:

`AuthRepository`, `UserRepository`, `SessionRepository`, `PublicationRepository`, `ServiceRequestRepository`, `CommentRepository`, `NotificationRepository`, `CategoryRepository`, `GeocodingRepository`, `ImageRepository`, `AiClassifierRepository`.

Implementaciones:

- **`data/repository/firestore/*`**: producción. Consultas en tiempo real y escrituras transaccionales.
- **`data/repository/memory/*`**: usadas por las pruebas y el entorno in-memory (`TestEnvironment`).
- **`data/repository/remote/OpenRouterClassifierRepository`**: integración con IA.

### Firestore en tiempo real

Los repositorios exponen `Flow` mediante el patrón `callbackFlow` + `addSnapshotListener` y liberan el listener en `awaitClose`. Esto permite que la UI se actualice sola y que el listener viva solo mientras exista un colector.

Ejemplo (`data/remote/user/UserFirestoreExtensions.kt`):

```kotlin
fun DocumentReference.observeUser(): Flow<User?> = callbackFlow {
    val registration = addSnapshotListener { snapshot, error ->
        if (error != null) { close(); return@addSnapshotListener }
        trySend(snapshot?.toUser())
    }
    awaitClose { registration.remove() }
}
```

### IA (OpenRouter)

- `ClassifyServiceWithAiUseCase` envía título/descripción a un modelo gratuito configurado con `OPENROUTER_MODEL`.
- `OpenRouterClassifierRepository` usa Retrofit + Gson y responde con la categoría sugerida.
- `AiRequestLimiter` limita a **20 solicitudes cada 10 minutos** y `PublicationFlowViewModel` aplica *debounce* de **800 ms**.
- Ante error, timeout o límite, se usa `KeywordCategoryRules` como **fallback local**, por lo que la app funciona sin clave de IA.

### Mapas y ubicación

- `MapboxGeocodingRepository` para geocodificación inversa y búsqueda.
- `ReverseGeocodeUseCase` intenta primero `android.location.Geocoder` (API 33+ asíncrono, versiones anteriores en IO) y hace fallback a Mapbox.
- `GetAddressFromLocationUseCase` usa Mapbox directamente.
- Mapbox requiere el token público en `local.properties` y se inyecta como `resValue`.

### Imágenes

- `CloudinaryImageRepository` sube archivos con *unsigned upload preset* y devuelve la URL segura.
- Cada subida usa un nombre único (`UUID`) para evitar que Coil sirva una imagen vieja desde caché.
- Coil carga avatares e imágenes de publicaciones en Compose.

### Sesión

- `DataStoreSessionRepository` guarda `userId` y `role` en Preferences DataStore.
- `SessionViewModel` expone el estado de sesión y `AppNavigation` decide entre autenticación y app principal.
- Al cerrar sesión o eliminar la cuenta, el flujo cambia y la navegación vuelve sola al login.

---

## Modelo de datos en Firestore

Base de datos en **Cloud Firestore** del proyecto `servify-32e2c`. Las reglas están en `firestore.rules` y los índices en `firestore.indexes.json`.

### Colección `users`

Documento con ID = UID de Firebase Auth.

| Campo | Tipo | Notas |
|---|---|---|
| `id` | string | UID (se copia del documento). |
| `name`, `lastName` | string | Nombre y apellido. |
| `email`, `phone`, `city` | string | Datos de contacto. |
| `avatarUrl` | string | URL de Cloudinary. |
| `role` | string | `USER`, `CLIENT`, `PROVIDER`, `MODERATOR`. |
| `verified` | boolean | Nunca usar prefijo `is` (mapeo de Firestore). |
| `trustIndexScore` | number | Índice de confianza. |
| `reputationLevel` | string | `BRONZE`…`DIAMOND`. |
| `points` | number | Puntos Servify. |
| `completedJobsCount` | number | Servicios completados. |
| `badges` | array<string> | Insignias desbloqueadas. |
| `memberSinceMillis` | number | Fecha de registro. |

### Colección `publications`

| Campo | Tipo | Notas |
|---|---|---|
| `id`, `title`, `description` | string | Datos base. |
| `categoryId`, `category`, `subcategoryId`, `subcategory` | string | Clasificación. |
| `price`, `priceRange`, `priceUnit` | number/string | Precio y unidad (`por hora`, etc.). |
| `images`, `imageUrls` | array<string> | URLs de Cloudinary. |
| `providerId`, `providerName`, `providerAvatar`, `providerEmail` | string | Proveedor desnormalizado. |
| `authorId` | string | Autor real (fallback a `providerId`). |
| `status` | string | `PENDIENTE` por defecto. |
| `ratingAverage`, `reviewsCount` | number | Se recalculan al comentar. |
| `locationName`, `latitude`, `longitude`, `zone`, `city` | varios | Ubicación. |
| `isFavorite`, `interestedCount`, `interestedUserIds` | varios | Votos "Me interesa". |
| `rejectionReason` | string? | Motivo de rechazo. |
| `createdAt`, `createdAtMillis`, `verifiedAtMillis`, `finalizedAtMillis` | number | Marcas de tiempo. |

### Colección `service_requests`

| Campo | Tipo | Notas |
|---|---|---|
| `id` | string | `req_<publicationId>_<timestamp>` (determinista). |
| `publicationId`, `publicationTitle` | string | Servicio solicitado. |
| `clientId`, `clientName` | string | Cliente. |
| `providerId`, `providerName` | string | Proveedor. |
| `scheduledDateTime` | number | Fecha/hora del cupo. |
| `status` | string | `PENDING` por defecto. |
| `note` | string? | Nota del cliente. |
| `latitude`, `longitude`, `address` | varios | Ubicación del servicio. |
| `totalAmount` | number | Valor acordado. |
| `createdAt`, `updatedAt` | number | Marcas de tiempo. |

### Colección `comments`

`id`, `publicationId`, `authorId`, `authorName`, `authorAvatarUrl`, `rating` (1-5), `text`, `createdAt`.

### Colección `notifications`

`id`, `userId`, `title`, `message`, `type`, `isRead` (y alias `read`), `createdAt`, `targetId`.

### Colección `categories`

`id`, `label`, `iconName`, `colorArgb`, `backgroundArgb`. Si la colección está vacía o falla la lectura, `CategoryFirestoreRepositoryImpl` usa `SeedCategories` como catálogo local.

### Índices

`firestore.indexes.json` define índices compuestos para:

- `publications`: `authorId + createdAt`, `status + createdAt`.
- `service_requests`: `clientId + createdAt`, `providerId + createdAt`, `clientId + status`.
- `comments`: `publicationId + createdAt`.
- `notifications`: `userId + createdAt`.

Despliegue:

```bash
firebase deploy --only firestore:rules,firestore:indexes
```

---

## Inyección de dependencias

Hilt con KSP. Los módulos están en `di/`:

| Módulo | Provee |
|---|---|
| `FirebaseModule` | `FirebaseAuth`, `FirebaseFirestore`. |
| `RepositoryModule` | Enlaces de repositorios de dominio a implementaciones Firestore/remote. |
| `DomainModule` | `TimeProvider`, `GamificationService`, utilidades de dominio. |
| `LocationModule` | Cliente de geocodificación Mapbox y utilidades de ubicación. |
| `NetworkModule` | `OkHttpClient`, `Retrofit`, `Gson`, API de OpenRouter/Mapbox/Cloudinary. |

Puntos importantes:

- `ServifyApp` está anotada con `@HiltAndroidApp`; `MainActivity` con `@AndroidEntryPoint`.
- Los ViewModels usan `@HiltViewModel` + `@Inject constructor`.
- En pruebas instrumentadas se usa `HiltTestRunner` y `@HiltAndroidTest`.

---

## Navegación y pantallas

La navegación se define en `navigation/AppNavigation.kt` con `NavHost`. Hay dos grafos lógicos:

- **AuthNavigation**: splash, login, registro, verificación de correo, recuperación de contraseña.
- **MainNavigation**: dashboard, inicio, explorar, publicar, solicitudes, notificaciones, perfil, logros, estadísticas, moderación, detalle de servicio.

`SessionViewModel` decide el grafo según haya sesión y correo verificado. `BottomNavDestination` define las pestañas principales. Los `SavedStateHandle` transportan IDs entre pantallas (publicación, solicitud, notificación).

### Pantallas principales (`features`)

| Feature | Responsabilidad |
|---|---|
| `splash` | Espera la restauración de sesión y decide el destino. |
| `onboarding` | Introducción inicial para nuevos usuarios. |
| `login`, `register` | Autenticación con correo/contraseña y Google. |
| `forgotpassword`, `resetpassword` | Recuperación de contraseña (incluye deep link con código). |
| `dashboard` | Resumen del usuario, accesos rápidos y estado de reputación. |
| `home` | Feed principal de publicaciones verificadas y recomendaciones. |
| `explore` | Búsqueda, filtros, mapa y catálogo. |
| `location` | Selector de ubicación con mapa/GPS. |
| `publication/create`, `publication/mine`, `publication/published` | Asistente multi-paso (`PublicationFlowViewModel`), "Mis publicaciones" y confirmación de publicación. |
| `service`, `service/detail` | Detalle de servicio, comentarios, "Me interesa" y solicitud. |
| `request/create`, `request/mine`, `request/confirmed` | Creación de solicitud, "Mis solicitudes" con cambio de estado y confirmación. |
| `notifications` | Bandeja reactiva, marcar leídas y navegación por `targetId`. |
| `profile`, `profile/edit`, `profile/achievements` | Perfil propio/ajeno, edición/avatar y logros con progreso. |
| `statistics` | Puntos, nivel y métricas de reputación. |
| `trust` | Explicación del índice de confianza y recomendaciones. |
| `moderator/panel`, `moderator/review` | Panel, cola de pendientes, aprobación/rechazo y finalización. |

### Componentes de UI

- Tema Material 3 con tipografía y paleta propias en `ui/theme`.
- Componentes reutilizables: tarjetas de publicación, chips de categoría, barras de progreso, avatares, estados vacíos, diálogos de confirmación y `OfflineBanner`.
- Íconos propios en `ui/icons` cuando Material no cubre la necesidad.

---

## Flujos funcionales

### 1. Registro e inicio de sesión

1. El usuario se registra con `RegisterUserUseCase`; **siempre** se crea con rol `USER`.
2. Se envía correo de verificación; el login falla con `EMAIL_NOT_VERIFIED` hasta que el usuario lo confirme (los moderadores están exentos).
3. Al iniciar sesión, `LoginUserUseCase` (o `LoginWithGoogleUseCase`) valida credenciales y `SessionRepository` guarda `userId` + rol.
4. `SessionViewModel` emite el cambio y `AppNavigation` entra a `MainNavigation`.
5. El logout limpia `DataStore` y regresa a `AuthNavigation`.

### 2. Recuperación de contraseña

1. `SendPasswordResetUseCase` llama a Firebase Auth con `ActionCodeSettings`.
2. El correo enlaza a `https://<host>/__/auth/action` (deep link declarado en el manifest).
3. Firebase procesa el enlace y permite definir una nueva contraseña.

### 3. Crear una publicación con IA

1. El usuario elige categoría/subcategoría o escribe título/descripción.
2. Tras 800 ms de inactividad, `ClassifyServiceWithAiUseCase` consulta OpenRouter.
3. Si hay error, límite o falta de clave, `KeywordCategoryRules` sugiere la categoría.
4. El usuario fija ubicación en el mapa (Mapbox/GPS) y sube fotos a Cloudinary.
5. `CreatePublicationUseCase` guarda la publicación en estado `PENDIENTE`; el flujo de creación suma `PUBLICATION_CREATED` (+20 puntos).
6. Un moderador la revisa; al aprobar pasa a `VERIFICADA`, se notifica al autor y se otorgan `PUBLICATION_VERIFIED` (+30 puntos).

### 4. Solicitar un servicio

1. En el detalle, el usuario abre el formulario de solicitud (fecha, nota, dirección, total).
2. `CreateServiceRequestUseCase`:
   - valida que la publicación exista;
   - impide solicitar el propio servicio (`CannotRequestOwnServiceException`);
   - valida que exista un `providerId`;
   - genera el ID determinista y persiste la solicitud;
   - **intenta** notificar al proveedor (`REQUEST_RECEIVED`). Si la notificación falla, la solicitud ya creada se conserva: la notificación es *best-effort* y no se propaga la excepción (salvo `CancellationException`).
3. El proveedor ve la solicitud y puede aceptarla.
4. `UpdateRequestStatusUseCase` cambia el estado; si pasa a `ACCEPTED`, notifica al cliente (`REQUEST_ACCEPTED`).

### 5. Comentar y calificar

1. `AddCommentUseCase` guarda el comentario.
2. Recalcula `ratingAverage` y `reviewsCount` de la publicación.
3. Reevalúa insignias del autor del comentario (por ejemplo, "10 comentarios").
4. La publicación se actualiza y el feed se refresca por el flujo reactivo.

### 6. "Me interesa"

1. `ToggleInterestUseCase` agrega o quita el UID del usuario de `interestedUserIds`.
2. Ajusta `interestedCount` y aplica `VOTE_RECEIVED` (+2) o `VOTE_REVOKED` (-2) para evitar farmear puntos alternando el voto.
3. Reevalúa insignias como "Publicación destacada".

### 7. Notificaciones

1. Se generan en el dominio (`GenerateNotificationUseCase`) y se guardan en Firestore.
2. `ObserveNotificationsUseCase` expone la bandeja en tiempo real filtrada por `userId`.
3. `MarkNotificationReadUseCase` / `MarkAllNotificationsReadUseCase` actualizan `isRead`.
4. Al tocar una notificación, la UI navega usando `targetId` (publicación o solicitud).

---

## Gamificación y reputación

### Puntos (`PointEvent`)

| Evento | Puntos | Cuándo se aplica |
|---|---:|---|
| `PUBLICATION_CREATED` | +20 | Al crear una publicación. |
| `PUBLICATION_VERIFIED` | +30 | Al aprobar una publicación en moderación. |
| `VOTE_RECEIVED` | +2 | Al recibir un "Me interesa". |
| `VOTE_REVOKED` | -2 | Al retirar el "Me interesa". |
| `COMMENT_POSTED` | +5 | Definido en las reglas (comentar la publicación propia no suma). |

Los puntos nunca bajan de 0 (`ReputationRules.applyEvent`).

### Niveles (`Level`)

| Nivel | Puntos mínimos |
|---|---:|
| Principiante | 0 |
| Profesional | 200 |
| Experto | 600 |
| Maestro | 1.500 |

`ReputationRules.progress` calcula puntos dentro del nivel, siguiente nivel y fracción para la barra de progreso. En el nivel máximo la fracción es `1f`.

### Insignias (`Badge`)

| Insignia | Requisito |
|---|---|
| Primera publicación | Crear la primera publicación. |
| Primera verificada | Tener una publicación verificada. |
| 10 verificadas | Acumular 10 publicaciones verificadas. |
| 100 puntos | Llegar a 100 puntos Servify. |
| Publicación destacada | Tener la publicación verificada con más votos (mínimo 5; empate = ambos). |
| 10 comentarios | Escribir 10 comentarios. |
| Usuario confiable | 3 publicaciones verificadas y ninguna rechazada actualmente. |

`BadgeEvaluator` es una función pura: recibe usuario, publicaciones y comentarios. Una insignia ya obtenida no se pierde aunque luego cambie el contexto. `EvaluateAchievementsUseCase` registra las nuevas y, opcionalmente, dispara la notificación `ACHIEVEMENT_UNLOCKED`.

---

## Moderación

- Solo disponible para `MODERATOR`; el rol no se puede obtener desde el registro.
- **Cola**: `ModerationRules.pendingQueue` ordena por fecha de creación (FIFO).
- **Aprobar**: solo si `ModerationRules.canReview(status)`; pasa a `VERIFICADA`, registra `verifiedAtMillis`, limpia el motivo de rechazo, notifica y suma puntos.
- **Rechazar**: exige motivo no vacío (`isValidRejectionReason`); pasa a `RECHAZADA`, guarda el motivo y notifica con el texto.
- **Finalizar**: el moderador puede marcar como `FINALIZADA` si la transición está permitida.
- **Métricas**: pendientes, verificadas hoy, rechazadas y finalizadas se calculan con `TimeProvider` (testeable).

La restricción vive en el dominio: ocultar un botón no se considera seguridad.

---

## Notificaciones

| Tipo | Destinatario | Origen |
|---|---|---|
| `REQUEST_RECEIVED` | Proveedor | Creación de solicitud. |
| `REQUEST_ACCEPTED` | Cliente | Cambio de estado a `ACCEPTED`. |
| `MODERATION_UPDATE` | Autor | Aprobación o rechazo de publicación. |
| `ACHIEVEMENT_UNLOCKED` | Usuario | Nueva insignia. |
| `REVIEW_PUBLISHED` / `APPOINTMENT_REMINDER` | Definidos | Reservados para futuras integraciones. |

- Las notificaciones internas viven en Firestore y se observan en tiempo real.
- No se usan notificaciones push de FCM: la app genera los documentos desde el cliente.
- La creación de notificaciones es *best-effort* cuando ocurre después de una operación principal (por ejemplo, crear una solicitud), para no romper el flujo del usuario.

---

## Modo offline

Documentado en `docs/OFFLINE_PERSISTENCE.md`.

- Firestore tiene **persistencia offline habilitada por defecto**: las lecturas se sirven desde caché y las escrituras se encolan.
- `ConnectivityMonitor` observa el estado de red y `AppStateViewModel` lo expone.
- `OfflineBanner` se muestra globalmente cuando no hay conexión.
- Al volver la red, Firestore sincroniza automáticamente.
- Limitación: las operaciones que dependen de red (Cloudinary, OpenRouter, Mapbox) fallan o usan fallback local mientras no haya conexión.

---

## Seguridad

### Reglas de Firestore (`firestore.rules`)

- `users`: lectura para cualquier usuario autenticado; cada quien edita su perfil. `role`, `email` y `memberSinceMillis` son inmutables desde el cliente; los campos de gamificación (`points`, `reputationLevel`, `badges`, `trustIndexScore`, `completedJobsCount`) pueden actualizarse desde cuentas autenticadas (necesario para votos y reseñas).
- `publications`: lectura pública. El autor crea en `PENDIENTE`/`DRAFT`, edita o finaliza; el moderador aprueba/rechaza/finaliza; cualquier usuario autenticado puede actualizar solo `ratingAverage`/`reviewsCount` o los campos de "Me interesa".
- Los cambios de estado de moderación solo los puede hacer un `MODERATOR` (y el autor puede reenviar a `PENDIENTE` o finalizar una verificada).
- `service_requests`: lectura para usuarios autenticados; creación solo por el cliente y con estado `PENDING`; el cliente cancela y el proveedor acepta/rechaza/completa/cancela. Existe la subcolección `status_history` (solo lectura para participantes/moderador y creación por participantes).
- `notifications`: cada usuario lee y marca solo las suyas; cualquier usuario autenticado puede crear una notificación para otro (eventos de solicitudes/moderación).
- `comments`: lectura pública; el autor crea/edita/elimina las suyas y el moderador puede retirar contenido.
- `categories`: lectura pública; escritura solo para moderación.
- Helpers: `isModerator()`, `onlyChanged()`, `validPublicationStatus()`.

### Secretos y credenciales

Archivos **no versionados** (ver `.gitignore`):

- `app/google-services.json`: configuración de Firebase.
- `app/release.jks`: keystore de release.
- `local.properties`: tokens y rutas locales.

`local.properties.example` documenta las variables:

```properties
sdk.dir=/path/to/Android/Sdk

OPENROUTER_API_KEY=sk-or-...
OPENROUTER_MODEL=...:free
MAPBOX_ACCESS_TOKEN=pk...
FIREBASE_AUTH_HOST=servify-32e2c.firebaseapp.com

CLOUDINARY_CLOUD_NAME=...
CLOUDINARY_UPLOAD_PRESET=...

# Firma de release (opcional)
RELEASE_STORE_FILE=app/release.jks
RELEASE_STORE_PASSWORD=...
RELEASE_KEY_ALIAS=...
RELEASE_KEY_PASSWORD=...
```

`app/build.gradle.kts` convierte los tokens en `resValue` para que Mapbox y OpenRouter los lean en tiempo de ejecución. Esto implica que **viajan dentro del APK**; para producción real se recomienda mover la IA y las subidas a un backend propio.

### Otras medidas

- Verificación de correo antes de operar.
- `Internal`/`Private` en pantallas sensibles (moderación).
- R8 con reglas `@Keep` para DTOs reflexivos.
- No se registran tokens ni contraseñas en logs.

---

## Configuración del entorno

### Requisitos

- Android Studio reciente (AGP 9.4.1).
- JDK 17+ (el que trae Android Studio).
- SDK de Android 37.
- Cuenta de Firebase con Auth (correo/contraseña y Google) y Firestore.
- Token de Mapbox.
- Cuenta de Cloudinary con preset *unsigned*.
- (Opcional) clave de OpenRouter.

### Pasos

1. Clona el repositorio y abre la carpeta en Android Studio.
2. Copia `local.properties.example` a `local.properties` y completa `sdk.dir`, tokens y firma.
3. Coloca `google-services.json` en `app/` (no se versiona).
4. Verifica que `applicationId` del JSON coincida con `co.edu.uniquindio.servify`.
5. Sincroniza Gradle.
6. Ejecuta la app en un dispositivo/emulador con API 28+.

> Si cambias el `applicationId`, debes registrar la nueva app en Firebase y volver a descargar `google-services.json`. El proyecto actual usa `co.edu.uniquindio.servify`.

### Firebase

1. Habilita Authentication (Email/Password y Google).
2. Crea Cloud Firestore en modo producción.
3. Publica reglas e índices:

```bash
firebase deploy --only firestore:rules,firestore:indexes
```

4. Para el deep link de recuperación, agrega el dominio de Firebase a los dominios autorizados de Authentication y mantén el `intent-filter` del manifest.

---

## Compilar, ejecutar y probar

### Compilar

```bash
./gradlew assembleDebug
./gradlew assembleRelease
```

Salidas:

- `app/build/outputs/apk/debug/app-debug.apk`
- `app/build/outputs/apk/release/app-release.apk`

### Instalar

```bash
./gradlew installDebug
adb install -r app/build/outputs/apk/release/app-release.apk
```

### Pruebas

```bash
./gradlew testDebugUnitTest
./gradlew connectedDebugAndroidTest   # requiere dispositivo/emulador
```

Cobertura de pruebas (JVM, `app/src/test`):

- Reglas puras: reputación, insignias, votos, moderación, credenciales, palabras clave.
- Casos de uso: solicitudes (incluye los tests del fix de notificaciones), publicaciones, comentarios, moderación, notificaciones, gamificación.
- Repositorios in-memory, mapeos DTO↔dominio y utilidades.
- ViewModels principales con `MainDispatcherRule` y `TestEnvironment`.
- Paridad de cadenas entre `values` y `values-es`.
- Pruebas instrumentadas con Hilt (`HiltTestRunner`) para flujos de UI.

### Distribución

El APK de release se distribuye con Firebase App Distribution:

```bash
firebase appdistribution:distribute app/build/outputs/apk/release/app-release.apk \
  --app 1:967035314422:android:d747738d04b956abcc8cd7 \
  --groups testers
```

---

## Optimización del APK

La configuración actual de release (`app/build.gradle.kts`):

- **R8 activo**: `optimization { enable = true }` (código y recursos reducidos).
- **ABI única en release**: `arm64-v8a` (la mayoría de dispositivos modernos). Debug conserva `arm64-v8a` + `x86_64` para emuladores.
- **Reglas `@Keep`** en DTOs que Firestore/Gson construyen por reflexión (`CategoryDto`, `CommentDto`, `NotificationDto`).
- Resultado: el APK pasó de ~112 MB a **~30 MB**.

Para generar un bundle de Play Store (si se requiere):

```bash
./gradlew bundleRelease
```

Si necesitas soportar dispositivos `armeabi-v7a` o `x86`, agrega las ABIs en `abiFilters` (aumenta el tamaño).

---

## Internacionalización y diseño

- `values/strings.xml` (inglés, por defecto) y `values-es/strings.xml` (español) mantienen las mismas claves (~255 cadenas cada uno). Hay una prueba que valida la paridad.
- El tema vive en `ui/theme` (Material 3, colores, tipografía y formas).
- Diseño mobile-first con tarjetas, chips, avatares, barras de progreso y estados vacíos.
- Soporte de modo claro/oscuro según el sistema.
- Mapbox y Coil se usan en pantallas de ubicación e imágenes respectivamente.

---

## Limitaciones conocidas

- **Notificaciones generadas en el cliente**: no hay Cloud Functions ni FCM; un cliente autenticado podría escribir notificaciones si las reglas lo permiten. Para producción se recomienda moverlas a un backend.
- **Tokens en el APK**: Mapbox y OpenRouter se inyectan con `resValue`. La clave de OpenRouter es pública dentro del binario; úsala solo en desarrollo o con límites estrictos.
- **Cloudinary unsigned**: el preset sin firma permite subidas; para producción usa subidas firmadas.
- **Puntos por comentario**: `COMMENT_POSTED` está definido, pero `AddCommentUseCase` actualmente solo reevalúa insignias. Si se desea, se puede llamar a `AwardPointsUseCase`.
- **Estados legados**: conviven `VERIFICADA` y `APPROVED` por compatibilidad. Los datos nuevos usan `VERIFICADA`.
- **Seed data**: `data/seed` y los repositorios in-memory son para pruebas; la app real usa Firestore.
- **Idioma de contenido**: los textos generados (notificaciones, títulos por defecto) están en español, aunque la UI esté en inglés o español.

---

## Solución de problemas

| Problema | Causa probable | Solución |
|---|---|---|
| `google-services.json` no encontrado | Archivo ausente | Colócalo en `app/`. |
| Mapbox no carga el mapa | Token vacío o inválido | Revisa `MAPBOX_ACCESS_TOKEN` en `local.properties` y resincroniza. |
| La IA no sugiere categoría | Sin clave/cuota/límite | La app usa `KeywordCategoryRules` como fallback. |
| `Field 'verified' has conflicting types` | Booleanos con prefijo `is` en Firestore | Usa `verified`, `isRead` con `@PropertyName` según corresponda. |
| Error de índice en Firestore | Consulta compuesta sin índice | Despliega `firestore.indexes.json`. |
| Permiso denegado al cerrar sesión | Listener activo tras logout | Los flujos cierran con `awaitClose` y los errores de listener terminan el flujo. |
| APK release no instala en x86 | Release solo `arm64-v8a` | Usa un dispositivo ARM o agrega la ABI. |
| Compilación falla por Hilt/KSP | Versiones incompatibles | Usa las versiones de `libs.versions.toml` y JDK 17+. |

---

## Convenciones de contribución

- Ramas descriptivas: `feat/...`, `fix/...`, `docs/...`.
- Commits en inglés o español, concisos y en imperativo (el historial mezcla ambos).
- Antes de abrir un PR:
  - `./gradlew testDebugUnitTest`
  - `./gradlew assembleDebug`
- No subir `local.properties`, `google-services.json` ni `release.jks`.
- Mantener la lógica de negocio en `domain`, no en ViewModels ni composables.
- Agregar pruebas para reglas y casos de uso nuevos.
- Respetar la paridad de cadenas `values` / `values-es`.

---

## Créditos

Proyecto académico de la **Universidad del Quindío** — programa de Ingeniería de Sistemas. Construido con Kotlin, Jetpack Compose, Firebase, Mapbox, Cloudinary y OpenRouter.

Para dudas o reportes, abre un issue en el repositorio.
