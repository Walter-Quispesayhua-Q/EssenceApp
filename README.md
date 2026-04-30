<div align="center">

# Essence

**Streaming de música nativo para Android, construido con Jetpack Compose y arquitectura limpia.**

[![Android](https://img.shields.io/badge/Android-9%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Spring Boot](https://img.shields.io/badge/Backend-Spring_Boot-6DB33F?logo=springboot&logoColor=white)](#)
[![Azure](https://img.shields.io/badge/Hosted-Azure-0078D4?logo=microsoftazure&logoColor=white)](#)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)

[Descargar APK](https://github.com/Walter-Quispesayhua-Q/EssenceApp/releases/latest) ·
[Documentación](docs/) ·
[Reportar bug](https://github.com/Walter-Quispesayhua-Q/EssenceApp/issues) ·
[Roadmap](docs/ROADMAP.md)

</div>

---

## Acerca del proyecto

**Essence** es un proyecto educativo que explora el desarrollo Android moderno de extremo a extremo: una aplicación nativa multimedia con su propio backend.

## Características principales

- Reproducción HLS adaptativa con Media3 ExoPlayer
- Búsqueda global de canciones, álbumes y artistas
- Historial de reproducción y recomendaciones personalizadas
- Notificación persistente con controles del sistema
- Interfaz fluida con Material 3 y modo oscuro automático
- Coexistencia de variantes `debug` y `release` en el mismo dispositivo

> Para el detalle técnico de cómo funciona cada característica, consulta [`CHANGELOG.md`](CHANGELOG.md) y [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

## Documentación

| Documento | Contenido |
|---|---|
| [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) | Clean Architecture, capas, módulos por feature |
| [`docs/BUILD.md`](docs/BUILD.md) | Cómo compilar, generar APK firmado, build types |
| [`docs/BACKEND.md`](docs/BACKEND.md) | API Spring Boot, integración con Azure |
| [`docs/SECURITY.md`](docs/SECURITY.md) | ProGuard, network security, gestión de keystore |
| [`docs/ROADMAP.md`](docs/ROADMAP.md) | Modo offline, descargas y mejoras futuras |

## Instalación rápida

1. Descarga el APK desde [Releases](https://github.com/Walter-Quispesayhua-Q/EssenceApp/releases/latest).
2. Ábrelo en tu dispositivo Android (9 o superior).
3. Permite la instalación desde fuente desconocida cuando Android lo solicite.
4. Listo.

> Para configurar el entorno de desarrollo, consulta [`docs/BUILD.md`](docs/BUILD.md).

## Contribuir

¿Encontraste un bug o tienes una idea? Lee [`CONTRIBUTING.md`](CONTRIBUTING.md) para conocer cómo reportar issues y enviar pull requests.

## Autor

**Walter Quispesayhua Quispe**

[![GitHub](https://img.shields.io/badge/GitHub-Walter--Quispesayhua--Q-181717?logo=github&logoColor=white)](https://github.com/Walter-Quispesayhua-Q)

## Licencia

Distribuido bajo la licencia **Apache 2.0**. Consulta el archivo [`LICENSE`](LICENSE) para conocer los términos completos.

```
Copyright 2026 Walter Quispesayhua Quispe

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```
