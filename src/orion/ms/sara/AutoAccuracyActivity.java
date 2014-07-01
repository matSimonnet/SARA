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

public class AutoAccuracyActivity extends Activity {
		
	private Intent intentMainAutoSetting;
	
	private TextToSpeech tts = null;

	private CheckBox AccuracyAutoCheckBox = null;
	private TextView textViewAccuracyTimeTreshold = null;
	private Button SaveSettingButton = null;

	// declare change accuracy time treshold buttons
	private Button IncreaseAccuracyTimeTresholdButton = null;
	private Button DecreaseAccuracyTimeTresholdButton = null;
	
	private long accuracyTimeTreshold = 10;
	private long accuracyTimeTresholdStep = 1;

	private boolean isAutoAccuracy = true;
	
	private AlertDialog.Builder alertDialog;
	
	public SharedPreferences settings;
	public SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_autoaccuracy);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

	    // Restore preferences
		this.settings = getSharedPreferences(MyLocationListener.PREFS_NAME, 0);
		this.editor = settings.edit();
		LoadPref();

	    // save setting button
		SaveSettingButton = (Button) findViewById(R.id.SaveSettingButton);
		SaveSettingButton.setContentDescription(getResources().getString(R.string.savebutton));

	    //setSilent(silent);
	    AccuracyAutoCheckBox = (CheckBox) findViewById(R.id.AccuracyAutoCheckBox);
	    AccuracyAutoCheckBox.setChecked(this.isAutoAccuracy);

		//intent creation
	    intentMainAutoSetting = new Intent(AutoAccuracyActivity.this,AutoSettingActivity.class);

	    //accuracy time treshold view
	    textViewAccuracyTimeTreshold = (TextView) findViewById(R.id.AccuracyTimeTresholdView);
	    textViewAccuracyTimeTreshold.setText(getResources().getString(R.string.accuracytimetreshold)+ " "  + accuracyTimeTreshold + " " + getResources().getString(R.string.timeunit));
	    textViewAccuracyTimeTreshold.setContentDescription(getResources().getString(R.string.accuracytimetreshold) + accuracyTimeTreshold + " " + getResources().getString(R.string.timeunit));

		// increase&decrease accuracy time treshold button
		IncreaseAccuracyTimeTresholdButton = (Button) findViewById(R.id.IncreaseAccuracyTimeTresholdButton);
		IncreaseAccuracyTimeTresholdButton.setContentDescription(getResources().getString(R.string.increase) + " " + getResources().getString(R.string.accuracytimetreshold));
		DecreaseAccuracyTimeTresholdButton = (Button) findViewById(R.id.DecreaseAccuracyTimeTresholdButton);
		DecreaseAccuracyTimeTresholdButton.setContentDescription(getResources().getString(R.string.decrease) + " " + getResources().getString(R.string.accuracytimetreshold));
		
		// OnClickListener creation
	    View.OnClickListener onclickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v== IncreaseAccuracyTimeTresholdButton){
					if(accuracyTimeTreshold >= 0 && accuracyTimeTreshold < 30) {
						accuracyTimeTreshold = accuracyTimeTreshold + accuracyTimeTresholdStep;
						textViewAccuracyTimeTreshold.setText(getResources().getString(R.string.accuracytimetreshold)+ " "  + accuracyTimeTreshold + " " + getResources().getString(R.string.timeunit));
						textViewAccuracyTimeTreshold.setContentDescription(getResources().getString(R.string.accuracytimetreshold) + accuracyTimeTreshold + " " + getResources().getString(R.string.timeunit));
						tts.speak(getResources().getString(R.string.accuracytimetreshold) + accuracyTimeTreshold + " " + getResources().getString(R.string.timeunit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "increase accuracy time");
					}
					else {
						tts.speak(getResources().getString(R.string.maxaccuracytimetreshold) + getResources().getString(R.string.cant_increase),TextToSpeech.QUEUE_FLUSH, null);
					}
					
		        }
				if (v== DecreaseAccuracyTimeTresholdButton){
					if(accuracyTimeTreshold > 0 && accuracyTimeTreshold <= 30) {
						accuracyTimeTreshold = accuracyTimeTreshold - accuracyTimeTresholdStep;
						textViewAccuracyTimeTreshold.setText(getResources().getString(R.string.accuracytimetreshold)+ " "  + accuracyTimeTreshold + " " + getResources().getString(R.string.timeunit));
						textViewAccuracyTimeTreshold.setContentDescription(getResources().getString(R.string.accuracytimetreshold) + accuracyTimeTreshold + " " + getResources().getString(R.string.timeunit));
						tts.speak(getResources().getString(R.string.accuracytimetreshold) + accuracyTimeTreshold + " " + getResources().getString(R.string.timeunit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "decrease accuracy time");
					}
					else {
						tts.speak(getResources().getString(R.string.minaccuracytimetreshold) + getResources().getString(R.string.cant_decrease),TextToSpeech.QUEUE_FLUSH, null);
					}
					
		        }
				if (v== SaveSettingButton){
				    editor.putLong("accuracyTimeTreshold", accuracyTimeTreshold);
				    editor.putBoolean("isAutoAccuracy", AccuracyAutoCheckBox.isChecked());
				    editor.commit();

					intentMainAutoSetting.putExtra("accuracyTimeTreshold", accuracyTimeTreshold);
					intentMainAutoSetting.putExtra("isAutoAccuracy", AccuracyAutoCheckBox.isChecked());
					setResult(RESULT_OK, intentMainAutoSetting);
					finish();
				}
		    }// end of onclick		
	    }; //end of new View.LocationListener	
		IncreaseAccuracyTimeTresholdButton.setOnClickListener(onclickListener);
		DecreaseAccuracyTimeTresholdButton.setOnClickListener(onclickListener);	
		
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
	    this.accuracyTimeTreshold = settings.getLong("accuracyTimeTreshold", 5);  
	    this.isAutoAccuracy = settings.getBoolean("isAutoAccuracy", true);
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
			
		    long LastaccuracyTimeTreshold = settings.getLong("accuracyTimeTreshold", 5);  
		    boolean LastisAutoAccuracy = settings.getBoolean("isAutoAccuracy", true);
		    
			if(LastaccuracyTimeTreshold != accuracyTimeTreshold || LastisAutoAccuracy != AccuracyAutoCheckBox.isChecked()) {				
				alertDialog = new AlertDialog.Builder(this);
				alertDialog.setTitle(getResources().getString(R.string.title_alertdialog_accuracysetting));
				alertDialog.setNegativeButton(getResources().getString(R.string.yes), new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
					    editor.putLong("accuracyTimeTreshold", accuracyTimeTreshold);
					    editor.putBoolean("isAutoAccuracy", AccuracyAutoCheckBox.isChecked());
					    editor.commit();

						intentMainAutoSetting.putExtra("accuracyTimeTreshold", accuracyTimeTreshold);
						intentMainAutoSetting.putExtra("isAutoAccuracy", AccuracyAutoCheckBox.isChecked());
						setResult(RESULT_OK, intentMainAutoSetting);
						finish();					
					}
				});
				alertDialog.setPositiveButton(getResources().getString(R.string.no), new OnClickListener(){
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
