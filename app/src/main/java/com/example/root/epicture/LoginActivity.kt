package com.example.root.epicture

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.util.LogPrinter
import android.view.View
import android.webkit.WebView
import android.webkit.WebSettings
import android.webkit.WebViewClient
import java.io.Console
import java.util.logging.Logger


class Login : AppCompatActivity() {

    var imgurWebView: WebView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        imgurWebView = findViewById(R.id.LoginWebView);
        imgurWebView!!.setBackgroundColor(Color.TRANSPARENT);
        val settings = imgurWebView!!.getSettings()
        settings.setSupportMultipleWindows(true);
        imgurWebView!!.loadUrl("https://api.imgur.com/oauth2/authorize?client_id="+UserObject.clientId+"&response_type=token&state=APPLICATION_STATE");
        imgurWebView!!.getSettings().setJavaScriptEnabled(true);
        imgurWebView!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url!!.contains("https://org.barroso.epicture/")) {
                    splitUrl(url, view!!)
                    val mainActivity = Intent(this@Login, MainActivity::class.java)
                    startActivity(mainActivity)
                    finish()
                } else {
                    imgurWebView!!.loadUrl("https://api.imgur.com/oauth2/authorize?client_id="+UserObject.clientId+"&response_type=token&state=APPLICATION_STATE")
                }
                return true
            }
        }
    }

    fun login(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent);
    }


    fun splitUrl(url: String, view: WebView) {
        var outerSplit = url.split ("#")[1].split("&");

        var index = 0;
        for (s: String in outerSplit) {
            var innerSplit = s.split ("=");
            when(index) {
                0 -> {
                    UserObject.token = innerSplit[1];
                }
                3 -> {
                    UserObject.refreshToken = innerSplit[1];
                }
                4 -> {
                    UserObject.username = innerSplit[1]

                }
            }
            index++;
        }
    }
}
