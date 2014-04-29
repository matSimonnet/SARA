package orion.ms.sara;

import java.io.File;
import java.util.List;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapController;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.overlay.ItemizedOverlay;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.core.GeoPoint;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WaypointMapActivity extends MapActivity{
	//variables declaration
	//Intent
	private Intent intentToWPMap;//from both new and modify way point
	private Intent intentToNewWP;//to new way point 
	private Intent intentToModWP;//to modify way point 	
	
	//boolean to check if come from modify activity
	private boolean isMod = false;
	
	//old location
	private String oldLatitude = "";
	private String oldLongitude = "";
	
	//new location
	private String newLatitude = "";
	private String newLongitude = "";
	
	//layout components
	//map
	private static MapView mapView;
	//button
	private Button saveButton = null;
	
	//location
	private LocationManager lm;
	//map component
	private static MapController Controller;
	private OverlayItem item;
	private MyItemizedOverlay itemizedOverlay;
	
	//way point
	private WP tempWP;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_waypoint_map);
		
		//intent creation
		intentToWPMap = getIntent();
		intentToNewWP = new Intent(WaypointMapActivity.this,NewWayPointActivity.class);
		intentToModWP = new Intent(WaypointMapActivity.this,ModifyWPActivity.class);
		
		//receive the old location and boolean if it comes from modify activity
		isMod = intentToWPMap.getBooleanExtra("ifMod", false);
		oldLatitude = intentToWPMap.getStringExtra("oldLatitude");
		oldLongitude = intentToWPMap.getStringExtra("oldLongitude");
		
		//map
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);
		setMapFile();

		//initial map location with current position and old way point
		Controller = mapView.getController();
		initLocation();
		oldWaypoint(WayPointActivity.wayPointList);
		
		
		
		//button
		saveButton = (Button) findViewById(R.id.button1);
		saveButton.setContentDescription("save current location from the map");
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//get the current location from the map
				//getCurrentLocation();
				//going back and save the changes value
				//pass new latitude and longitude
				if(!isMod){
					//from new waypoint activity
					intentToNewWP.putExtra("newLatitude", newLatitude);//latitude
					intentToNewWP.putExtra("newLongitude", newLatitude);//longitude
					setResult(RESULT_OK, intentToNewWP);
					finish();
				}
				else{
					//from modify waypoint activity
					intentToModWP.putExtra("newLatitude", newLatitude);//latitude
					intentToModWP.putExtra("newLongitude", newLatitude);//longitude
					setResult(RESULT_OK, intentToModWP);
					finish();
				}
			}
		});
		
	}
	
	private void oldWaypoint(List<WP> wList){
		//check if there are more than 1 item in the list including default item
		if(wList.size()>1){
			for(int i = 0;i<wList.size();i++){
				tempWP = wList.get(i);
				//check if a way point is not the default waypoint 
				if(!tempWP.getName().equals("Please selected a waypoint")){
					Log.i("tempWP", tempWP.getName());
					markPoint(tempWP,"inactive");
				}//end if
			}//end for
		}//end if size>1
	}
	
	//pin each way point on the map with 
	private void markPoint(WP wp, String type){
		double latitude = Double.parseDouble(wp.getLatitude());
		double longitude = Double.parseDouble(wp.getLongitude());
		GeoPoint point = new GeoPoint(latitude,longitude);
		item = new OverlayItem(point, wp.getName(), latitude + " , " + longitude);
		
		//check type of the way point : inactive, active, modify
		if(type.equals("inactive")){
			item.setMarker(ItemizedOverlay.boundCenter(getResources().getDrawable(R.drawable.inactivewp)));
			itemizedOverlay = new MyItemizedOverlay(this.getResources().getDrawable(R.drawable.inactivewp),true, this);
		}
		else if(type.equals("active")){
			item.setMarker(ItemizedOverlay.boundCenter(getResources().getDrawable(R.drawable.activewp)));
			itemizedOverlay = new MyItemizedOverlay(this.getResources().getDrawable(R.drawable.activewp),true, this);
		}
		else if(type.equals("modify")){
			item.setMarker(ItemizedOverlay.boundCenter(getResources().getDrawable(R.drawable.modifywp)));
			itemizedOverlay = new MyItemizedOverlay(this.getResources().getDrawable(R.drawable.modifywp),true, this);
		}

		item.setTitle(wp.getName());
		item.setSnippet(wp.getName()+" : "+latitude+" , "+longitude);
		
		// add item to item management
		itemizedOverlay.addItem(item);
		
		//add item to map view
		mapView.getOverlays().add(itemizedOverlay);
	}
	
	//return current location from the map
	private String[] getCurrentLocation(MapView map, GeoPoint geo){
		String[] point = new String[3];
		
	    

		return point;
	}
	
	//setting up the map
	private void setMapFile() {
		File mapFile = new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/org.mapsforge.android.maps/map/bretagne.map");
		// check if there is a bretagne.map
		if (mapFile.exists()) {
			mapView.setMapFile(mapFile);
		}
		else {
			AlertDialog.Builder dialog = new AlertDialog.Builder(WaypointMapActivity.this);
			dialog.setIcon(android.R.drawable.presence_busy);
			dialog.setTitle("You didn't have a map yet, please download it by going to the Map");
			dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//go back
					//pass old latitude and longitude
					if(!isMod){
						//from new waypoint activity
						intentToNewWP.putExtra("newLatitude", oldLatitude);//latitude
						intentToNewWP.putExtra("newLongitude", oldLatitude);//longitude
						setResult(RESULT_OK, intentToNewWP);
						finish();
					}
					else{
						//from modify waypoint activity
						intentToModWP.putExtra("newLatitude", oldLatitude);//latitude
						intentToModWP.putExtra("newLongitude", oldLatitude);//longitude
						setResult(RESULT_OK, intentToModWP);
						finish();
					}
				}
			});
			dialog.show();
		}
	}
	
	private void initLocation(){
		// get the pointers to different system services
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);		
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.ll);
		
		//show current location
		centerLocation();
	}

	//focus on center of current location
	public static void centerLocation() {
		if(MyLocationListener.currentLatitude != "" && MyLocationListener.currentLongitude != "") {
			double latitude = Double.valueOf(MyLocationListener.currentLatitude);
			double longitude = Double.valueOf(MyLocationListener.currentLongitude);
			GeoPoint currentlocation = new GeoPoint(latitude, longitude);
			Controller.setCenter(currentlocation);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.waypoint_map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.back_setting:
			//check if some values change without saving
			if(!oldLatitude.equals(newLatitude) || !oldLongitude.equals(newLongitude)){
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setTitle("Some values change, do you want to save?");
				dialog.setNegativeButton("Yes", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//going back and save the changes value
						//pass new latitude and longitude
						if(!isMod){
							//from new waypoint activity
							intentToNewWP.putExtra("newLatitude", newLatitude);//latitude
							intentToNewWP.putExtra("newLongitude", newLatitude);//longitude
							setResult(RESULT_OK, intentToNewWP);
							finish();
						}
						else{
							//from modify waypoint activity
							intentToModWP.putExtra("newLatitude", newLatitude);//latitude
							intentToModWP.putExtra("newLongitude", newLatitude);//longitude
							setResult(RESULT_OK, intentToModWP);
							finish();
						}
					}
				});
				
				dialog.setNeutralButton("No", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//going back without saving
						//pass old latitude and longitude
						if(!isMod){
							//from new waypoint activity
							intentToNewWP.putExtra("newLatitude", oldLatitude);//latitude
							intentToNewWP.putExtra("newLongitude", oldLatitude);//longitude
							setResult(RESULT_OK, intentToNewWP);
							finish();
						}
						else{
							//from modify waypoint activity
							intentToModWP.putExtra("newLatitude", oldLatitude);//latitude
							intentToModWP.putExtra("newLongitude", oldLatitude);//longitude
							setResult(RESULT_OK, intentToModWP);
							finish();
						}
					}
				});
				dialog.show();
				
			}
			else{
				//pass old latitude and longitude
				if(!isMod){
					//from new waypoint activity
					intentToNewWP.putExtra("newLatitude", oldLatitude);//latitude
					intentToNewWP.putExtra("newLongitude", oldLatitude);//longitude
					setResult(RESULT_OK, intentToNewWP);
					finish();
				}
				else{
					//from modify waypoint activity
					intentToModWP.putExtra("newLatitude", oldLatitude);//latitude
					intentToModWP.putExtra("newLongitude", oldLatitude);//longitude
					setResult(RESULT_OK, intentToModWP);
					finish();
				}
				break;
			}
		
		default:
			break;
		}
		return false;
	}

	

}
