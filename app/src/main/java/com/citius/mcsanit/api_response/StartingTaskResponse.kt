package com.citius.mcsanit.api_response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StartingTaskResponse {


    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("start_working_time")
    @Expose
    var start_working_time: String? = null

    @SerializedName("start_working_time_unix")
    @Expose
    var start_working_time_unix: String? = null
}