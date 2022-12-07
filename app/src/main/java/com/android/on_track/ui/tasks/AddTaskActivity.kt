package com.android.on_track.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import com.android.on_track.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView

class AddTaskActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        val applications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val appNames = ArrayList<String>()

        for (application in applications) {
            appNames.add(application.packageName)
        }

        val taskNameInput = findViewById<TextInputEditText>(R.id.task_name_input)
        val appNameDropdown = findViewById<MaterialAutoCompleteTextView>(R.id.app_name_dropdown)
        val incrementInput = findViewById<NumberPicker>(R.id.increment_input)
        val progressGoalGoalUnitsHelper = findViewById<MaterialTextView>(R.id.progress_goal_goal_units_helper)
        val goalInput = findViewById<NumberPicker>(R.id.goal_input)
        val goalUnitsInput = findViewById<NumberPicker>(R.id.goal_units_input)
        val durationDurationUnitsHelper = findViewById<MaterialTextView>(R.id.duration_duration_units_helper)
        val durationInput = findViewById<NumberPicker>(R.id.duration_input)
        val durationUnitsInput = findViewById<NumberPicker>(R.id.duration_units_input)
        val cancelAddTaskButton = findViewById<FloatingActionButton>(R.id.cancel_add_task_button)
        val confirmAddTaskButton = findViewById<FloatingActionButton>(R.id.confirm_add_task_button)

        ArrayAdapter(this, android.R.layout.simple_spinner_item, appNames).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            appNameDropdown.setAdapter(adapter)
        }

        incrementInput.minValue = 0
        incrementInput.maxValue = 1
        incrementInput.displayedValues = resources.getStringArray(R.array.increment_values)
        incrementInput.value = 0

        goalInput.minValue = 1
        goalInput.maxValue = 100
        goalInput.value = 5

        goalUnitsInput.minValue = 0
        goalUnitsInput.maxValue = 1
        goalUnitsInput.displayedValues = resources.getStringArray(R.array.goal_units_values)
        goalUnitsInput.value = 0

        durationInput.minValue = 1
        durationInput.maxValue = 100
        durationInput.value = 10

        durationUnitsInput.minValue = 0
        durationUnitsInput.maxValue = 2
        durationUnitsInput.displayedValues = resources.getStringArray(R.array.duration_units_values)
        durationUnitsInput.value = 1

        cancelAddTaskButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        confirmAddTaskButton.setOnClickListener {
            val incrementValue = if (incrementInput.value == 0) {
                "Negative"
            } else { // incrementInput.value == 1
                "Positive"
            }

            val goalUnitsValue = if (goalUnitsInput.value == 0) {
                "Minutes"
            } else { // if goalUnitsInput.value == 1
                "Hours"
            }

            val durationUnitsValue = when (durationUnitsInput.value) {
                0 -> "Hours"
                1 -> "Days"
                else -> "Weeks" // if durationUnitsInput.value == 2
            }

            if (taskNameInput.text.toString().isNotBlank() && appNameDropdown.text.toString().isNotBlank()) {
                val bundle = Bundle()
                bundle.putString(getString(R.string.name_key), taskNameInput.text.toString())
                bundle.putString(getString(R.string.app_name_key), appNameDropdown.text.toString())
                bundle.putString(getString(R.string.increment_key), incrementValue)
                bundle.putString(getString(R.string.goal_units_key), goalUnitsValue)
                bundle.putInt(getString(R.string.goal_key), goalInput.value)
                bundle.putString(getString(R.string.duration_units_key), durationUnitsValue)
                bundle.putInt(getString(R.string.duration_key), durationInput.value)

                val intent = Intent()
                intent.putExtras(bundle)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }
}