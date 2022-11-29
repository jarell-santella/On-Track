package com.android.on_track.ui.geofence

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.on_track.R
import com.android.on_track.databinding.FragmentGeofenceBinding
import com.android.on_track.geofenceDB.*
import com.google.android.gms.maps.model.LatLng
import java.util.ArrayList

class GeofenceFragment : Fragment() {
    private var _binding: FragmentGeofenceBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var addButton: Button
    private lateinit var deleteButton: Button
    private lateinit var deleteAllButton: Button
    private lateinit var myListView: ListView

    private lateinit var arrayList: ArrayList<GeofenceEntry>
    private lateinit var arrayAdapter: GeofenceListAdapter

    private lateinit var database: GeofenceDatabase
    private lateinit var databaseDao: GeofenceDatabaseDao
    private lateinit var repository: GeofenceRepository
    private lateinit var viewModelFactory: GeofenceViewModelFactory
    private lateinit var historyViewModel: GeofenceViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGeofenceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myListView = view.findViewById(R.id.listView_geofence)
        addButton = view.findViewById(R.id.btn_addNew)
        deleteButton = view.findViewById(R.id.btn_deleteFirst)
        deleteAllButton = view.findViewById(R.id.btn_deleteAll)

        arrayList = ArrayList()
        arrayAdapter = GeofenceListAdapter(requireActivity(), arrayList)
        myListView.adapter = arrayAdapter

        database = GeofenceDatabase.getInstance(requireActivity())
        databaseDao = database.geofenceDatabaseDao
        repository = GeofenceRepository(databaseDao)
        viewModelFactory = GeofenceViewModelFactory(repository)
        historyViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[GeofenceViewModel::class.java]

        historyViewModel.allGeofenceEntriesLiveData.observe(requireActivity()) {
            arrayAdapter.replace(it)
            arrayAdapter.notifyDataSetChanged()
        }

        myListView.setOnItemClickListener { parent, _, pos, _ ->
            val entry = parent.getItemAtPosition(pos) as GeofenceEntry
            Toast.makeText(requireActivity(), "Clicked on pos: $pos, at location: ${entry.location}", Toast.LENGTH_SHORT).show()
//            val intent: Intent
//
//            when (entry.inputType) {
//                0 -> {
//                }
//                1 -> {
//                }
//                else -> intent = Intent(requireActivity(), HistoryEntryActivity::class.java)
//            }
//
//            result.launch(intent)
        }

//        result = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
//            if(result.resultCode == Activity.RESULT_OK ) {
//                val index = result.data?.getIntExtra("return_index", -1) as Int
//                if (index > -1) {
//                    historyViewModel.deletePosition(index)
//                }
//            }
//        }

        addButton.setOnClickListener {
            val geofenceEntry = GeofenceEntry()

            geofenceEntry.entry_name = "Science World"
            geofenceEntry.location = LatLng(69.2734, -123.1038)
            geofenceEntry.geofence_radius = 10.0

            historyViewModel.insert(geofenceEntry)
        }

        deleteButton.setOnClickListener {
            historyViewModel.deleteFirst()
        }

        deleteAllButton.setOnClickListener {
            historyViewModel.deleteAll()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}