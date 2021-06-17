package com.example.monuments.ui.fragment.forgotPassword

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.monuments.R
import com.example.monuments.databinding.FragmentForgotPasswordBinding
import com.example.monuments.extensions.changeVisible
import com.example.monuments.repository.MainRepository
import com.example.monuments.ui.activity.AuthenticationActivity
import com.example.monuments.utils.Constants
import com.example.monuments.utils.KeyboardUtils
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this).get(ForgotPasswordViewModel::class.java) }

    private val forgotPasswordObserver = Observer<Int> {
        when(it) {
            Constants.SUCCESS_CODE -> {
                val action = ForgotPasswordFragmentDirections.actionLoginToLogin()
                NavHostFragment.findNavController(this).navigate(action)
                view?.let { view -> Snackbar.make(view, getString(R.string.detail_restore_password_email_sent), Snackbar.LENGTH_LONG).show() }
            }
            Constants.NULL_DATA_CODE -> {
                forgotPasswordViewBinding?.forgotInputEmail?.error = getString(R.string.error_null_values)
            }
            Constants.INVALID_CREDENTIALS_CODE -> {
                forgotPasswordViewBinding?.forgotInputEmail?.error = getString(R.string.error_invalid_credentials)
            }
        }
    }

    private var forgotPasswordViewBinding: FragmentForgotPasswordBinding? = null

    private val progressBarObserver = Observer<Boolean> { visible ->
        forgotPasswordViewBinding?.forgotProgressProgressBar?.changeVisible(visible)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        forgotPasswordViewBinding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return forgotPasswordViewBinding?.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.forgotResult.observe(viewLifecycleOwner, forgotPasswordObserver)
        viewModel.progressBarStatus.observe(viewLifecycleOwner, progressBarObserver)

        forgotPasswordViewBinding?.forgotBtnRestorePassword?.setOnClickListener {
            val activity: AuthenticationActivity = activity as AuthenticationActivity
            KeyboardUtils.closeKeyboard(activity)
            viewModel.resetPassword(forgotPasswordViewBinding?.forgotInputEmail?.text.toString())
        }

        forgotPasswordViewBinding?.forgotLayoutMainLayout?.setOnTouchListener { v, event ->
            val activity: AuthenticationActivity = activity as AuthenticationActivity
            KeyboardUtils.closeKeyboard(activity)
            false
        }
    }

}