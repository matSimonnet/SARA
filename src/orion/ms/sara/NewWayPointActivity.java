package orion.ms.sara;

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
import android.widget.Toast;

@SuppressLint("ShowToast")
public class NewWayPointActivity extends Activity {
	//variables declaration
	
		//string for each attribute of the new waypoint
		private String name = "";
		private String latitude = "";
		private String longitude = "";
		
		//EditText
		private EditText nameBox = null;
		private EditText latitudeBox = null;
		private EditText longitudeBox = null;
		
		//save button
		private Button saveButton = null;
		
		private TextToSpeech tts = null;
		
		//receiving parameter arrays
		private String[] nameArray = null;
		private String[] latitudeArray = null;
		private String[] longitudeArray = null;	
		
		private LocationManager lm = null;	
		private LocationListener ll = null;

		//default name and current position
		private String defaultName = "";
		private String currentLatitude = "";
		private String currentLongitude = "";
		
		//dialog for GPS signal if is disable
		private AlertDialog.Builder gpsDisDialog = null; 
		
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
				
				//EditText and description
				nameBox = (EditText) findViewById(R.id.editText1);
				nameBox.setContentDescription("describes Name of the new waypoint");
				latitudeBox = (EditText) findViewById(R.id.editText2);
				latitudeBox.setContentDescription("describes Latitude of the new waypoint");
				longitudeBox = (EditText) findViewById(R.id.editText3);
				longitudeBox.setContentDescription("describes Longitude of the new waypoint");
				
				//receiving parameters from the waypoint activity when create a new waypoint
				Intent intentFromWayPointAct = getIntent();
				nameArray = intentFromWayPointAct.getStringArrayExtra("nameArrayFromWP");
				latitudeArray = intentFromWayPointAct.getStringArrayExtra("latitudeArrayFromWP");
				longitudeArray = intentFromWayPointAct.getStringArrayExtra("longitudeArrayFromWP");
				//receiving default name
				defaultName = intentFromWayPointAct.getStringExtra("defaultNameFromWP");
				
				//dialog creation
				gpsDisDialog = new AlertDialog.Builder(this);
				//setOnclickListener
				//nameBox
				nameBox.setOnClickListener(new OnClickListener() {
					//OnClick creation
					@Override
					public void onClick(View v) {
						//set default name
						nameBox.setText(defaultName);
						nameBox.setSelectAllOnFocus(true);
					}
				});
				//latitudeBox
				latitudeBox.setOnClickListener(new OnClickListener() {
					//OnClick creation
					@Override
					public void onClick(View v) {
						if(v==latitudeBox){
							latitudeBox.setSelectAllOnFocus(true);
							//update location
							lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
							if(!currentLatitude.equals("")){
								//set each EditText default
								latitudeBox.setText(currentLatitude);
								longitudeBox.setText(currentLongitude);
							}
							else{
								gpsDisDialog.setTitle("GPS disable");
								gpsDisDialog.setPositiveButton("Dismiss", null);
								gpsDisDialog.show();
							}
						}
					}
				});
				
				//latitudeBox
				longitudeBox.setOnClickListener(new OnClickListener() {
					//OnClick creation
					@Override
					public void onClick(View v) {
						if(v==longitudeBox){
							longitudeBox.setSelectAllOnFocus(true);
							//update location
							lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
							if(!currentLongitude.equals("")){
								//set each EditText default
								latitudeBox.setText(currentLatitude);
								longitudeBox.setText(currentLongitude);
							}
							else{
								gpsDisDialog.setTitle("GPS disable");
								gpsDisDialog.setPositiveButton("Dismiss", null);
								gpsDisDialog.show();
							}
						}
					}
				});
				
				//"save" button
				saveButton = (Button) findViewById(R.id.button1);
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
									Intent intentToWayPoint = new Intent(NewWayPointActivity.this,WayPointActivity.class);
									//pass the parameters including name,latitude,longitude
									intentToWayPoint.putExtra("newName",name);//name
									intentToWayPoint.putExtra("newLatitude", latitude);//latitude
									intentToWayPoint.putExtra("newLongitude", longitude);//longitude
									
									//back to WayPoint activity and send some parameters to the activity
									setResult(RESULT_OK, intentToWayPoint);
									finish();
									
								}//end else in if-else
								
							}//end else	
						}	
					}//end onClick
				});
	}//end of OnCreate

	//to check if the filled name or the position (latitude and longitude) are already recorded
	@SuppressWarnings("static-access")
	@SuppressLint("ShowToast")
	
	public boolean isRecorded(String n, String la, String lo){
		for(int i = 0;i<nameArray.length;i++){
			if(nameArray[i].equalsIgnoreCase(n)){
				// same name
				Toast.makeText(NewWayPointActivity.this, "This name is already recorded.", Toast.LENGTH_SHORT);
				tts.speak("This name is already recorded.", tts.QUEUE_FLUSH, null);
				return true;
			}//end if
			else if(latitudeArray[i].equalsIgnoreCase(la) && longitudeArray[i].equalsIgnoreCase(lo)){
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
	    //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
	  }

	  @Override
	  protected void onPause() {
	    super.onPause();
	    lm.removeUpdates(ll);
	  }
	  
	  @Override
	  protected void onStop() {
	    super.onStop();
		tts.shutdown();
	  }
	  
		@Override
		protected void onDestroy() {
			super.onDestroy();
			lm.removeUpdates(ll);
			tts.shutdown();
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
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
