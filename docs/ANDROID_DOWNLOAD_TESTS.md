# Protocolo de Pruebas de Descargas y WebView — KLMR Singles
**Documento Técnico:** `docs/ANDROID_DOWNLOAD_TESTS.md`  
**Aplicación:** KLMR Singles (`com.klmr.singles`)  
**Portal Exclusivo:** `https://cd-trckas-plataforma.vercel.app/exclusive`  
**Sitio Web:** `https://djklmr2025.github.io/klmr-singles/`  

---

## 1. Alcance y Objetivos de Prueba
Validar que:
1. La aplicación Android opera de forma 100% fluida en modo **WebView Full**.
2. Las descargas iniciadas desde la WebView o portales asociados son capturadas y gestionadas por el `DownloadManager` nativo de Android.
3. El botón Descargas redirige sin fricción al portal exclusivo `https://cd-trckas-plataforma.vercel.app/exclusive`.
4. El portal exclusivo valida el código maestro exclusivamente del lado del servidor (backend `/api/exclusive-access`), sin exponer llaves en el cliente.
5. Los archivos descargados (ZIP del álbum completo / pistas) mantienen integridad y nombres correctos en el almacenamiento público de Android (`Downloads`).

---

## 2. Casos de Prueba Ejecutados

### TC-01: Carga y Renderizado WebView Full
* **Objetivo:** Comprobar que la aplicación carga el sitio web a pantalla completa sin elementos nativos duplicados.
* **Precondición:** Aplicación instalada y abierta en dispositivo/emulador Android.
* **Procedimiento:**
  1. Iniciar la actividad `com.klmr.singles.MainActivity`.
  2. Verificar URL cargada inicial (`https://djklmr2025.github.io/klmr-singles/`).
  3. Comprobar ausencia de la barra inferior dibujada en Kotlin.
  4. Comprobar presencia de la barra web responsive `#app-nav`.
* **Resultado Esperado:** Carga instantánea a pantalla completa; la barra responsive se ajusta perfectamente a la pantalla vertical respetando safe areas.
* **Resultado Obtenido:** **PASS**

---

### TC-02: Reproducción Musical y Navegación entre Singles
* **Objetivo:** Verificar que el motor de Web Audio y MediaSession funcionan en la WebView.
* **Procedimiento:**
  1. Tocar la tarjeta de la pista 01 ("A dos cuerdas").
  2. Verificar transición al reproductor `#pv`.
  3. Comprobar que el audio se reproduce desde `https://cd-trckas-plataforma.vercel.app/audio/01.mp3`.
  4. Presionar "Siguiente" (⏮ / ⏭) y pausar/reanudar (▶ / ⏸).
* **Resultado Esperado:** Reproducción fluida sin bloqueos, visualización correcta de carátulas y actualización de tiempos.
* **Resultado Obtenido:** **PASS**

---

### TC-03: Botón Descargas — Redirección a Portal Exclusivo
* **Objetivo:** Comprobar que el botón Descargas abre `/exclusive` y ya no muestra la leyenda "muy pronto".
* **Procedimiento:**
  1. Presionar el botón "Descargas" en la barra de navegación `#app-nav` o "Obtener el CD completo desde aquí" en la cabecera.
  2. Verificar destino de la navegación.
  3. Revisar el DOM para comprobar que el texto "Las descargas estarán disponibles muy pronto" fue eliminado.
* **Resultado Esperado:** Navegación directa a `https://cd-trckas-plataforma.vercel.app/exclusive`. La pantalla de descargas falsas queda erradicada.
* **Resultado Obtenido:** **PASS**

---

### TC-04: Portal Exclusivo — Validación Server-Side
* **Objetivo:** Evaluar la seguridad del flujo de validación del código maestro.
* **Comprobación:**
  - Inspección del código fuente de `https://cd-trckas-plataforma.vercel.app/exclusive`.
  - El formulario envía: `POST /api/exclusive-access` con `{ idToken, code }`.
  - El código de acceso NO se encuentra en variables de JavaScript, meta tags ni archivos estáticos.
* **Casos del Servidor:**
  - Código inválido: Retorna `{ authorized: false, error: "Acceso rechazado" }`.
  - Código correcto con sesión activa: Retorna `{ authorized: true }` y habilita descarga.
* **Resultado Esperado:** Cero credenciales maestras o URLs privadas expuestas en el cliente o APK.
* **Resultado Obtenido:** **PASS**

---

### TC-05: Descargas Nativas Android (`DownloadManager`)
* **Objetivo:** Comprobar que el listener `setDownloadListener` en Android captura enlaces directos de descarga y los procesa vía `DownloadManager`.
* **Implementación probada en `MainActivity.kt`:**
  ```kotlin
  web.setDownloadListener { url, userAgent, contentDisposition, mimetype, _ ->
      handleDownload(url, userAgent, contentDisposition, mimetype)
  }
  ```
* **Comportamiento verificado:**
  1. `URLUtil.guessFileName` infiere el nombre correcto (ej. `KLMR_CD_TRCKAS_COMPLETO.zip`).
  2. Extrae las cookies de sesión con `CookieManager.getInstance().getCookie(url)`.
  3. Configura `setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)`.
  4. Registra notificación en la barra del sistema: `VISIBILITY_VISIBLE_NOTIFY_COMPLETED`.
  5. Despliega Toast confirmatorio al usuario: "Iniciando descarga en el gestor de Android...".
* **Resultado Obtenido:** **PASS**

---

### TC-06: Manejo de Enlaces Externos (WhatsApp, YouTube, Redes)
* **Objetivo:** Asegurar que los botones externos no se bloquean dentro de la WebView y abren la aplicación correspondiente o el navegador.
* **Procedimiento:**
  1. Tocar WhatsApp (`https://wa.me/525516469310`).
  2. Tocar YouTube (`https://www.youtube.com/@KLMROFFCIALCHANNEL`).
  3. Tocar Facebook (`https://www.facebook.com/KLMROFFICIALCHANNEL`).
* **Comportamiento en `shouldOverrideUrlLoading`:**
  - Detecta dominios externos y lanza `Intent(Intent.ACTION_VIEW, uri)` con flag `FLAG_ACTIVITY_NEW_TASK`.
  - Si el usuario tiene la app de WhatsApp o YouTube instalada, el sistema operativo le ofrece abrirla directamente.
* **Resultado Obtenido:** **PASS**

---

### TC-07: Comportamiento del Botón Atrás (`onBackPressed`)
* **Objetivo:** Prevenir que la aplicación se cierre accidentalmente al presionar Atrás dentro de la navegación web.
* **Procedimiento:**
  1. Abrir un single.
  2. Presionar el botón físico o gesto Atrás de Android.
* **Comportamiento verificado:**
  - `web.canGoBack()` detecta el historial de hash o páginas y retrocede dentro de la WebView (`web.goBack()`).
  - Solo si no hay más historial web, se invoca `super.onBackPressed()`.
* **Resultado Obtenido:** **PASS**

---

## 3. Matriz de Resultados

| ID | Prueba | Componente | Estatus |
| :--- | :--- | :--- | :---: |
| TC-01 | Renderizado WebView Full | Android / Web | **PASS** |
| TC-02 | Web Audio & MediaSession | Web Player | **PASS** |
| TC-03 | Redirección `/exclusive` | Web / Navigation | **PASS** |
| TC-04 | Validación Server-Side | Backend / Vercel API | **PASS** |
| TC-05 | Descargas Nativa Android | Android `DownloadManager` | **PASS** |
| TC-06 | Intents Externos (WA/YT/FB) | Android `WebViewClient` | **PASS** |
| TC-07 | Botón Atrás Seguro | Android Lifecycle | **PASS** |
