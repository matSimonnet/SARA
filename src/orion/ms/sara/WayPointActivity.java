package orion.ms.sara;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
		//initial list selection value
		private boolean isSelected = false;
		
		//choosing waypoint
		private WP choosingWaypoint = null;
		
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
			choosingDialog = new AlertDialog.Builder(this);
			deletingDialog = new AlertDialog.Builder(this);
			
			//setOnItemSelectedListener
			way.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() { 
				//OnItemSelectedListener creation
	            @SuppressWarnings("static-access")
				public void onItemSelected(final AdapterView<?> adapterView, View view, int i, long l) { 
	      				try{
	                		switch(adapterView.getId()){
	                		case R.id.spinner1: 
	                			if(isSelected){
	                				//notify
	                				Toast.makeText(WayPointActivity.this,"You selected : "+toNameArrayList(wayPointList).get(i),Toast.LENGTH_SHORT).show();
	        	      				tts.speak("Your Selected : "+toNameArrayList(wayPointList).get(i), tts.QUEUE_FLUSH, null);
	                				//choosing waypoint
	        	      				choosingWaypoint = wayPointList.get(i);
	        	      				
	        	      				//dialog creation
	        	      				choosingDialog.setTitle("You selected : "+choosingWaypoint.getName());
	                				choosingDialog.setIcon(android.R.drawable.presence_busy);
	                				choosingDialog.setMessage("What do you want to do with "+choosingWaypoint.getName()+",activate, modify or delete?");
	                				
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
											intentToMain.putExtra("actLatitude", Double.parseDouble(choosingWaypoint.getLatitude()));//latitude
											intentToMain.putExtra("actLongitude", Double.parseDouble(choosingWaypoint.getLongitude()));//longitude
																					
											//back to WayPoint activity and send some parameters to the activity
											setResult(RESULT_OK, intentToMain);
											finish();
											
											
										}
	                				});
	                				
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
	                					
	                				});
	                				//delete button OnClickListener
	                				choosingDialog.setPositiveButton("Delete", new OnClickListener(){
	                					//delete button OnClickListener creation
										@Override
										public void onClick(DialogInterface dialog,int which) {
			                				//notify
											Toast.makeText(WayPointActivity.this,"Delete",Toast.LENGTH_SHORT).show();
											tts.speak("Delete", tts.QUEUE_FLUSH, null);
											
											//dialog creation
											deletingDialog.setTitle("Delete : "+choosingWaypoint.getName());
											deletingDialog.setIcon(android.R.drawable.presence_busy);
											deletingDialog.setMessage("Are you sure deleting "+choosingWaypoint.getName()+"?");
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
	                				});	                				
	                				//show the choosing dialog
	                				choosingDialog.show();
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
								startActivityForResult(intentToNewWayPoint, NEW_WAYPOINT);
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
			lastNum = Integer.parseInt(n.substring(n.lastIndexOf("t")+1));//substring "waypoint" name to get the number after that
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
	
	//deleting the waypoint from the waypoint list
	public void deleteWPfromList(List<WP> wList, WP del){
		//Deleting the waypoint from the list
		wList.remove(del);
		Collections.sort(wList);//Sorting the lisst by proximity
		
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
    		deleteWPfromList(wayPointList, choosingWaypoint);
    		addNewWPtoList(wayPointList, modName, modLatitude, modLongitude, 0.0, 0.0);
    		Log.i("Receive from modify", "Name "+modName+" La "+modLatitude+" lo "+modLongitude);
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
