package com.citius.mcsanit.api_response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetFinishedTaskDataResponse (
    val data: Data, val report_data:List<ReportDataChecklistJob>, val report_data_img:List<ReportDataChecklistJobImage>){

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    data class Data (
        var vendor_name: String?,
        var atm_code: String?,
        var area_name: String?,
        var city_name: String?,
        var city_detail_location_name: String?,
        var atm_address: String?,
        var id_pelanggan: String?,
        var start_working_time: String?,
        var finish_working_time: String?,
        var status: String?,
        var duration: String?,
        var task_date: String?,
    )

    data class ReportDataChecklistJob (
        var checklist_job_name: String?,
        var checklist_job_detail_name: String?,
        var checklist_job_remarks: String?,
    )

    data class ReportDataChecklistJobImage (
        var img_url: String?,
        var name: String?,
    )
}