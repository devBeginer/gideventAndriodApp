package ru.gidevent.androidapp.ui.makeBooking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.gidevent.andriodapp.R
import ru.gidevent.androidapp.data.model.advertisement.PriceRVItem
import ru.gidevent.androidapp.data.model.booking.BookingPriceRVItem

class BookingPriceRecyclerViewAdapter(
    var dataSet: List<BookingPriceRVItem>,
    private val onMinus: (count: Int, price: Int, id: Long)->Int,
    private val onPlus: (count: Int, price: Int, id: Long)->Int
): RecyclerView.Adapter<BookingPriceRecyclerViewAdapter.ItemViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item_booking_price, parent, false)
        return ItemViewHolder(itemView)


    }
    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int
    ) {
        holder.categoryName.text = dataSet[position].name
        holder.price.text = dataSet[position].price.toString()
        holder.count.text = dataSet[position].count.toString()
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setItemList(newDataSet: List<BookingPriceRVItem>){
        dataSet = newDataSet
        notifyDataSetChanged()
    }

    fun updateItemList(count: Int, position: Int){
        val newDataSet = mutableListOf<BookingPriceRVItem>()
        newDataSet.addAll(dataSet)

        newDataSet[position] = BookingPriceRVItem(
            newDataSet[position].priceId,
            newDataSet[position].customerCategoryId,
            newDataSet[position].name,
            newDataSet[position].price,
            count
        )
        dataSet = newDataSet
        notifyItemChanged(position)

    }

    fun resetItemList(){
        val newDataSet = mutableListOf<BookingPriceRVItem>()
        newDataSet.addAll(dataSet)

        dataSet = newDataSet.map {
            BookingPriceRVItem(
                it.priceId,
                it.customerCategoryId,
                it.name,
                it.price,
                0
            )
        }
        notifyDataSetChanged()

    }



    inner class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val categoryName: TextView
        val price: TextView
        val count: TextView
        val btnPlus: ImageView
        val btnMinus: ImageView

        init {
            categoryName = view.findViewById(R.id.tv_booking_price_name)
            price = view.findViewById(R.id.tv_booking_price_rate)
            count = view.findViewById(R.id.tv_booking_price_count)
            btnPlus = view.findViewById(R.id.iv_booking_price_plus)
            btnMinus = view.findViewById(R.id.iv_booking_price_minus)
            btnPlus.setOnClickListener {
                val newCount = onPlus(/*count.text.toString().toInt()*/dataSet[adapterPosition].count, dataSet[adapterPosition].price, dataSet[adapterPosition].priceId)
                //count.text = newCount.toString()
                updateItemList(newCount, adapterPosition)
            }
            btnMinus.setOnClickListener {
                val newCount = onMinus(/*count.text.toString().toInt(),*/dataSet[adapterPosition].count, dataSet[adapterPosition].price, dataSet[adapterPosition].priceId)
                //count.text = newCount.toString()
                updateItemList(newCount, adapterPosition)
            }
        }
    }
}