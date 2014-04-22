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

public class DistanceUnitActivity extends Activity {
	
	//radio group
	private RadioGroup group;
	private RadioButton KilometreRadioButton;
	private RadioButton NMRadioButton;
	
	private Button saveButton;
	private Intent intentToGeneral;
	private TextToSpeech tts = null;
	
	public SharedPreferences settings;
	public SharedPreferences.Editor editor;
	
	private AlertDialog.Builder alertDialog;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_distance_unit);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		//intent creation
		intentToGeneral = new Intent(DistanceUnitActivity.this,GeneralSettingActivity.class);
		
		this.group = (RadioGroup) findViewById(R.id.radioGroup1);
		this.group.setContentDescription("A group of distance unit");
		
		this.KilometreRadioButton = (RadioButton) findViewById(R.id.radioButton1);
		this.KilometreRadioButton.setContentDescription("kilometre and metre unit");
		
		this.NMRadioButton = (RadioButton) findViewById(R.id.radioButton2);
		this.NMRadioButton.setContentDescription("nautical mile unit");
		
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
				if(v == KilometreRadioButton) {
					Log.i("distance unit", "kilometre");
				}
				if(v == NMRadioButton) {
					Log.i("distance unit", "nautical mile");
				}
				if(v == saveButton) {
				    editor.putBoolean("isKilometreSelected", KilometreRadioButton.isChecked());
				    editor.putBoolean("isNMSelected", NMRadioButton.isChecked());
				    editor.commit();
				    
				    intentToGeneral.putExtra("distanceLastAuto", 0.0);
					intentToGeneral.putExtra("isKilometreSelected", KilometreRadioButton.isChecked());
					intentToGeneral.putExtra("isNMSelected", NMRadioButton.isChecked());
					setResult(RESULT_OK, intentToGeneral);
					finish();	
				}
			}
	    };
	    KilometreRadioButton.setOnClickListener(onclickListener);
	    NMRadioButton.setOnClickListener(onclickListener);
	    saveButton.setOnClickListener(onclickListener);

	}//end onCreate

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.distance_unit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.general_setting:
			
		    boolean LastIsKilometreSelected = settings.getBoolean("isKilometreSelected", true);
		    boolean LastIsNMSelected = settings.getBoolean("isNMSelected", false);
		    
			if(LastIsKilometreSelected != KilometreRadioButton.isChecked() || LastIsNMSelected != NMRadioButton.isChecked()) {	
				
				alertDialog = new AlertDialog.Builder(this);
				alertDialog.setTitle(getResources().getString(R.string.title_alertdialog_distanceunit));
				alertDialog.setNegativeButton("YES", new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
					    editor.putBoolean("isKilometreSelected", KilometreRadioButton.isChecked());
					    editor.putBoolean("isNMSelected", NMRadioButton.isChecked());
					    editor.commit();
					    
					    intentToGeneral.putExtra("distanceLastAuto", 0.0);
						intentToGeneral.putExtra("isKilometreSelected", KilometreRadioButton.isChecked());
						intentToGeneral.putExtra("isNMSelected", NMRadioButton.isChecked());
						setResult(RESULT_OK, intentToGeneral);
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
		return super.onOptionsItemSelected(item);
	}
	public void LoadPref() {
	    this.KilometreRadioButton.setChecked(settings.getBoolean("isKilometreSelected", true));  
	    this.NMRadioButton.setChecked(settings.getBoolean("isNMSelected", false));  
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
