package orion.ms.sara;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    private static Context mContext;

	// variables declarations
	protected static final int RESULT_SPEECH = 1;
	protected static final int RESULT_AUTO_SETTING = 2;
	protected static final int RESULT_WAYPOINT = 3;
	protected static final int RESULT_GENERAL_SETTING = 4;
	protected static final int RESULT_MAP = 5;

	private Intent intent_AutoSetting_activity;
	private Intent intent_Waypoint_activity;
	private Intent intent_GeneralSetting_activity;
	private Intent intent_Map_activity;

	public static TextView textViewSpeed = null;
	public static TextView textViewheading = null;
	public static TextView textViewDistance = null;
	public static TextView textViewBearing = null;
	public static TextView textViewAccuracy = null;
	public static TextToSpeech tts = null;

	private ImageButton buttonReco = null;
	private Button instantButton = null;

	private LocationManager lm = null;
	public static MyLocationListener ll = null;

	public SharedPreferences settings;
	public SharedPreferences.Editor editor;

	//Generating a number for a new waypoint's default name
	public static int lastNumberForInstantWaypoint = 0;
	//activating way point item from the list
	private int actItem = 0;

	@Override
    public void onCreate(Bundle savedInstanceState) {

		Log.i("test", "///////// onCreate \\\\\\\\\\");
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        MyLocationListener.isInMain = true;
        
        //location manager creation
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ll = new MyLocationListener();		
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
        
	    // Restore preferences
		this.settings = getSharedPreferences(MyLocationListener.PREFS_NAME, 0);
		this.editor = settings.edit();
		LoadPref();

        //intent creation
		intent_Waypoint_activity = new Intent(MainActivity.this,WayPointActivity.class);
		intent_AutoSetting_activity = new Intent(MainActivity.this,MainAutoSettingActivity.class);
		intent_GeneralSetting_activity = new Intent(MainActivity.this,GeneralSettingActivity.class);
		intent_Map_activity = new Intent(MainActivity.this,MyMapActivity.class);

		MyLocationListener.heading =  getResources().getString(R.string.no_satellite);
		MyLocationListener.speed =  getResources().getString(R.string.no_satellite);
		MyLocationListener.DistanceToCurrentWaypoint = getResources().getString(R.string.no_satellite);
		MyLocationListener.BearingToCurrentWaypoint = getResources().getString(R.string.no_satellite);
		MyLocationListener.accuracy = getResources().getString(R.string.no_satellite);

        //TextView creation
        textViewDistance = (TextView) findViewById(R.id.distanceView);
        Utils.setDistanceTextView(MyLocationListener.DistanceToCurrentWaypoint, "");
        textViewDistance.setContentDescription(getResources().getString(R.string.distance) + " " + getResources().getString(R.string.waiting_gps));
        
        textViewBearing = (TextView) findViewById(R.id.bearingView);
        Utils.setBearingTextView(MyLocationListener.BearingToCurrentWaypoint, "");
        textViewBearing.setContentDescription(getResources().getString(R.string.bearing) + " " + getResources().getString(R.string.waiting_gps));
            
        textViewSpeed = (TextView) findViewById(R.id.speedView);
        Utils.setSpeedTextView(MyLocationListener.speed, "");
        textViewSpeed.setContentDescription(getResources().getString(R.string.speed) + " " + getResources().getString(R.string.waiting_gps));
        
        textViewheading = (TextView) findViewById(R.id.heading);
        Utils.setHeadingTextView(MyLocationListener.heading, "");
        textViewheading.setContentDescription(getResources().getString(R.string.heading) + " " + getResources().getString(R.string.waiting_gps));

        textViewAccuracy = (TextView) findViewById(R.id.accuracyView);
        Utils.setAccuracyTextView(MyLocationListener.accuracy, "");
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
        tts.setSpeechRate(GeneralSettingActivity.speechRate);
        
        //"Instantly create a waypoint" button
        instantButton = (Button) findViewById(R.id.instantButton);
        instantButton.setTextSize(30);

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
	                }
	            }
				if(v==instantButton){
					if(!MyLocationListener.currentLatitude.equals("") && !MyLocationListener.currentLongitude.equals("")){
						Log.i("Instant button", "pressed");
				        //"create an new instant waypoint" button onClick listener
				        if(!isRecorded("H"+lastNumberForInstantWaypoint, MyLocationListener.currentLatitude, MyLocationListener.currentLongitude)){
				        	//same as instant list and waypoint list items
				        	lastNumberForInstantWaypoint += 1;
				        	WayPointActivity.wayPointList.add(new WP("H"+lastNumberForInstantWaypoint,//name
					        		MyLocationListener.currentLatitude, MyLocationListener.currentLongitude));//current location
					        //Notify
					        tts.speak("H"+lastNumberForInstantWaypoint+" is saved here.", TextToSpeech.QUEUE_ADD, null);
					        Toast.makeText(MainActivity.this, "H"+lastNumberForInstantWaypoint+" is saved here.", Toast.LENGTH_SHORT).show();
					        for(int i=0;i<WayPointActivity.wayPointList.size();i++){
					        	Log.i("waypoint list", "item "+i+" : "+WayPointActivity.wayPointList.get(i).getName());
					        }
					        //save value
					        savePref();
				        }
				        else{
				        	//Notify
					        tts.speak("This waypoint is already saved before.", TextToSpeech.QUEUE_FLUSH, null);
					        Toast.makeText(MainActivity.this, "This waypoint is already saved before.", Toast.LENGTH_SHORT).show();
				        }
					}//end if in if-else
					else{
						//GPS not available
						Toast.makeText(MainActivity.this,"GPS is unavailable." , Toast.LENGTH_SHORT).show();
						tts.speak("GPS is unavailable.", TextToSpeech.QUEUE_FLUSH, null);
					}
			    }
	        }// end of on click		
		}; // end of new View.LocationListener	

		// button activation
		buttonReco.setOnClickListener(onclickListener);
		instantButton.setOnClickListener(onclickListener);

	}//end of on create
	
	//to check if the filled name or the position (latitude and longitude) are already recorded
	@SuppressLint("ShowToast")		
	public static boolean isRecorded(String n,String la, String lo){
		for(int i = 0;i<WayPointActivity.wayPointList.size();i++){
			WP temp = WayPointActivity.wayPointList.get(i);
			if(temp.getName().equals(n) && temp.getLatitude().equals(la) && temp.getLongitude().equals(lo))
				return true;
		}
		return false;
	}//end isRecored

	@Override
	protected void onResume() {
		super.onResume();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
        MyLocationListener.isInMain = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		lm.removeUpdates(ll);
        MyLocationListener.isInMain = false;
	}
  
	@Override
	protected void onStop() {
        MyLocationListener.isInMain = false;
		super.onStop();
	}
  
	@Override
	protected void onDestroy() {
		super.onDestroy();
		lm.removeUpdates(ll);
		tts.shutdown();
        MyLocationListener.isInMain = false;
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
        		}
        	break;
        	}// end of case
        	case RESULT_WAYPOINT : {
        		if (resultCode == RESULT_OK && null != data) {
        		        	        
        	        //activating way point name and location
        	        MyLocationListener.WaypointName = data.getStringExtra("actName");
        	        MyLocationListener.WaypointLatitude = data.getDoubleExtra("actLatitude", 999);
        	        MyLocationListener.WaypointLongitude = data.getDoubleExtra("actLongitude", 999);
        			actItem = data.getIntExtra("actItem", 0);
        			
				    editor.putString("WaypointLatitude", String.valueOf(MyLocationListener.WaypointLatitude));
				    editor.putString("WaypointLongitude", String.valueOf(MyLocationListener.WaypointLongitude));
				    editor.putString("WaypointName", MyLocationListener.WaypointName);
				    editor.commit();
				    
        			Log.i("Waypoint Latitude", MyLocationListener.WaypointLatitude+"");
        			Log.i("Waypoint Latitude", MyLocationListener.WaypointLongitude+"");
        		}
        	break;
        	}// end of case
        	case RESULT_GENERAL_SETTING : {
        		if (resultCode == RESULT_OK) {
        	        tts.setSpeechRate(GeneralSettingActivity.speechRate);
        		}
        	break;
        	}


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
			intent_Waypoint_activity.putExtra("actItem", actItem);
			Log.i("Activate item", actItem+"");
			startActivityForResult(intent_Waypoint_activity, RESULT_WAYPOINT);
			break;
		case R.id.general_setting:
			startActivityForResult(intent_GeneralSetting_activity, RESULT_GENERAL_SETTING);
			break;
		case R.id.map:
			startActivityForResult(intent_Map_activity, RESULT_MAP);
			break;
		default:
			break;
		}
		return false;
	}

	public void LoadPref() {
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
     	MyLocationListener.isKnotsSelected = settings.getBoolean("isKnotsSelected", true);  
     	MyLocationListener.isKmPerHrSelected = settings.getBoolean("isKmPerHrSelected", false);
     	MyLocationListener.isPortandstarboardSelected = settings.getBoolean("isPortandstarboardSelected", true);
     	MyLocationListener.isCardinalSelected = settings.getBoolean("isCardinalSelected", false);
     	MyLocationListener.isKilometreSelected = settings.getBoolean("isKilometreSelected", true);
     	MyLocationListener.isNMSelected = settings.getBoolean("isNMSelected", false);
     	MyLocationListener.WaypointName = settings.getString("WaypointName", "defValue");
        MyLocationListener.WaypointLatitude = Double.parseDouble(settings.getString("WaypointLatitude", "999"));
        MyLocationListener.WaypointLongitude = Double.parseDouble(settings.getString("WaypointLongitude", "999"));
        lastNumberForInstantWaypoint = settings.getInt(getString(R.string.save_inst_num), 0);
        
        //speech rate
        GeneralSettingActivity.speechRate = settings.getFloat("speechRate", 1.5f);
        
        //waypoinList
        //name array
		int nameSize = settings.getInt("nameArray" + "_size", 0);  
	    String name[] = new String[nameSize];  
	    for(int i=0;i<nameSize;i++)  
	        name[i] = settings.getString("nameArray" + "_" + i, null);
	    
	    //latitude array
	    int latitudeSize = settings.getInt("latitudeArray" + "_size", 0);  
	  	String latitude[] = new String[latitudeSize];  
	  	for(int i=0;i<latitudeSize;i++)  
	  		latitude[i] = settings.getString("latitudeArray" + "_" + i, null);
	  	
	  	//longitude array
	    int longitudeSize = settings.getInt("longitudeArray" + "_size", 0);  
	  	String longitude[] = new String[longitudeSize];  
	  	for(int i=0;i<longitudeSize;i++)  
	  		longitude[i] = settings.getString("longitudeArray" + "_" + i, null);
	  	
	  	//way point list
	  	List<WP> wList = new ArrayList<WP>();
	  	for(int i=0;i<nameSize;i++){
		  	wList.add(new WP(name[i],latitude[i],longitude[i]));
	  	}
	  	
	  	WayPointActivity.wayPointList = wList;
	}

	//save preferences
	public void savePref(){
		SharedPreferences.Editor editor = settings.edit();
		//last number for instant waypoint
		editor.putInt(getString(R.string.save_inst_num), lastNumberForInstantWaypoint);
		
		String[] name = WayPointActivity.nameArray(WayPointActivity.wayPointList);
		String[] lati = WayPointActivity.latitudeArray(WayPointActivity.wayPointList);
		String[] longi = WayPointActivity.longitudeArray(WayPointActivity.wayPointList);
		
		//waypoint list
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

    public static Context getContext(){
        return mContext;
    }

}//end of Activity