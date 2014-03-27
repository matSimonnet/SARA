package orion.ms.sara;

import java.util.ArrayList;
import java.util.Date;
import java.lang.Math;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnSeekBarChangeListener{

	// variables declarations
	
	protected static final int RESULT_SPEECH = 1;
	
	private TextView textViewSpeed = null;
	private TextView textViewheading = null;
	private TextView textViewDistance = null;
	private TextView textViewBearing = null;

	
	private TextView textViewSpeedTreshold = null;
	private TextView textViewHeadingTreshold = null;
	private TextView textViewspeedTimeTreshold = null;
	private TextView textViewHeadingTimeTreshold = null;
	
	private ImageButton buttonReco = null;
	
	private CheckBox speedAutoCheckBox = null;
	private CheckBox headingAutoCheckBox = null;
	
	private SeekBar speedBar = null;
	private SeekBar headingBar = null;
	private SeekBar speedtimeBar = null; 
	private SeekBar headingtimeBar = null; 
	
	private TextToSpeech tts = null;
	
	private LocationManager lm = null;
	private LocationListener ll = null;
	
	private String heading = "";
	private double headingAuto = 0;
	private double headingLastAuto = 0;
	private double headingTreshold = 10; 
	private long headingTimeTreshold = 5;
	private Date headingNow = null;
	private Date headingBefore = null;
	
	private String speed = "";
	private double speedAuto = 0;
	private double speedLastAuto = 0;
	private double speedTreshold = 1; 
	private long speedTimeTreshold = 5;
	private Date speedNow = null;
	private Date speedBefore = null;
	
	private String distance = "";
	//private Location loc = null;
	
	// Carrefour Express, 33 Rue Victor Eusen, 29200 Brest, France
	private double currentlatitude = 48.381481;
	private double currentlongitude = -4.533540;
	
	
	private String BearingToWaypoint = "";
	private double bearing = 0;

	
	//positions : 
	@SuppressWarnings("unused")
	private String latitude = "";
	@SuppressWarnings("unused")
	private String longitude = "";
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //DefaultValue
        latitude =  getResources().getString(R.string.nosatelite);
        longitude =  getResources().getString(R.string.nosatelite);
        heading =  getResources().getString(R.string.nosatelite);
        speed =  getResources().getString(R.string.nosatelite);
        distance = getResources().getString(R.string.nosatelite);
        BearingToWaypoint = getResources().getString(R.string.nosatelite);


    //TextView creation
    textViewDistance = (TextView) findViewById(R.id.distanceView);
    textViewDistance.setText(distance);
    textViewDistance.setContentDescription("");
    
    textViewBearing = (TextView) findViewById(R.id.bearingView);
    textViewBearing.setText(BearingToWaypoint);
    textViewBearing.setContentDescription("");
        
    textViewSpeed = (TextView) findViewById(R.id.speedView);
    textViewSpeed.setText(speed);
    textViewSpeed.setContentDescription(getResources().getString(R.string.speed) + speed + " " + getResources().getString(R.string.speedunit));
    
    textViewheading = (TextView) findViewById(R.id.heading);
    textViewheading.setText(heading);
    textViewheading.setContentDescription(getResources().getString(R.string.heading)+ " "  + heading  + " " + getResources().getString(R.string.headingunit));
    
    textViewSpeedTreshold = (TextView) findViewById(R.id.speedTresholdView); 
    textViewSpeedTreshold.setText(getResources().getString(R.string.speedtreshold)+ " "  + speedTreshold + " " + getResources().getString(R.string.speedunit));
    textViewSpeedTreshold.setContentDescription(getResources().getString(R.string.speedtreshold) + speedTreshold + " " + getResources().getString(R.string.speedunit));
    
    textViewHeadingTreshold = (TextView) findViewById(R.id.headingTresholdView);
    textViewHeadingTreshold.setText(getResources().getString(R.string.headingtreshold)+ " "  + (int)headingTreshold + " " + getResources().getString(R.string.headingunit));
    textViewHeadingTreshold.setContentDescription(getResources().getString(R.string.headingtreshold) + (int)headingTreshold  + " " + getResources().getString(R.string.headingunit));
    
    textViewspeedTimeTreshold = (TextView) findViewById(R.id.speedtimetreshold);
    textViewspeedTimeTreshold.setText(getResources().getString(R.string.speedtimetreshold)+ " "  + speedTimeTreshold + " " + getResources().getString(R.string.timeunit));
    textViewspeedTimeTreshold.setContentDescription(getResources().getString(R.string.speedtimetreshold) + speedTimeTreshold + " " + getResources().getString(R.string.timeunit));
    
    textViewHeadingTimeTreshold = (TextView) findViewById(R.id.headingtimetreshold);
    textViewHeadingTimeTreshold.setText(getResources().getString(R.string.headingtimetreshold) + " " + headingTimeTreshold + " " + getResources().getString(R.string.timeunit));
    textViewHeadingTimeTreshold.setContentDescription(getResources().getString(R.string.headingtimetreshold) + " " + headingTimeTreshold + " " + getResources().getString(R.string.timeunit));
    
    //CheckBox
    speedAutoCheckBox = (CheckBox) findViewById(R.id.speedAutoCheckBox);
    speedAutoCheckBox.setChecked(true);
    headingAutoCheckBox = (CheckBox) findViewById(R.id.headingAutoCheckBox);
    headingAutoCheckBox.setChecked(true);
        
    //SpeedBar
    speedBar = (SeekBar) findViewById(R.id.seekBarSpeed);
    speedBar.setOnSeekBarChangeListener(this);
    speedBar.setContentDescription(getResources().getString(R.string.speedtreshold));
    
    //SpeedBar
    headingBar = (SeekBar) findViewById(R.id.seekBarheading);
    headingBar.setOnSeekBarChangeListener(this);
    headingBar.setContentDescription(getResources().getString(R.string.headingtreshold));
    
    //TimeBars 
    speedtimeBar = (SeekBar) findViewById(R.id.seekBarSpeedTime);
    speedtimeBar.setOnSeekBarChangeListener(this);
    speedtimeBar.setContentDescription(getResources().getString(R.string.speedtimetreshold));
    headingtimeBar = (SeekBar) findViewById(R.id.seekBarHeadingTime);
    headingtimeBar.setOnSeekBarChangeListener(this);
    headingtimeBar.setContentDescription(getResources().getString(R.string.headingtimetreshold));
        
	//location manager creation
	lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	ll = new MyLocationListener();		
	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
   
	/**
    loc = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
    bearing = _Bearing(loc.getLatitude(),loc.getLongitude(),currentlatitude,currentlongitude);
	**/
    
	//dates creation
	speedBefore = new Date();
	headingBefore = new Date();
	
	
	//2point creation
	/**
	point1 = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
	point2 = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);	
	point1.setLatitude(48.1);
    point1.setLongitude(-4.1);
    point2.setLatitude(48.9);
    point2.setLongitude(-4.9);
    int dist1to2 = (int)(point1.distanceTo(point2))/1000  ;
    test = (String.valueOf(dist1to2));
    textView5.setText(test);
    **/
	
	//OnInitListener Creation
	OnInitListener onInitListener = new OnInitListener() {
		@Override
		public void onInit(int status) {
		}
	};
	
    // tts creation
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
    //tts.speak("resume", tts.QUEUE_FLUSH, null);
  }

  @Override
  protected void onPause() {
    super.onPause();
    //lm.removeUpdates(ll);
  }
  
  @Override
  protected void onStop() {
    super.onStop();
	//tts.shutdown();
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
        }//end of switch 
    }//end of on Activity result 
	
	//method to round 1 decimal

	public double arrondiSpeed(double val) {return (Math.floor(val*10))/10;}
	public double arrondiDistance(double val) {return (Math.floor(val*10))/10;}
	public double arrondiBearing(double val) {return (Math.floor(val*10))/10;}



    public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			
			latitude = String.valueOf(loc.getLatitude());
			longitude = String.valueOf(loc.getLongitude());			
			speed = String.valueOf(arrondiSpeed(loc.getSpeed()*(1.94)));
			heading = String.valueOf((int)loc.getBearing());
			
			// calculate distance to the current waypoint
			float[] result = new float[1];
			Location.distanceBetween(loc.getLatitude(), loc.getLongitude(),currentlatitude,currentlongitude, result);
			distance = String.valueOf(arrondiDistance(result[0]/1000));
			Log.i("distance", distance);
			
			// calculate bearing to the current waypoint
			bearing = _Bearing(loc.getLatitude(),loc.getLongitude(),currentlatitude,currentlongitude);
			BearingToWaypoint = String.valueOf(arrondiBearing(bearing));
			Log.i("bearing", BearingToWaypoint);
			
			
			if (speedAutoCheckBox.isChecked()){
				speedAuto = arrondiSpeed(loc.getSpeed()*(1.94));
				speedNow = new Date();
				if 	((( speedAuto < speedLastAuto - speedTreshold ) || ( speedAuto > speedLastAuto + speedTreshold ))
				 &&	((speedNow.getTime() - speedBefore.getTime()) > speedTimeTreshold*1000)){
				tts.speak(getResources().getString(R.string.speed)+ " : " + speed, TextToSpeech.QUEUE_ADD, null);
				speedLastAuto = speedAuto;
				speedBefore = new Date();
				}
			}//end of if speedAutoCheck...

			if (headingAutoCheckBox.isChecked()){
				headingAuto = (int)loc.getBearing();
				headingNow = new Date();
				
				int headingDiff = java.lang.Math.abs( (int)headingLastAuto - (int)headingAuto );
				if (headingDiff > 180) headingDiff = java.lang.Math.abs(headingDiff-360);
				
				if 	((( headingDiff > headingTreshold ))
				 &&	((headingNow.getTime() - headingBefore.getTime()) > headingTimeTreshold*1000)){
				tts.speak(getResources().getString(R.string.heading)+ " : " + heading, TextToSpeech.QUEUE_ADD, null);
				headingLastAuto = headingAuto;
				headingBefore = new Date();
				}
			}//end of if speedAutoCheck...
			
			
			//displaying value
			textViewSpeed.setText(speed);
			textViewheading.setText(heading);    
			textViewDistance.setText(distance);
			textViewBearing.setText(BearingToWaypoint);    


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

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		
		if (seekBar.equals(speedBar)){
			speedTreshold = (double) progress/10;
			textViewSpeedTreshold.setText(getResources().getString(R.string.speedtreshold) + " " + Double.valueOf(speedTreshold).toString()+ " " + getResources().getString(R.string.speedunit) );
			seekBar.setContentDescription(Double.valueOf(speedTreshold).toString()+ " " + getResources().getString(R.string.speedunit));
		}
		
		else if (seekBar.equals(headingBar)){
			headingTreshold = progress;
			textViewHeadingTreshold.setText(getResources().getString(R.string.headingtreshold) + " " + Integer.toString((int)headingTreshold)+ " " + getResources().getString(R.string.headingunit));
			seekBar.setContentDescription(Integer.toString((int)headingTreshold)+ " " + getResources().getString(R.string.headingunit));
		}
		
		else if (seekBar.equals(speedtimeBar)){
			speedTimeTreshold = progress;
			textViewspeedTimeTreshold.setText(getResources().getString(R.string.speedtimetreshold) + " " + Integer.toString((int)speedTimeTreshold)+ " " + getResources().getString(R.string.timeunit));
			seekBar.setContentDescription(Integer.toString((int)speedTimeTreshold)+ " " + getResources().getString(R.string.speedunit));
		}
		
		else if (seekBar.equals(headingtimeBar)){
			headingTimeTreshold = progress;
			textViewHeadingTimeTreshold.setText(getResources().getString(R.string.headingtimetreshold) + " " + Integer.toString((int)headingTimeTreshold)+ " " + getResources().getString(R.string.timeunit));
			seekBar.setContentDescription(Integer.toString((int)headingTimeTreshold)+ " " + getResources().getString(R.string.timeunit));
		}
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

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
			Intent intentAuto_setting = new Intent(MainActivity.this,AutoSetting.class);
			startActivity(intentAuto_setting);
			break;
		case R.id.waypoint_setting:
			Intent intentWaypoint_setting = new Intent(MainActivity.this,AutoSetting.class);
			startActivity(intentWaypoint_setting);
			break;
		case R.id.main:
			Intent intentmain = new Intent(MainActivity.this,MainActivity.class);
			startActivity(intentmain);
			break;
		default:
			break;
		}

		return false;
	}
	
	
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
	

	
 
}//end of Activity