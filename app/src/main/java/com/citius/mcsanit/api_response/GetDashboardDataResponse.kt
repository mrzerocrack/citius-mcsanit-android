package com.citius.mcsanit.api_response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetDashboardDataResponse (
    val data: Data){

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    data class Data (
        var total_finished_task: Int?,
        var total_open_task: String?,
        var total_finished_reschedule: String?,
        var total_open_reschedule: String?,
        var team_id: String?,
    )
}