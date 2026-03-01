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

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.aidinhut.simpletextcrypt.exceptions.WrongPasscodeException
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test cases for SettingsManager class.
 */
@RunWith(AndroidJUnit4::class)
class SettingsManagerTest {

    @Before
    fun setUp() {
        clearPreferences()
    }

    @Test
    fun testTryGetDefaultPasscode() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val passcode = SettingsManager.instance.tryGetPasscode("1111", context)

        assertEquals(0, passcode.compareTo("1111"))
    }

    @Test
    fun testSettingPasscode() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val newPass = "184363"
        val defaultPass = "1111"

        SettingsManager.instance.setPasscode(newPass, context)

        // Getting the passcode again.
        var passcode = SettingsManager.instance.tryGetPasscode(newPass, context)

        assertEquals(0, passcode.compareTo(newPass))

        // Setting the default passcode again.
        SettingsManager.instance.setPasscode(defaultPass, context)

        // Getting the passcode again.
        passcode = SettingsManager.instance.tryGetPasscode(defaultPass, context)

        assertEquals(0, passcode.compareTo(defaultPass))
    }

    @Test
    fun testLongPasscode() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val newPass = "18436309872651726344567609"
        val defaultPass = "1111"

        SettingsManager.instance.setPasscode(newPass, context)

        // Getting the passcode again.
        var passcode = SettingsManager.instance.tryGetPasscode(newPass, context)

        assertEquals(0, passcode.compareTo(newPass))

        // Setting the default passcode again.
        SettingsManager.instance.setPasscode(defaultPass, context)

        // Getting the passcode again.
        passcode = SettingsManager.instance.tryGetPasscode(defaultPass, context)

        assertEquals(0, passcode.compareTo(defaultPass))
    }

    @Test(expected = WrongPasscodeException::class)
    fun testWrongPasscodeFirstRun() {
        // Should throw exception when passcode is not the default one, when the app runs for the first time.
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        SettingsManager.instance.tryGetPasscode("8888", context)
    }

    @Test(expected = WrongPasscodeException::class)
    fun testWrongPasscodeSecondRun() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Setting the passcode cause the app to create and encrypt the database.
        SettingsManager.instance.setPasscode("2222", context)
        // Now it should throw exception on the wrong passcode enter.
        SettingsManager.instance.tryGetPasscode("8888", context)
    }

    @Test
    fun testSettingEncryptionKey() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val encryptionKey = "ancbdhey7834@#*().,{}}[f1~`f93-+"

        SettingsManager.instance.setPassphrase(encryptionKey, context)

        // Getting the key again and check it.
        val gotEncKey = SettingsManager.instance.getPassphrase(context)

        assertEquals(0, gotEncKey.compareTo(encryptionKey))
    }

    /**
     * Clears all the shared preferences of the current app.
     */
    private fun clearPreferences() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val sharedPref = context.getSharedPreferences(Constants.PREFERENCES_KEY, Context.MODE_PRIVATE)
        val prefEditor = sharedPref.edit()
        prefEditor.clear()
        prefEditor.commit()
    }
}
