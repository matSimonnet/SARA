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
import android.graphics.Point;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
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
	protected static final int RESULT_WAY = 6;

	private Intent intent_AutoSetting_activity;
	private Intent intent_Waypoint_activity;
	private Intent intent_GeneralSetting_activity;
	private Intent intent_Map_activity;
	private Intent intent_Way_activity;

	public static TextView textViewSpeed = null;
	public static TextView textViewheading = null;
	public static TextView textViewDistance = null;
	public static TextView textViewBearing = null;
	public static TextView textViewAccuracy = null;
	public static TextView ActivatedWayTextView = null;
	public static TextView ActivatedWayPointTextView = null;

	public static TextToSpeech tts = null;

	private ImageButton buttonReco = null;
	private Button instantButton = null;
	private static Button nextButton = null;
	private static Button previousButton = null;
	private static Button stopWaypointButton = null;
	private static Button stopWayButton = null;

	private LocationManager lm = null;
	public static MyLocationListener ll = null;

	public SharedPreferences settings;
	public static SharedPreferences.Editor editor;
	
	//Generating a number for a new waypoint's default name
	public static int lastNumberForInstantWaypoint = 0;
	//activating way point and way item name from the list
	private String actName = "Please selected a waypoint";
	private String actWayName = "No selected way";
    private static RelativeLayout rl;


	@Override
    public void onCreate(Bundle savedInstanceState) {

		Log.i("test", "///////// onCreate \\\\\\\\\\");
		
		super.onCreate(savedInstanceState);
		setTitle(R.string.navigation_title);

		
        mContext = this;
		LoadPref(); // Restore preferences
		initView();
		if(MyLocationListener.isWayActivated) {
			initActivatedWayView();
			Log.i("test", "way view");

		}
		else if(MyLocationListener.isWaypointActivated) {
			initActivatedWaypointView();
			Log.i("test", "waypoint view");
		}

		initLocationListener(); // 
		initIntent(); // create all intent
		initDate(); // initial time for auto announce
		initTTS(); // initial text to speech
		
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
		MyLocationListener.WaypointName = "";
		MyLocationListener.WaypointLatitude = 999;
		MyLocationListener.WaypointLongitude = 999;
		saveActivatedWaypoint();
        MyLocationListener.isInMain = false;
        MyLocationListener.isWayActivated = false;
        MyLocationListener.isWaypointActivated = false;
		editor.putBoolean("isWayActivated", MyLocationListener.isWayActivated);
		editor.putBoolean("isWaypointActivated", MyLocationListener.isWaypointActivated);
		editor.commit();
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
        			Log.i("Auto setting", "setting done");
        		}
        	break;
        	}// end of case
        	
        	case RESULT_WAY : {
        		if (resultCode == RESULT_OK && null != data) {
        			deleteView();
        			MyLocationListener.activatedWay = new ArrayList<WP>();
        			int length = data.getIntExtra("WayLength", -1);
        			for(int i = 0; i < length; i++) {        				
        				String WPName = data.getStringExtra("WPName" + i);
        				String WPLa = data.getStringExtra("WPLa" + i);
        				String WPLo = data.getStringExtra("WPLo" + i);
        				MyLocationListener.activatedWay.add(i, new WP(WPName, WPLa, WPLo));
        				Log.i("way list" , WPName + " " + WPLa + " " + WPLo);
        				
        				if(i == 0) { 
                   	        MyLocationListener.WaypointName = WPName;
                	        MyLocationListener.WaypointLatitude = Double.parseDouble(WPLa);
                	        MyLocationListener.WaypointLongitude = Double.parseDouble(WPLo);
        				}
        			}
        			MyLocationListener.activatedWayName = data.getStringExtra("WayName");
        			actWayName = data.getStringExtra("WayName");
           			saveActivatedWaypoint();
           			MyLocationListener.isWayActivated = true;
           			editor.putBoolean("isWayActivated", MyLocationListener.isWayActivated);
           			editor.commit();
			        tts.speak(MyLocationListener.activatedWayName + " is activated", TextToSpeech.QUEUE_FLUSH, null);
        			initActivatedWayView();
        		}
        	break;
        	}// end of case
        	
        	case RESULT_WAYPOINT : {
        		if (resultCode == RESULT_OK && null != data) {
        			deleteView();
        	        //activating way point name and location
        	        MyLocationListener.WaypointName = data.getStringExtra("actName");
        	        MyLocationListener.WaypointLatitude = data.getDoubleExtra("actLatitude", 999);
        	        MyLocationListener.WaypointLongitude = data.getDoubleExtra("actLongitude", 999);
        	        MyLocationListener.WPTreshold = data.getIntExtra("actTreshold", 1);

        			this.actName = data.getStringExtra("actName");
        			saveActivatedWaypoint();
				    initActivatedWaypointView();
				    MyLocationListener.isWaypointActivated = true;
           			editor.putBoolean("isWaypointActivated", MyLocationListener.isWaypointActivated);
           			editor.commit();
        			Log.i("receiveing name", actName);
        			Log.i("Waypoint Latitude", MyLocationListener.WaypointLatitude+"");
        			Log.i("Waypoint Latitude", MyLocationListener.WaypointLongitude+"");
        		}
        	break;
        	}// end of case
        	case RESULT_GENERAL_SETTING : {
        		if (resultCode == RESULT_OK && null != data) {
        			Log.i("General setting", "setting done");
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
			intent_Waypoint_activity.putExtra("actName", actName);
			Log.i("Activate item", actName);
			startActivityForResult(intent_Waypoint_activity, RESULT_WAYPOINT);
			break;
		case R.id.general_setting:
			startActivityForResult(intent_GeneralSetting_activity, RESULT_GENERAL_SETTING);
			break;
		case R.id.map:
			startActivityForResult(intent_Map_activity, RESULT_MAP);
			break;
		case R.id.way_menu:
			intent_Way_activity.putExtra("actWayName", actWayName);
			startActivityForResult(intent_Way_activity, RESULT_WAY);
			break;
		default:
			break;
		}
		return false;
	}

	public void LoadPref() {
		this.settings = getSharedPreferences(MyLocationListener.PREFS_NAME, 0);
		editor = settings.edit();
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
        MyLocationListener.isWayActivated = settings.getBoolean("isWayActivated", false);
        MyLocationListener.isWaypointActivated = settings.getBoolean("isWaypointActivated", false);

        MyLocationListener.WPTreshold = settings.getInt("WPTreshold", 1);

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
	  	
	  	//longitude array
	    int tresholdSize = settings.getInt("tresholdArray" +"_size", 0);  
	  	int treshold[] = new int[tresholdSize];  
	  	for(int i=0;i<tresholdSize;i++)  
	  		treshold[i] = settings.getInt("tresholdArray" + "_" + i, 0);
	  	
	  	//way point list
	  	List<WP> wList = new ArrayList<WP>();
	  	for(int i=0;i<nameSize;i++){
	  		WP tmp = new WP(name[i],latitude[i],longitude[i]);
	  		tmp.setTreshold(treshold[i]);
		  	wList.add(tmp);
	  	}
	  	
	  	WayPointActivity.wayPointList = wList;
	  	
	  	//way List
	  	List<Way> tempWayList = new ArrayList<Way>();
	  	//way name array
	  	int waySize = settings.getInt("nameWayArray" +"_size", 0);
	  	for(int i=0;i<waySize;i++){
	  		//ways
	  		String wayName = settings.getString("nameWayArray" + "_" + i, null);
	  		Way tempWay = new Way(wayName);
	  		int tempWaySize = settings.getInt(wayName+"_waySize", 0);
	  		for(int j=0;j<tempWaySize;j++){
	  			//way points
	  			String wpLoadName = settings.getString(wayName+"_wpNameArray" + "_" + j, null);
	  			WP tempLoad = WayActivity.findWPfromName(wpLoadName);
	  			//adding way points into way
	  			tempWay.addWPtoWay(tempLoad);
	  		}
	  		//adding ways into a way list
	  		tempWayList.add(tempWay);
	  	}
	  	
	  	WayActivity.wayList = tempWayList;
	}
	
	//save activated waypoint

	public static void saveActivatedWaypoint() {
	    editor.putString("WaypointLatitude", String.valueOf(MyLocationListener.WaypointLatitude));
	    editor.putString("WaypointLongitude", String.valueOf(MyLocationListener.WaypointLongitude));
	    editor.putString("WaypointName", MyLocationListener.WaypointName);
	    editor.commit();
	}

	//save preferences
	public void savePref(){
		this.settings = getSharedPreferences(MyLocationListener.PREFS_NAME, 0);
		
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
    public static void deleteView() {
    	if(ActivatedWayTextView != null) rl.removeView(ActivatedWayTextView);
    	if(ActivatedWayPointTextView != null) rl.removeView(ActivatedWayPointTextView);
    	if(previousButton != null) rl.removeView(previousButton);
    	if(nextButton != null) rl.removeView(nextButton);
    	if(stopWaypointButton != null) rl.removeView(stopWaypointButton);
    	if(stopWayButton != null) rl.removeView(stopWayButton);

    }
private void initActivatedWaypointView() {
    	
    	// find view in my resource
        rl = (RelativeLayout) findViewById(R.id.relativelayout);

        // add new text view
		ActivatedWayPointTextView = new TextView(this);
		ActivatedWayPointTextView.setId(5421);
		ActivatedWayPointTextView.setText(MyLocationListener.WaypointName);
		ActivatedWayPointTextView.setContentDescription(MyLocationListener.WaypointName + "was activated.");
		ActivatedWayPointTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textViewSpeed.getTextSize());
		ActivatedWayPointTextView.setGravity(Gravity.CENTER_HORIZONTAL);
		
		// new textview's rules
        LayoutParams textViewParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        textViewParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        ActivatedWayPointTextView.setLayoutParams(textViewParam);
        rl.addView(ActivatedWayPointTextView);
        
        // get width of screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        
        //add stop button
        stopWaypointButton = new Button(this);
        
        LayoutParams StopParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        StopParam.addRule(RelativeLayout.BELOW, ActivatedWayPointTextView.getId());
        StopParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        
        stopWaypointButton.setBackgroundResource(R.drawable.custom_btn_shakespeare);
        stopWaypointButton.setTextAppearance(this, R.style.btnStyleShakespeare);
        stopWaypointButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, instantButton.getTextSize());
        
        stopWaypointButton.setText("Stop Navigation");
        stopWaypointButton.setId(5621);
        stopWaypointButton.setWidth(width - rl.getPaddingLeft());
        stopWaypointButton.setLayoutParams(StopParam);
        rl.addView(stopWaypointButton);
        
        LayoutParams textViewSpeedParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        textViewSpeedParam.addRule(RelativeLayout.BELOW, stopWaypointButton.getId());
        textViewSpeed.setLayoutParams(textViewSpeedParam);
        
        View.OnClickListener onclickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
        		if(v == stopWaypointButton) {
    				MainActivity.tts.speak(getResources().getString(R.string.stop_navigation), TextToSpeech.QUEUE_FLUSH, null);
    				MyLocationListener.WaypointName = "";
    				MyLocationListener.WaypointLatitude = 999;
    				MyLocationListener.WaypointLongitude = 999;
           			saveActivatedWaypoint();
           			MyLocationListener.isWayActivated = false;
           			editor.putBoolean("isWayActivated", MyLocationListener.isWayActivated);
           			editor.commit();
           			deleteView();
    				Log.i("waypoint", "disactivate");        
    			}

			}
        };
        stopWaypointButton.setOnClickListener(onclickListener);
    }
    private void initActivatedWayView() {
    	
    	// find view in my resource
        rl = (RelativeLayout) findViewById(R.id.relativelayout);

        // add new text view
		ActivatedWayTextView = new TextView(this);
		ActivatedWayTextView.setId(8453);
		ActivatedWayTextView.setText(MyLocationListener.activatedWayName);
		ActivatedWayTextView.setContentDescription(MyLocationListener.activatedWayName + "is activated.");
		ActivatedWayTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textViewSpeed.getTextSize());
		ActivatedWayTextView.setGravity(Gravity.CENTER_HORIZONTAL);
		
		// new textview's rules
        LayoutParams textViewParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        textViewParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        ActivatedWayTextView.setLayoutParams(textViewParam);
        rl.addView(ActivatedWayTextView);
        
        // get width of screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        
        //add previous button
        previousButton = new Button(this);
        
        LayoutParams PreviousParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        PreviousParam.addRule(RelativeLayout.BELOW, ActivatedWayTextView.getId());
        PreviousParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        
        previousButton.setBackgroundResource(R.drawable.custom_btn_shakespeare);
        previousButton.setTextAppearance(this, R.style.btnStyleShakespeare);
        previousButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, instantButton.getTextSize());
        
        previousButton.setText(getResources().getString(R.string.previous));
        previousButton.setId(6743);
        previousButton.setWidth(width/3 - rl.getPaddingLeft());
        previousButton.setLayoutParams(PreviousParam);
        rl.addView(previousButton);
        
        //add stop button
        stopWayButton = new Button(this);
        
        LayoutParams StopParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        StopParam.addRule(RelativeLayout.BELOW, ActivatedWayTextView.getId());
        StopParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        
        stopWayButton.setBackgroundResource(R.drawable.custom_btn_shakespeare);
        stopWayButton.setTextAppearance(this, R.style.btnStyleShakespeare);
        stopWayButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, instantButton.getTextSize());
        
        stopWayButton.setText(getResources().getString(R.string.stop_current_navigation));
        stopWayButton.setId(7881);
        stopWayButton.setWidth(width/3 - rl.getPaddingLeft());
        stopWayButton.setLayoutParams(StopParam);
        rl.addView(stopWayButton);
        
        // add next button
        nextButton = new Button(this);
        
        LayoutParams NextParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        NextParam.addRule(RelativeLayout.BELOW, ActivatedWayTextView.getId());
        NextParam.addRule(RelativeLayout.RIGHT_OF, previousButton.getId());
        NextParam.addRule(RelativeLayout.LEFT_OF, stopWayButton.getId());

        nextButton.setBackgroundResource(R.drawable.custom_btn_shakespeare);
        nextButton.setTextAppearance(this, R.style.btnStyleShakespeare);
        nextButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, instantButton.getTextSize());
        
        nextButton.setText(getResources().getString(R.string.next));
        nextButton.setId(3743);
        nextButton.setWidth(LayoutParams.MATCH_PARENT);
        nextButton.setLayoutParams(NextParam);
        rl.addView(nextButton);
        
        LayoutParams textViewSpeedParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        textViewSpeedParam.addRule(RelativeLayout.BELOW, previousButton.getId());
        textViewSpeed.setLayoutParams(textViewSpeedParam);
        
        View.OnClickListener onclickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
        		if(v == previousButton) {
        			MyLocationListener.previousWaypoint();
        		}
        		if(v == nextButton) {
        			MyLocationListener.nextWaypoint();
        		}
        		if(v == stopWayButton) {
					MainActivity.tts.speak(getResources().getString(R.string.stop_navigation), TextToSpeech.QUEUE_FLUSH, null);
					MyLocationListener.WaypointName = "";
					MyLocationListener.WaypointLatitude = 999.0;
					MyLocationListener.WaypointLongitude = 999.0;
					MyLocationListener.activatedIndex = 0;
	    	        deleteView();
	    	        saveActivatedWaypoint();
					MyLocationListener.isWayActivated = false;
           			editor.putBoolean("isWayActivated", MyLocationListener.isWayActivated);
           			editor.commit();
        		}
			}
        };
        previousButton.setOnClickListener(onclickListener);
        nextButton.setOnClickListener(onclickListener);		
        stopWayButton.setOnClickListener(onclickListener);		

    }
    private void initView() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        
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
        
        //"Instantly create a waypoint" button
        instantButton = (Button) findViewById(R.id.instantButton);

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
					        tts.speak("H"+lastNumberForInstantWaypoint+getResources().getString(R.string.is_saved_here)+".", TextToSpeech.QUEUE_ADD, null);
					        Toast.makeText(MainActivity.this, "H"+lastNumberForInstantWaypoint+ getResources().getString(R.string.is_saved_here)+".", Toast.LENGTH_SHORT).show();
					        for(int i=0;i<WayPointActivity.wayPointList.size();i++){
					        	Log.i("waypoint list", "item "+i+" : "+WayPointActivity.wayPointList.get(i).getName());
					        }
					        //save value
					        savePref();
				        }
				        else{
				        	//Notify
					        tts.speak(getResources().getString(R.string.waypoint_alreadysaved) + ".", TextToSpeech.QUEUE_FLUSH, null);
					        Toast.makeText(MainActivity.this, getResources().getString(R.string.waypoint_alreadysaved) + ".", Toast.LENGTH_SHORT).show();
				        }
					}//end if in if-else
					else{
						//GPS not available
						Toast.makeText(MainActivity.this,getResources().getString(R.string.no_satellite) , Toast.LENGTH_SHORT).show();
						tts.speak(getResources().getString(R.string.no_satellite), TextToSpeech.QUEUE_FLUSH, null);
					}
			    }
	        }// end of on click		
		}; // end of new View.LocationListener	

		// button activation
		buttonReco.setOnClickListener(onclickListener);
		instantButton.setOnClickListener(onclickListener);
    }
    
    private void initLocationListener() {
        MyLocationListener.isInMain = true;
        //location manager creation
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ll = new MyLocationListener();		
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
    }
    
    private void initIntent() {
        //intent creation
		intent_Waypoint_activity = new Intent(MainActivity.this,WayPointActivity.class);
		intent_AutoSetting_activity = new Intent(MainActivity.this,AutoSettingActivity.class);
		intent_GeneralSetting_activity = new Intent(MainActivity.this,GeneralSettingActivity.class);
		intent_Map_activity = new Intent(MainActivity.this,MyMapActivity.class);
		intent_Way_activity = new Intent(MainActivity.this,WayActivity.class);
    }
    
    private void initDate() {
        //dates creation
        MyLocationListener.speedBefore = new Date();
        MyLocationListener.headingBefore = new Date();
        MyLocationListener.distanceBefore = new Date();
        MyLocationListener.bearingBefore = new Date();
    }
    
    private void initTTS() {
		 //OnInitListener Creation
        OnInitListener onInitListener = new OnInitListener() {
        	@Override
        	public void onInit(int status) {
        	}
        };

        // Text to speech creation
        if(tts == null) {
        	tts = new TextToSpeech(this, onInitListener);
        }
    }

}//end of Activity