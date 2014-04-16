package orion.ms.sara;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GeneralSettingActivity extends Activity {
	//Button
	private Button speedUnitButton = null;
	private Button bearingUnitButton = null;
	private Button distanceUnitButton = null;
	private Button mapTypeButton = null;
	private Button speechRateButton = null;
	
	//intent to another activity
	private Intent intentToSpeed;
	private Intent intentToBearing;
	private Intent intentToDistance;
	private Intent intentToMapType;
	private Intent intentToSpeech;	
	
	//code for intent
	protected int SPEED_UNIT = 1;
	protected int BEARING_UNIT = 2;
	protected int DISTANCE_UNIT = 3;
	protected int MAP_TYPE = 4;
	protected int SPEECH_RATE = 5;
	
	//String with default value
	public static String speedUnit = "knots";
	public String bearingUnit = "port";
	public String portStarboard_Bearing = "0 on port";
	public String distanceUnit = "NM";
	public String mapType = "openSeaMap";
	public static float speechRate = 1.5f;
	
	//temp unit
	private String tempUnit = "";
	
	//confirmation alert dialog
	private AlertDialog.Builder dialog;
	
	private TextToSpeech tts = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generalsetting);
		setTitle("General Setting");
		
		//OnInitListener Creation
		OnInitListener onInitListener = new OnInitListener() {
			@Override
			public void onInit(int status) {
			}
		};
		
	    // textToSpeech creation
		tts = new TextToSpeech(this, onInitListener);
		tts.setSpeechRate(speechRate);
		
		//Buttons and their description
		speedUnitButton = (Button) findViewById(R.id.button1);
		speedUnitButton.setContentDescription("Speed unit setting");
		bearingUnitButton = (Button) findViewById(R.id.button2);
		bearingUnitButton.setContentDescription("Bearing unit setting");
		distanceUnitButton = (Button) findViewById(R.id.button3);
		distanceUnitButton.setContentDescription("Distance unit setting");
		mapTypeButton = (Button) findViewById(R.id.button4);
		mapTypeButton.setContentDescription("Map type setting");
		speechRateButton = (Button) findViewById(R.id.button5);
		speechRateButton.setContentDescription("Speech rate setting");
		
		//alert dialog creation
		dialog = new AlertDialog.Builder(this);
		
		//intent creations
		intentToSpeed = new Intent(GeneralSettingActivity.this,SpeedUnitActivity.class);
		intentToBearing = new Intent(GeneralSettingActivity.this,BearingUnitActivity.class);
		intentToDistance = new Intent(GeneralSettingActivity.this,DistanceUnitActivity.class);
		intentToMapType = new Intent(GeneralSettingActivity.this,MapTypeActivity.class);
		intentToSpeech = new Intent(GeneralSettingActivity.this,SpeechRateActivity.class);
		
		//onClickListener
		speedUnitButton.setOnClickListener(new OnClickListener() {
			//onClick creation
			@Override
			public void onClick(View v) {
				//start speed setting
				startActivityForResult(intentToSpeed, SPEED_UNIT);
			}//end onClick
		});//end setOnlick
		
		bearingUnitButton.setOnClickListener(new OnClickListener() {
			//onClick creation
			@Override
			public void onClick(View v) {
				//start bearing setting
				startActivityForResult(intentToBearing, BEARING_UNIT);
			}//end onClick
		});//end setOnlick
		
		distanceUnitButton.setOnClickListener(new OnClickListener() {
			//onClick creation
			@Override
			public void onClick(View v) {
				//start distance setting
				startActivityForResult(intentToDistance, DISTANCE_UNIT);
			}//end onClick
		});//end setOnlick
		
		mapTypeButton.setOnClickListener(new OnClickListener() {
			//onClick creation
			@Override
			public void onClick(View v) {
				//start map type setting
				startActivityForResult(intentToMapType, MAP_TYPE);
			}//end onClick
		});//end setOnlick
		
		speechRateButton.setOnClickListener(new OnClickListener() {
			//onClick creation
			@Override
			public void onClick(View v) {
				//start speech rate setting
				startActivityForResult(intentToSpeech, SPEECH_RATE);
			}//end onClick
		});//end setOnlick

	}//end onCreate
	
	//to calculate port/starboard
	public void toPortStarboard(double angle){
		double bearing = MyLocationListener.bearing;
		double heading = Double.parseDouble(MyLocationListener.heading);
		double angle_tmp = angle;
		if (bearing - heading < 0 ) angle_tmp += 360;
		if (angle_tmp < 180) portStarboard_Bearing = angle_tmp + "on starboard";
		else portStarboard_Bearing = (360 - angle_tmp) + " on port";
	}
	
	//Intent to handle receive parameters from other activities
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intentFromAnother){
	    super.onActivityResult(requestCode, resultCode, intentFromAnother);
	    //from speed unit activity
	    if(requestCode==SPEED_UNIT){
	    	tempUnit = intentFromAnother.getStringExtra("choosingSpeedUnit");
	    	if(!tempUnit.equals("NONE")){
	    		//didn't save the change yet
	    		dialog.setTitle("The speed unit changes to "+tempUnit);
	    		dialog.setIcon(android.R.drawable.ic_menu_edit);
	    		dialog.setMessage("Do you want to save?");
	    		dialog.setPositiveButton("No", null);
	    		dialog.setNeutralButton("Yes", new DialogInterface.OnClickListener() {
	    			//save the changed speed unit
					@SuppressWarnings("static-access")
					@Override
					public void onClick(DialogInterface dialog, int which) {
						speedUnit = tempUnit;
						tts.speak("Speed Unit changes to "+speedUnit, tts.QUEUE_FLUSH, null);
						//Log.i("Speed unit change", speedUnit);
					}
				});
	    		dialog.show();
	    	}//end if
	    }//end speedUnit
	    
	    //from bearing unit activity
	    else if(requestCode==BEARING_UNIT){
	    	
	    }
	    //from distance unit activity
	    else if(requestCode==DISTANCE_UNIT){
	    	
	    }
	    //from map type activity
	    else if(requestCode==MAP_TYPE){
	    	
	    }
	    //from speech rate activity
	    else if(requestCode==SPEECH_RATE){
	    	//speechRate = intentFromAnother.getFloatExtra("speechRate", 2);
	    }
	}

	//  action bar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_generalsetting, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem Item){
		switch (Item.getItemId()) {
		case R.id.navigation:
			finish();
			break;
		default:
			break;
		}
		return false;
	}


}
