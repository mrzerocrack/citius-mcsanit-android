package com.citius.mcsanit.fragment.task

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.replace
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.citius.mcsanit.MainActivity
import com.citius.mcsanit.R
import com.citius.mcsanit.card_adapter.CardAdapterFinishedTask
import com.citius.mcsanit.card_adapter.CardAdapterTask
import com.citius.mcsanit.fragment.FragmentHome
import com.citius.mcsanit.ui.login.UserPref
import com.example.tes.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentFinishedTask : Fragment() {

    private lateinit var view : View
    private lateinit var cardAdapter: CardAdapterFinishedTask
    private lateinit var listCard: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        requireActivity().setTitle("Finished Task")
        view = inflater.inflate(R.layout.fragment_finished_task, container, false)
        setupList()
        getData()
        return view

    }

    override fun onStart() {
        val mainActivity = requireActivity() as MainActivity

        SyncSizeContent(mainActivity)
        super.onStart()
    }


    private fun setupList(){
        listCard = view.findViewById(R.id.scrollViewData)
        cardAdapter = CardAdapterFinishedTask(arrayListOf())
        listCard.adapter = cardAdapter
    }

    fun SyncSizeContent(mainActivity: MainActivity){
        val mainLinLayLayoutParam = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        Log.d("LTOOLBARHEIH", mainActivity.binding.toolbar.height.toString())
        mainLinLayLayoutParam.setMargins(0,mainActivity.binding.toolbar.height,0,mainActivity.binding.bottomBar.height)
        val main_frame = view.findViewById<FrameLayout>(R.id.main_frame)
        main_frame.layoutParams = mainLinLayLayoutParam
    }

    private fun getData(){
        val userPref = UserPref(this.requireActivity())
        val request = TaskRequest()
        request.username = userPref.username
        request.request_order = "get_finished_task"
        ApiInterface.instance.get_task(request).enqueue(object : Callback<TaskResponse> {
            override fun onResponse(p0: Call<TaskResponse>, p1: Response<TaskResponse>) {
                if (p1.isSuccessful){
                    val stringResponse = p1.body()!!.data
                    cardAdapter.setData(stringResponse)
                }else{
                    Log.e("GETTASKAPI","ERRORR")
                }
            }

            override fun onFailure(p0: Call<TaskResponse>, p1: Throwable) {
                Log.e("GETTASKAPIERROR", p1.toString())
            }

        })
    }

}