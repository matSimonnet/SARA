package orion.ms.sara;

import java.util.ArrayList;
import java.util.Date;
import java.lang.Math;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationListener;
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

	// variables declarations
	protected static final int RESULT_SPEECH = 1;
	protected static final int RESULT_AUTO_SETTING = 2;
	protected static final int RESULT_WAYPOINT_SETTING = 3;
	protected static final int RESULT_MAIN = 3;

	private Intent intentAuto_setting;
	//private Intent intentWaypoint_setting;
	//private Intent intentMain;

	private TextView textViewSpeed = null;
	private TextView textViewheading = null;
	private TextView textViewDistance = null;
	private TextView textViewBearing = null;

	private ImageButton buttonReco = null;
	private TextToSpeech tts = null;
	
	private LocationManager lm = null;
	private LocationListener ll = null;
	
	private String speed = "";
	private double speedAuto = 0.0;
	private double speedLastAuto = 0.0;
	private double speedTreshold = 1.0; 
	private long speedTimeTreshold = 5;
	private Date speedNow = null;
	private Date speedBefore = null;
	
	private String heading = "";
	private double headingAuto = 0.0;
	private double headingLastAuto = 0.0;
	private double headingTreshold = 10.0; 
	private long headingTimeTreshold = 5;
	private Date headingNow = null;
	private Date headingBefore = null;
	
	private String DistanceToCurrentWaypoint = "";
	private float[] distance = new float[1];
	private double distanceAuto = 0.0;
	private double distanceLastAuto = 0.0;
	private double distanceTreshold = 0.0;
	private long distanceTimeTreshold = 5;
	private Date distanceNow = null;
	private Date distanceBefore = null;
	
	private String BearingToCurrentWaypoint = "";
	private double bearing = 0.0;
	private double bearingAuto = 0.0;
	private double bearingLastAuto = 0.0;
	private double bearingTreshold = 10.0;
	private long bearingTimeTreshold = 5;
	private Date bearingNow = null;
	private Date bearingBefore = null;
	
	//private Location LastKnowLocation = null;
	private String WaypointLatitude = "";
	private String WaypointLongitude = "";
	
	private boolean isAutoSpeed;
	private boolean isAutoHeading;
	private boolean isAutoDistance;
	private boolean isAutoBearing;

	//positions : 
	@SuppressWarnings("unused")
	private String latitude = "";
	@SuppressWarnings("unused")
	private String longitude = "";
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		Log.i("test", "///////// onCreate \\\\\\\\\\");
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Restore preferences
     	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
     	this.speedTreshold = Double.parseDouble(settings.getString("speedTreshold", "1.0"));
     	this.speedTimeTreshold = settings.getLong("speedTimeTreshold", 5);
     	this.headingTreshold = Double.parseDouble(settings.getString("headingTreshold", "10.0"));
     	this.headingTimeTreshold = settings.getLong("headingTimeTreshold", 5);
     	this.distanceTimeTreshold = settings.getLong("distanceTimeTreshold", 5); 	    
     	this.bearingTreshold = Double.parseDouble(settings.getString("bearingTreshold", "10.0"));
     	this.bearingTimeTreshold = settings.getLong("bearingTimeTreshold", 5);	    
     	this.isAutoSpeed = settings.getBoolean("isAutoSpeed", true);
     	this.isAutoHeading = settings.getBoolean("isAutoHeading", true);
     	this.isAutoDistance = settings.getBoolean("isAutoDistance", true);
     	this.isAutoBearing = settings.getBoolean("isAutoBearing", true);

        
        //intent creation
		//intentMain = new Intent(MainActivity.this,MainActivity.class);
		//intentWaypoint_setting = new Intent(MainActivity.this,Waypoint.class);
		intentAuto_setting = new Intent(MainActivity.this,AutoSetting.class);

        //DefaultValue
        latitude =  getResources().getString(R.string.nosatelite);
        longitude =  getResources().getString(R.string.nosatelite);
        
        heading =  "No Satellite";
        speed =  "No Satellite";
        DistanceToCurrentWaypoint = "No Satellite";
        BearingToCurrentWaypoint = "No Satellite";

        //TextView creation
        textViewDistance = (TextView) findViewById(R.id.distanceView);
        textViewDistance.setText(DistanceToCurrentWaypoint);
        textViewDistance.setContentDescription("Distance is waiting for GPS signal");
        
        textViewBearing = (TextView) findViewById(R.id.bearingView);
        textViewBearing.setText(BearingToCurrentWaypoint);
        textViewBearing.setContentDescription("Bearing is waiting for GPS signal");
            
        textViewSpeed = (TextView) findViewById(R.id.speedView);
        textViewSpeed.setText(speed);
        textViewSpeed.setContentDescription("Speed is waiting for GPS signal");
        
        textViewheading = (TextView) findViewById(R.id.heading);
        textViewheading.setText(heading);
        textViewheading.setContentDescription("Heading is waiting for GPS signal");
        
        //location manager creation
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ll = new MyLocationListener();		
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
	
        // Last know location creation
        //LastLocation = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
 
        this.WaypointLatitude = settings.getString("WaypointLatitude", "");
        this.WaypointLongitude = settings.getString("WaypointLongitude", "");
    
        //dates creation
        speedBefore = new Date();
        headingBefore = new Date();
        distanceBefore = new Date();
        bearingBefore = new Date();
        
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
	                }//end of catch
	            }// end of if button5
	        }// end of onclick		
		}; //end of new View.LocationListener	
	
		// button activation
		buttonReco.setOnClickListener(onclickListener);
	
	}//end of oncreate
	
	@Override
	protected void onResume() {
		super.onResume();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
		//tts.speak("resume", TextToSpeech.QUEUE_FLUSH, null);
	}

	@Override
	protected void onPause() {
		super.onPause();
		lm.removeUpdates(ll);
		//tts.speak("pause", TextToSpeech.QUEUE_FLUSH, null);
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
                		tts.speak(getResources().getString(R.string.speed)+ " : " + speed, TextToSpeech.QUEUE_ADD, null);
                	}
                	else if ( (text.get(0).equals(getResources().getString(R.string.heading)))){
                		tts.speak(getResources().getString(R.string.heading)+ " : " + heading, TextToSpeech.QUEUE_ADD, null);
                	}
                	else {
                		Toast.makeText(getApplicationContext(),text.get(0),Toast.LENGTH_SHORT).show();
                	}
        		}	 
            break;
        	}// end of case
        	case RESULT_AUTO_SETTING : {
        		if (resultCode == RESULT_OK && null != data) {

        			this.speedTreshold = data.getDoubleExtra("speedTreshold", 1.0);
        			this.speedTimeTreshold = data.getLongExtra("speedTimeTreshold", 5);
        			
        			this.headingTreshold = data.getDoubleExtra("headingTreshold", 10.0);
        			this.headingTimeTreshold = data.getLongExtra("headingTimeTreshold", 5);
        			
        			this.distanceTimeTreshold = data.getLongExtra("distanceTimeTreshold", 5);
        			
        			this.bearingTreshold = data.getDoubleExtra("bearingTreshold", 10.0);
        			this.bearingTimeTreshold = data.getLongExtra("bearingTimeTreshold", 5);
        			
        			this.isAutoSpeed = data.getBooleanExtra("isAutoSpeed", true);
        			this.isAutoHeading = data.getBooleanExtra("isAutoHeading", true);
        			this.isAutoDistance = data.getBooleanExtra("isAutoDistance", true);
        			this.isAutoBearing = data.getBooleanExtra("isAutoBearing", true);


        			Log.i("speed", this.speedTreshold+"");
        			Log.i("speedtime", this.speedTimeTreshold+"");
        			Log.i("heading", this.headingTreshold+"");
        			Log.i("headingtime", this.headingTimeTreshold+"");
        			Log.i("distancetime", this.distanceTimeTreshold+"");
        			Log.i("bearing", this.bearingTreshold+"");
        			Log.i("bearingtime", this.bearingTimeTreshold+"");

        			Log.i("isSpeed", this.isAutoSpeed+"");
        			Log.i("isheading", this.isAutoHeading+"");
        			Log.i("isDistance", this.isAutoDistance+"");
        			Log.i("isBearing", this.isAutoBearing+"");
        		}
        	break;
        	}// end of case

        }//end of switch 
    }//end of on Activity result 
	
	//  action bar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem Item){
		switch (Item.getItemId()) {
		case R.id.auto_setting:
            startActivityForResult(intentAuto_setting, RESULT_AUTO_SETTING);
			break;
		case R.id.waypoint_setting:
            //startActivityForResult(intentWaypoint_setting, RESULT_WAYPOINT_SETTING);
			break;
		default:
			break;
		}

		return false;
	}
	
	// FOR NEW UTILS CLASS
	
	public static double RadToDeg(double radians)  
    {  
        return radians * (180 / Math.PI);  
    }  

    public static double DegToRad(double degrees)  
    {  
        return degrees * (Math.PI / 180);  
    }  

    public static double _Bearing(double lat1, double long1, double lat2, double long2)  
    {  
        //Convert input values to radians  
        lat1 = DegToRad(lat1);  
        long1 = DegToRad(long1);  
        lat2 =  DegToRad(lat2);  
        long2 = DegToRad(long2);  

        double deltaLong = long2 - long1;  

        double y = Math.sin(deltaLong) * Math.cos(lat2);  
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltaLong);  
        double bearing = Math.atan2(y, x);  
        return ConvertToBearing(RadToDeg(bearing));  
    }  

    public static double ConvertToBearing(double deg)  
    {  
        return (deg + 360) % 360;  
    }  
    
	//method to round 1 decimal
	public double arrondiSpeed(double val) {return (Math.floor(val*10))/10;}
	public double arrondiDistance(double val) {return (Math.floor(val*10))/10;}
	public double arrondiBearing(double val) {return (Math.floor(val*10))/10;}
    
	// FOR NEW MYLOCATION LISTENER CLASS
    
    public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			
			latitude = String.valueOf(loc.getLatitude());
			longitude = String.valueOf(loc.getLongitude());			
			speed = String.valueOf(arrondiSpeed(loc.getSpeed()*(1.945)));
			heading = String.valueOf((int)loc.getBearing());
			
			if(WaypointLatitude == "" || WaypointLongitude == "") {
				textViewDistance.setText("No Waypoint");
				textViewBearing.setText("No Waypoint");  
		        textViewDistance.setContentDescription("Waypoint is not activated");
		        textViewBearing.setContentDescription("Waypoint is not activated");
			}
			else {
				// calculate distance to the current waypoint
				Location.distanceBetween(Double.parseDouble(WaypointLatitude), Double.parseDouble(WaypointLongitude), loc.getLatitude(), loc.getLongitude(), distance);
				DistanceToCurrentWaypoint = String.valueOf(arrondiDistance(distance[0]/1000));
				Log.i("distance", DistanceToCurrentWaypoint);
				
				// calculate bearing to the current waypoint						
				bearing = _Bearing(loc.getLatitude(), loc.getLongitude(), Double.parseDouble(WaypointLatitude), Double.parseDouble(WaypointLongitude));
				BearingToCurrentWaypoint = String.valueOf(arrondiBearing(bearing));
				Log.i("bearing", BearingToCurrentWaypoint);
				
				if (isAutoBearing){
					bearingAuto = (int) bearing;
					bearingNow = new Date();
				
					int bearingDiff = java.lang.Math.abs((int)bearingLastAuto - (int)bearingAuto);
					if (bearingDiff > 180) bearingDiff = java.lang.Math.abs(bearingDiff-360);
				
					if 	((( bearingDiff > bearingTreshold ))
						&&	((bearingNow.getTime() - bearingBefore.getTime()) > bearingTimeTreshold*1000)){
						tts.speak(getResources().getString(R.string.bearing)+ " : " + BearingToCurrentWaypoint + " " + getResources().getString(R.string.headingunit), TextToSpeech.QUEUE_ADD, null);
						bearingLastAuto = bearingAuto;
						bearingBefore = new Date();
					}
				}//end of if bearingAutoCheck...
			
				if (isAutoDistance){
					distanceAuto = distance[0]/1000;
					distanceNow = new Date();		
				
					if 	((( distanceAuto < distanceLastAuto - distanceTreshold) || ( distanceAuto > distanceLastAuto + distanceTreshold))
						&&	((distanceNow.getTime() - distanceBefore.getTime()) > distanceTimeTreshold*1000)){
						tts.speak(getResources().getString(R.string.Distance)+ " : " + DistanceToCurrentWaypoint + " " + getResources().getString(R.string.DistanceUnit), TextToSpeech.QUEUE_ADD, null);
				
						distanceLastAuto = distanceAuto;
						distanceBefore = new Date();
					}
				}//end of if distanceAutoCheck...
			}// end of else..
				
			if (isAutoSpeed){
				speedAuto = arrondiSpeed(loc.getSpeed()*(1.945));
				speedNow = new Date();
				if 	((( speedAuto < speedLastAuto - speedTreshold ) || ( speedAuto > speedLastAuto + speedTreshold ))
					&&	((speedNow.getTime() - speedBefore.getTime()) > speedTimeTreshold*1000)){
				tts.speak(getResources().getString(R.string.speed)+ " : " + speed + " " + getResources().getString(R.string.speedunit), TextToSpeech.QUEUE_ADD, null);
				speedLastAuto = speedAuto;
				speedBefore = new Date();
				}
			}//end of if speedAutoCheck...

			if (isAutoHeading){
				headingAuto = (int)loc.getBearing();
				headingNow = new Date();
				
				int headingDiff = java.lang.Math.abs((int)headingLastAuto - (int)headingAuto);
				if (headingDiff > 180) headingDiff = java.lang.Math.abs(headingDiff-360);
				
				if 	((( headingDiff > headingTreshold ))
					&&	((headingNow.getTime() - headingBefore.getTime()) > headingTimeTreshold*1000)){
				tts.speak(getResources().getString(R.string.heading)+ " : " + heading + " " + getResources().getString(R.string.headingunit), TextToSpeech.QUEUE_ADD, null);
				headingLastAuto = headingAuto;
				headingBefore = new Date();
				}
			}//end of if headingAutoCheck...
				
			//displaying value
			textViewSpeed.setText(speed);
			textViewheading.setText(heading);
			textViewSpeed.setContentDescription(getResources().getString(R.string.speed) + " " + speed + " " + getResources().getString(R.string.speedunit));			
		    textViewheading.setContentDescription(getResources().getString(R.string.heading)+ " "  + heading  + " " + getResources().getString(R.string.headingunit));
			
			if(WaypointLatitude != "" && WaypointLongitude != "") {
				textViewDistance.setText(DistanceToCurrentWaypoint);
				textViewBearing.setText(BearingToCurrentWaypoint);  
		        textViewDistance.setContentDescription("Distance" + DistanceToCurrentWaypoint + " " + "kilometres");
		        textViewBearing.setContentDescription("Bearing" + BearingToCurrentWaypoint + " " + "degrees");
			}
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
			//Log.i("LocationListener","onStatusChanged");
		}
    	
    } //end of MyLocationListener
	

	
 
}//end of Activity