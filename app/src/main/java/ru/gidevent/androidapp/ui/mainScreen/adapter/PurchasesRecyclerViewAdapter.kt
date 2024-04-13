package ru.gidevent.androidapp.ui.mainScreen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.gidevent.andriodapp.R
import ru.gidevent.androidapp.data.model.myAdverts.MyBooking
import ru.gidevent.androidapp.utils.Utils.toString

class PurchasesRecyclerViewAdapter(
    private var dataSet: List<MyBooking>,
    private val onClick: (id: Long)->Unit,
    private val onDecline: (id: Long) -> Unit
) : RecyclerView.Adapter<PurchasesRecyclerViewAdapter.CardsViewHolder>() {


    /*override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item_cards, parent, false)
        return CardsViewHolder(itemView)


        }



    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setItemsList(list: List<AdvertPreviewCard>) {
        dataSet = list
        notifyDataSetChanged()
    }

    fun updateItem(advert: AdvertPreviewCard) {
        val position = dataSet.find { it.id == advert.id }?.let { dataSet.indexOf(it) }
        if(position!=null){
            val tmpList = mutableListOf<AdvertPreviewCard>()
            tmpList.addAll(dataSet)
            tmpList[position] = advert
            dataSet = tmpList
            notifyItemChanged(position)
        }
    }

    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {
        holder.name.text = dataSet[position].name
        holder.price.text = "₽ ${dataSet[position].price.toString()}"
        holder.favourite.setImageDrawable(
            if (dataSet[position].isFavourite) ContextCompat.getDrawable(
                holder.favourite.context,
                R.drawable.baseline_favorite_active_24
            ) else ContextCompat.getDrawable(
                holder.favourite.context,
                R.drawable.twotone_favorite_24
            )
        )
        Glide.with(holder.imageView.context)
            .load("${Utils.IMAGE_URL}${dataSet[position].photoUrl}")
            .placeholder(R.drawable.card_preview_placeholder)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(holder.imageView)

        holder.categories.removeAllViews()
        dataSet[position].categories.forEach { category ->
            val category = createCategory(category, holder.categories.context)
            holder.categories.addView(category)
        }
    }

    private fun createCategory(text: String, context: Context): TextView {
        val textView = TextView(context)
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        layoutParams.marginEnd = (8 * Resources.getSystem().displayMetrics.density).toInt()
        textView.setPadding((2 * Resources.getSystem().displayMetrics.density).toInt())
        textView.background =
            ContextCompat.getDrawable(context, R.drawable.card_view_category_background)
        textView.layoutParams = layoutParams
        textView.text = text
        return textView
    }

    inner class CardsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val name: TextView
        val price: TextView
        val imageView: ImageView
        val favourite: ImageView
        val categories: LinearLayout

        init {
            name = view.findViewById(R.id.tv_card_name)
            price = view.findViewById(R.id.tv_card_price)
            imageView = view.findViewById(R.id.iv_card_preview)
            favourite = view.findViewById(R.id.iv_card_favourite)
            categories = view.findViewById(R.id.ll_card_category)
            view.setOnClickListener {
                onClick(dataSet[adapterPosition].id)
            }
            favourite.setOnClickListener {
                onFavourite(dataSet[adapterPosition].id)
            }
        }
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item_customer_booking, parent, false)
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
            holder.confimed.text = "Подтверждено"
        }else{
            holder.confimed.text = "Не подтверждено"
        }
    }

    inner class CardsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val price: TextView
        val customerCount: TextView
        val confimed: TextView
        val date: TextView
        val btnDecline: Button

        init {
            name = view.findViewById(R.id.tv_card_customer_booking_name)
            price = view.findViewById(R.id.tv_card_customer_booking_price)
            customerCount = view.findViewById(R.id.tv_card_customer_booking_people_count)
            confimed = view.findViewById(R.id.tv_card_customer_booking_confirmed)
            date = view.findViewById(R.id.tv_card_customer_booking_time)
            btnDecline = view.findViewById(R.id.btn_customer_booking_decline)
            view.setOnClickListener {
                onClick(dataSet[adapterPosition].id)
            }
            btnDecline.setOnClickListener {
                onDecline(dataSet[adapterPosition].id)
            }
        }
    }


}