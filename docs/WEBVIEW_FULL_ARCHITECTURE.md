# Arquitectura WebView Full Container — KLMR Singles
**Documento Técnico:** `docs/WEBVIEW_FULL_ARCHITECTURE.md`  
**Aplicación:** KLMR Singles (`com.klmr.singles`)  
**Ecosistema:** ARKAIOS Music Platform  

---

## 1. Visión y Principios de Diseño
La arquitectura previa de KLMR Singles mantenía una duplicidad arquitectónica: existía una barra de navegación inferior en Kotlin que pintaba mediante Canvas iconos fijos para redes sociales y descargas, mientras una WebView interna mostraba el catálogo musical. Dicho modelo presentaba problemas graves de mantenimiento:
- Cualquier modificación a los enlaces sociales, adición de canciones o cambio en el flujo de descarga requería recompilar la APK, firmarla y forzar al usuario a descargar e instalar una actualización.
- La experiencia visual estaba desarticulada entre el Canvas nativo de Android y los estilos web neón/glassmorphic.

La nueva arquitectura **WebView Full, Web-First** transforma la APK en un **contenedor nativo ligero, seguro y de alto rendimiento**, delegando el 100% de la interfaz de usuario, navegación, canciones, portadas y reproductor al sitio web oficial (`https://djklmr2025.github.io/klmr-singles/`), reteniendo en Android exclusivamente las capacidades de hardware y sistema operativo:
1. **Gestor nativo de descargas (`DownloadManager`)** con soporte de archivos grandes y reanudación.
2. **Enrutamiento inteligente de intents** hacia aplicaciones externas instaladas (WhatsApp, YouTube, Facebook, tel, mailto).
3. **Manejo de estado offline** con página de reintento.
4. **Almacenamiento y caché HTTP/DOM Storage** para cargas ultrarrápidas.

```
+-------------------------------------------------------------------+
|                        KLMR SINGLES (APK)                         |
|                                                                   |
|  +-------------------------------------------------------------+  |
|  |                 WEBVIEW FULL SCREEN CONTAINER               |  |
|  |                                                             |  |
|  |   https://djklmr2025.github.io/klmr-singles/                |  |
|  |   - Catálogo de 28 Singles CD TRCKAS                        |  |
|  |   - Reproductor Web Audio + MediaSession                    |  |
|  |   - Barra de Navegación Responsive (FB, YT, WA, DJ, DL)     |  |
|  |   - Botón Descargas -> https://cd-trckas-plataforma...      |  |
|  |                                                             |  |
|  +-------------------------------------------------------------+  |
|         |                                           |             |
|         v (Descargas de archivos ZIP/MP3)           v (Intents)   |
|  +-----------------------------+           +-------------------+  |
|  |  Android DownloadManager    |           | Sistema Android   |  |
|  |  - Descargas en segundo     |           | - WhatsApp Intent |  |
|  |    plano con barra de estado|           | - YouTube App     |  |
|  |  - Cookies & User-Agent     |           | - Facebook App    |  |
|  +-----------------------------+           +-------------------+  |
+-------------------------------------------------------------------+
```

---

## 2. Componentes del Sistema

### 2.1 Sitio Web (`klmr-singles`)
* **Única fuente de verdad** para:
  - 28 canciones y metadatos de CD TRCKAS.
  - Portadas (`covers/01.jpg` a `covers/28.jpg`).
  - Videos incrustados de YouTube oficiales.
  - Barra de navegación universal `#app-nav` adaptada para móviles y desktop.
  - Enlaces oficiales actualizables sin tocar Android.
  - Redirección del botón Descargas directamente al portal exclusivo: `https://cd-trckas-plataforma.vercel.app/exclusive`.

### 2.2 Contenedor Android (`MainActivity.kt`)
* **Pantalla Completa:**
  - `web = WebView(this)` configurado como única vista (`setContentView(web)`).
  - Configuración de seguridad:
    - `javaScriptEnabled = true`
    - `domStorageEnabled = true`
    - `databaseEnabled = true`
    - `allowFileAccess = false` (previene fugas de archivos locales)
    - `allowContentAccess = false`
    - `mixedContentMode = MIXED_CONTENT_NEVER_ALLOW` (fuerza HTTPS estricto)
* **DownloadListener con Android DownloadManager:**
  - Resuelve nombres de archivo automáticamente con `URLUtil.guessFileName`.
  - Inyecta cabeceras `Cookie` y `User-Agent`.
  - Almacena en `Environment.DIRECTORY_DOWNLOADS`.
  - Habilita notificación nativa de descarga completada.
* **Enrutamiento de Esquemas e Intents:**
  - Dominios permitidos internos:
    - `djklmr2025.github.io`
    - `cd-trckas-plataforma.vercel.app`
    - `djklmr.pro`
    - `www.djklmr.pro`
  - Esquemas externos que lanzan Intent:
    - `whatsapp://` y enlaces a `wa.me`
    - `https://www.facebook.com/...`
    - `https://www.youtube.com/...`
    - `tel:`, `mailto:`, `sms:`
* **Navegación Histórica:**
  - `onBackPressed()` consulta `web.canGoBack()` para retroceder en la navegación web antes de salir de la aplicación.

---

## 3. Matriz de Responsabilidades y Ciclo de Vida

| Funcionalidad | Dónde se Administra | ¿Requiere Recompilar APK? |
| :--- | :--- | :---: |
| Nuevas canciones o singles | Sitio Web (`index.html`) | **NO** |
| Nuevas portadas o letras | Sitio Web (`covers/`) | **NO** |
| Modificación de enlaces de redes sociales | Sitio Web (`#app-nav`) | **NO** |
| Actualización del portal de descargas | Vercel (`/exclusive`) | **NO** |
| Correcciones CSS / responsive / colores | Sitio Web (`index.html`) | **NO** |
| Nuevos permisos de hardware (ej. Cámara) | Android (`AndroidManifest.xml`) | **SÍ** |
| Cambios en minSdk / targetSdk | Android (`build.gradle.kts`) | **SÍ** |
| Modificación del certificado de firma | Almacén de claves (`.p12`) | **SÍ** |

---

## 4. Beneficios Operativos
1. **Mantenimiento Cero en Tiendas/Distribución:** El artista o administrador actualiza el sitio web y todos los usuarios de la APK reciben el cambio al instante.
2. **Rendimiento Nativo en Descargas:** Las descargas pesadas (ZIP del álbum) se gestionan mediante el subsistema de descargas del sistema operativo Android, funcionando incluso si la app pasa a segundo plano.
3. **Consistencia Visual Absoluta:** No hay discrepancia entre la web y la aplicación móvil; ambas comparten la misma identidad artística y tipografía.
