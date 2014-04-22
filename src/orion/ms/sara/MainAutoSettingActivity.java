package orion.ms.sara;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainAutoSettingActivity extends Activity {

	private Button speedButton = null;
	private Button headingButton = null;
	private Button bearingButton = null;
	private Button distanceButton = null;
	private Button accuracyButton = null;
	
	private Intent intentAutoSpeedActivity = null;
	protected static final int RESULT_AUTOSPEED = 432;
	
	private Intent intentAutoHeadingActivity = null;
	protected static final int RESULT_AUTOHEADING = 962;

	
	private Intent intentAutoBearingActivity = null;
	protected static final int RESULT_AUTOBEARING = 825;
	
	private Intent intentAutoDistanceActivity = null;
	protected static final int RESULT_AUTODISTANCE = 764;

	private Intent intentAutoAccuracyActivity = null;
	protected static final int RESULT_AUTOACCURACY = 431;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_autosetting);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		intentAutoSpeedActivity = new Intent(MainAutoSettingActivity.this,AutoSpeedActivity.class);
		intentAutoHeadingActivity = new Intent(MainAutoSettingActivity.this,AutoHeadingActivity.class);
		intentAutoBearingActivity = new Intent(MainAutoSettingActivity.this,AutoBearingActivity.class);
		intentAutoDistanceActivity = new Intent(MainAutoSettingActivity.this,AutoDistanceActivity.class);
		intentAutoAccuracyActivity = new Intent(MainAutoSettingActivity.this,AutoAccuracyActivity.class);
		
		this.speedButton = (Button) findViewById(R.id.SpeedSettingButton);
		this.speedButton.setContentDescription(getResources().getString(R.string.speedsetting));
		
		this.headingButton = (Button) findViewById(R.id.HeadingSettingButton);
		this.headingButton.setContentDescription(getResources().getString(R.string.headingsetting));

		this.bearingButton = (Button) findViewById(R.id.BearingSettingButton);
		this.bearingButton.setContentDescription(getResources().getString(R.string.bearingsetting));

		this.distanceButton = (Button) findViewById(R.id.DistanceSettingButton);
		this.distanceButton.setContentDescription(getResources().getString(R.string.distancesetting));

		this.accuracyButton = (Button) findViewById(R.id.AccuracySettingButton);
		this.accuracyButton.setContentDescription(getResources().getString(R.string.accuracysetting));
		
	    View.OnClickListener onclickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(v == speedButton) {
		            startActivityForResult(intentAutoSpeedActivity, RESULT_AUTOSPEED);					
				}
				
				if(v == headingButton) {
		            startActivityForResult(intentAutoHeadingActivity, RESULT_AUTOHEADING);					
				}
				
				if(v == bearingButton) {
		            startActivityForResult(intentAutoBearingActivity, RESULT_AUTOBEARING);					
				}
				
				if(v == distanceButton) {
		            startActivityForResult(intentAutoDistanceActivity, RESULT_AUTODISTANCE);					
				}
				
				if(v == accuracyButton) {
		            startActivityForResult(intentAutoAccuracyActivity, RESULT_AUTOACCURACY);					
				}
			}
	    };
	    this.speedButton.setOnClickListener(onclickListener);
	    this.headingButton.setOnClickListener(onclickListener);
	    this.bearingButton.setOnClickListener(onclickListener);
	    this.distanceButton.setOnClickListener(onclickListener);
	    this.accuracyButton.setOnClickListener(onclickListener);

	} // end of oncreate();
	
//  action bar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main_autosetting, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem Item){
		switch (Item.getItemId()) {
		case R.id.navigation:
			finish();
			break;
		default:
			break;
		}
		return false;
	}
	
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 super.onActivityResult(requestCode, resultCode, data);
	        
	     switch (requestCode) {
	     	case RESULT_AUTOSPEED : {
	     		if (resultCode == RESULT_OK && null != data) {
	     			MyLocationListener.speedLastAuto = data.getDoubleExtra("speedLastAuto", 0.0);
	     			MyLocationListener.speedTreshold = data.getDoubleExtra("speedTreshold", 1.0);
	        		MyLocationListener.speedTimeTreshold = data.getLongExtra("speedTimeTreshold", 5);
	        		MyLocationListener.isAutoSpeed = data.getBooleanExtra("isAutoSpeed", true);
	        		
	        		Log.i("SpeedTeshold",MyLocationListener.speedTreshold+"");
	        		Log.i("SpeedTimeTeshold",MyLocationListener.speedTimeTreshold+"");
	        		Log.i("isAutoSpeed",MyLocationListener.isAutoSpeed+"");
	        	}
	        	break;
	        }// end of case
	     	case RESULT_AUTOHEADING : {
	     		if (resultCode == RESULT_OK && null != data) {
        			MyLocationListener.headingTreshold = data.getDoubleExtra("headingTreshold", 10.0);
        			MyLocationListener.headingTimeTreshold = data.getLongExtra("headingTimeTreshold", 5);
        			MyLocationListener.isAutoHeading = data.getBooleanExtra("isAutoHeading", true);
	        		
	        		Log.i("HeadingTeshold",MyLocationListener.headingTreshold+"");
	        		Log.i("HeadingTimeTeshold",MyLocationListener.headingTimeTreshold+"");
	        		Log.i("isAutoHeaing",MyLocationListener.isAutoHeading+"");

	        	}
	        	break;
	        }// end of case
	     	case RESULT_AUTOBEARING : {
	     		if (resultCode == RESULT_OK && null != data) {
	     			MyLocationListener.bearingLastAuto = data.getDoubleExtra("bearingLastAuto", 0.0);
        			MyLocationListener.bearingTreshold = data.getDoubleExtra("bearingTreshold", 10.0);
        			MyLocationListener.bearingTimeTreshold = data.getLongExtra("bearingTimeTreshold", 5);
        			MyLocationListener.isAutoBearing = data.getBooleanExtra("isAutoBearing", true);
	        		
	        		Log.i("BearingTeshold",MyLocationListener.bearingTreshold+"");
	        		Log.i("BearingTimeTeshold",MyLocationListener.bearingTimeTreshold+"");
	        		Log.i("isAutoBearing",MyLocationListener.isAutoBearing+"");
	        	}
	        	break;
	        }// end of case
	     	case RESULT_AUTODISTANCE : {
	     		if (resultCode == RESULT_OK && null != data) {
        			MyLocationListener.distanceLastAuto = data.getDoubleExtra("distanceLastAuto", 0.0);
        			MyLocationListener.distanceTimeTreshold = data.getLongExtra("distanceTimeTreshold", 5);
        			MyLocationListener.isAutoDistance = data.getBooleanExtra("isAutoDistance", true);
        			
	        		Log.i("DistanceTimeTeshold",MyLocationListener.distanceTimeTreshold+"");
	        		Log.i("isAutoDistance",MyLocationListener.isAutoDistance+"");
	        	}
	        	break;
	        }// end of case
	     	case RESULT_AUTOACCURACY : {
	     		if (resultCode == RESULT_OK && null != data) {
        			MyLocationListener.accuracyTimeTreshold = data.getLongExtra("accuracyTimeTreshold", 5);
        			MyLocationListener.isAutoAccuracy = data.getBooleanExtra("isAutoAccuracy", true);
        			
	        		Log.i("AccuracyTimeTeshold",MyLocationListener.accuracyTimeTreshold+"");
	        		Log.i("isAutoAccuracy",MyLocationListener.isAutoAccuracy+"");
	        	}
	        	break;
	        }// end of case
	     }// end of switch 
	 }// end of on Activity result 
}
