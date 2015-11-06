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
            Utilities.showErrorMessage(getString(R.string.invalid_key_error), this);
            return;
        }
        if (passcodeTextBox.getText().toString().length() < 2) {
            Utilities.showErrorMessage(getString(R.string.invalid_passcode_error), this);
            return;
        }

        // Saving settings.
        try {
            SettingsManager.getInstance().setPasscode(passcodeTextBox.getText().toString(), this);
            SettingsManager.getInstance().setEncryptionKey(encryptionKeyTextBox.getText().toString(), this);
        } catch (Exception error) {
            Utilities.showErrorMessage(error.getMessage(), this);
            return;
        }

        // Everything goes OK.
        finish();
    }

    private void loadPreviousSettings() {
        EditText encryptionKeyTextBox = (EditText)findViewById(R.id.encryptionKeyEditText);
        EditText passcodeTextBox = (EditText)findViewById(R.id.passcodeEditText);

        try {
            encryptionKeyTextBox.setText(SettingsManager.getInstance().getEncryptionKey(this));
            passcodeTextBox.setText(SettingsManager.getInstance().getPasscode(this));
        } catch (Exception error) {
            Utilities.showErrorMessage(error.getMessage(), this);
        }
    }
}
