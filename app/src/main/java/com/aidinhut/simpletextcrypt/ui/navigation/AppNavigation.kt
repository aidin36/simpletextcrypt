/*
 * This file is part of SimpleTextCrypt.
 * Copyright (c) 2015-2026, Aidin Gharibnavaz <aidin@aidinhut.com>
 *
 * SimpleTextCrypt is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SimpleTextCrypt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SimpleTextCrypt.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aidinhut.simpletextcrypt.ui.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aidinhut.simpletextcrypt.ui.screen.LockScreen
import com.aidinhut.simpletextcrypt.ui.screen.MainScreen
import com.aidinhut.simpletextcrypt.ui.screen.SettingsScreen
import com.aidinhut.simpletextcrypt.viewmodel.MainViewModel

object Routes {
    const val LOCK = "lock"
    const val MAIN = "main"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val mainViewModel: MainViewModel = viewModel(context as ComponentActivity)

    NavHost(
        navController = navController,
        startDestination = Routes.LOCK,
    ) {
        composable(Routes.LOCK) {
            LockScreen(
                onUnlocked = {
                    mainViewModel.resetActivityTimer()
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOCK) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.MAIN) {
            MainScreen(
                viewModel = mainViewModel,
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                },
                onLock = {
                    navController.navigate(Routes.LOCK) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = {
                    if (navController.currentBackStackEntry?.destination?.route == Routes.SETTINGS) {
                        navController.popBackStack()
                    }
                },
            )
        }
    }
}

