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
package com.aidinhut.simpletextcrypt;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/*
 * Implements stand-alone tests for Crypter class.
 */
@RunWith(AndroidJUnit4.class)
public class CrypterTest {

    /*
     * Tests if encrypting and decrypting a data will result
     * in the same data.
     */
    @Test
    public void testEncryptDecryptValidation() throws Exception {
        String[] passwords = {
                "uir43@#89djhncAd.,[]-+091jhdncq`",
                "18736",
                "❤\uFE0F\uD83D\uDE1C\uD83D\uDE48\uD83D\uDE43\uD83C\uDF4E\uD83C\uDF40\uD83C\uDF75\uD83C\uDF6A",
                "Me L@ve Ch33se! Double, no triple cream! Gonna have a heart attack.",
                "کلید فارسی"
        };
        String[] dataList = {
                "Hi there! I'm a test data that is going to be encrypted.",
                "I love you! ❤",
                "یک مسیج فارسی سری که برای بقیه بفرستیم",
                "A very long message to be secretly send over the network. It's very very very very very very very very long! And has some UTF-8 characters like یک and マンガが大好きです"
        };

        for (String password : passwords) {
            for (String data : dataList) {
                validateEncryptDecrypt(password, data);
            }
        }
    }

    private void validateEncryptDecrypt(String password, String data) throws Exception {
        Log.i("CrypterTest", "password=" + password + " - data=" + data);

        String encryptedData = Crypter.encrypt(password, data);
        String decryptedData = Crypter.decrypt(password, encryptedData);

        Assert.assertEquals(data, decryptedData);
    }

    /*
     * Tests if we can decrypt messages that are encrypted with an old version of the app.
     */
    @Test
    public void TestLegacyAlgorithm() throws Exception {
        Assert.assertEquals(
                "Hi! I'm encrypted with the old algorithm.",
                Crypter.legacyDecrypt("buSD4778@af-bq3299", "q:*`Bgd|I[2y9#[SRbsqNBNdEb+7gBaGSUylWNcSHQWw+Sq32XDF6N3avTWiBzUyObviPqdTzkFEDd2o"));
        Assert.assertEquals(
                "Another message for testing. \uD83D\uDE00\uD83D\uDE43",
                Crypter.legacyDecrypt("1356", "g4A!>y{%pz}Q4B\u007FRr9Da4/d/y33wGJichEjYy3Z+uRgKaTz4WvsOfcC/h2HJTDoSCktv2WNFRM5gdXEI"));
    }
}
