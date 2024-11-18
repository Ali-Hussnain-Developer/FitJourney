package com.example.fitjourney.util

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class PermissionUtil(private val activity: FragmentActivity) {
    companion object {
        const val REQUEST_ACTIVITY_RECOGNITION = 1
        const val REQUEST_NOTIFICATIONS = 2
    }

    fun checkAndRequestActivityRecognitionPermission(callback: (Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when {
                ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    callback(true)
                }
                else -> {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                        REQUEST_ACTIVITY_RECOGNITION
                    )
                }
            }
        } else {
            callback(true)
        }
    }

    fun checkAndRequestNotificationPermission(callback: (Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    callback(true)
                }
                else -> {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        REQUEST_NOTIFICATIONS
                    )
                }
            }
        } else {
            callback(true)
        }
    }

    fun handlePermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        onActivityRecognitionGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        when (requestCode) {
            REQUEST_ACTIVITY_RECOGNITION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onActivityRecognitionGranted()
                } else {
                    onPermissionDenied()
                }
            }
        }
    }
}