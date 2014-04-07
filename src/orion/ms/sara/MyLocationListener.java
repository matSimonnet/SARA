package orion.ms.sara;

import java.util.Date;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

public class MyLocationListener extends Activity implements LocationListener {


	static String speed = "";
	static String heading = "";
	static String DistanceToCurrentWaypoint = "";
	static String BearingToCurrentWaypoint = "";
	static double WaypointLatitude = 999;
	static double WaypointLongitude = 999;
	
	static float distance[] = new float[1];
	static double bearing = 0.0;
	
	static Date speedNow = null;
	static Date speedBefore = new Date();
	
	static Date headingNow = null;
	static Date headingBefore = new Date();
	
	static Date distanceNow = null;
	static Date distanceBefore = new Date();
	
	static Date bearingNow = null;
	static Date bearingBefore = new Date();
	
	static boolean isAutoSpeed = true;
	static boolean isAutoHeading = true;
	static boolean isAutoDistance = true;
	static boolean isAutoBearing = true;
	
	static double speedAuto = 0.0;
	static double headingAuto = 0.0;
	static double distanceAuto = 0.0;
	static double bearingAuto = 0.0;

	static double speedLastAuto = 0.0;
	static double headingLastAuto = 0.0;
	static double distanceLastAuto = 0.0;
	static double bearingLastAuto = 0.0;
	
	static double speedTreshold = 1.0;
	static double headingTreshold = 10.0;
	static double distanceTreshold = 0.0;
	static double bearingTreshold = 10.0;
	
	static long speedTimeTreshold = 5;
	static long headingTimeTreshold = 5;
	static long distanceTimeTreshold = 5;
	static long bearingTimeTreshold = 5;

	@Override
	public void onLocationChanged(Location loc) {
		speed = String.valueOf(Utils.arrondiSpeed(loc.getSpeed() * (1.945)));
		heading = String.valueOf((int) loc.getBearing());

		if (WaypointLatitude == 999 || WaypointLongitude == 999) {
			MainActivity.textViewDistance.setText("No Waypoint");
			MainActivity.textViewBearing.setText("No Waypoint");
			MainActivity.textViewDistance.setContentDescription("Waypoint is not activated");
			MainActivity.textViewBearing.setContentDescription("Waypoint is not activated");
		} else {
			// calculate distance to the current waypoint
			Location.distanceBetween(WaypointLatitude, WaypointLongitude, loc.getLatitude(), loc.getLongitude(), distance);
			DistanceToCurrentWaypoint = String.valueOf(Utils.arrondiDistance(distance[0] / 1000));

			// calculate bearing to the current waypoint
			bearing = Utils._Bearing(loc.getLatitude(), loc.getLongitude(), WaypointLatitude, WaypointLongitude);
			BearingToCurrentWaypoint = String.valueOf(Utils.arrondiBearing(bearing));

			if (isAutoBearing) {
				bearingAuto = (int) bearing;
				bearingNow = new Date();

				int bearingDiff = java.lang.Math.abs((int) bearingLastAuto - (int) bearingAuto);
				if (bearingDiff > 180)
					bearingDiff = java.lang.Math.abs(bearingDiff - 360);

				if (((bearingDiff > bearingTreshold))
					&& ((bearingNow.getTime() - bearingBefore.getTime()) > bearingTimeTreshold * 1000)) {
					
					MainActivity.tts.speak("Bearing" + " " + BearingToCurrentWaypoint + " " + "degrees", TextToSpeech.QUEUE_ADD, null);
					bearingLastAuto = bearingAuto;
					bearingBefore = new Date();
					Log.i("bearing", BearingToCurrentWaypoint);
				}
			}// end of if bearingAutoCheck...

			if (isAutoDistance) {
				distanceAuto = distance[0] / 1000;
				distanceNow = new Date();

				if (((distanceAuto < distanceLastAuto - distanceTreshold) || (distanceAuto > distanceLastAuto + distanceTreshold))
					&& ((distanceNow.getTime() - distanceBefore.getTime()) > distanceTimeTreshold * 1000)) {
					
					MainActivity.tts.speak("Distance" + " " + DistanceToCurrentWaypoint + " " + "kilometres", TextToSpeech.QUEUE_ADD, null);
					distanceLastAuto = distanceAuto;
					distanceBefore = new Date();
					Log.i("distance", DistanceToCurrentWaypoint);
				}
			}// end of if distanceAutoCheck...
		}// end of else..

		if (isAutoSpeed) {
			speedAuto = Utils.arrondiSpeed(loc.getSpeed() * (1.945));
			speedNow = new Date();
			if (((speedAuto < speedLastAuto - speedTreshold) || (speedAuto > speedLastAuto + speedTreshold))
				&& ((speedNow.getTime() - speedBefore.getTime()) > speedTimeTreshold * 1000)) {
				
				MainActivity.tts.speak("Speed" + " : " + speed + " " + "knots", TextToSpeech.QUEUE_ADD, null);
				speedLastAuto = speedAuto;
				speedBefore = new Date();
				Log.i("speed", speed);
			}
		}// end of if speedAutoCheck...

		if (isAutoHeading) {
			headingAuto = (int) loc.getBearing();
			headingNow = new Date();

			int headingDiff = java.lang.Math.abs((int) headingLastAuto - (int) headingAuto);
			if (headingDiff > 180) 
				headingDiff = java.lang.Math.abs(headingDiff - 360);

			if (((headingDiff > headingTreshold))
				&& ((headingNow.getTime() - headingBefore.getTime()) > headingTimeTreshold * 1000)) {
				
				MainActivity.tts.speak("Heading" + " " + heading + " " + "degrees", TextToSpeech.QUEUE_ADD, null);
				headingLastAuto = headingAuto;
				headingBefore = new Date();
				Log.i("heading", heading);

			}
		}// end of if headingAutoCheck...

		// displaying value
		MainActivity.textViewSpeed.setText(speed);
		MainActivity.textViewheading.setText(heading);
		MainActivity.textViewSpeed.setContentDescription("Speed" + " " + speed + " " + "knots");
		MainActivity.textViewheading.setContentDescription("Heading" + " " + heading + " " + "degrees");

		if (WaypointLatitude != 999 && WaypointLongitude != 999) {
			MainActivity.textViewDistance.setText(DistanceToCurrentWaypoint);
			MainActivity.textViewBearing.setText(BearingToCurrentWaypoint);
			MainActivity.textViewDistance.setContentDescription("Distance" + DistanceToCurrentWaypoint + " " + "kilometres");
			MainActivity.textViewBearing.setContentDescription("Bearing" + BearingToCurrentWaypoint + " " + "degrees");
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(getApplicationContext(), "Gps Disabled",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(getApplicationContext(), "Gps Enabled",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// Log.i("LocationListener","onStatusChanged");
	}

}
