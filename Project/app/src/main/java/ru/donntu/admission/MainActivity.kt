package ru.donntu.admission

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.net.URI


class MainActivity : AppCompatActivity()
{
    private lateinit var webView: WebView

    var filePath: ValueCallback<Array<Uri>>? = null

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        val backButton: FloatingActionButton = findViewById(R.id.BTN_back)
        backButton.setOnClickListener { if (webView.canGoBack()) webView.goBack() }
        backButton.setOnLongClickListener { webView.reload(); return@setOnLongClickListener true; }

        webView = findViewById(R.id.webView)
        webView.setOnLongClickListener {
            val fileName = File(URI(webView.url).path).name
            if (fileName.contains("."))
                Snackbar.make(webView, "Save As", Snackbar.LENGTH_LONG)
                        .setAction("Save") {
                            val request = DownloadManager.Request(Uri.parse(webView.url))
                                .setTitle(fileName)
                                .setDescription("Downloading...")
                                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                .setAllowedOverMetered(true)
                            dm.enqueue(request) }.show()
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
                if (view != null)
                    Snackbar.make(view, "Начата загрузка страницы", Snackbar.LENGTH_SHORT).show()
            }
            override fun onPageFinished(view: WebView?, url: String?)
            {
                super.onPageFinished(view, url)
                if (view != null)
                    Snackbar.make(view, "Страница загружена!", Snackbar.LENGTH_SHORT).show()
            }
        }

        webView.setDownloadListener { url, _, contentDisposition, mimetype, _ -> // userAgent ... contentLength
            val fileName = URLUtil.guessFileName(url, contentDisposition, mimetype)
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(fileName)
                .setDescription("Downloading...")
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)

            dm.enqueue(request)

            //To notify the Client that the file is being downloaded
            Snackbar.make(webView, "Downloading File", Snackbar.LENGTH_SHORT).show()
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

        val targetUrl = "http://pk2022.donntu.ru/"
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
