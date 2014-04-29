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

public class AutoHeadingActivity extends Activity {
		
	private Intent intentMainAutoSetting;
	
	private TextToSpeech tts = null;
	
	private CheckBox HeadingAutoCheckBox = null;

	private TextView textViewHeadingTreshold = null;
	private TextView textViewHeadingTimeTreshold = null;

	private Button SaveSettingButton = null;
	
	// declare change heading treshold buttons
	private Button IncreaseHeadingTresholdButton = null;
	private Button DecreaseHeadingTresholdButton = null;
	
	// declare change heading time treshold buttons
	private Button IncreaseHeadingTimeTresholdButton = null;
	private Button DecreaseHeadingTimeTresholdButton = null;
	
	private double headingTreshold = 10.0; 
	private long headingTimeTreshold = 5;	
	private double headingTresholdStep = 1.0;
	private long headingTimeTresholdStep = 1;

	private boolean isAutoHeading = true;
	
	private AlertDialog.Builder alertDialog;
	
	public SharedPreferences settings;
	public SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_autoheading);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    
		// Restore preferences
		this.settings = getSharedPreferences(MyLocationListener.PREFS_NAME, 0);
		this.editor = settings.edit();
		LoadPref();
		
	    // save setting button
		SaveSettingButton = (Button) findViewById(R.id.SaveSettingButton);
		SaveSettingButton.setContentDescription(getResources().getString(R.string.savebutton));

	    //setSilent(silent);
		HeadingAutoCheckBox = (CheckBox) findViewById(R.id.headingAutoCheckBox);
	    HeadingAutoCheckBox.setChecked(this.isAutoHeading);

		//intent creation
	    intentMainAutoSetting = new Intent(AutoHeadingActivity.this,AutoSettingActivity.class);

	    //heading treshold view
	    textViewHeadingTreshold = (TextView) findViewById(R.id.HeadingTresholdView);
		textViewHeadingTreshold.setText(getResources().getString(R.string.headingtreshold)+ " "  + Integer.toString((int)headingTreshold) + " " + getResources().getString(R.string.headingunit));
	    textViewHeadingTreshold.setContentDescription(getResources().getString(R.string.headingtreshold) + Integer.toString((int)headingTreshold) + " " + getResources().getString(R.string.headingunit));
	    
	    //heading time treshold view
	    textViewHeadingTimeTreshold = (TextView) findViewById(R.id.HeadingTimeTresholdView);
		textViewHeadingTimeTreshold.setText(getResources().getString(R.string.headingtimetreshold)+ " "  + headingTimeTreshold + " " + getResources().getString(R.string.timeunit));
	    textViewHeadingTimeTreshold.setContentDescription(getResources().getString(R.string.headingtimetreshold) + headingTimeTreshold + " " + getResources().getString(R.string.timeunit));

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

		// OnClickListener creation
	    View.OnClickListener onclickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v== IncreaseHeadingTresholdButton){
					if(headingTreshold >= 0 && headingTreshold < 30) {
						headingTreshold = Utils.arrondiHeadingTreshold(headingTreshold + headingTresholdStep);
						textViewHeadingTreshold.setText(getResources().getString(R.string.headingtreshold)+ " "  + Integer.toString((int)headingTreshold) + " " + getResources().getString(R.string.headingunit));
						textViewHeadingTreshold.setContentDescription(getResources().getString(R.string.headingtreshold) + Integer.toString((int)headingTreshold) + " " + getResources().getString(R.string.headingunit));
						tts.speak(getResources().getString(R.string.headingtreshold) + Integer.toString((int)headingTreshold) + " " + getResources().getString(R.string.headingunit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "inc heading");
					}
					else {
						tts.speak(getResources().getString(R.string.maxheadingtreshold) + getResources().getString(R.string.cant_increase),TextToSpeech.QUEUE_FLUSH, null);
					}
		        }	
				if (v== DecreaseHeadingTresholdButton){
					if(headingTreshold > 0 && headingTreshold <= 30) {
						headingTreshold = Utils.arrondiHeadingTreshold(headingTreshold - headingTresholdStep);
						textViewHeadingTreshold.setText(getResources().getString(R.string.headingtreshold)+ " "  + Integer.toString((int)headingTreshold) + " " + getResources().getString(R.string.headingunit));
						textViewHeadingTreshold.setContentDescription(getResources().getString(R.string.headingtreshold) + Integer.toString((int)headingTreshold) + " " + getResources().getString(R.string.headingunit));
						tts.speak(getResources().getString(R.string.headingtreshold) + Integer.toString((int)headingTreshold) + " " + getResources().getString(R.string.headingunit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "dec heading");
					}
					else {
						tts.speak(getResources().getString(R.string.minheadingtreshold) + getResources().getString(R.string.cant_decrease),TextToSpeech.QUEUE_FLUSH, null);
					}
					
		        }
				if (v== IncreaseHeadingTimeTresholdButton){
					if(headingTimeTreshold >= 0 && headingTimeTreshold < 30) {
						headingTimeTreshold = headingTimeTreshold + headingTimeTresholdStep;
						textViewHeadingTimeTreshold.setText(getResources().getString(R.string.headingtimetreshold)+ " "  + headingTimeTreshold + " " + getResources().getString(R.string.timeunit));
						textViewHeadingTimeTreshold.setContentDescription(getResources().getString(R.string.headingtimetreshold) + headingTimeTreshold + " " + getResources().getString(R.string.timeunit));
						tts.speak(getResources().getString(R.string.headingtimetreshold) + headingTimeTreshold + " " + getResources().getString(R.string.timeunit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "increase heading time");
					}
					else {
						tts.speak(getResources().getString(R.string.maxheadingtimetreshold) + getResources().getString(R.string.cant_increase),TextToSpeech.QUEUE_FLUSH, null);
					}
					
		        }
				if (v== DecreaseHeadingTimeTresholdButton){
					if(headingTimeTreshold > 0 && headingTimeTreshold <= 30) {
						headingTimeTreshold = headingTimeTreshold - headingTimeTresholdStep;
						textViewHeadingTimeTreshold.setText(getResources().getString(R.string.headingtimetreshold)+ " "  + headingTimeTreshold + " " + getResources().getString(R.string.timeunit));
						textViewHeadingTimeTreshold.setContentDescription(getResources().getString(R.string.headingtimetreshold) + headingTimeTreshold + " " + getResources().getString(R.string.timeunit));
						tts.speak(getResources().getString(R.string.headingtimetreshold) + headingTimeTreshold + " " + getResources().getString(R.string.timeunit) ,TextToSpeech.QUEUE_FLUSH, null);
						Log.i("test", "decrease heading time");
					}
					else {
						tts.speak(getResources().getString(R.string.minheadingtimetreshold) + getResources().getString(R.string.cant_decrease),TextToSpeech.QUEUE_FLUSH, null);
					}
					
		        }
				if (v== SaveSettingButton){
				    // put heading & heading treshold
				    editor.putString("headingTreshold", String.valueOf(headingTreshold));
				    editor.putLong("headingTimeTreshold", headingTimeTreshold);
				    editor.putBoolean("isAutoHeading", HeadingAutoCheckBox.isChecked());
				    editor.commit();

					intentMainAutoSetting.putExtra("headingTreshold", headingTreshold);
					intentMainAutoSetting.putExtra("headingTimeTreshold", headingTimeTreshold);
					intentMainAutoSetting.putExtra("isAutoHeading", HeadingAutoCheckBox.isChecked());

					setResult(RESULT_OK, intentMainAutoSetting);
					finish();
				}
		    }// end of onclick		
	    }; //end of new View.LocationListener	
		IncreaseHeadingTresholdButton.setOnClickListener(onclickListener);
		DecreaseHeadingTresholdButton.setOnClickListener(onclickListener);
		IncreaseHeadingTimeTresholdButton.setOnClickListener(onclickListener);
		DecreaseHeadingTimeTresholdButton.setOnClickListener(onclickListener);
		
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
		this.headingTreshold = Double.parseDouble(settings.getString("headingTreshold", "10.0"));
	    this.headingTimeTreshold = settings.getLong("headingTimeTreshold", 5);
	    this.isAutoHeading = settings.getBoolean("isAutoHeading", true);
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
			
			Double LastheadingTreshold = Double.parseDouble(settings.getString("headingTreshold", "10.0"));
		    long LastheadingTimeTreshold = settings.getLong("headingTimeTreshold", 5);
		    boolean LastisAutoHeading = settings.getBoolean("isAutoHeading", true);
		    
			if(LastheadingTreshold != headingTreshold || LastheadingTimeTreshold != headingTimeTreshold || LastisAutoHeading != HeadingAutoCheckBox.isChecked()) {				
				alertDialog = new AlertDialog.Builder(this);
				alertDialog.setTitle(getResources().getString(R.string.title_alertdialog_headingsetting));
				alertDialog.setNegativeButton("YES", new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
					    editor.putString("headingTreshold", String.valueOf(headingTreshold));
					    editor.putLong("headingTimeTreshold", headingTimeTreshold);
					    editor.putBoolean("isAutoHeading", HeadingAutoCheckBox.isChecked());
					    editor.commit();

						intentMainAutoSetting.putExtra("headingTreshold", headingTreshold);
						intentMainAutoSetting.putExtra("headingTimeTreshold", headingTimeTreshold);
						intentMainAutoSetting.putExtra("isAutoHeading", HeadingAutoCheckBox.isChecked());

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
