package ru.gidevent.androidapp.ui.mainScreen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ru.gidevent.andriodapp.R
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.HeaderViewpagerItem
import ru.gidevent.androidapp.utils.Utils

class HeaderViewPagerAdapter(
    val onClick: (id: Long)->Unit
) : RecyclerView.Adapter<HeaderViewPagerAdapter.HeaderViewHolder>() {

    private var itemsList: List<HeaderViewpagerItem> = listOf()

    fun setItemList(newItemsList: List<HeaderViewpagerItem>){
        itemsList = newItemsList
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.header_viewpager_item, parent, false)
        return HeaderViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.name.text = itemsList[position].name
        holder.price.text = "₽ ${itemsList[position].price.toString()}"

        Glide.with(holder.imageView.context)
            .load("${Utils.IMAGE_URL}${itemsList[position].photoUrl}")
            //.load(ContextCompat.getDrawable(holder.imageView.context, itemsList[position].photoUrl))
            .placeholder(R.drawable.card_preview_placeholder)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(holder.imageView)
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val price: TextView
        val imageView: ImageView

        init {
            name = view.findViewById(R.id.tv_name_header_viewpager)
            price = view.findViewById(R.id.tv_price_header_viewpager)
            imageView = view.findViewById(R.id.iv_header_viewpager)

            view.setOnClickListener {
                onClick(itemsList[adapterPosition].id)
            }
        }
    }

}