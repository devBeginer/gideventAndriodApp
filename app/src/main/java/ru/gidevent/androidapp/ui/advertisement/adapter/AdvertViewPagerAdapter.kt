package ru.gidevent.androidapp.ui.advertisement.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ru.gidevent.andriodapp.R
import ru.gidevent.androidapp.data.model.advertisement.AdvertViewpagerItem

class AdvertViewPagerAdapter(): RecyclerView.Adapter<AdvertViewPagerAdapter.AdvertViewHolder>() {
    private var itemsList: List<AdvertViewpagerItem> = listOf()

    fun setItemList(newItemsList: List<AdvertViewpagerItem>){
        itemsList = newItemsList
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdvertViewPagerAdapter.AdvertViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.header_viewpager_item, parent, false)
        return AdvertViewPagerAdapter.AdvertViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdvertViewPagerAdapter.AdvertViewHolder, position: Int) {
        Glide.with(holder.imageView.context)
            .load(itemsList[position].photoUrl)
            //.load(ContextCompat.getDrawable(holder.imageView.context, itemsList[position].photoUrl))
            .placeholder(R.drawable.card_preview_placeholder)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    class AdvertViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val imageView: ImageView
        init {
            imageView = view.findViewById(R.id.view_pager_advertisement)
        }
    }
}