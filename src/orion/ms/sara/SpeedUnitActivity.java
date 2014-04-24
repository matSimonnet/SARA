package orion.ms.sara;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SpeedUnitActivity extends Activity {
	
	//radio group
	private RadioGroup group;
	private RadioButton knotsRadioButton;
	private RadioButton kmPerHrRadioButton;
	
	private Button saveButton;
	private Intent intentToGeneral;
	private TextToSpeech tts = null;
	
	public SharedPreferences settings;
	public SharedPreferences.Editor editor;
	
	private AlertDialog.Builder alertDialog;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_speed_unit);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		//intent creation
		intentToGeneral = new Intent(SpeedUnitActivity.this,GeneralSettingActivity.class);
		
		this.group = (RadioGroup) findViewById(R.id.radioGroup1);
		this.group.setContentDescription(getResources().getString(R.string.choos_speed_unit));
		
		this.knotsRadioButton = (RadioButton) findViewById(R.id.radioButton1);
		this.knotsRadioButton.setContentDescription(getResources().getString(R.string.knotunit));
		
		this.kmPerHrRadioButton = (RadioButton) findViewById(R.id.radioButton2);
		this.kmPerHrRadioButton.setContentDescription(getResources().getString(R.string.kilometersperhourunit));
		
		this.saveButton = (Button) findViewById(R.id.button1);
		this.saveButton.setContentDescription(getResources().getString(R.string.savesetting));
		
	    // Restore preferences
		this.settings = getSharedPreferences(MyLocationListener.PREFS_NAME, 0);
		this.editor = settings.edit();
		LoadPref();

		
		//OnInitListener Creation
		OnInitListener onInitListener = new OnInitListener() {
			@Override
			public void onInit(int status) {
			}
		};
		
	    // textToSpeech creation
		tts = new TextToSpeech(this, onInitListener);
		
	    View.OnClickListener onclickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(v == knotsRadioButton) {
					Log.i("speedunit", "knot");
				}
				if(v == kmPerHrRadioButton) {
					Log.i("speedunit", "km/hr");
				}
				if(v == saveButton) {
				    editor.putBoolean("isKnotsSelected", knotsRadioButton.isChecked());
				    editor.putBoolean("isKmPerHrSelected", kmPerHrRadioButton.isChecked());
				    editor.commit();
				    
				    intentToGeneral.putExtra("speedLastAuto", 0.0);
					intentToGeneral.putExtra("isKnotsSelected", knotsRadioButton.isChecked());
					intentToGeneral.putExtra("isKmPerHrSelected", kmPerHrRadioButton.isChecked());
					setResult(RESULT_OK, intentToGeneral);
					finish();	
				}
			}
	    };
	    knotsRadioButton.setOnClickListener(onclickListener);
	    kmPerHrRadioButton.setOnClickListener(onclickListener);
	    saveButton.setOnClickListener(onclickListener);

	}//end onCreate

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.speed_unit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.general_setting:
			
		    boolean LastIsKnotsSelected = settings.getBoolean("isKnotsSelected", true);
		    boolean LastIsKmPerHrSelected = settings.getBoolean("isKmPerHrSelected", false);
		    
			if(LastIsKnotsSelected != knotsRadioButton.isChecked() || LastIsKmPerHrSelected != kmPerHrRadioButton.isChecked()) {	
				
				alertDialog = new AlertDialog.Builder(this);
				alertDialog.setTitle(getResources().getString(R.string.title_alertdialog_speedunit));
				alertDialog.setNegativeButton("YES", new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					    editor.putBoolean("isKnotsSelected", knotsRadioButton.isChecked());
					    editor.putBoolean("isKmPerHrSelected", kmPerHrRadioButton.isChecked());
					    editor.commit();
					    
					    intentToGeneral.putExtra("speedLastAuto", 0.0);
						intentToGeneral.putExtra("isKnotsSelected", knotsRadioButton.isChecked());
						intentToGeneral.putExtra("isKmPerHrSelected", kmPerHrRadioButton.isChecked());
						setResult(RESULT_OK, intentToGeneral);
						finish();					
					}
				});
				alertDialog.setPositiveButton("NO", new OnClickListener(){
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
		return super.onOptionsItemSelected(item);
	}
	public void LoadPref() {
	    this.knotsRadioButton.setChecked(settings.getBoolean("isKnotsSelected", true));  
	    this.kmPerHrRadioButton.setChecked(settings.getBoolean("isKmPerHrSelected", false));  
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
