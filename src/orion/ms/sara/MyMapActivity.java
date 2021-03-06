package orion.ms.sara;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import java.util.List;
import java.io.File;
import org.mapsforge.android.maps.*;
import org.mapsforge.android.maps.overlay.ArrayCircleOverlay;
import org.mapsforge.android.maps.overlay.ArrayWayOverlay;
import org.mapsforge.android.maps.overlay.ItemizedOverlay;
import org.mapsforge.android.maps.overlay.OverlayCircle;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.android.maps.overlay.OverlayWay;
import org.mapsforge.core.GeoPoint;

public class MyMapActivity extends MapActivity {

	private static Context mContext;
	public static MapView mapView;
	private static MapController Controller;	

	private Button DisplayTrackButton;
	private Button AutoCenterButton;
	
	private static boolean isAutoCenter = false;
	
	private static MyItemizedOverlay itemizedOverlay;
	private static ArrayWayOverlay wayOverlay;
	private static ArrayWayOverlay wayLineOverlay;
	private static ArrayCircleOverlay arrayRadius;

	private static MyItemizedOverlay pinOverlay;
	
	private static OverlayItem item;
	private static OverlayWay way;
	private static Paint wayPaint;
	private static Paint wayLinePaint;
	private static Paint OutlineCirclePaint;
	private static Paint FillCirclePaint;	
	private LocationManager lm = null;
	public static MyLocationListener ll = null;
	
	// url for downloading file
	private String url = "http://download.mapsforge.org/maps/europe/france/bretagne.map";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mymap);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
        //location manager creation
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ll = new MyLocationListener();		
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);

		mContext = this;
		DisplayTrackButton = (Button) findViewById(R.id.trackbutton);
		AutoCenterButton = (Button) findViewById(R.id.autocenterbutton);

		if(MyLocationListener.isStartedDisplay) {
			DisplayTrackButton.setContentDescription(getResources().getString(R.string.stopdisplay));
			DisplayTrackButton.setText(getResources().getString(R.string.stopdisplay));
		}
		else {
			DisplayTrackButton.setContentDescription(getResources().getString(R.string.display));
			DisplayTrackButton.setText(getResources().getString(R.string.display));
		}		
		if(isAutoCenter) {
			AutoCenterButton.setContentDescription(getResources().getString(R.string.manualcenter));
			AutoCenterButton.setText(getResources().getString(R.string.manualcenter));
		}
		else {
			AutoCenterButton.setContentDescription(getResources().getString(R.string.autocenter));
			AutoCenterButton.setText(getResources().getString(R.string.autocenter));
		}
		
		MyLocationListener.isAutoDrawTrack = true;

		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);
		mapView.getMapScaleBar().setShowMapScaleBar(true);

		setPaintStyle();
		loadWayLine();
		loadWaypoint();
		loadPath();
		loadHeading();
		setMapFile();
		
		Controller = mapView.getController();

		View.OnClickListener onclickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v == DisplayTrackButton) {
					if (MyLocationListener.isStartedDisplay == false) {
						MyLocationListener.isStartedDisplay = true;
						DisplayTrackButton.setText(getResources().getString(R.string.stopdisplay));
						DisplayTrackButton.setContentDescription(getResources().getString(R.string.stopdisplay));
					} 
					else { // stop display and delete path
						MyLocationListener.isStartedDisplay = false;
						DisplayTrackButton.setText(getResources().getString(R.string.display));
						DisplayTrackButton.setContentDescription(getResources().getString(R.string.display));
						
						mapView.getOverlays().remove(wayOverlay);
						MyLocationListener.geoPoint = new GeoPoint[0];
					}

				}
				if (v == AutoCenterButton) {
					if (isAutoCenter == false) {
							isAutoCenter = true;
							AutoCenterButton.setContentDescription(getResources().getString(R.string.manualcenter));
							AutoCenterButton.setText(getResources().getString(R.string.manualcenter));
							centerLocation();
					} 
					else { // stop focusing
						isAutoCenter = false;
						AutoCenterButton.setContentDescription(getResources().getString(R.string.autocenter));
						AutoCenterButton.setText(getResources().getString(R.string.autocenter));
					}

				}
			}
		};

		DisplayTrackButton.setOnClickListener(onclickListener);
		AutoCenterButton.setOnClickListener(onclickListener);
	}
	public static void loadWayLine() {
		if(MyLocationListener.isWayActivated) {
			// remove the last way
			if (wayLineOverlay != null) {
				mapView.getOverlays().remove(wayLineOverlay);
				wayLineOverlay.clear();
			}
			if(MyLocationListener.WaypointName != "") {
				// new way management and set paint style
				wayLineOverlay = new ArrayWayOverlay(wayLinePaint, wayLinePaint); 
				// Create geopoint
				int size = MyLocationListener.activatedWay.size();
				GeoPoint[] waylist = new GeoPoint[size];
				for(int i = 0; i < size; i++) {
					Double la = Double.parseDouble(MyLocationListener.activatedWay.get(i).getLatitude());
					Double lo = Double.parseDouble(MyLocationListener.activatedWay.get(i).getLongitude());
					waylist[i] = new GeoPoint(la, lo);
				}
				// add GeoPoint to way overlay
				OverlayWay way = new OverlayWay(new GeoPoint[][] { waylist });
			
				// add way to map view
				wayLineOverlay.addWay(way);
				mapView.getOverlays().add(wayLineOverlay);
			}
		}
	}
	public static void loadWaypoint() {
		int size = WayPointActivity.wayPointList.size();
		GeoPoint[] waypoint = new GeoPoint[size];
		double latitude;
		double longitude;
		String name;
		
		if(pinOverlay != null) {
			mapView.getOverlays().remove(pinOverlay);
			pinOverlay.clear();
			Log.i("clear pin", "cleared");
		}		
		
		pinOverlay = new MyItemizedOverlay(mContext.getResources().getDrawable(R.drawable.inactivewp), true, getContext());
		OverlayItem item;
		
		for(int i = size-1; i >= 0; i--) {
			if(WayPointActivity.wayPointList.get(i).getLatitude() != "" && WayPointActivity.wayPointList.get(i).getLongitude() != "") {
				latitude = Double.parseDouble(WayPointActivity.wayPointList.get(i).getLatitude());
				longitude = Double.parseDouble(WayPointActivity.wayPointList.get(i).getLongitude());
				
				name = WayPointActivity.wayPointList.get(i).getName();
				waypoint[i] = new GeoPoint(latitude, longitude);
				item = new OverlayItem(waypoint[i], name, latitude + " " + longitude);
				
				if(MyLocationListener.isWayActivated && isInActivatedWay(name)) {
					item.setMarker(ItemizedOverlay.boundCenterBottom(mContext.getResources().getDrawable(R.drawable.activateway)));
					Log.i("activated way", name);
				}
				
				if(latitude == MyLocationListener.WaypointLatitude && longitude == MyLocationListener.WaypointLongitude) {
					item.setMarker(ItemizedOverlay.boundCenterBottom(mContext.getResources().getDrawable(R.drawable.activewp)));
					drawRadius(WayPointActivity.wayPointList.get(i));
					Log.i("activated waypoint", name);
				}
				if(MyLocationListener.WaypointLatitude == 999 && MyLocationListener.WaypointLongitude == 999) {
					drawRadius(WayPointActivity.wayPointList.get(i));
					loadWayLine();
				}
				pinOverlay.addItem(item);
			}
		}
		mapView.getOverlays().add(pinOverlay);	
	}
	private static void drawRadius(WP activatedWP) {
		if(arrayRadius != null) {
			mapView.getOverlays().remove(arrayRadius);
			arrayRadius.clear();
		}
		if(MyLocationListener.WaypointName != "") { 
			double la = Double.parseDouble(activatedWP.getLatitude());
			double lo = Double.parseDouble(activatedWP.getLongitude());
			GeoPoint tmp = new GeoPoint(la,lo);
			OverlayCircle Radius = new OverlayCircle(tmp , activatedWP.getTreshold(), FillCirclePaint, OutlineCirclePaint, "Waypoint Treshold");
			arrayRadius = new ArrayCircleOverlay(FillCirclePaint, OutlineCirclePaint);
			arrayRadius.addCircle(Radius);
			mapView.getOverlays().add(arrayRadius);	
		}
	}
	
	private static boolean isInActivatedWay(String name) {
		int size = MyLocationListener.activatedWay.size();
		for(int i = 0; i < size; i++) {
			if(MyLocationListener.activatedWay.get(i).getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	private void loadPath() {
		if(MyLocationListener.geoPoint.length != 0) {
			drawPath();
		}
	}
	
	public static void drawPath() {
		// remove the last way
		if (wayOverlay != null) {
			mapView.getOverlays().remove(wayOverlay);
			wayOverlay.clear();
		}
		// new way management and set paint style
		wayOverlay = new ArrayWayOverlay(wayPaint, wayPaint); 
		
		// add GeoPoint to way overlay
		way = new OverlayWay(new GeoPoint[][] { MyLocationListener.geoPoint });
		
		// add way to map view
		wayOverlay.addWay(way);
		mapView.getOverlays().add(wayOverlay);
	}
	
	private void loadHeading() {
		if(MyLocationListener.heading != getResources().getString(R.string.no_satellite)) {
			drawHeading(Integer.parseInt(MyLocationListener.heading));
		}
	}

	public static void drawHeading(int heading) {
		// remove the last arrow
		if (itemizedOverlay != null) {
			mapView.getOverlays().remove(itemizedOverlay);
			itemizedOverlay.clear();
		}
		// add marker to the current location with rotated arrow
		if(MyLocationListener.currentLatitude != "" && MyLocationListener.currentLongitude != "") {
			double latitude = Double.valueOf(MyLocationListener.currentLatitude);
			double longitude = Double.valueOf(MyLocationListener.currentLongitude);
			GeoPoint currentlocation = new GeoPoint(latitude, longitude);
			item = new OverlayItem(currentlocation, "Current location", currentlocation.getLatitude() + " " + currentlocation.getLongitude());
			item.setMarker(ItemizedOverlay.boundCenter(rotateDrawable(heading)));
		}
		
		// add item to item management
		itemizedOverlay = new MyItemizedOverlay(mContext.getResources().getDrawable(R.drawable.arrow), true, getContext()); // set default marker
		itemizedOverlay.addItem(item);
		
		//add item to map view
		mapView.getOverlays().add(itemizedOverlay);
	}

	public static BitmapDrawable rotateDrawable(float heading) {
		
		// transform drawable to bitmap from my resources
		Bitmap arrowBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.arrow);
		
		// Create blank bitmap of equal size
		Bitmap canvasBitmap = arrowBitmap.copy(Bitmap.Config.ARGB_8888, true);
		canvasBitmap.eraseColor(0x00000000);
		
		// Create canvas
		Canvas canvas = new Canvas(canvasBitmap);

		// Create rotation matrix
		Matrix rotateMatrix = new Matrix();
		rotateMatrix.setRotate(heading, canvas.getWidth() / 2, canvas.getHeight() / 2); // at this pivot (x,y)
		
		// Draw bitmap onto canvas using matrix
		canvas.drawBitmap(arrowBitmap, rotateMatrix, null);
		return new BitmapDrawable(mContext.getResources(), canvasBitmap); // return drawable
	}
	
	public static void centerLocation() {
		if(isAutoCenter) {
			if(MyLocationListener.currentLatitude != "" && MyLocationListener.currentLongitude != "") {
				double latitude = Double.valueOf(MyLocationListener.currentLatitude);
				double longitude = Double.valueOf(MyLocationListener.currentLongitude);
				GeoPoint currentlocation = new GeoPoint(latitude, longitude);
				Controller.setCenter(currentlocation);
			}
		}
	}

	public void setPaintStyle() {
		wayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		wayPaint.setStyle(Paint.Style.STROKE);
		wayPaint.setColor(getResources().getColor(R.color.ocean));
		wayPaint.setStrokeWidth(10);
	    wayPaint.setDither(true);
		wayPaint.setAlpha(120);
	    wayPaint.setStrokeJoin(Paint.Join.ROUND);
	    wayPaint.setStrokeCap(Paint.Cap.ROUND);
		wayPaint.setPathEffect(new CornerPathEffect(10));
	    wayPaint.setAntiAlias(true);
	    
	    wayLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	    wayLinePaint.setStyle(Paint.Style.STROKE);
	    wayLinePaint.setColor(getResources().getColor(R.color.maroon));
	    wayLinePaint.setStrokeWidth(8);
	    wayLinePaint.setDither(true);
	    wayLinePaint.setAlpha(500);
	    wayLinePaint.setStrokeJoin(Paint.Join.ROUND);
	    wayLinePaint.setStrokeCap(Paint.Cap.ROUND);
	    wayLinePaint.setPathEffect(new CornerPathEffect(10));
	    wayLinePaint.setAntiAlias(true);
	    wayLinePaint.setPathEffect(new DashPathEffect(new float[] {10,20}, 0)); 	   
	    
	    FillCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	    FillCirclePaint.setStyle(Paint.Style.FILL);
	    FillCirclePaint.setColor(getResources().getColor(R.color.yellow));
	    FillCirclePaint.setAlpha(80);
	    
	    OutlineCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	    OutlineCirclePaint.setStyle(Paint.Style.STROKE);
	    OutlineCirclePaint.setColor(getResources().getColor(R.color.darkyellow));
	    OutlineCirclePaint.setStrokeWidth(3);
	    OutlineCirclePaint.setDither(true);
	    OutlineCirclePaint.setAlpha(200);
	    OutlineCirclePaint.setStrokeJoin(Paint.Join.ROUND);
	    OutlineCirclePaint.setStrokeCap(Paint.Cap.ROUND);
	    OutlineCirclePaint.setPathEffect(new CornerPathEffect(10));
	    OutlineCirclePaint.setAntiAlias(true);
	    
	}

	// action bar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_map, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem Item) {
		switch (Item.getItemId()) {
		case R.id.navigation:
			finish();
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyLocationListener.isAutoDrawTrack = true;
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyLocationListener.isAutoDrawTrack = false;
	}

	@Override
	protected void onStop() {
		MyLocationListener.isAutoDrawTrack = false;
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyLocationListener.isAutoDrawTrack = false;
	}

	private void setMapFile() {
		File mapFile = new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/org.mapsforge.android.maps/map/bretagne.map");
//		File mapFile = new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/org.mapsforge.android.maps/map/provence-alpes-cote-d-azur.map");
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
							AlertDialog.Builder dialog = new AlertDialog.Builder(MyMapActivity.this);
							dialog.setIcon(android.R.drawable.presence_busy);
							dialog.setTitle("Warning!!!");
							dialog.setMessage("Your Internet is connecting via 3G, downloading now may cost a lot. Are you sure to continue?");
							dialog.setNegativeButton("Sure",
									new OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// download
											if (isDownloadManagerAvailable(MyMapActivity.this)) {
												DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
												request.setDescription("Downloading...");
												request.setTitle("bretagne.map");
												// in order for this if to run,
												// you must use the android 3.2
												// to compile your app
												if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
													request.allowScanningByMediaScanner();
													request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
												}
												request.setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory().getPath() + "/Android/data/org.mapsforge.android.maps/map/", "bretagne.map");

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
							AlertDialog.Builder dialog = new AlertDialog.Builder(MyMapActivity.this);
							dialog.setTitle("Start downloading bretagne.map, please come back to this page when finish downloading file");
							dialog.setNeutralButton("OK", null);
							dialog.show();
							// download
							if (isDownloadManagerAvailable(MyMapActivity.this)) {
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
						AlertDialog.Builder dialog = new AlertDialog.Builder(MyMapActivity.this);
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
}