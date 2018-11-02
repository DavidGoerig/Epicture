package com.example.root.epicture.models

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject
import java.io.Serializable


class Image(val imageUrl: String, val author: String, val name: String, val deleteHash: String, val description: String): Serializable
{
}

public fun CreateImageFromJson(json: JSONObject): Image {
    var name = json.getString("title")
    var author = json.getString("account_url")
    var imageUrl = json.getString("link")
    var description = json.getString("description")
    var deleteHash = json.getString("deletehash")
    return Image(imageUrl, author, name, deleteHash, description)
}
