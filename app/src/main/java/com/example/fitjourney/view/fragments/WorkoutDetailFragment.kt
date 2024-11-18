package com.example.fitjourney.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fitjourney.R
import com.example.fitjourney.databinding.FragmentWorkoutDetailBinding
import com.example.fitjourney.enum.WorkoutName
import com.example.fitjourney.util.WorkoutTimer


class WorkoutDetailFragment : Fragment() {
    private var _binding: FragmentWorkoutDetailBinding? = null
    private val binding get() = _binding!!

    private val workoutTimer = WorkoutTimer()
    private var isWorkoutInProgress = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        receiveDataFromWorkoutFragment()
        setupClickListener()
        updateUI()
    }

    private fun receiveDataFromWorkoutFragment() {
        val args = WorkoutDetailFragmentArgs.fromBundle(requireArguments())
        with(binding) {
            tvWorkoutName.text = args.workoutName
            tvWorkoutDescription.text = args.workoutDescription

            // Set animation based on workout name
            val animationResource = when (args.workoutName) {
                WorkoutName.PUSH_UPS.displayName -> R.raw.push_ups
                WorkoutName.SQUATS.displayName -> R.raw.squat
                WorkoutName.JUMPING_JACKS.displayName -> R.raw.jumping_jacks
                WorkoutName.LUNGES.displayName -> R.raw.lunges
                else -> R.raw.plank
            }
            ivWorkoutImage.setAnimation(animationResource)
            ivWorkoutImage.playAnimation()
        }
    }

    private fun setupClickListener() {
        binding.btnStartWorkout.setOnClickListener {
            if (!isWorkoutInProgress) {
                startWorkout()
            }
        }
    }

    private fun startWorkout() {
        val workoutDuration = when (binding.tvWorkoutName.text.toString()) {
            WorkoutName.PUSH_UPS.displayName -> 40_000L
            WorkoutName.SQUATS.displayName -> 40_000L
            WorkoutName.JUMPING_JACKS.displayName -> 50_000L
            WorkoutName.LUNGES.displayName -> 60_000L
            else -> 40_000L
        }

        isWorkoutInProgress = true
        binding.btnStartWorkout.isEnabled = false

        workoutTimer.startTimer(
            durationMillis = workoutDuration,
            onTick = { millisUntilFinished ->
                if (view != null && isAdded) {
                    val secondsRemaining = millisUntilFinished / 1000
                    binding.tvWorkoutTimer.text =
                        "00:${secondsRemaining.toString().padStart(2, '0')}"
                }
            },
            onFinish = {
                if (view != null && isAdded) {
                    binding.tvWorkoutTimer.text = getString(R.string.completed)
                    isWorkoutInProgress = false
                    binding.btnStartWorkout.isEnabled = true
                }
            }
        )
    }

    private fun updateUI() {
        binding.btnStartWorkout.isEnabled = !isWorkoutInProgress
    }

    override fun onPause() {
        super.onPause()
        workoutTimer.stopTimer()
        isWorkoutInProgress = false
        binding.btnStartWorkout.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        workoutTimer.stopTimer()
        _binding = null
    }
}