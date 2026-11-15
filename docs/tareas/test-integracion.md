# Alcance: test/integracion

## Contexto

El proyecto usa repositorios en memoria y casos de uso de dominio que permiten probar flujos
completos sin backend. Se requieren pruebas de integración JVM que validen la interacción
entre casos de uso, repositorios y lógica de gamificación/moderación.

## Objetivo

Agregar pruebas de integración JVM que cubran los cinco flujos principales del proyecto
usando repositorios en memoria, y opcionalmente algunas pruebas de navegación Compose en
`androidTest`.

## Alcance funcional

Cubrir los siguientes flujos:

1. Publicación aprobada:
   - Crear publicación.
   - Moderar/verificar.
   - Generar notificación y otorgar puntos.
   - Evaluar y desbloquear insignia.
2. Publicación rechazada:
   - Rechazar con motivo.
   - Generar notificación al autor.
   - Editar la publicación.
   - Reenviar y verificar que vuelve a `PENDING`.
3. Solicitud de servicio:
   - Crear solicitud.
   - Actualizar estado.
   - Verificar reflejo en repositorio y notificaciones si aplica.
4. Reputación y niveles:
   - Acumular puntos.
   - Evaluar progreso de nivel.
   - Verificar gamificación e insignias.
5. Flujo de moderador:
   - Login de moderador.
   - Acceso al panel.
   - Decisión sobre una publicación y efectos asociados.

Adicionalmente, de forma opcional:

- 2 o 3 pruebas Compose de navegación entre pantallas clave en `androidTest`.
- Uso de `hilt-android-testing` solo si ya está contemplado o se justifica; no debe
  introducir dependencias de backend.

## Estructura propuesta

```text
app/src/test/java/co/edu/uniquindio/servify/integration/
  PublicationApprovalFlowTest.kt
  PublicationRejectionAndResubmitFlowTest.kt
  ServiceRequestFlowTest.kt
  ReputationAndLevelsFlowTest.kt
  ModeratorFlowTest.kt
  support/
    IntegrationTestEnvironment.kt
```

```text
app/src/androidTest/java/co/edu/uniquindio/servify/navigation/
  NavigationSmokeTest.kt   (opcional)
```

## Integración de arquitectura

- Reutilizar repositorios en memoria, casos de uso reales y `TestEnvironment` cuando aplique.
- Mantener cada prueba aislada con un entorno nuevo por test.
- No depender de orden entre pruebas ni de estado global.
- No modificar lógica de producción salvo defectos detectados, documentados y corregidos en
  commits separados.
- Textos y nombres de pruebas consistentes con el proyecto.

## Criterios de aceptación

- Los cinco flujos de integración están cubiertos y pasan en JVM.
- Las pruebas son deterministas, aisladas y no requieren backend ni red.
- Se validan efectos secundarios: notificaciones, puntos, insignias y estados.
- Las pruebas opcionales de Compose, si se implementan, pasan en `androidTest`.
- No se agregan dependencias de backend ni se altera la arquitectura existente.
- La suite completa de tests pasa.

## Fuera de alcance

- Pruebas unitarias aisladas de ViewModels (rama `test/unitarias-viewmodels`).
- Pruebas end-to-end con servicios remotos.
- Cambios de funcionalidad no relacionados con los flujos probados.
