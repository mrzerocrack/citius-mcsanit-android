package com.citius.mcsanit.card_adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.citius.mcsanit.MainActivity
import com.citius.mcsanit.R
import com.citius.mcsanit.fragment.task.FragmentTaskFormFinish
import com.citius.mcsanit.fragment.task.TaskResponse

class CardAdapterReschedule(
    val item : ArrayList<TaskResponse.Data>
): RecyclerView.Adapter<CardAdapterReschedule.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardAdapterReschedule.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.card_reschedule_task_list_adapter, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CardAdapterReschedule.ViewHolder, position: Int) {
        val task_list = item[position]
        holder.location_detail.text = task_list.city_detail_location
        holder.task_date.text = "Task date : ${task_list.task_date}"
        holder.reschedule_from.text = "Reschedule from : ${task_list.date_reschedule_from}"
        holder.reschedule_to.text = "Reschedule to : ${task_list.date_reschedule_to}"
        holder.task_status.text = task_list.task_status
        holder.atm_code.text = "ATM ID : ${task_list.atm_code}"
        holder.address.text = task_list.address
    }

    override fun getItemCount(): Int {
        return item.size
    }

    public fun setData(data: List<TaskResponse.Data>) {
        item.clear()
        item.addAll(data)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val location_detail = view.findViewById<TextView>(R.id.task_list_open_detail_location_name)
        val task_date = view.findViewById<TextView>(R.id.task_list_open_date)
        val reschedule_from = view.findViewById<TextView>(R.id.task_list_rescheduled_from)
        val reschedule_to = view.findViewById<TextView>(R.id.task_list_rescheduled_to)
        val task_status = view.findViewById<TextView>(R.id.task_list_open_status)
        val address = view.findViewById<TextView>(R.id.task_list_open_address)
        val atm_code = view.findViewById<TextView>(R.id.task_list_open_atm_code)
        val card = view.findViewById<ConstraintLayout>(R.id.task_list_open_card)
    }
}