package com.example.fitjourney.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitjourney.util.WorkOutDataUtil
import com.example.fitjourney.adapter.WorkoutAdapter
import com.example.fitjourney.databinding.FragmentWorkoutBinding
import com.example.fitjourney.model.Workout


class WorkoutFragment : Fragment(), WorkoutAdapter.WorkoutAdapterCallbacks {
    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        binding.workoutRecyclerView.setHasFixedSize(true)
        binding.workoutRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.workoutRecyclerView.adapter =
            WorkoutAdapter(WorkOutDataUtil.workoutList(), this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(item: Workout) {
        val action = WorkoutFragmentDirections.actionWorkoutFragmentToWorkoutDetailFragment(
            item.name,
            item.description,
            item.type.name
        )
        findNavController().navigate(action)

    }
}
