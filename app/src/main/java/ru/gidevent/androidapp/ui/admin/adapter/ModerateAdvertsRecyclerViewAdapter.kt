package ru.gidevent.androidapp.ui.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.gidevent.andriodapp.R
import ru.gidevent.androidapp.data.model.myAdverts.MyAdvert

class ModerateAdvertsRecyclerViewAdapter(
    private var dataSet: List<MyAdvert>,
    private val onClick: (id: Long) -> Unit,
    private val onConfirm: (id: Long) -> Unit,
    private val onDecline: (id: Long) -> Unit
) : RecyclerView.Adapter<ModerateAdvertsRecyclerViewAdapter.CardsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item_moderate_advert, parent, false)
        return CardsViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setItemsList(list: List<MyAdvert>) {
        dataSet = list
        notifyDataSetChanged()
    }

    fun updateItem(myAdvert: MyAdvert) {
        val position = dataSet.find { it.id == myAdvert.id }?.let { dataSet.indexOf(it) }
        if(position!=null){
            val tmpList = mutableListOf<MyAdvert>()
            tmpList.addAll(dataSet)
            tmpList.removeAt(position)
            dataSet = tmpList
            notifyItemChanged(position)
        }
    }


    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {
        holder.name.text = dataSet[position].name
        holder.price.text = "${dataSet[position].cost}₽"
        holder.customerCount.text = dataSet[position].count.toString()
    }

    inner class CardsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val price: TextView
        val customerCount: TextView
        val btnConfirm: Button
        val btnDecline: Button

        init {
            name = view.findViewById(R.id.tv_card_moderate_advert_name)
            price = view.findViewById(R.id.tv_card_moderate_advert_price)
            customerCount = view.findViewById(R.id.tv_card_moderate_advert_people_count)
            btnConfirm = view.findViewById(R.id.btn_moderate_advert_confirm)
            btnDecline = view.findViewById(R.id.btn_moderate_advert_decline)

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