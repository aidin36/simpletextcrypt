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

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/*
 * Provides methods for encrypting and decrypting data.
 */
public class Crypter {

    public static String encrypt(String password, String input)
            throws UnsupportedEncodingException,
            GeneralSecurityException {

        // IV (Initialization Vector) generates randomly, and sends along with the message.
        // Since we use CBC mode, 1. IV *must* be unique in every message 2. IV does not need
        // to be secret.
        // See: https://stackoverflow.com/questions/3436864/sending-iv-along-with-cipher-text-safe

        String ivKey = getRandomIV();
        IvParameterSpec iv = new IvParameterSpec(ivKey.getBytes("UTF-8"));

        SecretKey secretKey = deriveKey(password, ivKey);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        byte[] encrypted = cipher.doFinal(input.getBytes());

        return ivKey + Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    public static String decrypt(String password, String input)
            throws UnsupportedEncodingException,
            GeneralSecurityException {
        // First 16 chars is the random IV.
        String ivKey = input.substring(0, 16);
        String encryptedMessage = input.substring(16);

        IvParameterSpec iv = new IvParameterSpec(ivKey.getBytes("UTF-8"));

        SecretKey secretKey = deriveKey(password, ivKey);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

        byte[] original = cipher.doFinal(Base64.decode(encryptedMessage, Base64.DEFAULT));

        return new String(original);
    }

    /*
     * Returns a random string of length 16 chars.
     */
    private static String getRandomIV() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 16; ++i) {
            builder.append((char)(random.nextInt(96) + 32));
        }

        return builder.toString();
    }

    /*
     * Derives a key from the specified password.
     */
    private static SecretKey deriveKey(String password, String salt)
        throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        char[] passwordChars = new char[password.length()];
        password.getChars(0, password.length(), passwordChars, 0);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(passwordChars, salt.getBytes(), 2000, 256);

        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }
}
