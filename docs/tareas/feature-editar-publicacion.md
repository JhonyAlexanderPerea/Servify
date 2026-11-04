# Alcance: feature/editar-publicacion

## Contexto

En `MyPublications` existe un botón "Editar" que aún no abre el flujo de edición (TODO
pendiente). El proyecto ya cuenta con `CreatePublicationScreen`, `PublicationFlowViewModel`,
`GetPublicationByIdUseCase` y `UpdatePublicationUseCase`. Además, una publicación rechazada
debe poder corregirse y reenviarse para volver a estado `PENDING`.

## Objetivo

Conectar el botón "Editar" de `MyPublications` con `CreatePublication` en modo edición,
enviando el id de la publicación, precargando sus datos y permitiendo reenviar una
publicación rechazada para que vuelva a `PENDING`.

## Alcance funcional

- Resolver el TODO del botón "Editar" en `MyPublicationsScreen`.
- Navegar a `CreatePublication` en modo edición con el `publicationId`.
- Extender la ruta de creación para aceptar un id opcional de publicación.
- En modo edición:
  - Precargar los datos existentes de la publicación.
  - Permitir modificar los campos permitidos por el flujo actual.
  - Mostrar el estado actual y, si aplica, el motivo de rechazo.
  - Guardar los cambios mediante `UpdatePublicationUseCase`.
- Reenvío de publicación rechazada:
  - Al guardar o reenviar, la publicación vuelve a `PENDING`.
  - Limpiar el motivo de rechazo previo si el modelo lo contempla.
  - Refrescar `MyPublications` al volver.
- En modo creación se conserva el comportamiento actual.
- Manejo de estados de carga, error y publicación no encontrada.
- Textos de interfaz en español.

## Estructura propuesta

```text
features/publication/create/   (reutilizar y extender)
  CreatePublicationScreen.kt
  PublicationFlowViewModel.kt
  PublicationFlowUiState.kt

features/publication/mine/
  MyPublicationsScreen.kt      (conectar acción Editar)
```

## Integración de arquitectura

- Reutilizar `GetPublicationByIdUseCase`, `UpdatePublicationUseCase` y el flujo existente
  de creación de publicaciones.
- Ajustar `navigation/Screen.kt` y `AppNavigation.kt` para soportar el id opcional en la
  ruta de publicación.
- No duplicar validaciones: usar `PublicationRules` y la lógica de dominio existente.
- Mantener componentes de `ui/components` para estados y feedback.
- DI con Hilt siguiendo los módulos actuales.

## Criterios de aceptación

- El botón "Editar" abre el flujo con la publicación correcta precargada.
- Los cambios se guardan y se reflejan en `MyPublications`.
- Una publicación rechazada puede editarse y reenviarse, volviendo a `PENDING`.
- El modo creación sigue funcionando sin regresiones.
- Se manejan estados de carga, error y publicación inexistente.
- No se agregan dependencias de backend ni se altera la arquitectura existente.
- El código compila y los tests existentes siguen pasando.

## Fuera de alcance

- Cambios en las reglas de moderación distintas a las existentes.
- Persistencia remota o base de datos.
- Rediseño completo del wizard de creación.
