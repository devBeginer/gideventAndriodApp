package ru.gidevent.androidapp.ui.advertisement.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.gidevent.andriodapp.R
import ru.gidevent.androidapp.data.model.advertisement.dto.EventTime
import ru.gidevent.androidapp.utils.Utils.toString

class AdvertScheduleRecyclerViewAdapter(
    private var dataSet: List<EventTime>
) : RecyclerView.Adapter<AdvertScheduleRecyclerViewAdapter.CardsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item_advert_eventtime, parent, false)
        return CardsViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setItemsList(list: List<EventTime>) {
        dataSet = list
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {
        holder.scheduleTime.text = dataSet[position].time.time.toString("HH:mm")
        holder.date.text = if(dataSet[position].isRepeatable){
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

    inner class CardsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val scheduleTime: TextView
        val date: TextView

        init {
            scheduleTime = view.findViewById(R.id.tv_schedule_time)
            date = view.findViewById(R.id.tv_schedule_week_days)
        }
    }


}