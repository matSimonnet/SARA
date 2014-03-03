package orion.ms.sara;

import java.util.ArrayList;
import java.util.Date;

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
import android.view.View;
import android.widget.Button;
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
	private TextView textViewAuto = null;	
	private Button buttonSpeed = null;
	private Button buttonheading = null;
	private ImageButton buttonReco = null;
	
	private CheckBox speedAutoCheckBox = null;
	private CheckBox headingAutoCheckBox = null;
	
	private SeekBar speedBar = null;
	private SeekBar headingBar = null;
	private SeekBar timeBar = null; 
	
	private TextToSpeech tts = null;
	
	private LocationManager lm = null;
	private LocationListener ll = null;
	
	private String heading = "";
	private double headingAuto = 0;
	private double headingLastAuto = 0;
	private double headingTreshold = 4; 
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
        
    //TextView creation
    textViewSpeed = new TextView(this);
    textViewSpeed = (TextView) findViewById(R.id.speedView);
    textViewheading = new TextView(this);
    textViewheading = (TextView) findViewById(R.id.heading);
    textViewAuto = new TextView(this);
    textViewAuto = (TextView) findViewById(R.id.speak);
    textViewAuto.setText(getResources().getString(R.string.defaultmode));
    
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
    
    //TimeBar 
    timeBar = (SeekBar) findViewById(R.id.seekBarTime);
    timeBar.setOnSeekBarChangeListener(this);
    timeBar.setContentDescription("Time");
    
    //DefaultValue
    heading =  getResources().getString(R.string.nosatelite);
    speed =  getResources().getString(R.string.nosatelite);
    latitude =  getResources().getString(R.string.nosatelite);
    longitude =  getResources().getString(R.string.nosatelite);
	  
	//location manager creation
	lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	ll = new MyLocationListener();		
	//loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
   
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
    buttonSpeed= new Button(this);
    buttonSpeed = (Button) findViewById(R.id.buttonSpeed);
	buttonheading = new Button(this);
    buttonheading = (Button) findViewById(R.id.buttonheading);
	buttonReco= new ImageButton(this);
    buttonReco = (ImageButton) findViewById(R.id.buttonSpeak);

    // OnClickListener creation
    View.OnClickListener onclickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v== buttonSpeed){
				tts.speak(getResources().getString(R.string.speed)+ " : " + speed, TextToSpeech.QUEUE_ADD, null);
			}
			if (v== buttonheading){
				tts.speak(getResources().getString(R.string.heading)+ " : " + heading, TextToSpeech.QUEUE_ADD, null);
			}
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
	buttonSpeed.setOnClickListener(onclickListener);
	buttonheading.setOnClickListener(onclickListener);
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
	//public double arrondiLat(double val) {return (Math.floor(val*1000))/1000;}
	//public double arrondiLong(double val) {return (Math.floor(val*100))/100;}
	public double arrondiSpeed(double val) {return (Math.floor(val*10))/10;}
	//public double arrondiheading(double val) {return (Math.floor(val*100))/100;}

    public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			
			latitude = String.valueOf(loc.getLatitude());
			longitude = String.valueOf(loc.getLongitude());			
			speed = String.valueOf(arrondiSpeed(loc.getSpeed()*(1.94)));
			heading = String.valueOf((int)loc.getBearing());
			
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
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (seekBar.equals(speedBar)){
			speedTreshold = (double) progress/10;
			textViewAuto.setText(getResources().getString(R.string.speedtreshold) + " : " + Double.valueOf(speedTreshold).toString());
			seekBar.setContentDescription(Double.valueOf(speedTreshold).toString());
		}
		
		else if (seekBar.equals(headingBar)){
			headingTreshold = progress;
//			textViewAuto.setText("Seuil de cap : " + Double.valueOf(headingTreshold).toString());
			textViewAuto.setText(getResources().getString(R.string.headingtreshold) + "  : " + Integer.toString((int)headingTreshold));
			seekBar.setContentDescription(Integer.toString((int)headingTreshold));
		}
		
		else if (seekBar.equals(timeBar)){
			speedTimeTreshold = progress;
			textViewAuto.setText(getResources().getString(R.string.timetreshold) + "  : " + Integer.toString((int)speedTimeTreshold));
			seekBar.setContentDescription(Integer.toString((int)speedTimeTreshold));
		}
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		//seekBar.setContentDescription("Seuil vitesse auto : " + Double.valueOf(speedTreshold).toString());	
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		//textView5.setContentDescription("On stop Seuil vitesse auto : " + Double.valueOf(speedTreshold).toString());
		//tts.speak(" Le Seuil de la vitesse auto est réglé à : " + Double.valueOf(speedTreshold).toString(), TextToSpeech.QUEUE_ADD, null);
	}
 
}//end of Activity