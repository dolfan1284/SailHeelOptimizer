package com.android.sailing;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class OptimizedHeel extends Activity {
	TextView heelText;
	TextView speedText;
	final String FILENAME = "sail_heel_speed_file";
	final int EOF = -1;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.drawable.optimumheel);
        heelText = (TextView) findViewById(R.id.optimumheeltext);
        speedText = (TextView) findViewById(R.id.optimumspeedtext);
        
        try {
			determineOptimumHeelAndSpeed();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        
	}
	private void determineOptimumHeelAndSpeed() throws FileNotFoundException {
		FileInputStream fis = openFileInput(FILENAME);
		StringBuffer strContent = new StringBuffer();
		int ch;
		try {
			while( (ch = fis.read()) != EOF){
		        strContent.append((char)ch);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//need to search and find the highest value that clumps a bunch
		//of nicely high values together.
		String maxHeel = null,maxSpeed = null;
		int speedIndex = 0,heelIndex = 0;
		for(int i=0;i<strContent.length();i++)
		{
			if(strContent.charAt(i)==' ')
				speedIndex=i;
			if(strContent.charAt(i)=='\u00B0')//was U00B0
			{
				heelIndex=i;
				maxHeel = strContent.substring(speedIndex+1, heelIndex-1);
				//need to ensure speeds are only 3,4,or 5 digits long(99.99 max speed), we aren't racing Americas cup boats!
				//need to use %1(0),%3 or %4 to see what the remainder is to determine what to subtract with...
				//if(speedIndex%1==0){
				//	maxSpeed = strContent.substring(speedIndex-1,speedIndex);
				//}
				if(speedIndex%3==0){
					maxSpeed = strContent.substring(speedIndex-3,speedIndex);
				}
				else if(speedIndex%4==0){
					maxSpeed = strContent.substring(speedIndex-4,speedIndex);
				}
				else if(speedIndex%5==0){
					maxSpeed = strContent.substring(speedIndex-5,speedIndex);
				}
				break;
			}
		}
		String checkHeel=null,checkSpeed=null;
		int previousSpeedIndex=0;
		for(int i=heelIndex+1;i<strContent.length();i++)
		{
			//need a previous value to determine how long the speed number is
			if(strContent.charAt(i)==' ')
				speedIndex=i;
			if(strContent.charAt(i)=='\u00B0')
			{
				previousSpeedIndex=heelIndex;
				heelIndex=i;
				checkHeel = strContent.substring(speedIndex+1, heelIndex-1);
				//need to ensure speeds are only 3,4,or 5 digits long(99.99 max speed), we aren't racing Americas cup boats!
				//need to use %3 or %4 to see what the remainder is to determine what to subtract with...
				//if((speedIndex-previousSpeedIndex+1)%1==0){
				//	checkSpeed = strContent.substring(speedIndex-1,speedIndex);
				//}
				if((speedIndex-previousSpeedIndex+1)%3==0/*speedIndex%3==0*/){
					checkSpeed = strContent.substring(speedIndex-3,speedIndex);
				}
				else if((speedIndex-previousSpeedIndex+1)%4==0){
					checkSpeed = strContent.substring(speedIndex-4,speedIndex);
				}
				else if((speedIndex-previousSpeedIndex+1)%5==0){
					checkSpeed = strContent.substring(speedIndex-5,speedIndex);
				}
				else if((speedIndex-previousSpeedIndex+1)%6==0){
					checkSpeed = strContent.substring(speedIndex-6,speedIndex);
				}
				
				if(checkSpeed.compareToIgnoreCase(maxSpeed)>=0){
					//then need to check if heel is less because if the boat
					//is going harder over with greater wind strength
					//that is NOT optimum for your boat and you would want
					//the lesser value for optimum speed
					if(checkHeel.compareToIgnoreCase(maxHeel)<0){
						maxHeel = checkHeel;
						maxSpeed = checkSpeed;
					}
				}
				
			}
		}
		heelText.setText(maxHeel.toString());
		speedText.setText(maxSpeed.toString());
		
		try {
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		try {
			determineOptimumHeelAndSpeed();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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
