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
package com.aidinhut.simpletextcrypt.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.aidinhut.simpletextcrypt.R
import com.aidinhut.simpletextcrypt.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsUiState(
    val passphrase: String = "",
    val passcode: String = "",
    val lockTimeout: String = "0",
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false,
)

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun loadSettings(context: Context) {
        try {
            val passphrase = SettingsManager.instance.getPassphrase(context)
            val passcode = SettingsManager.instance.getPasscode(context)
            val lockTimeout = SettingsManager.instance.getLockTimeout(context)
            _uiState.value = SettingsUiState(
                passphrase = passphrase,
                passcode = passcode,
                lockTimeout = lockTimeout.toString(),
            )
        } catch (error: Exception) {
            _uiState.value = _uiState.value.copy(errorMessage = error.message)
        }
    }

    fun onPassphraseChanged(passphrase: String) {
        _uiState.value = _uiState.value.copy(passphrase = passphrase)
    }

    fun onPasscodeChanged(passcode: String) {
        _uiState.value = _uiState.value.copy(passcode = passcode)
    }

    fun onLockTimeoutChanged(lockTimeout: String) {
        _uiState.value = _uiState.value.copy(lockTimeout = lockTimeout)
    }

    fun clearPassphrase() {
        _uiState.value = _uiState.value.copy(passphrase = "")
    }

    fun save(context: Context) {
        val state = _uiState.value

        if (state.passphrase.length < 3) {
            _uiState.value = _uiState.value.copy(
                errorMessage = context.getString(R.string.invalid_passphrase_error)
            )
            return
        }
        if (state.passcode.length < 2) {
            _uiState.value = _uiState.value.copy(
                errorMessage = context.getString(R.string.invalid_passcode_error)
            )
            return
        }

        try {
            SettingsManager.instance.setPasscode(state.passcode, context)
            SettingsManager.instance.setPassphrase(state.passphrase, context)
            SettingsManager.instance.setLockTimeout(state.lockTimeout, context)
        } catch (error: Exception) {
            _uiState.value = _uiState.value.copy(errorMessage = error.message)
            return
        }

        _uiState.value = _uiState.value.copy(saveSuccess = true)
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }
}
