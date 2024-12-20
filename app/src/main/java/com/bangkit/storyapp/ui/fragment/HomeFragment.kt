package com.bangkit.storyapp.ui.fragment

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.storyapp.data.local.datastore.UserPreferences
import com.bangkit.storyapp.databinding.FragmentHomeBinding
import com.bangkit.storyapp.ui.StorieMapsActivity
import com.bangkit.storyapp.ui.adapter.LoadingStateAdapter
import com.bangkit.storyapp.ui.adapter.StoryAdapter
import com.bangkit.storyapp.ui.factory.HomeViewModelFactory
import com.bangkit.storyapp.ui.model.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: StoryAdapter


    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory.getInstance(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = UserPreferences(requireContext()).getToken()
        homeViewModel.stories(token = token!!)
            .observe(viewLifecycleOwner) { pagingData ->
                adapter.submitData(lifecycle, pagingData)
            }

        setUpData()

        binding.fabMap.setOnClickListener {
            val intent = Intent(requireActivity(), StorieMapsActivity::class.java)
            startActivity(intent)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finishAffinity()
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setUpData() {
        binding.rvStory.layoutManager = LinearLayoutManager(requireContext())
        adapter = StoryAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        adapter.addLoadStateListener { loadState ->
            binding.progressIndicator.visibility =
                if (loadState.source.refresh is LoadState.Loading) View.VISIBLE else View.GONE

            val errorState = loadState.source.refresh as? LoadState.Error
                ?: loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error

            errorState?.let {
                it.error.localizedMessage?.let { it1 -> Toast.makeText(requireContext(), it1, Toast.LENGTH_SHORT).show() }
            }
        }

    }

}