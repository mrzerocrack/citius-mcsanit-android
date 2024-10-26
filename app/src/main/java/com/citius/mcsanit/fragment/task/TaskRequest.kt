package com.citius.mcsanit.fragment.task

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TaskRequest {
    @SerializedName("username")
    @Expose
    var username: String? = null

    @SerializedName("request_order")
    @Expose
    var request_order: String? = null
}