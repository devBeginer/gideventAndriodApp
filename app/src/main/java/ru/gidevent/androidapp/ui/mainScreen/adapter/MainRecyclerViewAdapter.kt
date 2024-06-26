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
import android.widget.TableLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ru.gidevent.andriodapp.R
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.AdvertPreviewCard
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.MainRecyclerViewData
import ru.gidevent.androidapp.utils.Utils

class MainRecyclerViewAdapter(
    private var dataSet: MainRecyclerViewData,
    private val onClick: (id: Long)->Unit,
    private val onClickViewPager: (id: Long)->Unit,
    private val onFavourite: (id: Long)->Unit
) : RecyclerView.Adapter<MainRecyclerViewAdapter.MainViewHolder>() {
    companion object{
        const val VIEW_TYPE_HEADER1: Int = 0
        const val VIEW_TYPE_HEADER2: Int = 1
        const val VIEW_TYPE_ITEM: Int = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> {
                VIEW_TYPE_HEADER1
            }
            1 -> {
                VIEW_TYPE_HEADER2
            }
            else -> {
                VIEW_TYPE_ITEM
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER1 -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.header_1_main_recyclerview_item, parent, false)
                HeaderViewHolder(itemView)
            }
            VIEW_TYPE_HEADER2 -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.header_2_main_recyclerview_item, parent, false)
                CategoryViewHolder(itemView)
            }
            else -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item_cards, parent, false)
                CardsViewHolder(itemView)

            }
        }
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {

        holder.bind(position)

    }

    override fun getItemCount(): Int {
        return dataSet.cardsDataSet.size+2
    }

    fun setItemsList(list: MainRecyclerViewData) {
        dataSet = list
        notifyDataSetChanged()
    }

    fun updateItem(advert: AdvertPreviewCard) {
        val position = dataSet.cardsDataSet.find { it.id == advert.id }?.let { dataSet.cardsDataSet.indexOf(it)+2 }
        if(position!=null){
            val tmpList = mutableListOf<AdvertPreviewCard>()
            tmpList.addAll(dataSet.cardsDataSet)
            tmpList[position-2] = advert
            val newDataSet = MainRecyclerViewData(dataSet.headerDataSet, dataSet.categoryDataSet, tmpList)
            dataSet = newDataSet
            notifyItemChanged(position)
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

    inner class CardsViewHolder(view: View) : MainViewHolder(view) {
        val name: TextView
        private val price: TextView
        private val imageView: ImageView
        private val favourite: MaterialButton//ImageView
        private val categories: LinearLayout

        init {
            name = view.findViewById(R.id.tv_card_name)
            price = view.findViewById(R.id.tv_card_price)
            imageView = view.findViewById(R.id.iv_card_preview)
            favourite = view.findViewById(R.id.iv_card_favourite)
            categories = view.findViewById(R.id.ll_card_category)

            view.setOnClickListener {
                onClick(dataSet.cardsDataSet[adapterPosition-2].id)
            }
            favourite.setOnClickListener {
                onFavourite(dataSet.cardsDataSet[adapterPosition-2].id)
            }
        }

        override fun bind(position: Int) {
            val positionInList = position-2
            name.text = dataSet.cardsDataSet[positionInList].name
            price.text = "от ₽ ${ dataSet.cardsDataSet[positionInList].price.toString() }"
            favourite.setIcon/*setImageDrawable*/(
                if (dataSet.cardsDataSet[positionInList].isFavourite) ContextCompat.getDrawable(
                    favourite.context,
                    R.drawable.baseline_favorite_active_24
                ) else ContextCompat.getDrawable(
                    favourite.context,
                    R.drawable.twotone_favorite_24
                )
            )
            Glide.with(imageView.context)
                .load(/*${Utils.IMAGE_URL}*/"${dataSet.cardsDataSet[positionInList].photoUrl}")
                .placeholder(R.drawable.image_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(imageView)

            categories.removeAllViews()
            dataSet.cardsDataSet[positionInList].categories.forEach { category ->
                val category = createCategory(category, categories.context)
                categories.addView(category)
            }
        }
    }

    inner class HeaderViewHolder(view: View) : MainViewHolder(view) {
        private var headerViewPager: ViewPager2
        private var headerViewPagerAdapter: HeaderViewPagerAdapter
        private var indicators: TabLayout

        init {
            headerViewPager = view.findViewById(R.id.view_pager_main_header)
            indicators = view.findViewById(R.id.view_pager_main_header_indicator)
            headerViewPagerAdapter = HeaderViewPagerAdapter(onClickViewPager)
        }

        override fun bind(position: Int) {
            headerViewPagerAdapter.setItemList(dataSet.headerDataSet)
            headerViewPager.adapter = headerViewPagerAdapter
            TabLayoutMediator(indicators, headerViewPager) { tab, position ->

            }.attach()
        }
    }

    inner class CategoryViewHolder(view: View) : MainViewHolder(view) {

        private var categoryRecyclerView: RecyclerView
        private var categoryAdapter: CategoryRecyclerViewAdapter

        init {
            categoryRecyclerView = view.findViewById(R.id.rv_main_cateory)
            categoryAdapter = CategoryRecyclerViewAdapter()
        }

        override fun bind(position: Int) {
            categoryAdapter.setItemList(dataSet.categoryDataSet)
            categoryRecyclerView.adapter = categoryAdapter
        }
    }

    abstract class MainViewHolder(view: View): RecyclerView.ViewHolder(view){
        abstract fun bind(position: Int)
    }
}