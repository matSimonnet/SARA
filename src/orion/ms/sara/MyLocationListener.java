package orion.ms.sara;

import java.util.ArrayList;
import java.util.Date;

import org.mapsforge.core.GeoPoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

public class MyLocationListener extends Activity implements LocationListener {

	public static final String PREFS_NAME = "MyPrefsFile";
	public static String speed = "";
	public static String heading = "";
	public static String DistanceToCurrentWaypoint = "";
	public static String BearingToCurrentWaypoint = "";
	public static String accuracy = "";

	public static String WaypointName = "";
	public static double WaypointLatitude = 999;
	public static double WaypointLongitude = 999;

	public static String currentLatitude = "";
	public static String currentLongitude = "";

	public static float distance[] = new float[1];

	public static Date speedNow = null;
	public static Date speedBefore = new Date();

	public static Date headingNow = null;
	public static Date headingBefore = new Date();

	public Date distanceNow = null;
	public static Date distanceBefore = new Date();

	public static Date bearingNow = null;
	public static Date bearingBefore = new Date();

	public static Date accuracyNow = null;
	public static Date accuracyBefore = new Date();

	public static boolean isAutoSpeed = true;
	public static boolean isAutoHeading = true;
	public static boolean isAutoDistance = true;
	public static boolean isAutoBearing = true;
	public static boolean isAutoAccuracy = true;

	public static double speedAuto = 0.0;
	public static double headingAuto = 0.0;
	public static double distanceAuto = 0.0;
	public static double bearingAuto = 0.0;
	public static double accuracyAuto = 0;

	public static double speedLastAuto = 0.0;
	public static double headingLastAuto = 0.0;
	public static double distanceLastAuto = 0.0;
	public static double bearingLastAuto = 0.0;
	public static double accuracyLastAuto = 0.0;

	public static double speedTreshold = 1.0;
	public static double headingTreshold = 10.0;
	public static double distanceTreshold = 0.0;
	public static double bearingTreshold = 10.0;

	public static long speedTimeTreshold = 10;
	public static long headingTimeTreshold = 10;
	public static long distanceTimeTreshold = 5;
	public static long bearingTimeTreshold = 10;
	public static long accuracyTimeTreshold = 10;

	private String speedUnit = "";
	private String distanceUnit = "";
	private String bearingUnit = "";
	private String headingUnit = "";
	private String accuracyUnit = "";
	
	public static boolean isWaypointActivated = false;
	private boolean isMorePrecise8Announced = false;
	private boolean isMorePrecise16Announced = false;
	private boolean isLessPrecise16Announced = false;

	public static boolean isKnotsSelected = true;
	public static boolean isKmPerHrSelected = false;
	
	public static boolean isPortandstarboardSelected = true;
	public static boolean isCardinalSelected = false;
	
	public static boolean isKilometreSelected = true;
	public static boolean isNMSelected = false;
	
	public static int WPTreshold = 1;

	public static boolean isInMain = true;

	public static Resources resource = MainActivity.getContext().getResources();
	
	public static GeoPoint geoPoint[] = new GeoPoint[0];
	public static boolean isStartedDisplay = false;
	public static boolean isAutoDrawTrack = false;
	
	public static ArrayList<WP> activatedWay = new ArrayList<WP>();
	public static String activatedWayName;
	public static boolean isWayActivated = false;
	public static int activatedIndex = 0;
	public static float[] distanceWaypoint = new float[1];
	
	private static AlertDialog.Builder alertDialog;

	@Override
	public void onLocationChanged(Location loc) {
		
		if (isWayActivated && activatedWay.size() != 0) {
			Log.i("WPTreshold", WPTreshold + "");
			String activatedName = activatedWay.get(activatedIndex).getName();
			Double activatedLa = Double.parseDouble(activatedWay.get(activatedIndex).getLatitude());
			Double activatedLo = Double.parseDouble(activatedWay.get(activatedIndex).getLongitude());

			Location.distanceBetween(activatedLa, activatedLo, loc.getLatitude(), loc.getLongitude(), distanceWaypoint);
			Log.i("WP distance", distanceWaypoint[0] + "");

			if(distanceWaypoint[0] <= WayActivity.findWPfromName(activatedName).getTreshold()) { // in meters
				if(activatedIndex == activatedWay.size() - 1) {
					/*MainActivity.tts.speak(getResources().getString(R.string.You_have_reached_the_last_way_point) + " : " + activatedWayName + " " +  
							getResources().getString(R.string.is_disactivated) + " . ", TextToSpeech.QUEUE_FLUSH, null);*/
					MainActivity.tts.speak("dernier point de route atteint", TextToSpeech.QUEUE_FLUSH, null);
					WaypointName = "";
	    	        WaypointLatitude = 999.0;
	    	        WaypointLongitude = 999.0;
	    	        isWayActivated = false;
	    	        activatedIndex = 0;
	    	        MainActivity.deleteView();
	    	        updatePin();
	    	        saveActivatedWaypoint();
				}
				else {
					/*MainActivity.tts.speak(getResources().getString(R.string.You_have_reached_the_last_way_point) + " : " 
							+ WaypointName + " . ", TextToSpeech.QUEUE_FLUSH, null);*/
					MainActivity.tts.speak( WaypointName + " est atteint ", TextToSpeech.QUEUE_FLUSH, null);		
					activatedIndex = activatedIndex + 1;
					WaypointName = activatedWay.get(activatedIndex).getName();
					WaypointLatitude = Double.parseDouble(activatedWay.get(activatedIndex).getLatitude());
    	        	WaypointLongitude = Double.parseDouble(activatedWay.get(activatedIndex).getLongitude());
    	        	//MainActivity.tts.speak(WaypointName + getResources().getString(R.string.is_activated) + " . ", TextToSpeech.QUEUE_FLUSH, null);
    	        	//MainActivity.tts.speak(WaypointName + " est activé ", TextToSpeech.QUEUE_FLUSH, null);
	    	        updatePin();
	    	        saveActivatedWaypoint();
				}
			}
		}
		
		if (!isWayActivated && isWaypointActivated()) {
			Location.distanceBetween(WaypointLatitude, WaypointLongitude, loc.getLatitude(), loc.getLongitude(), distanceWaypoint);
			if(distanceWaypoint[0] <= WPTreshold) { // in meters
//				MainActivity.tts.speak(WaypointName + " " + getResources().getString(R.string.reached) + " . "  
//						+  WaypointName + " " + getResources().getString(R.string.is_activated) + " . ", TextToSpeech.QUEUE_FLUSH, null);
				MainActivity.tts.speak(WaypointName + " atteint" + " . "  , TextToSpeech.QUEUE_FLUSH, null);
				WaypointName = "";
				WaypointLatitude = 999;
				WaypointLongitude = 999;
    	        updatePin();
       			saveActivatedWaypoint();
				Log.i("waypoint", "disactivate");
			}
		}

		isWaypointActivated();
		currentLatitude = String.valueOf(loc.getLatitude());
		currentLongitude = String.valueOf(loc.getLongitude());

		// get information from the current location
		speed = getSpeed(loc);
		speedUnit = getSpeedUnit();

		heading = getHeading(loc);
		headingUnit = getHeadingUnit();

		BearingToCurrentWaypoint = getBearing(loc);
		bearingUnit = getBearingUnit(loc);

		DistanceToCurrentWaypoint = getDistance(loc);
		Log.i("text distance", DistanceToCurrentWaypoint);
		distanceUnit = getDistanceUnit();

		accuracy = getAccuracy(loc);
		accuracyUnit = getAccuracyUnit();
		
		createPoint(Double.parseDouble(currentLatitude), Double.parseDouble(currentLongitude));
		drawTrack();
		centerLocation();

		// set all text view
		Utils.setSpeedTextView(speed, speedUnit);
		Utils.setHeadingTextView(heading, headingUnit);
		Utils.setBearingTextView(BearingToCurrentWaypoint, bearingUnit);
		Utils.setDistanceTextView(DistanceToCurrentWaypoint, distanceUnit);
		Utils.setAccuracyTextView(accuracy, accuracyUnit);

		// set the description of each text view for using with talk back
		Utils.setSpeedTextViewDescription(speed, speedUnit);
		Utils.setHeadingTextViewDescription(heading);
		Utils.setBearingTextViewDescription(BearingToCurrentWaypoint, bearingUnit);
		Utils.setDistanceTextViewDescription(DistanceToCurrentWaypoint, distanceUnit);
		Utils.setAccuracyTextViewDescription(accuracy);

		if (isAutoBearing && isWaypointActivated) {
			if(isCardinalSelected) {
				bearingAuto = Integer.parseInt(Utils._Bearing(loc.getLatitude(), loc.getLongitude(), WaypointLatitude, WaypointLongitude));
				bearingNow = new Date();
			}
			else { // select port/starboard
				String cardinal_bearing = Utils._Bearing(loc.getLatitude(), loc.getLongitude(), WaypointLatitude, WaypointLongitude);
				bearingAuto = Integer.valueOf(cardinal_bearing) - (int)loc.getBearing();
				bearingNow = new Date();
				if (bearingAuto < 0) {
					bearingAuto = bearingAuto + 360;
				}
			}
			int bearingDiff = java.lang.Math.abs((int) bearingLastAuto - (int) bearingAuto);
			if (((bearingDiff > bearingTreshold))
					&& ((bearingNow.getTime() - bearingBefore.getTime()) > bearingTimeTreshold * 1000)) {
				
				Utils.speakBearingTextView(BearingToCurrentWaypoint, bearingUnit);
				bearingLastAuto = bearingAuto;
				bearingBefore = new Date();
				Log.i("bearing", BearingToCurrentWaypoint);
			}
		}// end of if bearingAutoCheck...

		if (isAutoDistance && isWaypointActivated) {
			distanceAuto = Double.parseDouble(DistanceToCurrentWaypoint);
			distanceNow = new Date();
			distanceTreshold = getDistanceTreshold();

			if (((distanceAuto < distanceLastAuto - distanceTreshold) || (distanceAuto > distanceLastAuto + distanceTreshold))
					&& ((distanceNow.getTime() - distanceBefore.getTime()) > distanceTimeTreshold * 1000)) {

				Utils.speakDistanceTextView(DistanceToCurrentWaypoint, distanceUnit);
				distanceLastAuto = distanceAuto;
				distanceBefore = new Date();
				Log.i("distance", DistanceToCurrentWaypoint);
			}
		}// end of if distanceAutoCheck...

		if (isAutoSpeed) {
			speedAuto = Double.parseDouble(speed);
			speedNow = new Date();
			if (((speedAuto < speedLastAuto - speedTreshold) || (speedAuto > speedLastAuto + speedTreshold))
					&& ((speedNow.getTime() - speedBefore.getTime()) > speedTimeTreshold * 1000)) {

				Utils.speakSpeedTextView(speed, speedUnit);
				speedLastAuto = speedAuto;
				speedBefore = new Date();
				Log.i("speed", speed);
			}
		}// end of if speedAutoCheck...

		if (isAutoHeading) {
			headingAuto = Integer.parseInt(heading);
			headingNow = new Date();

			int headingDiff = java.lang.Math.abs((int) headingLastAuto - (int) headingAuto);
			if (headingDiff > 180)  headingDiff = java.lang.Math.abs(headingDiff - 360);
			
			if (((headingDiff > headingTreshold))
					&& ((headingNow.getTime() - headingBefore.getTime()) > headingTimeTreshold * 1000)) {

				Utils.speakHeadingTextView(heading, headingUnit);
				headingLastAuto = headingAuto;
				headingBefore = new Date();
				Log.i("heading", heading);
			}
		}// end of if headingAutoCheck...

		if (isAutoAccuracy) {
			accuracyAuto = loc.getAccuracy();
			accuracyNow = new Date();
			if (isMorePrecise8Announced == false && accuracyAuto < 8 
					&& (accuracyNow.getTime() - accuracyBefore.getTime()) > accuracyTimeTreshold * 1000) {
				
				Utils.speakAccuracyTextView(accuracy, accuracyUnit);
				accuracyBefore = new Date();
				Log.i("accuracy", accuracy);
				isMorePrecise8Announced = true;
				isMorePrecise16Announced = false;
				isLessPrecise16Announced = false;
			} 
			else if (isMorePrecise16Announced == false && accuracyAuto >= 8 && accuracyAuto < 16 
					&& (accuracyNow.getTime() - accuracyBefore.getTime()) > accuracyTimeTreshold * 1000) {

				Utils.speakAccuracyTextView(accuracy, accuracyUnit);
				accuracyBefore = new Date();
				Log.i("accuracy", accuracy);
				isMorePrecise8Announced = false;
				isMorePrecise16Announced = true;
				isLessPrecise16Announced = false;
			} else if (isLessPrecise16Announced == false && accuracyAuto >= 16
					&& (accuracyNow.getTime() - accuracyBefore.getTime()) > accuracyTimeTreshold * 1000) {
				
				Utils.speakAccuracyTextView(accuracy, accuracyUnit);
				accuracyBefore = new Date();
				Log.i("accuracy", accuracy);
				isMorePrecise8Announced = false;
				isMorePrecise16Announced = false;
				isLessPrecise16Announced = true;
			}
		}// end of if AccuracyAutoCheck...
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.GPS_disable) + " . ", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.GPS_enable) + " . ", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// Log.i("LocationListener","onStatusChanged");
	}

	public String getSpeedUnit() {
		String speedUnit = "";
		if (isKnotsSelected) {
			speedUnit = resource.getString(R.string.knots);
		}
		if (isKmPerHrSelected) {
			speedUnit = resource.getString(R.string.kmperh);
		}
		return speedUnit;
	}

	public String getSpeed(Location loc) {
		String speed = "";
		if (isKnotsSelected) {
			speed = String.valueOf(Utils.roundSpeed(loc.getSpeed() * (1.944)));
		}
		if (isKmPerHrSelected) {
			speed = String.valueOf(Utils.roundSpeed(loc.getSpeed() * (3.6)));
		}
		return speed;
	}

	public String getHeading(Location loc) {
		return String.valueOf((int) loc.getBearing());
	}

	public String getHeadingUnit() {
		return resource.getString(R.string.deg);
	}

	public String getAccuracy(Location loc) {
		return String.valueOf((int) loc.getAccuracy());
	}

	public String getAccuracyUnit() {
		return resource.getString(R.string.m);
	}

	public String getDistance(Location loc) {
		if (WaypointLatitude == 999 || WaypointLongitude == 999) {
			return resource.getString(R.string.nowaypoint);
		}
		if (isNMSelected) {
			Location.distanceBetween(WaypointLatitude, WaypointLongitude, loc.getLatitude(), loc.getLongitude(), distance);
			return String.valueOf(Utils.roundDistance(((distance[0] / 1000) * 0.54)));
		}
		else { // kilometre and Metre
			Location.distanceBetween(WaypointLatitude, WaypointLongitude, loc.getLatitude(), loc.getLongitude(), distance);
			return Utils.precisionDistance(distance[0] / 1000);
		}
	}

	public String getDistanceUnit() {
		if (WaypointLatitude == 999 || WaypointLongitude == 999) {
			return "";
		}
		if (isNMSelected) {
			return resource.getString(R.string.nm);
		}
		else {
			if (distance[0] < 1000) return resource.getString(R.string.m);
			else return resource.getString(R.string.km);
		}	
	}

	public String getBearing(Location loc) {
		if (WaypointLatitude == 999 || WaypointLongitude == 999) {
			return resource.getString(R.string.nowaypoint);
		}
		if(isCardinalSelected) {
			return Utils._Bearing(loc.getLatitude(), loc.getLongitude(), WaypointLatitude, WaypointLongitude);
		}
		else { // port&starboard selected
			String cardinal_bearing = Utils._Bearing(loc.getLatitude(), loc.getLongitude(), WaypointLatitude, WaypointLongitude);
			int angle_tmp = Integer.valueOf(cardinal_bearing) - (int)loc.getBearing();
			if (angle_tmp < 0) {
				angle_tmp = angle_tmp + 360;
			}

			if (angle_tmp < 180) return String.valueOf(angle_tmp);
			else return String.valueOf(360 - angle_tmp);
		}
	}

	public String getBearingUnit(Location loc) {
		if (WaypointLatitude == 999 || WaypointLongitude == 999) {
			return "";
		}
		if(isCardinalSelected) {
			return resource.getString(R.string.deg);
		}
		else { // port&starboard selected
			String cardinal_bearing = Utils._Bearing(loc.getLatitude(), loc.getLongitude(), WaypointLatitude, WaypointLongitude);
			int angle_tmp = Integer.valueOf(cardinal_bearing) - (int)loc.getBearing();
			if (angle_tmp < 0) {
				angle_tmp = angle_tmp + 360;
			}

			if (angle_tmp < 180) return resource.getString(R.string.onstarboard);
			else return resource.getString(R.string.onport);
		}			
	}
	
	public static boolean isWaypointActivated() {
		if (WaypointLatitude == 999 || WaypointLongitude == 999) {
			isWaypointActivated = false;
			return false;
		}
		else {
			isWaypointActivated = true;
			return true;
		}
	}
	
	public double getSpeedAuto(Location loc) {
		if (isKnotsSelected) {
			return Utils.roundSpeed(loc.getSpeed() * (1.945));
		}
		else { // km/h
			return Utils.roundSpeed(loc.getSpeed() * (3.6));
		}
	}
	
	public int getDistanceTreshold() {
		if (distanceAuto < 10) return 1;
		if (distanceAuto < 50 && distanceAuto >= 10) return 5;
		if (distanceAuto < 100 && distanceAuto >= 50) return 10;
		if (distanceAuto < 500 && distanceAuto >= 100) return 50;
		if (distanceAuto < 1000 && distanceAuto >= 500) return 100;
		if (distanceAuto < 5000 && distanceAuto >= 1000) return 500;
		if (distanceAuto < 10000 && distanceAuto >= 5000) return 1000;
		if (distanceAuto < 50000 && distanceAuto >= 100000) return 5000;
		else return 10000;
	}
	
	
	public void createPoint(double latitude, double longitude) {
		if(isStartedDisplay) {
			int newsize = geoPoint.length + 1;
			GeoPoint[] tmp = new GeoPoint[newsize];
		
			for(int i = 0; i < geoPoint.length; i++) {
				tmp[i] = geoPoint[i];
			}
			tmp[newsize-1] = new GeoPoint(latitude, longitude);
			geoPoint = new GeoPoint[newsize];	
			for(int i = 0; i < geoPoint.length; i++) {
				geoPoint[i] = tmp[i];
			}
		
			Log.i("add geo", latitude + " " + longitude);
		}
	}
	public void drawTrack() {
		if(isAutoDrawTrack) {
			if(isStartedDisplay) {
				MyMapActivity.drawPath();
			}
			MyMapActivity.drawHeading(Integer.parseInt(heading));
		}
	}
	public void centerLocation() {
		if(isAutoDrawTrack) {
			MyMapActivity.centerLocation();
		}
	}
	
	public static void updatePin() {
		if(isAutoDrawTrack) {
			MyMapActivity.loadWaypoint();
		}
	}
	public static void saveActivatedWaypoint() {
	    MainActivity.editor.putString("WaypointLatitude", String.valueOf(MyLocationListener.WaypointLatitude));
	    MainActivity.editor.putString("WaypointLongitude", String.valueOf(MyLocationListener.WaypointLongitude));
	    MainActivity.editor.putString("WaypointName", MyLocationListener.WaypointName);
	    MainActivity.editor.commit();
	}
	public static void previousWaypoint() {
		if(activatedIndex == 0) {
			alertDialog = new AlertDialog.Builder(MainActivity.getContext());
			alertDialog.setTitle(resource.getString(R.string.This_is_the_start_waypoint_of) + " :  " + activatedWayName);
			alertDialog.setIcon(android.R.drawable.presence_busy);
			alertDialog.setCancelable(true);
			alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			alertDialog.show();
		}
		else {
			activatedIndex = activatedIndex - 1;
			WaypointName = activatedWay.get(activatedIndex).getName();
			WaypointLatitude = Double.parseDouble(activatedWay.get(activatedIndex).getLatitude());
			WaypointLongitude = Double.parseDouble(activatedWay.get(activatedIndex).getLongitude());
			MainActivity.tts.speak(WaypointName + " "  + resource.getString(R.string.is_activated), TextToSpeech.QUEUE_FLUSH, null);
			updatePin();
			saveActivatedWaypoint();
		}
	}
	
	public static void nextWaypoint() {
		if(activatedIndex == activatedWay.size() - 1) {
			alertDialog = new AlertDialog.Builder(MainActivity.getContext());
			alertDialog.setTitle(resource.getString(R.string.This_is_the_end_waypoint_of) + "  " + activatedWayName);
			alertDialog.setIcon(android.R.drawable.presence_busy);
			alertDialog.setCancelable(true);
			alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			alertDialog.show();
		}
		else {
			activatedIndex = activatedIndex + 1;
			WaypointName = activatedWay.get(activatedIndex).getName();
			WaypointLatitude = Double.parseDouble(activatedWay.get(activatedIndex).getLatitude());
	    	WaypointLongitude = Double.parseDouble(activatedWay.get(activatedIndex).getLongitude());
	    	MainActivity.tts.speak(WaypointName +  resource.getString(R.string.is_activated), TextToSpeech.QUEUE_FLUSH, null);
	        updatePin();
	        saveActivatedWaypoint();
		}
	}


	
}