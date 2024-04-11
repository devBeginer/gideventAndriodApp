package ru.gidevent.androidapp.ui.mainScreen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.gidevent.andriodapp.R
import ru.gidevent.androidapp.data.model.myAdverts.MyBooking
import ru.gidevent.androidapp.data.model.myAdverts.VisitorsGroupResponse
import ru.gidevent.androidapp.utils.Utils.toString

class BookingsGroupRecyclerViewAdapter(
    private var dataSet: List<VisitorsGroupResponse>
) : RecyclerView.Adapter<BookingsGroupRecyclerViewAdapter.CardsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item_booking_group, parent, false)
        return CardsViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setItemsList(list: List<VisitorsGroupResponse>) {
        dataSet = list
        notifyDataSetChanged()
    }

    fun updateItem(myBooking: VisitorsGroupResponse) {
        val position = dataSet.find { it.id == myBooking.id }?.let { dataSet.indexOf(it) }
        if(position!=null){
            val tmpList = mutableListOf<VisitorsGroupResponse>()
            tmpList.addAll(dataSet)
            tmpList[position] = myBooking
            dataSet = tmpList
            notifyItemChanged(position)
        }
    }


    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {
        holder.name.text = dataSet[position].name
        holder.customerCount.text = "x${dataSet[position].count}"
    }

    inner class CardsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val customerCount: TextView

        init {
            name = view.findViewById(R.id.tv_price_name)
            customerCount = view.findViewById(R.id.tv_price_rate)
        }
    }


}