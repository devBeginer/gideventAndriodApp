package ru.gidevent.androidapp.ui.mainScreen.adapter

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.button.MaterialButton
import ru.gidevent.andriodapp.R
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.AdvertPreviewCard
import ru.gidevent.androidapp.utils.Utils

class FavouriteRecyclerViewAdapter(
    private var dataSet: List<AdvertPreviewCard>,
    private val onClick: (id: Long)->Unit,
    private val onFavourite: (id: Long)->Unit
) : RecyclerView.Adapter<FavouriteRecyclerViewAdapter.CardsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item_cards, parent, false)
        return CardsViewHolder(itemView)


        }



    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setItemsList(list: List<AdvertPreviewCard>) {
        dataSet = list
        notifyDataSetChanged()
    }

    fun updateItem(advert: AdvertPreviewCard) {
        val position = dataSet.find { it.id == advert.id }?.let { dataSet.indexOf(it) }
        if(position!=null){
            val tmpList = mutableListOf<AdvertPreviewCard>()
            tmpList.addAll(dataSet)
            tmpList[position] = advert
            dataSet = tmpList
            notifyItemChanged(position)
        }
    }

    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {
        holder.name.text = dataSet[position].name
        holder.price.text = "от ₽ ${dataSet[position].price.toString()}"
        holder.favourite.setIcon/*setImageDrawable*/(
            if (dataSet[position].isFavourite) ContextCompat.getDrawable(
                holder.favourite.context,
                R.drawable.baseline_favorite_active_24
            ) else ContextCompat.getDrawable(
                holder.favourite.context,
                R.drawable.twotone_favorite_24
            )
        )
        Glide.with(holder.imageView.context)
            .load(/*${Utils.IMAGE_URL}*/"${dataSet[position].photoUrl}")
            .placeholder(R.drawable.card_preview_placeholder)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(holder.imageView)

        holder.categories.removeAllViews()
        dataSet[position].categories.forEach { category ->
            val category = createCategory(category, holder.categories.context)
            holder.categories.addView(category)
        }
    }

    private fun createCategory(text: String, context: Context): TextView {
        val textView = TextView(context)
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        layoutParams.marginEnd = (8 * Resources.getSystem().displayMetrics.density).toInt()
        textView.setPadding((2 * Resources.getSystem().displayMetrics.density).toInt())
        textView.background =
            ContextCompat.getDrawable(context, R.drawable.card_view_category_background)
        textView.layoutParams = layoutParams
        textView.text = text
        return textView
    }

    inner class CardsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val name: TextView
        val price: TextView
        val imageView: ImageView
        val favourite: MaterialButton//ImageView
        val categories: LinearLayout

        init {
            name = view.findViewById(R.id.tv_card_name)
            price = view.findViewById(R.id.tv_card_price)
            imageView = view.findViewById(R.id.iv_card_preview)
            favourite = view.findViewById(R.id.iv_card_favourite)
            categories = view.findViewById(R.id.ll_card_category)
            view.setOnClickListener {
                onClick(dataSet[adapterPosition].id)
            }
            favourite.setOnClickListener {
                onFavourite(dataSet[adapterPosition].id)
            }
        }
    }


}