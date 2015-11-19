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

        String encryptedData = Crypter.encryptWithPassword(password, data);
        decryptedData = Crypter.decryptWithPassword(password, encryptedData);

        Assert.assertEquals(decryptedData, data);
    }
}
