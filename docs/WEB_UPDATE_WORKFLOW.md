# Flujo de Actualización Web — KLMR Singles
**Documento Técnico:** `docs/WEB_UPDATE_WORKFLOW.md`  
**Aplicación:** KLMR Singles (`com.klmr.singles`)  
**Arquitectura:** WebView Full (Web-First)  

---

## 1. El Nuevo Paradigma: Actualizaciones Instantáneas
A partir de la versión 1.2 (WebView Full), el contenido principal y las funciones de usuario de KLMR Singles se administran desde el repositorio del sitio web:
`https://github.com/djklmr2025/klmr-singles`

Esto significa que **el 95% de las actualizaciones habituales no requieren compilar, firmar ni reinstalar la APK**.

---

## 2. Lo que PUEDE actualizarse desde la Web (Sin Recompilar Android)

1. **Catálogo Musical:**
   - Añadir nuevos singles o canciones al arreglo `T` en `index.html`.
   - Modificar títulos, subtítulos o metadatos de las canciones.
   - Cambiar los archivos de audio en el servidor de Vercel (`AUDIO_BASE`).
2. **Arte y Diseño Visual:**
   - Nuevas portadas en la carpeta `covers/`.
   - Actualizaciones de colores, tipografías y efectos visuales neón / glassmorphism.
   - Ajustes de diseño responsivo para diferentes tamaños de pantalla y tabletas.
3. **Navegación y Botones Sociales:**
   - Cambiar el número de WhatsApp de contacto en `#app-nav`.
   - Actualizar el canal de YouTube, enlaces de Facebook o redes sociales.
   - Añadir nuevos botones de enlaces o patrocinadores en la barra `#app-nav`.
4. **Portal Exclusivo y Descargas:**
   - Nuevos álbumes o archivos ZIP en el portal exclusivo.
   - Cambios de reglas o formularios en `https://cd-trckas-plataforma.vercel.app/exclusive`.
5. **Videos de YouTube:**
   - Asignar nuevos IDs de video en el diccionario `V` de `index.html`.

---

## 3. Lo que TODAVÍA Requiere Compilar una Nueva APK Android

1. **Permisos del Sistema:**
   - Solicitud de nuevos permisos que requieran declaración en `AndroidManifest.xml` (por ejemplo, Bluetooth, Notificaciones push nativas FCM en Android 13+, Ubicación).
2. **Capacidades Nativas del Contenedor:**
   - Modificaciones en el comportamiento de `DownloadManager` o guardado en carpetas privadas.
   - Integración con SDKs nativos de Android que no funcionen vía WebView estándar.
3. **Seguridad y Dominios Base:**
   - Cambios en los dominios permitidos (`ALLOWED_HOSTS`) en `MainActivity.kt`.
   - Modificaciones en el User-Agent o políticas de cookies de la WebView.
4. **Ciclo de Vida y Requisitos de Plataforma:**
   - Actualización de `minSdkVersion` o `targetSdkVersion` para cumplir con las políticas de Google Play / Android OS.
   - Modificación del icono del lanzador (`ic_launcher`) del sistema operativo.

---

## 4. Guía Rápida para Publicar un Cambio Web

1. Editar los archivos correspondientes en `klmr-singles` (`index.html`, `covers/`, etc.).
2. Hacer commit y push a la rama `main`:
   ```bash
   git add .
   git commit -m "feat: nuevo single y actualización de portada"
   git push origin main
   ```
3. GitHub Pages publicará el cambio automáticamente en `https://djklmr2025.github.io/klmr-singles/`.
4. Al abrir la APK en el teléfono, el nuevo contenido aparecerá de forma inmediata.
