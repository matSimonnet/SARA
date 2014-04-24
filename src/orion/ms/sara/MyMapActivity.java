package orion.ms.sara;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import java.util.List;
import java.io.File;
import org.mapsforge.android.maps.*;
import org.mapsforge.android.maps.overlay.ArrayWayOverlay;
import org.mapsforge.android.maps.overlay.OverlayWay;
import org.mapsforge.core.GeoPoint;

public class MyMapActivity extends MapActivity {

	private static MapView mapView;
	private static GeoPoint geoPoint[] = null;
	private LocationManager lm = null;
	private MyLocationListener ll;
	private static ArrayWayOverlay wayOverlay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mymapview);
		
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		ll = new MyLocationListener();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
		
		MyLocationListener.isAutoDrawTrack = true;
		
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);
		setMapFile();
		drawTrack();
	}

	public static void drawTrack() {

		geoPoint = MyLocationListener.geoPoint;
		if (wayOverlay != null)
			mapView.getOverlays().remove(wayOverlay);

		Paint wayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		wayPaint.setStyle(Paint.Style.STROKE);
		wayPaint.setColor(Color.BLACK);
		wayPaint.setAlpha(192);
		wayPaint.setStrokeWidth(6);

		wayOverlay = new ArrayWayOverlay(wayPaint, wayPaint);
		OverlayWay way = new OverlayWay(new GeoPoint[][] { geoPoint });
		wayOverlay.addWay(way);
		mapView.getOverlays().add(wayOverlay);
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
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
	}

	@Override
	protected void onPause() {
		super.onPause();
		lm.removeUpdates(ll);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyLocationListener.isAutoDrawTrack = false;
		lm.removeUpdates(ll);
	}

	private void setMapFile() {
		File mapFile = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/Android/data/org.mapsforge.android.maps/map/bretagne.map");
		// check if there is a bretagne.map
		if (mapFile.exists()) {
			// already had a map
			mapView.setMapFile(mapFile);
		} else {
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

						}// end is3GAvailable
						else if (isWifiAvailable()) {
							// Start downloading file via wireless network
							// notify
							AlertDialog.Builder dialog = new AlertDialog.Builder(
									MyMapActivity.this);
							dialog.setTitle("Start downloading bretagne.map");
							dialog.setNeutralButton("OK", null);
							dialog.show();
							// download
							String url = "http://download.mapsforge.org/maps/europe/france/bretagne.map";
							if (isDownloadManagerAvailable(MyMapActivity.this)) {
								DownloadManager.Request request = new DownloadManager.Request(
										Uri.parse(url));
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
						AlertDialog.Builder dialog = new AlertDialog.Builder(
								MyMapActivity.this);
						dialog.setIcon(android.R.drawable.presence_busy);
						dialog.setTitle("No Internet connection right now, please try again later.");
						dialog.setNegativeButton("OK", new OnClickListener() {
							// Go back to main activity
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
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
			intent.setClassName("com.android.providers.downloads.ui",
					"com.android.providers.downloads.ui.DownloadList");
			List<ResolveInfo> list = context.getPackageManager()
					.queryIntentActivities(intent,
							PackageManager.MATCH_DEFAULT_ONLY);
			return list.size() > 0;
		} catch (Exception e) {
			return false;
		}
	}

	// check if the Internet connection is available
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	// check if 3G connection is available
	private boolean is3GAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobile = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return mobile.isAvailable() && mobile.isConnected();
	}

	// check if wireless connection is available
	private boolean isWifiAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return wifi.isAvailable() && wifi.isConnected();
	}
}