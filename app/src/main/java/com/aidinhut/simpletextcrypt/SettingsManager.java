package com.aidinhut.simpletextcrypt;

import android.content.Context;
import android.content.SharedPreferences;

import com.aidinhut.simpletextcrypt.exceptions.EncryptionKeyNotSet;
import com.aidinhut.simpletextcrypt.exceptions.SettingsNotSavedException;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

/*
 * Manages settings of the app.
 * It store settings encrypted.
 */
public class SettingsManager {

    private static SettingsManager instance;

    private String passcode = "1111";

    private SettingsManager() {
    }

    public static SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }

        return instance;
    }

    /*
     * Passcode is also the key for the encryption of the settings.
     * This method tries to decrypt the settings with the given passcode.
     * It also keeps the specified passcode for later decryption.
     */
    public String tryGetPasscode(String passcode, Context context)
            throws UnsupportedEncodingException,
            GeneralSecurityException {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.PREFERENCES_KEY,
                Context.MODE_PRIVATE);

        if (!sharedPref.contains(Constants.PASSCODE_SETTINGS_KEY)) {
            return Constants.DEFAULT_PASSCODE;
        }

        // Keeping passcode for later use.
        this.passcode = passcode;

        return Crypter.decryptWithPassword(
                this.passcode,
                sharedPref.getString(Constants.PASSCODE_SETTINGS_KEY, Constants.DEFAULT_PASSCODE));
    }

    /*
     * This is the passcode user should type in LockActicity to open the lock.
     */
    public String getPasscode(Context context)
            throws UnsupportedEncodingException,
            GeneralSecurityException {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.PREFERENCES_KEY,
                Context.MODE_PRIVATE);

        if (!sharedPref.contains(Constants.PASSCODE_SETTINGS_KEY)) {
            return Constants.DEFAULT_PASSCODE;
        }

        return Crypter.decryptWithPassword(
                    this.passcode,
                    sharedPref.getString(Constants.PASSCODE_SETTINGS_KEY, Constants.DEFAULT_PASSCODE));
    }

    /*
     * This is the passcode user typed on LockActivity.
     * It will be used to encrypt/decrypt settings.
     *
     * @throw SettingsNotSavedException: If settings could not be saved.
     */
    public void setPasscode(String passcode, Context context)
            throws UnsupportedEncodingException,
            GeneralSecurityException,
            SettingsNotSavedException {

        this.passcode = passcode;

        SharedPreferences sharedPref = context.getSharedPreferences(Constants.PREFERENCES_KEY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();

        prefEditor.putString(Constants.PASSCODE_SETTINGS_KEY,
                Crypter.encryptWithPassword(passcode, passcode));

        if (!prefEditor.commit()) {
            throw new SettingsNotSavedException(context);
        }
    }

    /*
     * Gets the key that the text on the textbox should be encrypted with.
     *
     * @throw EncryptionKeyNotSet: If the encryption key doesn't set in the settings.
     */
    public String getEncryptionKey(Context context)
            throws UnsupportedEncodingException,
            GeneralSecurityException,
            EncryptionKeyNotSet {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.PREFERENCES_KEY,
                Context.MODE_PRIVATE);

        if (!sharedPref.contains(Constants.ENCRYPTION_KEY_SETTINGS_KEY)) {
            return "";
        }

        return Crypter.decryptWithPassword(
                this.passcode,
                sharedPref.getString(Constants.ENCRYPTION_KEY_SETTINGS_KEY, ""));
    }

    /*
     * Sets the key that the text on the textbox should be encrypted with.
     *
     * @throw SettingsNotSavedException: If settings could not be saved.
     */
    public void setEncryptionKey(String key, Context context)
            throws UnsupportedEncodingException,
            GeneralSecurityException,
            SettingsNotSavedException {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.PREFERENCES_KEY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();

        prefEditor.putString(Constants.ENCRYPTION_KEY_SETTINGS_KEY,
                Crypter.encryptWithPassword(this.passcode, key));

        if (!prefEditor.commit()) {
            throw new SettingsNotSavedException(context);
        }
    }
}
