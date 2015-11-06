package com.aidinhut.simpletextcrypt;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/*
 * Provides methods for encrypting and decrypting data.
 */
public class Crypter {

    public static String encrypt(String encKey, String input)
        throws UnsupportedEncodingException,
            GeneralSecurityException {
        return encrypt(encKey.getBytes("UTF-8"), input);
    }

    public static String encrypt(byte[] encKey, String input)
            throws UnsupportedEncodingException,
            GeneralSecurityException {

        // IV (Initialization Vector) generates randomly, and sends along with the message.
        // Since we use CBC mode, 1. IV *must* be unique in every message 2. IV does not need
        // to be secret.
        // See: https://stackoverflow.com/questions/3436864/sending-iv-along-with-cipher-text-safe

        String ivKey = getRandomIV();
        IvParameterSpec iv = new IvParameterSpec(ivKey.getBytes("UTF-8"));

        SecretKeySpec skeySpec = new SecretKeySpec(encKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

        byte[] encrypted = cipher.doFinal(input.getBytes());

        return ivKey + Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    public static String decrypt(String encKey, String input)
        throws UnsupportedEncodingException,
            GeneralSecurityException {
        return decrypt(encKey.getBytes("UTF-8"), input);
    }

    public static String decrypt(byte[] encKey, String input)
            throws UnsupportedEncodingException,
            GeneralSecurityException {
        // First 16 chars is the random IV.
        String ivKey = input.substring(0, 16);
        String encryptedMessage = input.substring(16);

        IvParameterSpec iv = new IvParameterSpec(ivKey.getBytes("UTF-8"));

        SecretKeySpec skeySpec = new SecretKeySpec(encKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

        byte[] original = cipher.doFinal(Base64.decode(encryptedMessage, Base64.DEFAULT));

        return new String(original);
    }

    public static String encryptWithPassword(String password, String input)
            throws UnsupportedEncodingException,
            GeneralSecurityException {
        return encrypt(getHashOf(password), input);
    }

    public static String decryptWithPassword(String password, String input)
            throws UnsupportedEncodingException,
            GeneralSecurityException {
        return decrypt(getHashOf(password), input);
    }

    private static String getRandomIV() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 16; ++i) {
            builder.append((char)(random.nextInt(96) + 32));
        }

        return builder.toString();
    }

    private static byte[] getHashOf(String input)
        throws NoSuchAlgorithmException
    {
        MessageDigest hashDigest = MessageDigest.getInstance("SHA-256");

        hashDigest.reset();
        return hashDigest.digest(input.getBytes());
    }
}
