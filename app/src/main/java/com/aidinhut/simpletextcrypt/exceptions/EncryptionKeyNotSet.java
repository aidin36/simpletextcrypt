package com.aidinhut.simpletextcrypt.exceptions;

import android.content.Context;

import com.aidinhut.simpletextcrypt.R;

/*
 * Throws if encryption key doesn't set in the settings.
 */
public class EncryptionKeyNotSet extends Exception {

    public EncryptionKeyNotSet(Context context) {
        super(context.getString(R.string.no_encryption_key_set_error));
    }
}
