package ru.donntu.admission

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.view.MotionEvent
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.net.URI


class MainActivity : AppCompatActivity()
{
    private lateinit var webView: WebView
    var cancelTimer = false

    var filePath: ValueCallback<Array<Uri>>? = null

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        val btnReload: FloatingActionButton = findViewById(R.id.BTN_reload)
        btnReload.setOnTouchListener { view, motion_event ->
            val timer = object : CountDownTimer(1000, 100)
            {
                override fun onTick(millisUntilFinished: Long)
                {
                    if (cancelTimer) cancel()
                    else view.alpha -= 0.1f
                }
                override fun onFinish()
                {
                    view.alpha = 1f
                    view.background.setTint(getColor(R.color.yellow))
                    webView.reload()
                }
            }

            if (motion_event.action == MotionEvent.ACTION_DOWN)
            {
                cancelTimer = false
                timer.start()
            }
            if (motion_event.action == MotionEvent.ACTION_UP)
            {
                cancelTimer = true
                view.alpha = 1f
                view.background.setTint(getColor(R.color.teal_900))
            }
            true
        }

        webView = findViewById(R.id.webView)
        webView.setOnLongClickListener {
            val fileName = File(URI(webView.url).path).name
            if (fileName.contains("."))
                Snackbar.make(webView, fileName, Snackbar.LENGTH_LONG)
                        .setAction("Скачать") {
                            val request = DownloadManager.Request(Uri.parse(webView.url))
                                .setTitle(fileName)
                                .setDescription("Скачивание...")
                                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                .setAllowedOverMetered(true)
                            dm.enqueue(request)
                        }.show()
            return@setOnLongClickListener true
        }

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient()
        {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean
            {
                if (url != null) view?.loadUrl(url)
                return true
            }
            override fun shouldOverrideUrlLoading(view: WebView,
                                                  request: WebResourceRequest): Boolean
            {
                view.loadUrl(request.url.toString())
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?)
            {
                super.onPageStarted(view, url, favicon)
                if (view != null) Snackbar.make(view, "Загрузка...", Snackbar.LENGTH_SHORT).show()
            }
            override fun onPageFinished(view: WebView?, url: String?)
            {
                super.onPageFinished(view, url)
                if (view != null) Snackbar.make(view, "✔", Snackbar.LENGTH_SHORT).show()
            }
        }

        webView.setDownloadListener { url, _, contentDisposition, mimetype, _ -> // '_' => userAgent ... contentLength
            val fileName = URLUtil.guessFileName(url, contentDisposition, mimetype)
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(fileName)
                .setDescription("Скачивание...")
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
            dm.enqueue(request)

            //To notify the Client that the file is being downloaded
            Snackbar.make(webView, "$fileName загружается", Snackbar.LENGTH_SHORT).show()
        }

        webView.webChromeClient = object : WebChromeClient()
        {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean
            {
                filePath = filePathCallback

                val contentIntent = Intent(Intent.ACTION_GET_CONTENT)
                contentIntent.type = "*/*"
                contentIntent.addCategory(Intent.CATEGORY_OPENABLE)

                startActivityForResult(contentIntent, 1)
                return true
            }
        }

        val targetUrl = "http://pk2022.donntu.ru/" // "https://vk.com"
        webView.loadUrl(targetUrl)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack()
        else super.onBackPressed()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_CANCELED)
        {
            filePath?.onReceiveValue(null)
            return
        }
        else if (resultCode == Activity.RESULT_OK)
        {
            if (filePath == null) return
            filePath!!.onReceiveValue(WebChromeClient.FileChooserParams
                                                     .parseResult(resultCode, data))
            filePath = null
        }
    }
}
