# Contribuir a Essence

Gracias por tu interés en mejorar **Essence**. Este documento describe cómo reportar bugs, sugerir mejoras y enviar pull requests de forma efectiva.

## Tabla de contenidos

- [Código de conducta](#código-de-conducta)
- [Reportar un bug](#reportar-un-bug)
- [Sugerir una mejora](#sugerir-una-mejora)
- [Pull requests](#pull-requests)
- [Estilo de código](#estilo-de-código)
- [Commits](#commits)

## Código de conducta

Sé respetuoso. Las críticas técnicas se hacen sobre el código, no sobre las personas. No se toleran comentarios discriminatorios ni ataques personales.

## Reportar un bug

Antes de abrir un issue:

1. **Busca** en los [issues existentes](https://github.com/Walter-Quispesayhua-Q/EssenceApp/issues) por si ya está reportado.
2. Reproduce el error en la **última versión** disponible en Releases.

Cuando abras un nuevo issue, incluye:

- **Título descriptivo**: `[Bug] Descripción breve del problema`.
- **Pasos para reproducir** numerados:
  ```
  1. Abrir la app
  2. Ir a Search y buscar "ejemplo"
  3. Click en el primer resultado
  4. Observar que el reproductor no inicia
  ```
- **Comportamiento esperado** vs **comportamiento actual**.
- **Versión de la app** (visible en ajustes o en el APK descargado).
- **Modelo y versión de Android** del dispositivo.
- **Logs** si están disponibles (logcat con filtro `EssenceApp` o `Essence`).
- **Screenshots o video** si aplica.

## Sugerir una mejora

Las propuestas de features siguen el mismo flujo que los bugs, pero con etiqueta `enhancement` y deben describir:

- **Problema** que resuelve la mejora.
- **Solución propuesta** con detalle.
- **Alternativas** consideradas.
- **Contexto adicional**: capturas, ejemplos de otras apps, links a documentación.

## Pull requests

### Antes de empezar

Si tu cambio es **mayor** (refactor grande, nueva feature, cambio de arquitectura), abre primero un issue para discutirlo. Esto evita rechazos tras invertir mucho tiempo.

Cambios **menores** (typos, documentación, fixes pequeños) pueden ir directos como PR.

### Flujo

1. **Fork** del repositorio.
2. Crea una rama desde `main`:
   ```bash
   git checkout -b feature/nombre-descriptivo
   git checkout -b fix/descripcion-del-bug
   ```
3. Implementa el cambio respetando el [estilo de código](#estilo-de-código).
4. Compila y prueba en debug y release antes de enviar:
   ```powershell
   .\gradlew.bat assembleDebug
   .\gradlew.bat assembleRelease
   ```
5. Push a tu fork y abre un PR contra `main` con:
   - **Título** claro siguiendo formato de [Conventional Commits](https://www.conventionalcommits.org).
   - **Descripción** que explique qué cambió, por qué, y cómo se probó.
   - **Screenshots** o GIFs si afecta UI.
   - **Referencia al issue** que resuelve: `Closes #123`.

### Criterios de aceptación

- El código compila sin warnings nuevos.
- No se introducen comentarios innecesarios (ver [estilo de código](#estilo-de-código)).
- Las reglas de ProGuard se actualizan si es necesario.
- La estructura sigue Clean Architecture: `data/`, `domain/`, `ui/`.
- No se hardcodean URLs ni secretos.

## Estilo de código

- **Kotlin** siguiendo las convenciones oficiales de [kotlinlang.org](https://kotlinlang.org/docs/coding-conventions.html).
- **Composables** con nombres en `PascalCase`, `Modifier` siempre como primer parámetro opcional con valor por defecto `Modifier`.
- **Sin comentarios** salvo cuando sean estrictamente necesarios para explicar una decisión no obvia. El código debe ser autoexplicativo a través de nombres claros.
- **Funciones pequeñas** y enfocadas en una sola responsabilidad.
- **Estado expuesto** desde ViewModels vía `StateFlow`.
- **Coroutines** lanzadas siempre con scope explícito (`viewModelScope`, `lifecycleScope`).
- **Inmutabilidad** preferida: `val`, `data class`, listas inmutables.

## Commits

Usa [Conventional Commits](https://www.conventionalcommits.org/es/v1.0.0/):

```
feat: agregar reproducción en background
fix: corregir crash al rotar pantalla en SongDetail
docs: actualizar README con nueva URL de Azure
refactor: extraer SongCard a componente reutilizable
perf: reducir tiempo de splash en 25%
chore: actualizar versiones de dependencias
```

Los commits deben ser **atómicos**: un commit hace una sola cosa. Evita commits con mensajes genéricos como `update`, `fix stuff` o `wip`.

## Áreas con mayor necesidad de contribución

### Prioridad alta: modo offline y descargas

Estas dos features están planificadas para la versión `1.2.0` y son la **mayor oportunidad de contribución técnica** del proyecto.

- **Modelado de datos offline (Room)**: diseñar e implementar las entidades de Room y DAOs necesarios para persistir canciones, álbumes y artistas localmente. Esto incluye definir el esquema de base de datos, las relaciones entre entidades, y los queries necesarios para servir contenido offline.

- **Flujo de descargas**: implementar la gestión completa de descargas con WorkManager, almacenamiento de segmentos HLS con `media3-datasource-cache`, cola persistente con estados (pendiente, descargando, completada, fallida), y una pantalla "Descargas" en la biblioteca con control de espacio.

- **Modo offline**: el cliente debe seguir funcionando sin red para contenido previamente descargado, mostrando un indicador claro de estado offline en el mini player y manejando los errores de red en pantallas que requieren conexión.

Si quieres contribuir a alguna de estas áreas, abre primero un issue describiendo tu enfoque para coordinar el diseño antes de implementar.

Detalles técnicos en [`docs/ROADMAP.md`](docs/ROADMAP.md).

### Otras áreas

- **Testing**: la cobertura de unit tests y UI tests aún es baja. Se busca infraestructura básica con MockK + Turbine + Compose UI testing.
- **Accesibilidad**: revisar `contentDescription` en composables, soporte de TalkBack.
- **Localización**: traducción a inglés, portugués y otros idiomas LATAM.
- **Performance**: profiling de listas largas, optimización del reproductor, reducción de recomposiciones innecesarias.

¡Gracias por contribuir!
