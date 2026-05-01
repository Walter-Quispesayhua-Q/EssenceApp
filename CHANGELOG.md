# Changelog

Todos los cambios relevantes del proyecto se documentan en este archivo.

El formato sigue el estándar [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/) y este proyecto adhiere a [Semantic Versioning](https://semver.org/lang/es/).

---

## [1.0.1] — Mejora del registro de historial

### Cambios

- **Historial de reproducción más oportuno**: el POST al backend ahora se dispara automáticamente al alcanzar 60 segundos de reproducción, en lugar de esperar al final de la canción. Mejora la experiencia cuando el usuario salta de canción tras escuchar lo suficiente.
- Mantiene el comportamiento de fallback `recordCompleted` para canciones más cortas que 60 segundos.

### Documentación

- Reestructura completa: el `README.md` ahora es presentación pura con badges profesionales.
- Nueva carpeta `docs/` con `ARCHITECTURE.md`, `BUILD.md`, `BACKEND.md`, `ROADMAP.md` y `SECURITY.md`.
- Nuevo `CHANGELOG.md` siguiendo el formato Keep a Changelog.
- Nuevo `CONTRIBUTING.md` con guía de bugs, PRs y áreas prioritarias (modo offline y descargas destacadas).
- `docs/BACKEND.md` simplificado para no exponer detalles internos de infraestructura.

---

## [1.0.0] — Primera versión pública

### Reproducción de audio

- Integración con **Media3 ExoPlayer** para reproducción HLS adaptativa.
- **MediaSession** persistente con notificación del sistema y controles en pantalla de bloqueo.
- Servicio en primer plano (`MediaPlaybackService`) con tipo `mediaPlayback`.
- Cache de segmentos HLS con `media3-datasource-cache`.
- Recolección de historial de reproducción a través de `PlaybackHistoryRecorder`.

### Navegación y UI

- Interfaz construida 100% con **Jetpack Compose** y Material 3.
- Splash screen con animación nativa de fade out (`HOLD = 70 ms`, `EXIT = 90 ms`).
- Transiciones nativas entre tabs con `slideInHorizontally` y `fadeIn`.
- Shell principal (`MainShellScreen`) con Bottom Navigation y mini player flotante.
- Modo oscuro automático según preferencia del sistema.

### Búsqueda y descubrimiento

- Búsqueda global vía endpoint `GET /search?query=...&type=...&page=...`.
- Resultados unificados de canciones, álbumes y artistas en una sola respuesta.
- Pantalla **Home** con secciones de recomendados, recientes, top y trending.
- Pantallas de detalle para canción, álbum y artista con reproducción en línea.

### Autenticación

- Flujo de login y registro con validación local.
- Persistencia de sesión en **DataStore Preferences**.
- Refresco automático de usuario en `LifecycleResumeEffect` cuando vuelve al frente.

### Networking

- **Retrofit 2 + OkHttp + Gson** con adaptadores personalizados para `LocalDate` e `Instant`.
- Inyección de dependencias con **Hilt** en módulo `NetworkModule`.
- URL base inyectada vía `BuildConfig.BASE_URL` por buildType.
- Resolución inteligente de URLs de imágenes (`ImageUrlResolver`) que tolera URLs absolutas, relativas, protocol-relative y data URIs.

### Build y distribución

- Variantes `debug` (apunta a localhost) y `release` (apunta a Azure).
- `applicationIdSuffix = ".dev"` permite tener ambas variantes coexistiendo en el mismo dispositivo.
- **R8** con `isMinifyEnabled = true` y `isShrinkResources = true` en release.
- Reglas ProGuard específicas para Retrofit, Gson, Hilt, Media3, Coil, NewPipe Extractor y Mozilla Rhino.
- Configuración de `network_security_config.xml` que permite HTTP solo a IPs locales en debug y fuerza HTTPS en release.
- Firma de APK con keystore RSA 2048 bits configurada vía `local.properties`.

### Performance

- `LifecycleResumeEffect` con threshold de 30 s evita refrescos innecesarios al cambiar entre tabs.
- Cache singleton en `HomeRepositoryImpl` evita peticiones duplicadas.
- `computeInitialState()` muestra contenido cacheado al instante sin skeleton, eliminando el lag perceptible al volver a Home.
- `LinearProgressIndicator` sutil (1.5 dp, alpha 0.65) para refrescos en background.

### Seguridad

- Tráfico HTTP bloqueado en release.
- Tráfico HTTP permitido solo a IPs locales en debug (`192.168.x.x`, `10.0.2.2`, `localhost`, `127.0.0.1`).
- Keystores y contraseñas excluidos del repositorio vía `.gitignore`.
- Configuración sensible cargada exclusivamente desde `local.properties`.

---

[1.0.1]: https://github.com/Walter-Quispesayhua-Q/EssenceApp/releases/tag/v1.0.1
[1.0.0]: https://github.com/Walter-Quispesayhua-Q/EssenceApp/releases/tag/v1.0.0
