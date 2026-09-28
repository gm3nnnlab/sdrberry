package org.chornobyl.hamdash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.chornobyl.hamdash.settings.AppTheme
import org.chornobyl.hamdash.ui.HamDashApp
import org.chornobyl.hamdash.ui.theme.ChornobylHamDashTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as HamDashApplication).container

        setContent {
            val settings by container.settingsManager.settingsFlow.collectAsState(initial = null)
            val isDark = settings?.theme != AppTheme.LIGHT

            ChornobylHamDashTheme(useDarkTheme = isDark) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HamDashApp(container = container)
                }
            }
        }
    }
}
