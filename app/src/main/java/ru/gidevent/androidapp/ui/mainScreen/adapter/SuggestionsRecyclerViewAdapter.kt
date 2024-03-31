package ru.gidevent.androidapp.ui.mainScreen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.gidevent.RestAPI.model.Category
import ru.gidevent.RestAPI.model.City
import ru.gidevent.andriodapp.R
import ru.gidevent.androidapp.data.model.suggestionsRecyclerviewModels.SuggestionRecyclerViewData

class SuggestionsRecyclerViewAdapter(
    private var dataSet: SuggestionRecyclerViewData,
    private val onClick: (id: Long)->Unit,
    private val onClickCity: (city: City)->Unit,
    private val onClickCategory: (category: Category)->Unit
) : RecyclerView.Adapter<SuggestionsRecyclerViewAdapter.MainViewHolder>() {
    companion object{
        const val VIEW_TYPE_CATEGORY: Int = 0
        const val VIEW_TYPE_CITY: Int = 1
        const val VIEW_TYPE_ITEM: Int = 2
    }

    override fun getItemViewType(position: Int): Int {
        val categoriesCount = dataSet.categoryList.size
        val cityCount = dataSet.cityList.size

        return when  {
            position < categoriesCount -> {
                VIEW_TYPE_CATEGORY
            }
            position < categoriesCount+cityCount -> {
                VIEW_TYPE_CITY
            }
            else -> {
                VIEW_TYPE_ITEM
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return when (viewType) {
            VIEW_TYPE_CATEGORY -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.search_category_recycler_view_item, parent, false)
                CategoryViewHolder(itemView)
            }
            VIEW_TYPE_CITY -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.search_city_recycler_view_item, parent, false)
                CityViewHolder(itemView)
            }
            else -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.search_recycler_view_item, parent, false)
                SuggestionViewHolder(itemView)

            }
        }
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {

        holder.bind(position)

    }

    override fun getItemCount(): Int {
        return dataSet.categoryList.size+
                dataSet.cityList.size+
                dataSet.suggestionsList.size
    }

    fun setItemsList(list: SuggestionRecyclerViewData) {
        dataSet = list
        notifyDataSetChanged()
    }


    inner class SuggestionViewHolder(view: View) : MainViewHolder(view) {
        val name: TextView
        val city: TextView

        init {
            name = view.findViewById(R.id.tv_suggestion_name)
            city = view.findViewById(R.id.tv_suggestion_item_city)
            view.setOnClickListener {
                onClick(dataSet.suggestionsList[adapterPosition-dataSet.categoryList.size-dataSet.cityList.size].id)
            }
        }

        override fun bind(position: Int) {
            val positionInList = position-dataSet.categoryList.size-dataSet.cityList.size
            name.text = dataSet.suggestionsList[positionInList].name
            city.text = dataSet.suggestionsList[positionInList].city
        }
    }

    inner class CityViewHolder(view: View) : MainViewHolder(view) {
        val name: TextView

        init {
            name = view.findViewById(R.id.tv_suggestion_city)
            view.setOnClickListener {
                onClickCity(City(dataSet.cityList[adapterPosition-dataSet.categoryList.size].id, dataSet.cityList[adapterPosition-dataSet.categoryList.size].name))
            }
        }

        override fun bind(position: Int) {
            val positionInList = position-dataSet.categoryList.size
            name.text = dataSet.cityList[positionInList].name
        }
    }

    inner class CategoryViewHolder(view: View) : MainViewHolder(view) {

        val name: TextView

        init {
            name = view.findViewById(R.id.tv_suggestion_category)
            view.setOnClickListener {
                onClickCategory(Category(dataSet.categoryList[adapterPosition].id, dataSet.categoryList[adapterPosition].name))
            }
        }

        override fun bind(position: Int) {
            val positionInList = position
            name.text = dataSet.categoryList[positionInList].name
        }
    }

    abstract class MainViewHolder(view: View): RecyclerView.ViewHolder(view){
        abstract fun bind(position: Int)
    }
}