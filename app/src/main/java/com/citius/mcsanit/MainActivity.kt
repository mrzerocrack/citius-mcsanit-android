package com.citius.mcsanit

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.citius.mcsanit.databinding.ActivityMainBinding
import com.citius.mcsanit.fragment.FragmentHome
import com.citius.mcsanit.fragment.FragmentProfile
import com.citius.mcsanit.fragment.coverage.FragmentCoverage
import com.citius.mcsanit.fragment.history.FragmentHistory
import com.citius.mcsanit.fragment.reschedule.FragmentReschedule
import com.citius.mcsanit.fragment.task.FragmentTask
import com.citius.mcsanit.ui.login.LoginActivity
import com.citius.mcsanit.ui.login.UserPref
import com.citius.mcsanit.ui.login.UserRequest
import com.citius.mcsanit.ui.login.UserResponse
import com.example.tes.ApiInterface
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var fragmentManager: FragmentManager
    lateinit var binding: ActivityMainBinding
    private lateinit var team_fullname: TextView
    var doublebacktoexit: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        InitUI()

        setSupportActionBar(binding.toolbar)

        var toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)

        binding.bottomNavigation.background = null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            setInnactiveAllNavMenu()
            when(item.itemId){
                R.id.bottom_menu_task_open -> openFragment(FragmentTask(), "frag_task", true)
                R.id.bottom_menu_task_history -> openFragment(FragmentHistory(), "frag_history", true)
                R.id.bottom_menu_task_reschedule -> openFragment(FragmentReschedule(), "frag_reschedule", true)
                R.id.bottom_menu_coverage -> openFragment(FragmentCoverage(), "frag_coverage", true)
            }
            true
        }
        fragmentManager = supportFragmentManager

        binding.fab.setOnClickListener{
            openFragment(FragmentHome(), "frag_home", true)
        }
        openFragment(FragmentHome(), "frag_home")
        setInnactiveAllNavMenu()

        askNotificationPermission()

        Log.e("onCreate_CALLED_MAINACTIVITY","onCreateMAINACT")
    }

    fun setInnactiveAllNavMenu(){
        binding.bottomNavigation.menu.forEach {
            if (it.itemId == 0){
                it.setChecked(true)
            }
        }
        binding.navView.menu.forEach {
            if (it.itemId == 0){
                it.setChecked(true)
            }
        }
        binding.navView.menu.setGroupCheckable(0,false, true)
        binding.bottomNavigation.menu.setGroupCheckable(0,false, true)
    }

    fun CekLogin(loginCallBack: LoginCallBack){
        val userPref = UserPref(this)
        val request = UserRequest()
        request.token = userPref.token
        ApiInterface.instance.login_token(request).enqueue(object : Callback<UserResponse> {
            override fun onResponse(p0: Call<UserResponse>, p1: Response<UserResponse>) {
                if (p1.isSuccessful){
                    val stringResponse = p1.body()
                    val status: Int = stringResponse!!.status as Int
                    if (status == 1){
                        userPref.Login(stringResponse!!.data?.username as String, stringResponse!!.data?.email as String, stringResponse!!.data?.fullname as String, stringResponse!!.data?.token as String)
                        loginCallBack.onSuccess()
                    }else{
                        Logout()
                        loginCallBack.onFailure("Invalid token login")
                    }
                }
            }

            override fun onFailure(p0: Call<UserResponse>, p1: Throwable) {
                Toast.makeText(this@MainActivity, "Cannot connect to the server", Toast.LENGTH_SHORT).show()
                loginCallBack.onFailure("Cannot Connect To Server")
            }

        })
    }

    fun get_dir():String{
        return "${externalMediaDirs.first()}/Pictures"
    }

    fun pop(){
        if (fragmentManager.backStackEntryCount != 0){
            CekLogin(object : LoginCallBack{
                override fun onSuccess() {
                    fragmentManager.popBackStack()
                }

                override fun onFailure(error: String) {
                }
            })

        }
    }

    fun InitUI(){
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val headerView: View = navigationView.getHeaderView(0)
        val userPref = UserPref(this)
        Log.d("tes_panggil_pref_mainacti",userPref.username.toString())
        team_fullname = headerView.findViewById(R.id.db_team_name)
        team_fullname.setText(userPref.fullname.toString())
    }

    fun Logout(){

        val userPref = UserPref(this)
        userPref.Logout()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        setInnactiveAllNavMenu()
        when(item.itemId){
            R.id.nav_profile -> openFragment(FragmentProfile(), "frag_profile", true)
            R.id.nav_logout -> Logout()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            doublebacktoexit = 0
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            if (fragmentManager.backStackEntryCount > 0){
                doublebacktoexit = 0
                pop()
            }else{
                if (doublebacktoexit == 1){
                    finishAffinity()
                }else{
                    doublebacktoexit += 1
                    Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun openFragment(fragment: Fragment, fragName: String, addToStack: Boolean = false){
        CekLogin(object : LoginCallBack{
            override fun onSuccess() {
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment_container, fragment)
                if (addToStack){
                    fragmentTransaction.addToBackStack(fragName)
                }else{
                    fragmentTransaction.disallowAddToBackStack()
                }
                fragmentTransaction.commit()
//        for (i in 0 until fragmentManager.backStackEntryCount){
//            Log.d("FRAGMENT_STACK", fragmentManager.getBackStackEntryAt(i).name.toString())
//        }
            }

            override fun onFailure(error: String) {
            }

        })
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("TIRASIMISUFIREBASE", "SDHSD")
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }else{
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("TOKEN_FIREBASE_FAILED", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result.toString()

                // Log and toast
                Log.d("TOKEN_FIREBASE", token)
                PutMyFBToken(token)
            })
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("TOKEN_FIREBASE_FAILED", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result.toString()

                // Log and toast
                Log.d("TOKEN_FIREBASE", token)
                PutMyFBToken(token)
            })
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    fun onMessageReceived(remoteMessage: RemoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("FIREBASE_MESSAGING_EVENT", "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FIREBASE_MESSAGING_EVENT", "Message data payload: ${remoteMessage.data}")

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.

                Log.d("BISAPAKEINI_1", "BISAPAKE INI")
            } else {
                // Handle message within 10 seconds
                Log.d("BISAPAKEINI_HANDLE_10", "BISAPAKE INI")
            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d("BISAPAKEINI_NOTIF_BODY", "Message Notification Body: ${it.body}")
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    fun PutMyFBToken(token: String, oauthToken: String? = null){

        val userPref= UserPref(this)
        val request = UserRequest()
        request.token = userPref.token
        request.firebase_token = token
        Log.e("PUT_TOKEN", token)
        ApiInterface.instance.put_firebase_token(request).enqueue(object : Callback<UserResponse>{
            override fun onResponse(p0: Call<UserResponse>, p1: Response<UserResponse>) {
                Log.d("RESULT_PUT_TOKEN", p1.body()?.message.toString())
            }

            override fun onFailure(p0: Call<UserResponse>, p1: Throwable) {
                Log.e("RESULT_PUT_TOKEN",  p1.toString())
            }
        })
    }

    fun CheckToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val accessToken = AccessToken()
//            lifecycleScope.launch(Dispatchers.IO){
//                var oAuthToken = accessToken.accessToken
//                PutMyFBToken(token.toString(), oAuthToken.toString())
//            }
            Log.e("TOKEN_1", token.toString())
            PutMyFBToken(token.toString())
        })
    }
}

interface LoginCallBack{
    fun onSuccess()

    fun onFailure(error: String)
}