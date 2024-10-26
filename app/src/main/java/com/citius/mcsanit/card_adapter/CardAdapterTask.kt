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

class CardAdapterTask(
    val item : ArrayList<TaskResponse.Data>
): RecyclerView.Adapter<CardAdapterTask.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardAdapterTask.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.card_task_list_adapter, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CardAdapterTask.ViewHolder, position: Int) {
        val task_list = item[position]
        holder.location_detail.text = task_list.city_detail_location
        holder.task_date.text = task_list.task_date
        holder.task_status.text = task_list.task_status
        holder.atm_code.text = "ATM ID : ${task_list.atm_code}"
        holder.address.text = task_list.address
        holder.card.setOnClickListener {
            val mainActivity = holder.itemView.context as MainActivity
            val bundle = Bundle()
            bundle.putString("atm_code", task_list.atm_code.toString())
            bundle.putString("task_date", task_list.task_date.toString())
            bundle.putInt("task_id", task_list.task_id!!)
            bundle.putDouble("atm_loc_lat", task_list.atm_loc_lat!!.toDouble())
            bundle.putDouble("atm_loc_lon", task_list.atm_loc_lon!!.toDouble())
            val fragmentTaskFormFinish = FragmentTaskFormFinish()
            fragmentTaskFormFinish.arguments = bundle
            mainActivity.openFragment(fragmentTaskFormFinish,"frag_task_form_finish", true)
        }
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
        val task_status = view.findViewById<TextView>(R.id.task_list_open_status)
        val address = view.findViewById<TextView>(R.id.task_list_open_address)
        val atm_code = view.findViewById<TextView>(R.id.task_list_open_atm_code)
        val card = view.findViewById<ConstraintLayout>(R.id.task_list_open_card)
    }
}