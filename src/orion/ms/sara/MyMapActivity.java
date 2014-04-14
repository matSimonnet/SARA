package orion.ms.sara;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import org.mapsforge.android.maps.*;
import org.mapsforge.android.maps.overlay.ArrayItemizedOverlay;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.core.GeoPoint;


public class MyMapActivity extends MapActivity {
	
	private MapView mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {	
        super.onCreate(savedInstanceState);
        mapView = new MapView(this);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setMapFile(new File(Environment.getExternalStorageDirectory().getPath()+ "/Android/data/map/bretagne.map")); 

        // create a default marker for the overlay
        // R.drawable.marker is just a placeholder for your own drawable
        Drawable defaultMarker = getResources().getDrawable(R.drawable.location);
        

        // create an ItemizedOverlay with the default marker
        ArrayItemizedOverlay itemizedOverlay = new ArrayItemizedOverlay(defaultMarker);

        // create a GeoPoint with the latitude and longitude coordinates
        GeoPoint geoPoint = new GeoPoint(48.358780, -4.570152);

        // create an OverlayItem with title and description
        OverlayItem item = new OverlayItem(geoPoint, "Brandenburg Gate",
                "One of the main symbols of Berlin and Germany.");
       
        // add the OverlayItem to the ArrayItemizedOverlay
        itemizedOverlay.addItem(item);

        // add the ArrayItemizedOverlay to the MapView
        mapView.getOverlays().add(itemizedOverlay);
        setContentView(mapView);

	}
//  action bar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_map, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem Item){
		switch (Item.getItemId()) {
		case R.id.navigation:
			finish();
			break;
		default:
			break;
		}
		return false;
	}
}