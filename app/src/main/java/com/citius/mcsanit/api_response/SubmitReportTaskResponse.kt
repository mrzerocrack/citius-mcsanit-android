package com.citius.mcsanit.api_response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SubmitReportTaskResponse (){

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

}