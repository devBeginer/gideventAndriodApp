package ru.gidevent.androidapp.ui.edit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ru.gidevent.andriodapp.R
import ru.gidevent.androidapp.data.model.advertisement.PriceRVItem
import ru.gidevent.androidapp.data.model.advertisement.dto.EventTime
import ru.gidevent.androidapp.ui.advertisement.adapter.AdvertReviewRecyclerViewAdapter
import ru.gidevent.androidapp.utils.Utils.IMAGE_URL
import ru.gidevent.androidapp.utils.Utils.toString

class PhotoEditRecyclerViewAdapter(
    private var dataSet: List<String>,
    private val onDelete: (uri: String)->Unit,
    private val onCreate: ()->Unit
): RecyclerView.Adapter<PhotoEditRecyclerViewAdapter.MainViewHolder>() {

    companion object{
        const val VIEW_TYPE_ITEM: Int = 0
        const val VIEW_TYPE_CREATE: Int = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            dataSet.size -> {
                VIEW_TYPE_CREATE
            }
            else -> {
                VIEW_TYPE_ITEM
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item_photo, parent, false)
                ItemViewHolder(itemView)
            }

            else -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item_photo_add, parent, false)
                CreateViewHolder(itemView)

            }
        }
    }
    override fun onBindViewHolder(
        holder: MainViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return dataSet.size+1
    }

    fun setItemList(newDataSet: List<String>){
        dataSet = newDataSet
        notifyDataSetChanged()
    }

    fun addItemList(newItem: String){
        val newList = mutableListOf<String>()
        newList.addAll(dataSet)
        newList.add(newItem)
        dataSet = newList
        notifyItemInserted(dataSet.lastIndex)
    }



    inner class ItemViewHolder(val view: View) : MainViewHolder(view) {
        val image: ImageView
        val btnDelete: ImageView

        init {
            image = view.findViewById(R.id.iv_photo_image)
            btnDelete = view.findViewById(R.id.iv_photo_delete)
            btnDelete.setOnClickListener {
                onDelete(dataSet[adapterPosition])
            }
        }

        override fun bind(position: Int) {
            Glide.with(view.context)
                .load("$IMAGE_URL${dataSet[position]}")
                .placeholder(R.drawable.card_preview_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(image)
        }
    }

    inner class CreateViewHolder(val view: View) : MainViewHolder(view) {

        init {
            view.setOnClickListener {
                onCreate()
            }
        }
        override fun bind(position: Int) {

        }
    }

    abstract class MainViewHolder(view: View): RecyclerView.ViewHolder(view){
        abstract fun bind(position: Int)
    }
}