package orion.ms.sara;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ModifyActivity extends Activity {
	//variables declaration
	
			//string for each attribute of the modifying waypoint
			private String modName = "";
			private String modLatitude = "";
			private String modLongitude = "";
			
			//TextView
			private TextView nameText = null;
			private TextView latitudeText = null;
			private TextView longitudeText = null;
			
			//EditText
			private EditText nameBox = null;
			private EditText latitudeBox = null;
			private EditText longitudeBox = null;
			
			//CheckBox
			private CheckBox currentPositionBox = null;
			
			//save button
			private Button saveButton = null;
			
			private TextToSpeech tts = null;
			
			//receiving parameter arrays
			private String[] nameArray = null;
			private String[] latitudeArray = null;
			@SuppressWarnings("unused")
			private String[] longitudeArray = null;	
			
			private LocationManager lm = null;	
			private LocationListener ll = null;

			//old name and position
			private String oldName = "";
			private String oldLatitude = "";
			private String oldLongitude = "";
			
			//current position
			private String currentLatitude = "";
			private String currentLongitude = "";
			
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
				Log.i("Latitude current","current la is "+currentLatitude);
				Log.i("Longitude current","current long is "+currentLongitude);
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
		nameText = (TextView) findViewById(R.id.textView2);
		nameText.setContentDescription("Name of the modifying waypoint in editbox below");
		latitudeText = (TextView) findViewById(R.id.textView3);
		latitudeText.setContentDescription("Latitude of the modifying waypoint in editbox below");
		longitudeText = (TextView) findViewById(R.id.textView4);
		longitudeText.setContentDescription("Longitude of the modifying waypoint  in editbox below");
		
		//EditText
		nameBox = (EditText) findViewById(R.id.editText1);
		latitudeBox = (EditText) findViewById(R.id.editText2);
		longitudeBox = (EditText) findViewById(R.id.editText3);
		
		//CheckBox
		currentPositionBox = (CheckBox) findViewById(R.id.checkBox1);
		
		//receiving parameters from the waypoint activity
		Intent intentFromWayPointAct = getIntent();
		nameArray = intentFromWayPointAct.getStringArrayExtra("nameArrayFromWP");
		latitudeArray = intentFromWayPointAct.getStringArrayExtra("latitudeArrayFromWP");
		longitudeArray = intentFromWayPointAct.getStringArrayExtra("longitudeArrayFromWP");
		//receiving old name
		oldName = intentFromWayPointAct.getStringExtra("modName");
		oldLatitude = intentFromWayPointAct.getStringExtra("modLatitude");
		oldLongitude = intentFromWayPointAct.getStringExtra("modLongitude");
		
		//set default name
		nameBox.setText(oldName);
		//set each EditText default
		latitudeBox.setText(oldLatitude);
		longitudeBox.setText(oldLongitude);
		
		//setOnclickListener of current position check box
		currentPositionBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			//onCheckedChanged creation
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					//update location
					lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
					if(!currentLatitude.equals("")){
						//set each EditText with current position
						latitudeBox.setText(currentLatitude);
						longitudeBox.setText(currentLongitude);
					}
				}
				else{
					//set each EditText with default position
					latitudeBox.setText(oldLatitude);
					longitudeBox.setText(oldLongitude);
				}
			}
		});
		
		//setOnclickListener
		//nameBox
		nameBox.setOnClickListener(new OnClickListener() {
			//OnClick creation
			@Override
			public void onClick(View v) {
				if(v==nameBox)
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
				}
			}
		});
		//longitudeBox
		longitudeBox.setOnClickListener(new OnClickListener() {
			//OnClick creation
			@Override
			public void onClick(View v) {
				if(v==longitudeBox){
					longitudeBox.setSelectAllOnFocus(true);
				}
			}
		});

		//"save" button
		saveButton = (Button) findViewById(R.id.button1);
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
						Intent intentToWayPoint = new Intent(ModifyActivity.this,WayPointActivity.class);
						//pass the parameters including name,latitude,longitude
						intentToWayPoint.putExtra("modName",modName);//name
						intentToWayPoint.putExtra("modLatitude", modLatitude);//latitude
						intentToWayPoint.putExtra("modLongitude", modLongitude);//longitude
						
						//back to WayPoint activity and send some parameters to the activity
						setResult(RESULT_OK, intentToWayPoint);
						finish();
						
					}//end else
				}	
			}//end onClick
		});
		
	}//end of OnCreate
	
	//to check if the filled name or the position (latitude and longitude) are already recorded
		@SuppressLint("ShowToast")
		
		public boolean isRecorded(String n, String la, String lo){
			for(int i = 0;i<nameArray.length;i++){
				if((latitudeArray[i].equalsIgnoreCase(la) && latitudeArray[i].equalsIgnoreCase(la)) || (nameArray[i].equalsIgnoreCase(n))){
					if(nameArray[i].equalsIgnoreCase(n)){
						// same name
						Toast.makeText(ModifyActivity.this, "This name is in the list.", Toast.LENGTH_SHORT);
					}//end if
					if(latitudeArray[i].equalsIgnoreCase(la) && latitudeArray[i].equalsIgnoreCase(la)){
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
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
