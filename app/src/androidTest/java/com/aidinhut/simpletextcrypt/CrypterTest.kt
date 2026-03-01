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

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Implements stand-alone tests for Crypter class.
 */
@RunWith(AndroidJUnit4::class)
class CrypterTest {

    /**
     * Tests if encrypting and decrypting a data will result
     * in the same data.
     */
    @Test
    fun testEncryptDecryptValidation() {
        val passwords = arrayOf(
            "uir43@#89djhncAd.,[]-+091jhdncq`",
            "18736",
            "❤️😜🙈🙃🍎🍀🍵🍪",
            "Me L@ve Ch33se! Double, no triple cream! Gonna have a heart attack.",
            "کلید فارسی"
        )
        val dataList = arrayOf(
            "Hi there! I'm a test data that is going to be encrypted.",
            "I love you! ❤",
            "یک مسیج فارسی سری که برای بقیه بفرستیم",
            "A very long message to be secretly send over the network. It's very very very very very very" +
                    " very very long! And has some UTF-8 characters like یک and マンガが大好きです"
        )

        for (password in passwords) {
            for (data in dataList) {
                validateEncryptDecrypt(password, data)
            }
        }
    }

    private fun validateEncryptDecrypt(password: String, data: String) {
        Log.i("CrypterTest", "password=$password - data=$data")

        val encryptedData = Crypter.encrypt(password, data)
        val decryptedData = Crypter.decrypt(password, encryptedData)

        assertEquals(data, decryptedData)
    }

    /**
     * Tests if we can decrypt messages that are encrypted with an old version of the app.
     */
    @Test
    fun testLegacyAlgorithm() {
        assertEquals(
            "Hi! I'm encrypted with the old algorithm.",
            Crypter.legacyDecrypt(
                "buSD4778@af-bq3299",
                "q:*`Bgd|I[2y9#[SRbsqNBNdEb+7gBaGSUylWNcSHQWw+Sq32XDF6N3avTWiBzUyObviPqdTzkFEDd2o"
            )
        )
        assertEquals(
            "Another message for testing. 😀🙃",
            Crypter.legacyDecrypt(
                "1356",
                "g4A!>y{%pz}Q4B\u007FRr9Da4/d/y33wGJichEjYy3Z+uRgKaTz4WvsOfcC/h2HJTDoSCktv2WNFRM5gdXEI"
            )
        )
    }
}
