package com.citius.mcsanit.api_request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetLocationAddressNameRequest {
    @SerializedName("lat")
    @Expose
    var lat: String? = null

    @SerializedName("lon")
    @Expose
    var lon: String? = null
}