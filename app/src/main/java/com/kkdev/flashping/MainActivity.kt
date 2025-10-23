package com.kkdev.flashping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kkdev.flashping.ui.navigation.AppNavigation
import com.kkdev.flashping.ui.theme.FlashPingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashPingTheme {
                // MainActivity's only job is to set up the theme and host the navigation.
                AppNavigation()
            }
        }
    }
}
