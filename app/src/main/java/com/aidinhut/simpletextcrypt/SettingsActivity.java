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

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        loadPreviousSettings();
    }

    public void onSaveClicked(View view) {
        EditText passphraseTextBox = (EditText)findViewById(R.id.passphraseEditText);
        EditText passcodeTextBox = (EditText)findViewById(R.id.passcodeEditText);
        EditText lockTimeoutTextBox = (EditText)findViewById(R.id.lockTimeoutEditText);

        if (passphraseTextBox.getText().toString().length() < 3) {
            Utilities.showErrorMessage(getString(R.string.invalid_passphrase_error), this);
            return;
        }
        if (passcodeTextBox.getText().toString().length() < 2) {
            Utilities.showErrorMessage(getString(R.string.invalid_passcode_error), this);
            return;
        }

        // Saving settings.
        try {
            SettingsManager.getInstance().setPasscode(passcodeTextBox.getText().toString(), this);
            SettingsManager.getInstance().setPassphrase(passphraseTextBox.getText().toString(), this);
            SettingsManager.getInstance().setLockTimeout(lockTimeoutTextBox.getText().toString(), this);
        } catch (Exception error) {
            Utilities.showErrorMessage(error.getMessage(), this);
            return;
        }

        // Everything goes OK.
        finish();
    }

    public void onKeyCleanClicked(View view) {
        EditText encryptionKeyTextBox = (EditText)findViewById(R.id.passphraseEditText);
        encryptionKeyTextBox.setText("");
    }

    @Override
    protected void onPause() {
        // This activity won't lock. So, if the user send the app to the background while on
        // the settings activity, anyone can get back to it without the need to enter passcode.
        finish();

        super.onPause();
    }

    private void loadPreviousSettings() {
        EditText encryptionKeyTextBox = (EditText)findViewById(R.id.passphraseEditText);
        EditText passcodeTextBox = (EditText)findViewById(R.id.passcodeEditText);
        EditText lockTimeoutTextBox = (EditText)findViewById(R.id.lockTimeoutEditText);

        try {
            encryptionKeyTextBox.setText(SettingsManager.getInstance().getPassphrase(this));
            passcodeTextBox.setText(SettingsManager.getInstance().getPasscode(this));
            lockTimeoutTextBox.setText(Integer.toString(SettingsManager.getInstance().getLockTimeout(this)));
        } catch (Exception error) {
            Utilities.showErrorMessage(error.getMessage(), this);
        }
    }
}
