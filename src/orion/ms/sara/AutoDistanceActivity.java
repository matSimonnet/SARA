package orion.ms.sara;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
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
import android.app.AlertDialog;

public class AutoDistanceActivity extends Activity {

	private Intent intentMainAutoSetting;
	
	private TextToSpeech tts = null;

	private CheckBox DistanceAutoCheckBox = null;

	private TextView textViewDistanceTimeTreshold = null;

	private Button SaveSettingButton = null;

	// declare change distance time treshold buttons
	private Button IncreaseDistanceTimeTresholdButton = null;
	private Button DecreaseDistanceTimeTresholdButton = null;
	
	private long distanceTimeTreshold = 5;
	private long distanceTimeTresholdStep = 1;

	private boolean isAutoDistance = true;
	
	private AlertDialog.Builder alertDialog;
	
	public SharedPreferences settings;
	public SharedPreferences.Editor editor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_autodistance);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
	    // Restore preferences
		this.settings = getSharedPreferences(MyLocationListener.PREFS_NAME, 0);
		this.editor = settings.edit();
		LoadPref();
		
	    // save setting button
		SaveSettingButton = (Button) findViewById(R.id.SaveSettingButton);
		SaveSettingButton.setContentDescription(getResources().getString(R.string.savebutton));

	    //setSilent(silent);
		DistanceAutoCheckBox = (CheckBox) findViewById(R.id.DistanceAutoCheckBox);
		DistanceAutoCheckBox.setChecked(this.isAutoDistance);
		
		//intent creation
	    intentMainAutoSetting = new Intent(AutoDistanceActivity.this,AutoSettingActivity.class);

	    //distance time treshold view
	    textViewDistanceTimeTreshold = (TextView) findViewById(R.id.DistanceTimeTresholdView);
	    textViewDistanceTimeTreshold.setText(getResources().getString(R.string.distancetimetreshold)+ " "  + distanceTimeTreshold + " " + getResources().getString(R.string.timeunit));
	    textViewDistanceTimeTreshold.setContentDescription(getResources().getString(R.string.distancetimetreshold) + distanceTimeTreshold + " " + getResources().getString(R.string.timeunit));

		// increase&decrease distance time treshold button
		IncreaseDistanceTimeTresholdButton = (Button) findViewById(R.id.IncreaseDistanceTimeTresholdButton);
		IncreaseDistanceTimeTresholdButton.setContentDescription(getResources().getString(R.string.increase) + " " + getResources().getString(R.string.distancetimetreshold));
		DecreaseDistanceTimeTresholdButton = (Button) findViewById(R.id.DecreaseDistanceTimeTresholdButton);
		DecreaseDistanceTimeTresholdButton.setContentDescription(getResources().getString(R.string.decrease) + " " + getResources().getString(R.string.distancetimetreshold));

		// OnClickListener creation
	    View.OnClickListener onclickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v== IncreaseDistanceTimeTresholdButton){
					if(distanceTimeTreshold >= 0 && distanceTimeTreshold < 30) {
						distanceTimeTreshold = distanceTimeTreshold + distanceTimeTresholdStep;
						textViewDistanceTimeTreshold.setText(getResources().getString(R.string.distancetimetreshold)+ " "  + distanceTimeTreshold + " " + getResources().getString(R.string.timeunit));
						textViewDistanceTimeTreshold.setContentDescription(getResources().getString(R.string.distancetimetreshold) + distanceTimeTreshold + " " + getResources().getString(R.string.timeunit));
						tts.speak(getResources().getString(R.string.distancetimetreshold) + distanceTimeTreshold + " " + getResources().getString(R.string.timeunit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "increase distance time");
					}
					else {
						tts.speak(getResources().getString(R.string.maxdistancetimetreshold) + getResources().getString(R.string.cant_increase),TextToSpeech.QUEUE_FLUSH, null);
					}
					
		        }
				if (v== DecreaseDistanceTimeTresholdButton){
					if(distanceTimeTreshold > 0 && distanceTimeTreshold <= 30) {
						distanceTimeTreshold = distanceTimeTreshold - distanceTimeTresholdStep;
						textViewDistanceTimeTreshold.setText(getResources().getString(R.string.distancetimetreshold)+ " "  + distanceTimeTreshold + " " + getResources().getString(R.string.timeunit));
						textViewDistanceTimeTreshold.setContentDescription(getResources().getString(R.string.distancetimetreshold) + distanceTimeTreshold + " " + getResources().getString(R.string.timeunit));
						tts.speak(getResources().getString(R.string.distancetimetreshold) + distanceTimeTreshold + " " + getResources().getString(R.string.timeunit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "decrease distance time");
					}
					else {
						tts.speak(getResources().getString(R.string.mindistancetimetreshold) + getResources().getString(R.string.cant_decrease),TextToSpeech.QUEUE_FLUSH, null);
					}
					
		        }
				if (v== SaveSettingButton){
				    editor.putLong("distanceTimeTreshold", distanceTimeTreshold);
				    editor.putBoolean("isAutoDistance", DistanceAutoCheckBox.isChecked());
				    editor.commit();
				    
					intentMainAutoSetting.putExtra("distanceTimeTreshold", distanceTimeTreshold);
					intentMainAutoSetting.putExtra("isAutoDistance", DistanceAutoCheckBox.isChecked());
					setResult(RESULT_OK, intentMainAutoSetting);
					finish();
				}
		    }// end of onclick		
	    }; //end of new View.LocationListener	
		
		IncreaseDistanceTimeTresholdButton.setOnClickListener(onclickListener);
		DecreaseDistanceTimeTresholdButton.setOnClickListener(onclickListener);
		
		SaveSettingButton.setOnClickListener(onclickListener);

		//OnInitListener Creation
		OnInitListener onInitListener = new OnInitListener() {
			@Override
			public void onInit(int status) {
			}
		};
		
	    // tts creation
		tts = new TextToSpeech(this, onInitListener);
			
	} // end of onCreate
	
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
	
	public void LoadPref() {
	    this.distanceTimeTreshold = settings.getLong("distanceTimeTreshold", 5);   
	    this.isAutoDistance = settings.getBoolean("isAutoDistance", true);
	}
//  action bar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_autosetting, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem Item){
		switch (Item.getItemId()) {
		case R.id.backtoautosetting:
			
		    long LastdistanceTimeTreshold = settings.getLong("distanceTimeTreshold", 5);   
		    boolean LastisAutoDistance = settings.getBoolean("isAutoDistance", true);
		    
			if(LastdistanceTimeTreshold != distanceTimeTreshold || LastisAutoDistance != DistanceAutoCheckBox.isChecked()) {				
				alertDialog = new AlertDialog.Builder(this);
				alertDialog.setTitle(getResources().getString(R.string.title_alertdialog_distancesetting));
				alertDialog.setNegativeButton("YES", new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
					    editor.putLong("distanceTimeTreshold", distanceTimeTreshold);
					    editor.putBoolean("isAutoDistance", DistanceAutoCheckBox.isChecked());
					    editor.commit();
					    
						intentMainAutoSetting.putExtra("distanceTimeTreshold", distanceTimeTreshold);
						intentMainAutoSetting.putExtra("isAutoDistance", DistanceAutoCheckBox.isChecked());
						setResult(RESULT_OK, intentMainAutoSetting);
						finish();					
					}
				});
				alertDialog.setPositiveButton("No", new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				alertDialog.setIcon(android.R.drawable.presence_busy);
				alertDialog.show();
			}
			else {
				finish();
			}
			break;
		default:
			break;
		}
		return false;
	}
	
	  
	

	

}
