package com.example.monuments.ui.fragment.login

import android.annotation.SuppressLint
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.example.monuments.R
import com.example.monuments.databinding.LoginFragmentBinding
import com.example.monuments.extensions.changeVisible
import com.example.monuments.repository.MainRepository
import com.example.monuments.ui.activity.AuthenticationActivity
import com.example.monuments.ui.activity.MainActivity
import com.example.monuments.utils.Constants
import com.example.monuments.utils.KeyboardUtils
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }

    private var loginViewBinding: LoginFragmentBinding? = null

    private val loginObserver = Observer<Int> {
        when (it) {
            Constants.NULL_DATA_CODE -> {
                if (loginViewBinding?.loginInputEmail?.text.toString() == "") {
                    loginViewBinding?.loginInputEmail?.error = getString(R.string.error_null_values)
                }
                if (loginViewBinding?.loginInputPassword?.text.toString() == "") {
                    loginViewBinding?.loginInputPassword?.error = getString(R.string.error_null_values)
                }
            }
            Constants.SUCCESS_CODE -> {
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            Constants.EMAIL_NOT_VERIFIED_CODE -> {
                loginViewBinding?.loginInputEmail?.error = getString(R.string.error_verify_email)
            }
            Constants.INVALID_USER_CODE -> {
                loginViewBinding?.loginInputEmail?.error = getString(R.string.error_invalid_user)
            }
            Constants.INVALID_CREDENTIALS_CODE -> {
                loginViewBinding?.loginInputEmail?.error = getString(R.string.error_invalid_credentials)
                loginViewBinding?.loginInputPassword?.error = getString(R.string.error_invalid_credentials)
            }
            Constants.TOO_MANY_REQUEST_CODE -> {
                loginViewBinding?.loginInputEmail?.error = getString(R.string.error_too_many_request)
            }
        }
    }

    private val progressBarObserver = Observer<Boolean> { visible ->
        loginViewBinding?.loginProgressProgressBar?.changeVisible(visible)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginViewBinding = LoginFragmentBinding.inflate(inflater, container, false)
        return loginViewBinding?.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loginResult.observe(viewLifecycleOwner, loginObserver)
        viewModel.progressBarStatus.observe(viewLifecycleOwner, progressBarObserver)
        loginViewBinding?.loginBtnLoginButton?.setOnClickListener {
            val email = loginViewBinding?.loginInputEmail?.text.toString()
            val password = loginViewBinding?.loginInputPassword?.text.toString()
            val activity: AuthenticationActivity = activity as AuthenticationActivity
            KeyboardUtils.closeKeyboard(activity)
            viewModel.login(email, password)
        }

        loginViewBinding?.loginBtnRegisterBtn?.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginToRegister()
            NavHostFragment.findNavController(this).navigate(action)
        }

        loginViewBinding?.loginLabelForgotPassword?.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginToForgotPassword()
            NavHostFragment.findNavController(this).navigate(action)
        }

        loginViewBinding?.loginLayoutMainLayout?.setOnTouchListener { v, event ->
            val activity: AuthenticationActivity = activity as AuthenticationActivity
            KeyboardUtils.closeKeyboard(activity)
            false
        }

    }

}