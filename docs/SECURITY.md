# Seguridad

Este documento describe las medidas de seguridad implementadas en Essence y cómo gestionarlas correctamente.

## Tabla de contenidos

- [Modelo de amenazas](#modelo-de-amenazas)
- [Network Security Config](#network-security-config)
- [ProGuard y R8](#proguard-y-r8)
- [Gestión del keystore](#gestión-del-keystore)
- [Gestión de secretos](#gestión-de-secretos)
- [Reportar vulnerabilidades](#reportar-vulnerabilidades)

## Modelo de amenazas

Las superficies de ataque consideradas son:

| Amenaza | Mitigación |
|---|---|
| Interceptación de tráfico (MITM) | HTTPS obligatorio en release vía `network_security_config.xml` |
| Reversa de la APK | R8 con minify + shrinking + obfuscation |
| Robo del keystore | Keystore fuera del repositorio + backups cifrados |
| Exposición de secretos en repo | `.gitignore` con `local.properties`, `*.jks`, `*.keystore` |
| Inyección de URLs maliciosas | URLs base inyectadas en `BuildConfig` por buildType, sin runtime override |

## Network Security Config

Essence usa dos configuraciones de red diferentes según el buildType.

### Release: HTTPS obligatorio

Archivo: `app/src/main/res/xml/network_security_config.xml`

```xml
<network-security-config>
    <base-config cleartextTrafficPermitted="false">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
</network-security-config>
```

- Bloquea **todo** tráfico HTTP plano.
- Solo se aceptan certificados emitidos por CAs reconocidas por el sistema.

### Debug: HTTP permitido a IPs locales

Archivo: `app/src/debug/res/xml/network_security_config.xml`

```xml
<network-security-config>
    <base-config cleartextTrafficPermitted="false">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">192.168.1.14</domain>
        <domain includeSubdomains="true">10.0.2.2</domain>
        <domain includeSubdomains="true">localhost</domain>
        <domain includeSubdomains="true">127.0.0.1</domain>
    </domain-config>
</network-security-config>
```

- Permite HTTP únicamente a las IPs/hosts listados (todos locales).
- Cualquier otra URL HTTP en debug también será bloqueada.

El sistema de overrides de Android resuelve automáticamente cuál archivo aplicar según el buildType.

## ProGuard y R8

En release, R8 ejecuta tres tareas:

1. **Shrinking**: elimina código no alcanzable.
2. **Optimization**: inlining, eliminación de código muerto, simplificaciones.
3. **Obfuscation**: renombra clases, métodos y campos a nombres cortos sin significado.

Las reglas viven en `app/proguard-rules.pro` y cubren:

- **Modelos serializables**: `data/dto/`, `domain/model/` se mantienen sin ofuscar para que Gson pueda deserializar correctamente.
- **Retrofit**: interfaces con anotaciones `@GET`, `@POST`, etc. se mantienen.
- **Hilt**: ViewModels y entry points se mantienen.
- **Media3, Coil, Coroutines**: librerías con dependencias reflexivas tienen sus reglas estándar.
- **NewPipe Extractor**: se mantiene para evitar romper la extracción.

### Reducción de tamaño

Esperable tras minify + shrink:

- APK debug: ~25-35 MB.
- APK release: **~10-18 MB**.

Si el APK release pesa significativamente más de 18 MB, revisa que `isMinifyEnabled = true` y `isShrinkResources = true` estén activos en el bloque `release` de `app/build.gradle.kts`.

## Gestión del keystore

### Reglas de oro

1. **Nunca** commitees el archivo `.jks` al repositorio.
2. **Nunca** compartas las contraseñas por canales no cifrados.
3. **Mantén 3 copias** del keystore en lugares físicamente separados:
   - PC principal.
   - Dispositivo USB cifrado.
   - Almacenamiento en la nube cifrado (Drive con E2EE, etc.).
4. **Documenta** las contraseñas en un gestor de contraseñas (Bitwarden, KeePass, 1Password).
5. **Nunca** uses el mismo keystore para apps distintas.

### Crear el keystore

```powershell
keytool -genkey -v -keystore essence-release.jks `
  -keyalg RSA -keysize 2048 -validity 9125 -alias essence
```

### Si pierdes el keystore

Si pierdes el `.jks` o sus contraseñas, **no puedes** firmar nuevas versiones de la app con la misma identidad. Las opciones son:

- **Distribuir como nueva app** con otro `applicationId` y otro keystore.
- En el caso de Play Store, contactar a Google Support con prueba de propiedad para reemplazar la clave (proceso lento y no garantizado).

Por eso es crítico mantener al menos 3 copias del keystore.

### Verificar la firma de un APK existente

```powershell
keytool -printcert -jarfile app/build/outputs/apk/release/app-release.apk
```

## Gestión de secretos

### Qué archivos NUNCA deben commitearse

Estos archivos están en `.gitignore` por defecto:

- `local.properties` (contiene SDK path y credenciales del keystore).
- `*.jks`, `*.keystore`, `*.p12`, `*.pfx` (cualquier formato de keystore).
- `*.apk`, `*.aab` (binarios; van solo a Releases, no al repo).
- `*.log`, `*.hprof` (logs y heap dumps pueden contener datos sensibles).

### Cómo agregar nuevos secretos

Si necesitas agregar un nuevo secreto (por ejemplo, una API key de un servicio externo):

1. **Nunca** lo escribas directamente en el código.
2. Agrégalo a `local.properties` con un prefijo claro:
   ```properties
   THIRD_PARTY_API_KEY=valor_secreto
   ```
3. Léelo en `app/build.gradle.kts` y exponlo via `BuildConfig`:
   ```kotlin
   val localProps = Properties().apply {
       val file = rootProject.file("local.properties")
       if (file.exists()) load(FileInputStream(file))
   }
   buildConfigField(
       "String",
       "THIRD_PARTY_API_KEY",
       "\"${localProps.getProperty("THIRD_PARTY_API_KEY") ?: ""}\""
   )
   ```
4. Consume desde el código:
   ```kotlin
   val key = BuildConfig.THIRD_PARTY_API_KEY
   ```

### Si un secreto se commiteó por error

Si por accidente se commiteó un secreto:

1. **Rota el secreto inmediatamente** (cambiarlo en el servicio externo).
2. Considera el secreto comprometido aunque hayas borrado el commit; el historial de git lo conserva.
3. Para limpiar el historial usa [BFG Repo-Cleaner](https://rtyley.github.io/bfg-repo-cleaner/) o `git filter-repo`.
4. Force-push la rama corregida y notifica al equipo.

## Reportar vulnerabilidades

Si descubres una vulnerabilidad de seguridad en Essence:

1. **NO la reportes públicamente** en Issues.
2. Contacta directamente al autor a través del email vinculado a su perfil de GitHub: [@Walter-Quispesayhua-Q](https://github.com/Walter-Quispesayhua-Q).
3. Incluye:
   - Descripción de la vulnerabilidad.
   - Pasos para reproducir.
   - Impacto potencial.
   - Sugerencia de mitigación si tienes una.

Se responderá en un plazo razonable y se acordará una ventana de divulgación coordinada.
