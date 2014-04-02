package orion.ms.sara;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.speech.tts.TextToSpeech.OnInitListener;

public class AutoSetting extends Activity{
	
	public static final String PREFS_NAME = "MyPrefsFile";
	
	//private Intent intentAuto_setting;
	//private Intent intentWaypoint_setting;
	private Intent intentMain;
	
	private TextToSpeech tts = null;
	
	private CheckBox SpeedAutoCheckBox = null;
	private CheckBox HeadingAutoCheckBox = null;
	private CheckBox DistanceAutoCheckBox = null;
	private CheckBox BearingAutoCheckBox = null;


	private TextView textViewSpeedTreshold = null;
	private TextView textViewSpeedTimeTreshold = null;
	
	private TextView textViewHeadingTreshold = null;
	private TextView textViewHeadingTimeTreshold = null;
	
	private TextView textViewDistanceTimeTreshold = null;
	
	private TextView textViewBearingTreshold = null;
	private TextView textViewBearingTimeTreshold = null;
	

	private Button SaveSettingButton = null;

	
	// declare change speed treshold buttons
	private Button IncreaseSpeedTresholdButton = null;
	private Button DecreaseSpeedTresholdButton = null;
	
	
	// declare change speed time treshold buttons
	private Button IncreaseSpeedTimeTresholdButton = null;
	private Button DecreaseSpeedTimeTresholdButton = null;
	
	// declare change heading treshold buttons
	private Button IncreaseHeadingTresholdButton = null;
	private Button DecreaseHeadingTresholdButton = null;
	
	// declare change heading time treshold buttons
	private Button IncreaseHeadingTimeTresholdButton = null;
	private Button DecreaseHeadingTimeTresholdButton = null;
	
	// declare change distance time treshold buttons
	private Button IncreaseDistanceTimeTresholdButton = null;
	private Button DecreaseDistanceTimeTresholdButton = null;
	
	// declare change bearing treshold buttons
	private Button IncreaseBearingTresholdButton = null;
	private Button DecreaseBearingTresholdButton = null;
	
	// declare change bearing time treshold buttons
	private Button IncreaseBearingTimeTresholdButton = null;
	private Button DecreaseBearingTimeTresholdButton = null;
	

	private double speedTreshold = 1.0; 
	private long speedTimeTreshold = 5;
	private double speedTresholdStep = 0.1;
	private long speedTimeTresholdStep = 1;

	private double headingTreshold = 10.0; 
	private long headingTimeTreshold = 5;	
	private double headingTresholdStep = 1.0;
	private long headingTimeTresholdStep = 1;
	
	private long distanceTimeTreshold = 5;
	private long distanceTimeTresholdStep = 1;

	
	private double bearingTreshold = 10.0;
	private long bearingTimeTreshold = 5;
	private double bearingTresholdStep = 1.0;
	private long bearingTimeTresholdStep = 1;

	
	
	
	
	private boolean isAutoSpeed = true;
	private boolean isAutoHeading = true;
	private boolean isAutoDistance = true;
	private boolean isAutoBearing = true;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_autosetting);
		
	    // Restore preferences
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		this.speedTreshold = Double.parseDouble(settings.getString("speedTreshold", "1.0"));
	    this.speedTimeTreshold = settings.getLong("speedTimeTreshold", 5);
	    
		this.headingTreshold = Double.parseDouble(settings.getString("headingTreshold", "10.0"));
	    this.headingTimeTreshold = settings.getLong("headingTimeTreshold", 5);
	    
	    this.distanceTimeTreshold = settings.getLong("distanceTimeTreshold", 5);
	    
	    this.bearingTreshold = Double.parseDouble(settings.getString("bearingTreshold", "10.0"));
	    this.bearingTimeTreshold = settings.getLong("bearingTimeTreshold", 5);
	    
	    this.isAutoSpeed = settings.getBoolean("isAutoSpeed", true);
	    this.isAutoHeading = settings.getBoolean("isAutoHeading", true);
	    this.isAutoDistance = settings.getBoolean("isAutoDistance", true);
	    this.isAutoBearing = settings.getBoolean("isAutoBearing", true);

	    
	    // save setting button
		SaveSettingButton = (Button) findViewById(R.id.SaveSettingButton);
		SaveSettingButton.setContentDescription("Save");


	    //setSilent(silent);
		SpeedAutoCheckBox = (CheckBox) findViewById(R.id.speedAutoCheckBox);
	    SpeedAutoCheckBox.setChecked(this.isAutoSpeed);
	    
		HeadingAutoCheckBox = (CheckBox) findViewById(R.id.headingAutoCheckBox);
	    HeadingAutoCheckBox.setChecked(this.isAutoHeading);
	    
		DistanceAutoCheckBox = (CheckBox) findViewById(R.id.DistanceAutoCheckBox);
		DistanceAutoCheckBox.setChecked(this.isAutoDistance);
	    
		BearingAutoCheckBox = (CheckBox) findViewById(R.id.BearingAutoCheckBox);
	    BearingAutoCheckBox.setChecked(this.isAutoBearing);

		//intent creation
	    intentMain = new Intent(AutoSetting.this,MainActivity.class);
		//intentWaypoint_setting = new Intent(AutoSetting.this,Waypoint.class);
		//intentAuto_setting = new Intent(AutoSetting.this,AutoSetting.class);

	    //speed treshold view
		textViewSpeedTreshold = (TextView) findViewById(R.id.speedTresholdView);
		textViewSpeedTreshold.setText(getResources().getString(R.string.speedtreshold)+ " "  + speedTreshold + " " + getResources().getString(R.string.SpeedUnit));
	    textViewSpeedTreshold.setContentDescription(getResources().getString(R.string.speedtreshold) + speedTreshold + " " + getResources().getString(R.string.SpeedUnit));
	    
	    //speed time treshold view
	    textViewSpeedTimeTreshold = (TextView) findViewById(R.id.speedTimeTresholdView3);
		textViewSpeedTimeTreshold.setText(getResources().getString(R.string.speedtimetreshold)+ " "  + speedTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
	    textViewSpeedTimeTreshold.setContentDescription(getResources().getString(R.string.speedtimetreshold) + speedTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
	    
	    //heading treshold view
	    textViewHeadingTreshold = (TextView) findViewById(R.id.HeadingTresholdView);
		textViewHeadingTreshold.setText(getResources().getString(R.string.headingtreshold)+ " "  + Integer.toString((int)headingTreshold) + " " + getResources().getString(R.string.HeadingUnit));
	    textViewHeadingTreshold.setContentDescription(getResources().getString(R.string.headingtreshold) + Integer.toString((int)headingTreshold) + " " + getResources().getString(R.string.HeadingUnit));
	    
	    //heading time treshold view
	    textViewHeadingTimeTreshold = (TextView) findViewById(R.id.HeadingTimeTresholdView);
		textViewHeadingTimeTreshold.setText(getResources().getString(R.string.headingtimetreshold)+ " "  + headingTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
	    textViewHeadingTimeTreshold.setContentDescription(getResources().getString(R.string.headingtimetreshold) + headingTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
	    
	    //distance time treshold view
	    textViewDistanceTimeTreshold = (TextView) findViewById(R.id.DistanceTimeTresholdView);
	    textViewDistanceTimeTreshold.setText(getResources().getString(R.string.distancetimetreshold)+ " "  + distanceTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
	    textViewDistanceTimeTreshold.setContentDescription(getResources().getString(R.string.distancetimetreshold) + distanceTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
	    
	    //bearing treshold view
	    textViewBearingTreshold = (TextView) findViewById(R.id.BearingTresholdView);
	    textViewBearingTreshold.setText(getResources().getString(R.string.bearingtreshold)+ " "  + Integer.toString((int)bearingTreshold) + " " + getResources().getString(R.string.HeadingUnit));
	    textViewBearingTreshold.setContentDescription(getResources().getString(R.string.bearingtreshold) + Integer.toString((int)bearingTreshold) + " " + getResources().getString(R.string.HeadingUnit));
	    
	    //bearing time treshold view
	    textViewBearingTimeTreshold = (TextView) findViewById(R.id.BearingTimeTresholdView);
	    textViewBearingTimeTreshold.setText(getResources().getString(R.string.bearingtimetreshold)+ " "  + bearingTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
	    textViewBearingTimeTreshold.setContentDescription(getResources().getString(R.string.bearingtimetreshold) + bearingTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
	    
	    
	    // increase&decrease speed treshold button
		IncreaseSpeedTresholdButton = (Button) findViewById(R.id.IncreaseSpeedTresholdButton);
		IncreaseSpeedTresholdButton.setContentDescription(getResources().getString(R.string.increase) + " " + getResources().getString(R.string.speedtreshold));
		DecreaseSpeedTresholdButton = (Button) findViewById(R.id.DecreaseSpeedTresholdButton);
		DecreaseSpeedTresholdButton.setContentDescription(getResources().getString(R.string.decrease) + " " + getResources().getString(R.string.speedtreshold));
	    
		// increase&decrease speed time treshold button
		IncreaseSpeedTimeTresholdButton = (Button) findViewById(R.id.IncreaseSpeedTimeTresholdButton);
		IncreaseSpeedTimeTresholdButton.setContentDescription(getResources().getString(R.string.increase) + " " + getResources().getString(R.string.speedtimetreshold));
		DecreaseSpeedTimeTresholdButton = (Button) findViewById(R.id.DecreaseSpeedTimeTresholdButton);
		DecreaseSpeedTimeTresholdButton.setContentDescription(getResources().getString(R.string.decrease) + " " + getResources().getString(R.string.speedtimetreshold));
		
	    // increase&decrease heading treshold button
		IncreaseHeadingTresholdButton = (Button) findViewById(R.id.IncreaseHeadingTresholdButton);
		IncreaseHeadingTresholdButton.setContentDescription(getResources().getString(R.string.increase) + " " + getResources().getString(R.string.headingtreshold));
		DecreaseHeadingTresholdButton = (Button) findViewById(R.id.DecreaseHeadingTresholdButton);
		DecreaseHeadingTresholdButton.setContentDescription(getResources().getString(R.string.decrease) + " " + getResources().getString(R.string.headingtreshold));
	    
		// increase&decrease heading time treshold button
		IncreaseHeadingTimeTresholdButton = (Button) findViewById(R.id.IncreaseHeadingTimeTresholdButton);
		IncreaseHeadingTimeTresholdButton.setContentDescription(getResources().getString(R.string.increase) + " " + getResources().getString(R.string.headingtimetreshold));
		DecreaseHeadingTimeTresholdButton = (Button) findViewById(R.id.DecreaseHeadingTimeTresholdButton);
		DecreaseHeadingTimeTresholdButton.setContentDescription(getResources().getString(R.string.decrease) + " " + getResources().getString(R.string.headingtimetreshold));
		
		// increase&decrease distance time treshold button
		IncreaseDistanceTimeTresholdButton = (Button) findViewById(R.id.IncreaseDistanceTimeTresholdButton);
		IncreaseDistanceTimeTresholdButton.setContentDescription(getResources().getString(R.string.increase) + " " + getResources().getString(R.string.distancetimetreshold));
		DecreaseDistanceTimeTresholdButton = (Button) findViewById(R.id.DecreaseDistanceTimeTresholdButton);
		DecreaseDistanceTimeTresholdButton.setContentDescription(getResources().getString(R.string.decrease) + " " + getResources().getString(R.string.distancetimetreshold));
		
		 // increase&decrease bearing treshold button
		IncreaseBearingTresholdButton = (Button) findViewById(R.id.IncreaseBearingTresholdButton);
		IncreaseBearingTresholdButton.setContentDescription(getResources().getString(R.string.increase) + " " + getResources().getString(R.string.bearingtreshold));
		DecreaseBearingTresholdButton = (Button) findViewById(R.id.DecreaseBearingTresholdButton);
		DecreaseBearingTresholdButton.setContentDescription(getResources().getString(R.string.decrease) + " " + getResources().getString(R.string.bearingtreshold));
	    
		// increase&decrease bearing time treshold button
		IncreaseBearingTimeTresholdButton = (Button) findViewById(R.id.IncreaseBearingTimeTresholdButton);
		IncreaseBearingTimeTresholdButton.setContentDescription(getResources().getString(R.string.increase) + " " + getResources().getString(R.string.bearingtimetreshold));
		DecreaseBearingTimeTresholdButton = (Button) findViewById(R.id.DecreaseBearingTimeTresholdButton);
		DecreaseBearingTimeTresholdButton.setContentDescription(getResources().getString(R.string.decrease) + " " + getResources().getString(R.string.bearingtimetreshold));
		
		// OnClickListener creation
	    View.OnClickListener onclickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v== IncreaseSpeedTresholdButton){
					if(speedTreshold >= 0.0 && speedTreshold < 3.0) {
						speedTreshold = arrondiSpeedTreshold(speedTreshold + speedTresholdStep);
						textViewSpeedTreshold.setText(getResources().getString(R.string.speedtreshold)+ " "  + speedTreshold + " " + getResources().getString(R.string.SpeedUnit));
						textViewSpeedTreshold.setContentDescription(getResources().getString(R.string.speedtreshold) + speedTreshold + " " + getResources().getString(R.string.SpeedUnit));
						tts.speak(getResources().getString(R.string.speedtreshold) + speedTreshold + " " + getResources().getString(R.string.SpeedUnit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "inc speed");
					}
					else {
						tts.speak("Speed treshold is 3.0 knots. Can't increase.",TextToSpeech.QUEUE_FLUSH, null);
					}
		        }
				if (v== DecreaseSpeedTresholdButton){
					if(speedTreshold > 0.0 && speedTreshold <= 3.0) {
						speedTreshold = arrondiSpeedTreshold(speedTreshold - speedTresholdStep);
						textViewSpeedTreshold.setText(getResources().getString(R.string.speedtreshold)+ " "  + speedTreshold + " " + getResources().getString(R.string.SpeedUnit));
						textViewSpeedTreshold.setContentDescription(getResources().getString(R.string.speedtreshold) + speedTreshold + " " + getResources().getString(R.string.SpeedUnit));
						tts.speak(getResources().getString(R.string.speedtreshold) + speedTreshold + " " + getResources().getString(R.string.SpeedUnit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "dec speed");
					}
					else {
						tts.speak("Speed treshold is 0.0 knot. Can't decrease.",TextToSpeech.QUEUE_FLUSH, null);
					}
		        }
				if (v== IncreaseSpeedTimeTresholdButton){
					if(speedTimeTreshold >= 0 && speedTimeTreshold < 30) {
						speedTimeTreshold = speedTimeTreshold + speedTimeTresholdStep;
						textViewSpeedTimeTreshold.setText(getResources().getString(R.string.speedtimetreshold)+ " "  + speedTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
						textViewSpeedTimeTreshold.setContentDescription(getResources().getString(R.string.speedtimetreshold) + speedTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
						tts.speak(getResources().getString(R.string.speedtimetreshold) + speedTimeTreshold + " " + getResources().getString(R.string.TimeUnit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "increase speed time");
					}
					else {
						tts.speak("Minimum speed repetition is 30 seconds. Can't increase.",TextToSpeech.QUEUE_FLUSH, null);
					}
		        }
				if (v== DecreaseSpeedTimeTresholdButton){
					if(speedTimeTreshold > 0 && speedTimeTreshold <= 30) {
						speedTimeTreshold = speedTimeTreshold - speedTimeTresholdStep;
						textViewSpeedTimeTreshold.setText(getResources().getString(R.string.speedtimetreshold)+ " "  + speedTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
						textViewSpeedTimeTreshold.setContentDescription(getResources().getString(R.string.speedtimetreshold) + speedTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
						tts.speak(getResources().getString(R.string.speedtimetreshold) + speedTimeTreshold + " " + getResources().getString(R.string.TimeUnit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "decrease speed time");
					}
					else {
						tts.speak("Minimum speed repetition is 0 seconds. Can't decrease.",TextToSpeech.QUEUE_FLUSH, null);
					}
		        }
				if (v== IncreaseHeadingTresholdButton){
					if(headingTreshold >= 0 && headingTreshold < 30) {
						headingTreshold = arrondiHeadingTreshold(headingTreshold + headingTresholdStep);
						textViewHeadingTreshold.setText(getResources().getString(R.string.headingtreshold)+ " "  + Integer.toString((int)headingTreshold) + " " + getResources().getString(R.string.HeadingUnit));
						textViewHeadingTreshold.setContentDescription(getResources().getString(R.string.headingtreshold) + Integer.toString((int)headingTreshold) + " " + getResources().getString(R.string.HeadingUnit));
						tts.speak(getResources().getString(R.string.headingtreshold) + Integer.toString((int)headingTreshold) + " " + getResources().getString(R.string.HeadingUnit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "inc heading");
					}
					else {
						tts.speak("Heading treshold is 30 degrees. Can't increase.",TextToSpeech.QUEUE_FLUSH, null);
					}
		        }	
				if (v== DecreaseHeadingTresholdButton){
					if(headingTreshold > 0 && headingTreshold <= 30) {
						headingTreshold = arrondiHeadingTreshold(headingTreshold - headingTresholdStep);
						textViewHeadingTreshold.setText(getResources().getString(R.string.headingtreshold)+ " "  + Integer.toString((int)headingTreshold) + " " + getResources().getString(R.string.HeadingUnit));
						textViewHeadingTreshold.setContentDescription(getResources().getString(R.string.headingtreshold) + Integer.toString((int)headingTreshold) + " " + getResources().getString(R.string.HeadingUnit));
						tts.speak(getResources().getString(R.string.headingtreshold) + Integer.toString((int)headingTreshold) + " " + getResources().getString(R.string.HeadingUnit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "dec heading");
					}
					else {
						tts.speak("Heading treshold is 0 degrees. Can't decrease.",TextToSpeech.QUEUE_FLUSH, null);
					}
					
		        }
				if (v== IncreaseHeadingTimeTresholdButton){
					if(headingTimeTreshold >= 0 && headingTimeTreshold < 30) {
						headingTimeTreshold = headingTimeTreshold + headingTimeTresholdStep;
						textViewHeadingTimeTreshold.setText(getResources().getString(R.string.headingtimetreshold)+ " "  + headingTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
						textViewHeadingTimeTreshold.setContentDescription(getResources().getString(R.string.headingtimetreshold) + headingTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
						tts.speak(getResources().getString(R.string.headingtimetreshold) + headingTimeTreshold + " " + getResources().getString(R.string.TimeUnit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "increase heading time");
					}
					else {
						tts.speak("Minimum heading repetition is 30 seconds. Can't increase.",TextToSpeech.QUEUE_FLUSH, null);
					}
					
		        }
				if (v== DecreaseHeadingTimeTresholdButton){
					if(headingTimeTreshold > 0 && headingTimeTreshold <= 30) {
						headingTimeTreshold = headingTimeTreshold - headingTimeTresholdStep;
						textViewHeadingTimeTreshold.setText(getResources().getString(R.string.headingtimetreshold)+ " "  + headingTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
						textViewHeadingTimeTreshold.setContentDescription(getResources().getString(R.string.headingtimetreshold) + headingTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
						tts.speak(getResources().getString(R.string.headingtimetreshold) + headingTimeTreshold + " " + getResources().getString(R.string.TimeUnit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "decrease heading time");
					}
					else {
						tts.speak("Minimum heading repetition is 0 seconds. Can't decrease.",TextToSpeech.QUEUE_FLUSH, null);
					}
					
		        }
				if (v== IncreaseBearingTresholdButton){
					if(bearingTreshold >= 0 && bearingTreshold < 30) {
						bearingTreshold = arrondiHeadingTreshold(bearingTreshold + bearingTresholdStep);
						textViewBearingTreshold.setText(getResources().getString(R.string.bearingtreshold)+ " "  + Integer.toString((int)bearingTreshold) + " " + getResources().getString(R.string.HeadingUnit));
						textViewBearingTreshold.setContentDescription(getResources().getString(R.string.bearingtreshold) + Integer.toString((int)bearingTreshold) + " " + getResources().getString(R.string.HeadingUnit));
						tts.speak(getResources().getString(R.string.bearingtreshold) + Integer.toString((int)bearingTreshold) + " " + getResources().getString(R.string.HeadingUnit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "inc bearing");
					}
					else {
						tts.speak("Bearing treshold is 30 degrees. Can't increase.",TextToSpeech.QUEUE_FLUSH, null);
					}
		        }	
				if (v== DecreaseBearingTresholdButton){
					if(headingTreshold > 0 && headingTreshold <= 30) {
						bearingTreshold = arrondiHeadingTreshold(bearingTreshold - bearingTresholdStep);
						textViewBearingTreshold.setText(getResources().getString(R.string.bearingtreshold)+ " "  + Integer.toString((int)bearingTreshold) + " " + getResources().getString(R.string.HeadingUnit));
						textViewBearingTreshold.setContentDescription(getResources().getString(R.string.bearingtreshold) + Integer.toString((int)bearingTreshold) + " " + getResources().getString(R.string.HeadingUnit));
						tts.speak(getResources().getString(R.string.bearingtreshold) + Integer.toString((int)bearingTreshold) + " " + getResources().getString(R.string.HeadingUnit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "dec bearing");
					}
					else {
						tts.speak("Bearing treshold is 0 degrees. Can't decrease.",TextToSpeech.QUEUE_FLUSH, null);
					}
					
		        }
				if (v== IncreaseBearingTimeTresholdButton){
					if(bearingTimeTreshold >= 0 && bearingTimeTreshold < 30) {
						bearingTimeTreshold = bearingTimeTreshold + bearingTimeTresholdStep;
						textViewBearingTimeTreshold.setText(getResources().getString(R.string.bearingtimetreshold)+ " "  + bearingTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
						textViewBearingTimeTreshold.setContentDescription(getResources().getString(R.string.bearingtimetreshold) + bearingTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
						tts.speak(getResources().getString(R.string.bearingtimetreshold) + bearingTimeTreshold + " " + getResources().getString(R.string.TimeUnit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "increase bearing time");
					}
					else {
						tts.speak("Minimum bearing repetition is 30 seconds. Can't increase.",TextToSpeech.QUEUE_FLUSH, null);
					}
					
		        }
				if (v== DecreaseBearingTimeTresholdButton){
					if(bearingTimeTreshold > 0 && bearingTimeTreshold <= 30) {
						bearingTimeTreshold = bearingTimeTreshold - bearingTimeTresholdStep;
						textViewBearingTimeTreshold.setText(getResources().getString(R.string.bearingtimetreshold)+ " "  + bearingTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
						textViewBearingTimeTreshold.setContentDescription(getResources().getString(R.string.bearingtimetreshold) + bearingTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
						tts.speak(getResources().getString(R.string.bearingtimetreshold) + bearingTimeTreshold + " " + getResources().getString(R.string.TimeUnit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "decrease bearing time");
					}
					else {
						tts.speak("Minimum bearing repetition is 0 seconds. Can't decrease.",TextToSpeech.QUEUE_FLUSH, null);
					}
					
		        }
				
				if (v== IncreaseDistanceTimeTresholdButton){
					if(distanceTimeTreshold >= 0 && distanceTimeTreshold < 30) {
						distanceTimeTreshold = distanceTimeTreshold + distanceTimeTresholdStep;
						textViewDistanceTimeTreshold.setText(getResources().getString(R.string.distancetimetreshold)+ " "  + distanceTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
						textViewDistanceTimeTreshold.setContentDescription(getResources().getString(R.string.distancetimetreshold) + distanceTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
						tts.speak(getResources().getString(R.string.distancetimetreshold) + distanceTimeTreshold + " " + getResources().getString(R.string.TimeUnit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "increase distance time");
					}
					else {
						tts.speak("Minimum distance repetition is 30 seconds. Can't increase.",TextToSpeech.QUEUE_FLUSH, null);
					}
					
		        }
				if (v== DecreaseDistanceTimeTresholdButton){
					if(distanceTimeTreshold > 0 && distanceTimeTreshold <= 30) {
						distanceTimeTreshold = distanceTimeTreshold - distanceTimeTresholdStep;
						textViewDistanceTimeTreshold.setText(getResources().getString(R.string.distancetimetreshold)+ " "  + distanceTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
						textViewDistanceTimeTreshold.setContentDescription(getResources().getString(R.string.distancetimetreshold) + distanceTimeTreshold + " " + getResources().getString(R.string.TimeUnit));
						tts.speak(getResources().getString(R.string.distancetimetreshold) + distanceTimeTreshold + " " + getResources().getString(R.string.TimeUnit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "decrease distance time");
					}
					else {
						tts.speak("Minimum distance repetition is 0 seconds. Can't decrease.",TextToSpeech.QUEUE_FLUSH, null);
					}
					
		        }
				if (v== SaveSettingButton){
					  
					  SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				      SharedPreferences.Editor editor = settings.edit();
				      
				      // put speed & speed time treshold
				      editor.putString("speedTreshold", String.valueOf(speedTreshold));
				      editor.putLong("speedTimeTreshold", speedTimeTreshold);
				      
				      // put heading & heading treshold
				      editor.putString("headingTreshold", String.valueOf(headingTreshold));
				      editor.putLong("headingTimeTreshold", headingTimeTreshold);
				      
				      //put distance time treshold 
				      editor.putLong("distanceTimeTreshold", distanceTimeTreshold);
				      
				      //put bearing & bearing time treshold
				      editor.putString("bearingTreshold", String.valueOf(bearingTreshold));
				      editor.putLong("bearingTimeTreshold", bearingTimeTreshold);
				      
				      // put auto checkbox
				      editor.putBoolean("isAutoSpeed", SpeedAutoCheckBox.isChecked());
				      editor.putBoolean("isAutoHeading", HeadingAutoCheckBox.isChecked());
				      editor.putBoolean("isAutoDistance", DistanceAutoCheckBox.isChecked());
				      editor.putBoolean("isAutoBearing", BearingAutoCheckBox.isChecked());

				      editor.commit();
					intentMain.putExtra("speedTreshold", speedTreshold);
					intentMain.putExtra("speedTimeTreshold", speedTimeTreshold);
					intentMain.putExtra("headingTreshold", headingTreshold);
					intentMain.putExtra("headingTimeTreshold", headingTimeTreshold);
					intentMain.putExtra("distanceTimeTreshold", distanceTimeTreshold);
					intentMain.putExtra("bearingTreshold", bearingTreshold);
					intentMain.putExtra("bearingTimeTreshold", bearingTimeTreshold);
					
					intentMain.putExtra("isAutoSpeed", SpeedAutoCheckBox.isChecked());
					intentMain.putExtra("isAutoHeading", HeadingAutoCheckBox.isChecked());
					intentMain.putExtra("isAutoDistance", DistanceAutoCheckBox.isChecked());
					intentMain.putExtra("isAutoBearing", BearingAutoCheckBox.isChecked());

					setResult(RESULT_OK, intentMain);
					//tts.shutdown();
					finish();
				}
		    }// end of onclick		
	    }; //end of new View.LocationListener	
		IncreaseSpeedTresholdButton.setOnClickListener(onclickListener);
		DecreaseSpeedTresholdButton.setOnClickListener(onclickListener);
		IncreaseSpeedTimeTresholdButton.setOnClickListener(onclickListener);
		DecreaseSpeedTimeTresholdButton.setOnClickListener(onclickListener);
		
		IncreaseHeadingTresholdButton.setOnClickListener(onclickListener);
		DecreaseHeadingTresholdButton.setOnClickListener(onclickListener);
		IncreaseHeadingTimeTresholdButton.setOnClickListener(onclickListener);
		DecreaseHeadingTimeTresholdButton.setOnClickListener(onclickListener);
		
		IncreaseDistanceTimeTresholdButton.setOnClickListener(onclickListener);
		DecreaseDistanceTimeTresholdButton.setOnClickListener(onclickListener);
		
		IncreaseBearingTresholdButton.setOnClickListener(onclickListener);
		DecreaseBearingTresholdButton.setOnClickListener(onclickListener);
		IncreaseBearingTimeTresholdButton.setOnClickListener(onclickListener);
		DecreaseBearingTimeTresholdButton.setOnClickListener(onclickListener);
		
		SaveSettingButton.setOnClickListener(onclickListener);

		//OnInitListener Creation
		OnInitListener onInitListener = new OnInitListener() {
			@Override
			public void onInit(int status) {
			}
		};
		
	    // tts creation
		tts = new TextToSpeech(this, onInitListener);
		tts.setSpeechRate((float) 1.0);
			
	}
	
	/*
	//  action bar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	*/
	
	  @Override
	  protected void onResume() {
	    super.onResume();
	    //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
	    //tts.speak("resume", TextToSpeech.QUEUE_FLUSH, null);
	  }

	  @Override
	  protected void onPause() {
	    super.onPause();
	    //lm.removeUpdates(ll);
	    //tts.speak("pause", TextToSpeech.QUEUE_FLUSH, null);

	  }
	  
	  @Override
	  protected void onStop() {
		  super.onStop();
		//tts.shutdown();
	  }
	  
		@Override
		protected void onDestroy() {
			super.onDestroy();
			tts.shutdown();
			//lm.removeUpdates(ll);
			//tts.shutdown();
		}
	
	/*public boolean onOptionsItemSelected(MenuItem Item){
		switch (Item.getItemId()) {
		case R.id.auto_setting:
			break;
		case R.id.waypoint_setting:
			//Intent intentWaypoint_setting = new Intent(AutoSetting.this,Waypoint.class);
			//startActivity(intentWaypoint_setting);
			break;
		case R.id.main:
			
			intentMain.putExtra("speedTreshold", this.speedTreshold);
			intentMain.putExtra("speedTimeTreshold", this.speedTimeTreshold);
			intentMain.putExtra("headingTreshold", this.headingTreshold);
			intentMain.putExtra("headingTimeTreshold", this.headingTimeTreshold);
			intentMain.putExtra("isAutoSpeed", this.speedAutoCheckBox.isChecked());
			intentMain.putExtra("isAutoHeading", this.HeadingAutoCheckBox.isChecked());

			setResult(RESULT_OK, intentMain);
			tts.shutdown();
			finish();
			
			break;
		default:
			break;
		}

		return false;
	}*/

	public double arrondiSpeedTreshold(double val) {return (Math.floor((val+0.00001)*10))/10;}
	public double arrondiHeadingTreshold(double val) {return (Math.floor((val+0.00001)*10))/10;}
	

	

}
