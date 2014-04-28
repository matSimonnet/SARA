package orion.ms.sara;

import java.io.File;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WaypointMapActivity extends MapActivity {
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_waypoint_map);
		
		//intent creation
		intentToWPMap = getIntent();
		intentToNewWP = new Intent(WaypointMapActivity.this,NewWayPointActivity.class);
		intentToModWP = new Intent(WaypointMapActivity.this,ModifyActivity.class);
		
		//receive the old location and boolean if it comes from modify activity
		isMod = intentToWPMap.getBooleanExtra("ifMod", false);
		oldLatitude = intentToWPMap.getStringExtra("oldLatitude");
		oldLongitude = intentToWPMap.getStringExtra("oldLongitude");
		
		//map
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);
		setMapFile();
		
		//button
		saveButton = (Button) findViewById(R.id.button1);
		saveButton.setContentDescription("save current location from the map");
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//get the current location from the map
				getCurrentLocation();
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
	//get current location from the map
	private void getCurrentLocation(){
		
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
