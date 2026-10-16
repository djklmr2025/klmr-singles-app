# Auditoría Técnica Inicial — KLMR Singles (Android & WebView)
**Fecha de auditoría:** 15 de octubre de 2026  
**Proyecto:** KLMR Singles — Transformación WebView Full  
**Identificador de aplicación:** `com.klmr.singles`  
**Auditor:** Antigravity / ARKAIOS AI Principal Security Engineer  

---

## 1. Resumen Ejecutivo
Se realizó una inspección forense y técnica exhaustiva de los repositorios `klmr-singles` (sitio web), `klmr-singles-app` (aplicación Android) y de los artefactos APK originales localizados en el equipo local.

La aplicación Android original actuaba como un híbrido incompleto: mantenía una barra de navegación inferior dibujada manualmente en Kotlin (Canvas) y una WebView que cargaba el sitio web, pero con un listener de descargas completamente deshabilitado (`setDownloadListener { _, _, _, _, _ -> }`). Asimismo, el sitio web contenía un mensaje obsoleto ("Las descargas estarán disponibles muy pronto.") en lugar de enlazar al portal exclusivo real alojado en `https://cd-trckas-plataforma.vercel.app/exclusive`.

Esta auditoría establece las bases técnicas, evidencias criptográficas y hoja de ruta para la migración a **WebView Full, Web-First**.

---

## 2. Inventario y Verificación Criptográfica de APKs Originales

Se auditaron y compararon las APKs disponibles en el sistema local utilizando `Get-FileHash`, `aapt` y `apksigner`.

| Artefacto APK | Ubicación Local | Tamaño (Bytes) | Hash SHA-256 |
| :--- | :--- | :--- | :--- |
| **KLMR-Singles.apk** (v1.0 original) | `C:\ARKAIOS\KLMR-Singles.apk` | 905,315 bytes | `36B02B86E4E9D8A1EAE58E20A63DCD3FBDCDC606ECE437A3DF77A0040B83534B` |
| **KLMR_Singlescom.klmr.singlesv1.0.apk** | `C:\Users\Arkaios\Desktop\` | 905,315 bytes | `36B02B86E4E9D8A1EAE58E20A63DCD3FBDCDC606ECE437A3DF77A0040B83534B` |
| **dist/KLMR-Singles.apk** (copia repo) | `C:\ARKAIOS\klmr-singles-app\dist\` | 905,315 bytes | `36B02B86E4E9D8A1EAE58E20A63DCD3FBDCDC606ECE437A3DF77A0040B83534B` |
| **dist/app-release.apk** (v1.1 repo) | `C:\ARKAIOS\klmr-singles-app\dist\` | 902,494 bytes | `CE315F2C6E585CE876382D755150DB640D933ADB512622033B14BD5AD1A34331` |

> **Hallazgo 1:** Se comprobó mediante verificación SHA-256 bit a bit que `KLMR-Singles.apk` y `KLMR_Singlescom.klmr.singlesv1.0.apk` son **absolutamente idénticos**.

---

## 3. Metadatos de la Aplicación Android

Extracción realizada mediante `aapt dump badging` y `apkanalyzer apk summary`:

* **Application ID / Package Name:** `com.klmr.singles`
* **Version Code:** `1` (v1.0 original) / `2` (v1.1 en dist)
* **Version Name:** `1.0` (v1.0 original) / `1.1` (v1.1 en dist)
* **minSdkVersion:** `21` (Android 5.0 Lollipop)
* **targetSdkVersion:** `34` (Android 14)
* **compileSdkVersion:** `34`
* **Clase Actividad Principal:** `com.klmr.singles.MainActivity`
* **Permisos del Sistema:** `android.permission.INTERNET`
* **Orientación declarada:** Vertical fija (`android.hardware.screen.portrait`)

---

## 4. Auditoría de Certificados y Firmas Digitales

Se utilizó `apksigner verify --verbose --print-certs` y `keytool -list -v` sobre los binarios y almacenes de claves:

### Certificado APK Original v1.0 (`KLMR-Singles.apk`)
* **Firmante (Distinguished Name):** `CN=djklmr, OU=klmr, O=klmr, L=klmr, ST=klmr, C=MX`
* **Algoritmo de clave:** RSA 2048 bits
* **Algoritmo de firma:** SHA256withRSA
* **Huella SHA-256 Certificado:** `14ef3d39e1f2705247323a8253fb38ca31f8b2c348feb75c59f6b30753ae22c5`
* **Huella SHA-1 Certificado:** `2d3d14111d3e038353eb593c5743900ba74c636c`
* **Esquemas de firma activos:** Scheme v1 (JAR signing) + Scheme v2 (Full APK Signature)

### Certificado APK v1.1 (`dist/app-release.apk`)
* **Firmante (Distinguished Name):** `CN=KLMR, OU=KLMR, O=KLMR, L=City, ST=State, C=US`
* **Algoritmo de clave:** RSA 2048 bits
* **Huella SHA-256 Certificado:** `4a022398197fd2cfafd2cc32b1e95f02363bfb17005b2bba2b08eaf422e192f7`
* **Almacén asociado:** Coincide con `klmr-v2.p12` (alias `klmr`, contraseña `EDmLWbQiVa782pwP4kj3`).

### Estado de Compatibilidad de Instalación
* En Android, si un usuario tiene instalada una aplicación firmada con un certificado A (`14ef3d...`) y se intenta instalar una actualización firmada con un certificado B (`4a0223...`), el instalador de Android arroja `INSTALL_FAILED_UPDATE_INCOMPATIBLE`.
* **Regla estricta:** Para mantener compatibilidad con las instalaciones de la v1.0 original, la APK de producción debe generarse a través del pipeline de GitHub Actions oficial que resguarda la clave `secrets.KS_B64` y `secrets.KS_PASS` original de v1.

---

## 5. Auditoría de Código y Arquitectura Previa

### 5.1 En la Aplicación Android (`MainActivity.kt`)
1. **Duplicación de Interfaz:**
   - Una barra nativa inferior (`bar = LinearLayout`) ocupaba espacio en pantalla con 6 botones dibujados en Canvas (`Ico`): Facebook, Doxer Music, YouTube, WhatsApp, DJ KLMR y Descargas.
   - Cualquier cambio estético, enlace o nuevo botón requería modificar Kotlin y compilar una nueva APK.
2. **Descargas Nulas:**
   - La implementación de descargas en `setupWeb()` era:
     ```kotlin
     web.setDownloadListener { _, _, _, _, _ -> }
     ```
     El callback no hacía absolutamente nada; cualquier enlace de descarga directa dentro de la WebView era ignorado.
3. **Manejo de Enlaces Externos:**
   - WhatsApp y YouTube no contaban con verificación de intents ni selectores seguros, abriendo únicamente mediante `ACTION_VIEW` genérico sin control de fallos robusto.

### 5.2 En el Sitio Web (`klmr-singles/index.html`)
1. **Mensaje Obsoleto de Descargas:**
   - El código cliente mostraba:
     ```javascript
     if (auth) gate(); else show(h("p", { class: "mu" }, "Las descargas estarán disponibles muy pronto."));
     ```
   - El botón `#descargas` mostraba esta leyenda engañosa cuando la plataforma oficial de acceso por código y descarga exclusiva ya existe en producción.
2. **Portal Exclusivo Real:**
   - El portal oficial y funcional es `https://cd-trckas-plataforma.vercel.app/exclusive`.
   - Implementa inicio de sesión seguro con Firebase Auth y validación server-side contra `/api/exclusive-access`.
   - No expone códigos maestros en HTML, JavaScript ni APK.

---

## 6. Plan de Transformación WebView Full

| Componente | Antes (Híbrido Incompleto) | Ahora (WebView Full Container) |
| :--- | :--- | :--- |
| **Navegación y Botones** | Duplicados en Kotlin (Canvas) | Administrados 100% en la Web (`index.html`) |
| **Contenedor Android** | Actividad con Layout vertical partido | WebView a pantalla completa (`MATCH_PARENT`) |
| **Descargas** | Listener vacío (no-op) | `DownloadManager` nativo con soporte HTTPS, cookies y MIME types |
| **Portal de Descargas** | Texto "muy pronto" | Redirección directa y segura a `/exclusive` |
| **Actualizaciones** | Recompilar APK para enlaces/botones | Automáticas e instantáneas desde la web |
| **Seguridad** | Sin credenciales en APK | 0 credenciales/secretos incrustados |

---

## 7. Conclusión de la Auditoría
La aplicación KLMR Singles califica perfectamente para la arquitectura **WebView Full, Web-First**. Las modificaciones necesarias se encuentran delimitadas:
1. Eliminar la barra inferior en Kotlin y dotar a la WebView de pantalla completa.
2. Implementar `DownloadListener` conectado al `DownloadManager` de Android.
3. Migrar la barra de botones y navegación a la interfaz web con diseño visual idéntico y responsive.
4. Conectar el botón de descargas al portal exclusivo real `https://cd-trckas-plataforma.vercel.app/exclusive`.
5. Incrementar `versionCode` a 3 y documentar compatibilidad.
