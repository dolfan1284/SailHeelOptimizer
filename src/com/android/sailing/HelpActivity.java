package com.android.sailing;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class HelpActivity extends Activity {
	TextView helpText;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.drawable.help);
        helpText = (TextView) findViewById(R.id.helptext);
        helpText.setText("Program determines fastest speed and its associated heel to help " +
        		"optimize boat heel angle determinations.\n\nMake sure to setup preferences to " +
        		"KTS, MPH, or KM/H.\n\n"
        		+ "Currently optimized for the DROID RAZR MAXX");
	}
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
}
