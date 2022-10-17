package ru.donntu.admission

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity()
{
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val backButton : FloatingActionButton = findViewById(R.id.BTN_back)
        backButton.setOnClickListener { if (webView.canGoBack()) webView.goBack() }
        backButton.setOnLongClickListener { webView.reload(); return@setOnLongClickListener true; }

        webView = findViewById(R.id.webView)
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

            override fun onPageFinished(view: WebView?, url: String?)
            {
                super.onPageFinished(view, url)
                if (view != null)
                    Snackbar.make(view, "Страница загружена!", Snackbar.LENGTH_SHORT).show()
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?)
            {
                super.onPageStarted(view, url, favicon)
                if (view != null)
                    Snackbar.make(view, "Начата загрузка страницы", Snackbar.LENGTH_SHORT).show()
            }

            override fun onUnhandledKeyEvent(view: WebView?, event: KeyEvent?)
            {
                super.onUnhandledKeyEvent(view, event)
            }
        }
        webView.loadUrl("http://pk2022.donntu.ru/")
    }
}