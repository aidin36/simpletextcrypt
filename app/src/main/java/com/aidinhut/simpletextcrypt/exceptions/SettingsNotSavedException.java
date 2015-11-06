package com.aidinhut.simpletextcrypt.exceptions;

import android.content.Context;

import com.aidinhut.simpletextcrypt.R;

/*
 * Throws if settings could not be saved for whatever reason.
 */
public class SettingsNotSavedException extends Exception {

    public SettingsNotSavedException(Context context) {
        super(context.getString(R.string.settings_save_error));
    }
}
