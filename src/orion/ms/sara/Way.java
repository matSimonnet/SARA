package orion.ms.sara;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;

public class Way implements Comparable<Way>{
	private List<WP> way;
	private Double distance;
	private float[] result;
	@SuppressWarnings("unused")
	private WP wp1;
	@SuppressWarnings("unused")
	private WP wp2;
	private WP tempWP1;
	private WP tempWP2;
	private String name;
	
	//constructor
	public Way(String name,WP wp1,WP wp2){
		this.wp1 = wp1;
		this.wp2 = wp2;
		this.name = name;
		way = new ArrayList<WP>();
		way.add(wp1);
		way.add(wp2);
	}
	
	//add new waypoint to way
	public List<WP> addWPtoWay(WP newWP){
		if(!newWP.getName().equals("No waypoint selected")){
			//if there is a selected waypoint
			way.add(newWP);
		}
		else{
			way = deleteWPfromWay(newWP);
		}
		return way;
	}
	
	//delete waypoint from way
	private List<WP> deleteWPfromWay(WP deleteWP){
		//check if there is still any waypoint left
		if(way.size()>=1){
			for(int i=0;i<way.size();i++){
				tempWP1 = way.get(i);
				if(tempWP1.getName().equals(deleteWP.getName())){
					//delete the selected waypoint
					way.remove(i);
				}
			}
		}
		return way;
	}

	//calculate distance between two waypoints
	public Double getDistanceBTWtwoWPs(String wp1, String wp2){
		for(int i=0;i<way.size();i++){
			if(way.get(i).getName().equals(wp1))	tempWP1 = way.get(i);
			else if(way.get(i).getName().equals(wp2))	tempWP2 = way.get(i);
		}
		Location.distanceBetween(Double.parseDouble(tempWP1.getLatitude()), Double.parseDouble(tempWP1.getLongitude()), 
				Double.parseDouble(tempWP2.getLatitude()), Double.parseDouble(tempWP2.getLongitude()), result);
		distance = Double.parseDouble(""+result[0]);
		return distance;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public WP getFirstWP() {
		return way.get(0);
	}

	public WP getWP(int i){
		if(i<way.size()){
			return way.get(i);
		}
		return null;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public int getSize() {
		return way.size();
	}
	
	@Override
	public int compareTo(Way another) {
		//compare each way by the first waypoint of the way
		if(this.getFirstWP().getDistance()<=another.getFirstWP().getDistance()) return -1;
		else return 1;
	}

}
