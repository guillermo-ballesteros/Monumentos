package com.example.monuments.ui.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.monuments.R
import com.example.monuments.databinding.ActivityMainBinding
import com.example.monuments.ui.dialog.LogOutDialogFragment
import com.example.monuments.ui.dialog.RemoveMonumentDialogFragment
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), LogOutDialogFragment.LogOutListener {
    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment }
    private val viewModel by lazy {ViewModelProvider(this).get(MainActivityViewModel::class.java)}
    private val navController by lazy { navHostFragment.navController }
    private var mainActivityViewBinding: ActivityMainBinding? = null
    private var backButton = false
    private var isRootElement = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityViewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityViewBinding?.root)
        toolbar()
        init()
    }

    private fun toolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (backButton) {
                    navController.popBackStack()

                } else {
                    mainActivityViewBinding?.drawerlayout?.openDrawer(GravityCompat.START)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (isRootElement) {
            val dialog: LogOutDialogFragment = LogOutDialogFragment.newInstance()
            dialog.show(this.supportFragmentManager, getString(R.string.detail_dialog_tag))

        } else {
            super.onBackPressed()
        }
    }

    private fun init() {
        navController.addOnDestinationChangedListener(navigationControllerListener)
        val navView: NavigationView = findViewById(R.id.navView)
        navView.setupWithNavController(navController)
        mainActivityViewBinding?.navViewLabelLogOutBtn?.setOnClickListener {
            onBackPressed()
        }
    }


    private val navigationControllerListener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->
            val showNavBackButton = arguments?.get(getString(R.string.detail_type_home_button))?: false
            isRootElement = arguments?.getBoolean("root_element")?: false
            if (showNavBackButton == true) {
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
                val name = arguments?.get(getString(R.string.detail_search_fragment_name))
                supportActionBar?.title = name.toString()
                backButton = showNavBackButton as Boolean

            } else {
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
                supportActionBar?.title = getString(R.string.general_app_name)
                backButton = showNavBackButton as Boolean
            }

            if ( navController.currentDestination?.label == getString(R.string.detail_label_fragment_add_monument) ||
                navController.currentDestination?.label == getString(R.string.detail_label_fragment_detail)  ) {
                supportActionBar?.hide()
            } else {
                supportActionBar?.show()
            }
        }

    override fun onLogOutBtnClicked() {
        val intent = Intent(this, AuthenticationActivity::class.java)
        startActivity(intent)
        viewModel.logOut()
        finish()
    }


}