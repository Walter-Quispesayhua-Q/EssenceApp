# Roadmap

Este documento describe las mejoras planificadas para Essence. Las prioridades pueden ajustarse según el feedback recibido en [Issues](https://github.com/Walter-Quispesayhua-Q/EssenceApp/issues).

## Tabla de contenidos

- [Estado actual](#estado-actual)
- [Próxima versión: 1.1.0](#próxima-versión-110)
- [Versión 1.2.0](#versión-120)
- [Versión 2.0.0](#versión-200)
- [Ideas exploratorias](#ideas-exploratorias)
- [Cómo proponer cambios](#cómo-proponer-cambios)

## Estado actual

La versión publicada (`1.0.0`) cumple el alcance mínimo viable: descubrimiento, búsqueda, reproducción HLS, historial e interfaz Material 3. Los siguientes hitos están centrados en **robustez**, **modo offline** y **mejor experiencia con backend en cold start**.

## Próxima versión: 1.1.0

> Foco: estabilidad y experiencia en condiciones de red imperfectas.

### Pre-warm del backend desde el splash

El cliente disparará una petición silenciosa a `/actuator/health` durante el splash para que el contenedor de Azure se despierte mientras el usuario ve la animación inicial. Esto reducirá drásticamente el cold start visible.

### Coil con timeouts extendidos y retry automático

Configuración personalizada del `ImageLoader` global con:

- Timeout de conexión: 30 segundos.
- Reintentos automáticos con backoff exponencial.
- Cache agresivo en disco con `DiskCache` propio.

### Manejo robusto de errores de red

- `Result<T>` o `Either<Error, T>` en use cases para diferenciar errores de negocio vs infraestructura.
- Pantalla de error global con botón de reintento.
- Snackbars informativos para errores de red puntuales.

### Mejoras de accesibilidad

- `contentDescription` en todos los iconos y portadas.
- Soporte de TalkBack revisado en pantalla a pantalla.
- Tamaños de texto que respetan la preferencia del sistema.

## Versión 1.2.0

> Foco: **modo offline** y **descargas**.

### Descarga de canciones

Permitir al usuario marcar canciones, álbumes o artistas para descarga local:

- Cola de descargas gestionada con **WorkManager**.
- Almacenamiento de segmentos HLS en cache persistente con `media3-datasource-cache`.
- UI con indicador de estado (pendiente, descargando, completada, fallida).
- Pantalla "Descargas" en la biblioteca con gestión de espacio.

### Reproducción offline

- ExoPlayer detecta automáticamente segmentos cacheados y los usa sin red.
- Indicador visual de "modo offline" en el mini player cuando no hay conexión.
- Cola de reproducción persistente sobreviviendo cierres de la app.

### Sincronización de historial

- Encolado de eventos de reproducción cuando no hay red.
- Subida en lote al recuperarse la conexión.
- Resolución de conflictos por timestamp.

## Versión 2.0.0

> Foco: **producto cerrado** y **calidad de vida**.

### Listas de reproducción personales

CRUD completo de playlists del usuario, ordenamiento, importación/exportación.

### Lyrics sincronizadas

Visualización de letras sincronizadas (formato LRC) con scroll automático según el progreso de reproducción.

### Recomendaciones contextuales

- "Porque escuchaste X" basado en historial reciente.
- Mix diario generado por el backend.
- Radio infinita basada en una canción semilla.

### Integración con Wear OS

Aplicación complementaria mínima en reloj inteligente con controles de reproducción.

### Localización

Traducción del producto a inglés, portugués y otros idiomas LATAM.

## Ideas exploratorias

Estas ideas no tienen versión asignada, dependen de viabilidad técnica e interés:

- **Cast a Chromecast** vía MediaSession.
- **Letras karaoke** con resaltado palabra por palabra.
- **Compartir canción** con preview embebido para redes sociales.
- **Modo conducción** con UI simplificada para tap grande.
- **Sleep timer** para detener reproducción tras N minutos.
- **Ecualizador** con presets configurables.

## Cómo proponer cambios

Si tienes una idea que crees que debería estar en este roadmap:

1. Verifica que no esté ya listada arriba.
2. Abre un [Issue](https://github.com/Walter-Quispesayhua-Q/EssenceApp/issues/new) con etiqueta `enhancement`.
3. Describe el problema que resuelve y al menos un enfoque técnico viable.

Las decisiones sobre roadmap se documentan en los issues y se reflejan aquí cuando alcanzan la fase de planificación.
