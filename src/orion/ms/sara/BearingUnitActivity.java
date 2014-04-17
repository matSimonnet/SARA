package orion.ms.sara;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class BearingUnitActivity extends Activity {
	
	public static final String PREFS_NAME = "MyPrefsFile";

	//radio group
	private RadioGroup group;
	private RadioButton knotsRadioButton;
	private RadioButton kmPerHrRadioButton;
	
	private Button saveButton;
	private Intent intentToGeneral;
	private TextToSpeech tts = null;
	
	public SharedPreferences settings;
	public SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bearing_unit);
		setTitle("Speed Unit Setting");
		
		//intent creation
		intentToGeneral = new Intent(BearingUnitActivity.this,GeneralSettingActivity.class);
		
		this.group = (RadioGroup) findViewById(R.id.radioGroup1);
		this.group.setContentDescription("A group of speed unit");
		
		this.knotsRadioButton = (RadioButton) findViewById(R.id.radioButton1);
		this.knotsRadioButton.setContentDescription("knots unit");
		
		this.kmPerHrRadioButton = (RadioButton) findViewById(R.id.radioButton2);
		this.kmPerHrRadioButton.setContentDescription("kilometers per hour unit");
		
		this.saveButton = (Button) findViewById(R.id.button1);
		this.saveButton.setContentDescription("save the changing setting");
		
	    // Restore preferences
		this.settings = getSharedPreferences(PREFS_NAME, 0);
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
		tts.setSpeechRate(GeneralSettingActivity.speechRate);
		
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
			finish();
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
