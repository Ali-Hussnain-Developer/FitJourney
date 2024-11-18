package com.example.fitjourney.util

import android.os.CountDownTimer

class WorkoutTimer {
    private var countDownTimer: CountDownTimer? = null
    private var isRunning = false

    fun startTimer(
        durationMillis: Long,
        onTick: (Long) -> Unit,
        onFinish: () -> Unit
    ) {
        // Cancel existing timer if any
        stopTimer()

        countDownTimer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                onTick(millisUntilFinished)
            }

            override fun onFinish() {
                isRunning = false
                onFinish()
            }
        }.start()

        isRunning = true
    }

    fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
        isRunning = false
    }

    fun isTimerRunning() = isRunning
}
