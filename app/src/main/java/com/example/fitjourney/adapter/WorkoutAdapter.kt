package com.example.fitjourney.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitjourney.R
import com.example.fitjourney.databinding.ItemWorkoutBinding
import com.example.fitjourney.enum.WorkoutName
import com.example.fitjourney.model.Workout

class WorkoutAdapter(
    private val workoutList: List<Workout>,
    private val callbacks: WorkoutAdapterCallbacks
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    inner class WorkoutViewHolder(val binding: ItemWorkoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding = ItemWorkoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workoutList[position]
        with(holder.binding) {
            workoutName.text = workout.name
            val animationRes = when (workout.name) {
                WorkoutName.PUSH_UPS.displayName -> R.raw.push_ups
                WorkoutName.SQUATS.displayName -> R.raw.squat
                WorkoutName.JUMPING_JACKS.displayName -> R.raw.jumping_jacks
                WorkoutName.LUNGES.displayName -> R.raw.lunges
                else -> R.raw.plank
            }
            lottieAnimationView.setAnimation(animationRes)
            lottieAnimationView.playAnimation()
            layoutWorkoutDetail.setOnClickListener {
                callbacks.onItemClicked(workout)
            }
        }
    }

    override fun getItemCount(): Int = workoutList.size

    interface WorkoutAdapterCallbacks {
        fun onItemClicked(item: Workout)
    }
}
