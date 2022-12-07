package com.android.on_track.ui.geofence

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.on_track.R
import com.android.on_track.Util
import com.android.on_track.data.geofenceDB.*
import com.android.on_track.databinding.FragmentGeofenceBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.ArrayList

class GeofenceFragment : Fragment() {
    companion object{
        const val KEY_LIST_INDEX = "list_index"
        const val KEY_IS_NEW = "type"
        const val KEY_NAME = "name"
        const val KEY_LAT_LNG = "latLng"
        const val KEY_RADIUS = "radius"
    }

    private var _binding: FragmentGeofenceBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var addButton: Button
    private lateinit var deleteButton: Button
    private lateinit var deleteAllButton: Button
    private lateinit var fabButton: FloatingActionButton
    private lateinit var myListView: ListView

    private lateinit var arrayList: ArrayList<GeofenceEntry>
    private lateinit var arrayAdapter: GeofenceListAdapter

    private lateinit var database: GeofenceDatabase
    private lateinit var databaseDao: GeofenceDatabaseDao
    private lateinit var repository: GeofenceRepository
    private lateinit var viewModelFactory: GeofenceViewModelFactory
    private lateinit var geofenceViewModel: GeofenceViewModel

    private lateinit var result: ActivityResultLauncher<Intent>

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
        fabButton = view.findViewById(R.id.add_geofence_button)

        arrayList = ArrayList()
        arrayAdapter = GeofenceListAdapter(requireActivity(), arrayList)
        myListView.adapter = arrayAdapter

        database = GeofenceDatabase.getInstance(requireActivity())
        databaseDao = database.geofenceDatabaseDao
        repository = GeofenceRepository(databaseDao)
        viewModelFactory = GeofenceViewModelFactory(repository)
        geofenceViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[GeofenceViewModel::class.java]

        geofenceViewModel.allGeofenceEntriesLiveData.observe(requireActivity()) {
            arrayAdapter.replace(it)
            arrayAdapter.notifyDataSetChanged()
        }

        myListView.setOnItemClickListener { parent, _, pos, _ ->
            val entry = parent.getItemAtPosition(pos) as GeofenceEntry

            val intent = Intent(requireActivity(), MapActivity::class.java).apply {
                putExtra(KEY_LIST_INDEX, pos)
                putExtra(KEY_IS_NEW, false)
                putExtra(KEY_NAME, entry.entry_name)
                putExtra(KEY_LAT_LNG, Util.latLngToString(entry.location!!))
                putExtra(KEY_RADIUS, entry.geofence_radius)
            }

            result.launch(intent)
        }

        result = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK ) {
                val index = result.data?.getIntExtra(KEY_LIST_INDEX, -1) as Int
                if (index > -1) {
                    geofenceViewModel.deletePosition(index)
                }
            }
            else if(result.resultCode == 1337 ) {
                val name = result.data?.getStringExtra(KEY_NAME)
                val latLngStr = result.data?.getStringExtra(KEY_LAT_LNG)
                val latLng = Util.stringToLatLng(latLngStr!!)
                val radius = result.data?.getDoubleExtra(KEY_RADIUS, 50.0)

                val geofenceEntry = GeofenceEntry()
                geofenceEntry.entry_name = if (name == "") "(No name)" else name!!
                geofenceEntry.geofence_radius = radius!!
                geofenceEntry.location = latLng

                geofenceViewModel.insert(geofenceEntry)
            }
        }

        fabButton.setOnClickListener {
            val intent = Intent(requireActivity(), MapActivity::class.java).apply {
                putExtra(KEY_IS_NEW, true)
            }

            result.launch(intent)
        }

        addButton.setOnClickListener {
            val geofenceEntry = GeofenceEntry()

            geofenceEntry.entry_name = "Bonsor"
            geofenceEntry.location = LatLng(49.2231, -122.9954)
            geofenceEntry.geofence_radius = 50.0

            geofenceViewModel.insert(geofenceEntry)
        }

        deleteButton.setOnClickListener {
            geofenceViewModel.deleteFirst()
        }

        deleteAllButton.setOnClickListener {
            geofenceViewModel.deleteAll()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}