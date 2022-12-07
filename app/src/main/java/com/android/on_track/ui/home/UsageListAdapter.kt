package com.android.on_track.ui.home

import android.annotation.SuppressLint
import android.app.usage.UsageStats
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.android.on_track.R


class UsageListAdapter(private val context: Context, private var usageList: List<AppInfo>) : BaseAdapter(){
    override fun getItem(position: Int): Any {
        return usageList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return this.usageList.size
    }

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(pos: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.item_usage,null)

        val packageName = usageList[pos].app_name
        val usageTime = usageList[pos].time_used

        val textName = view.findViewById<TextView>(R.id.app_name)
        val textUsage = view.findViewById<TextView>(R.id.app_usage)
        val imageView = view.findViewById<ImageView>(R.id.app_image)

        val shortenedAppName = packageName.split('.').last()
        textName.text = shortenedAppName
//        text_name.text = packageName

        val seconds = (usageTime/1000)%60
        val minutes = (usageTime/(1000*60))%60
        val hours = (usageTime/(1000*60*60))
        textUsage.text = "$hours hours, $minutes minutes, $seconds seconds"

        try {
            val icon: Drawable = context.packageManager.getApplicationIcon(packageName)
            imageView.setImageDrawable(icon)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }


        return view
    }

    fun replace(newList: List<UsageStats>){
//        usageList = newList
    }
}