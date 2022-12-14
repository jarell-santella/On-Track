package com.android.on_track.ui.home

import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
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
import com.android.on_track.R
import com.android.on_track.data.FirebaseUserData
import com.android.on_track.data.FirebaseUserDataRepository
import com.android.on_track.databinding.FragmentHomeBinding
import com.android.on_track.ui.loginregister.LoginRegisterViewModel
import com.android.on_track.ui.loginregister.LoginRegisterViewModelFactory
import com.android.on_track.ui.loginregister.MainActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.ZonedDateTime
import java.util.*


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var m_view: View? = null

    // TODO: Do not use LoginRegisterViewModel and LoginRegisterViewModelFactory
    private lateinit var viewModel: LoginRegisterViewModel

    private lateinit var textView: TextView
    private lateinit var useTime: TextView
    private lateinit var signOutButton: Button
    private lateinit var barChart: BarChart


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        m_view = view
        textView = view.findViewById(R.id.text_home)
        useTime = view.findViewById(R.id.daily_use_time)
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
                    textView.text = "Account: Anonymous"
                } else if (user.accountType == "Parent") {
                    // they are using parent account
                    textView.text = "Account: Parent"
                } else if (user.accountType == "Child") {
                    // they are using child account
                    textView.text = "Account: Child"
                }
            } else {
                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }

        initBarChart()
    }

    override fun onStart() {
        super.onStart()

        if (getGrantStatus()) {
            getUsageStats()
            showBarChart()
        } else {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }

    private fun initBarChart() {
        barChart = m_view!!.findViewById(R.id.barChart_view);

        barChart.setDrawGridBackground(false)
        barChart.setDrawBarShadow(false)
        barChart.setDrawBorders(false)

        val description = Description()
        description.isEnabled = false
        barChart.description = description

        barChart.animateY(1000)
        barChart.animateX(1000)

        val xAxis = barChart.xAxis
        //change the position of x-axis to the bottom
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        //set the horizontal distance of the grid line
        xAxis.granularity = 1f
        //hiding the x-axis line, default true if not set
        xAxis.setDrawAxisLine(false)
        //hiding the vertical grid lines, default true if not set
        xAxis.setDrawGridLines(false)

        //hiding the left y-axis line
        val leftAxis = barChart.axisLeft
        leftAxis.setDrawAxisLine(false)
        //hiding the right y-axis line
        val rightAxis = barChart.axisRight
        rightAxis.setDrawAxisLine(false)

        // Hide the legend
        barChart.legend.isEnabled = false;
    }

    private fun showBarChart() {
        val valueList = ArrayList<Double>()
        val entries: ArrayList<BarEntry> = ArrayList()

        //input data
        for (i in 6.downTo(0)) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val d = getTotalTimeForDay (calendar)
            valueList.add(d.toMinutes().toDouble() / 60)
        }

        //fit the data into a bar
        val labelsNames = ArrayList<String>();
        for (i in valueList.size - 1 downTo 0) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val day = calendar.time.toString().split(' ')[0]

            val barEntry = BarEntry(i.toFloat(), valueList[i].toFloat())
            entries.add(barEntry)
            labelsNames.add(day)
        }

        val barDataSet = BarDataSet(entries, "Title")
        val data = BarData(barDataSet)
        barChart.data = data
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labelsNames)
        barChart.invalidate()

        barDataSet.color = Color.parseColor("#304567");
        barDataSet.formSize = 15f;
        barDataSet.setDrawValues(false);
        barDataSet.valueTextSize = 12f;
    }

    private fun getGrantStatus(): Boolean {
        return try {
            val packageManager = context!!.packageManager
            val applicationInfo = packageManager.getApplicationInfo(context!!.packageName, 0)
            val appOpsManager = context!!.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = appOpsManager.checkOpNoThrow(
                OPSTR_GET_USAGE_STATS,
                applicationInfo.uid,
                applicationInfo.packageName
            )
            mode == MODE_ALLOWED
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun getTotalTimeForDay(calendar: Calendar): Duration {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endTime = calendar.timeInMillis

        val usageStatsManager =
            context!!.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val stats = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime)

        return Duration.ofMillis(stats.values.sumOf { it.totalTimeInForeground })
    }

    private fun getUsageStats() {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.HOUR_OF_DAY, -1)
        val startTime = calendar.timeInMillis

        val dateFormat = SimpleDateFormat("M-d-yyyy HH:mm:ss");
        Log.d("DEBUG: ", "_____________________________Range start: " + dateFormat.format(startTime))
        Log.d("DEBUG: ", "_____________________________Range end: " + dateFormat.format(endTime))


        val usageStatsManager = context!!.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
        val total = getTotalTimeForDay(Calendar.getInstance())

        useTime.text = "App usage time for today: ${String.format("%.3f", total.toMinutes().toDouble() / 60).toDouble()} hrs"
        Log.d("DEBUG: ", "YOU SPENT ${total.toMinutes()} mins.")

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

        val myListView = view!!.findViewById<ListView>(R.id.list_usage)
        val arrayAdapter = UsageListAdapter(requireActivity(), list)
        myListView.adapter = arrayAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}