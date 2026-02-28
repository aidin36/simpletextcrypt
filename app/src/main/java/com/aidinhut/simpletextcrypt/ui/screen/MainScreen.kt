/*
 * This file is part of SimpleTextCrypt.
 * Copyright (c) 2015-2025, Aidin Gharibnavaz <aidin@aidinhut.com>
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
package com.aidinhut.simpletextcrypt.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aidinhut.simpletextcrypt.R
import com.aidinhut.simpletextcrypt.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToSettings: () -> Unit,
    onLock: () -> Unit,
    viewModel: MainViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    var showHelp by remember { mutableStateOf(false) }

    // Check lock timeout every time this screen enters composition
    // (returning from Settings or from background).
    LaunchedEffect(Unit) {
        viewModel.checkLockTimeout(context)
    }

    // Handle lock-on-background using the Activity lifecycle.
    // Using Activity lifecycle (not NavBackStackEntry) so in-app navigation
    // to Settings doesn't trigger the pause lock.
    val activity = context as ComponentActivity
    DisposableEffect(activity) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.checkPauseLock(context)
            }
        }
        activity.lifecycle.addObserver(observer)
        onDispose {
            activity.lifecycle.removeObserver(observer)
        }
    }

    // Navigate to lock when shouldLock triggers.
    LaunchedEffect(uiState.shouldLock) {
        if (uiState.shouldLock) {
            viewModel.resetLockFlag()
            onLock()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_more),
                            contentDescription = "Menu",
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.action_settings)) },
                            onClick = {
                                showMenu = false
                                onNavigateToSettings()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.action_help_text)) },
                            onClick = {
                                showMenu = false
                                showHelp = true
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.action_about)) },
                            onClick = {
                                showMenu = false
                                showAbout = true
                            },
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            // Scrollable text area filling available space.
            OutlinedTextField(
                value = uiState.text,
                onValueChange = viewModel::onTextChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                readOnly = false,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Encrypt buttons row.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
            ) {
                Button(
                    onClick = { viewModel.encrypt(context) },
                    modifier = Modifier.padding(end = 5.dp),
                ) {
                    Text(text = stringResource(R.string.encrypt_button_text))
                }
                Button(
                    onClick = { viewModel.decrypt(context) },
                    modifier = Modifier.padding(end = 5.dp),
                ) {
                    Text(text = stringResource(R.string.decrypt_button_text))
                }
                Button(
                    onClick = { viewModel.legacyDecrypt(context) },
                ) {
                    Text(
                        text = stringResource(R.string.legacy_decrypt_button_text),
                        fontSize = 14.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Clipboard buttons row.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
            ) {
                Button(
                    onClick = { viewModel.copy(context) },
                    modifier = Modifier.padding(end = 5.dp),
                ) {
                    Text(text = stringResource(R.string.copy_button_text))
                }
                Button(
                    onClick = { viewModel.paste(context) },
                    modifier = Modifier.padding(end = 5.dp),
                ) {
                    Text(text = stringResource(R.string.paste_button_text))
                }
                Button(
                    onClick = { viewModel.clear() },
                ) {
                    Text(text = stringResource(R.string.clear_button))
                }
            }
        }
    }

    // Error dialog.
    if (uiState.errorMessage != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissError,
            title = { Text(text = stringResource(R.string.error_title)) },
            text = { Text(text = uiState.errorMessage!!) },
            confirmButton = {
                TextButton(onClick = viewModel::dismissError) {
                    Text("OK")
                }
            },
        )
    }

    // About dialog.
    if (showAbout) {
        AlertDialog(
            onDismissRequest = { showAbout = false },
            text = {
                val aboutText = "${stringResource(R.string.about_copyright)}\n\n" +
                        "${stringResource(R.string.about_source)}\n\n" +
                        "${stringResource(R.string.about_license_1)}\n" +
                        "${stringResource(R.string.about_license_2)}\n\n" +
                        stringResource(R.string.about_license_3)
                Text(
                    text = aboutText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                )
            },
            confirmButton = {
                TextButton(onClick = { showAbout = false }) {
                    Text("OK")
                }
            },
        )
    }

    // Help dialog.
    if (showHelp) {
        AlertDialog(
            onDismissRequest = { showHelp = false },
            text = {
                val helpText = "${stringResource(R.string.help_title)}\n\n" +
                        "${stringResource(R.string.help_line1)}\n\n" +
                        "${stringResource(R.string.help_line2)}\n\n" +
                        stringResource(R.string.help_line3)
                Text(
                    text = helpText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                )
            },
            confirmButton = {
                TextButton(onClick = { showHelp = false }) {
                    Text("OK")
                }
            },
        )
    }
}
