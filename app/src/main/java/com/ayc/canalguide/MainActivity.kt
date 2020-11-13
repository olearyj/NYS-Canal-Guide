package com.ayc.canalguide

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {


    //private val mapsViewModel: MapsViewModel by viewModels()

    private lateinit var appUpdateManager: AppUpdateManager

    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupActionBarWithNavController(this, navController)

        // Request location permissions
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions(this, permissions, LOCATION_REQUEST_CODE)

        // https://developer.android.com/guide/playcore/in-app-updates
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)

        // Checks that the platform will allow the specified type of update
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE)
                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, APP_UPDATE_REQUEST_CODE)
        }






        navController.addOnDestinationChangedListener { navController, navDestination, bundle ->
            when (navDestination.id) {
                R.id.optionsDialogFragment -> lifecycleScope.launch {
                    delay(500L)
                    if (immerseMode) showSystemUI()
                }
                R.id.markerDetailsFragment -> {
                    if (immerseMode) showSystemUI()
                }
                R.id.nav_map -> {
                    //if (immerseMode) hideSystemUI()
                    // TODO only delay if coming from options fragment
                    lifecycleScope.launch {
                        delay(500L)
                        if (immerseMode) hideSystemUI()
                    }
                }
            }
        }



        ViewCompat.setOnApplyWindowInsetsListener(main_container) { view: View, windowInsetsCompat: WindowInsetsCompat ->
            if (windowInsetsCompat.isVisible(WindowInsetsCompat.Type.systemBars())) {
                Log.i("TESTQQQ", "Window Insets visible")

            } else {
                Log.i("TESTQQQ", "Window Insets invisible")

            }
            windowInsetsCompat
        }

//        window.decorView.setOnApplyWindowInsetsListener { view, windowInsets ->
//            if (windowInsets.isVisible(WindowInsets.Type.systemBars())) {
//                immerseMode = false
//                supportActionBar?.show()
//                Log.i("TESTQQQ", "system bars are Visible")
//            }
//            else {
//                Log.i("TESTQQQ", "system bars are Invisible")
//                supportActionBar?.hide()
//                immerseMode = true
//            }
//            main_container.fitsSystemWindows = immerseMode
//            windowInsets
//        }

        // Set immerseMode variable here in case the user swipes down to show system UI
//        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
//            // Note that system bars will only be "visible" if none of the
//            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
//            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
//                // TODO: The system bars are visible. Make any desired
//                // adjustments to your UI, such as showing the action bar or
//                // other navigational controls.
//                immerseMode = false
//                supportActionBar?.show()
//                Log.i("TESTQQQ", "LISTENER says visibility = $visibility")
//            } else {
//                // TODO: The system bars are NOT visible. Make any desired
//                // adjustments to your UI, such as hiding the action bar or
//                // other navigational controls.
//                Log.i("TESTQQQ", "LISTENER says visibility = $visibility")
//                supportActionBar?.hide()
//                immerseMode = true
//            }
//
//        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    var immerseMode = false
    fun toggleImmerseMode() {
        if (immerseMode) {
            showSystemUI()
        }
        else {
            hideSystemUI()
        }

        immerseMode = !immerseMode
    }

    // https://stackoverflow.com/questions/62643517/immersive-fullscreen-on-android-11
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

    override fun onResume() {
        super.onResume()

        // If an in-app update is already running, resume the update.
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS)
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    APP_UPDATE_REQUEST_CODE
                )
        }
    }

    companion object {
        val LOCATION_PERMISSION = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        const val LOCATION_REQUEST_CODE = 123

        const val APP_UPDATE_REQUEST_CODE = 321

        fun hasPermissions(context: Context, permissions: Array<String>) = permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    }
}