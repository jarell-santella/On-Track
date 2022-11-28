package com.android.on_track.ui.home

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.on_track.R
import com.android.on_track.databinding.FragmentHomeBinding
import java.util.*


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    lateinit var  usageStats: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        usageStats = view.findViewById(R.id.usage_stats)
        if(false){
            showUsageStats()
        }
        else{
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))


            val startTime: Long = GregorianCalendar(2022, 0, 1).getTimeInMillis()
            val endTime: Long = GregorianCalendar(2022, 11, 25).getTimeInMillis()

            val usageStatsManager =
                context!!.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val queryUsageStats =
                usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)

            for (us in queryUsageStats) {
                Log.d("DEBUG: ", us.packageName + " = " + us.totalTimeInForeground)
            }

        }
    }

    private fun showUsageStats(){

    }

    private fun checkUsageStatsPermission(){
//        var appOpsManager: AppOpsManager? = null
//        var mode: Int = 0
//        appOpsManager = getSystemService(Context.APP_OPS_SERVICE)!! as AppOpsManager
//        mode = appOpsManager.checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
//        return mode == MODE_ALLOWED

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}