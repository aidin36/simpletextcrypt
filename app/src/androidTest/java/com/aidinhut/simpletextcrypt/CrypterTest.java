/*
 * This file is part of SimpleTextCrypt.
 * Copyright (c) 2015, Aidin Gharibnavaz <aidin@aidinhut.com>
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

import android.app.Application;
import android.test.ApplicationTestCase;

import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/*
 * Implements stand-alone tests for Crypter class.
 */
public class CrypterTest extends ApplicationTestCase<Application> {

    public CrypterTest() {
        super(Application.class);
    }

    /*
     * Tests if encrypting and decrypting a data will result
     * in the same data.
     */
    @Test
    public void testEncryptDecryptValidation() throws Exception {
        String encKey = "uir43@#89djhncAd.,[]-+091jhdncq`";
        String data = "Hi there! I'm a test data that is going to be encrypted.";
        String decryptedData = null;

        String encryptedData = Crypter.encrypt(encKey, data);
        decryptedData = Crypter.decrypt(encKey, encryptedData);

        Assert.assertEquals(decryptedData, data);
    }

    /*
     * Tests if encrypting and decrypting a data will result
     * in the same data.
     */
    @Test
    public void testPasswordEncryptDecryptValidation() throws Exception {
        String password = "18736";
        String data = "Hi there! I'm a test data that is going to be encrypted.";
        String decryptedData = null;

        String encryptedData = Crypter.encrypt(password, data);
        decryptedData = Crypter.decrypt(password, encryptedData);

        Assert.assertEquals(decryptedData, data);
    }
}
