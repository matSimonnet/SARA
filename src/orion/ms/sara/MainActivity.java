package orion.ms.sara;

import java.util.ArrayList;
import java.util.Date;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	public static final String PREFS_NAME = "MyPrefsFile";
    private static Context mContext;

	// variables declarations
	protected static final int RESULT_SPEECH = 1;
	protected static final int RESULT_AUTO_SETTING = 2;
	protected static final int RESULT_WAYPOINT = 3;
	protected static final int RESULT_MAIN = 3;

	private Intent intent_AutoSetting_activity;
	private Intent intent_Waypoint_activity;

	static TextView textViewSpeed = null;
	static TextView textViewheading = null;
	static TextView textViewDistance = null;
	static TextView textViewBearing = null;
	static TextView textViewAccuracy = null;

	static TextToSpeech tts = null;
	private ImageButton buttonReco = null;
	
	private LocationManager lm = null;
	private MyLocationListener ll = null;
	private Location location = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		Log.i("test", "///////// onCreate \\\\\\\\\\");
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        
        //location manager creation
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ll = new MyLocationListener();		
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
        
        // Restore preferences
        LoadPref();

        //intent creation
		intent_Waypoint_activity = new Intent(MainActivity.this,WayPointActivity.class);
		intent_AutoSetting_activity = new Intent(MainActivity.this,AutoSettingActivity.class);

		MyLocationListener.heading =  getResources().getString(R.string.no_satellite);
		MyLocationListener.speed =  getResources().getString(R.string.no_satellite);
		MyLocationListener.DistanceToCurrentWaypoint = getResources().getString(R.string.no_satellite);
		MyLocationListener.BearingToCurrentWaypoint = getResources().getString(R.string.no_satellite);
		MyLocationListener.accuracy = getResources().getString(R.string.no_satellite);

        //TextView creation
        textViewDistance = (TextView) findViewById(R.id.distanceView);
        textViewDistance.setText(MyLocationListener.DistanceToCurrentWaypoint);
        textViewDistance.setContentDescription(getResources().getString(R.string.distance) + " " + getResources().getString(R.string.waiting_gps));
        
        textViewBearing = (TextView) findViewById(R.id.bearingView);
        textViewBearing.setText(MyLocationListener.BearingToCurrentWaypoint);
        textViewBearing.setContentDescription(getResources().getString(R.string.bearing) + " " + getResources().getString(R.string.waiting_gps));
            
        textViewSpeed = (TextView) findViewById(R.id.speedView);
        textViewSpeed.setText(MyLocationListener.speed);
        textViewSpeed.setContentDescription(getResources().getString(R.string.speed) + " " + getResources().getString(R.string.waiting_gps));
        
        textViewheading = (TextView) findViewById(R.id.heading);
        textViewheading.setText(MyLocationListener.heading);
        textViewheading.setContentDescription(getResources().getString(R.string.heading) + " " + getResources().getString(R.string.waiting_gps));
	     
        textViewAccuracy = (TextView) findViewById(R.id.accuracyView);
        textViewAccuracy.setText(MyLocationListener.accuracy);
        textViewAccuracy.setContentDescription(getResources().getString(R.string.accuracy) + " " + getResources().getString(R.string.waiting_gps));
        
        //dates creation
        MyLocationListener.speedBefore = new Date();
        MyLocationListener.headingBefore = new Date();
        MyLocationListener.distanceBefore = new Date();
        MyLocationListener.bearingBefore = new Date();
        
		 //OnInitListener Creation
        OnInitListener onInitListener = new OnInitListener() {
        	@Override
        	public void onInit(int status) {
        	}
        };
	
        // Text to speech creation
        tts = new TextToSpeech(this, onInitListener);
        tts.setSpeechRate((float) 2.0);
	
        // button creation
        buttonReco= new ImageButton(this);
        buttonReco = (ImageButton) findViewById(R.id.buttonSpeak);

        // OnClickListener creation
        View.OnClickListener onclickListener = new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		if (v== buttonReco){
        			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE);	 
        			try {
        				startActivityForResult(intent, RESULT_SPEECH);
	                } catch (ActivityNotFoundException a) {
	                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.norecognition),Toast.LENGTH_SHORT).show();
	                }// end of catch
	            }// end of if button5
	        }// end of onclick		
		}; // end of new View.LocationListener	
	
		// button activation
		buttonReco.setOnClickListener(onclickListener);
	
	}//end of oncreate
	
	@Override
	protected void onResume() {
		super.onResume();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
	}

	@Override
	protected void onPause() {
		super.onPause();
		lm.removeUpdates(ll);
	}
  
	@Override
	protected void onStop() {
		super.onStop();
	}
  
	@Override
	protected void onDestroy() {
		super.onDestroy();
		lm.removeUpdates(ll);
		tts.shutdown();
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        switch (requestCode) {
        	case RESULT_SPEECH: {
        		if (resultCode == RESULT_OK && null != data) {
        			ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                	if ( (text.get(0).equals(getResources().getString(R.string.speed)))){
                		tts.speak(getResources().getString(R.string.speed)+ " : " + MyLocationListener.speed, TextToSpeech.QUEUE_ADD, null);
                	}
                	else if ( (text.get(0).equals(getResources().getString(R.string.heading)))){
                		tts.speak(getResources().getString(R.string.heading)+ " : " + MyLocationListener.heading, TextToSpeech.QUEUE_ADD, null);
                	}
                	else {
                		Toast.makeText(getApplicationContext(),text.get(0),Toast.LENGTH_SHORT).show();
                	}
        		}	 
            break;
        	}// end of case
        	case RESULT_AUTO_SETTING : {
        		if (resultCode == RESULT_OK && null != data) {

        			MyLocationListener.speedTreshold = data.getDoubleExtra("speedTreshold", 1.0);
        			MyLocationListener.speedTimeTreshold = data.getLongExtra("speedTimeTreshold", 5);
        			MyLocationListener.headingTreshold = data.getDoubleExtra("headingTreshold", 10.0);
        			MyLocationListener.headingTimeTreshold = data.getLongExtra("headingTimeTreshold", 5);
        			MyLocationListener.distanceTimeTreshold = data.getLongExtra("distanceTimeTreshold", 5);
        			MyLocationListener.bearingTreshold = data.getDoubleExtra("bearingTreshold", 10.0);
        			MyLocationListener.bearingTimeTreshold = data.getLongExtra("bearingTimeTreshold", 5);
        			MyLocationListener.accuracyTimeTreshold = data.getLongExtra("accuracyTimeTreshold", 5);

        			MyLocationListener.isAutoSpeed = data.getBooleanExtra("isAutoSpeed", true);
        			MyLocationListener.isAutoHeading = data.getBooleanExtra("isAutoHeading", true);
        			MyLocationListener.isAutoDistance = data.getBooleanExtra("isAutoDistance", true);
        			MyLocationListener.isAutoBearing = data.getBooleanExtra("isAutoBearing", true);
        			MyLocationListener.isAutoAccuracy = data.getBooleanExtra("isAutoAccuracy", true);

        			Log.i("speed", MyLocationListener.speedTreshold+"");
        			Log.i("speedtime", MyLocationListener.speedTimeTreshold+"");
        			Log.i("heading", MyLocationListener.headingTreshold+"");
        			Log.i("headingtime", MyLocationListener.headingTimeTreshold+"");
        			Log.i("distancetime", MyLocationListener.distanceTimeTreshold+"");
        			Log.i("bearing", MyLocationListener.bearingTreshold+"");
        			Log.i("bearingtime", MyLocationListener.bearingTimeTreshold+"");
        			Log.i("accuracytime", MyLocationListener.accuracyTimeTreshold+"");

        			Log.i("isSpeed", MyLocationListener.isAutoSpeed+"");
        			Log.i("isheading", MyLocationListener.isAutoHeading+"");
        			Log.i("isDistance", MyLocationListener.isAutoDistance+"");
        			Log.i("isBearing", MyLocationListener.isAutoBearing+"");
        			Log.i("isAccuracy", MyLocationListener.isAutoAccuracy+"");

        		}
        	break;
        	}// end of case
        	case RESULT_WAYPOINT : {
        		if (resultCode == RESULT_OK && null != data) {
        			
        	        location = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        	        
        	        MyLocationListener.WaypointLatitude = data.getDoubleExtra("actLatitude", 999);
        	        MyLocationListener.WaypointLongitude = data.getDoubleExtra("actLongitude", 999);
        			
        			Location.distanceBetween(MyLocationListener.WaypointLatitude, MyLocationListener.WaypointLongitude, location.getLatitude(), location.getLongitude(), MyLocationListener.distance);
        			MyLocationListener.distanceTreshold = MyLocationListener.distance[0]/10000;

        			Log.i("Waypoint Latitude", MyLocationListener.WaypointLatitude+"");
        			Log.i("Waypoint Latitude", MyLocationListener.WaypointLongitude+"");
        			Log.i("distance to waypoint", MyLocationListener.distance[0]/1000+"");
        			Log.i("distance treshold", MyLocationListener.distanceTreshold+"");
        		}
        	break;
        	}// end of case

        }// end of switch 
    }// end of on Activity result 
	
	//  action bar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem Item){
		switch (Item.getItemId()) {
		case R.id.auto_setting:
            startActivityForResult(intent_AutoSetting_activity, RESULT_AUTO_SETTING);
			break;
		case R.id.waypoint_setting:
			startActivityForResult(intent_Waypoint_activity, RESULT_WAYPOINT);
			break;
		default:
			break;
		}
		return false;
	}
	
	public void LoadPref() {
     	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
     	MyLocationListener.speedTreshold = Double.parseDouble(settings.getString("speedTreshold", "1.0"));
     	MyLocationListener.speedTimeTreshold = settings.getLong("speedTimeTreshold", 5);
     	MyLocationListener.headingTreshold = Double.parseDouble(settings.getString("headingTreshold", "10.0"));
     	MyLocationListener.headingTimeTreshold = settings.getLong("headingTimeTreshold", 5);
     	MyLocationListener.distanceTimeTreshold = settings.getLong("distanceTimeTreshold", 5); 	    
     	MyLocationListener.bearingTreshold = Double.parseDouble(settings.getString("bearingTreshold", "10.0"));
     	MyLocationListener.bearingTimeTreshold = settings.getLong("bearingTimeTreshold", 5);	    
     	MyLocationListener.accuracyTimeTreshold = settings.getLong("accuracyTimeTreshold", 5);	    
     	MyLocationListener.isAutoSpeed = settings.getBoolean("isAutoSpeed", true);
     	MyLocationListener.isAutoHeading = settings.getBoolean("isAutoHeading", true);
     	MyLocationListener.isAutoDistance = settings.getBoolean("isAutoDistance", true);
     	MyLocationListener.isAutoBearing = settings.getBoolean("isAutoBearing", true);
     	MyLocationListener.isAutoAccuracy = settings.getBoolean("isAutoAccuracy", true);

        MyLocationListener.WaypointLatitude = Double.parseDouble(settings.getString("WaypointLatitude", "999"));
        MyLocationListener.WaypointLongitude = Double.parseDouble(settings.getString("WaypointLongitude", "999"));
	}
    public static Context getContext(){
        return mContext;
    }

}//end of Activity