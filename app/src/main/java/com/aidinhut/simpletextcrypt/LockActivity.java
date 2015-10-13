package com.aidinhut.simpletextcrypt;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class LockActivity extends ActionBarActivity {

    private String clickedDigits = "";
    private final String pass = "1829";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
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
