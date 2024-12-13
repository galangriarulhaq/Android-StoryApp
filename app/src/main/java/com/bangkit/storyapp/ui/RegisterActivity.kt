package com.bangkit.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import com.bangkit.storyapp.data.Result
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.storyapp.R
import com.bangkit.storyapp.databinding.ActivityRegisterBinding
import com.bangkit.storyapp.ui.factory.RegisterViewModelFactory
import com.bangkit.storyapp.ui.model.RegisterViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (savedInstanceState != null) {
            binding.registerName.setText(savedInstanceState.getString("name"))
            binding.registerEmail.setText(savedInstanceState.getString("email"))
            binding.registerPassword.setText(savedInstanceState.getString("password"))
        }

        updateUI()

        registerViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                showProgressBar()
            } else {
                hideProgressBar()
            }
        }


        registerViewModel.registerResponse.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    hideProgressBar()
                    Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                is Result.Error -> {
                    hideProgressBar()
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }

                is Result.Loading -> {
                    showProgressBar()
                }
            }
        }

        binding.tvSignin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        playAnimation()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("name", binding.registerName.text.toString())
        outState.putString("email", binding.registerEmail.text.toString())
        outState.putString("password", binding.registerPassword.text.toString())
    }

    private fun updateUI() {
        binding.btnRegister.setOnClickListener {
            val name = binding.registerName.text.toString().trim()
            val email = binding.registerEmail.text.toString().trim()
            val password = binding.registerPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill out All Form", Toast.LENGTH_SHORT).show()
            } else {
                registerViewModel.register(name, email, password)
            }
        }
    }

    private fun playAnimation() {

        val nameInput = ObjectAnimator.ofFloat(binding.nameInput, View.ALPHA, 1f).setDuration(500)
        val emailInput = ObjectAnimator.ofFloat(binding.emailInput, View.ALPHA, 1f).setDuration(500)
        val passwordInput = ObjectAnimator.ofFloat(binding.passInput, View.ALPHA, 1f).setDuration(500)
        val button = ObjectAnimator.ofFloat(binding.btnFrame, View.ALPHA, 1f).setDuration(500)
        val info = ObjectAnimator.ofFloat(binding.tvInfo, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                nameInput,
                emailInput,
                passwordInput,
                button,
                info
            )
            startDelay = 100
        }.start()

    }


    private fun showProgressBar() {
        binding.btnRegister.text = ""
        binding.btnProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.btnRegister.text = getString(R.string.register)
        binding.btnProgressBar.visibility = View.GONE
    }


}