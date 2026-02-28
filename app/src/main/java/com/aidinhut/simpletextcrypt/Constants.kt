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
package com.aidinhut.simpletextcrypt

/**
 * Constants needed by the whole application.
 */
object Constants {
    const val PREFERENCES_KEY = "com.aidinhut.SimpleTextCrypt.Preferences"

    // For backward compatibility, the value is still called Encryption key rather than passphrase.
    const val PASSPHRASE_SETTINGS_KEY = "EncryptionKeySettingsKey"
    const val PASSCODE_SETTINGS_KEY = "PasscodeSettingsKey"
    const val LOCK_TIMEOUT_SETTINGS_KEY = "LockTimeoutSettingsKey"
    const val SETTINGS_VERSION_KEY = "SettingsVersion"
    const val IS_PASSCODE_CHANGED_SETTINGS_KEY = "IsPasscodeChangedSettingsKey"
    const val DEFAULT_PASSCODE = "1111"
    const val CURRENT_SETTINGS_VERSION = 2
}
