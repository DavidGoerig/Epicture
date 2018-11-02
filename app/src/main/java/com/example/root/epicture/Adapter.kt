package com.example.root.epicture

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import com.example.root.epicture.models.Image
import com.squareup.picasso.Picasso

class Adapter(var _context: Context, var _images: ArrayList<Image>) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    var _imageView: ImageView? = null
    var _textViewAuthor: TextView? = null
    var _textViewName: TextView? = null
    var _textViewDesc: TextView? = null
    var _listener: onItemClickListener? = null

    interface onItemClickListener {
        fun onItemClick(position: Number);
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        _listener = listener
    }

    override fun getItemCount(): Int {
        return _images.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var currentImage: Image = _images[position]

        var url: String = currentImage.imageUrl

        holder.getTextViewAuthor()!!.text = currentImage.author
        holder.getTextViewName()!!.text = currentImage.name
        Picasso.get().load(url).fit().centerInside().into(holder.getImageView())
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        var v: View = LayoutInflater.from(_context).inflate(R.layout.recycler_item, p0, false)
        return ViewHolder(v)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        init {
            _imageView = itemView.findViewById(R.id.imageView)
            _textViewAuthor = itemView.findViewById(R.id.textViewAuthor)
            _textViewName = itemView.findViewById(R.id.textViewName)

            itemView.setOnClickListener {
                if (_listener != null) {
                    var position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        _listener!!.onItemClick(position)
                    }
                }
            }
        }

        fun getTextViewDesc(): TextView? {
            return _textViewDesc
        }

        fun getImageView(): ImageView? {
            return _imageView
        }

        fun getTextViewName(): TextView? {
            return _textViewName
        }

        fun getTextViewAuthor(): TextView? {
            return _textViewAuthor
        }
    }
}