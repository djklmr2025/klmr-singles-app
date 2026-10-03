package com.klmr.singles

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Base64
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

private const val HOME = "https://djklmr2025.github.io/klmr-singles/"
private const val FB = "https://www.facebook.com/KLMROFFICIALCHANNEL"
private const val DOXER = "https://cd-trckas-plataforma.vercel.app/"
private const val YT = "https://www.youtube.com/@KLMROFFCIALCHANNEL"
private const val WA = "https://wa.me/525516469310"
private const val DJ = "https://djklmr.pro/"
private val ALLOWED = setOf(
    "djklmr2025.github.io", "cd-trckas-plataforma.vercel.app", "djklmr.pro", "www.djklmr.pro"
)
private const val OFFLINE = "<html><body style='background:#07060f;color:#f4f3ff;font-family:sans-serif;" +
    "text-align:center;padding:40px 20px'><h2>Sin conexi\u00f3n</h2><p>Revisa tu internet y toca para reintentar.</p>" +
    "<p><a style='color:#38e8ff;font-size:18px' href='$HOME'>Reintentar</a></p></body></html>"

enum class K { FB, MUSIC, YT, WA, DJ, DL }

class Ico(c: Context, private val kind: K) : View(c) {
    var locked = true
        set(v) { field = v; invalidate() }
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(cv: Canvas) {
        val s = minOf(width, height).toFloat()
        val cx = width / 2f
        val cy = height / 2f
        val r = s / 2f * 0.94f
        p.reset()
        p.isAntiAlias = true
        when (kind) {
            K.FB -> {
                p.color = 0xFF1877F2.toInt(); cv.drawCircle(cx, cy, r, p)
                p.color = Color.WHITE; p.textSize = s * 0.78f
                p.typeface = Typeface.DEFAULT_BOLD; p.textAlign = Paint.Align.CENTER
                cv.drawText("f", cx + s * 0.05f, cy + s * 0.28f, p)
            }
            K.MUSIC -> {
                p.shader = LinearGradient(cx - r, cy - r, cx + r, cy + r, 0xFF7C3AED.toInt(), 0xFFEC4899.toInt(), Shader.TileMode.CLAMP)
                cv.drawCircle(cx, cy, r, p); p.shader = null
                p.color = Color.WHITE; p.textSize = s * 0.7f; p.textAlign = Paint.Align.CENTER
                cv.drawText("\u266A", cx, cy + s * 0.24f, p)
            }
            K.YT -> {
                p.color = 0xFFFF0000.toInt()
                cv.drawRoundRect(RectF(cx - r, cy - r * 0.7f, cx + r, cy + r * 0.7f), r * 0.4f, r * 0.4f, p)
                p.color = Color.WHITE
                val t = Path()
                t.moveTo(cx - r * 0.25f, cy - r * 0.35f); t.lineTo(cx - r * 0.25f, cy + r * 0.35f)
                t.lineTo(cx + r * 0.4f, cy); t.close()
                cv.drawPath(t, p)
            }
            K.WA -> {
                p.color = 0xFF25D366.toInt(); cv.drawCircle(cx, cy, r, p)
                p.color = Color.WHITE; p.style = Paint.Style.STROKE; p.strokeWidth = s * 0.07f
                cv.drawCircle(cx, cy - s * 0.02f, r * 0.58f, p)
                p.style = Paint.Style.FILL
                val t = Path()
                t.moveTo(cx - r * 0.62f, cy + r * 0.72f); t.lineTo(cx - r * 0.52f, cy + r * 0.22f)
                t.lineTo(cx - r * 0.14f, cy + r * 0.52f); t.close()
                cv.drawPath(t, p)
                p.style = Paint.Style.STROKE; p.strokeWidth = s * 0.1f; p.strokeCap = Paint.Cap.ROUND
                cv.drawArc(RectF(cx - s * 0.17f, cy - s * 0.17f, cx + s * 0.17f, cy + s * 0.17f), 95f, 95f, false, p)
            }
            K.DJ -> {
                p.shader = LinearGradient(cx - r, cy, cx + r, cy, 0xFF38E8FF.toInt(), 0xFFFF3DF0.toInt(), Shader.TileMode.CLAMP)
                cv.drawRoundRect(RectF(cx - r, cy - r * 0.62f, cx + r, cy + r * 0.62f), r * 0.35f, r * 0.35f, p)
                p.shader = null
                p.color = 0xFF07060F.toInt(); p.textSize = s * 0.52f
                p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC); p.textAlign = Paint.Align.CENTER
                cv.drawText("DJ", cx, cy + s * 0.18f, p)
            }
            K.DL -> {
                p.color = if (locked) 0xFF4B5563.toInt() else 0xFF16A34A.toInt()
                cv.drawCircle(cx, cy, r, p)
                p.color = Color.WHITE; p.style = Paint.Style.STROKE; p.strokeWidth = s * 0.09f
                p.strokeCap = Paint.Cap.ROUND; p.strokeJoin = Paint.Join.ROUND
                cv.drawLine(cx, cy - s * 0.24f, cx, cy + s * 0.12f, p)
                val a = Path()
                a.moveTo(cx - s * 0.16f, cy - s * 0.04f); a.lineTo(cx, cy + s * 0.14f); a.lineTo(cx + s * 0.16f, cy - s * 0.04f)
                cv.drawPath(a, p)
                cv.drawLine(cx - s * 0.2f, cy + s * 0.27f, cx + s * 0.2f, cy + s * 0.27f, p)
                if (locked) {
                    p.style = Paint.Style.FILL; p.color = 0xFFFFC94D.toInt()
                    val lx = cx + s * 0.2f
                    val ly = cy + s * 0.2f
                    val lw = s * 0.17f
                    cv.drawRoundRect(RectF(lx - lw, ly - lw * 0.2f, lx + lw, ly + lw * 1.25f), 3f, 3f, p)
                    p.style = Paint.Style.STROKE; p.strokeWidth = s * 0.05f
                    cv.drawArc(RectF(lx - lw * 0.65f, ly - lw * 1.1f, lx + lw * 0.65f, ly + lw * 0.5f), 180f, 180f, false, p)
                }
            }
        }
    }
}

class MainActivity : Activity() {
    private lateinit var web: WebView
    private lateinit var dlIco: Ico
    private val prefs by lazy { getSharedPreferences("klmr", MODE_PRIVATE) }

    private fun dp(v: Int) = (v * resources.displayMetrics.density + 0.5f).toInt()
    private fun toast(m: String) = Toast.makeText(this, m, Toast.LENGTH_LONG).show()

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        web = WebView(this)
        setupWeb()
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(0xFF0B0A1C.toInt())
            setPadding(dp(4), 0, dp(4), 0)
        }
        fun add(v: View) = bar.addView(v, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        add(item(Ico(this, K.FB), "Facebook", "Facebook de KLMR") { open(FB) })
        add(item(Ico(this, K.MUSIC), "Doxer Music", "Doxer-Music") { web.loadUrl(DOXER) })
        add(item(Ico(this, K.YT), "YouTube", "Canal de YouTube HD") { open(YT) })
        add(item(Ico(this, K.WA), "Contacto directo", "Contacto directo con el artista por WhatsApp") { open(WA) })
        add(item(Ico(this, K.DJ), "DJ", "Sitio DJ KLMR") { web.loadUrl(DJ) })
        dlIco = Ico(this, K.DL)
        dlIco.locked = prefs.getString("dl", null) == null
        add(item(dlIco, "Descargas", "Descargas protegidas con c\u00f3digo") { onDownload() })

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF07060F.toInt())
        }
        root.addView(web, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
        val line = View(this).apply { setBackgroundColor(0x5538E8FF) }
        root.addView(line, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(1)))
        root.addView(bar, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        setContentView(root)
        if (b == null) web.loadUrl(HOME)
    }

    private fun item(ico: Ico, label: String, desc: String, onClick: () -> Unit): LinearLayout {
        val tv = TextView(this).apply {
            text = label
            setTextColor(0xFFC9C7EE.toInt())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            gravity = Gravity.CENTER
            minLines = 2
            maxLines = 2
        }
        val out = TypedValue()
        theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, out, true)
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            setPadding(dp(2), dp(8), dp(2), dp(6))
            contentDescription = desc
            isClickable = true
            isFocusable = true
            setBackgroundResource(out.resourceId)
            setOnClickListener { onClick() }
            addView(ico, LinearLayout.LayoutParams(dp(30), dp(30)))
            addView(tv, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(3) })
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWeb() {
        val s = web.settings
        s.javaScriptEnabled = true
        s.domStorageEnabled = true
        s.mediaPlaybackRequiresUserGesture = false
        s.allowFileAccess = false
        s.setSupportZoom(false)
        s.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        web.setBackgroundColor(0xFF07060F.toInt())
        web.isLongClickable = false
        web.setOnLongClickListener { true }
        web.setDownloadListener { _, _, _, _, _ -> }
        web.webViewClient = object : WebViewClient() {
            private fun route(u: Uri): Boolean {
                return if (u.scheme == "https" && u.host.orEmpty() in ALLOWED) false else { open(u.toString()); true }
            }
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean =
                if (!request.isForMainFrame) false else route(request.url)
            @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean = route(Uri.parse(url))
            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                if (request.isForMainFrame) view.loadDataWithBaseURL(null, OFFLINE, "text/html", "utf-8", null)
            }
            @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
            override fun onReceivedError(view: WebView, errorCode: Int, description: String?, failingUrl: String?) {
                if (failingUrl != null && failingUrl == view.url) view.loadDataWithBaseURL(null, OFFLINE, "text/html", "utf-8", null)
            }
        }
    }

    private fun open(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: Exception) {
            toast("No se pudo abrir el enlace")
        }
    }

    private fun payload(): JSONObject? = try {
        assets.open("unlock.json").bufferedReader().use { JSONObject(it.readText()) }
    } catch (e: Exception) {
        null
    }

    private fun decrypt(code: String, o: JSONObject): String? = try {
        val salt = Base64.decode(o.getString("s"), Base64.DEFAULT)
        val iv = Base64.decode(o.getString("i"), Base64.DEFAULT)
        val ct = Base64.decode(o.getString("c"), Base64.DEFAULT)
        val key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            .generateSecret(PBEKeySpec(code.trim().toCharArray(), salt, 120000, 256)).encoded
        val c = Cipher.getInstance("AES/GCM/NoPadding")
        c.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(128, iv))
        String(c.doFinal(ct), Charsets.UTF_8)
    } catch (e: Exception) {
        null
    }

    private fun onDownload() {
        val saved = prefs.getString("dl", null)
        if (saved != null) { open(saved); return }
        val pl = payload()
        if (pl == null) { toast("Las descargas estar\u00e1n disponibles muy pronto."); return }
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(8), dp(22), 0)
        }
        val msg = TextView(this).apply {
            text = "Muestra tu llave: inserta el c\u00f3digo para entrar en modo completo y activar las descargas."
            setTextColor(0xFFC9C7EE.toInt())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        }
        val et = EditText(this).apply {
            hint = "C\u00f3digo"
            setTextColor(Color.WHITE)
            setHintTextColor(0xFF8886AA.toInt())
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            isSingleLine = true
        }
        box.addView(msg)
        box.addView(et)
        val d = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            .setTitle("Gracias por entrar al mundo Arkaios")
            .setView(box)
            .setPositiveButton("Desbloquear", null)
            .setNegativeButton("Cancelar", null)
            .create()
        d.show()
        val ok = d.getButton(AlertDialog.BUTTON_POSITIVE)
        ok.setOnClickListener {
            val code = et.text.toString()
            if (code.isBlank()) return@setOnClickListener
            ok.isEnabled = false
            ok.text = "Verificando\u2026"
            Thread {
                val url = decrypt(code, pl)
                runOnUiThread {
                    if (url != null && url.startsWith("https://")) {
                        prefs.edit().putString("dl", url).apply()
                        dlIco.locked = false
                        d.dismiss()
                        toast("\u00a1Bienvenido al mundo Arkaios! Descargas desbloqueadas.")
                    } else {
                        ok.isEnabled = true
                        ok.text = "Desbloquear"
                        et.error = "C\u00f3digo incorrecto"
                    }
                }
            }.start()
        }
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onBackPressed() {
        if (web.canGoBack()) web.goBack() else super.onBackPressed()
    }

    override fun onDestroy() {
        web.destroy()
        super.onDestroy()
    }
}
