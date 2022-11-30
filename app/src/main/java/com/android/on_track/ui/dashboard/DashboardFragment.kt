package com.android.on_track.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.on_track.data.Task
import com.android.on_track.databinding.FragmentDashboardBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // TODO: This needs to be used later on when Room database for tasks is implemented
        val dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView: RecyclerView = binding.recyclerView
        val addTaskButton: FloatingActionButton = binding.addTaskButton

        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayout.VERTICAL, false)
        // TODO: This will be from Room database for tasks instead
        //  Need ViewModel for this class to get that from Room database which also needs to be made still
        val taskList = ArrayList<Task>()
        taskList.add(Task("Task Name 1", "Task Description 1", "0/1 months"))
        taskList.add(Task("Task Name 2", "Task Description 2", "1/2 weeks"))
        taskList.add(Task("Task Name 3", "Task Description 3", "2/7 days"))
        taskList.add(Task("Task Name 4", "Task Description 4", "3/10 times"))

        recyclerView.adapter = TaskAdapter(taskList)

        addTaskButton.setOnClickListener {
            Toast.makeText(requireContext(), "Add Task button clicked", Toast.LENGTH_SHORT).show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}