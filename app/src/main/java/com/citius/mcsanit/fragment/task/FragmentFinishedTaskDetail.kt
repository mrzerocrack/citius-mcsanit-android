package com.citius.mcsanit.fragment.task

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.citius.mcsanit.MainActivity
import com.citius.mcsanit.R
import com.citius.mcsanit.api_request.GetAtmRequest
import com.citius.mcsanit.api_request.StartingTaskRequest
import com.citius.mcsanit.api_response.GetAtmResponse
import com.citius.mcsanit.api_response.GetFinishedTaskDataResponse
import com.citius.mcsanit.ui.login.UserPref
import com.example.tes.ApiInterface
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentFinishedTaskDetail.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentFinishedTaskDetail : Fragment() {

    private lateinit var view:View
    private var task_id:Int = 0
    private lateinit var checklsitJobData: List<GetFinishedTaskDataResponse.ReportDataChecklistJob>
    private lateinit var checklsitJobDataImage: List<GetFinishedTaskDataResponse.ReportDataChecklistJobImage>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainActivity = requireActivity() as MainActivity
        view = inflater.inflate(R.layout.fragment_finished_task_detail, container, false)

        val data = arguments
        task_id = data!!.getInt("task_id")
        SyncSizeContent(mainActivity)
        SetDataAtm()
        return view
    }

    fun SyncSizeContent(mainActivity: MainActivity){
        val mainLinLayLayoutParam = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        mainLinLayLayoutParam.setMargins(0,0,0,mainActivity.binding.bottomBar.height)
        val main_frame = view.findViewById<FrameLayout>(R.id.main_frame)
        main_frame.layoutParams = mainLinLayLayoutParam

        view.findViewById<LinearLayout>(R.id.container_atm_info).setPadding(0,mainActivity.binding.toolbar.height,0,0)
    }

    fun SetDataAtm(){

        val request = StartingTaskRequest()
        val userPref = UserPref(requireContext())
        request.username = userPref.username
        request.task_id = task_id
        ApiInterface.instance.get_report_task(request).enqueue(object :
            Callback<GetFinishedTaskDataResponse> {
            override fun onResponse(p0: Call<GetFinishedTaskDataResponse>, p1: Response<GetFinishedTaskDataResponse>) {
                if (p1.isSuccessful){
                    val response_data = p1.body()!!
                    if (response_data.status == 1){
                        val report_data = response_data.data
                        checklsitJobData = response_data.report_data
                        checklsitJobDataImage = response_data.report_data_img
                        view.findViewById<TextView>(R.id.atm_detail_atm_code).text = "ATM ID : ${report_data.atm_code}"
                        view.findViewById<TextView>(R.id.atm_detail_task_date).text = ": ${report_data.task_date}"
                        view.findViewById<TextView>(R.id.atm_detail_area).text = ": ${report_data.area_name}"
                        view.findViewById<TextView>(R.id.atm_detail_city).text = ": ${report_data.city_name}"
                        view.findViewById<TextView>(R.id.atm_detail_city_detail_loc).text = ": ${report_data.city_detail_location_name}"
                        view.findViewById<TextView>(R.id.atm_detail_address).text = ": ${report_data.atm_address}"
                        view.findViewById<TextView>(R.id.atm_detail_start_working_time).text = " : ${report_data.start_working_time}"
                        view.findViewById<TextView>(R.id.atm_detail_finish_working_time).text = " : ${report_data.finish_working_time}"
                        view.findViewById<TextView>(R.id.atm_detail_duration).text = "(${report_data.duration})"
                        generate_ui_report()
                        generate_ui_report_image()
                    }else{
                        Toast.makeText(requireContext(), "ERROR GET REPORT DATA", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(requireContext(), "SERVER ERROR GET REPORT DATA", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(p0: Call<GetFinishedTaskDataResponse>, p1: Throwable) {
                Log.e("GETTASKAPIERROR", p1.toString())
            }

        })
    }

    fun generate_ui_report() {

        val layoutContainer = view.findViewById<LinearLayout>(R.id.parent_linear_report_data)
        layoutContainer.setPadding(50,50,50,50)

        val checklist_job_title_text = TextView(requireContext())
        checklist_job_title_text.text = "Checklist Form"
        checklist_job_title_text.textSize = 20f
        checklist_job_title_text.setPadding(10, 10, 10, 10)
        layoutContainer.addView(checklist_job_title_text)

        checklsitJobData.forEach {
            val containerLinearLayoutParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val containerLinearLayout = LinearLayout(requireContext())
            containerLinearLayout.layoutParams = containerLinearLayoutParam
            containerLinearLayout.orientation = LinearLayout.HORIZONTAL
            containerLinearLayout.setPadding(10, 10, 10, 10)

            val containerDetailJobLayoutParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val containerDetailJob = LinearLayout(requireContext())
            containerDetailJob.layoutParams = containerDetailJobLayoutParam
            containerDetailJob.orientation = LinearLayout.VERTICAL

            val checklist_job_name_text = TextView(requireContext())
            checklist_job_name_text.width = 300
            checklist_job_name_text.text = it.checklist_job_name
            checklist_job_name_text.setPadding(10, 10, 10, 10)
            containerLinearLayout.addView(checklist_job_name_text)

            val checklist_job_detail_name_text = TextView(requireContext())
            checklist_job_detail_name_text.text = it.checklist_job_detail_name
            checklist_job_detail_name_text.setPadding(10, 10, 10, 10)
            containerDetailJob.addView(checklist_job_detail_name_text)

            val checklist_job_remarks_text = TextView(requireContext())
            checklist_job_remarks_text.text = "note : ${it.checklist_job_remarks}"
            checklist_job_remarks_text.setPadding(10, 10, 10, 10)
            containerDetailJob.addView(checklist_job_remarks_text)

            containerLinearLayout.addView(containerDetailJob)

            layoutContainer.addView(containerLinearLayout)
        }
    }

    fun generate_ui_report_image() {

        val layoutContainer = view.findViewById<LinearLayout>(R.id.parent_linear_report_data)
        layoutContainer.setPadding(50,50,50,50)

        val checklist_job_title_text = TextView(requireContext())
        checklist_job_title_text.text = "Report Image"
        checklist_job_title_text.textSize = 20f
        checklist_job_title_text.setPadding(10, 40, 10, 10)
        layoutContainer.addView(checklist_job_title_text)
        checklsitJobDataImage.forEach {
            val containerLinearLayoutParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            containerLinearLayoutParam.gravity = Gravity.CENTER

            val containerLinearLayout = LinearLayout(requireContext())
            containerLinearLayout.gravity = Gravity.CENTER
            containerLinearLayout.layoutParams = containerLinearLayoutParam
            containerLinearLayout.orientation = LinearLayout.VERTICAL
            containerLinearLayout.setPadding(10, 10, 10, 10)

            val checklist_job_name_text = TextView(requireContext())
            checklist_job_name_text.text = it.name
            checklist_job_name_text.gravity = Gravity.CENTER
            containerLinearLayout.addView(checklist_job_name_text)

            val checklist_job_image_layout_param = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val checklist_job_image = ImageView(requireContext())
            checklist_job_image.setImageResource(R.drawable.ic_image_file)
            checklist_job_image.layoutParams = checklist_job_image_layout_param
            checklist_job_image.adjustViewBounds = true
            checklist_job_image.setBackgroundColor(resources.getColor(R.color.grey_camera_bg))
            containerLinearLayout.addView(checklist_job_image)

            layoutContainer.addView(containerLinearLayout)
            Picasso.get().load(it.img_url).error(R.drawable.ic_image_not_found).into(checklist_job_image);
        }
    }

}