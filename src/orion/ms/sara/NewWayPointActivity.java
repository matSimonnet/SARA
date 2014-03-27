package orion.ms.sara;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NewWayPointActivity extends Activity{

	//variables declaration
	
	//string for each attribute of the new waypoint
	private String name = "";
	private String latitude = "";
	private String longitude = "";
	
	//TextView
	private TextView introText = null;
	private TextView nameText = null;
	private TextView latitudeText = null;
	private TextView longitudeText = null;
	
	//EditText
	private EditText nameBox = null;
	private EditText latitudeBox = null;
	private EditText longitudeBox = null;
	
	//save button
	private Button saveButton = null;
	
	private TextToSpeech tts = null;
	private LocationManager lm = null;
	private LocationListener ll = null;
	


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_way_point);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		//OnInitListener Creation
		OnInitListener onInitListener = new OnInitListener() {
			@Override
			public void onInit(int status) {
			}
		};
		
	    // textToSpeech creation
		tts = new TextToSpeech(this, onInitListener);
		tts.setSpeechRate((float) 2.0);
		
		//location manager creation
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//ll = new MyLocationListener();		
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
		
		Location loc = new Location("aa");
		//setting default as the current position
		latitude = ""+loc.getLatitude();
		longitude = ""+loc.getLongitude();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_way_point, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_new_way_point,
					container, false);
			return rootView;
		}
	}

}
