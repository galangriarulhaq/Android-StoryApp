package com.bangkit.storyapp.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import com.bangkit.storyapp.R
import com.bangkit.storyapp.data.local.datastore.UserPreferences
import com.bangkit.storyapp.databinding.FragmentAddStoryBinding
import com.bangkit.storyapp.ui.factory.AddStoryViewModelFactory
import com.bangkit.storyapp.ui.model.AddStoryViewModel
import com.bangkit.storyapp.util.getImageUri
import com.bangkit.storyapp.util.reduceFileImage
import com.bangkit.storyapp.util.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var currentLat: Double? = 0.0
    private var currentLon: Double? = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val savedImageUri = addStoryViewModel.getImageUri()
        if (savedImageUri != null) {
            currentImageUri = savedImageUri
            showImage()
        }

        val token = UserPreferences(requireContext()).getToken()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        getCurrentLocation()

        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener { postStory(token!!) }

        addStoryViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) showProgressBar() else hideProgressBar()
        }

        addStoryViewModel.postResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Result.Success -> {
                    hideProgressBar()
                    view.findNavController().navigate(R.id.action_navigation_add_to_navigation_home)
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
            Toast.makeText(requireContext(), getString(R.string.please_fill_a_description), Toast.LENGTH_SHORT).show()
            return
        }

        if (currentImageUri == null) {
            Toast.makeText(requireContext(), getString(R.string.please_select_image), Toast.LENGTH_SHORT).show()
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
            val imageUri = addStoryViewModel.getImageUri()
            if (imageUri == null) {
                currentImageUri = null
            }
            currentImageUri = imageUri
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivPhoto.setImageURI(it)
        }
    }

    private fun getCurrentLocation() {
        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        101
                    )
                    return@setOnCheckedChangeListener
                }

                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        currentLat = location.latitude
                        currentLon = location.longitude
                    } else {
                        Log.e("LocationError", "Cannot find location")
                    }
                }
            } else {
                currentLat = 0.0
                currentLon = 0.0
            }
        }
    }

    private fun showProgressBar() {
        binding.buttonAdd.text = ""
        binding.buttonProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.buttonAdd.text = getString(R.string.add_story)
        binding.buttonProgressBar.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}