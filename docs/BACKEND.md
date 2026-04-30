# Backend

El backend de Essence es un servicio REST construido con **Spring Boot** y desplegado en **Azure**. Esta guía describe únicamente cómo se integra con el cliente Android.

## Tabla de contenidos

- [URL base por entorno](#url-base-por-entorno)
- [Endpoints principales](#endpoints-principales)
- [Manejo de imágenes](#manejo-de-imágenes)
- [Cold start](#cold-start)

## URL base por entorno

La URL del backend se inyecta en tiempo de compilación a través de `BuildConfig.BASE_URL`:

| Build type | URL base |
|---|---|
| `debug` | URL local en la red de desarrollo |
| `release` | URL pública del servicio en Azure |

Las URLs se definen en `app/build.gradle.kts` dentro del bloque `buildConfigField` correspondiente a cada buildType.

## Endpoints principales

> Esta sección documenta los endpoints **consumidos por el cliente Android**.

### Búsqueda

```
GET /api/v1/search
```

**Query params:**

| Param | Tipo | Requerido | Descripción |
|---|---|---|---|
| `query` | `String` | Sí | Texto a buscar |
| `type` | `String?` | No | Filtro: `song`, `album`, `artist` |
| `page` | `Int` | No | Página (default 0) |

### Categorías

```
GET /api/v1/search/categories
```

### Home

```
GET /api/v1/home
```

### Detalles

```
GET /api/v1/songs/{id}
GET /api/v1/albums/{id}
GET /api/v1/artists/{id}
```

### Reproducción

```
GET /api/v1/songs/{id}/playback
```

### Autenticación

```
POST /api/v1/auth/login
POST /api/v1/auth/register
GET  /api/v1/users/me
```

## Manejo de imágenes

Las URLs de imágenes vienen en los DTOs como `imageKey` y se resuelven automáticamente según el formato recibido.

La lógica vive en `ImageUrlResolver.kt`.

## Cold start

El backend usa escalado dinámico. Tras un periodo de inactividad, la primera petición puede tardar unos segundos mientras la instancia arranca.

**Mitigaciones implementadas en el cliente:**

- Cache de respuestas en `HomeRepositoryImpl`.
- `computeInitialState()` muestra el cache mientras el backend responde.
- Reintentos automáticos de Retrofit.
