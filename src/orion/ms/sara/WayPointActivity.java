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
import android.content.SharedPreferences;
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
		
		//a list of many way points sorted by proximity
		public static List<WP> wayPointList = new ArrayList<WP>();
		
		//Receiving parameters from new waypoint
		private String newName = "Waypoint1";
		private String newLatitude = "0.0";
		private String newLongitude = "0.0";
		
		//strings for each attribute of the modifying way point
		private String modName = "";
		private String modLatitude = "";
		private String modLongitude = "";
		
		//Generating a number for a new waypoint's default name
		public static int lastNumberForWaypoint = 0;
		
		//code for communication between activity
		protected int NEW_WAYPOINT = 7777777;
		protected int MODIFY_WAYPOINT = 9999999;
		
		//array adapter
		private ArrayAdapter<String> arrAd = null;
		
		//alert dialog 
		private AlertDialog.Builder choosingDialog = null;//after choosing the way point from the list
		private AlertDialog.Builder deletingDialog = null;//after choosing delete button from the list
		
		//choosing way point
		private WP choosingWaypoint = null;
		
		//location handler
		private LocationManager lm = null;	
		private LocationListener ll = null;

		//current position
		private String currentLatitude = "0.0";
		private String currentLongitude = "0.0";
		
		//shared preferences
		SharedPreferences sharedPref ;
		
		//intent
		private Intent intentToMain;
		
		//selected way point item number
		private int selectedItem = 0;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			setContentView(R.layout.activity_way_point);
			setTitle(R.string.title_activity_way_point);
			
			//load preferences
			sharedPref = this.getPreferences(Context.MODE_PRIVATE);
			loadPref();
			
			//check if there is any instantWaypoint
			if(MainActivity.instList.size()!=0){
				//two lists combination
				wayPointList.addAll(MainActivity.instList);
				MainActivity.instList.clear();//empty the instant waypoint list
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
				
			//sorting way point
			sortingWaypointList(wayPointList);

			//alert dialog creation
			choosingDialog = new AlertDialog.Builder(this);
			deletingDialog = new AlertDialog.Builder(this);
			
			//Intent creation
			intentToMain = new Intent(WayPointActivity.this,MainActivity.class);

			//spinner set up
			way.setContentDescription("Choose the waypoint in ");
			
			//setOnItemSelectedListener
			way.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() { 
				//OnItemSelectedListener creation
	            @SuppressWarnings("static-access")
				public void onItemSelected(final AdapterView<?> adapterView, View view, int i, long l) { 
	      				try{
	                		switch(adapterView.getId()){
	                		case R.id.spinner1: 
	                				if(i!=0){
	                					//notify
		                				Toast.makeText(WayPointActivity.this,"You selected : "+toNameArrayList(wayPointList).get(i),Toast.LENGTH_SHORT).show();
		        	      				tts.speak("Your Selected : "+toNameArrayList(wayPointList).get(i), tts.QUEUE_FLUSH, null);
		        	      				selectedItem = i;
	                				}
	                				//choosing way point
	        	      				choosingWaypoint = wayPointList.get(selectedItem);
	        	      				
	        	      				
	        	      				//dialog creation
	        	      				choosingDialog.setTitle("You selected : "+choosingWaypoint.getName());
	                				choosingDialog.setIcon(android.R.drawable.presence_busy);
	                				choosingDialog.setMessage("What do you want to do with "+choosingWaypoint.getName()+"?");
	                				
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
											//passing activate way point name and position
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
											//passing modifying way point name and position
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
	                				if(i!=0){
	                					//show the choosing dialog if selected some way point from the list
		                				choosingDialog.show();
		                				sortingWaypointList(wayPointList);
	                				}//end if
	                			}//end switch case
	                    }catch(Exception e){
	                        e.printStackTrace();
	                    }//end try-catch
	              	}

					@SuppressWarnings("static-access")
					public void onNothingSelected(AdapterView<?> arg0) {
	      				Toast.makeText(WayPointActivity.this,"You selected Empty",Toast.LENGTH_SHORT).show();
	      				tts.speak("Your Selected : nothing", tts.QUEUE_FLUSH, null);
					} 
					
				

	        });
			
			//"New Way point" button
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
								
								//sending default name for a new way point
								intentToNewWayPoint.putExtra("defaultNameFromWP", String.valueOf("Waypoint"+(lastNumberForWaypoint+1)));
								//start NewWayPoint activity
								startActivityForResult(intentToNewWayPoint, NEW_WAYPOINT);
							}
						}//end of onClick
				    	
				    });//end of new way clickListener	
	
	}//end of OnCreate
	
	//to convert from array list of way point into name of the way point array list
	public static ArrayList<String> toNameArrayList(List<WP> wList){
		ArrayList<String> nameList = new ArrayList<String>();
		for(int i = 0;i<wList.size();i++){
			nameList.add(wList.get(i).getName());
			//Log.i("Name to show in the list :", nameList.get(i));
		}
		return nameList;
	}
	
	//to convert from array list of way point into name of the way point array
	public String[] nameArray(List<WP> wList){
		String[] arrayName = new String[wList.size()];
		for(int i = 0;i<wList.size();i++){
			arrayName[i] = wList.get(i).getName();
		}
		return arrayName;
	}
	//to convert from array list of way point into latitude of the way point array
	public String[] latitudeArray(List<WP> wList){
		String[] arrayLatitude = new String[wList.size()];
		for(int i = 0;i<wList.size();i++){
			arrayLatitude[i] = wList.get(i).getLatitude();
		}
		return arrayLatitude;
	}
	//to convert from array list of way point into longitude of the way point array
	public String[] longitudeArray(List<WP> wList){
		String[] arrayLongitude = new String[wList.size()];
		for(int i = 0;i<wList.size();i++){
			arrayLongitude[i] = wList.get(i).getLongitude();
		}
		return arrayLongitude;
	}
	
	public static List<WP> getWayPointList() {
		return wayPointList;
	}

	//adding new way point into the way point list
	public void addNewWPtoList(List<WP> wList,String n,String la,String lo){
		//Get the latest number after adding a new way point
		if(n.substring(0,8).equalsIgnoreCase("waypoint")){
			lastNumberForWaypoint = Integer.parseInt(n.substring(8));//substring "WayPoint" name to get the number after that
		}
		//Adding the new way point into the list
		wList.add(new WP(n,la,lo));//create new way point with assuming distance
		sortingWaypointList(wList);//sorting the list
	}
	
	//deleting the way point from the way point list
	public void deleteWPfromList(List<WP> wList, WP del){
		//Deleting the way point from the list
		wList.remove(del);
		sortingWaypointList(wList);//sorting the list
	}
	
	//sorting the way point list by proximity calculated from current distance
		private void sortingWaypointList(List<WP> wList){
			//temp way point, distance and result
			WP tempWP = null;
			float[] tempResult = new float[3];
			//default value
			WP first = null;
			//remove the first default way point before sorting
			if(wList.size()>0){
				first = wList.remove(0);
			}
			first = new WP("Please selected a waypoint","","");
			
			//Recalculating distance in the list
			for(int i = 1;i< wList.size();i++){
				tempWP = wList.get(i);
				//calculate new distance
				double tempLa = Double.parseDouble(tempWP.getLatitude());
				double tempLong = Double.parseDouble(tempWP.getLongitude());
				double curLa = Double.parseDouble(currentLatitude);
				double curLong = Double.parseDouble(currentLongitude);
				
				Location.distanceBetween(tempLa, tempLong, curLa, curLong, tempResult);
				//set up the new distance from the current position into every way point in the list
				wList.get(i).setDistance(tempResult[0]);
				Log.i("Cur dis for sort", "===============item "+i+" dis now = "+tempResult[0]+"===============");
			}
			Collections.sort(wList);//Sorting the list by proximity
			wList.add(0, first);

			//set array adapter of the list into the spinner
			way = (Spinner) findViewById(R.id.spinner1);
			arrAd = new ArrayAdapter<String>(WayPointActivity.this,
							android.R.layout.simple_spinner_item, 
							toNameArrayList(wList));
			arrAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);      
			way.setAdapter(arrAd);
			
			//save the last number of default name value and attributes of way point in the list
			savePref(lastNumberForWaypoint,nameArray(wList),latitudeArray(wList),longitudeArray(wList));
			
		}//end sorting
		
		//load preferences
		private void loadPref(){
			//last number
			int lnum = sharedPref.getInt(getString(R.string.save_last_num),0);
			lastNumberForWaypoint = lnum;
			
			//name array
			int nameSize = sharedPref.getInt("nameArray" + "_size", 0);  
		    String name[] = new String[nameSize];  
		    for(int i=0;i<nameSize;i++)  
		        name[i] = sharedPref.getString("nameArray" + "_" + i, null);
		    
		    //latitude array
		    int latitudeSize = sharedPref.getInt("latitudeArray" + "_size", 0);  
		  	String latitude[] = new String[latitudeSize];  
		  	for(int i=0;i<latitudeSize;i++)  
		  		latitude[i] = sharedPref.getString("latitudeArray" + "_" + i, null);
		  	
		  	//longitude array
		    int longitudeSize = sharedPref.getInt("longitudeArray" + "_size", 0);  
		  	String longitude[] = new String[longitudeSize];  
		  	for(int i=0;i<longitudeSize;i++)  
		  		longitude[i] = sharedPref.getString("longitudeArray" + "_" + i, null);
		  	
		  	//way point list
		  	List<WP> wList = new ArrayList<WP>();
		  	for(int i=0;i<nameSize;i++){
			  	wList.add(new WP(name[i],latitude[i],longitude[i]));
		  	}
		  	
		  	wayPointList = wList;
		}
		
		//save preferences
		private void savePref(int lnum, String[] name, String[] lati, String[] longi){
			SharedPreferences.Editor editor = sharedPref.edit();
			//last number for new waypoint 
			editor.putInt(getString(R.string.save_last_num), lnum);
			
			//name array
			editor.putInt("nameArray" +"_size", name.length);  
		    for(int i=0;i<name.length;i++)  
		        editor.putString("nameArray" + "_" + i, name[i]);
		    
		    //latitude array
		    editor.putInt("latitudeArray" +"_size", lati.length);  
		    for(int i=0;i<lati.length;i++)  
		        editor.putString("latitudeArray" + "_" + i, lati[i]);
		    
		    //longitude array
		    editor.putInt("longitudeArray" +"_size", longi.length);  
		    for(int i=0;i<longi.length;i++)  
		        editor.putString("longitudeArray" + "_" + i, longi[i]);
			editor.commit();
			
		}
		
	
	//Intent to handle receive parameters from NewWayPoint and Modify
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intentFromAnother){
        super.onActivityResult(requestCode, resultCode, intentFromAnother);
		
    	//get parameters from the NewWayPoint activity when create a new way point
        if(requestCode == NEW_WAYPOINT && resultCode == RESULT_OK){
        	newName = intentFromAnother.getStringExtra("newName");
    		newLatitude = intentFromAnother.getStringExtra("newLatitude");
    		newLongitude = intentFromAnother.getStringExtra("newLongitude");
    		//not from pressing menu item
    		if(!newName.equals("") && !newLatitude.equals("") && !newLongitude.equals(""))
    			addNewWPtoList(wayPointList, newName, newLatitude, newLongitude);
    		//pressing save and activate
    		if(NewWayPointActivity.isAlsoActivateForNWP){
    			//change back to the main activity
				//passing activate way point name and position
				intentToMain.putExtra("actLatitude", newLatitude);//latitude
				intentToMain.putExtra("actLongitude", newLongitude);//longitude
				
				//back to main activity and send some parameters to the activity
				setResult(RESULT_OK, intentToMain);
				
				finish();
    		}//end if for pressing save and activate
        }
        
      //get parameters from the Modify activity and replace the old information
        if(requestCode == MODIFY_WAYPOINT && resultCode == RESULT_OK){
        	modName = intentFromAnother.getStringExtra("modName");
    		modLatitude = intentFromAnother.getStringExtra("modLatitude");
    		modLongitude = intentFromAnother.getStringExtra("modLongitude");
    		//not pressing from menu item
    		if(!modName.equals("") && !modLatitude.equals("") && !modLongitude.equals("")){
    			//replace the old information with the modifying information
        		choosingWaypoint.setName(modName);
        		choosingWaypoint.setLatitude(modLatitude);
        		choosingWaypoint.setLongitude(modLongitude);
        		sortingWaypointList(wayPointList);
    		}
    		//pressing save and activate
    		if(ModifyActivity.isAlsoActivateForMWP){
    			//change back to the main activity
				//passing activate way point name and position
				intentToMain.putExtra("actLatitude", modLatitude);//latitude
				intentToMain.putExtra("actLongitude", modLongitude);//longitude
				
				//back to main activity and send some parameters to the activity
				setResult(RESULT_OK, intentToMain);
				
				finish();
    		}//end if for pressing save and activate
        }
        
	}
	@Override
	  protected void onResume() {
	    super.onResume();
	    Log.i("Resume the program", "=======================RESUME+++++++++++++++++++++");
	    sortingWaypointList(wayPointList);
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
			switch (item.getItemId()) {
			case R.id.navigation_setting:
				//back to WayPoint activity and send some parameters to the activity
				setResult(RESULT_OK, intentToMain);
				finish();
				break;
			default:
				break;
			}
			return false;
		}

}
