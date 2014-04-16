package orion.ms.sara;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
	public String speedUnit = "knots";
	public String bearingUnit = "port";
	public String portStarboard_Bearing = "0 on port";
	public String distanceUnit = "NM";
	public String mapType = "openSeaMap";
	public float speechRate = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generalsetting);
		setTitle("General Setting");
		
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
	
	public float getSpeechRate() {
		return speechRate;
	}

	public void setSpeechRate(float speechRate) {
		this.speechRate = speechRate;
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
