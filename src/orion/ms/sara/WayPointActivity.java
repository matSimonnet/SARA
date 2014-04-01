package orion.ms.sara;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class WayPointActivity extends Activity {
	
	//components
		private TextView chooseText = null;
		private Button newWay = null;
		private Spinner way = null;	
				
		private TextToSpeech tts = null;
		
		//a list of many waypoints sorted by proximity
		public static List<WP> wayPointList = new ArrayList<WP>();
		//testing WP
		private WP wp1 = new WP("Waypoint1", "1la", "1long", 90, 2);
		private WP wp2 = new WP("Waypoint2", "2la", "2long", 45, 2);
		
		//Receiving parameter arrays
		private String newName = "Waypoint1";
		private String newLatitude = "";
		private String newLongitude = "";
		private double newBearing = 0.0;
		
		//Generating default number for a new waypoint's name
		private int lastNum = 0;
		
		//code for communication between activity
		protected int BETWEEN_WAYPOINT_AND_NEWWAYPOINT = 7777777;
		
		//array adapter
		private ArrayAdapter<String> arrAd = null;
		
		//alert dialog after choosing the waypoint from the list
		private AlertDialog.Builder dDialog = null;
		//initial list selection value
		private boolean isSelected = false;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			setContentView(R.layout.activity_way_point);
			
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
			chooseText.setContentDescription("a list containing many waypoints sorted by the least distance");
			
			//adding test
			addNewWPtoList(wayPointList, wp1.getName(), wp1.getLatitude(), wp1.getLongitude(),wp1.getDistance(),wp1.getBearing());
			addNewWPtoList(wayPointList, wp2.getName(), wp2.getLatitude(), wp2.getLongitude(),wp2.getDistance(),wp2.getBearing());			

			//alert dialog creation
			dDialog = new AlertDialog.Builder(this);
			//setOnItemSelectedListener
			way.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() { 
				//OnItemSelectedListener creation
	            @SuppressWarnings("static-access")
				public void onItemSelected(AdapterView<?> adapterView, 
	            	View view, int i, long l) { 
	      				try{
	                		switch(adapterView.getId()){
	                		case R.id.spinner1: 
	                			if(isSelected){
	                				Toast.makeText(WayPointActivity.this,"You selected : "+toNameArrayList(wayPointList).get(i),Toast.LENGTH_SHORT).show();
	        	      				tts.speak("Your Selected : "+toNameArrayList(wayPointList).get(i), tts.QUEUE_FLUSH, null);
	                				dDialog.setTitle("You selected : "+toNameArrayList(wayPointList).get(i));
	                				dDialog.setIcon(android.R.drawable.presence_busy);
	                				dDialog.setMessage("What do you want to do with "+toNameArrayList(wayPointList).get(i)+"?");
	                				dDialog.setNegativeButton("Activate", null);
	                				dDialog.setNeutralButton("Modify", null);
	                				dDialog.setPositiveButton("Delete", null);
	                				dDialog.show();
	                			}
	            				isSelected = true;
	                		}
	                    }catch(Exception e){
	                        e.printStackTrace();
	                    }
	              	}

					@SuppressWarnings("static-access")
					public void onNothingSelected(AdapterView<?> arg0) {
	      				Toast.makeText(WayPointActivity.this,"You selected Empty",Toast.LENGTH_SHORT).show();
	      				tts.speak("Your Selected : nothing", tts.QUEUE_FLUSH, null);
					} 

	        });

			//"New Waypoint" button
			//button creation
			newWay = (Button) findViewById(R.id.button1);
					
			//setOnClickListener
			newWay.setOnClickListener(new View.OnClickListener(){
						// OnClickListener creation			    
						@SuppressWarnings("static-access")
						@Override
						public void onClick(View v) {
							if(v==newWay){
								//notification
								Toast.makeText(WayPointActivity.this,"Clicked new waypoint", Toast.LENGTH_SHORT).show();
								tts.speak("create a new waypoint", tts.QUEUE_FLUSH, null);
								
								//change to the "NewWayPoint" activity
								Intent intentToNewWayPoint = new Intent(WayPointActivity.this,NewWayPointActivity.class);
								
								//pass the parameters including name,latitude,longitude arrays
								intentToNewWayPoint.putExtra("nameArrayFromWP", nameArray(wayPointList));//name
								intentToNewWayPoint.putExtra("latitudeArrayFromWP", latitudeArray(wayPointList));//latitude
								intentToNewWayPoint.putExtra("longitudeArrayFromWP", longitudeArray(wayPointList));//longitude
								//sending default name for a new waypoint
								intentToNewWayPoint.putExtra("defaultNameFromWP", String.valueOf("Waypoint"+(lastNum+1)));
								//start NewWayPoint activity
								startActivityForResult(intentToNewWayPoint, BETWEEN_WAYPOINT_AND_NEWWAYPOINT);
							}
						}//end of onClick
				    	
				    });//end of View.OnClickListener	
	}//end of OnCreate
	
	//to convert from array list of waypoint into name of the waypoint array list
	public static ArrayList<String> toNameArrayList(List<WP> wList){
		ArrayList<String> nameList = new ArrayList<String>();
		for(int i = 0;i<wList.size();i++){
			nameList.add(wList.get(i).getName());
			Log.i("Name to show in the list :", nameList.get(i));
		}
		return nameList;
	}
	
	//to convert from array list of waypoint into name of the waypoint array
	public String[] nameArray(List<WP> wList){
		String[] arrayName = new String[wList.size()];
		for(int i = 0;i<wList.size();i++){
			arrayName[i] = wList.get(i).getName();
		}
		return arrayName;
	}
	//to convert from array list of waypoint into latitude of the waypoint array
	public String[] latitudeArray(List<WP> wList){
		String[] arrayLatitude = new String[wList.size()];
		for(int i = 0;i<wList.size();i++){
			arrayLatitude[i] = wList.get(i).getLatitude();
		}
		return arrayLatitude;
	}
	//to convert from array list of waypoint into longitude of the waypoint array
	public String[] longitudeArray(List<WP> wList){
		String[] arrayLongitude = new String[wList.size()];
		for(int i = 0;i<wList.size();i++){
			arrayLongitude[i] = wList.get(i).getLongitude();
		}
		return arrayLongitude;
	}
	
	//adding new waypoint into the waypoint list
	public void addNewWPtoList(List<WP> wList,String n,String la,String lo,double dis,double bear){
		//Get the latest number after adding a new waypoint
		if(n.contains("Waypoint")){
			lastNum = Integer.parseInt(n.substring(n.lastIndexOf("t")+1));//substring "waypoint" and get the number after that
			Log.i("NameNUM", "lastnum :"+lastNum);
		}
		//Adding the new waypoint into the list
		wList.add(new WP(n,la,lo,dis,bear));
		Collections.sort(wList);//Sorting the lisst by proximity
		
		//set array adapter of the list into the spinner
		way = (Spinner) findViewById(R.id.spinner1);
		arrAd = new ArrayAdapter<String>(WayPointActivity.this,
				android.R.layout.simple_spinner_item, 
				toNameArrayList(wList));
        
		way.setAdapter(arrAd);
	}
	
	//Intent to receive parameters from NewWayPoint
	//get parameters from the NewWayPoint activity when create a new waypoint
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intentFromNewWayPoint){
        super.onActivityResult(requestCode, resultCode, intentFromNewWayPoint);
        if(requestCode == BETWEEN_WAYPOINT_AND_NEWWAYPOINT && resultCode == RESULT_OK){
        	newName = intentFromNewWayPoint.getStringExtra("newName");
    		newLatitude = intentFromNewWayPoint.getStringExtra("newLatitude");
    		newLongitude = intentFromNewWayPoint.getStringExtra("newLongitude");
    		newBearing =  Double.parseDouble(intentFromNewWayPoint.getStringExtra("newBearing"));
			addNewWPtoList(wayPointList, newName, newLatitude, newLongitude,0.0,newBearing);
        }
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
