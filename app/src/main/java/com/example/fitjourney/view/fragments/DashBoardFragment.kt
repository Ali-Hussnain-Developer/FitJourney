/*
package com.example.fitjourney.view.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.fitjourney.R
import com.example.fitjourney.databinding.FragmentDashBoardBinding
import com.example.fitjourney.util.NotificationUtil
import com.example.fitjourney.util.SharedPreferencesUtil

class DashBoardFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentDashBoardBinding? = null
    private val binding get() = _binding!!
    private val REQUEST_ACTIVITY_RECOGNITION_PERMISSION = 1
    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var stepDetectorSensor: Sensor? = null
    private var totalSteps = 0
    private var dailyGoalSteps = 10000 // Default goal of 10,000 steps
    private var caloriesBurned = 0.0 // Default calories burned

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        requestPermission()
        retrieveDataFromSharedPreferences()
        setUpListeners()
    }

    @SuppressLint("SetTextI18n")
    private fun retrieveDataFromSharedPreferences() {
        dailyGoalSteps = SharedPreferencesUtil.getStepGoal(requireContext())
        binding.pbStepsCount.max = dailyGoalSteps
        binding.tvGoal.text = "Daily goal: $dailyGoalSteps Steps" // Show saved goal
    }

    private fun requestPermission() {
        // Check for ACTIVITY_RECOGNITION permission
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    REQUEST_ACTIVITY_RECOGNITION_PERMISSION
                )
            }
        } else {
            initializeStepSensors()
        }

        // Check for POST_NOTIFICATIONS permission (for Android 13 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request notification permission
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    2 // You can define a different request code for notifications
                )
            }
        } else {
            // If the OS is below Android 13, no need to request permission
            NotificationUtil.createNotificationChannel(requireContext()) // Create notification channel if needed
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpListeners() {
        binding.btnSetGoal.setOnClickListener {
            val goalInput = binding.etStepGoal.text.toString().trim()
            if (goalInput.isNotEmpty()) {
                dailyGoalSteps = goalInput.toInt()
                // Save updated goal to SharedPreferences
                SharedPreferencesUtil.saveStepGoal(requireContext(), dailyGoalSteps)
                binding.pbStepsCount.max = dailyGoalSteps
                binding.tvGoal.text = "Daily goal: $dailyGoalSteps Steps"
                updateUI()
                requestPermission()
                // Check if the new goal has already been achieved
                if (totalSteps >= dailyGoalSteps) {
                    sendGoalAchievedNotification()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.enter_valid_number),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun sendGoalAchievedNotification() {
        NotificationUtil.createNotificationChannel(requireContext()) // Create channel if needed
        NotificationUtil.showNotification(
            context = requireContext(),
            notificationId = 1,
            title = getString(R.string.goal_achieved),
            message = "Congratulations! You've already achieved your daily step goal of $dailyGoalSteps steps."
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_ACTIVITY_RECOGNITION_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializeStepSensors()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.permission_needed_for_step_counter),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun initializeStepSensors() {
        sensorManager = requireContext().getSystemService(SensorManager::class.java)
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        if (stepCounterSensor == null) {
            Toast.makeText(
                requireContext(),
                getString(R.string.step_counter_sensor_not_available),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
        }

        if (stepDetectorSensor == null) {
            Toast.makeText(
                requireContext(),
                getString(R.string.step__detector_sensor_not_available),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                totalSteps = event.values[0].toInt()
                updateUI()
                calculateCalories()
                checkGoalCompletion()
            }

            Sensor.TYPE_STEP_DETECTOR -> {
                val stepsDetected = event.values[0].toInt()
                if (stepsDetected == 1) {
                    totalSteps++
                    updateUI()
                    calculateCalories()
                    checkGoalCompletion()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateCalories() {
        caloriesBurned = totalSteps * 0.04 // Assume 0.04 calories burned per step
        binding.tvCaloriesBurned.text = "Calories burned: %.2f".format(caloriesBurned)
    }

    private fun checkGoalCompletion() {
        if (totalSteps >= dailyGoalSteps) {
            sendGoalAchievedNotification()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        binding.tvSteps.text = "Steps today: $totalSteps"
        binding.pbStepsCount.progress = totalSteps
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        stepCounterSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
        stepDetectorSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        if (::sensorManager.isInitialized){
        sensorManager.unregisterListener(this)
    }}


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}


*/
package com.example.fitjourney.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fitjourney.R
import com.example.fitjourney.databinding.FragmentDashBoardBinding
import com.example.fitjourney.util.NotificationUtil
import com.example.fitjourney.util.PermissionUtil
import com.example.fitjourney.util.SharedPreferencesUtil
import com.example.fitjourney.util.StepSensorManager

class DashBoardFragment : Fragment() {
    private var _binding: FragmentDashBoardBinding? = null
    private val binding get() = _binding!!

    private lateinit var permissionUtil: PermissionUtil
    private lateinit var stepSensorManager: StepSensorManager

    private var dailyGoalSteps = 10000
    private var totalSteps = 0
    private var caloriesBurned = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        permissionUtil = PermissionUtil(requireActivity())
        stepSensorManager = StepSensorManager(requireContext())
        retrieveDataFromSharedPreferences()
        setUpListeners()
        requestPermissions()
    }

    private fun requestPermissions() {
        permissionUtil.checkAndRequestActivityRecognitionPermission { granted ->
            if (granted) {
                initializeStepTracking()
            } else {
                showStepsPermissionNeededToast()
            }
        }

        permissionUtil.checkAndRequestNotificationPermission { granted ->
            if (granted) {
                NotificationUtil.createNotificationChannel(requireContext())
            } else {
                showNotificationPermissionNeededToast()
            }
        }
    }

    private fun initializeStepTracking() {
        if (stepSensorManager.areSensorsAvailable()) {
            stepSensorManager.startTracking { steps ->
                totalSteps = steps
                updateUI()
                calculateCalories()
                checkGoalCompletion()
            }
        } else {
            showSensorNotAvailableToast()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun retrieveDataFromSharedPreferences() {
        dailyGoalSteps = SharedPreferencesUtil.getStepGoal(requireContext())
        binding.pbStepsCount.max = dailyGoalSteps
        binding.tvGoal.text = "$dailyGoalSteps Steps"
    }

    private fun setUpListeners() {
        binding.btnSetGoal.setOnClickListener {
            handleGoalUpdate()
        }
    }

    private fun handleGoalUpdate() {
        val goalInput = binding.etStepGoal.text.toString().trim()
        if (goalInput.isNotEmpty()) {
            dailyGoalSteps = goalInput.toInt()
            SharedPreferencesUtil.saveStepGoal(requireContext(), dailyGoalSteps)
            updateGoalUI()
            if (totalSteps >= dailyGoalSteps) {
                sendGoalAchievedNotification()
            }
        } else {
            showInvalidNumberToast()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateGoalUI() {
        binding.pbStepsCount.max = dailyGoalSteps
        binding.tvGoal.text = "$dailyGoalSteps Steps"
        updateUI()
    }

    private fun sendGoalAchievedNotification() {
        NotificationUtil.showNotification(
            context = requireContext(),
            notificationId = 1,
            title = getString(R.string.goal_achieved),
            message = "Congratulations! You've achieved your daily step goal of $dailyGoalSteps steps."
        )
    }

    @SuppressLint("SetTextI18n")
    private fun calculateCalories() {
        caloriesBurned = totalSteps * 0.04
        binding.tvCaloriesBurned.text = "%.2f".format(caloriesBurned)
    }

    private fun checkGoalCompletion() {
        if (totalSteps >= dailyGoalSteps) {
            sendGoalAchievedNotification()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        binding.tvSteps.text = "Steps Today: $totalSteps"
        binding.pbStepsCount.progress = totalSteps
    }

    private fun showStepsPermissionNeededToast() {
        Toast.makeText(
            requireContext(),
            getString(R.string.permission_needed_for_step_counter),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showNotificationPermissionNeededToast() {
        Toast.makeText(
            requireContext(),
            getString(R.string.permission_needed_for_notification),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showSensorNotAvailableToast() {
        Toast.makeText(
            requireContext(),
            getString(R.string.step_counter_sensor_not_available),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showInvalidNumberToast() {
        Toast.makeText(
            requireContext(),
            getString(R.string.enter_valid_number),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onResume() {
        super.onResume()
        stepSensorManager.startTracking { steps ->
            totalSteps = steps
            updateUI()
            calculateCalories()
            checkGoalCompletion()
        }
    }

    override fun onPause() {
        super.onPause()
        stepSensorManager.stopTracking()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
