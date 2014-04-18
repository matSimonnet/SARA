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
	
	//radio group
	private RadioGroup group;
	private RadioButton portandstarboardRadioButton;
	private RadioButton cardinalRadioButton;
	
	private Button saveButton;
	private Intent intentToGeneral;
	private TextToSpeech tts = null;
	
	public SharedPreferences settings;
	public SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bearing_unit);
		
		//intent creation
		intentToGeneral = new Intent(BearingUnitActivity.this,GeneralSettingActivity.class);
		
		this.group = (RadioGroup) findViewById(R.id.radioGroup1);
		this.group.setContentDescription("A group of bearing unit");
		
		this.portandstarboardRadioButton = (RadioButton) findViewById(R.id.radioButton1);
		this.portandstarboardRadioButton.setContentDescription("port and starboard unit");
		
		this.cardinalRadioButton = (RadioButton) findViewById(R.id.radioButton2);
		this.cardinalRadioButton.setContentDescription("cardinal unit");
		
		this.saveButton = (Button) findViewById(R.id.button1);
		this.saveButton.setContentDescription("save");
		
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
		tts.setSpeechRate(GeneralSettingActivity.speechRate);
		
	    View.OnClickListener onclickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(v == portandstarboardRadioButton) {
					Log.i("bearingunit", "port and starboard");
				}
				if(v == cardinalRadioButton) {
					Log.i("bearingunit", "cardinal");
				}
				if(v == saveButton) {
				    editor.putBoolean("isPortandstarboardSelected", portandstarboardRadioButton.isChecked());
				    editor.putBoolean("isCardinalSelected", cardinalRadioButton.isChecked());
				    editor.commit();
				    
					intentToGeneral.putExtra("isPortandstarboardSelected", portandstarboardRadioButton.isChecked());
					intentToGeneral.putExtra("isCardinalSelected", cardinalRadioButton.isChecked());
					setResult(RESULT_OK, intentToGeneral);
					finish();	
				}
			}
	    };
	    portandstarboardRadioButton.setOnClickListener(onclickListener);
	    cardinalRadioButton.setOnClickListener(onclickListener);
	    saveButton.setOnClickListener(onclickListener);

	}//end onCreate

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.bearing_unit, menu);
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
	    this.portandstarboardRadioButton.setChecked(settings.getBoolean("isPortandstarboardSelected", true));  
	    this.cardinalRadioButton.setChecked(settings.getBoolean("isCardinalSelected", false));  
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
