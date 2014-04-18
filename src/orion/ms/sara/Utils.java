package orion.ms.sara;

import android.content.res.Resources;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;

public class Utils {
	
	private static SpannableString msp = null;
	public static Resources resource = MainActivity.getContext().getResources();
	
	public static double RadToDeg(double radians)  
    {  
        return radians * (180 / Math.PI);  
    }  

    public static double DegToRad(double degrees)  
    {  
        return degrees * (Math.PI / 180);  
    }  

    public static String _Bearing(double lat1, double long1, double lat2, double long2)  
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
        return String.valueOf((int) ConvertToBearing(RadToDeg(bearing)));  
    }  

    public static double ConvertToBearing(double deg)  
    {  
        return (deg + 360) % 360;  
    }  
    
    public static String precisionDistance(double distance) {
		if(distance < 1) {
			return String.valueOf((int) (distance * 1000));
		}
		if(distance >= 1 && distance < 10) {
			return String.valueOf(arrondiDistance(distance));
		}
		else {
			return String.valueOf((int) distance);
		}
    }
    
	//method to round 1 decimal
	public static double arrondiSpeed(double val) {return (Math.floor(val*10))/10;}
	public static double arrondiDistance(double val) {return (Math.floor(val*10))/10;}
	public static double arrondiBearing(double val) {return (Math.floor(val*10))/10;}
	public static double arrondiSpeedTreshold(double val) {return (Math.floor((val+0.000001)*10))/10;}
	public static double arrondiHeadingTreshold(double val) {return (Math.floor((val+0.000001)*10))/10;}
	
	public static void setDistanceTextView(String value, String unit) {
		if(unit != "") {
			String temp = "Distance" + " to " + MyLocationListener.WaypointName + "\n" + value + " " + unit;
			msp = new SpannableString (temp);
			int titleindex = temp.lastIndexOf("\n");
			int unitindex = temp.lastIndexOf(" ");
			msp.setSpan (new RelativeSizeSpan (0.4f), unitindex, temp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			msp.setSpan (new RelativeSizeSpan (0.4f), 0, titleindex+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		else {
			String temp = "Distance" + "\n" + value + " " + unit;
			msp = new SpannableString (temp);
			int titleindex = temp.lastIndexOf("\n");
			msp.setSpan (new RelativeSizeSpan (0.4f), 0, titleindex+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		MainActivity.textViewDistance.setText(msp);
		MainActivity.textViewDistance.setGravity(Gravity.RIGHT);
	}
	public static void setHeadingTextView(String value, String unit) {
		String temp = "Heading" + "\n" + value + " " + unit;
		msp = new SpannableString (temp);
		int unitindex = temp.lastIndexOf(" ");
		int titleindex = temp.lastIndexOf("\n");

		if(unit != "") {
			msp.setSpan (new RelativeSizeSpan (0.4f), unitindex, temp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			msp.setSpan (new RelativeSizeSpan (0.4f), 0, titleindex+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		else {
			msp.setSpan (new RelativeSizeSpan (0.4f), 0, titleindex+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		MainActivity.textViewheading.setText(msp);
		MainActivity.textViewheading.setGravity(Gravity.RIGHT);
	}	
	public static void setBearingTextView(String value, String unit) {
		if(unit != "") {
			String temp = "Bearing" + " to " + MyLocationListener.WaypointName + "\n" + value + " " + unit;
			msp = new SpannableString (temp);
			int unitindex = temp.lastIndexOf(" ");
			int titleindex = temp.lastIndexOf("\n");
			if(MyLocationListener.isCardinalSelected) {
				msp.setSpan (new RelativeSizeSpan (0.4f), unitindex, temp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				msp.setSpan (new RelativeSizeSpan (0.4f), 0, titleindex+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			else {
				msp.setSpan (new RelativeSizeSpan (0.4f), unitindex-2, temp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				msp.setSpan (new RelativeSizeSpan (0.4f), 0, titleindex+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		else {
			String temp = "Bearing" + "\n" + value + " " + unit;
			msp = new SpannableString (temp);
			int titleindex = temp.lastIndexOf("\n");
			msp.setSpan (new RelativeSizeSpan (0.4f), 0, titleindex+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		MainActivity.textViewBearing.setText(msp);
		MainActivity.textViewBearing.setGravity(Gravity.LEFT);
	}
	public static void setSpeedTextView(String value, String unit) {
		String temp = "Speed" + "\n" + value + " " + unit;
		msp = new SpannableString (temp);
		int unitindex = temp.lastIndexOf(" ");
		int titleindex = temp.lastIndexOf("\n");
		
		if(unit != "") {
			msp.setSpan (new RelativeSizeSpan (0.4f), unitindex, temp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			msp.setSpan (new RelativeSizeSpan (0.4f), 0, titleindex+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		else {
			msp.setSpan (new RelativeSizeSpan (0.4f), 0, titleindex+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		MainActivity.textViewSpeed.setText(msp);
		MainActivity.textViewSpeed.setGravity(Gravity.LEFT);
	}
	public static void setAccuracyTextView(String value, String unit) {
		String temp = "Accuracy" + "\n" + value + " " + unit;
		msp = new SpannableString (temp);
		int unitindex = temp.lastIndexOf(" ");
		int titleindex = temp.lastIndexOf("\n");

		if(unit != "") {
			msp.setSpan (new RelativeSizeSpan (0.4f), unitindex, temp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			msp.setSpan (new RelativeSizeSpan (0.4f), 0, titleindex+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		else {
			msp.setSpan (new RelativeSizeSpan (0.4f), 0, titleindex+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		MainActivity.textViewAccuracy.setText(msp);
		MainActivity.textViewAccuracy.setGravity(Gravity.LEFT);
	}
	
	public static void setSpeedTextViewDescription(String value, String unit) {
		if(unit == resource.getString(R.string.knots)) {
			MainActivity.textViewSpeed.setContentDescription(resource.getString(R.string.speed) + " " + value + " " + resource.getString(R.string.knots));
		}
		if(unit == resource.getString(R.string.kmperh)) {
			MainActivity.textViewSpeed.setContentDescription(resource.getString(R.string.speed) + " " + value + " " + resource.getString(R.string.km_per_hour));
		}
	}
	
	public static void setHeadingTextViewDescription(String value) {
		MainActivity.textViewheading.setContentDescription(resource.getString(R.string.heading) + " " + value + " " + resource.getString(R.string.headingunit));
	}
	
	public static void setAccuracyTextViewDescription(String value) {
		MainActivity.textViewAccuracy.setContentDescription(resource.getString(R.string.accuracy) + " " + value + " " + resource.getString(R.string.metres));
	}
	
	public static void setDistanceTextViewDescription(String value, String unit) {
		if(unit == "") {
			MainActivity.textViewDistance.setContentDescription(resource.getString(R.string.notactivate));
		}
		if(unit == resource.getString(R.string.m)) {
			MainActivity.textViewDistance.setContentDescription(resource.getString(R.string.distance) + value + " " + resource.getString(R.string.metres));
		}
		if(unit == resource.getString(R.string.km)) {
			MainActivity.textViewDistance.setContentDescription(resource.getString(R.string.distance) + value + " " + resource.getString(R.string.kilometres));			
		}
	}
	
	public static void setBearingTextViewDescription(String value, String unit) {
		if(unit == "") {
			MainActivity.textViewBearing.setContentDescription(resource.getString(R.string.notactivate));
		}
		if(unit == resource.getString(R.string.deg)) {
			MainActivity.textViewBearing.setContentDescription(resource.getString(R.string.bearing) + value + " " + resource.getString(R.string.degrees));
		}
		if(unit == resource.getString(R.string.onport)) {
			MainActivity.textViewBearing.setContentDescription(resource.getString(R.string.bearing) + value + " " + resource.getString(R.string.onport));
		}
		if(unit == resource.getString(R.string.onstarboard)) {
			MainActivity.textViewBearing.setContentDescription(resource.getString(R.string.bearing) + value + " " + resource.getString(R.string.onstarboard));
		}
	}

}
