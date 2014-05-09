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

public class ModifyWPActivity extends Activity {
	//variables declaration
	
	private final int WP_MAP_MODIFY = 763;

	//string for each attribute of the modifying way point
	private String modName = "";
	private String modLatitude = "";
	private String modLongitude = "";
	private String modTres = "";
	
	//TextView
	private TextView modNameText =null;
	private TextView modLatitudeText =null;
	private TextView modLongitudeText =null;
	private TextView modTresholdText =null;
	
	//EditText
	private EditText nameBox = null;
	private EditText latitudeBox = null;
	private EditText longitudeBox = null;
	private EditText tresholdBox = null;
	
	//button
	private Button saveButton = null;
	private Button saveActButton = null;
	private Button currentLoButton = null;
	private Button mapLoButton = null;
	
	private TextToSpeech tts = null;
	private LocationManager lm = null;

	//old name and position
	private String oldName = "";
	private String oldLatitude = "";
	private String oldLongitude = "";
	private String oldTres = "";
	
	//current location
	private String currentLatitude = "";
	private String currentLongitude = "";
	
	//Intent
	private Intent intentToWayPoint;
	private Intent intentFromWayPointAct;
	
	//status for check if save and activate button is pressed (modify waypoint)
	public static boolean isAlsoActivateForMWP = false;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_wp);

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
			
		//TextView
		modNameText = (TextView) findViewById(R.id.textView1);
		modNameText.setContentDescription("mod waypoint name is");
		modLatitudeText = (TextView) findViewById(R.id.textView2);
		modLatitudeText.setContentDescription("mod waypoint latitude is");
		modLongitudeText = (TextView) findViewById(R.id.textView3);
		modLongitudeText.setContentDescription("mod waypoint longitude is");
		modTresholdText = (TextView) findViewById(R.id.textView4);
		modTresholdText.setContentDescription("mod waypoint treshold is");
		
		//EditText
		nameBox = (EditText) findViewById(R.id.editText1);
		latitudeBox = (EditText) findViewById(R.id.editText2);
		longitudeBox = (EditText) findViewById(R.id.editText3);
		tresholdBox = (EditText) findViewById(R.id.editText4);
		
		//intent creation
		intentFromWayPointAct = getIntent();
		intentToWayPoint = new Intent(ModifyWPActivity.this,WayPointActivity.class);
		
		//receiving old name
		oldName = intentFromWayPointAct.getStringExtra("modName");
		oldLatitude = intentFromWayPointAct.getStringExtra("modLatitude");
		oldLongitude = intentFromWayPointAct.getStringExtra("modLongitude");
		oldTres = intentFromWayPointAct.getStringExtra("modTres");
		
		//set default modifying name and position as the old ones
		modName = oldName;
		modLatitude = oldLatitude;
		modLongitude = oldLongitude;
		modTres = oldTres;
		
		//nameBox
		//set default name
		nameBox.setText(oldName);
		nameBox.setSelectAllOnFocus(true);
		//latitudeBox
		latitudeBox.setText(oldLatitude);
		latitudeBox.setSelectAllOnFocus(true);	
		//latitudeBox
		longitudeBox.setText(oldLongitude);
		longitudeBox.setSelectAllOnFocus(true);
		//tresholdBox
		tresholdBox.setText(oldTres);
		tresholdBox.setSelectAllOnFocus(true);
		
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
					AlertDialog.Builder dialog = new AlertDialog.Builder(ModifyWPActivity.this);
					dialog.setTitle("GPS is unavailable,please wait.");
					dialog.setNeutralButton("OK", null);
					dialog.show();
				}
				else{
					//GPS available
					if(!latitudeBox.getText().equals(currentLatitude) || !longitudeBox.getText().equals(currentLongitude)){
						//if the location change
						AlertDialog.Builder dialog = new AlertDialog.Builder(ModifyWPActivity.this);
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
				}
			}
		});
		
		//map location button
		mapLoButton = (Button) findViewById(R.id.button3);
		mapLoButton.setTextSize(30);
		mapLoButton.setOnClickListener(new OnClickListener() {
			//OnClick creation
			@SuppressLint("ShowToast")
			@Override
			public void onClick(View v) {
				Intent intentToWPMap = new Intent(ModifyWPActivity.this, WaypointMapActivity.class);
				intentToWPMap.putExtra("ifMod", true);
				intentToWPMap.putExtra("oldLatitude", latitudeBox.getText().toString());
				intentToWPMap.putExtra("oldLongitude", longitudeBox.getText().toString());
				intentToWPMap.putExtra("index", intentFromWayPointAct.getIntExtra("index", -1));
				startActivityForResult(intentToWPMap, WP_MAP_MODIFY);
			}
		});

		//"save" button
		saveButton = (Button) findViewById(R.id.button1);
		saveButton.setTextSize(30);
		//setOnClickedListener
		saveButton.setOnClickListener(new OnClickListener() {
			//OnClickedListener creation
			@SuppressLint("ShowToast")
			@SuppressWarnings("static-access")
			@Override
			public void onClick(View v) {
				if(v==saveButton){
					//get the modifying waypoint's new name, latitude or longitude from the EditText
					modName = nameBox.getText().toString();
					modLatitude = latitudeBox.getText().toString();
					modLongitude = longitudeBox.getText().toString();
					modTres = tresholdBox.getText().toString();
					
					//check if the filled name or the position (latitude and longitude) are already recorded
					if(!isRecorded(modName, modLatitude, modLongitude)){
						tts.speak("Please fill the new information or create a new waypoint", tts.QUEUE_ADD, null);
					}
					else{
						//sent the modifying waypoint information back to waypoint activity
														
						//notification
						tts.speak("New information of the waypoint is already modified", tts.QUEUE_ADD, null);
						Toast.makeText(ModifyWPActivity.this,"New information of the waypoint is already modified", Toast.LENGTH_SHORT);
						
						//change back to the waypoint activity
						//pass the parameters including name,latitude,longitude
						intentToWayPoint.putExtra("modName",modName);//name
						intentToWayPoint.putExtra("modLatitude", modLatitude);//latitude
						intentToWayPoint.putExtra("modLongitude", modLongitude);//longitude
						intentToWayPoint.putExtra("modTres", modTres);//treshold
						isAlsoActivateForMWP = false;//change status
						
						//back to WayPoint activity and send some parameters to the activity
						setResult(RESULT_OK, intentToWayPoint);
						finish();
						
					}//end else in if-else
				}//end if	
			}//end onClick
		});
		
		//save and activate button
		saveActButton = (Button) findViewById(R.id.button4);
		saveActButton.setTextSize(30);
		saveActButton.setOnClickListener(new OnClickListener() {
			//onClick creation
			@SuppressWarnings("static-access")
			@SuppressLint("ShowToast")
			@Override
			public void onClick(View v) {
				if(v==saveActButton){
					//get the modifying waypoint's new name, latitude or longitude from the EditText
					modName = nameBox.getText().toString();
					modLatitude = latitudeBox.getText().toString();
					modLongitude = longitudeBox.getText().toString();
					modTres = tresholdBox.getText().toString();
					
					//check if the filled name or the position (latitude and longitude) are already recorded
					if(!isRecorded(modName, modLatitude, modLongitude)){
						tts.speak("Please fill the new information or create a new waypoint", tts.QUEUE_ADD, null);
					}
					else{
						//sent the modifying waypoint information back to waypoint activity
														
						//notification
						tts.speak("New information of the waypoint is already modified", tts.QUEUE_ADD, null);
						Toast.makeText(ModifyWPActivity.this,"New information of the waypoint is already modified", Toast.LENGTH_SHORT);
						
						//intent to way point
						//pass the parameters including name,latitude,longitude
						intentToWayPoint.putExtra("modName",modName);//name
						intentToWayPoint.putExtra("modLatitude", modLatitude);//latitude
						intentToWayPoint.putExtra("modLongitude", modLongitude);//longitude
						intentToWayPoint.putExtra("modTres", modTres);//treshold
						isAlsoActivateForMWP = true;//status change
						
						setResult(RESULT_OK, intentToWayPoint);
						finish();
						
					}//end else in if-else	
				}//end if
			}//end onClick
		});//end setOnClick
		
	}//end of OnCreate
	
	//to check if the filled name or the position (latitude and longitude) are already recorded
		@SuppressLint("ShowToast")
		
		public static boolean isRecorded(String n, String la, String lo){
			List<WP> wList = WayPointActivity.getWayPointList();
			for(int i = 1;i<wList.size();i++){
				if((wList.get(i).getLatitude().equalsIgnoreCase(la) && wList.get(i).getLongitude().equalsIgnoreCase(lo)) 
						|| (wList.get(i).getName().equalsIgnoreCase(n))){
					return true;
				}
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
	  }
	  
		@Override
		protected void onDestroy() {
			super.onDestroy();
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.modify_wp, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.waypoint_setting:
			//get the modifying waypoint's new name, latitude or longitude from the EditText
			modName = nameBox.getText().toString();
			modLatitude = latitudeBox.getText().toString();
			modLongitude = longitudeBox.getText().toString();
			modTres = tresholdBox.getText().toString();
			
			//check if some values change without saving
			if(!oldName.equals(modName) || !oldLatitude.equals(modLatitude) || !oldLongitude.equals(modLongitude)){
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setTitle("Some values change, do you want to save?");
				dialog.setNegativeButton("Yes", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						//pass the parameters including name,latitude,longitude
						intentToWayPoint.putExtra("modName",modName);//name
						intentToWayPoint.putExtra("modLatitude", modLatitude);//latitude
						intentToWayPoint.putExtra("modLongitude", modLongitude);//longitude
						intentToWayPoint.putExtra("modTres", modTres);//treshold
						isAlsoActivateForMWP = true;//status change
						
						setResult(RESULT_OK, intentToWayPoint);
						finish();
					}
					
				});
				dialog.setNeutralButton("No", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						//don't save use the old name and position
						//pass the parameters including name,latitude,longitude
						intentToWayPoint.putExtra("modName",oldName);//name
						intentToWayPoint.putExtra("modLatitude", oldLatitude);//latitude
						intentToWayPoint.putExtra("modLongitude", oldLongitude);//longitude
						intentToWayPoint.putExtra("modTres", modTres);//treshold
						isAlsoActivateForMWP = false;//change status 
						setResult(RESULT_OK, intentToWayPoint);
						finish();
					}
				});
				dialog.show();
			}
			else{
				//pass the parameters including name,latitude,longitude
				intentToWayPoint.putExtra("modName","");//name
				intentToWayPoint.putExtra("modLatitude", "");//latitude
				intentToWayPoint.putExtra("modLongitude", "");//longitude
				intentToWayPoint.putExtra("modTres", "");//treshold
				isAlsoActivateForMWP = false;//change status 
				setResult(RESULT_OK, intentToWayPoint);
				finish();
			}
			
			break;
		default:
			break;
		}
		return false;
	}
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==WP_MAP_MODIFY && resultCode == RESULT_OK){
        	
        	//receive latitude and longitude from the map
        	String latitudeFromMap = data.getStringExtra("newLatitude");
        	String longitudeFromMap = data.getStringExtra("newLongitude");
        	
        	//set the receiving location to the editText
        	latitudeBox.setText(latitudeFromMap);
        	longitudeBox.setText(longitudeFromMap);
        	
        	Log.i(latitudeFromMap, longitudeFromMap);
        }
        
	}
}
