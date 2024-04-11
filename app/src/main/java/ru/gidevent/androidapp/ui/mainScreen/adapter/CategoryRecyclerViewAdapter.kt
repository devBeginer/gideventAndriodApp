package ru.gidevent.androidapp.ui.mainScreen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.gidevent.andriodapp.R

class CategoryRecyclerViewAdapter() : RecyclerView.Adapter<CategoryRecyclerViewAdapter.CardsViewHolder>() {

    private var categoryList: List<String> = listOf()

    fun setItemList(newCategoryList: List<String>){
        categoryList = newCategoryList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item_category, parent, false)
        return CardsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    fun setItemsList(list: List<String>) {
        categoryList = list
    }

    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {
        holder.name.text = categoryList[position]

    }

    class CardsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView

        init {
            name = view.findViewById(R.id.tv_category_name)

        }
    }
}