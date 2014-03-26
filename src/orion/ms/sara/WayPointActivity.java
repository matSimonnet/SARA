package orion.ms.sara;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class WayPointActivity extends Activity {
	// variables declarations
	
		protected static final int RESULT_SPEECH = 1;
		
		//components
		private TextView chooseText = null;
		private Button newWay = null;
		private Spinner way = null;
		
		private TextToSpeech tts = null;

		private int routeNow = 0;//waypoint selected order number
		
		//a list of many waypoints sorted by proximity
		List<String> wayPointList = new ArrayList<String>();
		//testing distances
		private String d1 = "1000";
		private String d2 = "2000";
		
		/*
		private LocationManager lm = null;
		private LocationListener ll = null;
		//positions : 
		@SuppressWarnings("unused")
		private String latitude = "";
		@SuppressWarnings("unused")
		private String longitude = "";
		*/
		

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_way_point);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		//OnInitListener Creation
				OnInitListener onInitListener = new OnInitListener() {
					@Override
					public void onInit(int status) {
					}
				};
				
			    // textToSpeech creation
				tts = new TextToSpeech(this, onInitListener);
				tts.setSpeechRate((float) 2.0);
		
		//TextView
		chooseText = (TextView) findViewById(R.id.textView1);
		//chooseText.setText("Choose the waypoint");
		chooseText.setContentDescription("Please choose a waypoint from the list below");
		
		//waypoint list
		wayPointList.add(d1);
		wayPointList.add(d2);

		
		//Adapter for using array with spinner
		ArrayAdapter<String> arrAdapt = new ArrayAdapter<String>(WayPointActivity.this,android.R.layout.simple_spinner_item,wayPointList);
		arrAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		//Spinner
		way = (Spinner) findViewById(R.id.spinner1);
		way.setOnTouchListener((OnTouchListener) this);
		way.setAdapter(arrAdapt);
		
		way.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				routeNow = position;
				Toast.makeText(WayPointActivity.this, String.valueOf("Selected waypoint : " + wayPointList.get(position)), Toast.LENGTH_SHORT).show();
				tts.speak(String.valueOf("Selected waypoint : " + wayPointList.get(position)), TextToSpeech.QUEUE_FLUSH, null);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				Toast.makeText(WayPointActivity.this, "Selected waypoint : none", Toast.LENGTH_SHORT).show();
				tts.speak("Selected waypoint is none", TextToSpeech.QUEUE_FLUSH, null);
			}
			
		});
		
		

	}
	
	@Override
	  protected void onResume() {
	    super.onResume();
	    way.setTop(routeNow);
	    tts.speak(String.valueOf("resume to the selected waypoint :" + wayPointList.get(routeNow)), TextToSpeech.QUEUE_FLUSH, null);
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
			routeNow = 0;	
			tts.shutdown();
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.way_point, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_way_point,
					container, false);
			return rootView;
		}
	}

}
