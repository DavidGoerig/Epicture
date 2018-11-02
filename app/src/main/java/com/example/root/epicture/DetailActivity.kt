package com.example.root.epicture

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.root.epicture.models.Image
import com.squareup.picasso.Picasso
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.net.Uri
import android.widget.Toast


class DetailActivity : AppCompatActivity() {

    var deleteHash: String? = null
    var image: Image? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        var intent: Intent = getIntent()

        image = intent.extras.get("image") as Image


        var imageView: ImageView = findViewById(R.id.imageViewDetail)
        var textViewName: TextView = findViewById(R.id.textViewDetailName)
        var textViewAuthor: TextView = findViewById(R.id.textViewDetailAuthor)
        var textViewDesc: TextView = findViewById(R.id.textViewDetailDesc)

        Picasso.get().load(image!!.imageUrl).into(imageView)
        textViewName.text = image!!.name
        textViewAuthor.text = image!!.author
        textViewDesc.text = image!!.description
        deleteHash = image!!.deleteHash
    }

    fun copyUrl(view: View) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("url", image!!.imageUrl);
        clipboard.primaryClip = clip
        Toast.makeText(getApplicationContext(), "Link Copied",
            Toast.LENGTH_SHORT).show();
    }

    fun deleteImage(view: View) {
        val url = "https://api.imgur.com/3/account/" + UserObject.username + "/image/" + deleteHash
        val request = Request.Builder()
            .url(url)
            .delete()
            .addHeader("Authorization", "Bearer " + UserObject.token)
            .build()

        UserObject.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val mainIntent: Intent = Intent(this@DetailActivity, MainActivity::class.java)
                startActivity(mainIntent)
            }
        })
    }


}
