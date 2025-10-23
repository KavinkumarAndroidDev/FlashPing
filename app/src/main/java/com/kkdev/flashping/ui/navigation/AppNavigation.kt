package com.kkdev.flashping.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kkdev.flashping.ui.screens.encrpyt.EncryptScreen
import androidx.navigation.compose.rememberNavController
import com.kkdev.flashping.ui.screens.MainScreen
import com.kkdev.flashping.ui.screens.encrpyt.EncryptViewModel
import com.kkdev.flashping.ui.screens.encrpyt.TransmissionScreen
import com.kkdev.flashping.ui.screens.decrypt.DecryptScreen
import com.kkdev.flashping.ui.screens.flashlight.FlashlightInputScreen


sealed class Screen(val route: String) {
    object Main : Screen("main_screen")
    object Encrypt : Screen("encrypt_screen")
    object Transmit : Screen("transmit_screen")
    object Decrypt : Screen("decrypt_screen") // New
    object FlashlightInput : Screen("flashlight_input_screen") // New
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val encryptViewModel: EncryptViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {

        composable(Screen.Main.route) {
            MainScreen(navController = navController)
        }
        composable(Screen.Encrypt.route) {
            EncryptScreen(
                viewModel = encryptViewModel,
                onNavigateToTransmission = { navController.navigate(Screen.Transmit.route) }
            )
        }
        composable(Screen.Transmit.route) {
            TransmissionScreen(
                viewModel = encryptViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Decrypt.route) {
            DecryptScreen(
                onNavigateToFlashlightInput = { navController.navigate(Screen.FlashlightInput.route) }
            )
        }
        composable(Screen.FlashlightInput.route) {
            FlashlightInputScreen()
        }
    }
}
