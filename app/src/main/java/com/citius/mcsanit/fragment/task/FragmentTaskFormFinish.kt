package com.citius.mcsanit.fragment.task

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.citius.mcsanit.Helper
import com.citius.mcsanit.MainActivity
import com.citius.mcsanit.R
import com.citius.mcsanit.api_request.GetAtmRequest
import com.citius.mcsanit.api_request.StartingTaskRequest
import com.citius.mcsanit.api_request.SubmitRescheduleRequest
import com.citius.mcsanit.api_response.GetAtmResponse
import com.citius.mcsanit.api_response.GetTaskResponse
import com.citius.mcsanit.api_response.StartingTaskResponse
import com.citius.mcsanit.api_response.SubmitReportTaskResponse
import com.citius.mcsanit.api_response.SubmitRescheduleResponse
import com.citius.mcsanit.fragment.reschedule.FragmentReschedule
import com.citius.mcsanit.ui.login.UserPref
import com.example.tes.ApiInterface
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Timer
import java.util.TimerTask
import kotlin.math.roundToInt
import kotlin.properties.Delegates


class FragmentTaskFormFinish : Fragment() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var mainHandler: Handler
    private lateinit var view : View
    private lateinit var checklsitJobData: List<GetChecklistJobResponse.Data>

    lateinit var dialog: Dialog
    lateinit var startTaskBtn: Button
    lateinit var btnDialogConfirm: Button
    lateinit var rescheduleDateForm: CalendarView

    var taskIsStarted : Boolean = false

    var reschedule_data_form_input: String = ""

    private lateinit var loading : ProgressBar

    lateinit var iv_kondisi_mesin : ImageView
    lateinit var iv_ruangan : ImageView
    lateinit var iv_kondisi_lantai : ImageView
    lateinit var iv_tempat_sampah : ImageView
    lateinit var iv_kaca_ruangan : ImageView
    lateinit var iv_meteran_listrik : ImageView

    var file_kondisi_mesin : File = File("")
    var file_ruangan : File = File("")
    var file_kondisi_lantai : File = File("")
    var file_tempat_sampah : File = File("")
    var file_kaca_ruangan : File = File("")
    var file_meteran_listrik : File = File("")
    var task_time: Double = 0.0
    var task_start_time: Long = 0.toLong()
    private lateinit var task_timerText: TextView
    private lateinit var error_lokasi_text: TextView

    var my_lat: String = ""
    var my_lon: String = ""

    var open_camera_case: Int = 0

    var atm_code:String? = null
    var task_date:String? = null
    var task_id:Int? = null
    var manage_electric:Int? = null
    var atm_loc_lat:Double by Delegates.notNull<Double>()
    var atm_loc_lon:Double by Delegates.notNull<Double>()

    private val updateLocation = object : Runnable {
        override fun run() {
            SyncFunc()
            mainHandler.postDelayed(this, 30000)
        }
    }
    private val CountTimer = object : Runnable {
        override fun run() {
            StartTimer()
            mainHandler.postDelayed(this, 1000)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_task_form_finish, container, false)
        Log.e("ONSTART_CREATE","ONCREATE")

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mainHandler = Handler(Looper.getMainLooper())
        val data = arguments
        atm_code = data!!.getString("atm_code")
        task_date = data!!.getString("task_date")
        task_id = data!!.getInt("task_id")
        manage_electric = data!!.getInt("manage_electric")
        atm_loc_lat = data!!.getDouble("atm_loc_lat")
        atm_loc_lon = data!!.getDouble("atm_loc_lon")
        requireActivity().setTitle("Job Report")
        setupList()
        SetDataAtm()
        InitUI()

        CheckRadius(object: CalDistanceCallBack {
            override fun onSuccess() {
                error_lokasi_text.visibility = TextView.GONE
            }

            override fun onFailure(error: String) {
                error_lokasi_text.text = "${error}"
                error_lokasi_text.visibility = TextView.VISIBLE
            }

        })

        mainHandler.post(CountTimer)
        return view
    }

    fun SyncFunc(){

    }

    override fun onPause() {

        Log.e("onPause_CALLED","onPause")
        mainHandler.removeCallbacks(updateLocation)
        super.onPause()
    }

    override fun onResume() {

        Log.e("onResume_CALLED","onResume")
        mainHandler.post(updateLocation)
        super.onResume()
    }

    fun CheckRadius(calDistanceCallBack: CalDistanceCallBack){
        if (ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return calDistanceCallBack.onFailure("Kami tidak bisa menemukan lokasi anda. Silahkan buka Pengaturan Aplikasi dan izinkan Lokasi untuk aplikasi ini.")
        }
        val location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {
            if(it != null){
                my_lat = it.latitude.toString()
                my_lon = it.longitude.toString()
                var jarak = Helper.cal_distance(it.latitude, it.longitude, atm_loc_lat, atm_loc_lon)
                //var jarakFloat = String.format("%.2f", jarak).toFloat()//%.2f artinya cuma nampilin 2 angka setelah koma. contoh 0.99999 jadi 0.99
                var jarakInt = jarak.roundToInt()
                if (jarakInt < 200){
                    calDistanceCallBack.onSuccess()
                }else{
                    calDistanceCallBack.onFailure("Lokasi anda terlalu jauh dari Mesin")
                }

            }else{
                Log.d("LONLATES","GAGAL")
                calDistanceCallBack.onFailure("Anda harus mengizinkan IZIN LOKASI")
            }
        }


    }

    private fun InitUI() {
        loading = view.findViewById(R.id.loading)
        error_lokasi_text = view.findViewById(R.id.error_lokasi_text)
        task_timerText = view.findViewById(R.id.timer_text)
        task_timerText.visibility = TextView.GONE
        dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.reschedule_task_dialog)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)

        iv_kondisi_mesin = view.findViewById(R.id.iv_kondisi_mesin)
        iv_kondisi_mesin.setOnClickListener {
            open_camera_case = 1
            TakePicture()
        }
        iv_ruangan = view.findViewById(R.id.iv_ruangan)
        iv_ruangan.setOnClickListener {
            open_camera_case = 2
            TakePicture()
        }
        iv_kondisi_lantai = view.findViewById(R.id.iv_kondisi_lantai)
        iv_kondisi_lantai.setOnClickListener {
            open_camera_case = 3
            TakePicture()
        }
        iv_tempat_sampah = view.findViewById(R.id.iv_tempat_sampah)
        iv_tempat_sampah.setOnClickListener {
            open_camera_case = 4
            TakePicture()
        }
        iv_kaca_ruangan = view.findViewById(R.id.iv_kaca_ruangan)
        iv_kaca_ruangan.setOnClickListener {
            open_camera_case = 5
            TakePicture()
        }
        iv_meteran_listrik = view.findViewById(R.id.iv_meteran_listrik)
        iv_meteran_listrik.setOnClickListener {
            open_camera_case = 6
            TakePicture()
        }

        startTaskBtn = view.findViewById(R.id.start_task_button)
        startTaskBtn.setOnClickListener {
//            dialog.show()
            CheckRadius(object: CalDistanceCallBack {
                override fun onSuccess() {
                    StartTask()
                    error_lokasi_text.visibility = TextView.GONE
                }

                override fun onFailure(error: String) {
                    error_lokasi_text.text = "${error}"
                    error_lokasi_text.visibility = TextView.VISIBLE
                }
            })
        }

        rescheduleDateForm = dialog.findViewById(R.id.reschedule_date)
        rescheduleDateForm.setOnDateChangeListener{
                _, year, month, day ->
            val date = ("%02d".format(day) + "-" + "%02d".format((month+1)) + "-" + year)
            reschedule_data_form_input = date
        }
        btnDialogConfirm = dialog.findViewById(R.id.confirm_button)
        btnDialogConfirm.setOnClickListener{
            SubmitReschedule()
        }
        GetDataTask()
    }

    private fun StartTimer() {
        if (taskIsStarted) {
            startTaskBtn.visibility = Button.GONE
            task_time++
            var curent_time: Long = System.currentTimeMillis()/1000
            task_time = (curent_time - task_start_time).toDouble()
            if (task_time.roundToInt() > 0){
                task_timerText.visibility = TextView.VISIBLE
                task_timerText!!.text = getTimerText()
            }
        }else{
            startTaskBtn.visibility = Button.VISIBLE
        }
    }


    private fun getTimerText(): String {
        val rounded = Math.round(task_time).toInt()

        val seconds = ((rounded % 86400) % 3600) % 60
        val minutes = ((rounded % 86400) % 3600) / 60
        val hours = ((rounded % 86400) / 3600)

        return formatTime(seconds, minutes, hours)
    }

    private fun formatTime(seconds: Int, minutes: Int, hours: Int): String {
        return String.format("%02d", hours) + " : " + String.format(
            "%02d",
            minutes
        ) + " : " + String.format("%02d", seconds)
    }

    override fun onStart() {
        Log.e("ONSTART_CALLED","ONSTART")
        view.findViewById<FloatingActionButton>(R.id.submit_checklist_job).setOnClickListener{
            CheckRadius(object: CalDistanceCallBack {
                override fun onSuccess() {
                    error_lokasi_text.visibility = TextView.GONE
                    view.findViewById<FloatingActionButton>(R.id.submit_checklist_job).isEnabled = false
                    if (taskIsStarted){
                        if (task_time.roundToInt() > 297) {
                            OnSubmit()
                        }else{
                            view.findViewById<FloatingActionButton>(R.id.submit_checklist_job).isEnabled = true
                            Toast.makeText(requireContext(), "You need atleast 5 minutes to submit", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        view.findViewById<FloatingActionButton>(R.id.submit_checklist_job).isEnabled = true
                        Toast.makeText(requireContext(), "Please Start the Task first", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(error: String) {
                    error_lokasi_text.text = "${error}"
                    error_lokasi_text.visibility = TextView.VISIBLE
                }
            })
        }
        val mainActivity = requireActivity() as MainActivity

        SyncSizeContent(mainActivity)
        super.onStart()
    }



    val get_result_after_take_pic = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

        if (it.resultCode == 0){
            when(open_camera_case){
                1 -> iv_kondisi_mesin!!.setImageResource(R.drawable.ic_camera)
                2 -> iv_ruangan!!.setImageResource(R.drawable.ic_camera)
                3 -> iv_kondisi_lantai!!.setImageResource(R.drawable.ic_camera)
                4 -> iv_tempat_sampah!!.setImageResource(R.drawable.ic_camera)
                5 -> iv_kaca_ruangan!!.setImageResource(R.drawable.ic_camera)
                6 -> iv_meteran_listrik!!.setImageResource(R.drawable.ic_camera)
            }
            set_blank_file(open_camera_case)
        }
        if (it.resultCode == -1){
            when(open_camera_case){
                1 -> iv_kondisi_mesin!!.setImageURI(file_kondisi_mesin.toUri())
                2 -> iv_ruangan!!.setImageURI(file_ruangan.toUri())
                3 -> iv_kondisi_lantai!!.setImageURI(file_kondisi_lantai.toUri())
                4 -> iv_tempat_sampah!!.setImageURI(file_tempat_sampah.toUri())
                5 -> iv_kaca_ruangan!!.setImageURI(file_kaca_ruangan.toUri())
                6 -> iv_meteran_listrik!!.setImageURI(file_meteran_listrik.toUri())
            }
        }
        Log.d("PHOTOFILE TO ORU",file_kondisi_mesin.toUri().toString())
        Log.d("PHOTOFILE TO String",file_kondisi_mesin.toString())
        open_camera_case = 0
//        uploadFile(photoFile)
    }

    fun set_blank_file(caseId: Int){
        when(caseId){
            1 -> file_kondisi_mesin = File("")
            2 -> file_ruangan = File("")
            3 -> file_kondisi_lantai = File("")
            4 -> file_tempat_sampah = File("")
            5 -> file_kaca_ruangan = File("")
            6 -> file_meteran_listrik = File("")

        }
    }

    fun getFileFromUri(uri: Uri): File? {
        var resolver = requireActivity().contentResolver
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = resolver.query(uri, filePathColumn, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
        val filePath = cursor?.getString(columnIndex!!)
        cursor?.close()
        return filePath?.let { File(it) }
    }

    private fun TakePicture(){
        val pictureInten = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var fileStore: File = File("")
        when(open_camera_case){
            1 -> {
                file_kondisi_mesin = CreateImageFile()
                fileStore = file_kondisi_mesin
            }
            2 -> {
                file_ruangan = CreateImageFile()
                fileStore = file_ruangan
            }
            3 -> {
                file_kondisi_lantai = CreateImageFile()
                fileStore = file_kondisi_lantai
            }
            4 -> {
                file_tempat_sampah = CreateImageFile()
                fileStore = file_tempat_sampah
            }
            5 -> {
                file_kaca_ruangan = CreateImageFile()
                fileStore = file_kaca_ruangan
            }
            6 -> {
                file_meteran_listrik = CreateImageFile()
                fileStore = file_meteran_listrik
            }
        }
        val uri = FileProvider.getUriForFile(requireActivity(), "com.citius.mcsanit.provider", fileStore)
        pictureInten.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        get_result_after_take_pic.launch(pictureInten)
    }

    private fun CreateImageFile() : File {
        val timeStamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val mainActivity = requireActivity() as MainActivity
        Log.d("GET_DIR", mainActivity.get_dir())
        Log.d("FragmentTask", Environment.getExternalStorageDirectory().toString())
        Log.d("FragmentTask", Environment.DIRECTORY_PICTURES.toString())
        Log.d("FragmentTask", Environment.getExternalStorageDirectory().toString()+"/Android/media/com.citius.mcsanit")
        val storageDir = File(mainActivity.get_dir())

        if (!storageDir.exists()) {
            Log.d("NOT EXIST", storageDir.toString())
            storageDir.mkdirs()
        }
        Log.d("FragmentTaskS", File.createTempFile("capture_${timeStamp}_", ".png", storageDir).toString())
        return File.createTempFile("capture_${timeStamp}_", ".png", storageDir)
    }

    fun OnSubmit(){

//        Toast.makeText(requireContext(), "${value}", Toast.LENGTH_SHORT).show()
        val map = HashMap<String, String>()
        map["task_id"] = task_id.toString()
        map["longitude"] = my_lon.toString()
        map["latitude"] = my_lat.toString()
        checklsitJobData.forEach {
            if (it.checklist_job_data.size != 0){
                val checkedRadioId = view.findViewWithTag<RadioGroup>("radiTag${it.checklist_job_id}").checkedRadioButtonId
                if (checkedRadioId > 0){
                    val value = view.findViewById<RadioButton>(checkedRadioId)!!.text
                    map["checklist_job_id_${it.checklist_job_id.toString()}"] = value.toString()
                }

                val remarkInput = view.findViewWithTag<EditText>("remark${it.checklist_job_id}").text
                if (remarkInput.toString() == ""){
                    Log.d("asf6asf6as9f", "REMARK NULL");
                }else{
                    map["note_checklist_job_id_${it.checklist_job_id.toString()}"] = remarkInput.toString()
                    Log.d("asf6asf6as9f", view.findViewWithTag<EditText>("remark${it.checklist_job_id}").text.toString());
                }
            }

        }
        if (!file_kondisi_mesin.exists() || !file_ruangan.exists() || !file_kondisi_lantai.exists() || !file_tempat_sampah.exists() || !file_kaca_ruangan.exists()){
            Toast.makeText(requireContext(), "Make sure you take the picture to all required form", Toast.LENGTH_LONG).show()
            view.findViewById<FloatingActionButton>(R.id.submit_checklist_job).isEnabled = true
            return
        }
        val photo_kondisi_mesin = MultipartBody.Part.createFormData("photo_kondisi_mesin", file_kondisi_mesin.name, file_kondisi_mesin.asRequestBody("image/*".toMediaTypeOrNull()))
        val photo_ruangan = MultipartBody.Part.createFormData("photo_ruangan", file_ruangan.name, file_ruangan.asRequestBody("image/*".toMediaTypeOrNull()))
        val photo_lantai = MultipartBody.Part.createFormData("photo_lantai", file_kondisi_lantai.name, file_kondisi_lantai.asRequestBody("image/*".toMediaTypeOrNull()))
        val photo_tempat_sampah = MultipartBody.Part.createFormData("photo_tempat_sampah", file_tempat_sampah.name, file_tempat_sampah.asRequestBody("image/*".toMediaTypeOrNull()))
        val photo_kaca_ruangan = MultipartBody.Part.createFormData("photo_kaca_ruangan", file_kaca_ruangan.name, file_kaca_ruangan.asRequestBody("image/*".toMediaTypeOrNull()))
        var photo_meteran_listrik: MultipartBody.Part?= null
        if (manage_electric == 1){
            if (!file_meteran_listrik.exists()){
                Toast.makeText(requireContext(), "Make sure you take the picture for Meteran Listrik", Toast.LENGTH_LONG).show()
                view.findViewById<FloatingActionButton>(R.id.submit_checklist_job).isEnabled = true
                return
            }
            photo_meteran_listrik = MultipartBody.Part.createFormData("photo_meteran_listrik", file_meteran_listrik.name, file_meteran_listrik.asRequestBody("image/*".toMediaTypeOrNull()))
        }
        loading.visibility = ProgressBar.VISIBLE
        ApiInterface.instance.submit_report_task(map, photo_kondisi_mesin, photo_ruangan, photo_lantai, photo_tempat_sampah, photo_kaca_ruangan, photo_meteran_listrik).enqueue(object : Callback<SubmitReportTaskResponse> {
            override fun onResponse(p0: Call<SubmitReportTaskResponse>, p1: Response<SubmitReportTaskResponse>) {
                if (p1.isSuccessful){
                    val stringResponse = p1.body()!!
                    Toast.makeText(requireContext(), stringResponse.message.toString(), Toast.LENGTH_SHORT).show()
                    if (stringResponse.status == 1){
                        val mainActivity = requireActivity() as MainActivity
                        mainActivity.pop()
                    }
                }else{
                    Toast.makeText(requireContext(), "SERVER ERROR SUBMIT JOB REPORT", Toast.LENGTH_SHORT).show()
                }
                loading.visibility = ProgressBar.GONE
                view.findViewById<FloatingActionButton>(R.id.submit_checklist_job).isEnabled = true
            }

            override fun onFailure(p0: Call<SubmitReportTaskResponse>, p1: Throwable) {
                Log.e("SFJKHSJKFHS", p1.toString())
                view.findViewById<FloatingActionButton>(R.id.submit_checklist_job).isEnabled = true
                loading.visibility = ProgressBar.GONE
            }

        })
    }

    fun SubmitReschedule(){
        val userPref = UserPref(this.requireActivity())
        val request = SubmitRescheduleRequest()
        request.reschedule_date = reschedule_data_form_input
        request.username = userPref.username
        request.task_id = task_id
        ApiInterface.instance.submit_reschedule(request).enqueue(object : Callback<SubmitRescheduleResponse> {
            override fun onResponse(p0: Call<SubmitRescheduleResponse>, p1: Response<SubmitRescheduleResponse>) {
                if (p1.isSuccessful){
                    val stringResponse = p1.body()!!
                    if (stringResponse.status == 0){
                        Toast.makeText(requireContext(), stringResponse.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    if (stringResponse.status == 1){
                        val mainActivity = requireActivity() as MainActivity
                        Toast.makeText(requireContext(), stringResponse.message.toString(), Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        mainActivity.openFragment(FragmentReschedule(), "frag_reschedule", true)
                    }
                }else{
                    Toast.makeText(requireContext(), "SERVER ERROR SUBMIT RESCHEDULE", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(p0: Call<SubmitRescheduleResponse>, p1: Throwable) {
                Log.e("ERROR SUBMIT RESCHEDULE", p1.toString())
            }

        })
    }

    fun SyncSizeContent(mainActivity: MainActivity){
        val mainLinLayLayoutParam = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        Log.d("LTOOLBARHEIH", mainActivity.binding.toolbar.height.toString())
        mainLinLayLayoutParam.setMargins(0,0,0,mainActivity.binding.bottomBar.height)
        val main_frame = view.findViewById<FrameLayout>(R.id.main_frame)
        main_frame.layoutParams = mainLinLayLayoutParam

        view.findViewById<LinearLayout>(R.id.container_atm_info).setPadding(0,mainActivity.binding.toolbar.height,0,0)

    }

    fun SetDataAtm(){

        val request = GetAtmRequest()
        request.atm_code = atm_code
        ApiInterface.instance.get_atm_data_by_code(request).enqueue(object : Callback<GetAtmResponse> {
            override fun onResponse(p0: Call<GetAtmResponse>, p1: Response<GetAtmResponse>) {
                if (p1.isSuccessful){
                    val stringResponse = p1.body()!!.data
                    view.findViewById<TextView>(R.id.atm_detail_atm_code).text = "ATM ID : ${stringResponse.atm_code}"
                    view.findViewById<TextView>(R.id.atm_detail_task_date).text = ": ${task_date}"
                    view.findViewById<TextView>(R.id.atm_detail_area).text = ": ${stringResponse.area_name}"
                    view.findViewById<TextView>(R.id.atm_detail_city).text = ": ${stringResponse.city_name}"
                    view.findViewById<TextView>(R.id.atm_detail_city_detail_loc).text = ": ${stringResponse.city_detail_location_name}"
                    view.findViewById<TextView>(R.id.atm_detail_address).text = ": ${stringResponse.atm_address}"
                }else{
                    Toast.makeText(requireContext(), "SERVER ERROR GET ATM DATA BY CODE", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(p0: Call<GetAtmResponse>, p1: Throwable) {
                Log.e("GETTASKAPIERROR", p1.toString())
            }

        })
    }

    fun GetDataTask(){

        ApiInterface.instance.get_data_task(task_id).enqueue(object : Callback<GetTaskResponse> {
            override fun onResponse(p0: Call<GetTaskResponse>, p1: Response<GetTaskResponse>) {
                if (p1.isSuccessful){
                    val stringResponse = p1.body()!!.data
                    if (stringResponse!!.start_working_time_unix != null){
                        task_start_time = stringResponse!!.start_working_time_unix!!.toLong()
                        taskIsStarted = true
                        getData()
                    }
                }else{
                    Toast.makeText(requireContext(), "SERVER ERROR GET ATM DATA BY CODE", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(p0: Call<GetTaskResponse>, p1: Throwable) {
                Log.e("GETTASKAPIERROR", p1.toString())
            }

        })
    }

    private fun setupList(){
//        listCard = view.findViewById(R.id.scrollViewChecklistJob)
//        cardAdapter = CardAdapterChecklistJob(arrayListOf())
//        listCard.adapter = cardAdapter
    }

    fun generate_form(){

        val layoutContainer = view.findViewById<LinearLayout>(R.id.parent_linear_task_form_finish)
        Log.d("DATAFORACE_RESPONSE", checklsitJobData.toString())
        checklsitJobData.forEach {
            Log.d("DATAFORACE_NAME", it.checklist_job_name.toString())
            val checklistJobTitle = TextView(requireContext())
            checklistJobTitle.text = it.checklist_job_name.toString()
            checklistJobTitle.setTextSize(25.0f)
            layoutContainer.addView(checklistJobTitle)

            if (it.checklist_job_data.size != 0){
                val newLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                newLayoutParam.setMargins(15,0,0,40)
                val newLayout = LinearLayout(requireContext())
                newLayout.orientation = LinearLayout.VERTICAL
                newLayout.layoutParams = newLayoutParam
                val radiGroup = RadioGroup(requireContext())
                radiGroup.orientation = RadioGroup.VERTICAL
                radiGroup.tag = "radiTag${it.checklist_job_id}"

                it.checklist_job_data.forEach {
                    val checklistJobDetailRadio = RadioButton(requireContext())
                    checklistJobDetailRadio.text = it.name
                    radiGroup.addView(checklistJobDetailRadio)
                }
                newLayout.addView(radiGroup)
                val checklistJobDetailEditText = EditText(requireContext())
                checklistJobDetailEditText.setHint(R.string.remark_edit_text)
                checklistJobDetailEditText.tag = "remark${it.checklist_job_id}"
                newLayout.addView(checklistJobDetailEditText)
                layoutContainer.addView(newLayout)
            }
        }

    }

    private fun getData(){
        ApiInterface.instance.get_checklist_job(task_id).enqueue(object : Callback<GetChecklistJobResponse> {
            override fun onResponse(p0: Call<GetChecklistJobResponse>, p1: Response<GetChecklistJobResponse>) {
                if (p1.isSuccessful){
                    val stringResponse = p1.body()!!
                    if (stringResponse.status == 1){
                        checklsitJobData = p1.body()!!.data

                        generate_form()
                    }else{
                        val mainActivity = requireActivity() as MainActivity
                        mainActivity.pop()
                    }
                }else{
                    Toast.makeText(requireContext(), "SERVER ERROR GET DATA CHECKLIST JOB", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(p0: Call<GetChecklistJobResponse>, p1: Throwable) {
                Log.e("GETTASKAPIERROR", p1.toString())
            }

        })
    }

    fun StartTask(){

        val userPref = UserPref(this.requireActivity())
        val request = StartingTaskRequest()
        request.username = userPref.username
        request.task_id = task_id
        ApiInterface.instance.start_task(request).enqueue(object : Callback<StartingTaskResponse> {
            override fun onResponse(p0: Call<StartingTaskResponse>, p1: Response<StartingTaskResponse>) {
                if (p1.isSuccessful){
                    val stringResponse = p1.body()!!
                    Toast.makeText(requireContext(), stringResponse.message.toString(), Toast.LENGTH_SHORT).show()
                    if (stringResponse.status == 1){
                        taskIsStarted = true
                        getData()
                        task_start_time = stringResponse!!.start_working_time_unix!!.toLong()
                    }
                }else{
                    Toast.makeText(requireContext(), "SERVER ERROR SUBMIT RESCHEDULE", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(p0: Call<StartingTaskResponse>, p1: Throwable) {
                Log.e("ERROR SUBMIT RESCHEDULE", p1.toString())
            }

        })
    }

}
interface CalDistanceCallBack{
    fun onSuccess()

    fun onFailure(error: String)
}