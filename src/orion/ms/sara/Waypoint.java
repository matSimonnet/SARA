package orion.ms.sara;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.speech.tts.TextToSpeech.OnInitListener;

public class Waypoint extends Activity{
	
	private TextToSpeech tts = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_waypoint);
		
		//OnInitListener Creation
		OnInitListener onInitListener = new OnInitListener() {
			@Override
			public void onInit(int status) {
			}
		};
		
	    // tts creation
		tts = new TextToSpeech(this, onInitListener);
		tts.setSpeechRate((float) 2.0);
		
	}
	
	//  action bar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
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
			//lm.removeUpdates(ll);
			//tts.shutdown();
		}
	
	public boolean onOptionsItemSelected(MenuItem Item){
		switch (Item.getItemId()) {
		case R.id.auto_setting:
			Intent intentAuto_setting = new Intent(Waypoint.this,AutoSetting.class);
			startActivity(intentAuto_setting);
			break;
		case R.id.waypoint_setting:
			Intent intentWaypoint_setting = new Intent(Waypoint.this,Waypoint.class);
			startActivity(intentWaypoint_setting);
			break;
		case R.id.main:
			Intent intentmain = new Intent(Waypoint.this,MainActivity.class);
			startActivity(intentmain);
			break;
		default:
			break;
		}

		return false;
	}

}
