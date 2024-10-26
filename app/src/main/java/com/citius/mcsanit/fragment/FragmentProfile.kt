package com.citius.mcsanit.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.citius.mcsanit.MainActivity
import com.citius.mcsanit.R
import com.citius.mcsanit.ui.login.UserPref
import com.citius.mcsanit.ui.login.UserRequest
import com.citius.mcsanit.ui.login.UserResponse
import com.example.tes.ApiInterface
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentProfile : Fragment() {


    lateinit var et_username : EditText
    lateinit var et_fullname : EditText
    lateinit var et_email : EditText
    lateinit var et_phone : EditText
    lateinit var et_password_new : TextInputEditText
    lateinit var btn_submit : Button
    lateinit var dialog_et_password : TextInputEditText
    lateinit var dialog_btn_confirm : Button
    lateinit var dialog: Dialog
    lateinit var loading: ProgressBar
    private lateinit var view : View


    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false)
        requireActivity().setTitle("Profile")
        InitUI()
        SetProfileData()
        return view
    }

    fun DisableInput(){
        loading.visibility = ProgressBar.VISIBLE
        et_fullname.isEnabled = false
        et_email.isEnabled = false
        et_phone.isEnabled = false
        et_password_new.isEnabled = false
        btn_submit.isEnabled = false
        dialog_btn_confirm.isEnabled = false
    }

    fun EnableInput(){
        loading.visibility = ProgressBar.GONE
        et_fullname.isEnabled = true
        et_email.isEnabled = true
        et_phone.isEnabled = true
        et_password_new.isEnabled = true
        btn_submit.isEnabled = true
        dialog_btn_confirm.isEnabled = true
    }

    private fun SetProfileData() {
        DisableInput()
        val mainActivity = requireActivity() as MainActivity
        val userPref = UserPref(requireActivity())
        val request = UserRequest()
        request.token = userPref.token
        ApiInterface.instance.login_token(request).enqueue(object : Callback<UserResponse> {
            override fun onResponse(p0: Call<UserResponse>, p1: Response<UserResponse>) {
                if (p1.isSuccessful){
                    val stringResponse = p1.body()
                    val status: Int = stringResponse!!.status as Int
                    if (status == 1){
                        et_username.setText(stringResponse?.data?.username)
                        et_fullname.setText(stringResponse?.data?.fullname)
                        et_email.setText(stringResponse?.data?.email)
                        et_phone.setText(stringResponse?.data?.phone)
                    }else{
                        Toast.makeText(requireContext(), stringResponse.message, Toast.LENGTH_SHORT).show()
                        mainActivity.Logout()
                    }
                    EnableInput()
                }
            }

            override fun onFailure(p0: Call<UserResponse>, p1: Throwable) {
                Toast.makeText(requireContext(), "Cannot connect to the server", Toast.LENGTH_SHORT).show()
                EnableInput()
            }

        })
    }

    fun InitUI(){
        loading = view.findViewById(R.id.loading)
        dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.profile_update_dialog)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        et_username = view.findViewById(R.id.profile_et_username)
        et_fullname = view.findViewById(R.id.profile_et_fullname)
        et_email = view.findViewById(R.id.profile_et_email)
        et_phone = view.findViewById(R.id.profile_et_phone)
        et_password_new = view.findViewById(R.id.profile_et_password)
        btn_submit = view.findViewById(R.id.submit_button)
        dialog_et_password = dialog.findViewById(R.id.profile_dialog_password)
        dialog_btn_confirm = dialog.findViewById(R.id.confirm_button)
        RegisListener()
    }
    fun RegisListener(){
        dialog_btn_confirm.setOnClickListener {
            UpdateProfile()
        }
        btn_submit.setOnClickListener {
            dialog.show()
        }
    }

    private fun UpdateProfile() {
        DisableInput()
        val userPref = UserPref(requireActivity())
        val request = UserRequest()
        request.username = userPref.username
        request.password = dialog_et_password.text.toString()
        request.password_new = et_password_new.text.toString()
        request.email = et_email.text.toString()
        request.phone = et_phone.text.toString()
        request.fullname = et_fullname.text.toString()
        ApiInterface.instance.update_user(request).enqueue(object : Callback<UserResponse> {
            override fun onResponse(p0: Call<UserResponse>, p1: Response<UserResponse>) {
                if (p1.isSuccessful){
                    val stringResponse = p1.body()
                    val status: Int = stringResponse!!.status as Int
                    if (status == 1){
                        Toast.makeText(requireActivity(), stringResponse?.message, Toast.LENGTH_SHORT).show()
                        et_password_new.setText("")
                        dialog_et_password.setText("")
                        dialog.dismiss()
                    }else{
                        Toast.makeText(requireContext(), stringResponse.message, Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(requireContext(), "ERROR SERVER UPDATE USERr", Toast.LENGTH_SHORT).show()
                }
                EnableInput()
            }

            override fun onFailure(p0: Call<UserResponse>, p1: Throwable) {
                Toast.makeText(requireContext(), "ERROR UPDATE USERr", Toast.LENGTH_SHORT).show()
                EnableInput()
            }

        })
    }

    override fun onStart() {

        val mainActivity = requireActivity() as MainActivity

        SyncSizeContent(mainActivity)
        super.onStart()
    }

    fun SyncSizeContent(mainActivity: MainActivity){
        val mainLinLayLayoutParam = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        Log.d("LTOOLBARHEIH", mainActivity.binding.toolbar.height.toString())
        mainLinLayLayoutParam.setMargins(0,mainActivity.binding.toolbar.height,0,mainActivity.binding.bottomBar.height)
        val main_frame = view.findViewById<FrameLayout>(R.id.main_frame)

        main_frame.layoutParams = mainLinLayLayoutParam

    }
}