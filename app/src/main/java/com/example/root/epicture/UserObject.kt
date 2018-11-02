package com.example.root.epicture

import android.app.DownloadManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.root.epicture.R.id.lblImgurUsername
import com.example.root.epicture.models.IProfile
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import okhttp3.*
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import android.view.ViewGroup
import android.view.LayoutInflater



class ImgurProfile(val accountUrl: String, val email: String, val avatar: String, val cover: String) {

}

object UserObject: AppCompatActivity() {
    val clientId: String = "8a3f7a5c623a3b9"

    var username: String? = null
    var token: String? = null
    var refreshToken: String? = null
    var profile: ImgurProfile? = null
    val CONNECTON_TIMEOUT_MILLISECONDS = 60000

    val client = OkHttpClient()


    fun test() {
    }


}

