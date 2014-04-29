package orion.ms.sara;

import org.mapsforge.android.maps.overlay.ArrayItemizedOverlay;
import org.mapsforge.android.maps.overlay.OverlayItem;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.graphics.drawable.Drawable;

public class MyItemizedOverlay extends ArrayItemizedOverlay { 
    public MyItemizedOverlay (Drawable defaultMarker, boolean context) { 
      super (defaultMarker, context); 
    } 

    @Override 
    protected boolean onTap (int index) { 
      OverlayItem item = createItem (index); 
      if (item != null) { 
        Builder builder = new AlertDialog.Builder (MyMapActivity.getContext()); 
        builder.setIcon (android.R.drawable.ic_menu_info_details); 
        builder.setTitle (item.getTitle ()); 
        builder.setMessage (item.getSnippet ()); 
        builder.setPositiveButton ("OK", null); 
        builder.show (); 
      } 
      return true; 
    } 
  } 