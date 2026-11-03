# Alcance: feature/logros

## Contexto

Implementar la pantalla de logros e insignias del usuario (mockup 19), sustituyendo el
placeholder actual de la ruta `Achievements`. La fase 2 del proyecto funciona con
repositorios en memoria, sin backend ni Firebase.

## Objetivo

Mostrar el estado de las insignias del usuario, diferenciando las desbloqueadas de las
bloqueadas, junto con el progreso global y los requisitos de cada insignia. La pantalla
debe integrarse con la arquitectura MVVM existente y consumir la lógica de gamificación ya
disponible.

## Alcance funcional

- Crear el feature `features/profile/achievements` con:
  - `AchievementsScreen`: grid de insignias y resumen de progreso.
  - `AchievementsViewModel`: expone el estado y reacciona a los flujos del dominio.
  - `AchievementsUiState`: estado de carga, éxito, error y vacío.
- Reutilizar `BadgeEvaluator` y `GamificationService` existentes, además de los casos de
  uso y repositorios de reputación/logros ya presentes en el proyecto.
- Grid de insignias:
  - Desbloqueadas: color, icono y datos completos.
  - Bloqueadas: escala de grises, requisito visible y progreso si aplica.
- Indicador de progreso global de logros del usuario.
- Detalle o texto de requisito por insignia.
- Estados de carga, error y vacío con componentes existentes de `ui/components`.
- Textos de interfaz en español.
- Reemplazar el `ComingSoonScreen` de la ruta `Achievements` en `MainScreen`.

## Estructura propuesta

```text
features/profile/achievements/
  AchievementsScreen.kt
  AchievementsViewModel.kt
  AchievementsUiState.kt
  components/
    AchievementBadgeGrid.kt
    AchievementProgressHeader.kt
```

## Integración de arquitectura

- Rutas en `navigation/Screen.kt` y `navigation/AppNavigation.kt`.
- Navegación desde el punto de entrada actual de logros en `MainScreen`.
- Casos de uso en `domain/usecase` solo si falta alguna consulta necesaria.
- Componentes reutilizables en `ui/components` cuando aplique.
- Inyección de dependencias con Hilt, siguiendo los módulos actuales.

## Criterios de aceptación

- La ruta `Achievements` abre la nueva pantalla y no el placeholder.
- El grid refleja correctamente insignias desbloqueadas y bloqueadas.
- El progreso global y los requisitos se actualizan de forma reactiva.
- Se manejan estados de carga, error y vacío.
- No se agregan dependencias de backend ni se modifica la arquitectura existente.
- El código compila y los tests existentes siguen pasando.

## Fuera de alcance

- Persistencia en base de datos o servicios externos.
- Nuevos sistemas de gamificación distintos a los ya existentes.
- Cambios de diseño ajenos al mockup 19.
