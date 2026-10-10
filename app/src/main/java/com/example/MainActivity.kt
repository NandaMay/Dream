package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.navigation.OurDreamMainApp
import com.example.ui.theme.AppThemeSetting
import com.example.ui.theme.OurDreamTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val app = application as OurDreamApp

    setContent {
      val themeSetting by app.preferences.themeFlow.collectAsStateWithLifecycle(
        initialValue = AppThemeSetting.LIGHT_ROMANTIC
      )

      val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
      ) { _ ->
        // Permission handled
      }

      LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          if (ContextCompat.checkSelfPermission(
              this@MainActivity,
              Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
          ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
          }
        }
      }

      OurDreamTheme(themeSetting = themeSetting) {
        OurDreamMainApp(app = app)
      }
    }
  }
}
