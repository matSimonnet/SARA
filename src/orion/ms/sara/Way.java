package orion.ms.sara;

import java.util.ArrayList;
import java.util.List;

public class Way implements Comparable<Way>{
	private List<WP> way;
	private Double distance;
	private WP tempWP1;
	private String name;
	
	//constructor
	public Way(String name){
		this.name = name;
		way = new ArrayList<WP>();
	}
	
	//add new way point to way
	public List<WP> addWPtoWay(WP newWP){
		if(!newWP.getName().equals("No selected waypoint")){
			//if there is a selected way point
			way.add(newWP);
		}
		else{
			way = deleteWPfromWay(newWP);
		}
		return way;
	}
	
	//add a way point at the selected position
	public List<WP> addWPat(WP wp,int position){
		way.add(position, wp);
		return way;
	}
	
	//delete a way point at the selected position
	public List<WP> removeWPat(int position){
		way.remove(position);
		return way;
	}
	
	//delete way point from way
	private List<WP> deleteWPfromWay(WP deleteWP){
		//check if there is still any way point left
		if(way.size()>=1){
			for(int i=0;i<way.size();i++){
				tempWP1 = way.get(i);
				if(tempWP1.getName().equals(deleteWP.getName())){
					//delete the selected way point
					way.remove(i);
				}
			}
		}
		return way;
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
	
	public void setFirstWP(WP wp){
		way.remove(0);
		way.add(0,wp);
	}

	public WP getWP(int i){
		if(i<way.size()){
			return way.get(i);
		}
		return null;
	}
	
	public void setWP(int i,WP wp){
		if(i<way.size()){
			int position = i;
			way.remove(position);
			way.add(position, wp);
		}
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
		//compare each way by the first way point of the way
		if(this.getFirstWP().getDistance()<=another.getFirstWP().getDistance()) return -1;
		else return 1;
	}

}
