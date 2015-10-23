package com.aidinhut.simpletextcrypt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class SettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        loadPreviousSettings();
    }

    public void onSaveClicked(View view) {
        EditText encryptionKeyTextBox = (EditText)findViewById(R.id.encryptionKeyEditText);
        EditText passcodeTextBox = (EditText)findViewById(R.id.passcodeEditText);

        if (encryptionKeyTextBox.getText().toString().length() != 32) {
            showErrorMessage(getString(R.string.invalid_key_error));
            return;
        }
        if (passcodeTextBox.getText().toString().length() < 2) {
            showErrorMessage(getString(R.string.invalid_passcode_error));
            return;
        }

        // Saving settings.
        SharedPreferences sharedPref = this.getSharedPreferences(Constants.PREFERENCES_KEY,
                                                                 Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString(Constants.ENCRYPTION_KEY_SETTINGS_KEY,
                             encryptionKeyTextBox.getText().toString());

        prefEditor.putString(Constants.PASSCODE_SETTINGS_KEY,
                passcodeTextBox.getText().toString());

        if (!prefEditor.commit()) {
            showErrorMessage(getString(R.string.settings_save_error));
            return;
        }

        // Everything goes OK.
        finish();
    }

    private void showErrorMessage(String message) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle(getString(R.string.error_title));
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private void loadPreviousSettings() {
        EditText encryptionKeyTextBox = (EditText)findViewById(R.id.encryptionKeyEditText);
        EditText passcodeTextBox = (EditText)findViewById(R.id.passcodeEditText);

        SharedPreferences sharedPref = this.getSharedPreferences(Constants.PREFERENCES_KEY,
                Context.MODE_PRIVATE);

        encryptionKeyTextBox.setText(sharedPref.getString(
                Constants.ENCRYPTION_KEY_SETTINGS_KEY, ""));
        passcodeTextBox.setText(sharedPref.getString(
                Constants.PASSCODE_SETTINGS_KEY, "1111"));
    }
}
