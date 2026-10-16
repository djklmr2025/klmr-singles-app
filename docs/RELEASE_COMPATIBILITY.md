# Informe de Compatibilidad de Firma y Actualización — KLMR Singles
**Documento Técnico:** `docs/RELEASE_COMPATIBILITY.md`  
**Aplicación:** KLMR Singles (`com.klmr.singles`)  
**Fecha:** 15 de octubre de 2026  

---

## 1. Identificador de Paquete (Application ID)
* **Application ID Verificado:** `com.klmr.singles` (100% idéntico en todas las versiones).
* **Compatibilidad de Espacio de Nombres:** Conservado sin cambios para garantizar que el Package Manager de Android reconozca la aplicación como la misma entidad.

---

## 2. Progresión de Versiones

| Versión | Version Code | Version Name | Origen / Commit | Estado |
| :--- | :---: | :---: | :--- | :--- |
| **v1.0** | `1` | `1.0` | `KLMR-Singles.apk` (original) | Producción inicial |
| **v1.1** | `2` | `1.1` | PR #1 (`feature/premium`) | Corrección previa |
| **v1.2** | `3` | `1.2` | `feature/webview-full-architecture` | **WebView Full actual** |

---

## 3. Matriz de Certificados y Firmas Digitales

### 3.1 Certificado Original v1.0 (Producción Inicial)
* **Distinguished Name (DN):** `CN=djklmr, OU=klmr, O=klmr, L=klmr, ST=klmr, C=MX`
* **Huella SHA-256:** `14ef3d39e1f2705247323a8253fb38ca31f8b2c348feb75c59f6b30753ae22c5`
* **Huella SHA-1:** `2d3d14111d3e038353eb593c5743900ba74c636c`
* **Almacenamiento de Clave:** Resguardada de forma segura en GitHub Actions Secret (`secrets.KS_B64`, `secrets.KS_PASS`).

### 3.2 Certificado v2 / Desarrollo Local
* **Distinguished Name (DN):** `CN=KLMR, OU=KLMR, O=KLMR, L=City, ST=State, C=US`
* **Huella SHA-256:** `4a022398197fd2cfafd2cc32b1e95f02363bfb17005b2bba2b08eaf422e192f7`
* **Huella SHA-1:** `d21f71ee24d2e6f8f13709da88211bd2b656490b`
* **Almacenamiento de Clave:** Local `C:\ARKAIOS\klmr-singles-keystore\klmr-v2.p12` (alias `klmr`).

---

## 4. Reglas del Package Manager de Android para Actualizaciones
En el sistema operativo Android:
1. Una actualización de APK se instala sin perder datos (`Preferences`, Caché, descargas) si y solo si:
   - El `applicationId` coincide (`com.klmr.singles`).
   - El `versionCode` es estrictamente mayor al instalado (`versionCode 3 > versionCode 2 > versionCode 1`).
   - El certificado de firma de la nueva APK coincide con el certificado de la APK actualmente instalada en el dispositivo.
2. Si un usuario tiene instalada la versión firmada con el certificado v1 (`14ef3d...`), cualquier intento de instalar un APK firmado con el certificado v2 (`4a0223...`) provocará el error nativo:
   `INSTALL_FAILED_UPDATE_INCOMPATIBLE: Existing package com.klmr.singles signatures do not match newer version`
3. Por esta razón:
   - El artefacto local compilado en esta rama es un **artefacto de prueba/staging** firmado con `klmr-v2.p12`.
   - La **publicación oficial de Release para producción** debe originarse mediante el workflow de GitHub Actions (`.github/workflows/build.yml`) que inyecta el `secrets.KS_B64` de v1, asegurando que los usuarios existentes puedan actualizar la APK directamente sobre la versión previa sin desinstalar.

---

## 5. Verificación de Integridad de Datos de Usuario
La migración a WebView Full conserva:
- Directorio de caché web (`WebViewCache`).
- Almacenamiento local DOM (`localStorage` e `IndexedDB`).
- Cookies y credenciales de sesión en `cd-trckas-plataforma.vercel.app`.
- Permisos de almacenamiento del sistema operativo.
