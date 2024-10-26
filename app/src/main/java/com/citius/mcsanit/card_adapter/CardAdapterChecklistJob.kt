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
import com.citius.mcsanit.fragment.task.GetChecklistJobResponse
import com.citius.mcsanit.fragment.task.TaskResponse

class CardAdapterChecklistJob(
    val item : ArrayList<GetChecklistJobResponse.Data>
): RecyclerView.Adapter<CardAdapterChecklistJob.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardAdapterChecklistJob.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.card_checklist_job_form, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CardAdapterChecklistJob.ViewHolder, position: Int) {
        val task_list = item[position]
        holder.title_checklist_job.text = task_list.checklist_job_name

//        holder.card.setOnClickListener {
//            val mainActivity = holder.itemView.context as MainActivity
//            val bundle = Bundle()
//            bundle.putString("atm_code", task_list.atm_code.toString())
//            val fragmentTaskFormFinish = FragmentTaskFormFinish()
//            fragmentTaskFormFinish.arguments = bundle
//            mainActivity.openFragment(fragmentTaskFormFinish,"frag_task_form_finish", true)
//        }
    }

    override fun getItemCount(): Int {
        return item.size
    }

    public fun setData(data: List<GetChecklistJobResponse.Data>) {
        item.clear()
        item.addAll(data)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val title_checklist_job = view.findViewById<TextView>(R.id.title_checklist_job)
    }
}