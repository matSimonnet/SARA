package com.example.mainact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class WayPointActivity extends Activity {//implements AdapterView.OnItemSelectedListener {
	
	//components
		private TextView chooseText = null;
		private Button newWay = null;
		private Spinner way = null;	
				
		private TextToSpeech tts = null;
		
		//a list of many waypoints sorted by proximity
		public static List<WP> wayPointList = new ArrayList<WP>();
				
		//testing WP
		private WP wp1 = new WP("WP1", "1", "2", 90, 2);
		private WP wp2 = new WP("WP2", "1", "2", 45, 2);

	@Override
	public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			setContentView(R.layout.activity_way_point);
	
			chooseText = (TextView) findViewById(R.id.textView1);
			chooseText.setContentDescription("a list containing many waypoints sorted by the least distance");
			
			wayPointList.add(wp1);
			wayPointList.add(wp2);
			Collections.sort(wayPointList);

			way = (Spinner) findViewById(R.id.spinner1);
			/*
			arrList.add("Mercury");
			arrList.add("Venus");
			arrList.add("Earth");
			arrList.add("Mars");
			*/
			ArrayAdapter<String> arrAd = new ArrayAdapter<String>(WayPointActivity.this,
					android.R.layout.simple_spinner_item, 
					toNameList(wayPointList));
	        
			way.setAdapter(arrAd);
			
			way.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() { 

	              public void onItemSelected(AdapterView<?> adapterView, 
	            	View view, int i, long l) { 
	            	// TODO Auto-generated method stub
	      				Toast.makeText(WayPointActivity.this,"Your Selected : "+toNameList(wayPointList).get(i),Toast.LENGTH_SHORT).show();
	             
	              	}

					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
	      				Toast.makeText(WayPointActivity.this,"Your Selected Empty",Toast.LENGTH_SHORT).show();
					} 

	        });

			//"New Waypoint" button
			//button creation
			newWay = (Button) findViewById(R.id.button1);
					
			//setOnClickListener
			newWay.setOnClickListener(new View.OnClickListener(){
						// OnClickListener creation			    
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if(v==newWay){
								Toast.makeText(WayPointActivity.this,"Clicked new waypoint", Toast.LENGTH_SHORT).show();
								//tts.speak("new waypoint", tts.QUEUE_FLUSH, null);
								//change to the "NewWayPoint" activity
								Intent newActivity = new Intent(WayPointActivity.this,NewWayPointActivity.class);
								startActivity(newActivity);
							}
						}//end of onClick
				    	
				    });//end of View.OnClickListener
					
	}
	//to convert from array list of waypoint into name of the waypoint list
	public static ArrayList<String> toNameList(List<WP> wList){
		ArrayList<String> nameList = new ArrayList<String>();
		for(int i = 0;i<wList.size();i++){
			nameList.add(wList.get(i).getName());
		}
		return nameList;
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

}
