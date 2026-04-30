# Arquitectura

Essence sigue **Clean Architecture** combinada con el patrón **MVVM** sobre **Jetpack Compose**. Cada feature del producto se modela como un módulo independiente con sus tres capas (data, domain, ui), de forma que el dominio sea testeable y desacoplado de detalles de infraestructura.

## Tabla de contenidos

- [Visión general](#visión-general)
- [Capas](#capas)
- [Estructura de directorios](#estructura-de-directorios)
- [Flujo de datos](#flujo-de-datos)
- [Inyección de dependencias](#inyección-de-dependencias)
- [Gestión de estado](#gestión-de-estado)
- [Navegación](#navegación)
- [Reproductor de audio](#reproductor-de-audio)

## Visión general

```
┌─────────────────────────────────────────────────────────────┐
│                          UI Layer                            │
│  Composables · ViewModels · UI State (StateFlow)             │
└──────────────────────────┬──────────────────────────────────┘
                           │ observa estado
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                       Domain Layer                           │
│  Use Cases · Domain Models · Repository Interfaces           │
└──────────────────────────┬──────────────────────────────────┘
                           │ delega
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                        Data Layer                            │
│  Repositories Impl · API Services · DTOs · Mappers           │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
                    ┌─────────────┐
                    │  Backend    │
                    │ Spring Boot │
                    └─────────────┘
```

## Capas

### Data

Responsable de la **obtención y transformación** de datos. Contiene:

- **API services**: interfaces Retrofit con anotaciones `@GET`, `@POST`, etc.
- **DTOs**: clases serializables que reflejan la respuesta del backend.
- **Mappers**: funciones de extensión que convierten DTO en modelo de dominio.
- **Repository implementations**: implementan los contratos del dominio y orquestan llamadas a la API y a fuentes locales.

### Domain

El **núcleo de negocio**, sin dependencias de Android. Contiene:

- **Models**: clases puras Kotlin que representan entidades.
- **Repository interfaces**: contratos que la capa data debe implementar.
- **Use cases**: una operación de negocio por clase, invocada vía `operator fun invoke(...)`.

### UI

La **capa de presentación** con Jetpack Compose. Contiene:

- **Composables**: funciones `@Composable` que dibujan la UI.
- **ViewModels**: gestionan el estado y orquestan use cases.
- **UI State**: clases sealed o data classes que modelan los estados posibles de la pantalla.

## Estructura de directorios

```
app/src/main/java/com/essence/essenceapp/
├── core/
│   ├── di/                  Hilt modules transversales
│   ├── network/             Retrofit, OkHttp, ApiConstants, ImageUrlResolver
│   └── theme/               Tipografía, colores, dimensiones globales
│
├── feature/
│   ├── auth/
│   │   ├── data/{api, dto, mapper, repository}/
│   │   ├── domain/{model, usecase, repository}/
│   │   └── ui/{login, register}/
│   │
│   ├── home/
│   │   ├── data/...
│   │   ├── domain/...
│   │   └── ui/{components, navigation}/
│   │
│   ├── search/, song/, album/, artist/, library/  (mismo patrón)
│
├── navigation/              AppNavHost, RootRoutes
├── ui/
│   ├── shell/               MainShellScreen, BottomBar, MiniPlayer
│   └── splash/              SplashScreen
│
├── EssenceApp.kt            @HiltAndroidApp
└── MainActivity.kt          @AndroidEntryPoint
```

## Flujo de datos

Ejemplo: el usuario busca "Justin Bieber".

```
1. SearchScreen
   └─→ Composable observa SearchUiState desde SearchViewModel

2. SearchViewModel.search("Justin Bieber")
   └─→ Lanza coroutine en viewModelScope
       └─→ Invoca SearchUseCase(query)

3. SearchUseCase
   └─→ Llama a SearchRepository.search(query)

4. SearchRepositoryImpl
   ├─→ SearchApiService.search(query) [Retrofit]
   │    └─→ GET /api/v1/search?query=Justin+Bieber
   └─→ response.searchToDomain() [Mapper]

5. El resultado vuelve por la misma cadena hasta el ViewModel,
   que actualiza SearchUiState.

6. SearchScreen recompone con la nueva data.
```

## Inyección de dependencias

Se usa **Hilt** con grafo modular:

- `@HiltAndroidApp` en `EssenceApp` inicia el grafo.
- `@AndroidEntryPoint` en `MainActivity` y servicios.
- `@HiltViewModel` en cada ViewModel con `@Inject` constructor.
- Módulos `@InstallIn(SingletonComponent::class)` para singletons (Retrofit, OkHttp, Gson, repositorios).
- Módulos `@InstallIn(ViewModelComponent::class)` para use cases (un nuevo grafo por ViewModel).

`NetworkModule` provee la cadena Retrofit → OkHttp → Gson con sus adaptadores personalizados para `LocalDate` e `Instant`.

## Gestión de estado

- Cada ViewModel expone uno o varios `StateFlow<T>` con el estado de la pantalla.
- Los composables consumen estado vía `collectAsStateWithLifecycle()` para respetar el ciclo de vida.
- Estado inicial siempre derivado del cache cuando existe (`computeInitialState()` en HomeViewModel) para eliminar pantallas en blanco.
- Eventos one-shot (snackbars, navegación) se exponen vía `Channel` y se consumen con `receiveAsFlow()`.

## Navegación

Construida con **Navigation Compose** y dos niveles:

### Nivel raíz (`AppNavHost`)

```
RootRoutes
├── SPLASH      Pantalla de carga inicial
└── MAIN_SHELL  Contenedor principal con tabs
```

Las transiciones raíz usan `slideInHorizontally + fadeIn` con duración `ROOT_ENTER_DURATION_MS = 115 ms`.

### Nivel shell (`MainShellScreen`)

```
ShellTabs
├── HomeGraph
├── SearchGraph
├── LibraryGraph
└── ProfileGraph
```

Cada tab es un sub-NavHost con su propio back stack. Se usa `saveState = true` y `restoreState = true` en las llamadas a `navigate()` para preservar estado entre cambios de tab.

## Reproductor de audio

El reproductor usa **Media3 ExoPlayer** dentro de un servicio en primer plano:

```
PlaybackManager (singleton inyectado)
   ↓ controla
MediaPlaybackService (Foreground Service)
   ↓ contiene
MediaSession + ExoPlayer
   ↓ reproduce
HLS Master Playlist (.m3u8)
```

- El servicio expone `MediaSession` para que Android System y Auto/Wear puedan controlarlo.
- La notificación es generada automáticamente por Media3.
- El cache de segmentos HLS se gestiona con `media3-datasource-cache`.
- Los eventos de reproducción exitosa se emiten a `PlaybackHistoryRecorder.historyRecorded`, que `HomeViewModel` observa para refrescar el historial.

## Decisiones notables

- **Sin librería de pagos** porque el proyecto es educativo y no monetiza.
- **Gson sobre Kotlinx Serialization** por compatibilidad con Retrofit y menor configuración.
- **Coil sobre Glide** por su API basada en coroutines y mejor integración con Compose.
- **DataStore Preferences** sobre SharedPreferences por ser asíncrono y type-safe.
- **Hilt** sobre Koin/Dagger puro por su integración nativa con Compose y ViewModels.
