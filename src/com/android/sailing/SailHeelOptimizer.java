package com.android.sailing;

import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.TabHost;

@SuppressLint("Wakelock") @SuppressWarnings("deprecation")
public class SailHeelOptimizer extends TabActivity {
	
	private WakeLock mWakeLock;
	private PowerManager mPowerManager;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     // Get an instance of the PowerManager
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
    	mWakeLock = mPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, getClass().getName());
		
        setContentView(R.layout.main);

        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, SailHeelOptimizerActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("sailstats").setIndicator("Sail Stats",
                          res.getDrawable(R.drawable.smallboat))
                      .setContent(intent);
        tabHost.addTab(spec);
        
     // Do the same for the other tabs
        intent = new Intent().setClass(this, OptimizedHeel.class);
        spec = tabHost.newTabSpec("optimumheel").setIndicator("Optimum Heel",
                res.getDrawable(R.drawable.heeledover))
            .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, PreferencesActivity.class);
        spec = tabHost.newTabSpec("prefs").setIndicator("Preferences",
                          res.getDrawable(R.drawable.content_edit))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, HelpActivity.class);
        spec = tabHost.newTabSpec("help").setIndicator("Help",
                          res.getDrawable(R.drawable.action_help))
                      .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }
    
    @Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		/* When the activity is resumed, we acquire a wake-lock so that the
         * screen stays on, so that the user can watch what is going on in real-time.
         */
        mWakeLock.acquire();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle outState){
		super.onRestoreInstanceState(outState);
		
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
    protected void onStop(){
       super.onStop();
    }
	
	
}
