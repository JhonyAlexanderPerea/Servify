# Alcance: test/unitarias-viewmodels

## Contexto

El proyecto ya cuenta con `testutil/MainDispatcherRule` y `testutil/TestEnvironment`, además
de repositorios en memoria y casos de uso probados. Falta cubrir con pruebas unitarias los
ViewModels de los flujos principales.

## Objetivo

Agregar pruebas unitarias deterministas para los ViewModels del proyecto, usando
`MainDispatcherRule`, repositorios en memoria y dobles de prueba existentes, sin depender de
Android, backend ni servicios externos.

## Alcance funcional

Cubrir como mínimo estos ViewModels:

- `HomeViewModel`
- `ExploreViewModel`
- `ServiceDetailViewModel`
- `NotificationsViewModel`
- `StatisticsViewModel`
- `CreatePublicationViewModel` / `PublicationFlowViewModel`
- `ModeratorPanelViewModel`

Para cada uno se deben validar, según aplique:

- Estado inicial y valores por defecto de su `UiState`.
- Carga exitosa de datos desde repositorios en memoria.
- Cambios de estado ante acciones del usuario.
- Manejo de errores y estados vacíos o de carga.
- Reactividad ante flujos (`Flow`) de repositorios y casos de uso.
- Cancelación o finalización correcta de coroutines.

## Estructura propuesta

```text
app/src/test/java/co/edu/uniquindio/servify/
  features/home/HomeViewModelTest.kt
  features/explore/ExploreViewModelTest.kt
  features/service/detail/ServiceDetailViewModelTest.kt
  features/notifications/NotificationsViewModelTest.kt
  features/statistics/StatisticsViewModelTest.kt
  features/publication/create/PublicationFlowViewModelTest.kt
  features/moderator/panel/ModeratorPanelViewModelTest.kt
```

## Integración de arquitectura

- Reutilizar `MainDispatcherRule` y `TestEnvironment` existentes.
- Usar repositorios en memoria y casos de uso reales cuando sea posible.
- Evitar mocks innecesarios; usar fakes solo cuando el caso lo requiera.
- Mantener nombres de pruebas descriptivos en español o inglés consistente con el proyecto.
- No modificar la lógica de producción salvo que se detecte un defecto, en cuyo caso se
  documentará y corregirá en un commit separado.

## Criterios de aceptación

- Las pruebas corren en JVM sin emulador ni dispositivo.
- Todos los ViewModels listados tienen cobertura de sus estados y acciones principales.
- Las pruebas son deterministas y no dependen de tiempos reales.
- Se usan `MainDispatcherRule` y los utilitarios existentes.
- La suite completa de tests pasa.
- No se agregan dependencias de backend ni se altera la arquitectura existente.

## Fuera de alcance

- Pruebas de integración de flujos completos (rama `test/integracion`).
- Pruebas de interfaz Compose.
- Pruebas end-to-end con backend real.
