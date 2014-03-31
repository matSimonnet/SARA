package com.example.mainact;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class NewWayPointActivity extends Activity {
	//variables declaration
	
		//string for each attribute of the new waypoint
		private String name = "";
		private String latitude = "";
		private String longitude = "";
		
		//TextView
		private TextView introText = null;
		private TextView nameText = null;
		private TextView latitudeText = null;
		private TextView longitudeText = null;
		
		//EditText
		private EditText nameBox = null;
		private EditText latitudeBox = null;
		private EditText longitudeBox = null;
		
		//save button
		private Button saveButton = null;
		
		private TextToSpeech tts = null;
		
		private LocationManager lm = null;		
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
				tts.setSpeechRate((float) 2.0);
				
								
				//TextView
				introText = (TextView) findViewById(R.id.textView1);
				introText.setContentDescription("The new waypoint information including name and position are set as default");
				nameText = (TextView) findViewById(R.id.textView2);
				nameText.setContentDescription("Name of the new waypoint");
				latitudeText = (TextView) findViewById(R.id.textView3);
				latitudeText.setContentDescription("Latitude of the new waypoint");
				longitudeText = (TextView) findViewById(R.id.textView4);
				longitudeText.setContentDescription("Longitude of the new waypoint");
				
				//EditText
				nameBox = (EditText) findViewById(R.id.editText1);
				latitudeBox = (EditText) findViewById(R.id.editText2);
				longitudeBox = (EditText) findViewById(R.id.editText3);
				
				
				//location manager creation
				lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
				LocationListener ll = new LocationListener(){
					//LocationListener creation
					@Override
					public void onLocationChanged(Location loc) {
						// TODO Auto-generated method stub
						latitude = String.valueOf(loc.getLatitude());
						longitude = String.valueOf(loc.getLongitude());
						//set each EditText default
						latitudeBox.setText(latitude);
						longitudeBox.setText(longitude);
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
						Log.i("LocationListener","onStatusChanged");
						//tts.speak("Location changed", tts.QUEUE_FLUSH, null);
					}
					
				};
				//lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
				
				
				
				//"save" button
				saveButton = (Button) findViewById(R.id.button1);
				//setOnClickedListener
				saveButton.setOnClickListener(new OnClickListener() {
					//OnClickedListener creation
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(v==saveButton){
							name = nameBox.getText().toString();
							latitude = latitudeBox.getText().toString();
							longitude = longitudeBox.getText().toString();
							tts.speak("the new waypoint already saved", tts.QUEUE_FLUSH, null);
							Toast.makeText(NewWayPointActivity.this,"new waypoint already saved", Toast.LENGTH_SHORT);
						}
					}
				});
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
