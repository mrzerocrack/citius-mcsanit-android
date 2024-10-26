package com.citius.mcsanit.ui.login

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import com.citius.mcsanit.LoginResponse
import com.citius.mcsanit.MainActivity
import com.citius.mcsanit.databinding.ActivityLoginBinding

import com.citius.mcsanit.R
import com.example.tes.ApiInterface
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class LoginActivity : AppCompatActivity() {
    lateinit var username : EditText
    lateinit var password : EditText
    lateinit var loginBtn : Button
    lateinit var loading : ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_login)
        val userPref = UserPref(this)
        InitUI()
        RegisListener()
        if (!userPref.token.isNullOrEmpty()){
            LoginToken(userPref.token.toString())
        }
    }

    override fun onStart() {
        Log.d("THIS_IS_LOGIN", "FROM LOGIN")
        super.onStart()
    }

    fun GoToDashboard(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun DisableInput(){
        loading.visibility = ProgressBar.VISIBLE
        username.isEnabled = false
        password.isEnabled = false
        loginBtn.isEnabled = false
    }

    fun EnableInput(){
        loading.visibility = ProgressBar.GONE
        username.isEnabled = true
        password.isEnabled = true
        loginBtn.isEnabled = true
    }

    fun InitUI(){
        loading = findViewById(R.id.loading)
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        loginBtn = findViewById(R.id.login_btn)
    }
    fun RegisListener(){
        loginBtn.setOnClickListener {
            Login()
        }
    }

    private fun Login(){
        DisableInput()
        if (username.text.isNullOrEmpty()){
            Toast.makeText(this, "Username cant be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.text.isNullOrEmpty()){
            Toast.makeText(this, "Password cant be empty", Toast.LENGTH_SHORT).show()
            return
        }
        val request = UserRequest()
        request.username = username.text.toString().trim()
        request.password = password.text.toString().trim()
        ApiInterface.instance.login(request).enqueue(object : Callback<UserResponse> {
            override fun onResponse(p0: Call<UserResponse>, p1: Response<UserResponse>) {
                if (p1.isSuccessful){
                    val stringResponse = p1.body()
                    val status: Int = stringResponse!!.status as Int
                    if (status == 1){
                        val userPref = UserPref(this@LoginActivity)
                        userPref.Login(username.text.toString().trim(), stringResponse!!.data?.email as String, stringResponse!!.data?.fullname as String, stringResponse!!.data?.token as String)
                        GoToDashboard()
                    }else{
                        EnableInput()
                        Toast.makeText(this@LoginActivity, stringResponse!!.message?.toString(), Toast.LENGTH_SHORT).show()
                    }
//                    Log.d("LoginCoba", "${stringResponse!!.data?.email}")
//                    Log.d("LoginCoba", "${stringResponse!!.data?.token}")
                }else{
                    EnableInput()
                    Log.e("LoginCoba","ERRORR")
                }
            }

            override fun onFailure(p0: Call<UserResponse>, p1: Throwable) {
                EnableInput()
                Log.e("LoginCoba", p1.toString())
            }

        })
    }
    private fun LoginToken(token: String){
        DisableInput()
        val request = UserRequest()
        request.token = token.trim()
        ApiInterface.instance.login_token(request).enqueue(object : Callback<UserResponse> {
            override fun onResponse(p0: Call<UserResponse>, p1: Response<UserResponse>) {
                if (p1.isSuccessful){
                    val stringResponse = p1.body()
                    val status: Int = stringResponse!!.status as Int
                    if (status == 1){
                        val userPref = UserPref(this@LoginActivity)
                        userPref.Login(stringResponse!!.data?.username as String, stringResponse!!.data?.email as String, stringResponse!!.data?.fullname as String, stringResponse!!.data?.token as String)
                        GoToDashboard()
                    }else{
                        EnableInput()
                        Toast.makeText(this@LoginActivity, stringResponse!!.message?.toString(), Toast.LENGTH_SHORT).show()
                    }
//                    Log.d("LoginCoba", "${stringResponse!!.data?.email}")
//                    Log.d("LoginCoba", "${stringResponse!!.data?.token}")
                }else{
                    EnableInput()
                    Log.e("LoginCoba","ERRORR")
                }
            }

            override fun onFailure(p0: Call<UserResponse>, p1: Throwable) {
                EnableInput()
                Log.e("LoginCoba", p1.toString())
            }

        })
    }
}