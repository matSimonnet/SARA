package orion.ms.sara;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;
import android.widget.TextView;

public class SpeechRateActivity extends Activity {
	//layout components
	private TextView speechText = null;
	private TextView rate = null;
	
	//speech
	private TextToSpeech tts = null;
	private float speechRate = 2.0f;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_speech_rate);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		//set default as the old value
		this.speechRate = GeneralSettingActivity.speechRate;
		
		//OnInitListener Creation
		OnInitListener onInitListener = new OnInitListener() {
			@Override
			public void onInit(int status) {
			}
		};
		
	    // textToSpeech creation
		tts = new TextToSpeech(this, onInitListener);
		tts.setSpeechRate(GeneralSettingActivity.speechRate);
		
		//textView
		speechText = (TextView) findViewById(R.id.textView1);
		speechText.setContentDescription("Adjust the speech rate below");
		
		//rate picker
		rate = (TextView) findViewById(R.id.textView2);
		rate.setContentDescription("speech rate");
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.speech_rate, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.general_setting:
			finish();
			break;
		default:
			break;
		}
		return false;
	}

}
