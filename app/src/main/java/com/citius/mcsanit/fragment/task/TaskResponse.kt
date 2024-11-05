package com.citius.mcsanit.fragment.task

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TaskResponse (
    val data: List<Data>){

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    data class Data (
        var task_id: Int?,
        var atm_code: String?,
        var city_detail_location: String?,
        var address: String?,
        var task_date: String?,
        var task_status: String?,
        var atm_loc_lat: String?,
        var atm_loc_lon: String?,
        var date_reschedule_from: String?,
        var date_reschedule_to: String?,
        var task_status_int: Int?,
        var manage_electric: Int?
    )
}