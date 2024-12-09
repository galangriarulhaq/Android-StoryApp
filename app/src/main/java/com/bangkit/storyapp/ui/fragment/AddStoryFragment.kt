package com.bangkit.storyapp.ui.fragment

import com.bangkit.storyapp.data.Result
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.findNavController
import com.bangkit.storyapp.R
import com.bangkit.storyapp.data.local.datastore.UserPreferences
import com.bangkit.storyapp.databinding.FragmentAddStoryBinding
import com.bangkit.storyapp.ui.factory.AddStoryViewModelFactory
import com.bangkit.storyapp.ui.model.AddStoryViewModel
import com.bangkit.storyapp.util.getImageUri
import com.bangkit.storyapp.util.reduceFileImage
import com.bangkit.storyapp.util.uriToFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryFragment : Fragment() {

    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding!!

    private val addStoryViewModel: AddStoryViewModel by viewModels {
        AddStoryViewModelFactory.getInstance(requireActivity())
    }

    private var currentImageUri: Uri? = null

    private var currentLat: Double? = null
    private var currentLon: Double? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val savedImageUri = addStoryViewModel.getImageUri()
        if (savedImageUri != null) {
            currentImageUri = savedImageUri
            showImage()
        }

        val token = UserPreferences(requireContext()).getToken()

        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener { postStory(token!!) }

        observeViewModel()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun postStory(token: String) {
        val description = binding.addDescription.text.toString().trim()

        if (description.isEmpty()) {
            Toast.makeText(requireContext(), "Please Fill a Description", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentImageUri == null) {
            Toast.makeText(requireContext(), "Please Select Image", Toast.LENGTH_SHORT).show()
            return
        }

        val file = uriToFile(currentImageUri!!, requireContext()).reduceFileImage()

        val descriptionRequestBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val photoRequestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val photoMultipart =
            MultipartBody.Part.createFormData("photo", file.name, photoRequestBody)

        val latRequestBody = currentLat?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        val lonRequestBody = currentLon?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

        addStoryViewModel.postStory(
            token,
            descriptionRequestBody,
            photoMultipart,
            latRequestBody,
            lonRequestBody
        )
    }

    private fun observeViewModel() {
        addStoryViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) showProgressBar() else hideProgressBar()
        }

        addStoryViewModel.postResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Result.Success -> {
                    hideProgressBar()
                    view?.findNavController()?.navigate(R.id.action_navigation_add_to_navigation_home)
                    Toast.makeText(requireContext(), getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
                }

                is Result.Loading -> showProgressBar()

                is Result.Error -> {
                    hideProgressBar()
                    Toast.makeText(requireContext(), response.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        currentImageUri = getImageUri(requireContext())
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            addStoryViewModel.setImageUri(uri)
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivPhoto.setImageURI(it)
        }
    }

    private fun showProgressBar() {
        binding.buttonAdd.text = ""
        binding.buttonProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.buttonAdd.text = context?.getString(R.string.add_story)
        binding.buttonProgressBar.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}