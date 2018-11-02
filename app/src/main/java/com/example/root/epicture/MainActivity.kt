package com.example.root.epicture

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import android.widget.TextView
import java.io.IOException
import org.json.JSONArray
import org.json.JSONObject
import android.R.id.edit
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.media.Image
import android.support.design.shape.RoundedCornerTreatment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.method.TextKeyListener.clear
import android.widget.AdapterView
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import okhttp3.*
import java.io.Serializable

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, Adapter.onItemClickListener {


    var _avatarImageview: ImageView? = null
    var _recyclerView: RecyclerView? = null
    var _adapter: Adapter? = null
    var _images = ArrayList<com.example.root.epicture.models.Image>()
    val _client = OkHttpClient()


    override fun onItemClick(position: Number) {
        val detailIntent: Intent = Intent(this@MainActivity, DetailActivity::class.java)
        val image = _images[position.toInt()]
        detailIntent.putExtra("image", image as Serializable)
        startActivity(detailIntent)
    }

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        fab.setOnClickListener { view ->
            var fragmentManger: FragmentManager = supportFragmentManager
            var fragmentTransaction: FragmentTransaction = fragmentManger.beginTransaction()

            fragmentTransaction.replace(R.id.screen_area, UploadFragment())
            fragmentTransaction.commit()
        }

        nav_view.setNavigationItemSelectedListener(this)

        var fragmentManger: FragmentManager = supportFragmentManager
        var fragmentTransaction: FragmentTransaction = fragmentManger.beginTransaction()

        fragmentTransaction.replace(R.id.screen_area, ProfileFragment())
        fragmentTransaction.commit()

        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val headerView = navigationView.getHeaderView(0)
        val navUsername = headerView.findViewById(R.id.lblImgurUsername) as TextView
        val navEmail = headerView.findViewById(R.id.lblImgurEmail) as TextView
        navUsername.text = UserObject.username
        navEmail.text = UserObject.username
        getProfileSettings()
        Log.e("Access Token ", UserObject.token);

        _avatarImageview = headerView.findViewById(R.id.imageViewAvatar)
        getProfile()
    }


    override fun onBackPressed() {

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        var fragment: Fragment? = null
        when (item.itemId) {
            R.id.nav_profile -> {
                fragment = ProfileFragment()
            }
            R.id.nav_trending -> {
                fragment = TrendingFragment()
            }
            R.id.nav_search -> {
                fragment = SearchFragment()
            }
            R.id.nav_upload -> {
                fragment = UploadFragment()
            }
            R.id.nav_disconnect -> {
            }
        }
        if (fragment != null) {
            var fragmentManger: FragmentManager = supportFragmentManager
            var fragmentTransaction: FragmentTransaction = fragmentManger.beginTransaction()

            fragmentTransaction.replace(R.id.screen_area, fragment)
            fragmentTransaction.commit()
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun getCats() {
        val url = "https://pixabay.com/api/?key=5303976-fd6581ad4ac165d1b75cc15b3&q=kitten&image_type=photo&pretty=true"
        val request = Request.Builder()
            .url(url)
            .build()

        _client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val Jobject = JSONObject(response.body()?.string())
                val Jdata = Jobject.getJSONArray("hits");
                for (i in 0..Jdata.length() - 1) {
                    var image = Jdata.getJSONObject(i)

                    var author = image.getString("user")
                    var imageUrl = image.getString("webformatURL")
                }
                _adapter = Adapter(this@MainActivity, _images)
                this@MainActivity.runOnUiThread(Runnable {
                    _recyclerView!!.adapter = _adapter
                })
            }
        })
    }

    fun getProfile() {
        val url = "https://api.imgur.com/3/account/me"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer " + UserObject.token)
            .build()

        _client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val Jobject = JSONObject(response.body()?.string())
                val Jdata = Jobject.getJSONObject("data");
                val avatar = Jdata.getString("avatar");
                this@MainActivity.runOnUiThread {
                    Picasso.get().load(avatar).transform(CircleTransform()).into(_avatarImageview)
                }
            }
        })
    }

    fun getProfileSettings() {
        val url = "https://api.imgur.com/3/account/me/settings"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer " + UserObject.token)
            .build()

        _client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val Jobject = JSONObject(response.body()?.string())
                val Jdata = Jobject.getJSONObject("data");

                val accountUrl = Jdata.getString("account_url")
                val email = Jdata.getString("email");
                val avatar = Jdata.getString("avatar");
                val cover = Jdata.getString("cover");
                UserObject.profile = ImgurProfile(accountUrl, email, avatar, cover)
                lblImgurEmail!!.text = UserObject.profile!!.email
            }
        })
    }


    class CircleTransform : Transformation {
        override fun key(): String {
            return "circle";
        }

        override fun transform(source: Bitmap?): Bitmap {
            var size = Math.min(source!!.getWidth(), source!!.getHeight());

            var x =(source!!.getWidth() - size) / 2;
            var y =(source!!.getHeight() - size) / 2;

            var squaredBitmap: Bitmap = Bitmap . createBitmap (source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            var bitmap: Bitmap = Bitmap . createBitmap (size, size, source.getConfig());

            var canvas: Canvas = Canvas(bitmap);
            var paint: Paint = Paint();
            var shader: BitmapShader = BitmapShader(
                squaredBitmap,
                Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP
            );
            paint.setShader(shader);
            paint.setAntiAlias(true);

            var r = size /2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }


    }
}
