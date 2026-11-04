# Alcance: feature/moderador-revision

## Contexto

Implementar el flujo de revisión de publicaciones por parte del moderador (mockup 23).
La fase 2 del proyecto usa repositorios en memoria y lógica de dominio existente para
moderación, notificaciones, reputación y puntos. No se debe agregar backend ni Firebase.

## Objetivo

Permitir que el moderador revise una publicación, la verifique, la rechace con un motivo
obligatorio o la marque como finalizada. Cada decisión debe reflejarse en el estado de la
publicación y generar la notificación y los puntos correspondientes con los casos de uso y
repositorios ya existentes.

## Alcance funcional

- Crear el feature `features/moderator/review` con:
  - `ModeratorReviewScreen`: detalle de la publicación y acciones de moderación.
  - `ModeratorReviewViewModel`: carga la publicación, ejecuta decisiones y expone estado.
  - `ModeratorReviewUiState`: carga, éxito, error, publicación no encontrada y estado de la
    decisión.
- Crear `RejectReasonSheet` apoyado en `ServifyBottomSheet` existente:
  - Campo de motivo obligatorio.
  - Validación de longitud mínima y mensaje de error en español.
  - Confirmación y cancelación.
- Acciones disponibles:
  - Verificar publicación.
  - Rechazar publicación con motivo obligatorio.
  - Marcar publicación como finalizada.
- Al decidir:
  - Actualizar el estado de la publicación.
  - Generar la notificación al autor con los casos de uso existentes.
  - Otorgar o ajustar puntos/reputación según la lógica vigente.
- Nueva ruta `ModeratorReview/{serviceId}` en `navigation/Screen.kt` y `AppNavigation.kt`.
- Conectar el botón "Revisar" del panel de moderador con la nueva ruta, enviando el id de
  la publicación/servicio.

## Estructura propuesta

```text
features/moderator/review/
  ModeratorReviewScreen.kt
  ModeratorReviewViewModel.kt
  ModeratorReviewUiState.kt
  RejectReasonSheet.kt
  components/
    ReviewDecisionActions.kt
    ReviewPublicationSummary.kt
```

## Integración de arquitectura

- Reutilizar `ModeratePublicationUseCase`, `GenerateNotificationUseCase`,
  `AwardPointsUseCase` y los repositorios en memoria existentes.
- Si falta una consulta puntual, crear el caso de uso en `domain/usecase` sin duplicar
  lógica de dominio.
- Usar componentes de `ui/components` para diálogos, hojas, estados y feedback.
- DI con Hilt siguiendo los módulos actuales.
- Mantener textos de interfaz en español.

## Criterios de aceptación

- El botón "Revisar" del panel abre la ruta con el `serviceId` correcto.
- La pantalla muestra la información de la publicación y su estado actual.
- Verificar, rechazar y finalizar actualizan la publicación y la UI.
- El rechazo no se permite sin motivo válido.
- Cada decisión genera la notificación y los puntos esperados.
- Se manejan estados de carga, error y publicación inexistente.
- No se agregan dependencias de backend ni se altera la arquitectura existente.
- El código compila y los tests existentes siguen pasando.

## Fuera de alcance

- Nuevos roles o permisos distintos a los existentes.
- Persistencia remota o base de datos.
- Rediseño del panel de moderación más allá de conectar la acción de revisión.
