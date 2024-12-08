package com.bangkit.storyapp.ui.fragment

import android.content.Intent
import com.bangkit.storyapp.data.Result
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.storyapp.data.local.datastore.UserPreferences
import com.bangkit.storyapp.data.remote.response.ListStoryItem
import com.bangkit.storyapp.databinding.FragmentHomeBinding
import com.bangkit.storyapp.ui.DetailActivity
import com.bangkit.storyapp.ui.adapter.StoryAdapter
import com.bangkit.storyapp.ui.factory.HomeViewModelFactory
import com.bangkit.storyapp.ui.model.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory.getInstance(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            setHasFixedSize(true)
        }

        val token = UserPreferences(requireContext()).getToken()
        token?.let {
            homeViewModel.getAllStories(it)
        } ?: run {
            Toast.makeText(requireContext(), "Token Tidak Tersedia", Toast.LENGTH_SHORT).show()
        }

        homeViewModel.storiesResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    val stories = result.data
                    if (stories.isNotEmpty()) {
                        setStoryData(stories)
                    } else {
                        Toast.makeText(context, "Tidak ada data cerita", Toast.LENGTH_SHORT).show()
                    }
                }

                is Result.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressIndicator.visibility = View.VISIBLE
            } else {
                binding.progressIndicator.visibility = View.GONE
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setStoryData(listStory: List<ListStoryItem>) {
        val adapter = StoryAdapter(
            onItemClick = { id -> navigateToDetailStory(id) }
        )
        adapter.submitList(listStory)
        binding.rvStory.adapter = adapter
    }

    private fun navigateToDetailStory(storyId: String) {
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra("STORY_ID", storyId)
        }
        startActivity(intent)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}