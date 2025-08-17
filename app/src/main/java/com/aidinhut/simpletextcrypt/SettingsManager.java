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

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.aidinhut.simpletextcrypt.exceptions.EncryptionKeyNotSet;
import com.aidinhut.simpletextcrypt.exceptions.SettingsNotSavedException;
import com.aidinhut.simpletextcrypt.exceptions.WrongPasscodeException;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

/*
 * Manages settings of the app.
 * It store settings encrypted.
 */
public class SettingsManager {

    private static SettingsManager instance;

    private String passcode = Constants.DEFAULT_PASSCODE;

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
     *
     * If it can't find a "version" stored in the settings, it will try
     * to do the decryption with the legacy algorithm. On success, it will
     * upgrade the settings to the new version.
     */
    public String tryGetPasscode(String passcode, Context context)
            throws UnsupportedEncodingException,
            GeneralSecurityException,
            WrongPasscodeException,
            SettingsNotSavedException {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.PREFERENCES_KEY,
                Context.MODE_PRIVATE);

        if (!sharedPref.contains(Constants.PASSCODE_SETTINGS_KEY)) {
            // There was no settings. First run. So there's no legacy settings to upgrade.
            this.setSettingsVersionIfNeeded(Constants.CURRENT_SETTINGS_VERSION, context);
            return Constants.DEFAULT_PASSCODE;
        }

        // Keeping passcode for later use.
        this.passcode = passcode;

        upgradeSettingsIfNeeded(passcode, context);

        try {
            return Crypter.decrypt(
                    this.passcode,
                    sharedPref.getString(Constants.PASSCODE_SETTINGS_KEY, Constants.DEFAULT_PASSCODE));
        } catch (IllegalBlockSizeException | BadPaddingException error) {
            // The settings couldn't be decrypted using this passcode. It probably wrong.
            throw new WrongPasscodeException(context);
        }
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

        return Crypter.decrypt(
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
                Crypter.encrypt(passcode, passcode));

        if (!prefEditor.commit()) {
            throw new SettingsNotSavedException(context);
        }

        this.setSettingsVersionIfNeeded(Constants.CURRENT_SETTINGS_VERSION, context);
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

        return Crypter.decrypt(
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
                Crypter.encrypt(this.passcode, key));

        if (!prefEditor.commit()) {
            throw new SettingsNotSavedException(context);
        }
    }

    public void setLockTimeout(String timeout, Context context) throws SettingsNotSavedException {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.PREFERENCES_KEY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();

        int intTimeout = 0;
        if (timeout != null && !timeout.isEmpty()) {
            intTimeout = Integer.parseInt(timeout);
        }

        prefEditor.putInt(Constants.LOCK_TIMEOUT_SETTINGS_KEY, intTimeout);

        if (!prefEditor.commit()) {
            throw new SettingsNotSavedException(context);
        }
    }

    public int getLockTimeout(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.PREFERENCES_KEY,
                Context.MODE_PRIVATE);

        if (!sharedPref.contains(Constants.LOCK_TIMEOUT_SETTINGS_KEY)) {
            return 0;
        }

        return sharedPref.getInt(Constants.LOCK_TIMEOUT_SETTINGS_KEY, 0);
    }

    /**
     * Currently, we either has version 2 or no version.
     * So, we won't try to re-set the version if it's exists. Saves some computation power!
     */
    public void setSettingsVersionIfNeeded(Integer version, Context context) throws SettingsNotSavedException {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.PREFERENCES_KEY,
                Context.MODE_PRIVATE);

        if (sharedPref.contains(Constants.SETTINGS_VERSION_KEY)) {
            return;
        }

        Log.i("simpletextcrypt.SettingsManager", "No settings version found. Going to add it.");

        SharedPreferences.Editor prefEditor = sharedPref.edit();

        prefEditor.putInt(Constants.SETTINGS_VERSION_KEY, version);

        if (!prefEditor.commit()) {
            throw new SettingsNotSavedException(context);
        }
    }

    private void upgradeSettingsIfNeeded(String passcode, Context context)
            throws UnsupportedEncodingException,
            GeneralSecurityException,
            WrongPasscodeException,
            SettingsNotSavedException {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.PREFERENCES_KEY,
                Context.MODE_PRIVATE);

        if (sharedPref.contains(Constants.SETTINGS_VERSION_KEY)) {
            // New settings. Nothing to do.
            return;
        }

        // It's the legacy settings.

        String storedPasscode = "";
        try {
            storedPasscode = Crypter.legacyDecrypt(
                    passcode,
                    sharedPref.getString(Constants.PASSCODE_SETTINGS_KEY, Constants.DEFAULT_PASSCODE));
        } catch (IllegalBlockSizeException | BadPaddingException error) {
            // The settings couldn't be decrypted using this passcode. It probably wrong.
            throw new WrongPasscodeException(context);
        }

        if (storedPasscode.compareTo(passcode) != 0) {
            throw new WrongPasscodeException(context);
        }

        Log.i("simpletextcrypt.SettingsManager", "Going to upgrade the settings from the legacy format.");

        // Upgrading the settings. The 'set' methods will use the new algorithm. So we just need
        // to call them.
        this.passcode = passcode;
        this.setPasscode(this.passcode, context);

        if (sharedPref.contains(Constants.ENCRYPTION_KEY_SETTINGS_KEY)) {
            String encryptionKey = Crypter.legacyDecrypt(
                    this.passcode,
                    sharedPref.getString(Constants.ENCRYPTION_KEY_SETTINGS_KEY, "")
            );
            this.setEncryptionKey(encryptionKey, context);
        }

        // Setting the version so we won't try to upgrade the next time.
        setSettingsVersionIfNeeded(Constants.CURRENT_SETTINGS_VERSION, context);
    }
}

