package ru.gidevent.androidapp.ui.seller_management.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.gidevent.andriodapp.R
import ru.gidevent.androidapp.data.model.myAdverts.MyBooking
import ru.gidevent.androidapp.utils.Utils.toString

class MyBookingsRecyclerViewAdapter(
    private var dataSet: List<MyBooking>,
    private val onClick: (id: Long) -> Unit,
    private val onConfirm: (id: Long) -> Unit,
    private val onDecline: (id: Long) -> Unit
) : RecyclerView.Adapter<MyBookingsRecyclerViewAdapter.CardsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item_my_booking, parent, false)
        return CardsViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setItemsList(list: List<MyBooking>) {
        dataSet = list
        notifyDataSetChanged()
    }

    fun updateItem(confirmed: Boolean, id: Long) {
        val position = dataSet.find { it.id == id }?.let { dataSet.indexOf(it) }
        if(position!=null){
            val tmpList = mutableListOf<MyBooking>()
            tmpList.addAll(dataSet)
            tmpList[position] = MyBooking(dataSet[position].id, dataSet[position].name, dataSet[position].count, dataSet[position].date, dataSet[position].cost, confirmed)
            dataSet = tmpList
            notifyItemChanged(position)
        }
    }


    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {
        holder.name.text = dataSet[position].name
        holder.price.text = "${dataSet[position].cost}₽"
        holder.customerCount.text = dataSet[position].count.toString()
        holder.date.text = dataSet[position].date.time.toString("dd.MM.yy HH:mm")
        if (dataSet[position].isConfirmed) {
            holder.btnConfirm.visibility = View.GONE
            holder.btnDecline.visibility = View.VISIBLE
        }else{
            holder.btnConfirm.visibility = View.VISIBLE
            holder.btnDecline.visibility = View.GONE
        }
    }

    inner class CardsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val price: TextView
        val customerCount: TextView
        val date: TextView
        val btnConfirm: Button
        val btnDecline: Button

        init {
            name = view.findViewById(R.id.tv_card_my_booking_name)
            price = view.findViewById(R.id.tv_card_my_booking_price)
            customerCount = view.findViewById(R.id.tv_card_my_booking_people_count)
            date = view.findViewById(R.id.tv_card_my_booking_time)
            btnConfirm = view.findViewById(R.id.btn_my_booking_confirm)
            btnDecline = view.findViewById(R.id.btn_my_booking_decline)
            view.setOnClickListener {
                onClick(dataSet[adapterPosition].id)
            }
            btnConfirm.setOnClickListener {
                onConfirm(dataSet[adapterPosition].id)
            }
            btnDecline.setOnClickListener {
                onDecline(dataSet[adapterPosition].id)
            }
        }
    }


}