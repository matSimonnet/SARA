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
		private Button newWaypoint = null;
		private Spinner waypoint = null;	
				
		private TextToSpeech tts = null;
		private LocationManager lm = null;
		
		//a list of many way points sorted by proximity
		public static List<WP> wayPointList = new ArrayList<WP>();
		
		//Receiving parameters from new waypoint
		private String newName = "WP1";
		private String newLatitude = "";
		private String newLongitude = "";
		
		//strings for each attribute of the modifying waypoint point
		private String modName = "";
		private String modLatitude = "";
		private String modLongitude = "";
		private String modTres = "";
		
		//Generating a number for a new waypoint's default name
		public static int lastNumberForWaypoint = 0;
		
		//code for communication between activity
		protected int NEW_WAYPOINT = 7;
		protected int MODIFY_WAYPOINT = 9;
		
		//array adapter
		private ArrayAdapter<String> arrAd = null;
		
		//alert dialog 
		private AlertDialog.Builder choosingDialog = null;//after choosing the waypoint point from the list
		private AlertDialog.Builder deletingDialog = null;//after choosing delete button from the list
		
		//choosing waypoint point
		private WP choosingWaypoint = null;
		
		//shared preferences
		public SharedPreferences settings;
		public SharedPreferences.Editor editor;
		
		//intent
		private Intent intentToMain;
		private Intent intentToNewWayPoint;
		private Intent intentToModifyWP;
		
		//selected waypoint point item number
		private int selectedItem = 0;
		private String selectedName = "Please selected a waypoint";
		
		private int treshold = 1;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			setContentView(R.layout.activity_way_point);
			setTitle(R.string.title_activity_way_point);
			
			Log.i("WaypointAct", "---------------OnCreate----------------");
			
			//load preferences
			// Restore preferences
			this.settings = getSharedPreferences(MyLocationListener.PREFS_NAME, 0);
			this.editor = settings.edit();
			loadPref();
			
			for(int i = 0;i<wayPointList.size();i++){
				Log.i("waypoint list", "item "+i+": "+wayPointList.get(i).getName());
			}
								
			//OnInitListener Creation
			OnInitListener onInitListener = new OnInitListener() {
				@Override
				public void onInit(int status) {
				}
			};
			
		    // textToSpeech creation
			tts = new TextToSpeech(this, onInitListener);
			
			//location manager creation
	        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.ll);
			
			//alert dialog creation
			choosingDialog = new AlertDialog.Builder(this);
			deletingDialog = new AlertDialog.Builder(this);
			
			//Intent creation
			intentToMain = new Intent(WayPointActivity.this,MainActivity.class);
			intentToNewWayPoint = new Intent(WayPointActivity.this,NewWayPointActivity.class);
			intentToModifyWP = new Intent(WayPointActivity.this,ModifyWPActivity.class);

			//get selected item from main
			Intent intentFromMain = getIntent();
			selectedName = intentFromMain.getStringExtra("actName");
			Log.i("selected item from onCreate",selectedName);
			
			//sort the list
			if(MyLocationListener.currentLatitude.equals("") && selectedItem==0){
				//GPS unavailable
	        	AlertDialog.Builder dialog = new AlertDialog.Builder(this);
	        	dialog.setTitle("GPS is unavailable the list is not sort Please wait");
	        	dialog.setNeutralButton(R.string.ok_button, null);
	        	dialog.show();
	        }
			else{
				//GPS available
				sortingWaypointList(wayPointList);
				Log.i("from on create", "sort from on create");
			}
			
			//spinner set up
			waypoint = (Spinner) findViewById(R.id.spinner1);
			waypoint.setContentDescription("Choose the waypoint in ");
			//setOnItemSelectedListener
			waypoint.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() { 
				//OnItemSelectedListener creation
	            @SuppressWarnings("static-access")
				public void onItemSelected(final AdapterView<?> adapterView, View view, int i, long l) { 
	      				try{
	                		switch(adapterView.getId()){
	                		case R.id.spinner1: 
	                				if(i!=0){
	                					selectedItem = i;
	                				}
	                				//choosing waypoint point
	        	      				choosingWaypoint = wayPointList.get(selectedItem);
	        	      				
	        	      				//dialog creation
	        	      				choosingDialog.setTitle("You selected : "+choosingWaypoint.getName());
	                				choosingDialog.setIcon(android.R.drawable.presence_busy);
	                				
	                				//setOnClickListener
	                	
	                				//activate button OnClickListener
	                				choosingDialog.setNegativeButton(R.string.activate, new OnClickListener(){
	                					//activate button OnClickListener creation
										@Override
										public void onClick(DialogInterface arg0, int arg1) {
			                				//notify
											Toast.makeText(WayPointActivity.this,R.string.activate,Toast.LENGTH_SHORT).show();
											tts.speak("Activate", tts.QUEUE_FLUSH, null);

			                				//change back to the main activity
											//passing activate waypoint point name and position
											intentToMain.putExtra("actName", choosingWaypoint.getName());//name
											intentToMain.putExtra("actLatitude", Double.parseDouble( choosingWaypoint.getLatitude()));//latitude
											intentToMain.putExtra("actLongitude", Double.parseDouble(choosingWaypoint.getLongitude()));//longitude
											Log.i("selected", "name : "+choosingWaypoint.getName());
											
											//back to WayPoint activity and send some parameters to the activity
											setResult(RESULT_OK, intentToMain);
											
											finish();
										}
	                				});//end activate button
	                				
	                				//modify button OnClickListener
	                				choosingDialog.setNeutralButton(R.string.modify, new OnClickListener(){
	                					//modify button OnClickListener creation
										@Override
										public void onClick(DialogInterface dialog,int which) {
											//notification
											Toast.makeText(WayPointActivity.this,R.string.modify,Toast.LENGTH_SHORT).show();
											tts.speak("Modify", tts.QUEUE_FLUSH, null);
											
											//change to the "Modify" activity
											//passing modifying way point point name and position
											intentToModifyWP.putExtra("modName", choosingWaypoint.getName());//name
											intentToModifyWP.putExtra("modLatitude", choosingWaypoint.getLatitude());//latitude
											intentToModifyWP.putExtra("modLongitude", choosingWaypoint.getLongitude());//longitude
											intentToModifyWP.putExtra("modTres", choosingWaypoint.getTreshold()+"");//treshold
											intentToModifyWP.putExtra("index", selectedItem);//longitude

											//start ModifyWP activity
											startActivityForResult(intentToModifyWP, MODIFY_WAYPOINT);
										}//end of onClick
	                					
	                				});//end modify button
	                				
	                				//delete button OnClickListener
	                				choosingDialog.setPositiveButton(R.string.delete, new OnClickListener(){
	                					//delete button OnClickListener creation
										@Override
										public void onClick(DialogInterface dialog,int which) {
			                				//notify
											Toast.makeText(WayPointActivity.this,R.string.delete,Toast.LENGTH_SHORT).show();
											tts.speak("Delete", tts.QUEUE_FLUSH, null);
											
											//dialog creation
											deletingDialog.setTitle("Are you sure deleting "+choosingWaypoint.getName()+"?");
											deletingDialog.setIcon(android.R.drawable.presence_busy);
											tts.speak("Are you sure deleting "+choosingWaypoint.getName()+"?", tts.QUEUE_FLUSH, null);

											//button
											deletingDialog.setPositiveButton(R.string.cancel, null);
											deletingDialog.setNegativeButton(R.string.sure, new OnClickListener() {
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
	                				if(!choosingWaypoint.getName().equals("Please selected a waypoint") && i!=0){
	                					//show the choosing dialog if selected some waypoint points from the list
	                					choosingDialog.show();
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
			
			//"New Waypoint" button
			//button creation
			newWaypoint = (Button) findViewById(R.id.button1);
					
			//setOnClickListener
			newWaypoint.setOnClickListener(new View.OnClickListener(){
						// OnClickListener creation			    
						@SuppressWarnings("static-access")
						@Override
						public void onClick(View v) {
							if(v==newWaypoint){
								//notification
								tts.speak("create a new waypoint", tts.QUEUE_FLUSH, null);
								
								//sending default name for a new waypoint point
								intentToNewWayPoint.putExtra("defaultNameFromWP", String.valueOf("WP"+(lastNumberForWaypoint+1)));
								//start NewWayPoint activity
								startActivityForResult(intentToNewWayPoint, NEW_WAYPOINT);
							}
						}//end of onClick
				    	
				    });//end of new waypoint clickListener	
	
	}//end of OnCreate
	
	@Override
	  protected void onResume() {
	    super.onResume();
	    Log.i("Resume the program", "=======================RESUME+++++++++++++++++++++");
	    if(waypoint.getChildCount()==0 && selectedItem==0){
	    	//set array adapter of the list into the spinner
			sortingWaypointList(wayPointList);
			waypoint.setTop(selectedItem);
			Log.i("sort from on resume", "sort from on resume");
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
		
	//Intent to handle receive parameters from NewWayPoint and Modify
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent intentFromAnother){
		    super.onActivityResult(requestCode, resultCode, intentFromAnother);
			
			//get parameters from the NewWayPoint activity when create a new waypoint point
		    if(requestCode == NEW_WAYPOINT && resultCode == RESULT_OK){
		    	newName = intentFromAnother.getStringExtra("newName");
				newLatitude = intentFromAnother.getStringExtra("newLatitude");
				newLongitude = intentFromAnother.getStringExtra("newLongitude");
				treshold = intentFromAnother.getIntExtra("treshold", 1);

				//not from pressing menu item
				if(!newName.equals("") && !newLatitude.equals("") && !newLongitude.equals(""))
					addNewWPtoList(wayPointList, newName, newLatitude, newLongitude);
				//pressing save and activate
				if(NewWayPointActivity.isAlsoActivateForNWP){
					//change back to the main activity
					//passing activate waypoint point name and position
					intentToMain.putExtra("actName", newName);//name
					intentToMain.putExtra("actLatitude", Double.parseDouble(newLatitude));//latitude
					intentToMain.putExtra("actLongitude", Double.parseDouble(newLongitude));//longitude
					intentToMain.putExtra("actTreshold", treshold);//longitude

					Log.i("selected", newName);
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
				modTres = intentFromAnother.getStringExtra("modTres");
				//not pressing from menu item
				if(!modName.equals("") && !modLatitude.equals("") && !modLongitude.equals("") && !modTres.equals("")){
					//replace the old information with the modifying information
		    		choosingWaypoint.setName(modName);
		    		choosingWaypoint.setLatitude(modLatitude);
		    		choosingWaypoint.setLongitude(modLongitude);
		    		choosingWaypoint.setTreshold(Integer.parseInt(modTres));
		    		sortingWaypointList(wayPointList);
				}
				//pressing save and activate
				if(ModifyWPActivity.isAlsoActivateForMWP){
					//change back to the main activity
					//passing activate waypoint point name and position
					intentToMain.putExtra("actName", modName);//name
					intentToMain.putExtra("actLatitude", Double.parseDouble(modLatitude));//latitude
					intentToMain.putExtra("actLongitude", Double.parseDouble(modLongitude));//longitude
					Log.i("selected", modName);
					
					//back to main activity and send some parameters to the activity
					setResult(RESULT_OK, intentToMain);
					
					finish();
				}//end if for pressing save and activate
		    }
		    
		}

	//to convert from array list of waypoint point into name of the waypoint point array list
	public static ArrayList<String> toNameArrayList(List<WP> wList){
		ArrayList<String> nameList = new ArrayList<String>();
		for(int i = 0;i<wList.size();i++){
			nameList.add(wList.get(i).getName());
		}
		return nameList;
	}
	//to convert from array list of waypoint point into name of the waypoint point array
	public static int[] tresholdArray(List<WP> wList){
		int[] arrayTreshold = new int[wList.size()];
		for(int i = 0;i<wList.size();i++){
			arrayTreshold[i] = wList.get(i).getTreshold();
		}
		return arrayTreshold;
	}
	
	//to convert from array list of waypoint point into name of the waypoint point array
	public static String[] nameArray(List<WP> wList){
		String[] arrayName = new String[wList.size()];
		for(int i = 0;i<wList.size();i++){
			arrayName[i] = wList.get(i).getName();
		}
		return arrayName;
	}
	//to convert from array list of waypoint point into latitude of the waypoint point array
	public static String[] latitudeArray(List<WP> wList){
		String[] arrayLatitude = new String[wList.size()];
		for(int i = 0;i<wList.size();i++){
			arrayLatitude[i] = wList.get(i).getLatitude();
		}
		return arrayLatitude;
	}
	//to convert from array list of waypoint point into longitude of the waypoint point array
	public static String[] longitudeArray(List<WP> wList){
		String[] arrayLongitude = new String[wList.size()];
		for(int i = 0;i<wList.size();i++){
			arrayLongitude[i] = wList.get(i).getLongitude();
		}
		return arrayLongitude;
	}
	
	public static List<WP> getWayPointList() {
		return wayPointList;
	}

	//adding new waypoint point into the waypoint point list
	public void addNewWPtoList(List<WP> wList,String n,String la,String lo){
		//Get the latest number after adding a new waypoint point
		if(n.length()>2 && n.substring(0,2).equalsIgnoreCase("wp")){
			lastNumberForWaypoint = Integer.parseInt(n.substring(2));//substring "WP" name to get the number after that
		}
		//Adding the new waypoint point into the list
		WP newWP = new WP(n,la,lo);
		newWP.setTreshold(treshold);
		wList.add(newWP);//create new waypoint point with assuming distance
		sortingWaypointList(wList);//sorting the list
	}
	
	//deleting the waypoint point from the waypoint point list
	public void deleteWPfromList(List<WP> wList, WP del){
		//Deleting the waypoint point from the list
		wList.remove(del);
		selectedName = "Please selected a waypoint";//reset to default
		sortingWaypointList(wList);//sorting the list
	}
	
	//sorting the waypoint point list by proximity calculated from current distance
	private void sortingWaypointList(List<WP> wList){
		//temp waypoint point, distance and result
		WP tempWP = null;
		float[] tempResult = new float[3];
		//default value if no waypoint in the list yet
		WP first = new WP("Please selected a waypoint","","");
		
		//update location
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.ll);
        
      //remove the default value before sorting
    	int defaultItem = -1;
    	for(int i = 0;i<wList.size();i++){
    		if(wList.get(i).getName().equals("Please selected a waypoint")){
    			defaultItem = i;
    		}
    	}
    	if(defaultItem!=-1){
    		//have the default
    		wList.remove(defaultItem);
	        //GPS available -> sort by proximity
	        if(!MyLocationListener.currentLatitude.equals("") && wList.size()>1){
	        	//Recalculating distance in the list
				for(int i = 0;i< wList.size();i++){
					tempWP = wList.get(i);
					Log.i("in sorting list", tempWP.getName());
					//calculate new distance
					double tempLa = Double.parseDouble(tempWP.getLatitude());
					double tempLong = Double.parseDouble(tempWP.getLongitude());
					
					Location.distanceBetween(tempLa, tempLong, 
							Double.parseDouble(MyLocationListener.currentLatitude), Double.parseDouble(MyLocationListener.currentLongitude), tempResult);
					//set up the new distance from the current position into every waypoint point in the list
					wList.get(i).setDistance(tempResult[0]);
				}
				Collections.sort(wList);//Sorting the list by proximity
	        }//end if
	        //add the default value back on the top of the list
	        wList.add(0, first);
    	}//end if have default item
    	else{
    		//add the default value on the top of the list
	        wList.add(0, first);
    	}
		
        //back from main activity after activating
        if(!selectedName.equals("Please selected a waypoint")){
        	//get the selected waypoint and add it on the top of the list
        	for(int i = 0;i<wList.size();i++){
        		if(wList.get(i).getName().equals(selectedName)){
        			tempWP = wList.remove(i);
        			Log.i("selected item from sort", tempWP.getName());
        		}
        	}
        	wList.add(0,tempWP);
        	
        }
        	        
		//set array adapter of the list into the spinner
		waypoint = (Spinner) findViewById(R.id.spinner1);
		arrAd = new ArrayAdapter<String>(WayPointActivity.this,
						android.R.layout.simple_spinner_item, 
						toNameArrayList(wList));
		arrAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);      
		waypoint.setAdapter(arrAd);
		
		Log.i("top list", waypoint.getTop()+"");
		
		//save the last number of default name value and attributes of waypoint point in the list
		savePref(lastNumberForWaypoint,nameArray(wList),latitudeArray(wList),longitudeArray(wList),tresholdArray(wList));
		
	}//end sorting
		
		
	
	//load preferences
	private void loadPref(){
		//last number
		int lnum = settings.getInt(getString(R.string.save_last_num),0);
		lastNumberForWaypoint = lnum;
	}
	
	//save preferences
	private void savePref(int lnum, String[] name, String[] lati, String[] longi, int[] treshold){
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
		
	    //treshold array
	    editor.putInt("tresholdArray" +"_size", treshold.length);  
	    for(int i=0;i<longi.length;i++)  
	        editor.putInt("tresholdArray" + "_" + i, treshold[i]);
		editor.commit();
		
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
			finish();
			break;
		default:
			break;
		}
		return false;
	}

}
