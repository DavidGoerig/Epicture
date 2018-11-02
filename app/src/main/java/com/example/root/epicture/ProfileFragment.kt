package com.example.root.epicture

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.root.epicture.models.CreateImageFromJson
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.io.Serializable

class ProfileFragment : Fragment(), Adapter.onItemClickListener {
    var swipeContainer: SwipeRefreshLayout? = null


    var _avatarImageview: ImageView? = null
    var _recyclerView: RecyclerView? = null
    var _adapter: Adapter? = null
    var _images = ArrayList<com.example.root.epicture.models.Image>()
    val _client = OkHttpClient()

    override fun onItemClick(position: Number) {
        val detailIntent: Intent = Intent(this@ProfileFragment.context, DetailActivity::class.java)
        val image = _images[position.toInt()]
        detailIntent.putExtra("image", image as Serializable)
        startActivity(detailIntent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeContainer = view!!.findViewById(R.id.swipeContainer) as SwipeRefreshLayout

        swipeContainer!!.setOnRefreshListener {
            getProfilePictures()
        };
        swipeContainer!!.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);

        _recyclerView = view.findViewById(R.id.reciclerView)
        _recyclerView!!.setHasFixedSize(true)
        _recyclerView!!.setLayoutManager(LinearLayoutManager(this.context))

        getProfilePictures()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }


    fun getProfilePictures() {


        _images.clear()

        val url = "https://api.imgur.com/3/account/me/images"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer " + UserObject.token)
            .build()

        _client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val Jobject = JSONObject(response.body()?.string())
                val Jdata = Jobject.getJSONArray("data");
                for (i in 0..Jdata.length() - 1) {
                    var image = Jdata.getJSONObject(i)

                    _images.add(CreateImageFromJson(image))
                }
                _adapter = Adapter(this@ProfileFragment.context!!, _images)
                activity!!.runOnUiThread {
                    swipeContainer!!.setRefreshing(false);
                    _recyclerView!!.adapter = _adapter
                    _adapter!!.setOnItemClickListener(this@ProfileFragment)
                }
            }
        })
    }
}
