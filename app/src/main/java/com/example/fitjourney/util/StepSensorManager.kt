package com.example.fitjourney.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

// StepSensorManager.kt
class StepSensorManager(private val context: Context) : SensorEventListener {
    private var sensorManager: SensorManager = context.getSystemService(SensorManager::class.java)
    private var stepCounterSensor: Sensor? = null
    private var stepDetectorSensor: Sensor? = null
    private var onStepUpdateListener: ((Int) -> Unit)? = null
    private var totalSteps = 0

    init {
        initializeSensors()
    }

    private fun initializeSensors() {
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    }

    fun startTracking(onStepUpdate: (Int) -> Unit) {
        this.onStepUpdateListener = onStepUpdate
        stepCounterSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        stepDetectorSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopTracking() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                totalSteps = event.values[0].toInt()
                onStepUpdateListener?.invoke(totalSteps)
            }
            Sensor.TYPE_STEP_DETECTOR -> {
                if (event.values[0].toInt() == 1) {
                    totalSteps++
                    onStepUpdateListener?.invoke(totalSteps)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun areSensorsAvailable(): Boolean {
        return stepCounterSensor != null || stepDetectorSensor != null
    }
}