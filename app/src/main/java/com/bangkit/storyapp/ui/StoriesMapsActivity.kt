package com.bangkit.storyapp.ui

import android.content.res.Resources
import com.bangkit.storyapp.data.Result
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bangkit.storyapp.R
import com.bangkit.storyapp.data.local.datastore.UserPreferences
import com.bangkit.storyapp.data.remote.response.ListStoryItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.bangkit.storyapp.databinding.ActivityStoriesMapsBinding
import com.bangkit.storyapp.ui.factory.StoryMapViewModelFactory
import com.bangkit.storyapp.ui.model.StoryMapViewModel
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions

class StoriesMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityStoriesMapsBinding

    private val storyMapViewModel: StoryMapViewModel by viewModels {
        StoryMapViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoriesMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val token = UserPreferences(this).getToken()
        token?.let {
            storyMapViewModel.getAllStoriesLocation(it)
        } ?: run {
            Toast.makeText(this, "Token tidak tersedia", Toast.LENGTH_SHORT).show()
        }

        storyMapViewModel.storiesLocation.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    val stories = result.data
                    if (stories.isNotEmpty()) {
                        addManyMarker(stories)
                    } else {
                        Toast.makeText(this, "Tidak ada data cerita", Toast.LENGTH_SHORT).show()
                    }
                }

                is Result.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true


        setMapStyle()
    }

    private fun addManyMarker(stories: List<ListStoryItem>) {

        val boundsBuilder = LatLngBounds.Builder()

        stories.forEach { story ->
            val lat = story.lat
            val lon = story.lon
            if (lat != null && lon != null) {
                val latLng = LatLng(lat, lon)
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(story.name)
                        .snippet(story.description)
                )
                boundsBuilder.include(latLng)
            }
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    companion object {
        private const val TAG = "StoriesMapsActivity"
    }

}