package com.android.sailing;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


@SuppressLint({ "WorldReadableFiles", "WorldWriteableFiles" }) public class SailHeelOptimizerActivity extends Activity implements SensorEventListener {
    
    private SensorManager mSensorManager = null;
    private LocationManager mLocManager= null;
    private LocationListener mLocationListener = null;
    private Sensor mOrientation;
    private TextView heelAngleText = null;
    private TextView compassText = null;
    private TextView speedText = null;
    final String tag = "SailHeel";
    final String FILENAME = "sail_heel_speed_file";
    final float KNOTS = (float) 1.942615; //NOAA site says 1.9438445 but engineering site says 1.942615
    final float MPH = (float) 2.237;
    final float KMH = (float) 3.6;
    final int EOF = -1;
    private int mSensorDelay = 0;
    public static final String PREFS_NAME = "MyHeelAngleOptimizerPrefsFile";
    
	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
    	mLocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	// Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String speedSetting ;//local variable due to issues before
        speedSetting = settings.getString("speedSetting", null);
        //set to knots if not already set up prior to this onCreate
        if(speedSetting==null){
        	speedSetting = "knots";
  	        SharedPreferences.Editor editor = settings.edit();
  	        editor.putString("speedSetting", speedSetting);

  	        // Commit the edits!
  	        //editor.commit();
  	        editor.apply();
        }
    	
    	// Define a listener that responds to location updates
    	mLocationListener = new LocationListener() {
    	    public void onLocationChanged(Location location) {
    	      Location temp;
    	      //try to use a last best location so we don't use up battery power 
    	      //and have more relevant data displayed
    	      if( (temp = (mLocManager.getLastKnownLocation(Context.LOCATION_SERVICE)))!=null){
    	    	  try {
					calculateSpeed(temp);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
    	      } else
				try {
					calculateSpeed(location);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
    	    }

    	    public void onStatusChanged(String provider, int status, Bundle extras) {}

    	    public void onProviderEnabled(String provider) {}

    	    public void onProviderDisabled(String provider) {}
    	  };

    	mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        
    	//Register the listener with the Location Manager to receive location updates	
        //this uses Wifi and cell towers
        //mLocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
        
    	//Also can get GPS...
        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        
        //create actionBar, actually create a menu
        //requestFeature(FEATURE_ACTION_BAR);
        
    	setContentView(R.drawable.sail_stats);
        
    	heelAngleText = (TextView) findViewById(R.id.heelangletext);
    	speedText = (TextView) findViewById(R.id.speedtext);
    	compassText = (TextView) findViewById(R.id.compasstext);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.game_menu, menu);
	    return true;
	}
	
	public void invalidateOptionsMenu (){
		//call OnPrepareOptionsMenu(menu); here if you want to add/remove or dynamically create items in Android 3.0+
	}
	
	public boolean OnPrepareOptionsMenu(Menu menu){
		//You can use this method to efficiently enable/disable items or otherwise dynamically modify the contents. 
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        //case R.id.my_prefs:
	            //newGame();
	            //return true;
	        case R.id.help:
	        	
	        	//JDP:how would I send the user to grab focus of the actual help menu that is there??
	        	//HelpActivity.class.getDeclaredMethod(onResume());
	        	Toast.makeText(this, "Program tries to determine optimum boat heel angle." +
	        			"Make sure to setup preferences to KTS, MPH, or KM/H", Toast.LENGTH_LONG).show();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@SuppressWarnings("deprecation")
	protected void writeFileOutputDataFloat(float knotSpeed) {
		FileOutputStream fos = null;
		String degreeSymbol = "\u00B0";
		byte[] degreeByte = degreeSymbol.getBytes();
		String spaceSymbol = " ";
		byte[]spaceByte = spaceSymbol.getBytes();
		try {
			fos = openFileOutput(FILENAME, Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE|Context.MODE_APPEND);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String speedtext=(String)speedText.getText();
		byte [] speedtextString = speedtext.getBytes();
		//we want to remove the space part of the speed 
		//and the suffix at the end to only get raw numbers back
		try {
			fos.write(speedtextString, 0, speedtextString.length-3);
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//get the Heel Angle text from the screen that should be associated with the knotSpeed currently
		String text=(String)heelAngleText.getText();
		byte [] textString = text.getBytes();
		//we want to remove the Heel part and the degree at the end to only get raw numbers back
		try {
			//length w/ degree and subtract where we started from which is 4+1 because we are at 0 base
			fos.write(textString, 4, (textString.length-1)-6);
			fos.write(degreeByte,0,degreeByte.length);
			fos.write(spaceByte,0,spaceByte.length);
			//fos.write(textString,text.getBytes().length-1,1);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	protected void calculateSpeed(Location location) throws FileNotFoundException {
		//Use this to figure out how fast the boat is going and add that information into the database that
		//will eventually help to determine what angle of heel is best for speed
		setSpeedText(location);
		writeFileOutputDataFloat(location.getSpeed());
	}
	
	private void setSpeedText(Location temp){
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String speedSetting ;//local variable due to issues before
        //boolean silent = settings.getBoolean("silentMode", false);
        speedSetting = settings.getString("speedSetting", null);
        double result;
        if(speedSetting==null){
        	speedSetting = "knots";
  	        SharedPreferences.Editor editor = settings.edit();
  	        editor.putString("speedSetting", speedSetting);

  	        // Commit the edits!
  	        //editor.commit();
  	        editor.apply();
        }
        else{
			if(speedSetting.equalsIgnoreCase("KNOTS")){
	  		  double knotSpeed = temp.getSpeed() * KNOTS;
	  		  result = roundResult(knotSpeed);
	  		  speedText.setText(result + "KTS");  
	  	  	}
	  	  	else if(speedSetting.equalsIgnoreCase("KM/H"))
	  	  	{
	  	  		result = roundResult(temp.getSpeed());
		    	speedText.setText(result + "M/S");
	  	  	}
	  	  	else
	  	  	{
	  		  float mphSpeed = temp.getSpeed() * MPH;
	  		  result = roundResult(mphSpeed);
	  		  speedText.setText(result + "MPH");
	  	  	}
        }
        
	
	}
	
	private double roundResult(double num){ 
		double result = num * 100;
		result = Math.round(result);
		result = result / 100;
		return result;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
		//Also can get GPS...										3sec milliseconds,meters
        //mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5, mLocationListener);
		mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		Log.d(tag, "onAccuracyChanged: " + sensor + ", accuracy: " + accuracy);
	}
	
	@SuppressWarnings("deprecation")
	public void onSensorChanged(SensorEvent event)
    {
		//potentially find a small boat from the back with a mast and add that as a graphic with heel angle
		//for version 2.0
        
		//delay the setting of text until after two times that the onSensorChanged occurs
		if(mSensorDelay==2)
		{
			if(event.sensor.getType() == Sensor.TYPE_ORIENTATION)
		    {
				double result = roundResult(event.values[2]);
				result = Math.abs(result);
		    	heelAngleText.setText("Heel " + result + "\u00b0 ");
		    	result = roundResult(event.values[0]);
		    	compassText.setText("Compass " + result + "\u00B0");
		    }
			mSensorDelay=0;
		}
		else
			mSensorDelay++;
    }

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
		mLocManager.removeUpdates(mLocationListener);
	}

}