package com.bangkit.storyapp.ui

import com.bangkit.storyapp.data.Result
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bangkit.storyapp.R
import com.bangkit.storyapp.databinding.ActivityLoginBinding
import com.bangkit.storyapp.ui.factory.LoginViewModelFactory
import com.bangkit.storyapp.ui.model.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            binding.loginEmail.setText(savedInstanceState.getString("email"))
            binding.loginPassword.setText(savedInstanceState.getString("password"))
        }

        updateUI()

        loginViewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                showProgressBar()
            } else {
                hideProgressBar()
            }
        })

        loginViewModel.loginResponse.observe(this, Observer { response ->
            when(response) {
                is Result.Success -> {
                    hideProgressBar()
                    Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT)
                        .show()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)
                    finish()
                }

                is Result.Error -> {
                    hideProgressBar()
                    Toast.makeText(this, response.error, Toast.LENGTH_SHORT).show()
                }

                is Result.Loading -> {
                    showProgressBar()
                }

            }
        })


        binding.tvSignup.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("email", binding.loginEmail.text.toString())
        outState.putString("password", binding.loginPassword.text.toString())
    }

    private fun updateUI() {
        binding.btnLogin.setOnClickListener {
            val email = binding.loginEmail.text.toString().trim()
            val password = binding.loginPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Fill out All Form", Toast.LENGTH_SHORT).show()
            } else {
                loginViewModel.login(email, password)
            }

        }
    }

    private fun showProgressBar() {
        binding.btnLogin.visibility = View.GONE
        binding.progressIndicator.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.btnLogin.visibility = View.VISIBLE
        binding.progressIndicator.visibility = View.GONE
    }


}