package com.citius.mcsanit.fragment.task

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetChecklistJobResponse (
    val data: List<Data>){

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    data class Data (
        val checklist_job_id: Int?,
        val checklist_job_name: String?,
        val checklist_job_data: List<ChecklistJobData>
    ){
        data class ChecklistJobData(
            var name: String?,
            var additional_text: String?,
        )
    }
}