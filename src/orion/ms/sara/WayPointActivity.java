package orion.ms.sara;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class WayPointActivity extends Activity {
	// variables declarations
		
		//components
		private TextView chooseText = null;
		private Button newWay = null;
		private Spinner way = null;
		
		private TextToSpeech tts = null;
		
		//waypoint selected order number
		private int routeNow = 0;
		
		//a list of many waypoints sorted by proximity
		static List<WP> wayPointList = new ArrayList<WP>();
		
		//testing WP
		private WP wp1 = new WP("WP1", "1", "2", 90, 2);
		private WP wp2 = new WP("WP2", "1", "2", 45, 2);
		
		
		//to receive new waypoint from NewWayPoint class
		//private WP newWP = null;		

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

		//adding testing waypoint
		wayPointList.add(wp1);
		wayPointList.add(wp2);
		int i=0;
		for(WP temp: wayPointList){
			System.out.println("position " + ++i + "  with name : " + temp.getName());
		}
		Collections.sort(wayPointList);
		
		//alertDialog creation
		final AlertDialog.Builder selectedWay = new AlertDialog.Builder(this);

		//Adapter for using array with spinner
		ArrayAdapter<WP> arrAdapt = new ArrayAdapter<WP>(WayPointActivity.this,R.id.spinner1,wayPointList);
		arrAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		//Spinner
		way = (Spinner) findViewById(R.id.spinner1);
		way.setAdapter(arrAdapt);
		//setOnTouchListener
		/*
		way.setOnTouchListener((OnTouchListener) new  AdapterView.OnItemSelectedListener(){
			//OnSelectedListener creation
		});
		*/
		
		//OnSelectedListener creation
		way.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//set the route to be this waypoint
				routeNow = position;
				Toast.makeText(WayPointActivity.this, String.valueOf("Selected waypoint : " + wayPointList.get(position)), Toast.LENGTH_SHORT).show();
				tts.speak(String.valueOf("Selected waypoint : " + wayPointList.get(position)), TextToSpeech.QUEUE_FLUSH, null);
				
				//open the dialog
				selectedWay.setMessage(String.valueOf("Selected waypoint : " + wayPointList.get(position)));
				//selectedWay.set
				selectedWay.show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				Toast.makeText(WayPointActivity.this, "Selected waypoint : none", Toast.LENGTH_SHORT).show();
				tts.speak("Selected waypoint is none", TextToSpeech.QUEUE_FLUSH, null);
			}
			
		});
		
		//"New Waypoint" button
		//button creation
		newWay = (Button) findViewById(R.id.button1);
		
		// OnClickListener creation
	    View.OnClickListener onclickListener = new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(v==newWay){
					tts.speak("new waypoint", TextToSpeech.QUEUE_FLUSH, null);
					//change to the "NewWayPoint" activity
					Intent newActivity = new Intent(WayPointActivity.this,NewWayPointActivity.class);
					startActivity(newActivity);
				}
				
			}//end of onClick
	    	
	    };//end of View.OnClickListener
	    
	    //setOnClickListener
	    newWay.setOnClickListener(onclickListener);
		

	}
	
	@Override
	  protected void onResume() {
	    super.onResume();
	    way.setTop(routeNow);
	    tts.speak(String.valueOf("resume to the selected waypoint :" + wayPointList.get(routeNow)), TextToSpeech.QUEUE_FLUSH, null);
	   
	    /*
	    //Receive distance from a new waypoint
	    Intent returnWP = getIntent();
	    newWP = (WP)returnWP.getParcelableExtra("newDis");//not sure//
	    
	    //adding new distance into the waypoint list and sort by proximity
	    wayPointList.add(newWP);
	    insertionSort(wayPointList);
	    */
	    
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
