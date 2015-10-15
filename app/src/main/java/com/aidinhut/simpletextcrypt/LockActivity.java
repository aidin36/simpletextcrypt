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


public class LockActivity extends ActionBarActivity {

    private String clickedDigits = "";
    private final String defaultPass = "1111";
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        SharedPreferences sharedPref = this.getSharedPreferences(Constants.PREFERENCES_KEY,
                                                                 Context.MODE_PRIVATE);
        pass = sharedPref.getString(Constants.PASSCODE_SETTINGS_KEY, defaultPass);
    }

    @Override
    protected void onResume() {
        // Clearing previously typed pass.
        clickedDigits = "";

        super.onResume();
    }

    public void onDigitButtonClicked(View view) {
        clickedDigits += ((Button)view).getText();

        if (pass.compareTo(clickedDigits) == 0)
        {
            // Right password. Continue to the other activity.
            Intent newIntent = new Intent(view.getContext(), MainActivity.class);
            startActivity(newIntent);
        }

        if (clickedDigits.length() > pass.length() ||
            pass.substring(0, clickedDigits.length()).compareTo(clickedDigits) != 0)
        {
            // Wrong password. Resetting code.
            clickedDigits = "";
        }
    }
}
