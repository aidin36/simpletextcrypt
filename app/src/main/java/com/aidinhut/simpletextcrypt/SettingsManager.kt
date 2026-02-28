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
package com.aidinhut.simpletextcrypt

import android.content.Context
import android.util.Log
import com.aidinhut.simpletextcrypt.exceptions.SettingsNotSavedException
import com.aidinhut.simpletextcrypt.exceptions.WrongPasscodeException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException

/**
 * Manages settings of the app.
 * It stores settings encrypted.
 */
class SettingsManager private constructor() {

    private var passcode: String = Constants.DEFAULT_PASSCODE

    companion object {
        val instance: SettingsManager by lazy { SettingsManager() }
    }

    /**
     * Passcode is also the key for the encryption of the settings.
     * This method tries to decrypt the settings with the given passcode.
     * It also keeps the specified passcode for later decryption.
     *
     * If it can't find a "version" stored in the settings, it will try
     * to do the decryption with the legacy algorithm. On success, it will
     * upgrade the settings to the new version.
     */
    fun tryGetPasscode(passcode: String, context: Context): String {
        val sharedPref = context.getSharedPreferences(Constants.PREFERENCES_KEY, Context.MODE_PRIVATE)

        if (!sharedPref.contains(Constants.PASSCODE_SETTINGS_KEY)) {
            // There was no settings. First run. So there's no legacy settings to upgrade.
            setSettingsVersionIfNeeded(Constants.CURRENT_SETTINGS_VERSION, context)
            return Constants.DEFAULT_PASSCODE
        }

        // Keeping passcode for later use.
        this.passcode = passcode

        upgradeSettingsIfNeeded(passcode, context)

        return try {
            Crypter.decrypt(
                this.passcode,
                sharedPref.getString(Constants.PASSCODE_SETTINGS_KEY, Constants.DEFAULT_PASSCODE)!!
            )
        } catch (error: IllegalBlockSizeException) {
            throw WrongPasscodeException(context)
        } catch (error: BadPaddingException) {
            throw WrongPasscodeException(context)
        }
    }

    /**
     * This is the passcode user should type in LockActivity to open the lock.
     */
    fun getPasscode(context: Context): String {
        val sharedPref = context.getSharedPreferences(Constants.PREFERENCES_KEY, Context.MODE_PRIVATE)

        if (!sharedPref.contains(Constants.PASSCODE_SETTINGS_KEY)) {
            return Constants.DEFAULT_PASSCODE
        }

        return Crypter.decrypt(
            this.passcode,
            sharedPref.getString(Constants.PASSCODE_SETTINGS_KEY, Constants.DEFAULT_PASSCODE)!!
        )
    }

    /**
     * This is the passcode user typed on LockActivity.
     * It will be used to encrypt/decrypt settings.
     *
     * @throws SettingsNotSavedException if settings could not be saved.
     */
    fun setPasscode(passcode: String, context: Context) {
        this.passcode = passcode

        val sharedPref = context.getSharedPreferences(Constants.PREFERENCES_KEY, Context.MODE_PRIVATE)
        val prefEditor = sharedPref.edit()

        prefEditor.putString(
            Constants.PASSCODE_SETTINGS_KEY,
            Crypter.encrypt(passcode, passcode)
        )

        if (!prefEditor.commit()) {
            throw SettingsNotSavedException(context)
        }

        setSettingsVersionIfNeeded(Constants.CURRENT_SETTINGS_VERSION, context)
    }

    /**
     * Gets the passphrase that the text on the textbox should be encrypted with.
     */
    fun getPassphrase(context: Context): String {
        val sharedPref = context.getSharedPreferences(Constants.PREFERENCES_KEY, Context.MODE_PRIVATE)

        if (!sharedPref.contains(Constants.PASSPHRASE_SETTINGS_KEY)) {
            return ""
        }

        return Crypter.decrypt(
            this.passcode,
            sharedPref.getString(Constants.PASSPHRASE_SETTINGS_KEY, "")!!
        )
    }

    /**
     * Sets the passphrase that the text on the textbox should be encrypted with.
     *
     * @throws SettingsNotSavedException if settings could not be saved.
     */
    fun setPassphrase(passphrase: String, context: Context) {
        val sharedPref = context.getSharedPreferences(Constants.PREFERENCES_KEY, Context.MODE_PRIVATE)
        val prefEditor = sharedPref.edit()

        prefEditor.putString(
            Constants.PASSPHRASE_SETTINGS_KEY,
            Crypter.encrypt(this.passcode, passphrase)
        )

        if (!prefEditor.commit()) {
            throw SettingsNotSavedException(context)
        }
    }

    fun setLockTimeout(timeout: String?, context: Context) {
        val sharedPref = context.getSharedPreferences(Constants.PREFERENCES_KEY, Context.MODE_PRIVATE)
        val prefEditor = sharedPref.edit()

        val intTimeout = if (timeout.isNullOrEmpty()) 0 else timeout.toInt()

        prefEditor.putInt(Constants.LOCK_TIMEOUT_SETTINGS_KEY, intTimeout)

        if (!prefEditor.commit()) {
            throw SettingsNotSavedException(context)
        }
    }

    fun getLockTimeout(context: Context): Int {
        val sharedPref = context.getSharedPreferences(Constants.PREFERENCES_KEY, Context.MODE_PRIVATE)

        if (!sharedPref.contains(Constants.LOCK_TIMEOUT_SETTINGS_KEY)) {
            return 0
        }

        return sharedPref.getInt(Constants.LOCK_TIMEOUT_SETTINGS_KEY, 0)
    }

    /**
     * Currently, we either has version 2 or no version.
     * So, we won't try to re-set the version if it's exists. Saves some computation power!
     */
    fun setSettingsVersionIfNeeded(version: Int, context: Context) {
        val sharedPref = context.getSharedPreferences(Constants.PREFERENCES_KEY, Context.MODE_PRIVATE)

        if (sharedPref.contains(Constants.SETTINGS_VERSION_KEY)) {
            return
        }

        Log.i("simpletextcrypt.SettingsManager", "No settings version found. Going to add it.")

        val prefEditor = sharedPref.edit()
        prefEditor.putInt(Constants.SETTINGS_VERSION_KEY, version)

        if (!prefEditor.commit()) {
            throw SettingsNotSavedException(context)
        }
    }

    private fun upgradeSettingsIfNeeded(passcode: String, context: Context) {
        val sharedPref = context.getSharedPreferences(Constants.PREFERENCES_KEY, Context.MODE_PRIVATE)

        if (sharedPref.contains(Constants.SETTINGS_VERSION_KEY)) {
            // New settings. Nothing to do.
            return
        }

        // It's the legacy settings.
        val storedPasscode: String
        try {
            storedPasscode = Crypter.legacyDecrypt(
                passcode,
                sharedPref.getString(Constants.PASSCODE_SETTINGS_KEY, Constants.DEFAULT_PASSCODE)!!
            )
        } catch (error: IllegalBlockSizeException) {
            throw WrongPasscodeException(context)
        } catch (error: BadPaddingException) {
            throw WrongPasscodeException(context)
        }

        if (storedPasscode != passcode) {
            throw WrongPasscodeException(context)
        }

        Log.i("simpletextcrypt.SettingsManager", "Going to upgrade the settings from the legacy format.")

        // Upgrading the settings. The 'set' methods will use the new algorithm.
        this.passcode = passcode
        setPasscode(this.passcode, context)

        if (sharedPref.contains(Constants.PASSPHRASE_SETTINGS_KEY)) {
            val passphrase = Crypter.legacyDecrypt(
                this.passcode,
                sharedPref.getString(Constants.PASSPHRASE_SETTINGS_KEY, "")!!
            )
            setPassphrase(passphrase, context)
        }

        // Setting the version so we won't try to upgrade the next time.
        setSettingsVersionIfNeeded(Constants.CURRENT_SETTINGS_VERSION, context)
    }
}
