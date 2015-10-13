package com.aidinhut.simpletextcrypt;

import android.content.Context;
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onEncryptButtonClicked(View view) throws Exception {
        setText(Crypter.encrypt(getText()));
    }

    public void onDecryptButtonClicked(View view)  throws Exception {
        setText(Crypter.decrypt(getText()));
    }

    public void onCopyButtonClicked(View view) {
        ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);

        clipboard.setText(getText());
    }

    public void onPasteButtonClicked(View view) {
        ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboard.hasText())
        {
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

    private String getText() {
        EditText textBox = (EditText)findViewById(R.id.editText);
        return textBox.getText().toString();
    }

    private void setText(String input) {
        EditText textBox = (EditText)findViewById(R.id.editText);
        textBox.setText(input);
    }
}
