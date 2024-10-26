package com.citius.mcsanit.ui.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserResponse {
    @SerializedName("data")
    @Expose
    var data: User? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    class User {
        @SerializedName("username")
        @Expose
        var username: String? = null

        @SerializedName("password")
        @Expose
        var password: String? = null

        @SerializedName("email")
        @Expose
        var email: String? = null

        @SerializedName("phone")
        @Expose
        var phone: String? = null

        @SerializedName("fullname")
        @Expose
        var fullname: String? = null

        @SerializedName("token")
        @Expose
        var token: String? = null
    }
}