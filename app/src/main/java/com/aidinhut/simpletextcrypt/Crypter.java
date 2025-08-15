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

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.lambdapioneer.argon2kt.Argon2Kt;
import com.lambdapioneer.argon2kt.Argon2KtResult;
import com.lambdapioneer.argon2kt.Argon2Mode;

/*
 * Provides methods for encrypting and decrypting data.
 */
public class Crypter {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String LEGACY_TRANSFORMATION = "AES/CBC/PKCS5PADDING";
    private static final int IV_SIZE = 12; // Recommended size for GCM
    private static final int IV_SIZE_IN_BASE64 = 16;
    private static final int TAG_SIZE = 128; // Tag size in bits

    public static String encrypt(String password, String input)
            throws UnsupportedEncodingException,
            GeneralSecurityException {

        // IV (Initialization Vector) generates randomly, and sends along with the message.
        // Since we use CBC mode, 1. IV *must* be unique in every message 2. IV does not need
        // to be secret.
        // See: https://stackoverflow.com/questions/3436864/sending-iv-along-with-cipher-text-safe

        byte[] iv = getRandomIV();

        SecretKey secretKey = deriveKey(password, iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_SIZE, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
        byte[] encrypted = cipher.doFinal(input.getBytes());

        return Base64.encodeToString(iv, Base64.DEFAULT) + Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    public static String decrypt(String password, String input)
            throws UnsupportedEncodingException,
            GeneralSecurityException {
        // First 12 chars is the random IV.
        String ivString = input.substring(0, IV_SIZE_IN_BASE64);
        byte[] iv = Base64.decode(ivString, Base64.DEFAULT);
        byte[] inputBytes = Base64.decode(input.substring(IV_SIZE_IN_BASE64), Base64.DEFAULT);

        SecretKey secretKey = deriveKey(password, iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_SIZE, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
        byte[] original = cipher.doFinal(inputBytes);

        return new String(original);
    }

    public static String legacyDecrypt(String password, String input)
            throws UnsupportedEncodingException,
            GeneralSecurityException {
        // First 16 chars is the random IV.
        String ivKey = input.substring(0, 16);
        String encryptedMessage = input.substring(16);

        IvParameterSpec iv = new IvParameterSpec(ivKey.getBytes("UTF-8"));

        SecretKey secretKey = legacyDeriveKey(password, ivKey);

        Cipher cipher = Cipher.getInstance("LEGACY_TRANSFORMATION");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

        byte[] original = cipher.doFinal(Base64.decode(encryptedMessage, Base64.DEFAULT));

        return new String(original);
    }

    private static byte[] getRandomIV() {
        byte[] iv = new byte[IV_SIZE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return iv;
    }

    // TODO: Should I use IV as salt? Should I use another random string?
    //  Recommended salt is 16 bytes. 12 bytes (that I'm using) is the minimum.

    /**
     * Derives a secure key from the given password.
     * Uses Argon2 algorithm, which is the recommendation in 2025.
     */
    private static SecretKey deriveKey(String password, byte[] salt) {
        final Argon2Kt argon2Kt = new Argon2Kt();

        // Following parameters adjusted so it takes about 400ms on my own device.
        final Argon2KtResult hashResult =
                argon2Kt.hash(
                        Argon2Mode.ARGON2_ID,
                        password.getBytes(),
                        salt, 5,
                        64 * 1024);

        final byte[] passwordHash = hashResult.rawHashAsByteArray();

        return new SecretKeySpec(passwordHash, "AES");
    }

    /*
     * Derives a key from the specified password.
     * Uses older, insecure algorithms. Only used for decrypting old app's encrypted data.
     */
    private static SecretKey legacyDeriveKey(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        char[] passwordChars = new char[password.length()];
        password.getChars(0, password.length(), passwordChars, 0);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(passwordChars, salt.getBytes(), 2000, 256);

        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }
}
