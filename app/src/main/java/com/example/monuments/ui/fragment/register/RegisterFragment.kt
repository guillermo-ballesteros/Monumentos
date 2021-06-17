package com.example.monuments.ui.fragment.register

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.example.monuments.R
import com.example.monuments.databinding.RegisterFragmentBinding
import com.example.monuments.extensions.changeVisible
import com.example.monuments.repository.MainRepository
import com.example.monuments.ui.activity.AuthenticationActivity
import com.example.monuments.utils.Constants
import com.example.monuments.utils.KeyboardUtils
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {


    private val viewModel by lazy { ViewModelProvider(this).get(RegisterViewModel::class.java) }

    private var registerViewBinding: RegisterFragmentBinding? = null

    private val registerObserver = Observer<Int> { result ->
        when(result) {
            Constants.SUCCESS_CODE -> {
                val action = RegisterFragmentDirections.actionRegisterToLogin()
                NavHostFragment.findNavController(this).navigate(action)
                view?.let { view -> Snackbar.make(view, getString(R.string.detail_account_created), Snackbar.LENGTH_LONG).show() }
            }
            Constants.USER_COLLISION_CODE -> {
                registerViewBinding?.registerInputEmail?.error = getString(R.string.error_collision_user)
            }
            Constants.INVALID_CREDENTIALS_CODE -> {
                registerViewBinding?.registerInputEmail?.error = getString(R.string.error_invalid_user)
            }
            Constants.NULL_DATA_CODE -> {
                view?.let { view -> Snackbar.make(view, getString(R.string.error_null_values), Snackbar.LENGTH_LONG).show() }
                if (registerViewBinding?.registerInputName?.text.toString() == "") {
                    registerViewBinding?.registerInputName?.error = getString(R.string.error_null_values)
                }
                if (registerViewBinding?.registerInputLastName?.text.toString() == "") {
                    registerViewBinding?.registerInputLastName?.error = getString(R.string.error_null_values)
                }
                if (registerViewBinding?.registerInputEmail?.text.toString() == "") {
                    registerViewBinding?.registerInputEmail?.error = getString(R.string.error_null_values)
                }
                if (registerViewBinding?.registerInputPassword?.text.toString() == "") {
                    registerViewBinding?.registerInputPassword?.error = getString(R.string.error_null_values)
                }
                if (registerViewBinding?.registerInputConfirmPassword?.text.toString() == "") {
                    registerViewBinding?.registerInputConfirmPassword?.error = getString(R.string.error_null_values)
                }

            }
            Constants.PASSWORD_NOT_MATCH_CODE -> {
                registerViewBinding?.registerInputPassword?.error = getString(R.string.error_password_not_match)
            }
        }
    }

    private val progressBarObserver = Observer<Boolean> { visible ->
        registerViewBinding?.registerProgressProgressBar?.changeVisible(visible)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        registerViewBinding = RegisterFragmentBinding.inflate(inflater, container, false)
        return registerViewBinding?.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.registerResult.observe(viewLifecycleOwner, registerObserver)
        viewModel.progressBarStatus.observe(viewLifecycleOwner, progressBarObserver)
        registerViewBinding?.registerBtnBackButton?.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterToLogin()
            NavHostFragment.findNavController(this).navigate(action)
        }

        registerViewBinding?.registerBtnRegisterBtn?.setOnClickListener {
            val activity: AuthenticationActivity = activity as AuthenticationActivity
            KeyboardUtils.closeKeyboard(activity)
            viewModel.createNewAccount(registerViewBinding?.registerInputName?.text.toString(),
                                        registerViewBinding?.registerInputLastName?.text.toString(),
                                        registerViewBinding?.registerInputEmail?.text.toString(),
                                        registerViewBinding?.registerInputPassword?.text.toString(),
                                        registerViewBinding?.registerInputConfirmPassword?.text.toString())
        }

        registerViewBinding?.registerLayoutMainLayout?.setOnTouchListener { v, event ->
            val activity: AuthenticationActivity = activity as AuthenticationActivity
            KeyboardUtils.closeKeyboard(activity)
            false
        }
    }
}