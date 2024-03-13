package ru.gidevent.androidapp.ui.advertisement.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ru.gidevent.andriodapp.R
import ru.gidevent.androidapp.data.model.advertisement.ReviewRecyclerViewItem
import ru.gidevent.androidapp.ui.mainScreen.adapter.HeaderViewPagerAdapter

class AdvertReviewRecyclerViewAdapter(
    private var dataSet: List<ReviewRecyclerViewItem>
): RecyclerView.Adapter<AdvertReviewRecyclerViewAdapter.AdvertViewHolder>() {
    companion object{
        const val VIEW_TYPE_ITEM: Int = 0
        const val VIEW_TYPE_MORE: Int = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            3 -> {
                VIEW_TYPE_MORE
            }
            else -> {
                VIEW_TYPE_ITEM
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdvertViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item_feedback, parent, false)
                ItemViewHolder(itemView)
            }

            else -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item_all_feedbacks, parent, false)
                MoreViewHolder(itemView)

            }
        }
    }

    override fun onBindViewHolder(
        holder: AdvertViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return if(dataSet.size>=3) dataSet.size+1 else dataSet.size
    }

    fun setItemList(newDataSet: List<ReviewRecyclerViewItem>){
        dataSet = newDataSet
    }

    inner class ItemViewHolder(view: View) : AdvertViewHolder(view) {
        val name: TextView
        val rating: RatingBar
        val text: TextView
        val imageView: ImageView

        init {
            name = view.findViewById(R.id.tv_feedback_name)
            rating = view.findViewById(R.id.ratingBar_feedback)
            text = view.findViewById(R.id.tv_advert_feedback_text)
            imageView = view.findViewById(R.id.iv_feedback_circular_avatar)
        }

        override fun bind(position: Int) {
            name.text = dataSet[position].name
            rating.rating = dataSet[position].rating.toFloat()
            text.text = dataSet[position].text

            Glide.with(imageView.context)
                .load(dataSet[position].avatarUrl)
                .placeholder(R.drawable.card_preview_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(imageView)


        }
    }

    inner class MoreViewHolder(view: View) : AdvertViewHolder(view) {

        init {

        }
        override fun bind(position: Int) {

        }
    }

    abstract class AdvertViewHolder(view: View): RecyclerView.ViewHolder(view){
        abstract fun bind(position: Int)
    }
}