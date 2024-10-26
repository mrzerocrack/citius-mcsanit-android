package com.citius.mcsanit.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.citius.mcsanit.LoginResponse
import com.citius.mcsanit.R
import com.example.tes.ApiInterface
import com.google.android.gms.location.FusedLocationProviderClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class FragmentTaskJobCheck : Fragment() {


    lateinit var text : TextView
    lateinit var take_photo : TextView
    lateinit var photo_view : ImageView
    lateinit var photoFile : File
    private lateinit var view : View
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val file = getFileFromUri(uri)
            Log.d("PhotoPicker", "asd0")
            if (file == null) {
                Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }
            Log.d("PhotoPicker", "asd1")

        }
    }
    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_task_job_check, container, false)
        InitUI()
        return view
    }
    fun InitUI(){
        text = view.findViewById(R.id.text_main)
        photo_view = view.findViewById(R.id.photo_prev)
        take_photo = view.findViewById(R.id.take_photo)
        RegisListener()
    }
    fun RegisListener(){
        text.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//            val i = Intent(this, MainActivity2::class.java)
//            startActivity(i)
        }
        take_photo.setOnClickListener {
            TakePicture()
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

    val get_result_after_take_pic = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        Log.d("PHOTOFILE TO ORU",photoFile.toUri().toString())
        Log.d("PHOTOFILE TO String",photoFile.toString())
        photo_view!!.setImageURI(photoFile.toUri())
    }

    private fun TakePicture(){
        val pictureInten = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = CreateImageFile()
        val uri = FileProvider.getUriForFile(requireActivity(), "com.citius.mcsanit.provider", photoFile)
        Log.d("FileProviderTas",uri.toString())
        pictureInten.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        get_result_after_take_pic.launch(pictureInten)
    }

    private fun CreateImageFile() : File {
        val timeStamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        Log.d("FragmentTask", Environment.getExternalStorageDirectory().toString())
        Log.d("FragmentTask", Environment.DIRECTORY_PICTURES.toString())
        Log.d("FragmentTask", Environment.getExternalStorageDirectory().toString()+"/Android/media/com.citius.mcsanit")
        val storageDir = File(Environment.getExternalStorageDirectory().toString()+"/Android/media/com.citius.mcsanit/Pictures")

        if (!storageDir.exists()) {
            Log.d("NOT EXIST", storageDir.toString())
            storageDir.mkdirs()
        }
        Log.d("FragmentTaskS", File.createTempFile("capture_${timeStamp}_", ".png", storageDir).toString())
        return File.createTempFile("capture_${timeStamp}_", ".png", storageDir)
    }
}