package orion.ms.sara;

import java.util.Date;

import android.app.Activity;
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
	public static double accuracyLastAuto = 0;

	public static double speedTreshold = 1.0;
	public static double headingTreshold = 10.0;
	public static double distanceTreshold = 0.0;
	public static double bearingTreshold = 10.0;

	public static long speedTimeTreshold = 5;
	public static long headingTimeTreshold = 5;
	public static long distanceTimeTreshold = 5;
	public static long bearingTimeTreshold = 5;
	public static long accuracyTimeTreshold = 5;

	private String speedUnit = "";
	private String distanceUnit = "";
	private String bearingUnit = "";
	private String headingUnit = "";
	private String accuracyUnit = "";
	private boolean isWaypointActivated = false;

	private boolean isMorePrecise5Announced = false;
	private boolean isMorePrecise10Announced = false;
	private boolean isLessPrecise10Announced = false;

	public static boolean isKnotsSelected = true;
	public static boolean isKmPerHrSelected = false;
	
	public static boolean isPortandstarboardSelected = true;
	public static boolean isCardinalSelected = false;


	public static boolean isInMain = true;

	public Resources resource = MainActivity.getContext().getResources();

	@Override
	public void onLocationChanged(Location loc) {

		this.isWaypointActivated();
		currentLatitude = String.valueOf(loc.getLatitude());
		currentLongitude = String.valueOf(loc.getLongitude());

		// get information from the current location
		speed = getSpeed(loc);
		speedUnit = getSpeedUnit();

		heading = getHeading(loc);
		headingUnit = getHeadingUnit();

		BearingToCurrentWaypoint = getBearing(loc);
		bearingUnit = getBearingUnit();

		DistanceToCurrentWaypoint = getDistance(loc);
		distanceUnit = getDistanceUnit();

		accuracy = getAccuracy(loc);
		accuracyUnit = getAccuracyUnit();

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
			bearingAuto = Integer.parseInt(BearingToCurrentWaypoint);
			bearingNow = new Date();

			int bearingDiff = java.lang.Math.abs((int) bearingLastAuto - (int) bearingAuto);
			if (bearingDiff > 180) {
				bearingDiff = java.lang.Math.abs(bearingDiff - 360);
			}
			if (((bearingDiff > bearingTreshold))
					&& ((bearingNow.getTime() - bearingBefore.getTime()) > bearingTimeTreshold * 1000)) {
				
				speakBearingTextView(BearingToCurrentWaypoint, bearingUnit);
				bearingLastAuto = bearingAuto;
				bearingBefore = new Date();
				Log.i("bearing", BearingToCurrentWaypoint);
			}
		}// end of if bearingAutoCheck...

		if (isAutoDistance && isWaypointActivated) {
			distanceAuto = distance[0];
			distanceNow = new Date();

			if (distanceAuto < 10) distanceTreshold = 1;
			if (distanceAuto < 100 && distanceAuto >= 10) distanceTreshold = 10;
			if (distanceAuto < 1000 && distanceAuto >= 100) distanceTreshold = 100;
			if (distanceAuto < 10000 && distanceAuto >= 1000) distanceTreshold = 1000;
			if (distanceAuto >= 10000) distanceTreshold = 10000;

			if (((distanceAuto < distanceLastAuto - distanceTreshold) || (distanceAuto > distanceLastAuto + distanceTreshold))
					&& ((distanceNow.getTime() - distanceBefore.getTime()) > distanceTimeTreshold * 1000)) {

				speakDistanceTextView(DistanceToCurrentWaypoint, distanceUnit);
				distanceLastAuto = distanceAuto;
				distanceBefore = new Date();
				Log.i("distance", DistanceToCurrentWaypoint);
			}
		}// end of if distanceAutoCheck...

		if (isAutoSpeed) {
			speedAuto = Utils.arrondiSpeed(loc.getSpeed() * (1.945));
			speedNow = new Date();
			if (((speedAuto < speedLastAuto - speedTreshold) || (speedAuto > speedLastAuto + speedTreshold))
					&& ((speedNow.getTime() - speedBefore.getTime()) > speedTimeTreshold * 1000)) {

				speakSpeedTextView(speed, speedUnit);
				speedLastAuto = speedAuto;
				speedBefore = new Date();
				Log.i("speed", speed);
			}
		}// end of if speedAutoCheck...

		if (isAutoHeading) {
			headingAuto = (int) loc.getBearing();
			headingNow = new Date();

			int headingDiff = java.lang.Math.abs((int) headingLastAuto - (int) headingAuto);
			if (headingDiff > 180)  headingDiff = java.lang.Math.abs(headingDiff - 360);
			
			if (((headingDiff > headingTreshold))
					&& ((headingNow.getTime() - headingBefore.getTime()) > headingTimeTreshold * 1000)) {

				speakHeadingTextView(heading, headingUnit);
				headingLastAuto = headingAuto;
				headingBefore = new Date();
				Log.i("heading", heading);
			}
		}// end of if headingAutoCheck...

		if (isAutoAccuracy) {
			accuracyAuto = loc.getAccuracy();
			accuracyNow = new Date();
			if (isMorePrecise5Announced == false && accuracyAuto < 5 
					&& (accuracyNow.getTime() - accuracyBefore.getTime()) > accuracyTimeTreshold * 1000) {
				
				speakAccuracyTextView(accuracy, accuracyUnit);
				accuracyBefore = new Date();
				Log.i("accuracy", accuracy);
				isMorePrecise5Announced = true;
				isMorePrecise10Announced = false;
				isLessPrecise10Announced = false;
			} 
			else if (isMorePrecise10Announced == false && accuracyAuto >= 5 && accuracyAuto < 10 
					&& (accuracyNow.getTime() - accuracyBefore.getTime()) > accuracyTimeTreshold * 1000) {

				speakAccuracyTextView(accuracy, accuracyUnit);
				accuracyBefore = new Date();
				Log.i("accuracy", accuracy);
				isMorePrecise5Announced = false;
				isMorePrecise10Announced = true;
				isLessPrecise10Announced = false;
			} else if (isLessPrecise10Announced == false && accuracyAuto >= 10
					&& (accuracyNow.getTime() - accuracyBefore.getTime()) > accuracyTimeTreshold * 1000) {
				
				speakAccuracyTextView(accuracy, accuracyUnit);
				accuracyBefore = new Date();
				Log.i("accuracy", accuracy);
				isMorePrecise5Announced = false;
				isMorePrecise10Announced = false;
				isLessPrecise10Announced = true;
			}
		}// end of if AccuracyAutoCheck...
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
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
			speed = String.valueOf(Utils.arrondiSpeed(loc.getSpeed() * (1.944)));
		}
		if (isKmPerHrSelected) {
			speed = String.valueOf(Utils.arrondiSpeed(loc.getSpeed() * (3.6)));
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
		} else {
			Location.distanceBetween(WaypointLatitude, WaypointLongitude, loc.getLatitude(), loc.getLongitude(), distance);
			return Utils.precisionDistance(distance[0] / 1000);
		}
	}

	public String getDistanceUnit() {
		if (WaypointLatitude == 999 || WaypointLongitude == 999) {
			return "";
		}
		if (distance[0] < 1000) {
			return resource.getString(R.string.m);
		} else {
			return resource.getString(R.string.km);
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

	public String getBearingUnit() {
		if (WaypointLatitude == 999 || WaypointLongitude == 999) {
			return "";
		}
		if(isCardinalSelected) {
			return resource.getString(R.string.deg);
		}
		else { // port&starboard selected
			if (Integer.parseInt(BearingToCurrentWaypoint) < 180) return "on starboard";
			else return "on port";
		}
	}
	
	public void isWaypointActivated() {
		if (WaypointLatitude == 999 || WaypointLongitude == 999) {
			this.isWaypointActivated = false;
		}
		else {
			this.isWaypointActivated = true;
		}
	}
	
	public void speakBearingTextView(String value, String unit) {
		if(unit == resource.getString(R.string.deg)) {
			MainActivity.tts.speak(resource.getString(R.string.bearing) + " " + value + " " + resource.getString(R.string.bearingunit), TextToSpeech.QUEUE_ADD, null);
		}
		else { // on starboard/port
			MainActivity.tts.speak(resource.getString(R.string.bearing) + " " + value + " " + bearingUnit, TextToSpeech.QUEUE_ADD, null);
		}
	}
	
	public void speakSpeedTextView(String value, String unit) {
		MainActivity.tts.speak(resource.getString(R.string.speed) + " " + value + " " + unit, TextToSpeech.QUEUE_ADD, null);
	}
	
	public void speakHeadingTextView(String value, String unit) {
		MainActivity.tts.speak(resource.getString(R.string.heading) + " " + value + " " + resource.getString(R.string.headingunit), TextToSpeech.QUEUE_ADD, null);
	}
	
	public void speakAccuracyTextView(String value, String unit) {
		MainActivity.tts.speak(resource.getString(R.string.accuracy) + " " + value + " " + resource.getString(R.string.metres), TextToSpeech.QUEUE_ADD, null);
	}
	
	public void speakDistanceTextView(String value, String unit) {
		if(unit == resource.getString(R.string.m)) {
			MainActivity.tts.speak(resource.getString(R.string.distance) + " " + value + " " + resource.getString(R.string.metres), TextToSpeech.QUEUE_ADD, null);
		} 
		if(unit == resource.getString(R.string.km)){
			MainActivity.tts.speak(resource.getString(R.string.distance) + " " + value + " " + resource.getString(R.string.kilometres), TextToSpeech.QUEUE_ADD, null);
		}
	}
}
