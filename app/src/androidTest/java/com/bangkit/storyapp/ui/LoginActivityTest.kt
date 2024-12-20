package com.bangkit.storyapp.ui

import com.bangkit.storyapp.data.remote.retrofit.ApiConfig
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class LoginActivityTest {

    private val mockWebServer = MockWebServer()


    @Before
    fun setUp() {
        mockWebServer.start(8080)

    }

}