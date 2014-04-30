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

public class WayActivity extends Activity {

	//components
	private Button newWay = null;
	private Spinner way = null;	
			
	private TextToSpeech tts = null;
	private LocationManager lm = null;
	
	//a list of many way points sorted by proximity
	public static List<Way> wayList = new ArrayList<Way>();
	
	//Receiving parameters from new way
	private String newName = "W1";
	private String newLatitude = "";
	private String newLongitude = "";
	
	//strings for each attribute of the modifying way point
	private String modName = "";
	private String modLatitude = "";
	private String modLongitude = "";
	
	//Generating a number for a new way's default name
	public static int lastNumberForWay = 0;
	
	//code for communication between activity
	protected int NEW_WAY = 10;
	protected int MODIFY_WAY = 11;
	
	//array adapter
	private ArrayAdapter<String> arrAd = null;
	
	//alert dialog 
	private AlertDialog.Builder choosingDialog = null;//after choosing the way point from the list
	private AlertDialog.Builder deletingDialog = null;//after choosing delete button from the list
	
	//choosing way point
	private Way choosingWay = null;
	
	//shared preferences
	public SharedPreferences settings;
	public SharedPreferences.Editor editor;
	
	//intent
	private Intent intentToMain;
	private Intent intentToNewWay;
	private Intent intentToModifyWay;
	
	//selected way point item number
	private int selectedItem = 0;
	private String selectedName = "No selected way";
	
@Override
public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_way_point);
		setTitle(R.string.title_activity_way_point);
		
		Log.i("WayAct", "---------------OnCreate----------------");
		
		//load preferences
		// Restore preferences
		this.settings = getSharedPreferences(MyLocationListener.PREFS_NAME, 0);
		this.editor = settings.edit();
		//loadPref();
		
		for(int i = 0;i<wayList.size();i++){
			Log.i("way list", "item "+i+": "+wayList.get(i).getName());
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
		intentToMain = new Intent(WayActivity.this,MainActivity.class);
		intentToNewWay = new Intent(WayActivity.this,NewWayActivity.class);
		intentToModifyWay = new Intent(WayActivity.this,ModifyWayActivity.class);

		//get selected item from main
		/*Intent intentFromMain = getIntent();
		selectedName = intentFromMain.getStringExtra("actName");
		Log.i("selected item from onCreate",selectedName);
		*/
		
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
			//sortingWayList(wayList);
			Log.i("from on create", "sort from on create");
		}
		
		//testing way
		WP wp1 = new WP("wp1","44.3","-4.8");
		WP wp2 = new WP("wp2","43","-4.5");
		WP wp3 = new WP("wp3","40","-4.7");
		wayList.add(new Way("w1",wp1,wp2));
		wayList.add(new Way("w2",wp2,wp3));
		//spinner set up
		//set array adapter of the list into the spinner
		way = (Spinner) findViewById(R.id.spinner1);
		arrAd = new ArrayAdapter<String>(WayActivity.this,
								android.R.layout.simple_spinner_item, 
								toNameArrayList(wayList));
		arrAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);      
		way.setAdapter(arrAd);
		/*way = (Spinner) findViewById(R.id.spinner1);
		way.setContentDescription("Choose the way in ");
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
        	      				choosingWay = wayList.get(selectedItem);
        	      				
        	      				//dialog creation
        	      				choosingDialog.setTitle("You selected : "+choosingWay.getName());
                				choosingDialog.setIcon(android.R.drawable.presence_busy);
                				
                				//setOnClickListener
                	
                				//activate button OnClickListener
                				choosingDialog.setNegativeButton(R.string.activate, new OnClickListener(){
                					//activate button OnClickListener creation
									@Override
									public void onClick(DialogInterface arg0, int arg1) {
		                				//notify
										Toast.makeText(WayActivity.this,"Activate",Toast.LENGTH_SHORT).show();
										tts.speak("Activate", tts.QUEUE_FLUSH, null);

		                				//change back to the main activity
										//passing activate way point name and position
										intentToMain.putExtra("actName", choosingWay.getName());//name
										intentToMain.putExtra("actLatitude", Double.parseDouble( choosingWay.getLatitude()));//latitude
										intentToMain.putExtra("actLongitude", Double.parseDouble(choosingWay.getLongitude()));//longitude
										Log.i("selected", "name : "+choosingWay.getName());
										
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
										Toast.makeText(WayActivity.this,"Modify",Toast.LENGTH_SHORT).show();
										tts.speak("Modify", tts.QUEUE_FLUSH, null);
										
										//change to the "Modify" activity
										//passing modifying way point name and position
										intentToModifyWay.putExtra("modName", choosingWay.getName());//name
										intentToModifyWay.putExtra("modLatitude", choosingWay.getLatitude());//latitude
										intentToModifyWay.putExtra("modLongitude", choosingWay.getLongitude());//longitude
										
										//start NewWayPoint activity
										startActivityForResult(intentToModifyWay, MODIFY_WAY);
									}//end of onClick
                					
                				});//end modify button
                				
                				//delete button OnClickListener
                				choosingDialog.setPositiveButton(R.string.delete, new OnClickListener(){
                					//delete button OnClickListener creation
									@Override
									public void onClick(DialogInterface dialog,int which) {
		                				//notify
										Toast.makeText(WayActivity.this,R.string.delete,Toast.LENGTH_SHORT).show();
										tts.speak("Delete", tts.QUEUE_FLUSH, null);
										
										//dialog creation
										deletingDialog.setTitle("Are you sure deleting "+choosingWay.getName()+"?");
										deletingDialog.setIcon(android.R.drawable.presence_busy);
										tts.speak("Are you sure deleting "+choosingWay.getName()+"?", tts.QUEUE_FLUSH, null);

										//button
										deletingDialog.setPositiveButton(R.string.cancel, null);
										deletingDialog.setNegativeButton(R.string.sure, new OnClickListener() {
											//OnClick listener for delete button
											@Override
											public void onClick(DialogInterface dialog, int which) {
												//deleteWPfromList(wayList,choosingWay);
											}
										});
										//show the deleting dialog
										deletingDialog.show();
									}
                				});//end delete button	
                				if(!choosingWay.getName().equals("No selected way") && i!=0){
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
      				Toast.makeText(WayActivity.this,"You selected Empty",Toast.LENGTH_SHORT).show();
      				tts.speak("Your Selected : nothing", tts.QUEUE_FLUSH, null);
				} 
				
			

        });*/
		
		//"New Way" button
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
							tts.speak("create a new way", tts.QUEUE_FLUSH, null);
				
							//sending default name for a new way
							intentToNewWay.putExtra("defaultNameFromWay", String.valueOf("Way"+(lastNumberForWay+1)));
							//start NewWay activity
							startActivityForResult(intentToNewWay, NEW_WAY);
						}
					}//end of onClick
			    	
			    });//end of new way clickListener	

	}//end of OnCreate
	
	@Override
	  protected void onResume() {
	    super.onResume();
	    Log.i("Resume way", "=======================RESUME+++++++++++++++++++++");
	    if(way.getChildCount()==0 && selectedItem==0){
	    	//set array adapter of the list into the spinner
			sortingWayList(wayList);
			//way.setTop(selectedItem);
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
			lm.removeUpdates(MainActivity.ll);
			tts.shutdown();
		}
		
	//to convert from array list of way point into name of the way point array list
	public static ArrayList<String> toNameArrayList(List<Way> wList){
		ArrayList<String> nameList = new ArrayList<String>();
		for(int i = 0;i<wList.size();i++){
			nameList.add(wList.get(i).getName());
		}
		return nameList;
	}
	
	//to convert from array list of way point into name of the way point array
	public static String[] nameArray(List<Way> wList){
		String[] arrayName = new String[wList.size()];
		for(int i = 0;i<wList.size();i++){
			arrayName[i] = wList.get(i).getName();
		}
		return arrayName;
	}
	//to convert from array list of way point into latitude of the way point array
	public static String[] latitudeArray(List<Way> wList){
		String[] arrayLatitude = new String[wList.size()];
		String[] tempWay;
		for(int i = 0;i<wList.size();i++){
			//
			i = i;
		}
		return arrayLatitude;
	}
	//to convert from array list of way point into longitude of the way point array
	public static String[] longitudeArray(List<Way> wList){
		String[] arrayLongitude = new String[wList.size()];
		for(int i = 0;i<wList.size();i++){
			i=i;
		}
		return arrayLongitude;
	}

	//adding new way point into the way point list
	public void addNewWPtoList(List<Way> wList,String n,String la,String lo){
		//Get the latest number after adding a new way point
		if(n.length()>1 && n.substring(0,1).equalsIgnoreCase("w")){
			lastNumberForWay = Integer.parseInt(n.substring(2));//substring "W" name to get the number after that
		}
		else;
		//Adding the new way point into the list
		/*wList.add(new WP(n,la,lo));//create new way point with assuming distance
		sortingWayList(wList);//sorting the list*/
	}
	
	//deleting the way point from the way point list
	public void deleteWPfromList(List<Way> wList, WP del){
		//Deleting the way point from the list
		wList.remove(del);
		selectedName = "No selected way";//reset to default
		sortingWayList(wList);//sorting the list
	}
	
	//sorting the way list by proximity calculated from current distance to the first waypoint
	private void sortingWayList(List<Way> wList){
		//temp way, distance and result
		Way tempWay = null;
		float[] tempResult = new float[3];
		//default value if no way in the list yet
		Way first = new Way("No selected way",null,null);
		
		//update location
	    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.ll);
	    
	  //remove the default value before sorting
		int defaultItem = -1;
		for(int i = 0;i<wList.size();i++){
			if(wList.get(i).getName().equals("No selected way")){
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
					tempWay = wList.get(i);
					//calculate new distance from the first waypoint of the way
					double tempLa = Double.parseDouble(tempWay.getFirstWP().getLatitude());
					double tempLong = Double.parseDouble(tempWay.getFirstWP().getLongitude());
					
					Location.distanceBetween(tempLa, tempLong, 
							Double.parseDouble(MyLocationListener.currentLatitude), Double.parseDouble(MyLocationListener.currentLongitude), tempResult);
					//set up the new distance from the current position into every way point in the list
					wList.get(i).setDistance(Double.parseDouble(tempResult[0]+""));
				}
				Collections.sort(wList);//Sorting the list by proximity of the first waypoint
	        }//end if
	        //add the default value back on the top of the list
	        wList.add(0, first);
		}//end if have default item
		else{
			//add the default value on the top of the list
	        wList.add(0, first);
		}
		
	    //back from main activity after activating
	    if(!selectedName.equals("No selected way")){
	    	//get the selected way and add it on the top of the list
	    	for(int i = 0;i<wList.size();i++){
	    		if(wList.get(i).getName().equals(selectedName)){
	    			tempWay = wList.remove(i);
	    			Log.i("selected item from sort", tempWay.getName());
	    		}
	    	}
	    	wList.add(0,tempWay);
	    	
	    }
	    	        
		//set array adapter of the list into the spinner
		way = (Spinner) findViewById(R.id.spinner1);
		arrAd = new ArrayAdapter<String>(WayActivity.this,
						android.R.layout.simple_spinner_item, 
						toNameArrayList(wList));
		arrAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);      
		way.setAdapter(arrAd);
		
		Log.i("top list", way.getTop()+"");
		
		//save the last number of default name value and attributes of way point in the list
		//savePref(lastNumberForWay,nameArray(wList),latitudeArray(wList),longitudeArray(wList));
		
	}//end sorting
		
		
	
	//Intent to handle receive parameters from NewWayPoint and Modify
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intentFromAnother){
	    super.onActivityResult(requestCode, resultCode, intentFromAnother);
		
		//get parameters from the NewWayPoint activity when create a new way point
	    if(requestCode == NEW_WAY && resultCode == RESULT_OK){
	    	newName = intentFromAnother.getStringExtra("newName");
			newLatitude = intentFromAnother.getStringExtra("newLatitude");
			newLongitude = intentFromAnother.getStringExtra("newLongitude");
			//not from pressing menu item
			if(!newName.equals("") && !newLatitude.equals("") && !newLongitude.equals(""))
				addNewWPtoList(wayList, newName, newLatitude, newLongitude);
			//pressing save and activate
			if(NewWayPointActivity.isAlsoActivateForNWP){
				//change back to the main activity
				//passing activate way point name and position
				intentToMain.putExtra("actName", newName);//name
				intentToMain.putExtra("actLatitude", Double.parseDouble(newLatitude));//latitude
				intentToMain.putExtra("actLongitude", Double.parseDouble(newLongitude));//longitude
				
				Log.i("selected", newName);
				//back to main activity and send some parameters to the activity
				setResult(RESULT_OK, intentToMain);
				
				finish();
			}//end if for pressing save and activate
	    }
	    
	  //get parameters from the Modify activity and replace the old information
	    if(requestCode == MODIFY_WAY && resultCode == RESULT_OK){
	    	modName = intentFromAnother.getStringExtra("modName");
			modLatitude = intentFromAnother.getStringExtra("modLatitude");
			modLongitude = intentFromAnother.getStringExtra("modLongitude");
			//not pressing from menu item
			if(!modName.equals("") && !modLatitude.equals("") && !modLongitude.equals("")){
				//replace the old information with the modifying information
	    		choosingWay.setName(modName);
	    		/*choosingWay.setLatitude(modLatitude);
	    		choosingWay.setLongitude(modLongitude);
	    		sortingWayList(wayList);*/
			}
			//pressing save and activate
			if(ModifyWPActivity.isAlsoActivateForMWP){
				//change back to the main activity
				//passing activate way point name and position
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

	//load preferences
	private void loadPref(){
		//last number
		int lnum = settings.getInt(getString(R.string.save_last_num),0);
		lastNumberForWay = lnum;
	}
	
	//save preferences
	private void savePref(int lnum, String[] name, String[] lati, String[] longi){
		//last number for new way 
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
		getMenuInflater().inflate(R.menu.way, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.navigation_setting:
			//back to main activity and send some parameters to the activity
			
			finish();
			break;
		default:
			break;
		}
		return false;
	}

}
