package com.android.sailing;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;

public class PreferencesActivity extends Activity {
	public static final String PREFS_NAME = "MyHeelAngleOptimizerPrefsFile";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		
		setContentView(R.drawable.prefs);
		
		final RadioButton radio_kts = (RadioButton) findViewById(R.id.radio_kts);
        final RadioButton radio_mph = (RadioButton) findViewById(R.id.radio_mph);
        final RadioButton radio_kmh = (RadioButton) findViewById(R.id.radio_kmh);
        radio_kts.setOnClickListener(radio_listener);
        radio_mph.setOnClickListener(radio_listener);
        radio_kmh.setOnClickListener(radio_listener);
        
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    //boolean silent = settings.getBoolean("silentMode", false);
        String speedSetting;
	    if((speedSetting = settings.getString("speedSetting", null))!=null)
	    {
	    	if(speedSetting.equalsIgnoreCase("KNOTS")){
	    		radio_kts.setChecked(true);
	    		radio_mph.setChecked(false);
	    		radio_kmh.setChecked(false);
	    	}
	    	else if(speedSetting.equalsIgnoreCase("MPH")){
	    		radio_kts.setChecked(false);
	    		radio_mph.setChecked(true);
	    		radio_kmh.setChecked(false);
	    	}
	    	else {
	    		radio_kts.setChecked(false);
	    		radio_mph.setChecked(false);
	    		radio_kmh.setChecked(true);
	    	}
	    	
	    }
	    else
	    {
	    	//Shouldn't get here BUT...
	    	//set it to knots since that is what SailHeelOptimizerActivity will set it to.
	    	radio_kts.setChecked(true);
    		radio_mph.setChecked(false);
    		radio_kmh.setChecked(false);
	    }
	}
	
	private OnClickListener radio_listener = new OnClickListener() {
        public void onClick(View v) {
            // Perform action on clicks
            RadioButton rb = (RadioButton) v;
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("speedSetting", (String) rb.getText());
            //commit the change so that the other screen will start showing the new value
            //editor.commit();
            editor.apply();
        }
    };
    
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
		//SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    //SharedPreferences.Editor editor = settings.edit();
	    //editor.putString("speedSetting", speedSetting);

	    // Commit the edits!
	    //editor.commit();
	}
	
	@Override
    protected void onStop(){
       super.onStop();

      // We need an Editor object to make preference changes.
      // All objects are from android.context.Context
      //SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
      //SharedPreferences.Editor editor = settings.edit();
      //editor.putString("speedSetting", speedSetting);
      //editor.putBoolean("silentMode", mSilentMode);

      // Commit the edits!
      //editor.commit();
    }
}
