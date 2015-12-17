/*
 * This file is part of SimpleTextCrypt.
 * Copyright (c) 2015, Aidin Gharibnavaz <aidin@aidinhut.com>
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

        if (encryptionKeyTextBox.getText().toString().length() < 3) {
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

    @Override
    protected void onPause() {
        // This activity won't lock. So, if the user send the app to the background while on
        // the settings activity, anyone can get back to it without the need to enter passcode.
        finish();

        super.onPause();
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
