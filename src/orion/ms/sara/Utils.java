package orion.ms.sara;

public class Utils {
	
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
	public static double arrondiSpeed(double val) {return (Math.floor(val*10))/10;}
	public static double arrondiDistance(double val) {return (Math.floor(val*10))/10;}
	public static double arrondiBearing(double val) {return (Math.floor(val*10))/10;}
	public static double arrondiSpeedTreshold(double val) {return (Math.floor((val+0.00001)*10))/10;}
	public static double arrondiHeadingTreshold(double val) {return (Math.floor((val+0.00001)*10))/10;}
}
