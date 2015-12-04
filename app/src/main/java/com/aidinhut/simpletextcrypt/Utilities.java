package com.aidinhut.simpletextcrypt;

import android.app.AlertDialog;
import android.content.Context;

/*
 * Provides some static methods.
 */
public class Utilities {

    /*
     * Shows the specified message, in a dialog box titled `Error'.
     */
    public static void showErrorMessage(String message, Context context) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle(context.getString(R.string.error_title));
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.show();
    }
}
