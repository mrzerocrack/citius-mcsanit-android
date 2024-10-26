package com.citius.mcsanit.ui.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserRequest {
    @SerializedName("username")
    @Expose
    var username: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("password_new")
    @Expose
    var password_new: String? = null

    @SerializedName("password")
    @Expose
    var password: String? = null

    @SerializedName("phone")
    @Expose
    var phone: String? = null

    @SerializedName("fullname")
    @Expose
    var fullname: String? = null

    @SerializedName("token")
    @Expose
    var token: String? = null

    @SerializedName("firebase_token")
    @Expose
    var firebase_token: String? = null

    @SerializedName("oauth_token")
    @Expose
    var oauth_token: String? = null
}