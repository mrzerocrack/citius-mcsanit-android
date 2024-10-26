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
import com.citius.mcsanit.api_response.GetMyCoverageResponse

class CardAdapterCoverage(
    val item : ArrayList<GetMyCoverageResponse.Data>
): RecyclerView.Adapter<CardAdapterCoverage.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardAdapterCoverage.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.card_coverage_machine_list_adapter, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CardAdapterCoverage.ViewHolder, position: Int) {
        val task_list = item[position]
        holder.card_coverage_atm_code.text = "ATM ID : ${task_list.atm_id}"
        holder.card_coverage_area_name.text = task_list.area_name
        holder.card_coverage_city_name.text = task_list.city_name
        holder.card_coverage_address.text = task_list.address
        holder.card_coverage_pic_name.text = task_list.pic_name
        holder.card_coverage_building_name.text = task_list.building_name
        holder.card_coverage_city_detail_location_name.text = task_list.city_detail_location_name
    }

    override fun getItemCount(): Int {
        return item.size
    }

    public fun setData(data: List<GetMyCoverageResponse.Data>) {
        item.clear()
        item.addAll(data)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val card_coverage_atm_code = view.findViewById<TextView>(R.id.card_coverage_atm_code)
        val card_coverage_area_name = view.findViewById<TextView>(R.id.card_coverage_area_name)
        val card_coverage_city_name = view.findViewById<TextView>(R.id.card_coverage_city_name)
        val card_coverage_address = view.findViewById<TextView>(R.id.card_coverage_address)
        val card_coverage_pic_name = view.findViewById<TextView>(R.id.card_coverage_pic_name)
        val card_coverage_building_name = view.findViewById<TextView>(R.id.card_coverage_building_name)
        val card_coverage_city_detail_location_name = view.findViewById<TextView>(R.id.card_coverage_city_detail_location_name)
    }
}