# Compilación y empaquetado

Esta guía cubre todo lo necesario para compilar Essence en local, generar APKs firmados y entender las variantes de build.

## Tabla de contenidos

- [Prerrequisitos](#prerrequisitos)
- [Clonar y abrir el proyecto](#clonar-y-abrir-el-proyecto)
- [Variables locales](#variables-locales)
- [Build types](#build-types)
- [Compilar debug](#compilar-debug)
- [Generar APK firmado release](#generar-apk-firmado-release)
- [Verificar el APK](#verificar-el-apk)
- [Solución de problemas](#solución-de-problemas)

## Prerrequisitos

| Herramienta | Versión mínima |
|---|---|
| Android Studio | Ladybug 2024.2.1 |
| Android Gradle Plugin | 8.7+ |
| JDK | 17 |
| Gradle | 8.10+ (incluido en wrapper) |
| Git | 2.40+ |

El proyecto usa Gradle Wrapper, así que no es necesario instalar Gradle manualmente.

## Clonar y abrir el proyecto

```bash
git clone https://github.com/Walter-Quispesayhua-Q/EssenceApp.git
cd EssenceApp
```

Abre la carpeta en Android Studio. La primera sincronización descargará dependencias (Compose BOM, Media3, Hilt, etc.) y puede tardar varios minutos.

## Variables locales

El archivo `local.properties` está excluido del repositorio. Debes crearlo en la raíz con el siguiente contenido mínimo:

```properties
sdk.dir=C:/Users/<usuario>/AppData/Local/Android/Sdk
```

Para generar APKs firmados, agrega también:

```properties
RELEASE_KEYSTORE_FILE=C:/Users/<usuario>/Keys/essence-release.jks
RELEASE_KEYSTORE_PASSWORD=<contraseña_del_keystore>
RELEASE_KEY_ALIAS=essence
RELEASE_KEY_PASSWORD=<contraseña_del_alias>
```

> Las credenciales del keystore **nunca** deben commitearse. Consulta [`docs/SECURITY.md`](SECURITY.md) para más detalles sobre la gestión del keystore.

## Build types

Essence define dos variantes en `app/build.gradle.kts`:

| Característica | `debug` | `release` |
|---|---|---|
| Application ID | `com.essence.essenceapp.dev` | `com.essence.essenceapp` |
| Version Name | `1.0-dev` | `1.0` |
| Backend URL | `http://192.168.1.14:8099/api/v1/` | URL de Azure (HTTPS) |
| `isDebuggable` | `true` | `false` |
| `isMinifyEnabled` | `false` | `true` |
| `isShrinkResources` | `false` | `true` |
| ProGuard | desactivado | activo con reglas en `proguard-rules.pro` |
| HTTP en cleartext | permitido a IPs locales | bloqueado totalmente |

El `applicationIdSuffix = ".dev"` en debug permite tener ambas variantes instaladas a la vez en el mismo dispositivo, sin conflictos.

## Compilar debug

Desde Android Studio simplemente presiona **Run ▶**.

Desde terminal:

```powershell
.\gradlew.bat assembleDebug
```

Resultado:

```
app/build/outputs/apk/debug/app-debug.apk
```

Instalación directa en dispositivo conectado:

```powershell
.\gradlew.bat installDebug
```

## Generar APK firmado release

### Paso 1: Crear el keystore (solo la primera vez)

```powershell
keytool -genkey -v -keystore essence-release.jks `
  -keyalg RSA -keysize 2048 -validity 9125 -alias essence
```

`9125` días equivale a 25 años de validez. Mueve el archivo resultante fuera del repositorio (por ejemplo `C:/Users/<usuario>/Keys/`).

### Paso 2: Configurar `local.properties`

Agrega las cuatro variables `RELEASE_*` mostradas en la sección [Variables locales](#variables-locales).

### Paso 3: Generar el APK

**Opción A: desde Android Studio**

1. Build → Generate Signed App Bundle / APK
2. Selecciona **APK**
3. Apunta a tu `essence-release.jks` e ingresa contraseñas
4. Selecciona variante **release** y presiona **Create**

**Opción B: desde terminal**

```powershell
.\gradlew.bat assembleRelease
```

El APK firmado se generará en:

```
app/build/outputs/apk/release/app-release.apk
```

Tamaño esperado tras minify y shrinking: **10–18 MB** aproximadamente.

## Verificar el APK

### Comprobar la firma

```powershell
keytool -printcert -jarfile app/build/outputs/apk/release/app-release.apk
```

Debe mostrar el subject que pusiste al crear el keystore.

### Comprobar el `BuildConfig.BASE_URL` resultante

Tras generar el APK, abre el archivo y revisa que la URL embebida apunte al backend correcto:

```powershell
# Necesitas apkanalyzer (incluido en Android SDK)
apkanalyzer dex packages app/build/outputs/apk/release/app-release.apk | Select-String "BuildConfig"
```

### Probar instalación

Transfiere el APK al dispositivo y ábrelo desde el explorador de archivos. Android pedirá permiso para instalar desde fuente desconocida la primera vez.

## Solución de problemas

### `Unresolved reference 'BASE_URL'`

Causa: Gradle aún no ha generado la clase `BuildConfig`.

Solución:

1. **File → Sync Project with Gradle Files** en Android Studio.
2. Si persiste: **Build → Clean Project** y luego **Build → Rebuild Project**.

### `Missing classes detected while running R8`

Causa: una librería referencia clases que no existen en Android (típicamente jsoup, Mozilla Rhino, java.beans).

Solución: agregar reglas `-dontwarn` en `app/proguard-rules.pro`. Las reglas actuales ya cubren los casos conocidos. Si aparecen nuevas clases faltantes, suma `-dontwarn <paquete>.**`.

### El APK funciona en debug pero crashea en release

Causa habitual: ProGuard ofusca un DTO que se serializa con Gson.

Solución: agregar `-keep class com.essence.essenceapp.<feature>.data.dto.** { *; }` al `proguard-rules.pro`. Las reglas actuales ya cubren los DTOs por convención de paquete.

### Las imágenes no cargan en release

Causa más probable: el backend está en **cold start** (Azure Container Apps con scale-to-zero).

Solución temporal: esperar 15-30 segundos y reintentar la búsqueda. Solución definitiva en [`docs/ROADMAP.md`](ROADMAP.md).
