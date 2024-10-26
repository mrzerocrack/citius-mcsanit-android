package com.citius.mcsanit.fragment.coverage

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
import com.citius.mcsanit.api_response.GetMyCoverageResponse
import com.citius.mcsanit.card_adapter.CardAdapterCoverage
import com.citius.mcsanit.fragment.FragmentHome
import com.citius.mcsanit.ui.login.UserPref
import com.example.tes.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentCoverage : Fragment() {

    private lateinit var view : View
    private lateinit var cardAdapter: CardAdapterCoverage
    private lateinit var listCard: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        requireActivity().setTitle("Coverage")
        view = inflater.inflate(R.layout.fragment_coverage, container, false)
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
        cardAdapter = CardAdapterCoverage(arrayListOf())
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
        ApiInterface.instance.get_my_coverage_atm(userPref.username).enqueue(object : Callback<GetMyCoverageResponse> {
            override fun onResponse(p0: Call<GetMyCoverageResponse>, p1: Response<GetMyCoverageResponse>) {
                if (p1.isSuccessful){
                    val stringResponse = p1.body()!!
                    if (stringResponse.status == 1){
                        cardAdapter.setData(stringResponse.data)
                    }else{
                        Toast.makeText(requireContext(), stringResponse.message, Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Log.e("GETTASKAPI","ERRORR")
                }
            }

            override fun onFailure(p0: Call<GetMyCoverageResponse>, p1: Throwable) {
                Log.e("GETTASKAPIERROR", p1.toString())
            }

        })
    }

}