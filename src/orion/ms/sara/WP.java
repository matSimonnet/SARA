package orion.ms.sara;
//a waypoint class containing each latitude, longitude, distance and bearing
public class WP {
		//name
		private String name = "";
		//positions  
		private String latitude = "";
		private String longitude = "";
		//distance and bearing
		private double distance = 0.0;
		private double bearing = 0.0;
		
		//constructor
		public WP(String n, String la, String lo, double dis, double bear){
			this.name = n;
			this.latitude = la;
			this.longitude = lo;
			this.distance = dis;
			this.bearing = bear;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getLatitude() {
			return latitude;
		}

		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}

		public String getLongitude() {
			return longitude;
		}

		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}

		public double getDistance() {
			return distance;
		}

		public void setDistance(double distance) {
			this.distance = distance;
		}

		public double getBearing() {
			return bearing;
		}

		public void setBearing(double bearing) {
			this.bearing = bearing;
		}
		
		
}
