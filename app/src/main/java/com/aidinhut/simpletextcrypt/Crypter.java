package com.aidinhut.simpletextcrypt;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by aidin on 10/9/15.
 */
public class Crypter {

    private static String ivKey = "Rt9832jdnc@3kfP.";
    private static String encKey = "4rFdhfbv*&.,;[DwqncSq~dcmPjf.,LG";

    public static String encrypt(String input)
        throws UnsupportedEncodingException,
               NoSuchAlgorithmException,
               NoSuchPaddingException,
               InvalidKeyException,
               InvalidAlgorithmParameterException,
               IllegalBlockSizeException,
               BadPaddingException
    {
        IvParameterSpec iv = new IvParameterSpec(ivKey.getBytes("UTF-8"));

        SecretKeySpec skeySpec = new SecretKeySpec(encKey.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

        byte[] encrypted = cipher.doFinal(input.getBytes());

        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    public static String decrypt(String input)
        throws UnsupportedEncodingException,
               NoSuchAlgorithmException,
               NoSuchPaddingException,
               InvalidKeyException,
               InvalidAlgorithmParameterException,
               IllegalBlockSizeException,
               BadPaddingException
    {
        IvParameterSpec iv = new IvParameterSpec(ivKey.getBytes("UTF-8"));

        SecretKeySpec skeySpec = new SecretKeySpec(encKey.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

        byte[] original = cipher.doFinal(Base64.decode(input, Base64.DEFAULT));

        return new String(original);
    }
}
