package com.android.on_track.ui.geofence

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Switch
import android.widget.TextView
import com.android.on_track.R
import com.android.on_track.geofenceDB.GeofenceEntry

class GeofenceListAdapter(private val context: Context, private var geofenceList: List<GeofenceEntry>) : BaseAdapter(){
    override fun getItem(position: Int): Any {
        return geofenceList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return this.geofenceList.size
    }

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(pos: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.geofence_item,null)

        val locationStr = geofenceList[pos].entry_name
        val isEnabled = geofenceList[pos].isEnabled

        val name = view.findViewById(R.id.textView_locationName) as TextView
        val switch = view.findViewById(R.id.switch_enable) as Switch

        name.text = locationStr
        switch.isChecked = isEnabled

        return view
    }

    fun replace(newHistoryList: List<GeofenceEntry>){
        geofenceList = newHistoryList
    }
}