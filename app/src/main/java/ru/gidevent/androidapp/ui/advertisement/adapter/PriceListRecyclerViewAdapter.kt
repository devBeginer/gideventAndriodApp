package ru.gidevent.androidapp.ui.advertisement.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.gidevent.andriodapp.R
import ru.gidevent.androidapp.data.model.advertisement.response.TicketPriceDto

class PriceListRecyclerViewAdapter(
    var dataSet: List<TicketPriceDto>
): RecyclerView.Adapter<PriceListRecyclerViewAdapter.ItemViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item_advert_price, parent, false)
        return ItemViewHolder(itemView)


    }
    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int
    ) {
        holder.categoryName.text = dataSet[position].name
        holder.price.text = dataSet[position].price.toString()
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setItemList(newDataSet: List<TicketPriceDto>){
        dataSet = newDataSet
        notifyDataSetChanged()
    }



    inner class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val categoryName: TextView
        val price: TextView

        init {
            categoryName = view.findViewById(R.id.tv_advert_price_name)
            price = view.findViewById(R.id.tv_advert_price_rate)
        }
    }
}