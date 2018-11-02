package com.example.root.epicture

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.root.epicture.models.CreateImageFromJson
import com.example.root.epicture.models.Image
import okhttp3.*
import okhttp3.MultipartBody
import org.json.JSONObject
import java.io.*


class UploadFragment : Fragment() {


    var _progressBar: ProgressBar? = null
    var _btnBrowse: Button? = null
    var _btnSend: Button? = null
    var _imageView: ImageView? = null
    var _editTitle: EditText? = null
    var _editDesc: EditText? = null
    var _text: TextView? = null
    var _selectedImage: Bitmap? = null
    val _client = OkHttpClient()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_upload, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            try {
                var imageUri: Uri = data!!.getData();
                var imageStream: InputStream = context!!.getContentResolver().openInputStream(imageUri);
                _selectedImage = BitmapFactory.decodeStream(imageStream);
                _imageView!!.setImageBitmap(_selectedImage);
            } catch (e: FileNotFoundException) {
                e.printStackTrace();
                Toast.makeText(this@UploadFragment.context, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this@UploadFragment.context, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _btnBrowse = view.findViewById(R.id.upload_browse)
        _btnSend = view.findViewById(R.id.upload_btn_upload)
        _imageView = view.findViewById(R.id.upload_imageView)
        _editTitle = view.findViewById(R.id.upload_title_edit)
        _editDesc = view.findViewById(R.id.upload_desc_edit)
        _progressBar = view.findViewById(R.id.upload_pb)

        _btnBrowse!!.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, 1001)

        }
        _btnSend!!.setOnClickListener {
            _btnSend!!.visibility = View.GONE
            _progressBar!!.visibility = View.VISIBLE;
            upload()
        }

    }

    fun encodeTobase64(image: Bitmap): String {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }


    fun upload() {

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("title", _editTitle!!.text.toString())
            .addFormDataPart("description", _editDesc!!.text.toString())
            .addFormDataPart("image", encodeTobase64(_selectedImage!!))
            .build()


        val url = "https://api.imgur.com/3/image"
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Authorization","Client-ID " + UserObject.clientId)
            .header("Authorization","Bearer " + UserObject.token)
            .build();

        _client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val Jobject = JSONObject(response.body()?.string())
                val Jdata = Jobject.getJSONObject("data");

                activity!!.runOnUiThread {
                    _progressBar!!.visibility = View.GONE;
                    _btnSend!!.visibility = View.VISIBLE
                }


                val detailIntent: Intent = Intent(this@UploadFragment.context, DetailActivity::class.java)
                val image = CreateImageFromJson(Jdata)
                detailIntent.putExtra("image", image as Serializable)
                startActivity(detailIntent)
            }
        })
    }
}
