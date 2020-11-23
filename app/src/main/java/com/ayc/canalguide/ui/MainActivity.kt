package com.ayc.canalguide.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.ayc.canalguide.R
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * This class will:
 *  Check if app update is available
 *  Handle Immerse / full screen mode
 *  Setup navigation and action bar
 *  Handle navigation up (actionbar back) button and hardware back button
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {


    private val viewModel: MainViewModel by viewModels()
    //private val mapsViewModel: MapsViewModel by viewModels()

    @Inject lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var appUpdateManager: AppUpdateManager

    private lateinit var navController: NavController

    private var lastImmerseModeToggleTime = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup navigation and actionbar
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(this, navController)

        // Handle immerse mode when navigating
        addNavigationListener()

        // Checks that the platform will allow the specified type of update: https://developer.android.com/guide/playcore/in-app-updates
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE)
                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, APP_UPDATE_REQUEST_CODE)
        }
    }

    override fun onResume() {
        super.onResume()

        // If an in-app update is already running, resume the update.
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS)
                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, APP_UPDATE_REQUEST_CODE)
        }
    }

    // Handle navigation bar back button
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    /**
     * If current fragment is the home / map fragment then go home (don't do default behavior / destroy the activity)
     */
    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.mapFragment) {
            val intent = Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_HOME)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        else
            super.onBackPressed()
    }

    // Toggled by mapFragment when the user taps on an empty area of the map
    fun toggleImmerseMode() {
        if (!viewModel.toggleImmerseMode) return

        // Add a delay so immerse mode can only be toggled once every 3 seconds
        val currentTimeMillis = Date().time
        if (currentTimeMillis - lastImmerseModeToggleTime < 2000) return
        lastImmerseModeToggleTime = currentTimeMillis

        if (viewModel.immerseMode) showSystemUI()
        else hideSystemUI()
        viewModel.immerseMode = !viewModel.immerseMode
    }

    /**
     * Handle immerseMode when navigating
     * Delay is used to make animations look better
     */
    private fun addNavigationListener() {
        navController.addOnDestinationChangedListener { navController, navDestination, bundle ->
            when (navDestination.id) {
                R.id.optionsDialogFragment -> if (viewModel.immerseMode) lifecycleScope.launch {
                    delay(500L)
                    showSystemUI()
                }
                R.id.markerDetailsFragment -> {
                    if (viewModel.immerseMode) showSystemUI()
                }
                // TODO only delay if coming from options fragment
                R.id.mapFragment -> if (viewModel.immerseMode) lifecycleScope.launch {
                    delay(500L)
                    hideSystemUI()
                }
            }
        }
    }

    // https://stackoverflow.com/questions/62643517/immersive-fullscreen-on-android-11
    // Hide the app action bar, system status bar and system navigation bar
    private fun hideSystemUI() {
        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, main_container).let { controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showSystemUI() {
        supportActionBar?.show()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, main_container).show(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
    }

    companion object {
        const val APP_UPDATE_REQUEST_CODE = 321
    }
}