package orion.ms.sara;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.speech.tts.TextToSpeech.OnInitListener;

public class AutoBearingActivity extends Activity {
	
	public static final String PREFS_NAME = "MyPrefsFile";
	
	//private Intent intentAuto_setting;
	//private Intent intentWaypoint_setting;
	private Intent intentMainAutoSetting;
	
	private TextToSpeech tts = null;

	private CheckBox BearingAutoCheckBox = null;
	
	private TextView textViewBearingTreshold = null;
	private TextView textViewBearingTimeTreshold = null;

	private Button SaveSettingButton = null;

	// declare change bearing treshold buttons
	private Button IncreaseBearingTresholdButton = null;
	private Button DecreaseBearingTresholdButton = null;
	
	// declare change bearing time treshold buttons
	private Button IncreaseBearingTimeTresholdButton = null;
	private Button DecreaseBearingTimeTresholdButton = null;
	
	private double bearingTreshold = 10.0;
	private long bearingTimeTreshold = 5;
	private double bearingTresholdStep = 1.0;
	private long bearingTimeTresholdStep = 1;

	private boolean isAutoBearing = true;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_autobearing);
		
	    // Restore preferences
		LoadPref();
		
	    // save setting button
		SaveSettingButton = (Button) findViewById(R.id.SaveSettingButton);
		SaveSettingButton.setContentDescription(getResources().getString(R.string.savebutton));
	    
		BearingAutoCheckBox = (CheckBox) findViewById(R.id.BearingAutoCheckBox);
	    BearingAutoCheckBox.setChecked(this.isAutoBearing);

		//intent creation
	    intentMainAutoSetting = new Intent(AutoBearingActivity.this,MainAutoSettingActivity.class);

	    //bearing treshold view
	    textViewBearingTreshold = (TextView) findViewById(R.id.BearingTresholdView);
	    textViewBearingTreshold.setText(getResources().getString(R.string.bearingtreshold)+ " "  + Integer.toString((int)bearingTreshold) + " " + getResources().getString(R.string.bearingunit));
	    textViewBearingTreshold.setContentDescription(getResources().getString(R.string.bearingtreshold) + Integer.toString((int)bearingTreshold) + " " + getResources().getString(R.string.bearingunit));
	    
	    //bearing time treshold view
	    textViewBearingTimeTreshold = (TextView) findViewById(R.id.BearingTimeTresholdView);
	    textViewBearingTimeTreshold.setText(getResources().getString(R.string.bearingtimetreshold)+ " "  + bearingTimeTreshold + " " + getResources().getString(R.string.timeunit));
	    textViewBearingTimeTreshold.setContentDescription(getResources().getString(R.string.bearingtimetreshold) + bearingTimeTreshold + " " + getResources().getString(R.string.timeunit));

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
				if (v== IncreaseBearingTresholdButton){
					if(bearingTreshold >= 0 && bearingTreshold < 30) {
						bearingTreshold = Utils.arrondiHeadingTreshold(bearingTreshold + bearingTresholdStep);
						textViewBearingTreshold.setText(getResources().getString(R.string.bearingtreshold)+ " "  + Integer.toString((int)bearingTreshold) + " " + getResources().getString(R.string.bearingunit));
						textViewBearingTreshold.setContentDescription(getResources().getString(R.string.bearingtreshold) + Integer.toString((int)bearingTreshold) + " " + getResources().getString(R.string.bearingunit));
						tts.speak(getResources().getString(R.string.bearingtreshold) + Integer.toString((int)bearingTreshold) + " " + getResources().getString(R.string.bearingunit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "inc bearing");
					}
					else {
						tts.speak(getResources().getString(R.string.maxbearingtreshold) + getResources().getString(R.string.cant_increase),TextToSpeech.QUEUE_FLUSH, null);
					}
		        }	
				if (v== DecreaseBearingTresholdButton){
					if(bearingTreshold > 0 && bearingTreshold <= 30) {
						bearingTreshold = Utils.arrondiHeadingTreshold(bearingTreshold - bearingTresholdStep);
						textViewBearingTreshold.setText(getResources().getString(R.string.bearingtreshold)+ " "  + Integer.toString((int)bearingTreshold) + " " + getResources().getString(R.string.bearingunit));
						textViewBearingTreshold.setContentDescription(getResources().getString(R.string.bearingtreshold) + Integer.toString((int)bearingTreshold) + " " + getResources().getString(R.string.bearingunit));
						tts.speak(getResources().getString(R.string.bearingtreshold) + Integer.toString((int)bearingTreshold) + " " + getResources().getString(R.string.bearingunit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "dec bearing");
					}
					else {
						tts.speak(getResources().getString(R.string.minbearingtreshold) + getResources().getString(R.string.cant_decrease),TextToSpeech.QUEUE_FLUSH, null);
					}
					
		        }
				if (v== IncreaseBearingTimeTresholdButton){
					if(bearingTimeTreshold >= 0 && bearingTimeTreshold < 30) {
						bearingTimeTreshold = bearingTimeTreshold + bearingTimeTresholdStep;
						textViewBearingTimeTreshold.setText(getResources().getString(R.string.bearingtimetreshold)+ " "  + bearingTimeTreshold + " " + getResources().getString(R.string.timeunit));
						textViewBearingTimeTreshold.setContentDescription(getResources().getString(R.string.bearingtimetreshold) + bearingTimeTreshold + " " + getResources().getString(R.string.timeunit));
						tts.speak(getResources().getString(R.string.bearingtimetreshold) + bearingTimeTreshold + " " + getResources().getString(R.string.TimeUnit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "increase bearing time");
					}
					else {
						tts.speak(getResources().getString(R.string.maxbearingtimetreshold) + getResources().getString(R.string.cant_increase),TextToSpeech.QUEUE_FLUSH, null);
					}
					
		        }
				if (v== DecreaseBearingTimeTresholdButton){
					if(bearingTimeTreshold > 0 && bearingTimeTreshold <= 30) {
						bearingTimeTreshold = bearingTimeTreshold - bearingTimeTresholdStep;
						textViewBearingTimeTreshold.setText(getResources().getString(R.string.bearingtimetreshold)+ " "  + bearingTimeTreshold + " " + getResources().getString(R.string.timeunit));
						textViewBearingTimeTreshold.setContentDescription(getResources().getString(R.string.bearingtimetreshold) + bearingTimeTreshold + " " + getResources().getString(R.string.timeunit));
						tts.speak(getResources().getString(R.string.bearingtimetreshold) + bearingTimeTreshold + " " + getResources().getString(R.string.TimeUnit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "decrease bearing time");
					}
					else {
						tts.speak(getResources().getString(R.string.minbearingtimetreshold) + getResources().getString(R.string.cant_decrease),TextToSpeech.QUEUE_FLUSH, null);
					}
					
		        }
				if (v== SaveSettingButton){
					  
					SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
					SharedPreferences.Editor editor = settings.edit();
				      
				    //put bearing & bearing time treshold
				    editor.putString("bearingTreshold", String.valueOf(bearingTreshold));
				    editor.putLong("bearingTimeTreshold", bearingTimeTreshold);
				    editor.putBoolean("isAutoBearing", BearingAutoCheckBox.isChecked());
				    editor.commit();
				    
					intentMainAutoSetting.putExtra("bearingTreshold", bearingTreshold);
					intentMainAutoSetting.putExtra("bearingTimeTreshold", bearingTimeTreshold);
					intentMainAutoSetting.putExtra("isAutoBearing", BearingAutoCheckBox.isChecked());
					setResult(RESULT_OK, intentMainAutoSetting);
					finish();
				}
		    }// end of onclick		
	    }; //end of new View.LocationListener	

		
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
			
	} // end of onCreate
	
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
	
	public void LoadPref() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);   
	    this.bearingTreshold = Double.parseDouble(settings.getString("bearingTreshold", "10.0"));
	    this.bearingTimeTreshold = settings.getLong("bearingTimeTreshold", 5);  

	    this.isAutoBearing = settings.getBoolean("isAutoBearing", true);
	}
//  action bar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_autosetting, menu);
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
