package orion.ms.sara;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class NewWayPointActivity extends Activity {
		//variables declaration
		//TextView
		private TextView newNameText =null;
		private TextView newLatitudeText =null;
		private TextView newLongitudeText =null;
	
		//string for each attribute of the new waypoint
		private String name = "";
		private String latitude = "";
		private String longitude = "";
		
		//EditText
		private EditText nameBox = null;
		private EditText latitudeBox = null;
		private EditText longitudeBox = null;
		
		//button
		private Button saveButton = null;
		private Button saveActButton = null;
		private Button currentLoButton = null;
		private Button mapLoButton = null;
		
		private TextToSpeech tts = null;
		private LocationManager lm = null;
		
		//default name and current position
		private String defaultName = "";
		private String currentLatitude = "";
		private String currentLongitude = "";
	
		//Intent
		private Intent intentToWayPoint;
		private Intent intentFromWayPointAct;
		private Intent intentToWPMap;
		
		//request code
		private final int WP_MAP = 1;

		//status for check if save and activate button is pressed (new waypoint)
		public static boolean isAlsoActivateForNWP = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_way_point);
		
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
				
				//TextView
				newNameText = (TextView) findViewById(R.id.textView1);
				newNameText.setContentDescription("new waypoint name is");
				newLatitudeText = (TextView) findViewById(R.id.textView2);
				newLatitudeText.setContentDescription("new waypoint latitude is");
				newLongitudeText = (TextView) findViewById(R.id.textView3);
				newLongitudeText.setContentDescription("new waypoint longitude is");
				
				//EditText and description
				nameBox = (EditText) findViewById(R.id.editText1);
				latitudeBox = (EditText) findViewById(R.id.editText2);
				longitudeBox = (EditText) findViewById(R.id.editText3);
				
				//intent creation
				intentFromWayPointAct = getIntent();
				intentToWayPoint = new Intent(NewWayPointActivity.this,WayPointActivity.class);
				intentToWPMap = new Intent(NewWayPointActivity.this,WaypointMapActivity.class);
				
				//receiving default name
				defaultName = intentFromWayPointAct.getStringExtra("defaultNameFromWP");
				
				//nameBox
				//set default name
				nameBox.setText(defaultName);
				nameBox.setSelectAllOnFocus(true);
				//latitudeBox
				latitudeBox.setSelectAllOnFocus(true);	
				//latitudeBox
				longitudeBox.setSelectAllOnFocus(true);
				
				//button
				//current location button
				currentLoButton = (Button) findViewById(R.id.button2);
				currentLoButton.setTextSize(30);
				currentLoButton.setOnClickListener(new OnClickListener() {
					//OnClick creation
					@Override
					public void onClick(View v) {
						//update location
				        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.ll);
				        currentLatitude = MyLocationListener.currentLatitude;
						currentLongitude = MyLocationListener.currentLongitude;
						if(currentLatitude.equals("")){
							//GPS unavailable
							AlertDialog.Builder dialog = new AlertDialog.Builder(NewWayPointActivity.this);
							dialog.setTitle("GPS is unavailable,please wait.");
							dialog.setNeutralButton("OK", null);
							dialog.show();
						}
						else{
							//GPS available
							if(!latitudeBox.getText().equals(currentLatitude) || !longitudeBox.getText().equals(currentLongitude)){
								//if the location change
								AlertDialog.Builder dialog = new AlertDialog.Builder(NewWayPointActivity.this);
								dialog.setTitle("Are you sure changing to the current position?");
								dialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										//if the user want to change
										//set each EditText with current position
										latitudeBox.setText(currentLatitude);
										longitudeBox.setText(currentLongitude);
									}
								});//end onClick
								dialog.setPositiveButton("Cancel", null);//don't want to change
								dialog.show();
							}//end if
						}//end else
					}//end onClick
				});//end setOnClick
				
				//map location button
				mapLoButton = (Button) findViewById(R.id.button3);
				mapLoButton.setTextSize(30);
				mapLoButton.setOnClickListener(new OnClickListener() {
					//OnClick creation
					@Override
					public void onClick(View v) {
						//send the location in the editText and start the new activity
						String latitude = latitudeBox.getText().toString();
						String longitude = longitudeBox.getText().toString();
						if(!latitude.equals("") || !longitude.equals("")){
							intentToWPMap.putExtra("ifMod", true);
							intentToWPMap.putExtra("oldLatitude", latitudeBox.getText().toString());
							intentToWPMap.putExtra("oldLongitude", longitudeBox.getText().toString());
							startActivityForResult(intentToWPMap, WP_MAP);
							Log.i("", "put" + longitudeBox.getText().toString());
						}
						else {
							intentToWPMap.putExtra("ifMod", false);
							startActivityForResult(intentToWPMap, WP_MAP);
							Log.i("", "notput");
						}
					}
				});
				
				//"save" button
				saveButton = (Button) findViewById(R.id.button1);
				saveButton.setTextSize(30);
				//setOnClickedListener
				saveButton.setOnClickListener(new OnClickListener() {
					//OnClickedListener creation
					@SuppressWarnings("static-access")
					@Override
					public void onClick(View v) {
						if(v==saveButton){
							//get the new waypoint's name, latitude and longitude from the EditText
							name = nameBox.getText().toString();
							latitude = latitudeBox.getText().toString();
							longitude = longitudeBox.getText().toString();
							
							//check if the filled name or the position (latitude and longitude) are already recorded
							if(isRecorded(name, latitude, longitude)){
								tts.speak("Please fill the new information", tts.QUEUE_ADD, null);
							}
							else{
								if(latitude.isEmpty() || longitude.isEmpty() || name.isEmpty()){
									//prevent unfilled text box(es)
									tts.speak("Please fill all information before saving", tts.QUEUE_ADD, null);
								}
								else{
									//sent the new waypoint information back to waypoint activity
									
									//notification
									tts.speak("the new waypoint already saved", tts.QUEUE_ADD, null);
									Toast.makeText(NewWayPointActivity.this,"new waypoint already saved", Toast.LENGTH_SHORT);
									
									//change back to the waypoint activity
									//pass the parameters including name,latitude,longitude
									intentToWayPoint.putExtra("newName",name);//name
									intentToWayPoint.putExtra("newLatitude", latitude);//latitude
									intentToWayPoint.putExtra("newLongitude", longitude);//longitude
									isAlsoActivateForNWP = false;//change status

									//back to WayPoint activity and send some parameters to the activity
									setResult(RESULT_OK, intentToWayPoint);
									finish();
									
								}//end else in if-else
								
							}//end else	
						}	
					}//end onClick
				});
				
				//save and activate button
				saveActButton = (Button) findViewById(R.id.button4);
				saveActButton.setTextSize(30);
				saveActButton.setOnClickListener(new OnClickListener() {
					//onClick creation
					@SuppressWarnings("static-access")
					@Override
					public void onClick(View v) {
						if(v==saveActButton){
							//get the new waypoint's name, latitude and longitude from the EditText
							name = nameBox.getText().toString();
							latitude = latitudeBox.getText().toString();
							longitude = longitudeBox.getText().toString();
							
							//check if the filled name or the position (latitude and longitude) are already recorded
							if(isRecorded(name, latitude, longitude)){
								tts.speak("Please fill the new information", tts.QUEUE_ADD, null);
							}
							else{
								if(latitude.isEmpty() || longitude.isEmpty() || name.isEmpty()){
									//prevent unfilled text box(es)
									tts.speak("Please fill all information before saving", tts.QUEUE_ADD, null);
								}
								else{
									//sent the new waypoint information back to waypoint activity
									//intent to way point
									//pass the parameters including name,latitude,longitude
									intentToWayPoint.putExtra("newName",name);//name
									intentToWayPoint.putExtra("newLatitude", latitude);//latitude
									intentToWayPoint.putExtra("newLongitude", longitude);//longitude
									isAlsoActivateForNWP = true;//change the status
									
									//back to WayPoint activity and send some parameters to the activity
									setResult(RESULT_OK, intentToWayPoint);
									finish();
								}//end else in if-else
							}//end else						
						}//end if
					}//end onClick
				});//end setOnClick
				
	}//end of OnCreate

	//to check if the filled name or the position (latitude and longitude) are already recorded
	@SuppressWarnings("static-access")
	@SuppressLint("ShowToast")
	
	public boolean isRecorded(String n, String la, String lo){
		List<WP> wList = WayPointActivity.getWayPointList();
		for(int i = 1;i<wList.size();i++){
			if(wList.get(i).getName().equalsIgnoreCase(n)){
				// same name
				Toast.makeText(NewWayPointActivity.this, "This name is already recorded.", Toast.LENGTH_SHORT);
				tts.speak("This name is already recorded.", tts.QUEUE_FLUSH, null);
				return true;
			}//end if
			else if(wList.get(i).getLatitude().equalsIgnoreCase(la) && wList.get(i).getLongitude().equalsIgnoreCase(lo)){
				//same position
				Toast.makeText(NewWayPointActivity.this, "This position is already recorded.", Toast.LENGTH_SHORT);
				tts.speak("This position is already recorded.", tts.QUEUE_FLUSH, null);
				return true;
			}//end if
		}//end for
		return false;
	}//end isRecored
	
	@Override
	  protected void onResume() {
	    super.onResume();
	  }

	  @Override
	  protected void onPause() {
	    super.onPause();
	  }
	  
	  @Override
	  protected void onStop() {
	    super.onStop();
		tts.shutdown();
	  }
	  
	@Override
	protected void onDestroy() {
		super.onDestroy();
		lm.removeUpdates(MainActivity.ll);
		tts.shutdown();
	}
	
	//Intent to handle receive parameters from NewWayPoint and Modify
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==WP_MAP && resultCode == RESULT_OK){
        	//receive latitude and longitude from the map
        	String latitudeFromMap = data.getStringExtra("newLatitude");
        	String longitudeFromMap = data.getStringExtra("newLongitude");
        	Log.i(latitudeFromMap, longitudeFromMap);
        	//set the receiving location to the editText
        	latitudeBox.setText(latitudeFromMap);
        	longitudeBox.setText(longitudeFromMap);
        }
        
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_way_point, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.waypoint_setting:
			//get the waypoint's new name, latitude or longitude from the EditText
			name = nameBox.getText().toString();
			latitude = latitudeBox.getText().toString();
			longitude = longitudeBox.getText().toString();
			
			//check if some values change without saving
			if(!latitude.equals("") || !longitude.equals("")){
				final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setTitle("Some values change, do you want to save?");
				dialog.setNegativeButton("Yes", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						//pass the parameters including name,latitude,longitude
						intentToWayPoint.putExtra("newName",name);//name
						intentToWayPoint.putExtra("newLatitude", latitude);//latitude
						intentToWayPoint.putExtra("newLongitude", longitude);//longitude
						isAlsoActivateForNWP = false;//change status

						//back to WayPoint activity and send some parameters to the activity
						setResult(RESULT_OK, intentToWayPoint);
						finish();
					}
				});
				
				dialog.setNeutralButton("No", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						//don't save use the old name and position
						//pass the parameters including name,latitude,longitude
						intentToWayPoint.putExtra("newName","");//name
						intentToWayPoint.putExtra("newLatitude", "");//latitude
						intentToWayPoint.putExtra("newLongitude", "");//longitude
						setResult(RESULT_OK, intentToWayPoint);
						finish();
					}
				});
				dialog.show();
			}
			else{
				//pass the parameters including name,latitude,longitude
				intentToWayPoint.putExtra("newName","");//name
				intentToWayPoint.putExtra("newLatitude", "");//latitude
				intentToWayPoint.putExtra("newLongitude", "");//longitude
				setResult(RESULT_OK, intentToWayPoint);
				finish();
				break;
			}
		default:
			break;
		}
		return false;
	}

}

