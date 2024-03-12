package ru.gidevent.androidapp.ui.mainScreen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.gidevent.RestAPI.model.City
import ru.gidevent.andriodapp.R
import ru.gidevent.androidapp.data.model.suggestionsRecyclerviewModels.SuggestionCity
import ru.gidevent.androidapp.data.model.suggestionsRecyclerviewModels.SuggestionRecyclerViewData

class CityPickRecyclerViewAdapter(
    private var dataSet: List<SuggestionCity>,
    private var onClick: (city: SuggestionCity)->Unit,
) : RecyclerView.Adapter<CityPickRecyclerViewAdapter.CityViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_city_recycler_view_item, parent, false)
        return CityViewHolder(itemView)


    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {

        holder.name.text = dataSet[position].name

    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setItemsList(list: List<SuggestionCity>) {
        dataSet = list
        notifyDataSetChanged()
    }


    inner class CityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView

        init {
            name = view.findViewById(R.id.tv_suggestion_city)
            view.setOnClickListener {
                onClick(dataSet[adapterPosition])
            }
        }

    }


}