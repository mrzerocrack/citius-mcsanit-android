package com.citius.mcsanit.api_request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetAtmRequest {
    @SerializedName("atm_code")
    @Expose
    var atm_code: String? = null

    @SerializedName("team_id")
    @Expose
    var team_id: String? = null

    @SerializedName("team_username")
    @Expose
    var team_username: String? = null
}