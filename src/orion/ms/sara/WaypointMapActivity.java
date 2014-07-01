package orion.ms.sara;

import android.content.Context;

import android.os.Bundle;
import android.os.Environment;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.io.File;
import org.mapsforge.android.maps.*;
import org.mapsforge.android.maps.overlay.ItemizedOverlay;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.core.GeoPoint;

public class WaypointMapActivity extends MapActivity {
	
	private static Context mContext;
    private MyMapView mapView;
	private String url = "http://download.mapsforge.org/maps/europe/france/bretagne.map"; // for download .map file
	private boolean isModify = false;
	
	private double ModifyLatitude = 999;
	private double ModifyLongitude = 999;
	private int modifyIndex = -1;
	
	private double mapLatitude = 999;
	private double mapLongitude = 999;
	
	private MyItemizedOverlay itemizedOverlay; // is an array for managing an item
	
	private Button saveButton;
	private Button AutoCenterButton;

	private OverlayItem itemModify;
	private OverlayItem itemCreate;
	
	private boolean isChanged = false; // if end user have long pressed on screen, isChange is true
 
	private MapController Controller;	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
 
        setContentView(R.layout.activity_waypoint_map);
		mContext = this; // set context
		// create item manager with default drawable 
		itemizedOverlay = new MyItemizedOverlay(mContext.getResources().getDrawable(R.drawable.inactivewp), true, getContext());
        
		// get map view and save button from XML file
		mapView = (MyMapView) findViewById(R.id.mapview);
		mapView.setContentDescription( getResources().getString(R.string.map) );
        this.saveButton = (Button) findViewById(R.id.savebutton);
        this.saveButton.setContentDescription(getResources().getString(R.string.usethislocation));
        
		AutoCenterButton = (Button) findViewById(R.id.autocenterbutton);
		AutoCenterButton.setContentDescription(getResources().getString(R.string.autocenter));
		AutoCenterButton.setText(getResources().getString(R.string.autocenter));
		
        
        // create listener for all buttons
	    View.OnClickListener onclickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v== saveButton) {
					if(isChanged) {
						Intent IntentToNewWP = new Intent(WaypointMapActivity.this,NewWayPointActivity.class);
						IntentToNewWP.putExtra("newLatitude", String.valueOf(mapLatitude));
						IntentToNewWP.putExtra("newLongitude", String.valueOf(mapLongitude));
						setResult(RESULT_OK, IntentToNewWP);
						finish();
					}
					else {
						finish();
					}
				}
				if(v == AutoCenterButton) {
					if(MyLocationListener.currentLatitude != "" && MyLocationListener.currentLongitude != "") {
						double latitude = Double.valueOf(MyLocationListener.currentLatitude);
						double longitude = Double.valueOf(MyLocationListener.currentLongitude);
						GeoPoint currentlocation = new GeoPoint(latitude, longitude);
						Controller.setCenter(currentlocation);
					}
				}

			}
	    	
	    };
	    saveButton.setOnClickListener(onclickListener); // add listener to button
	    AutoCenterButton.setOnClickListener(onclickListener);
	    
		setMapFile(); // set map file in the directory
		loadFlag(); // if start activity from ModifyWaypointActivity, return true
		loadWaypoint();
		Controller = mapView.getController();


        mapView.setOnLongpressListener(new MyMapView.OnLongpressListener() {
        public void onLongpress(final MapView view, final GeoPoint longpressLocation) {
            runOnUiThread(new Runnable() {
            	public void run() {
            		isChanged = true;
            		mapLatitude = longpressLocation.getLatitude();
            		mapLongitude = longpressLocation.getLongitude();
            		pinWaypoint(mapLatitude, mapLongitude);
            	}
            });
        	}
        });
    }
    
    // if end user have long pressed on screen, add that waypoint with blue pin
    private void pinWaypoint(double la, double lo) {
    	// remove the last item before adding the new one
    	if(isModify) {
    		if(itemModify != null) {
    			itemizedOverlay.removeItem(itemModify);
    		}
			itemModify = new OverlayItem(new GeoPoint(la, lo), "Modifying", la + " " + lo);
			itemModify.setMarker(ItemizedOverlay.boundCenterBottom(getResources().getDrawable(R.drawable.modifywp)));
			itemizedOverlay.addItem(itemModify);
    	}
    	else {
    		if(itemCreate != null) {
    			itemizedOverlay.removeItem(itemCreate);
    		}
    		itemCreate = new OverlayItem(new GeoPoint(la, lo), "Creating", la + " " + lo);
    		itemCreate.setMarker(ItemizedOverlay.boundCenterBottom(getResources().getDrawable(R.drawable.modifywp)));
			itemizedOverlay.addItem(itemCreate);
    	}
    }
    
    private void loadFlag() {
    	Bundle extras = getIntent().getExtras();
    	if (extras != null) {
    	    this.isModify = extras.getBoolean("ifMod", false);
    	    if(isModify) {
    	    	this.ModifyLatitude = Double.parseDouble(extras.getString("oldLatitude"));
    	    	this.ModifyLongitude = Double.parseDouble(extras.getString("oldLongitude"));
    	    	this.modifyIndex = extras.getInt("index", -1);
    	    	pinWaypoint(ModifyLatitude, ModifyLongitude);
    	    }
    	}
    }
    
    private void loadWaypoint() {
		int size = WayPointActivity.wayPointList.size();
		GeoPoint[] waypoint = new GeoPoint[size];
		double latitude;
		double longitude;
		String name;
		OverlayItem item;
		
		for(int i = size-1; i >= 0; i--) {
			if(WayPointActivity.wayPointList.get(i).getLatitude() != "" && WayPointActivity.wayPointList.get(i).getLongitude() != "") {
				latitude = Double.parseDouble(WayPointActivity.wayPointList.get(i).getLatitude());
				longitude = Double.parseDouble(WayPointActivity.wayPointList.get(i).getLongitude());
				
				name = WayPointActivity.wayPointList.get(i).getName();
				waypoint[i] = new GeoPoint(latitude, longitude);
				
				// activated waypoint
				if(latitude == MyLocationListener.WaypointLatitude && longitude == MyLocationListener.WaypointLongitude) {
					item = new OverlayItem(waypoint[i], name, latitude + " " + longitude);
					item.setMarker(ItemizedOverlay.boundCenterBottom(getResources().getDrawable(R.drawable.activewp)));
					itemizedOverlay.addItem(item);
				}
				else if(i != modifyIndex) {
					item = new OverlayItem(waypoint[i], name, latitude + " " + longitude);
					itemizedOverlay.addItem(item);
				}
			}
		}
		mapView.getOverlays().add(itemizedOverlay);	
	}
    
	private void setMapFile() {
//		File mapFile = new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/org.mapsforge.android.maps/map/bretagne.map");
		File mapFile = new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/org.mapsforge.android.maps/map/provence-alpes-cote-d-azur.map");
		// check if there is a bretagne.map
		if (mapFile.exists()) {
			mapView.setMapFile(mapFile);
		}
		else {
			// didn't have a map, then ask if the user wants to download
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("You don't have a map, do you want to download now?");
			dialog.setNegativeButton("Yes", new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// check if network is available
					if (isNetworkAvailable()) {
						// warning if on 3G network
						if (is3GAvailable()) {
							// second alert to notice it will be expensive
							AlertDialog.Builder dialog = new AlertDialog.Builder(WaypointMapActivity.this);
							dialog.setIcon(android.R.drawable.presence_busy);
							dialog.setTitle("Warning!!!");
							dialog.setMessage("Your Internet is connecting via 3G, downloading now may cost a lot. Are you sure to continue?");
							dialog.setNegativeButton("Sure",
									new OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// download
											if (isDownloadManagerAvailable(WaypointMapActivity.this)) {
												DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
												request.setDescription("/Android/data/map/");
												request.setTitle("bretagne.map");
												// in order for this if to run,
												// you must use the android 3.2
												// to compile your app
												if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
													request.allowScanningByMediaScanner();
													request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
												}
												request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "bretagne.map");

												// get download service and
												// enqueue file
												DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
												manager.enqueue(request);
											}// end isDownloadManagerAvailable
										}
									});// end setNegativeButton
							dialog.setNeutralButton("Cancel",
									new OnClickListener() {
										// Go back to main activity
										@Override
										public void onClick(DialogInterface dialog, int which) {
											finish();
										}
									});//
							dialog.show();
						}// end is3GAvailable
						else if (isWifiAvailable()) {
							// Start downloading file via wireless network
							// notify
							AlertDialog.Builder dialog = new AlertDialog.Builder(WaypointMapActivity.this);
							dialog.setTitle("Start downloading bretagne.map");
							dialog.setNeutralButton("OK", null);
							dialog.show();
							// download
							if (isDownloadManagerAvailable(WaypointMapActivity.this)) {
								DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
								request.setDescription("/Android/data/map/");
								request.setTitle("bretagne.map");
								// in order for this if to run, you must use the
								// android 3.2 to compile your app
								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
									request.allowScanningByMediaScanner();
									request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
								}
								request.setDestinationInExternalPublicDir(
										Environment.DIRECTORY_DOWNLOADS,
										"bretagne.map");

								// get download service and enqueue file
								DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
								manager.enqueue(request);
							}
						}// end isWifiAvailable
					}// end isNetworkAvailable
					else {
						// there is no Internet connection
						AlertDialog.Builder dialog = new AlertDialog.Builder(WaypointMapActivity.this);
						dialog.setIcon(android.R.drawable.presence_busy);
						dialog.setTitle("No Internet connection right now, please try again later.");
						dialog.setNegativeButton("OK", new OnClickListener() {
							// Go back to main activity
							@Override
							public void onClick(DialogInterface dialog, int which) {
								finish();
							}
						});// end setNegativeButton
						dialog.show();
					}// end else
				}// end onClick
			});// end setNegativeButton
			dialog.setNeutralButton("No", new OnClickListener() {
				// Go back to main activity
				@Override
				public void onClick(DialogInterface dialog, int which) { 
					finish();
				}
			});// end setNegativeButton
			dialog.show();
		}
	}

	// check if DownloadManager is available
	private boolean isDownloadManagerAvailable(Context context) {
		try {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setClassName("com.android.providers.downloads.ui", "com.android.providers.downloads.ui.DownloadList");
			List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
			return list.size() > 0;
		} catch (Exception e) {
			return false;
		}
	}

	// check if the Internet connection is available
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	// check if 3G connection is available
	private boolean is3GAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return mobile.isAvailable() && mobile.isConnected();
	}

	// check if wireless connection is available
	private boolean isWifiAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return wifi.isAvailable() && wifi.isConnected();
	}

	public static Context getContext() {
		return mContext;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.waypoint_map, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem Item){
		switch (Item.getItemId()) {
		case R.id.backtosetting:
			
			if(isChanged) { 
				Builder alertDialog = new AlertDialog.Builder(this);
				alertDialog.setTitle(getResources().getString(R.string.title_alertdialog_waypointmap));
				alertDialog.setNegativeButton("YES", new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
	    				Intent IntentToNewWP = new Intent(WaypointMapActivity.this,NewWayPointActivity.class);
	    				IntentToNewWP.putExtra("newLatitude", String.valueOf(mapLatitude));
	    				IntentToNewWP.putExtra("newLongitude", String.valueOf(mapLongitude));
						setResult(RESULT_OK, IntentToNewWP);
						finish();					
					}
				});
				alertDialog.setPositiveButton("No", new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				alertDialog.setIcon(android.R.drawable.presence_busy);
				alertDialog.show();
			}
			else {
				finish();
			}
			break;
		default:
			break;
		}
		return false;
	}
    
}
