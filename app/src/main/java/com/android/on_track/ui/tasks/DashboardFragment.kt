package com.android.on_track.ui.tasks

import android.app.Activity
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.on_track.R
import com.android.on_track.data.tasks.Task
import com.android.on_track.data.tasks.TaskDatabase
import com.android.on_track.data.tasks.TaskRepository
import com.android.on_track.databinding.FragmentDashboardBinding
import com.android.on_track.ui.dashboard.DashboardViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {
    private lateinit var viewModel: DashboardViewModel
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView: RecyclerView = binding.recyclerView
        val addTaskButton: FloatingActionButton = binding.addTaskButton

        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayout.VERTICAL, false)

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            val tasksList = ArrayList<Task>(tasks.size)

            for (task in tasks) {
                val calendar = Calendar.getInstance()
                calendar.time = task.startDate

                val beginTime = calendar.time.time
                when (task.durationUnits) {
                    "Hours" -> calendar.add(Calendar.HOUR_OF_DAY, task.duration)
                    "Days" -> calendar.add(Calendar.DAY_OF_MONTH, task.duration)
                    "Weeks" -> calendar.add(Calendar.WEEK_OF_YEAR, task.duration)
                }
                val endTime = calendar.time.time

                if (endTime - Calendar.getInstance().time.time >= 0) {
                    // TODO: Get UsageStats on intervals smalled than an hour
                    val usageStatsManager = context!!.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                    val queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, beginTime, endTime)

                    for (usageStat in queryUsageStats) {
                        if (usageStat.packageName == task.appName) {
                            val minutes = usageStat.totalTimeInForeground.toInt() / 1000 / 60
                            val appUsage = if (task.goalUnits == "Minutes") {
                                minutes
                            } else { // if task.goalUnits == "Hours"
                                minutes / 60
                            }

                            if (appUsage != task.progress) {
                                viewModel.updateProgress(task.id, appUsage)
                            }
                        }
                    }
                }

                val applicationInfo = context!!.packageManager.getApplicationInfo(task.appName, PackageManager.GET_META_DATA)
                val strippedAppName = context!!.packageManager.getApplicationLabel(applicationInfo)

                val taskItem = Task(task.name, strippedAppName.toString(), task.increment, task.progress, task.goalUnits, task.goal, task.startDate, task.durationUnits, task.duration, task.id)
                tasksList.add(taskItem)
            }
            recyclerView.adapter = TaskAdapter(tasksList.toList())
        }

        addTaskButton.setOnClickListener {
            activityResultLauncher.launch(Intent(context, AddTaskActivity::class.java))
        }

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = TaskDatabase.getInstance(requireActivity())
        val dao = database.dao
        val repository = TaskRepository(dao)
        val viewModelFactory = DashboardViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[DashboardViewModel::class.java]

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent: Intent = result.data!!

                val name = intent.extras!!.getString(getString(R.string.name_key), "")
                val appName = intent.extras!!.getString(getString(R.string.app_name_key), "")
                val increment = intent.extras!!.getString(getString(R.string.increment_key), "Negative")
                val progress = intent.extras!!.getInt(getString(R.string.progress_key), 0)
                val goalUnits = intent.extras!!.getString(getString(R.string.goal_units_key), "Hours")
                val goal = intent.extras!!.getInt(getString(R.string.goal_key), 5)
                val startDate = intent.extras!!.getString(getString(R.string.start_date_key), SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().time))
                val durationUnits = intent.extras!!.getString(getString(R.string.duration_units_key), "Days")
                val duration = intent.extras!!.getInt(getString(R.string.duration_key), 10)

                val task = Task(name, appName, increment, progress, goalUnits, goal, SimpleDateFormat("dd/MM/yyyy HH:mm").parse(startDate) as Date, durationUnits, duration)
                viewModel.insert(task)

                Toast.makeText(requireContext(), "Task added", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Task discarded", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}