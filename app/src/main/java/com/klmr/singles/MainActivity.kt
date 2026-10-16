package com.klmr.singles

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

private const val HOME = "https://djklmr2025.github.io/klmr-singles/"
private val ALLOWED_HOSTS = setOf(
    "djklmr2025.github.io",
    "cd-trckas-plataforma.vercel.app",
    "djklmr.pro",
    "www.djklmr.pro"
)

private const val OFFLINE_HTML = """<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
body { background: #07060f; color: #f4f3ff; font-family: system-ui, sans-serif; text-align: center; padding: 48px 20px; }
h2 { color: #38e8ff; margin-bottom: 12px; font-size: 24px; }
p { color: #9a98c0; font-size: 15px; margin-bottom: 24px; }
.btn { display: inline-block; background: linear-gradient(135deg, #38e8ff, #ff3df0); color: #07060f; font-weight: bold; text-decoration: none; padding: 12px 28px; border-radius: 99px; font-size: 16px; }
</style>
</head>
<body>
<h2>Sin conexión a internet</h2>
<p>Verifica tu conexión y presiona reintentar para continuar escuchando KLMR Singles.</p>
<a class="btn" href="$HOME">Reintentar</a>
</body>
</html>"""

class MainActivity : Activity() {
    private lateinit var web: WebView

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        web = WebView(this)
        setupWebView()
        setContentView(web)
        if (savedInstanceState == null) {
            web.loadUrl(HOME)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val s = web.settings
        s.javaScriptEnabled = true
        s.domStorageEnabled = true
        s.databaseEnabled = true
        s.mediaPlaybackRequiresUserGesture = false
        s.allowFileAccess = false
        s.allowContentAccess = false
        s.setSupportZoom(false)
        s.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        s.userAgentString = "${s.userAgentString} KLMRApp/1.2 (Android WebView Full)"

        web.setBackgroundColor(0xFF07060F.toInt())
        web.isLongClickable = false
        web.setOnLongClickListener { true }

        // DownloadManager nativo de Android para descargas seguras (archivos ZIP, música, etc.)
        web.setDownloadListener { url, userAgent, contentDisposition, mimetype, _ ->
            handleDownload(url, userAgent, contentDisposition, mimetype)
        }

        web.webChromeClient = object : WebChromeClient() {}

        web.webViewClient = object : WebViewClient() {
            private fun shouldRouteExternal(uri: Uri): Boolean {
                val scheme = uri.scheme?.lowercase() ?: return false
                val host = uri.host?.lowercase().orEmpty()

                // Esquemas especiales nativos (WhatsApp, tel, mailto, etc.)
                if (scheme == "whatsapp" || scheme == "tel" || scheme == "mailto" || scheme == "sms") {
                    openExternal(uri)
                    return true
                }

                if (scheme == "https" || scheme == "http") {
                    // Navegación interna en los dominios del ecosistema KLMR
                    if (host in ALLOWED_HOSTS) {
                        return false
                    }
                    // Enlaces externos (Facebook, YouTube, WhatsApp web, etc.)
                    openExternal(uri)
                    return true
                }

                // Esquemas desconocidos se abren vía Intent
                openExternal(uri)
                return true
            }

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                return if (!request.isForMainFrame) {
                    false
                } else {
                    shouldRouteExternal(request.url)
                }
            }

            @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return shouldRouteExternal(Uri.parse(url))
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                if (request.isForMainFrame) {
                    view.loadDataWithBaseURL(null, OFFLINE_HTML, "text/html", "utf-8", null)
                }
            }

            @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
            override fun onReceivedError(view: WebView, errorCode: Int, description: String?, failingUrl: String?) {
                if (failingUrl != null && failingUrl == view.url) {
                    view.loadDataWithBaseURL(null, OFFLINE_HTML, "text/html", "utf-8", null)
                }
            }
        }
    }

    private fun handleDownload(url: String, userAgent: String?, contentDisposition: String?, mimetype: String?) {
        try {
            val uri = Uri.parse(url)
            val request = DownloadManager.Request(uri).apply {
                val filename = URLUtil.guessFileName(url, contentDisposition, mimetype)
                val cookie = CookieManager.getInstance().getCookie(url)
                if (!cookie.isNullOrEmpty()) {
                    addRequestHeader("Cookie", cookie)
                }
                if (!userAgent.isNullOrEmpty()) {
                    addRequestHeader("User-Agent", userAgent)
                }
                if (!mimetype.isNullOrEmpty()) {
                    setMimeType(mimetype)
                }
                setDescription("Descargando archivo desde KLMR...")
                setTitle(filename)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
            }

            val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
            toast("Iniciando descarga en el gestor de Android...")
        } catch (e: Exception) {
            openExternal(Uri.parse(url))
        }
    }

    private fun openExternal(uri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Exception) {
            toast("No se pudo abrir el enlace")
        }
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onBackPressed() {
        if (web.canGoBack()) {
            web.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        web.destroy()
        super.onDestroy()
    }
}
