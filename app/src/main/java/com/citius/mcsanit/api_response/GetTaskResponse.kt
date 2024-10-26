package com.citius.mcsanit.api_response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetTaskResponse (
    val data : Data?
){

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("start_time")
    @Expose
    var start_time: String? = null

    data class Data (
        val start_working_time: String?,
        val finish_working_time: String?,
        val start_working_time_unix: String?,
        val finish_working_time_unix: String?,
        val area_name: String?,
        val city_name: String?,
        val city_detail_location_name: String?,
        val team_name: String?,
        val task_status: String?,
        val atm_code: String?,
    )
}