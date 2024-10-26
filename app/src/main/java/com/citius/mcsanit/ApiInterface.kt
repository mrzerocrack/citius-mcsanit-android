package com.example.tes

import com.citius.mcsanit.LoginResponse
import com.citius.mcsanit.api_request.GetAtmRequest
import com.citius.mcsanit.api_request.GetLocationAddressNameRequest
import com.citius.mcsanit.api_request.StartingTaskRequest
import com.citius.mcsanit.api_request.SubmitRescheduleRequest
import com.citius.mcsanit.api_response.GetAtmResponse
import com.citius.mcsanit.api_response.GetDashboardDataResponse
import com.citius.mcsanit.api_response.GetFinishedTaskDataResponse
import com.citius.mcsanit.api_response.GetLocationAddressNameResponse
import com.citius.mcsanit.api_response.GetMyCoverageResponse
import com.citius.mcsanit.api_response.GetTaskResponse
import com.citius.mcsanit.api_response.StartingTaskResponse
import com.citius.mcsanit.api_response.SubmitReportTaskResponse
import com.citius.mcsanit.api_response.SubmitRescheduleResponse
import com.citius.mcsanit.fragment.task.GetChecklistJobResponse
import com.citius.mcsanit.fragment.task.TaskRequest
import com.citius.mcsanit.fragment.task.TaskResponse
import com.citius.mcsanit.ui.login.UserRequest
import com.citius.mcsanit.ui.login.UserResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiInterface {
    @GET("tes")
    fun getFile() : Call<LoginResponse>

    @GET("get_checklist_job_detail")
    fun get_checklist_job(@Query("task_id") task_id: Int?) : Call<GetChecklistJobResponse>

    @GET("get_my_coverage_atm")
    fun get_my_coverage_atm(@Query("username") task_id: String?) : Call<GetMyCoverageResponse>

    @GET("get_data_task")
    fun get_data_task(@Query("task_id") task_id: Int?) : Call<GetTaskResponse>

    @POST("get_atm_data_by_code")
    fun get_atm_data_by_code(
        @Body getAtmRequest: GetAtmRequest
    ) : Call<GetAtmResponse>

    @POST("get_location_address_name")
    fun get_location_address_name(
        @Body getLocationAddressNameRequest: GetLocationAddressNameRequest
    ) : Call<GetLocationAddressNameResponse>

    @POST("get_dashboard_data")
    fun get_dashboard_data(
        @Body getDashboardRequest: TaskRequest
    ) : Call<GetDashboardDataResponse>

    @POST("team_login")
    fun login(
        @Body userRequest: UserRequest
    ) : Call<UserResponse>

    @POST("update_user_team")
    fun update_user(
        @Body userRequest: UserRequest
    ) : Call<UserResponse>

    @POST("team_login_token")
    fun login_token(
        @Body userRequest: UserRequest
    ) : Call<UserResponse>

    @POST("put_firebase_token")
    fun put_firebase_token(
        @Body userRequest: UserRequest
    ) : Call<UserResponse>

    @POST("get_task")
    fun get_task(
        @Body taskRequest: TaskRequest
    ) : Call<TaskResponse>

    @Multipart
    @POST("submit_report_task")
    fun submit_report_task(
        @QueryMap data: Map<String, String>,
        @Part photo_kondisi_mesin : MultipartBody.Part,
        @Part photo_ruangan : MultipartBody.Part,
        @Part photo_lantai : MultipartBody.Part,
        @Part photo_tempat_sampah : MultipartBody.Part,
        @Part photo_kaca_ruangan : MultipartBody.Part,
        @Part photo_meteran_listrik : MultipartBody.Part
    ) : Call<SubmitReportTaskResponse>

    @POST("submit_reschedule")
    fun submit_reschedule(
        @Body submitRescheduleRequest: SubmitRescheduleRequest
    ) : Call<SubmitRescheduleResponse>

    @POST("start_task")
    fun start_task(
        @Body startingTaskRequest: StartingTaskRequest
    ) : Call<StartingTaskResponse>

    @POST("get_report_task")
    fun get_report_task(
        @Body startingTaskRequest: StartingTaskRequest
    ) : Call<GetFinishedTaskDataResponse>

    companion object {
        val intercepter = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().apply {
            this.addInterceptor(intercepter)
        }.build()
        val instance by lazy {
            Retrofit.Builder()
//                .baseUrl("https://mcsanit.citius.co.id/api/")
                .baseUrl("http://192.168.100.6/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface::class.java)
        }
    }
}