# KLMR Singles (app Android)

WebView portrait-only que muestra https://djklmr2025.github.io/klmr-singles/ con botones: Facebook, Doxer Music, YouTube, WhatsApp, DJ y Descargas (bloqueadas con codigo).

## Compilar en tu PC
1. Instala Android Studio (o JDK 17 + Android SDK 34 + Gradle 8.9).
2. Abre esta carpeta en Android Studio y elige Build > Build APK(s), o en terminal: `gradle assembleDebug`.
3. El APK queda en `app/build/outputs/apk/debug/`.

## Habilitar las descargas
`python3 tools/make_unlock.py "CODIGO" "https://enlace-de-descarga.com/..."` genera `app/src/main/assets/unlock.json` (enlace cifrado). Recompila despues.

## Compilar en GitHub Actions
Ya incluido en `.github/workflows/build.yml` (requiere los secretos KS_B64 y KS_PASS del keystore y que la cuenta pueda usar Actions).

