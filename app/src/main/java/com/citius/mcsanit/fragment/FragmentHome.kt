package com.citius.mcsanit.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.forEach
import androidx.core.view.marginBottom
import androidx.core.view.size
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.citius.mcsanit.MainActivity
import com.citius.mcsanit.R
import com.citius.mcsanit.api_request.GetLocationAddressNameRequest
import com.citius.mcsanit.api_response.GetDashboardDataResponse
import com.citius.mcsanit.api_response.GetLocationAddressNameResponse
import com.citius.mcsanit.card_adapter.CardAdapterTask
import com.citius.mcsanit.databinding.ActivityMainBinding
import com.citius.mcsanit.fragment.history.FragmentHistory
import com.citius.mcsanit.fragment.reschedule.FragmentFinishedReschedule
import com.citius.mcsanit.fragment.reschedule.FragmentReschedule
import com.citius.mcsanit.fragment.task.CalDistanceCallBack
import com.citius.mcsanit.fragment.task.FragmentFinishedTask
import com.citius.mcsanit.fragment.task.FragmentTask
import com.citius.mcsanit.fragment.task.GetChecklistJobResponse
import com.citius.mcsanit.fragment.task.TaskRequest
import com.citius.mcsanit.fragment.task.TaskResponse
import com.citius.mcsanit.ui.login.UserPref
import com.example.tes.ApiInterface
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentHome : Fragment() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var mainHandler: Handler

    private lateinit var view: View
    private lateinit var countFinishedTask: TextView
    private lateinit var countWaitingTask: TextView
    private lateinit var countFinishedReschedule: TextView
    private lateinit var countWaitingReschedule: TextView
    private lateinit var locationAddressName: TextView
    private lateinit var getDashboardDataResponse: GetDashboardDataResponse
    private lateinit var cardAdapter: CardAdapterTask
    private lateinit var listCard: RecyclerView

    private val updateLocation = object : Runnable {
        override fun run() {
            SyncFunc()
            mainHandler.postDelayed(this, 30000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainActivity = requireActivity() as MainActivity
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mainHandler = Handler(Looper.getMainLooper())

        view = inflater.inflate(R.layout.fragment_home, container, false)

        requireActivity().setTitle("Home")
        InitUI()

        SyncSizeContent(mainActivity)
        GetLocation()
        return view
    }

    override fun onStart() {
        super.onStart()
    }

    fun SyncSizeContent(mainActivity: MainActivity){
        val mainLinLayLayoutParam = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        Log.d("LTOOLBARHEIH", mainActivity.binding.toolbar.height.toString())
        mainLinLayLayoutParam.setMargins(0,mainActivity.binding.toolbar.height,0,mainActivity.binding.bottomBar.height)
        val main_frame = view.findViewById<FrameLayout>(R.id.main_frame)
        main_frame.layoutParams = mainLinLayLayoutParam
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateLocation)
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(updateLocation)
    }

    fun GetTodayTask(){
        val userPref = UserPref(this.requireActivity())
        val request = TaskRequest()
        request.username = userPref.username
        request.request_order = "get_open_task"
        ApiInterface.instance.get_task(request).enqueue(object : Callback<TaskResponse> {
            override fun onResponse(p0: Call<TaskResponse>, p1: Response<TaskResponse>) {
                if (p1.isSuccessful){
                    val stringResponse = p1.body()!!.data
                    Log.d("asdasd",stringResponse.toString())
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

    fun SyncFunc(){
        mainHandler.postDelayed(Runnable {
            try {
                if (ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    GetLocation()
                }
            }catch (e: Throwable){
                Log.d("GET_LOCATION", e.message.toString())
            }
            SetData()
            GetTodayTask()
            val mainActivity = requireActivity() as MainActivity
            mainActivity.CheckToken()
        }, 2000)
    }

    fun GetLocation(){
        if (ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }
        val location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {
            if(it != null){
                val request  = GetLocationAddressNameRequest()
                request.lat = it.latitude.toString()
                request.lon = it.longitude.toString()
                ApiInterface.instance.get_location_address_name(request).enqueue(object :
                    Callback<GetLocationAddressNameResponse> {
                    override fun onResponse(p0: Call<GetLocationAddressNameResponse>, p1: Response<GetLocationAddressNameResponse>) {
                        if (p1.isSuccessful){
                            Log.e("FROM_GET_LOCATION", p1.body().toString())
                            locationAddressName.text = p1.body()!!.address_name
                        }else{
                            Toast.makeText(requireContext(), "SERVER ERROR GET ADDRESS NAME", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(p0: Call<GetLocationAddressNameResponse>, p1: Throwable) {
                        Log.e("ERROR GET ADDRESS NAME", p1.toString())
                    }

                })

            }else{
                Log.d("LONLATES","GAGAL")
            }
        }
    }

    fun InitUI(){
        val mainActivity = requireActivity() as MainActivity
        countFinishedTask = view.findViewById(R.id.dashboard_task_finished_text)
        countWaitingTask = view.findViewById(R.id.dashboard_task_waiting_text)
        countFinishedReschedule = view.findViewById(R.id.dashboard_reschedule_finished_text)
        countWaitingReschedule = view.findViewById(R.id.dashboard_reschedule_waiting_text)
        locationAddressName = view.findViewById(R.id.dashboard_myaddress)

        view.findViewById<LinearLayout>(R.id.parent_finished_task).setOnClickListener{
            mainActivity.openFragment(FragmentFinishedTask(), "frag_finished_task", true)
        }
        view.findViewById<LinearLayout>(R.id.parent_open_task).setOnClickListener{
            mainActivity.openFragment(FragmentTask(), "frag_task", true)
        }
        view.findViewById<LinearLayout>(R.id.parent_finished_reschedule).setOnClickListener{
            mainActivity.openFragment(FragmentFinishedReschedule(), "frag_finished_reschedule", true)
        }
        view.findViewById<LinearLayout>(R.id.parent_open_reschedule).setOnClickListener{
            mainActivity.openFragment(FragmentReschedule(), "frag_reschedule", true)
        }

        //CURENT USER TASK
        listCard = view.findViewById(R.id.scrollViewData)
        cardAdapter = CardAdapterTask(arrayListOf())
        listCard.adapter = cardAdapter
    }

    fun SetDashboardData(){
        countFinishedTask.text = getDashboardDataResponse.data.total_finished_task.toString()
        countWaitingTask.text = getDashboardDataResponse.data.total_open_task
        countFinishedReschedule.text = getDashboardDataResponse.data.total_finished_reschedule
        countWaitingReschedule.text = getDashboardDataResponse.data.total_open_reschedule
    }

    fun SetData(){
        val userPref = UserPref(this.requireActivity())
        val request = TaskRequest()
        request.username = userPref.username
        ApiInterface.instance.get_dashboard_data(request).enqueue(object :
            Callback<GetDashboardDataResponse> {
            override fun onResponse(p0: Call<GetDashboardDataResponse>, p1: Response<GetDashboardDataResponse>) {
                if (p1.isSuccessful){
                    getDashboardDataResponse = p1.body()!!
                    SetDashboardData()
                }else{
                    Toast.makeText(requireContext(), "SERVER ERROR GET DATA DASHBOARD", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(p0: Call<GetDashboardDataResponse>, p1: Throwable) {
                Log.e("GETTASKAPIERROR", p1.toString())
            }

        })
    }

}