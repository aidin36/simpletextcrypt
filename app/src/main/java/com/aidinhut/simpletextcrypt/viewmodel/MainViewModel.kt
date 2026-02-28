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

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.ViewModel
import com.aidinhut.simpletextcrypt.Crypter
import com.aidinhut.simpletextcrypt.R
import com.aidinhut.simpletextcrypt.SettingsManager
import com.aidinhut.simpletextcrypt.exceptions.PassphraseNotSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MainUiState(
    val text: String = "",
    val errorMessage: String? = null,
    val shouldLock: Boolean = false,
)

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    var lastActivity: Long = System.currentTimeMillis() / 1000
        private set

    fun onTextChanged(text: String) {
        _uiState.value = _uiState.value.copy(text = text)
    }

    fun encrypt(context: Context) {
        try {
            val passphrase = getPassphrase(context)
            val result = Crypter.encrypt(passphrase, _uiState.value.text)
            _uiState.value = _uiState.value.copy(text = result)
        } catch (error: Exception) {
            _uiState.value = _uiState.value.copy(errorMessage = error.message)
        }
    }

    fun decrypt(context: Context) {
        try {
            val passphrase = getPassphrase(context)
            val result = Crypter.decrypt(passphrase, _uiState.value.text)
            _uiState.value = _uiState.value.copy(text = result)
        } catch (error: Exception) {
            _uiState.value = _uiState.value.copy(errorMessage = error.message)
        }
    }

    fun legacyDecrypt(context: Context) {
        try {
            val passphrase = getPassphrase(context)
            val result = Crypter.legacyDecrypt(passphrase, _uiState.value.text)
            _uiState.value = _uiState.value.copy(text = result)
        } catch (error: Exception) {
            _uiState.value = _uiState.value.copy(errorMessage = error.message)
        }
    }

    fun copy(context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("encrypted text", _uiState.value.text)
        clipboard.setPrimaryClip(clip)
    }

    fun paste(context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard.hasPrimaryClip()) {
            val item = clipboard.primaryClip?.getItemAt(0)
            val pastedText = item?.text?.toString() ?: return
            _uiState.value = _uiState.value.copy(text = pastedText)
        }
    }

    fun clear() {
        _uiState.value = _uiState.value.copy(text = "")
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Checks if the lock timeout has expired. Called on lifecycle resume/pause events.
     * Returns true if the app should lock (navigate back to lock screen).
     */
    fun checkLockTimeout(context: Context): Boolean {
        val timeout = SettingsManager.instance.getLockTimeout(context)
        val currentTime = System.currentTimeMillis() / 1000
        if (timeout != 0 && currentTime - lastActivity >= timeout * 60L) {
            _uiState.value = _uiState.value.copy(text = "", shouldLock = true)
            return true
        }
        lastActivity = System.currentTimeMillis() / 1000
        return false
    }

    /**
     * Checks the pause-time lock logic. If timeout is 0 (immediate lock) or timer expired,
     * clears text and signals lock.
     */
    fun checkPauseLock(context: Context): Boolean {
        val timeout = SettingsManager.instance.getLockTimeout(context)
        val currentTime = System.currentTimeMillis() / 1000
        if (timeout == 0 || currentTime - lastActivity >= timeout * 60L) {
            _uiState.value = _uiState.value.copy(text = "", shouldLock = true)
            return true
        }
        return false
    }

    fun resetLockFlag() {
        _uiState.value = _uiState.value.copy(shouldLock = false)
    }

    private fun getPassphrase(context: Context): String {
        val passphrase = SettingsManager.instance.getPassphrase(context)
        if (passphrase.isEmpty()) {
            throw PassphraseNotSet(context)
        }
        return passphrase
    }
}
