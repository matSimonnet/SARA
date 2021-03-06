package orion.ms.sara;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.util.Log;

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
		
		Log.e("LANGUAGE", "" + Locale.getDefault().getDisplayLanguage());
		
		//if (Locale.getDefault().getDisplayLanguage()== "ENGLISH"){
			if(!newWP.getName().equals("No selected waypoint")){
				//if there is a selected way point
				way.add(newWP);
			}
			else{
				way = deleteWPfromWay(newWP);
			}	
		//}
		
//		if (Locale.getDefault().getDisplayLanguage()== "FRENCH"){
//			if(!newWP.getName().equals("pas de point de route sélectionné")){
//				//if there is a selected way point
//				way.add(newWP);
//			}
//			else{
//				way = deleteWPfromWay(newWP);
//			}
//		}
		return way;
	}
	
	//delete way point from way
	public List<WP> deleteWPfromWay(WP deleteWP){
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
