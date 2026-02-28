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
package com.aidinhut.simpletextcrypt.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.aidinhut.simpletextcrypt.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LockUiState(
    val passcode: String = "",
    val errorMessage: String? = null,
    val unlockSuccess: Boolean = false,
)

class LockViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LockUiState())
    val uiState: StateFlow<LockUiState> = _uiState.asStateFlow()

    fun onPasscodeChanged(passcode: String) {
        _uiState.value = _uiState.value.copy(passcode = passcode)
    }

    fun unlock(context: Context) {
        val passcode = _uiState.value.passcode

        // Clear passcode field immediately.
        _uiState.value = _uiState.value.copy(passcode = "")

        val savedPasscode: String
        try {
            savedPasscode = SettingsManager.instance.tryGetPasscode(passcode, context)
        } catch (error: Exception) {
            _uiState.value = _uiState.value.copy(errorMessage = error.message)
            return
        }

        if (savedPasscode != passcode) {
            _uiState.value = _uiState.value.copy(
                errorMessage = context.getString(com.aidinhut.simpletextcrypt.R.string.wrong_passcode_error)
            )
            return
        }

        // Right passcode. Signal navigation.
        _uiState.value = _uiState.value.copy(unlockSuccess = true)
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetUnlockSuccess() {
        _uiState.value = _uiState.value.copy(unlockSuccess = false)
    }
}
