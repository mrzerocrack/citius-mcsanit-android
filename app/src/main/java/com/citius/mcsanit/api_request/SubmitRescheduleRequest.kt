package com.citius.mcsanit.api_request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SubmitRescheduleRequest {

    @SerializedName("username")
    @Expose
    var username: String? = null

    @SerializedName("task_id")
    @Expose
    var task_id: Int? = null

    @SerializedName("reschedule_date")
    @Expose
    var reschedule_date: String? = null
}