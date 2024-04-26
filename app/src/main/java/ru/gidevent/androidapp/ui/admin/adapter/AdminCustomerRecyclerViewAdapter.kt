package ru.gidevent.androidapp.ui.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.gidevent.RestAPI.model.Category
import ru.gidevent.andriodapp.R
import ru.gidevent.androidapp.data.model.advertisement.PriceRVItem
import ru.gidevent.androidapp.data.model.advertisement.dto.CustomerCategory

class AdminCustomerRecyclerViewAdapter(
    private var dataSet: List<CustomerCategory>,
    private val onDelete: (id: Long)->Unit,
    private val onEdit: (id: Long, name: String)->Unit
): RecyclerView.Adapter<AdminCustomerRecyclerViewAdapter.MainViewHolder>() {



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainViewHolder {

        val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item_admin_customer, parent, false)
        return MainViewHolder(itemView)

        }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.categoryName.text = dataSet[position].name
    }

    fun setItemList(newDataSet: List<CustomerCategory>){
        dataSet = newDataSet
        notifyDataSetChanged()
    }



    inner class MainViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val categoryName: TextView
        val btnEdit: ImageView
        val btnDelete: ImageView

        init {
            categoryName = view.findViewById(R.id.tv_admin_customer_name)
            btnEdit = view.findViewById(R.id.iv_admin_customer_edit)
            btnDelete = view.findViewById(R.id.iv_admin_customer_delete)
            btnEdit.setOnClickListener {
                onEdit(dataSet[adapterPosition].customerCategoryId, dataSet[adapterPosition].name)
            }
            btnDelete.setOnClickListener {
                onDelete(dataSet[adapterPosition].customerCategoryId)
            }
        }
    }
}