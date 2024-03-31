package ru.gidevent.androidapp.ui.edit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.gidevent.andriodapp.R
import ru.gidevent.androidapp.data.model.advertisement.PriceRVItem
import ru.gidevent.androidapp.data.model.advertisement.dto.EventTime
import ru.gidevent.androidapp.ui.advertisement.adapter.AdvertReviewRecyclerViewAdapter
import ru.gidevent.androidapp.utils.Utils.toString

class PriceEditRecyclerViewAdapter(
    private var dataSet: List<PriceRVItem>,
    private val onDelete: (id: Long)->Unit,
    private val onEdit: (id: Long)->Unit,
    private val onCreate: ()->Unit
): RecyclerView.Adapter<PriceEditRecyclerViewAdapter.MainViewHolder>() {

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
                    .inflate(R.layout.recyclerview_item_price, parent, false)
                ItemViewHolder(itemView)
            }

            else -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item_price_add, parent, false)
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

    fun setItemList(newDataSet: List<PriceRVItem>){
        dataSet = newDataSet
        notifyDataSetChanged()
    }



    inner class ItemViewHolder(view: View) : MainViewHolder(view) {
        val categoryName: TextView
        val price: TextView
        val btnEdit: ImageView
        val btnDelete: ImageView

        init {
            categoryName = view.findViewById(R.id.tv_price_name)
            price = view.findViewById(R.id.tv_price_rate)
            btnEdit = view.findViewById(R.id.iv_price_edit)
            btnDelete = view.findViewById(R.id.iv_price_delete)
            btnEdit.setOnClickListener {
                onEdit(dataSet[adapterPosition].priceId)
            }
            btnDelete.setOnClickListener {
                onDelete(dataSet[adapterPosition].priceId)
            }
        }

        override fun bind(position: Int) {
            categoryName.text = dataSet[position].name
            price.text = dataSet[position].price.toString()
        }
    }

    inner class CreateViewHolder(view: View) : MainViewHolder(view) {

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