package com.citius.mcsanit.api_response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetMyCoverageResponse (
    val data: List<Data>){

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    data class Data (
        val atm_id: String?,
        val area_name: String?,
        val city_name: String?,
        val city_detail_location_name: String?,
        val team_name: String?,
        val building_name: String?,
        val pic_name: String?,
        val address: String?,
    )
}