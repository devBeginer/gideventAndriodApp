package ru.gidevent.androidapp.ui.seller_management.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.gidevent.andriodapp.R
import ru.gidevent.androidapp.data.model.myAdverts.MyAdvert

class MyAdvertsRecyclerViewAdapter(
    private var dataSet: List<MyAdvert>,
    private val onClick: (id: Long) -> Unit,
    private val onEdit: (id: Long) -> Unit,
    private val onDelete: (id: Long) -> Unit
) : RecyclerView.Adapter<MyAdvertsRecyclerViewAdapter.CardsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item_my_advert, parent, false)
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
            tmpList[position] = myAdvert
            dataSet = tmpList
            notifyItemChanged(position)
        }
    }


    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {
        holder.name.text = dataSet[position].name
        holder.price.text = "${dataSet[position].cost}₽"
        holder.customerCount.text = dataSet[position].count.toString()
        holder.status.text = when(dataSet[position].status.toString()){
            "MODERATION"->"На модерации"
            "DECLINED"->"Отклонено"
            "ACCEPTED"->"Опубликовано"
            else -> "На модерации"
        }
    }

    inner class CardsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val price: TextView
        val customerCount: TextView
        val status: TextView
        val btnEdit: ImageView
        val btnDelete: ImageView

        init {
            name = view.findViewById(R.id.tv_card_my_advert_name)
            price = view.findViewById(R.id.tv_card_my_advert_price)
            customerCount = view.findViewById(R.id.tv_card_my_advert_people_count)
            status = view.findViewById(R.id.tv_card_my_advert_status)
            btnEdit = view.findViewById(R.id.iv_card_my_advert_edit)
            btnDelete = view.findViewById(R.id.iv_card_my_advert_delete)

            view.setOnClickListener {
                onClick(dataSet[adapterPosition].id)
            }

            btnEdit.setOnClickListener {
                onEdit(dataSet[adapterPosition].id)
            }

            btnDelete.setOnClickListener {
                onDelete(dataSet[adapterPosition].id)
            }

        }
    }


}