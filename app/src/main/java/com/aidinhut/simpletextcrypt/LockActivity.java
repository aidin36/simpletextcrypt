package com.aidinhut.simpletextcrypt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;


public class LockActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
    }

    public void onUnlockButtonClicked(View view) {
        EditText passcodeBox = (EditText)findViewById(R.id.passcodeEditText);
        String passcode = passcodeBox.getText().toString();

        // Clearing passcode text box.
        passcodeBox.setText("");

        String savedPasscode;
        try {
            savedPasscode = SettingsManager.getInstance().tryGetPasscode(passcode, this);
        } catch (IllegalBlockSizeException| BadPaddingException error) {
            // The settings couldn't be decrypted using this passcode. It probably wrong.
            Utilities.showErrorMessage(getString(R.string.wrong_passcode_error), this);
            return;
        } catch (Exception error) {
            // Any other errors.
            Utilities.showErrorMessage(error.getMessage(), this);
            return;
        }

        if (savedPasscode.compareTo(passcode) != 0) {
            Utilities.showErrorMessage(getString(R.string.wrong_passcode_error), this);
            return;
        }

        // Right password. Continue to the other activity.
        Intent newIntent = new Intent(view.getContext(), MainActivity.class);
        startActivity(newIntent);
    }
}
