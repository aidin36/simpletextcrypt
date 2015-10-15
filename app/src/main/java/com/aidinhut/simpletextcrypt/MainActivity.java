package com.aidinhut.simpletextcrypt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.nio.charset.Charset;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // Showing settings activity, when `Settings' menu item clicked.
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onEncryptButtonClicked(View view) {
        try {
            setText(Crypter.encrypt(getEncryptionKey(), getText()));
        }
        catch (Exception error) {
            showErrorMessage(error.getMessage());
        }
    }

    public void onDecryptButtonClicked(View view) {
        try {
            setText(Crypter.decrypt(getEncryptionKey(), getText()));
        }
        catch (Exception error) {
            showErrorMessage(error.getMessage());
        }
    }

    public void onCopyButtonClicked(View view) {
        ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);

        clipboard.setText(getText());
    }

    public void onPasteButtonClicked(View view) {
        ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboard.hasText()) {
            setText(clipboard.getText().toString());
        }
    }

    @Override
    protected void onPause() {
        // Empty the text box, to protect privacy.
        setText("");

        // Finishing this activity, to get back to the lock screen.
        finish();

        super.onPause();
    }

    /*
     * Returns the text inside the Text Box.
     */
    private String getText() {
        EditText textBox = (EditText)findViewById(R.id.editText);
        return textBox.getText().toString();
    }

    /*
     * Sets the specified text in the Text Box.
     */
    private void setText(String input) {
        EditText textBox = (EditText)findViewById(R.id.editText);
        textBox.setText(input);
    }

    /*
     * Returns the encryption key from settings.
     *
     * @throw Exception: When key not found.
     */
    private String getEncryptionKey() throws Exception {
        SharedPreferences sharedPref = this.getSharedPreferences(Constants.PREFERENCES_KEY,
                                                                 Context.MODE_PRIVATE);
        String encKey = sharedPref.getString(Constants.ENCRYPTION_KEY_SETTINGS_KEY, null);

        if (encKey == null) {
            throw new Exception(getString(R.string.no_encryption_key_set_error));
        }

        return encKey;
    }

    /*
     * Shows the specified message, in a dialog box titled `Error'.
     */
    private void showErrorMessage(String message) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle(getString(R.string.error_title));
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }
}
