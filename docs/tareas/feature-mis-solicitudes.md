# Alcance: feature/mis-solicitudes

## Contexto

La ruta `MyRequests` muestra actualmente un placeholder. El proyecto ya cuenta con
`ServiceRequest`, su repositorio en memoria y casos de uso para crear y actualizar
solicitudes. También existe la ruta `Settings`, que hoy apunta a un placeholder y debe
llevar a la edición de perfil.

## Objetivo

Sustituir el placeholder de `Screen.MyRequests` por una pantalla que liste las solicitudes
de servicio del usuario autenticado y su estado, y corregir la navegación de `Settings`
para abrir `EditProfile`.

## Alcance funcional

- Crear el feature de solicitudes del usuario, por ejemplo
  `features/request/mine`, con:
  - `MyRequestsScreen`: listado de solicitudes con estado, servicio y datos relevantes.
  - `MyRequestsViewModel`: observa las solicitudes del usuario y expone el estado.
  - `MyRequestsUiState`: carga, éxito, vacío y error.
- Listar únicamente las `ServiceRequest` del usuario autenticado.
- Mostrar el estado de cada solicitud de forma clara, usando los componentes y colores
  existentes.
- Permitir acceder al detalle o a la publicación asociada cuando el modelo lo permita.
- Estados de vacío con mensaje y acción sugerida.
- Cambiar la ruta `Settings` para que abra `EditProfile` en lugar del placeholder.
- Integrar las rutas en `navigation/Screen.kt`, `AppNavigation.kt` y `MainScreen` según
  corresponda.

## Estructura propuesta

```text
features/request/mine/
  MyRequestsScreen.kt
  MyRequestsViewModel.kt
  MyRequestsUiState.kt
  components/
    ServiceRequestCard.kt
    RequestStatusChip.kt
```

## Integración de arquitectura

- Reutilizar `ServiceRequestRepository`, `CreateServiceRequestUseCase` y
  `UpdateRequestStatusUseCase` existentes.
- Si no existe una consulta para observar las solicitudes del usuario, agregar el caso de
  uso correspondiente en `domain/usecase/request` sin duplicar lógica.
- Usar componentes de `ui/components` para tarjetas, estados y feedback.
- DI con Hilt siguiendo los módulos actuales.
- Textos de interfaz en español.

## Criterios de aceptación

- `Screen.MyRequests` muestra la lista real de solicitudes del usuario.
- Cada solicitud muestra su estado de forma legible y consistente.
- Se manejan estados de carga, vacío y error.
- La ruta `Settings` abre `EditProfile` y ya no muestra un placeholder.
- No se agregan dependencias de backend ni se altera la arquitectura existente.
- El código compila y los tests existentes siguen pasando.

## Fuera de alcance

- Cancelar o modificar solicitudes desde esta pantalla, salvo que ya exista el caso de uso
  y se acuerde como parte del feature.
- Persistencia remota o base de datos.
- Rediseño del flujo de creación de solicitudes.
