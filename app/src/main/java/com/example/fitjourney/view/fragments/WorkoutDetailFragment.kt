package com.example.fitjourney.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitjourney.R
import com.example.fitjourney.databinding.FragmentWorkoutDetailBinding
import com.example.fitjourney.enum.WorkoutName

class WorkoutDetailFragment : Fragment() {
    private var _binding: FragmentWorkoutDetailBinding? = null
    private val binding get() = _binding!!
    private var workoutName:String?=null
    private var workoutDescription:String?=null
    private var workoutType:String?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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
        setUpClickListener()
    }

    private fun setUpClickListener() {
        binding.btnStartWorkout.setOnClickListener {
            startWorkoutTimer(workoutName)
            binding.btnStartWorkout.isClickable=false
        }
    }

    private fun receiveDataFromWorkoutFragment() {
        val args = WorkoutDetailFragmentArgs.fromBundle(requireArguments())
        workoutName = args.workoutName
        workoutDescription = args.workoutDescription
        workoutType = args.workoutType
        setDataToView(workoutName, workoutDescription)
    }

    private fun setDataToView(
        workoutName: String?,
        workoutDescription: String?,
    ) {
        with(binding) {
            tvWorkoutName.text = workoutName
            tvWorkoutDescription.text = workoutDescription

            val animationResource = when (workoutName) {
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

    private fun startWorkoutTimer(workoutName: String?) {
        val workoutDuration = when (workoutName) {
            WorkoutName.PUSH_UPS.displayName -> 30_000L // 30 seconds
            WorkoutName.SQUATS.displayName -> 40_000L // 40 seconds
            WorkoutName.JUMPING_JACKS.displayName -> 50_000L // 50 seconds
            WorkoutName.LUNGES.displayName -> 60_000L // 60 seconds
            else -> 20_000L // Default 20 seconds
        }

        object : CountDownTimer(workoutDuration, 1_000L) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.tvWorkoutTimer.text = "00:${secondsRemaining.toString().padStart(2, '0')}"
            }

            override fun onFinish() {
                binding.tvWorkoutTimer.text = getString(R.string.completed)
                binding.btnStartWorkout.isClickable=true
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}