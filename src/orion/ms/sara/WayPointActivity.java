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
		private Button newWay = null;
		private Spinner way = null;	
				
		private TextToSpeech tts = null;
		private LocationManager lm = null;
		
		//a list of many way points sorted by proximity
		public static List<WP> wayPointList = new ArrayList<WP>();
		
		//Receiving parameters from new waypoint
		private String newName = "WP1";
		private String newLatitude = "";
		private String newLongitude = "";
		
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
		
		//shared preferences
		public SharedPreferences settings;
		public SharedPreferences.Editor editor;
		
		//intent
		private Intent intentToMain;
		private Intent intentToNewWayPoint;
		private Intent intentToModify;
		
		//selected way point item number
		private int selectedItem = 0;
		
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
			tts.setSpeechRate(GeneralSettingActivity.speechRate);
			
			//location manager creation
	        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.ll);
			
			//alert dialog creation
			choosingDialog = new AlertDialog.Builder(this);
			deletingDialog = new AlertDialog.Builder(this);
			
			//Intent creation
			intentToMain = new Intent(WayPointActivity.this,MainActivity.class);
			intentToNewWayPoint = new Intent(WayPointActivity.this,NewWayPointActivity.class);
			intentToModify = new Intent(WayPointActivity.this,ModifyActivity.class);

			//get selected item from main
			Intent intentFromMain = getIntent();
			selectedItem = intentFromMain.getIntExtra("actItem", 0);
			
			//sort the list
			if(MyLocationListener.currentLatitude.equals("") && selectedItem==0){
				//GPS unavailable
	        	AlertDialog.Builder dialog = new AlertDialog.Builder(this);
	        	dialog.setTitle("GPS is unavailable Waypoint list is not sort Please wait");
	        	dialog.setNeutralButton("OK", null);
	        	dialog.show();
	        	//maybe do this soon :)
	        	/*ProgressDialog progdialog = new ProgressDialog(WayPointActivity.this);
	        	progdialog.setMessage("GPS is unavailable. Please wait...");
	        	progdialog.setIndeterminate(!MyLocationListener.currentLatitude.equals(""));
	        	progdialog.setCancelable(true);
	        	progdialog.show();*/
	        }
			else{
				//GPS available
				sortingWaypointList(wayPointList);
				Log.i("from on create", "sort from on create");
			}
			
			//spinner set up
			way = (Spinner) findViewById(R.id.spinner1);
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
	                					selectedItem = i;
	                				}
	                				//choosing way point
	        	      				choosingWaypoint = wayPointList.get(selectedItem);
	        	      				
	        	      				//dialog creation
	        	      				choosingDialog.setTitle("You selected : "+choosingWaypoint.getName());
	                				choosingDialog.setIcon(android.R.drawable.presence_busy);
	                				//choosingDialog.setMessage("What do you want to do with "+choosingWaypoint.getName()+"?");
	                				
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
											intentToMain.putExtra("actName", choosingWaypoint.getName());//name
											intentToMain.putExtra("actLatitude", Double.parseDouble( choosingWaypoint.getLatitude()));//latitude
											intentToMain.putExtra("actLongitude", Double.parseDouble(choosingWaypoint.getLongitude()));//longitude
											intentToMain.putExtra("actItem", selectedItem);
											Log.i("selected", "i="+selectedItem+" name"+choosingWaypoint.getName());
											
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
	                				if(!choosingWaypoint.getName().equals("Please selected a waypoint") && i!=0){
	                					//show the choosing dialog if selected some way points from the list
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
								
								//sending default name for a new way point
								intentToNewWayPoint.putExtra("defaultNameFromWP", String.valueOf("WP"+(lastNumberForWaypoint+1)));
								//start NewWayPoint activity
								startActivityForResult(intentToNewWayPoint, NEW_WAYPOINT);
							}
						}//end of onClick
				    	
				    });//end of new way clickListener	
	
	}//end of OnCreate
	
	@Override
	  protected void onResume() {
	    super.onResume();
	    Log.i("Resume the program", "=======================RESUME+++++++++++++++++++++");
	    if(way.getChildCount()==0 && selectedItem==0){
	    	//set array adapter of the list into the spinner
			sortingWaypointList(wayPointList);
			way.setTop(selectedItem);
			Log.i("sort from on resume", "sort from on resume");
			Log.i("top resume list", ""+way.getTop());
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
			lm.removeUpdates(MainActivity.ll);
			tts.shutdown();
		}
		
	//to convert from array list of way point into name of the way point array list
	public static ArrayList<String> toNameArrayList(List<WP> wList){
		ArrayList<String> nameList = new ArrayList<String>();
		for(int i = 0;i<wList.size();i++){
			nameList.add(wList.get(i).getName());
		}
		return nameList;
	}
	
	//to convert from array list of way point into name of the way point array
	public static String[] nameArray(List<WP> wList){
		String[] arrayName = new String[wList.size()];
		for(int i = 0;i<wList.size();i++){
			arrayName[i] = wList.get(i).getName();
		}
		return arrayName;
	}
	//to convert from array list of way point into latitude of the way point array
	public static String[] latitudeArray(List<WP> wList){
		String[] arrayLatitude = new String[wList.size()];
		for(int i = 0;i<wList.size();i++){
			arrayLatitude[i] = wList.get(i).getLatitude();
		}
		return arrayLatitude;
	}
	//to convert from array list of way point into longitude of the way point array
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

	//adding new way point into the way point list
	public void addNewWPtoList(List<WP> wList,String n,String la,String lo){
		//Get the latest number after adding a new way point
		if(n.length()>2 && n.substring(0,2).equalsIgnoreCase("wp")){
			lastNumberForWaypoint = Integer.parseInt(n.substring(2));//substring "WP" name to get the number after that
		}
		else;
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
			//default value if no waypoint in the list yet
			WP first = new WP("Please selected a waypoint","","");
			
			//update location
	        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.ll);
	        //empty list
	        if(wList.size()==0){
				wList.add(first);
	        }
	        //GPS available -> sort by proximity
	        if(!MyLocationListener.currentLatitude.equals("") && wList.size()>1){
	        	//remove the default value before sorting
	        	int defaultItem = 0;
	        	for(int i = 0;i<wList.size();i++){
	        		if(wList.get(i).getName().equals("Please selected a waypoint"))
	        			defaultItem = i;
	        	}
	        	wList.remove(defaultItem);
	        	
	        	//Recalculating distance in the list
				for(int i = 0;i< wList.size();i++){
					tempWP = wList.get(i);
					//calculate new distance
					double tempLa = Double.parseDouble(tempWP.getLatitude());
					double tempLong = Double.parseDouble(tempWP.getLongitude());
					
					Location.distanceBetween(tempLa, tempLong, 
							Double.parseDouble(MyLocationListener.currentLatitude), Double.parseDouble(MyLocationListener.currentLongitude), tempResult);
					//set up the new distance from the current position into every way point in the list
					wList.get(i).setDistance(tempResult[0]);
				}
				Collections.sort(wList);//Sorting the list by proximity
				//add the default value back on the top of the list
				wList.add(0, first);
	        }//end if
	        
	        Log.i("sortttt", "in the sort");
	        //back from main activity after activating
	        if(selectedItem!=0){
	        	//get the selected waypoint and add it on the top of the list
	        	tempWP = wList.remove(selectedItem);
	        	wList.add(0,tempWP);
	        }
	        	        
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
				intentToMain.putExtra("actName", newName);//name
				intentToMain.putExtra("actLatitude", Double.parseDouble(newLatitude));//latitude
				intentToMain.putExtra("actLongitude", Double.parseDouble(newLongitude));//longitude
				//find the selected item location
				for(int i = 0;i<wayPointList.size();i++){
					if(wayPointList.get(i).getName().equals(newName))
						selectedItem = i;
				}
				intentToMain.putExtra("actItem", selectedItem);
				Log.i("selected", "i="+selectedItem+" name"+wayPointList.get(selectedItem).getName());
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
				intentToMain.putExtra("actName", modName);//name
				intentToMain.putExtra("actLatitude", Double.parseDouble(modLatitude));//latitude
				intentToMain.putExtra("actLongitude", Double.parseDouble(modLongitude));//longitude
				//find the selected item location
				for(int i = 0;i<wayPointList.size();i++){
					if(wayPointList.get(i).getName().equals(modName))
						selectedItem = i;
				}
				intentToMain.putExtra("actItem", selectedItem);
				Log.i("selected", "i="+selectedItem+" name"+wayPointList.get(selectedItem).getName());
				
				//back to main activity and send some parameters to the activity
				setResult(RESULT_OK, intentToMain);
				
				finish();
    		}//end if for pressing save and activate
        }
        
	}
	
	//load preferences
		private void loadPref(){
			//last number
			int lnum = settings.getInt(getString(R.string.save_last_num),0);
			lastNumberForWaypoint = lnum;
		}
		
		//save preferences
		private void savePref(int lnum, String[] name, String[] lati, String[] longi){
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
