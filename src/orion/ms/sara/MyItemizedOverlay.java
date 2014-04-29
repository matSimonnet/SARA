package orion.ms.sara;

import org.mapsforge.android.maps.overlay.ArrayItemizedOverlay;
import org.mapsforge.android.maps.overlay.OverlayItem;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.drawable.Drawable;

public class MyItemizedOverlay extends ArrayItemizedOverlay { 
	private Context context;
    public MyItemizedOverlay (Drawable defaultMarker, boolean alignMarker, Context context) { 
      super (defaultMarker, alignMarker); 
      this.context = context;
    } 

    @Override 
    protected boolean onTap (int index) { 
      OverlayItem item = createItem (index); 
      if (item != null) { 
        Builder builder = new AlertDialog.Builder (this.context); 
        builder.setIcon (android.R.drawable.ic_menu_info_details); 
        builder.setTitle ("Waypoint name : " + item.getTitle ()); 
        //builder.setMessage (item.getSnippet ()); 
        builder.setPositiveButton ("OK", null); 
        builder.show (); 
      } 
      return true; 
    } 
  } 