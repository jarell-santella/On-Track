package com.android.on_track.ui.home

import android.Manifest
import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.android.on_track.R
import com.android.on_track.data.FirebaseUserData
import com.android.on_track.data.FirebaseUserDataRepository
import com.android.on_track.databinding.FragmentHomeBinding
import com.android.on_track.ui.geofence.GeofenceListAdapter
import com.android.on_track.ui.loginregister.LoginRegisterViewModel
import com.android.on_track.ui.loginregister.LoginRegisterViewModelFactory
import com.android.on_track.ui.loginregister.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // TODO: Do not use LoginRegisterViewModel and LoginRegisterViewModelFactory
    private lateinit var viewModel: LoginRegisterViewModel

    private lateinit var textView: TextView
    private lateinit var signOutButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView = view.findViewById(R.id.text_home)
        signOutButton = view.findViewById(R.id.sign_out_button)

        val auth = Firebase.auth
        val db = Firebase.firestore
        val userData = FirebaseUserData(auth, db)
        val userDataRepository = FirebaseUserDataRepository(userData)
        // TODO: Do not use LoginRegisterViewModel and LoginRegisterViewModelFactory
        val viewModelFactory = LoginRegisterViewModelFactory(userDataRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[LoginRegisterViewModel::class.java]

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                if (user.isAnonymous == true) {
                    // they are using guest account
                    textView.text = "Anonymous"
                } else if (user.accountType == "Parent") {
                    // they are using parent account
                    textView.text = "Parent"
                } else if (user.accountType == "Child") {
                    // they are using child account
                    textView.text = "Child"
                }
            } else {
                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }

//        startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        getUsageStats(view)
    }

//    override fun onStart() {
//        super.onStart()
//
////        if (getGrantStatus()) {
//            getUsageStats()
////        } else {
////            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
////        }
//    }

    private fun getGrantStatus(): Boolean {
        val appOps = getApplicationContext<Context>().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            OPSTR_GET_USAGE_STATS, Process.myUid(), getApplicationContext<Context>().packageName
        )
        return if (mode == AppOpsManager.MODE_DEFAULT) {
            getApplicationContext<Context>()
                .checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED
        } else {
            mode == MODE_ALLOWED
        }
    }

    private fun getUsageStats(view: View) {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.HOUR_OF_DAY, -1)
        val startTime = calendar.timeInMillis

        val dateFormat = SimpleDateFormat("M-d-yyyy HH:mm:ss");
        Log.d("DEBUG: ", "_____________________________Range start: " + dateFormat.format(startTime))
        Log.d("DEBUG: ", "_____________________________Range end: " + dateFormat.format(endTime))

        val usageStatsManager = context!!.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)

        val appNameList = StringBuilder()
        val list = mutableListOf<AppInfo>()

        for (stat in queryUsageStats) {
            val appTime = stat.totalTimeInForeground
            val seconds = (appTime/1000)%60
            val minutes = (appTime/(1000*60))%60
            val hours = (appTime/(1000*60*60))

            if(!(hours == 0L && minutes == 0L && seconds == 0L)){
                val nameSep = stat.packageName.split('.')
                val shortenedAppName = nameSep.last()

                appNameList.append("Name: $shortenedAppName, hrs: $hours, mins: $minutes, secs: $seconds\n")

                list += AppInfo(stat.packageName, stat.totalTimeInForeground)
            }
        }
//        view.findViewById<TextView>(R.id.app_usage).text = appNameList.toString()

        val myListView = view.findViewById<ListView>(R.id.list_usage)
        val arrayAdapter = UsageListAdapter(requireActivity(), list)
        myListView.adapter = arrayAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}