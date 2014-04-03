package orion.ms.sara;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.Toast;

public class WayPointActivity extends Activity {
	
		//components
		private Button newWay = null;
		private Spinner way = null;	
				
		private TextToSpeech tts = null;
		
		//a list of many waypoints sorted by proximity
		public static List<WP> wayPointList = new ArrayList<WP>();
		
		//Receiving parameter arrays
		private String newName = "Waypoint1";
		private String newLatitude = "";
		private String newLongitude = "";
		
		//string for each attribute of the modifying waypoint
		private String modName = "";
		private String modLatitude = "";
		private String modLongitude = "";
		
		//Generating default number for a new waypoint's name
		private int lastNum = 0;
		
		//code for communication between activity
		protected int NEW_WAYPOINT = 7777777;
		protected int MODIFY_WAYPOINT = 9999999;
		
		//array adapter
		private ArrayAdapter<String> arrAd = null;
		
		//alert dialog 
		private AlertDialog.Builder choosingDialog = null;//after choosing the waypoint from the list
		private AlertDialog.Builder deletingDialog = null;//after choosing delete button from the list
		
		//choosing waypoint
		private WP choosingWaypoint = null;
		
		//location handler
		private LocationManager lm = null;	
		private LocationListener ll = null;

		//current position
		private String currentLatitude = "0.0";
		private String currentLongitude = "0.0";
		private float[] currentResult = new float[3];
		private double currentDistance = 0.0;
		
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

			
			//location manager creation
			lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			ll = new LocationListener(){
				//LocationListener creation
				@Override
				public void onLocationChanged(Location loc) {
					currentLatitude = String.valueOf(loc.getLatitude());
					currentLongitude = String.valueOf(loc.getLongitude());
				}

				@Override
				public void onProviderDisabled(String provider) {
					Toast.makeText( getApplicationContext(),"Gps Disabled",Toast.LENGTH_SHORT).show();	
				}

				@Override
				public void onProviderEnabled(String provider) {
					Toast.makeText( getApplicationContext(),"Gps Enabled",Toast.LENGTH_SHORT).show();	
				}

				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) {

				}
				
			};//end of locationListener creation

			//update location
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
						
			//sorting waypoint
			sortingWaypointList(wayPointList);

			//alert dialog creation
			choosingDialog = new AlertDialog.Builder(this);
			deletingDialog = new AlertDialog.Builder(this);
			
			way.setContentDescription("Choose the waypoint in ");
			//setOnItemSelectedListener
			way.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() { 
				//OnItemSelectedListener creation
	            @SuppressWarnings("static-access")
				public void onItemSelected(final AdapterView<?> adapterView, View view, int i, long l) { 
	      				try{
	                		switch(adapterView.getId()){
	                		case R.id.spinner1: 
	                				//notify
	                				Toast.makeText(WayPointActivity.this,"You selected : "+toNameArrayList(wayPointList).get(i),Toast.LENGTH_SHORT).show();
	        	      				tts.speak("Your Selected : "+toNameArrayList(wayPointList).get(i), tts.QUEUE_FLUSH, null);
	                				//choosing waypoint
	        	      				choosingWaypoint = wayPointList.get(i);
	        	      				
	        	      				//dialog creation
	        	      				choosingDialog.setTitle("You selected : "+choosingWaypoint.getName());
	                				choosingDialog.setIcon(android.R.drawable.presence_busy);
	                				choosingDialog.setMessage("What do you want to do with "+choosingWaypoint.getName());
	                				
	                				//setOnClickListener
	                				
	                				//activate button OnClickListener
	                				choosingDialog.setNegativeButton("Activate", new OnClickListener(){
	                					//activate button OnClickListener creation
										@Override
										public void onClick(DialogInterface arg0, int arg1) {
			                				//notify
											Toast.makeText(WayPointActivity.this,"Activate",Toast.LENGTH_SHORT).show();
											tts.speak("Activate", tts.QUEUE_FLUSH, null);

			                				//change back to the main activity
											Intent intentToMain = new Intent(WayPointActivity.this,MainActivity.class);
											//passing activate waypoint name and position
											intentToMain.putExtra("actName",choosingWaypoint.getName());//name
											intentToMain.putExtra("actLatitude", Double.parseDouble( choosingWaypoint.getLatitude()));//latitude
											intentToMain.putExtra("actLongitude", Double.parseDouble(choosingWaypoint.getLongitude()));//longitude
											
											//back to WayPoint activity and send some parameters to the activity
											setResult(RESULT_OK, intentToMain);
											finish();
										}
	                				});//end activate button
	                				
	                				//modify button OnClickListener
	                				choosingDialog.setNeutralButton("Modify", new OnClickListener(){
	                					//modify button OnClickListener creation
										@Override
										public void onClick(DialogInterface dialog,int which) {
											//notification
											Toast.makeText(WayPointActivity.this,"Modify",Toast.LENGTH_SHORT).show();
											tts.speak("Modify", tts.QUEUE_FLUSH, null);
											
											//change to the "Modify" activity
											Intent intentToModify = new Intent(WayPointActivity.this,ModifyActivity.class);
											//pass the parameters including name,latitude,longitude arrays
											intentToModify.putExtra("nameArrayFromWP", nameArray(wayPointList));//name
											intentToModify.putExtra("latitudeArrayFromWP", latitudeArray(wayPointList));//latitude
											intentToModify.putExtra("longitudeArrayFromWP", longitudeArray(wayPointList));//longitude
											//passing modifying waypoint name and position
											intentToModify.putExtra("modName", choosingWaypoint.getName());//name
											intentToModify.putExtra("modLatitude", choosingWaypoint.getLatitude());//latitude
											intentToModify.putExtra("modLongitude", choosingWaypoint.getLongitude());//longitude
											//start NewWayPoint activity
											startActivityForResult(intentToModify, MODIFY_WAYPOINT);
										}//end of onClick
	                					
	                				});//end modify button
	                				
	                				//delete button OnClickListener
	                				choosingDialog.setPositiveButton("Delete", new OnClickListener(){
	                					//delete button OnClickListener creation
										@Override
										public void onClick(DialogInterface dialog,int which) {
			                				//notify
											Toast.makeText(WayPointActivity.this,"Delete",Toast.LENGTH_SHORT).show();
											tts.speak("Delete", tts.QUEUE_FLUSH, null);
											
											//dialog creation
											deletingDialog.setTitle("Are you sure deleting "+choosingWaypoint.getName()+"?");
											deletingDialog.setIcon(android.R.drawable.presence_busy);
											tts.speak("Are you sure deleting "+choosingWaypoint.getName()+"?", tts.QUEUE_FLUSH, null);

											//button
											deletingDialog.setPositiveButton("Cancel", null);
											deletingDialog.setNegativeButton("Sure", new OnClickListener() {
												//OnClick listener for delete button
												@Override
												public void onClick(DialogInterface dialog, int which) {
													deleteWPfromList(wayPointList,choosingWaypoint);
												}
											});
											//show the deleting dialog
											deletingDialog.show();
										}
	                				});//end delete button	          
	                				
	                				//show the choosing dialog
	                				choosingDialog.show();
	                			}//end switch case
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
								startActivityForResult(intentToNewWayPoint, NEW_WAYPOINT);
							}
						}//end of onClick
				    	
				    });//end of newway clickListener	
	
	}//end of OnCreate
	
	//to convert from array list of waypoint into name of the waypoint array list
	public static ArrayList<String> toNameArrayList(List<WP> wList){
		ArrayList<String> nameList = new ArrayList<String>();
		for(int i = 0;i<wList.size();i++){
			nameList.add(wList.get(i).getName());
			//Log.i("Name to show in the list :", nameList.get(i));
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
			lastNum = Integer.parseInt(n.substring(n.lastIndexOf("t")+1));//substring "waypoint" name to get the number after that
			Log.i("NameNUM", "lastnum :"+lastNum);
		}
		//calculating the distance
		Location.distanceBetween(Double.parseDouble(la), Double.parseDouble(lo), Double.parseDouble(currentLatitude), 
						Double.parseDouble(currentLongitude), currentResult);
		Log.i("cur dis", ""+currentResult[0]+"bearing"+currentResult[1]);
		currentDistance = currentResult[0];
		//Adding the new waypoint into the list
		wList.add(new WP(n,la,lo,currentDistance,currentResult[1]));
		sortingWaypointList(wList);//sorting the list
	}
	
	//deleting the waypoint from the waypoint list
	public void deleteWPfromList(List<WP> wList, WP del){
		//Deleting the waypoint from the list
		wList.remove(del);
		sortingWaypointList(wList);//sorting the list
	}
	
	//sorting the waypoint list by proximity calculated from current distance
		private void sortingWaypointList(List<WP> wList){
			//temp waypoint, distance and result
			WP tempWP = null;
			float[] tempResult = new float[3];
			for(int i = 0;i< wList.size();i++){
				tempWP = wList.get(i);
				//calculate new distance
				Location.distanceBetween(Double.parseDouble(tempWP.getLatitude()), Double.parseDouble(tempWP.getLongitude()), 
						Double.parseDouble(currentLatitude), Double.parseDouble(currentLongitude), tempResult);
				//set up the new distance into every waypoint in the list
				wList.get(i).setDistance(tempResult[0]);
				//Log.i("Cur dis for sort", "item "+i+" dis now="+tempResult[0]);
			}
			Collections.sort(wList);//Sorting the list by proximity

			//set array adapter of the list into the spinner
			way = (Spinner) findViewById(R.id.spinner1);
			arrAd = new ArrayAdapter<String>(WayPointActivity.this,
							android.R.layout.simple_spinner_item, 
							toNameArrayList(wList));
			        
			way.setAdapter(arrAd);
		}
	
	//Intent to handle receive parameters from NewWayPoint and Modify
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intentFromAnother){
        super.onActivityResult(requestCode, resultCode, intentFromAnother);
    	//get parameters from the NewWayPoint activity when create a new waypoint
        if(requestCode == NEW_WAYPOINT && resultCode == RESULT_OK){
        	newName = intentFromAnother.getStringExtra("newName");
    		newLatitude = intentFromAnother.getStringExtra("newLatitude");
    		newLongitude = intentFromAnother.getStringExtra("newLongitude");
   			addNewWPtoList(wayPointList, newName, newLatitude, newLongitude,0.0,0.0);
        }
      //get parameters from the Modify activity and replace the old information
        if(requestCode == MODIFY_WAYPOINT && resultCode == RESULT_OK){
        	modName = intentFromAnother.getStringExtra("modName");
    		modLatitude = intentFromAnother.getStringExtra("modLatitude");
    		modLongitude = intentFromAnother.getStringExtra("modLongitude");

    		//replace the old information with the modifying information
    		choosingWaypoint.setName(modName);
    		choosingWaypoint.setLatitude(modLatitude);
    		choosingWaypoint.setLongitude(modLongitude);
    		sortingWaypointList(wayPointList);
       		//Log.i("Receive from modify", "Name "+modName+" La "+modLatitude+" lo "+modLongitude);
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
