package com.rleonb.reported

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import com.rleonb.reported.domain.models.ReportedTheme
import com.rleonb.reported.ui.designsystem.ObjCounterMaterialTheme
import com.rleonb.reported.ui.designsystem.theme.custom.LocalPadding
import com.rleonb.reported.ui.designsystem.theme.custom.padding
import com.rleonb.reported.ui.screens.home.HomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // use the entire display to draw
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
//        lateinit var fusedLocationClient: FusedLocationProviderClient
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            val isThemeDark = shouldUseDarkTheme(ReportedTheme.FollowSystem)
            UpdateSystemBarsEffect(isThemeDark)

            CompositionLocalProvider(LocalPadding provides padding) {
                ObjCounterMaterialTheme(isThemeDark = isThemeDark) {
                    AppNavigator()
                }
            }
        }
    }

    @Composable
    fun AppNavigator() {
        Scaffold(contentWindowInsets = WindowInsets(0)) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing
                    )
            ) {
                HomeScreen()
            }
        }
    }

    @Composable
    private fun UpdateSystemBarsEffect(isThemeDark: Boolean) {
        DisposableEffect(isThemeDark) {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(
                    Color.TRANSPARENT,
                    Color.TRANSPARENT
                ) { isThemeDark },
                navigationBarStyle = SystemBarStyle.auto(
                    lightScrim,
                    darkScrim
                ) { isThemeDark }
            )
            onDispose { }
        }
    }
}

@Composable
private fun shouldUseDarkTheme(
    theme: ReportedTheme
): Boolean = when (theme) {
    ReportedTheme.Dark -> true
    ReportedTheme.Light -> false
    ReportedTheme.FollowSystem -> isSystemInDarkTheme()
}

/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
