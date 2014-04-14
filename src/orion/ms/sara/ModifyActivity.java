package orion.ms.sara;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ModifyActivity extends Activity {
	//variables declaration
	
			//string for each attribute of the modifying way point
			private String modName = "";
			private String modLatitude = "";
			private String modLongitude = "";
			
			//TextView
			private TextView modNameText =null;
			private TextView modLatitudeText =null;
			private TextView modLongitudeText =null;
			
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
			private LocationListener ll = null;

			//old name and position
			private String oldName = "";
			private String oldLatitude = "";
			private String oldLongitude = "";
			
			//current position
			private String currentLatitude = "";
			private String currentLongitude = "";
			
			//dialog for GPS signal if is disable
			private AlertDialog.Builder gpsDisDialog = null; 
			
			//Intent
			private Intent intentToWayPoint;
			private Intent intentFromWayPointAct;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify);

		//OnInitListener Creation
		OnInitListener onInitListener = new OnInitListener() {
			@Override
			public void onInit(int status) {
			}
		};
		
	    // textToSpeech creation
		tts = new TextToSpeech(this, onInitListener);
		tts.setSpeechRate((float) 1.5);
		
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
				//set each EditText default
				latitudeBox.setText(currentLatitude);
				longitudeBox.setText(currentLongitude);
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
			
		//TextView
		modNameText = (TextView) findViewById(R.id.textView1);
		modNameText.setContentDescription("mod waypoint name is");
		modLatitudeText = (TextView) findViewById(R.id.textView2);
		modLatitudeText.setContentDescription("mod waypoint latitude is");
		modLongitudeText = (TextView) findViewById(R.id.textView3);
		modLongitudeText.setContentDescription("mod waypoint longitude is");
		
		//EditText
		nameBox = (EditText) findViewById(R.id.editText1);
		latitudeBox = (EditText) findViewById(R.id.editText2);
		longitudeBox = (EditText) findViewById(R.id.editText3);
		
		//intent creation
		intentFromWayPointAct = getIntent();
		intentToWayPoint = new Intent(ModifyActivity.this,WayPointActivity.class);
		
		//receiving old name
		oldName = intentFromWayPointAct.getStringExtra("modName");
		oldLatitude = intentFromWayPointAct.getStringExtra("modLatitude");
		oldLongitude = intentFromWayPointAct.getStringExtra("modLongitude");
		
		//dialog creation
		gpsDisDialog = new AlertDialog.Builder(this);
		
		
		
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
		
		//button
		//current location button
		currentLoButton = (Button) findViewById(R.id.button2);
		currentLoButton.setTextSize(30);
		currentLoButton.setOnClickListener(new OnClickListener() {
			//OnClick creation
			@Override
			public void onClick(View v) {
				//update location
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
				if(!currentLatitude.equals("")){
					//GPS enable
					//set each EditText with current position
					latitudeBox.setText(currentLatitude);
					longitudeBox.setText(currentLongitude);
				}
				else{
					//show GPS disable dialog
					gpsDisDialog.setTitle("GPS disable");
					gpsDisDialog.setPositiveButton("Dismiss", null);
					gpsDisDialog.show();
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
				Toast.makeText(ModifyActivity.this, "Show Map!!!!", Toast.LENGTH_SHORT).show();;
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
					
					//check if the filled name or the position (latitude and longitude) are already recorded
					if(!isRecorded(modName, modLatitude, modLongitude)){
						tts.speak("Please fill the new information or create a new waypoint", tts.QUEUE_ADD, null);
					}
					else{
						//sent the modifying waypoint information back to waypoint activity
														
						//notification
						tts.speak("New information of the waypoint is already modified", tts.QUEUE_ADD, null);
						Toast.makeText(ModifyActivity.this,"New information of the waypoint is already modified", Toast.LENGTH_SHORT);
						
						//change back to the waypoint activity
						//pass the parameters including name,latitude,longitude
						intentToWayPoint.putExtra("modName",modName);//name
						intentToWayPoint.putExtra("modLatitude", modLatitude);//latitude
						intentToWayPoint.putExtra("modLongitude", modLongitude);//longitude
						
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
					
					//check if the filled name or the position (latitude and longitude) are already recorded
					if(!isRecorded(modName, modLatitude, modLongitude)){
						tts.speak("Please fill the new information or create a new waypoint", tts.QUEUE_ADD, null);
					}
					else{
						//sent the modifying waypoint information back to waypoint activity
														
						//notification
						tts.speak("New information of the waypoint is already modified", tts.QUEUE_ADD, null);
						Toast.makeText(ModifyActivity.this,"New information of the waypoint is already modified", Toast.LENGTH_SHORT);
						
						//intent to way point
						//pass the parameters including name,latitude,longitude
						intentToWayPoint.putExtra("modName",modName);//name
						intentToWayPoint.putExtra("modLatitude", modLatitude);//latitude
						intentToWayPoint.putExtra("modLongitude", modLongitude);//longitude
						setResult(RESULT_OK, intentToWayPoint);
						
						//intent to navigation
						//pass the parameters including name,latitude,longitude
						//intentToNavigation.putExtra("modLatitude", modLatitude);//latitude
						//intentToNavigation.putExtra("modLongitude", modLongitude);//longitude
						//setResult(RESULT_OK,intentToNavigation);
						finish();
						
					}//end else in if-else	
				}//end if
			}//end onClick
		});//end setOnClick
		
	}//end of OnCreate
	
	//to check if the filled name or the position (latitude and longitude) are already recorded
		@SuppressLint("ShowToast")
		
		public boolean isRecorded(String n, String la, String lo){
			List<WP> wList = WayPointActivity.getWayPointList();
			for(int i = 1;i<wList.size();i++){
				if((wList.get(i).getLatitude().equalsIgnoreCase(la) && wList.get(i).getLongitude().equalsIgnoreCase(lo)) || (wList.get(i).getName().equalsIgnoreCase(n))){
					if(wList.get(i).getName().equalsIgnoreCase(n)){
						// same name
						Toast.makeText(ModifyActivity.this, "This name is in the list.", Toast.LENGTH_SHORT);
					}//end if
					if(wList.get(i).getLatitude().equalsIgnoreCase(la) && wList.get(i).getLongitude().equalsIgnoreCase(lo)){
						//same position
						Toast.makeText(ModifyActivity.this, "This position is in the list.", Toast.LENGTH_SHORT);
					}//end if
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
	    //lm.removeUpdates(ll);
	  }
	  
	  @Override
	  protected void onStop() {
	    super.onStop();
		tts.shutdown();
	  }
	  
		@Override
		protected void onDestroy() {
			super.onDestroy();
			//lm.removeUpdates(ll);
			tts.shutdown();
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.modify, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.waypoint_setting:
			//pass the parameters including name,latitude,longitude
			intentToWayPoint.putExtra("modName","");//name
			intentToWayPoint.putExtra("modLatitude", "");//latitude
			intentToWayPoint.putExtra("modLongitude", "");//longitude
			
			setResult(RESULT_OK, intentToWayPoint);
			finish();
			break;
		default:
			break;
		}
		return false;
	}
}
