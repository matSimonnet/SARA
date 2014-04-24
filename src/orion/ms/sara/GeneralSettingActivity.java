package orion.ms.sara;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class GeneralSettingActivity extends Activity {
	
	//Button
	private Button speedUnitButton = null;
	private Button bearingUnitButton = null;
	private Button distanceUnitButton = null;
	private Button mapTypeButton = null;
	
	private Intent intentToSpeed;
	private Intent intentToBearing;
	private Intent intentToDistance;
	private Intent intentToMapType;
	
	//code for intent
	protected static final int SPEED_UNIT = 141;
	protected static final int BEARING_UNIT = 362;
	protected static final int DISTANCE_UNIT = 365;
	protected static final int MAP_TYPE = 479;
	protected static final int SPEECH_RATE = 512;

	public static float speechRate = 1.5f;
	private TextToSpeech tts = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generalsetting);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		//OnInitListener Creation
		OnInitListener onInitListener = new OnInitListener() {
			@Override
			public void onInit(int status) {
			}
		};
		
	    // textToSpeech creation
		tts = new TextToSpeech(this, onInitListener);
		
		//Buttons and their description
		this.speedUnitButton = (Button) findViewById(R.id.button1);
		this.speedUnitButton.setContentDescription(getResources().getString(R.string.speedunitsetting));
		
		this.bearingUnitButton = (Button) findViewById(R.id.button2);
		this.bearingUnitButton.setContentDescription(getResources().getString(R.string.bearingunitsetting));
		
		this.distanceUnitButton = (Button) findViewById(R.id.button3);
		this.distanceUnitButton.setContentDescription(getResources().getString(R.string.distanceunitsetting));
		
		this.mapTypeButton = (Button) findViewById(R.id.button4);
		this.mapTypeButton.setContentDescription(getResources().getString(R.string.maptypesetting));

		//intent creations
		intentToSpeed = new Intent(GeneralSettingActivity.this,SpeedUnitActivity.class);
		intentToBearing = new Intent(GeneralSettingActivity.this,BearingUnitActivity.class);
		intentToDistance = new Intent(GeneralSettingActivity.this,DistanceUnitActivity.class);
		intentToMapType = new Intent(GeneralSettingActivity.this,MapTypeActivity.class);
		
	    View.OnClickListener onclickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(v == speedUnitButton) {
		            startActivityForResult(intentToSpeed, SPEED_UNIT);					
				}
				if(v == bearingUnitButton) {
		            startActivityForResult(intentToBearing, BEARING_UNIT);					
				}
				if(v == distanceUnitButton) {
		            startActivityForResult(intentToDistance, DISTANCE_UNIT);					
				}
				if(v == mapTypeButton) {
		            startActivityForResult(intentToMapType, MAP_TYPE);					
				}
			}
	    };
	    speedUnitButton.setOnClickListener(onclickListener);
	    bearingUnitButton.setOnClickListener(onclickListener);
	    distanceUnitButton.setOnClickListener(onclickListener);
	    mapTypeButton.setOnClickListener(onclickListener);

	} // end of on create
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	     switch (requestCode) {
	     	case SPEED_UNIT : {
	     		if (resultCode == RESULT_OK && null != data) {
	     			MyLocationListener.speedLastAuto = data.getDoubleExtra("speedLastAuto", 0.0);
	     			MyLocationListener.isKnotsSelected = data.getBooleanExtra("isKnotsSelected", true);
	     			MyLocationListener.isKmPerHrSelected = data.getBooleanExtra("isKmPerHrSelected", false);
	     		}
	     		break;
	     	}
	     	case BEARING_UNIT : {
	     		if (resultCode == RESULT_OK && null != data) {
	     			MyLocationListener.bearingLastAuto = data.getDoubleExtra("bearingLastAuto", 0.0);
	     			MyLocationListener.isPortandstarboardSelected = data.getBooleanExtra("isPortandstarboardSelected", true);
	     			MyLocationListener.isCardinalSelected = data.getBooleanExtra("isCardinalSelected", false);
	     		}
	     		break;
	     	}
	     	case DISTANCE_UNIT : {
	     		if (resultCode == RESULT_OK && null != data) {
	     			MyLocationListener.distanceLastAuto = data.getDoubleExtra("distanceLastAuto", 0.0);
	     			MyLocationListener.isKilometreSelected = data.getBooleanExtra("isKilometreSelected", true);
	     			MyLocationListener.isNMSelected = data.getBooleanExtra("isNMSelected", false);
	     		}
	     		break;	     	}
	     	case MAP_TYPE : {
	     		break;
	     	}
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
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	  
	@Override
	protected void onStop() {
		super.onStop();
	}
	  
	@Override
	protected void onDestroy() {
		super.onDestroy();
		tts.shutdown();
	}

}
