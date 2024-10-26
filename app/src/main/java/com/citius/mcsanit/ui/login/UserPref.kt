package com.citius.mcsanit.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.example.tes.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserPref (context: Context) {
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    //key
    val keyPref = "login"
    val userPrefUsername = "username"
    val userPrefEmail = "email"
    val userPrefFullname = "fullname"
    val userPrefToken = "token"

    init {
        sharedPreferences = context.getSharedPreferences(keyPref, Context.MODE_PRIVATE)
        editor = sharedPreferences?.edit()
    }

    fun Login(usernname: String, email: String, fullname: String, token: String){
        Log.e("ENAME", fullname)
        editor?.putString(userPrefUsername, usernname)
        editor?.putString(userPrefEmail, email)
        editor?.putString(userPrefFullname, fullname)
        editor?.putString(userPrefToken, token)
        editor?.apply()
    }

    fun Logout(){
        editor?.remove(userPrefUsername)
        editor?.remove(userPrefEmail)
        editor?.remove(userPrefFullname)
        editor?.remove(userPrefToken)
        editor?.apply()
    }

    val username: String?
        get() = sharedPreferences?.getString(userPrefUsername, null)

    val email: String?
        get() = sharedPreferences?.getString(userPrefEmail, null)

    val fullname: String?
        get() = sharedPreferences?.getString(userPrefFullname, null)

    val token: String?
        get() = sharedPreferences?.getString(userPrefToken, null)

    fun CekLoginToken() : Boolean{
        if (!token.isNullOrEmpty()){
            return true

        }else{
            return false
        }
    }

}