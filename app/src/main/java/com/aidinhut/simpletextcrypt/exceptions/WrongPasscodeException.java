package com.aidinhut.simpletextcrypt.exceptions;

import android.content.Context;

import com.aidinhut.simpletextcrypt.R;

/*
 * Throws if the passcode entered by user is wrong.
 */
public class WrongPasscodeException extends Exception {

    public WrongPasscodeException(Context context) {
        super(context.getString(R.string.wrong_passcode_error));
    }
}
