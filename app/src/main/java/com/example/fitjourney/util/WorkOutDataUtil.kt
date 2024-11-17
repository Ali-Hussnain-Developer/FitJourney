package com.example.fitjourney.util

import com.example.fitjourney.enum.WorkoutName
import com.example.fitjourney.enum.WorkoutType
import com.example.fitjourney.model.Workout

object WorkOutDataUtil {
    fun workoutList(): List<Workout> {
        val workoutList = mutableListOf<Workout>()

        workoutList.add(
            Workout(
                name = WorkoutName.PUSH_UPS.displayName,
                description = "Push-ups are a foundational bodyweight exercise that targets the chest, shoulders, triceps, and core. Begin in a high plank position with your hands shoulder-width apart and your body in a straight line from head to heels. Lower your chest towards the ground by bending your elbows, keeping your core tight and back straight. Push back up to the starting position while exhaling. Adjust hand positioning or knee placement for variations and difficulty levels.",
                type = WorkoutType.COUNTER
            )
        )

        workoutList.add(
            Workout(
                name = WorkoutName.SQUATS.displayName,
                description = "Squats are a fundamental lower-body exercise that strengthens your thighs, glutes, and core. Stand with your feet shoulder-width apart and your toes slightly pointed out. Keep your chest upright and your back straight as you lower your hips down and back, as if sitting into a chair, until your thighs are parallel to the ground. Push through your heels to return to a standing position, ensuring your knees track over your toes.",
                type = WorkoutType.COUNTER
            )
        )

        workoutList.add(
            Workout(
                name = WorkoutName.JUMPING_JACKS.displayName,
                description = "Jumping jacks are a dynamic cardiovascular exercise that increases heart rate and warms up the entire body. Start by standing upright with your arms resting by your sides and your feet together. Jump while simultaneously spreading your legs shoulder-width apart and raising your arms above your head to form a wide 'X' shape. Jump again to return to the starting position, completing one repetition. Maintain a steady rhythm for optimal cardio benefits.",
                type = WorkoutType.TIMER
            )
        )

        workoutList.add(
            Workout(
                name = WorkoutName.LUNGES.displayName,
                description = "Lunges are a versatile exercise that focuses on strengthening your legs, glutes, and improving balance. Start by standing upright with your hands on your hips or by your sides. Step forward with one leg, lowering your hips until both knees form 90-degree angles. Ensure your front knee stays directly above your ankle and your back knee hovers just above the ground. Push through your front heel to return to the starting position, then alternate legs.",
                type = WorkoutType.COUNTER
            )
        )

        workoutList.add(
            Workout(
                name = WorkoutName.PLANK.displayName,
                description = "The plank is a full-body isometric exercise that builds core strength and stability. Begin in a forearm plank position with your elbows directly beneath your shoulders and your body in a straight line from head to heels. Engage your core by pulling your belly button towards your spine. Avoid letting your hips sag or rise. Hold this position while breathing steadily, focusing on maintaining proper alignment throughout the exercise.",
                type = WorkoutType.TIMER
            )
        )

        return workoutList
    }
}
