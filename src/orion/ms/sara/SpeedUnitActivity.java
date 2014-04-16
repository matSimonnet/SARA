package orion.ms.sara;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SpeedUnitActivity extends Activity {
	//TextView
	private TextView speedUnitText;
	//radio group
	private RadioGroup group;
	//radio button
	private RadioButton knots;
	private RadioButton kmPerHr;
	//Button
	private Button saveButton;
	
	//intent
	private Intent intentToGeneral;
	//temp speed unit default
	private String tempSpeedUnit = "knots";
	
	private TextToSpeech tts = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_speed_unit);
		setTitle("Speed Unit Setting");
		
		//components and descriptions
		speedUnitText = (TextView) findViewById(R.id.textView1);
		speedUnitText.setContentDescription("Choose the speed unit from the choices");
		knots = (RadioButton) findViewById(R.id.radioButton1);
		knots.setContentDescription("knots until");
		kmPerHr = (RadioButton) findViewById(R.id.radioButton2);
		kmPerHr.setContentDescription("kilometers per hour unit");
		saveButton = (Button) findViewById(R.id.button1);
		saveButton.setContentDescription("save the changing setting");
		group = (RadioGroup) findViewById(R.id.radioGroup1);
		group.setContentDescription("A group of speed unit");
		
		//OnInitListener Creation
		OnInitListener onInitListener = new OnInitListener() {
			@Override
			public void onInit(int status) {
			}
		};
		
	    // textToSpeech creation
		tts = new TextToSpeech(this, onInitListener);
		tts.setSpeechRate(GeneralSettingActivity.speechRate);
		
		//intent creation
		intentToGeneral = new Intent(SpeedUnitActivity.this,GeneralSettingActivity.class);
		
		//setOnClick
		//knots
		knots.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				if(v==knots) tempSpeedUnit = "knots";
			}
		});
		
		//kilometers per hour
		kmPerHr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(v==kmPerHr) tempSpeedUnit = "km/hr";
			}
		});
		
		//save button
		saveButton.setOnClickListener(new OnClickListener() {
			//onClick creation
			@SuppressWarnings("static-access")
			@SuppressLint("ShowToast")
			@Override
			public void onClick(View v) {
				if(v==saveButton){
					if(knots.isChecked()){
						tempSpeedUnit = "knots";
						Toast.makeText(SpeedUnitActivity.this, "Speed unit changes to knots", Toast.LENGTH_SHORT);
						tts.speak("Speed unit changes to knots", tts.QUEUE_FLUSH, null);
					}
					else if(kmPerHr.isChecked()){
						tempSpeedUnit = "km/hr";
						Toast.makeText(SpeedUnitActivity.this, "Speed unit changes to kilometers per hour", Toast.LENGTH_SHORT);
						tts.speak("Speed unit changes to kilometers per hour", tts.QUEUE_FLUSH, null);
					}
					intentToGeneral.putExtra("choosingSpeedUnit", tempSpeedUnit);
					setResult(RESULT_OK,intentToGeneral);
					finish();
				}//end if
			}
		});//end setOnClick
		
	}//end onCreate

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.speed_unit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.general_setting:
			//pass the parameters
			intentToGeneral.putExtra("choosingSpeedUnit",tempSpeedUnit);//speed unit
			setResult(RESULT_OK, intentToGeneral);
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
