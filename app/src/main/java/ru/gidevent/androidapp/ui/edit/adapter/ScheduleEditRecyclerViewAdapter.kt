package ru.gidevent.androidapp.ui.edit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.gidevent.andriodapp.R
import ru.gidevent.androidapp.data.model.advertisement.dto.EventTime
import ru.gidevent.androidapp.ui.advertisement.adapter.AdvertReviewRecyclerViewAdapter
import ru.gidevent.androidapp.utils.Utils.toString

class ScheduleEditRecyclerViewAdapter(
    private var dataSet: List<EventTime>,
    private val onDelete: (id: Long)->Unit,
    private val onEdit: (id: Long)->Unit,
    private val onCreate: ()->Unit
): RecyclerView.Adapter<ScheduleEditRecyclerViewAdapter.MainViewHolder>() {

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
                    .inflate(R.layout.recyclerview_item_schedule, parent, false)
                ItemViewHolder(itemView)
            }

            else -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item_schedule_add, parent, false)
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

    fun setItemList(newDataSet: List<EventTime>){
        dataSet = newDataSet
        notifyDataSetChanged()
    }



    inner class ItemViewHolder(view: View) : MainViewHolder(view) {
        val time: TextView
        val weeckDays: TextView
        val btnEdit: ImageView
        val btnDelete: ImageView

        init {
            time = view.findViewById(R.id.tv_schedule_time)
            weeckDays = view.findViewById(R.id.tv_schedule_week_days)
            btnEdit = view.findViewById(R.id.iv_schedule_edit)
            btnDelete = view.findViewById(R.id.iv_schedule_delete)
            btnEdit.setOnClickListener {
                onEdit(dataSet[adapterPosition].timeId)
            }
            btnDelete.setOnClickListener {
                onDelete(dataSet[adapterPosition].timeId)
            }
        }

        override fun bind(position: Int) {
            time.text = dataSet[position].time.time.toString("HH:mm")
            weeckDays.text = if(dataSet[position].isRepeatable){
                dataSet[position].daysOfWeek.split(",").map {
                    when(it){
                        "Monday" -> "Пн"
                        "Tuesday" -> "Вт"
                        "Wednesday" -> "Ср"
                        "Thursday" -> "Чт"
                        "Friday" -> "Пт"
                        "Saturday" -> "Сб"
                        "Sunday"-> "Вс"
                        else -> ""
                    }
                }.joinToString(" ")
            }else{
                dataSet[position].startDate.time.toString("dd.MM.yyyy")
            }








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