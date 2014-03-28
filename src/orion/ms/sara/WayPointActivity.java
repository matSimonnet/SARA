package orion.ms.sara;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class WayPointActivity extends Activity {
	//components
	private TextView chooseText = null;
	private Button newWay = null;
	private Spinner way = null;	
			
	private TextToSpeech tts = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.activity_way_point);
		
		//OnInitListener Creation
		OnInitListener onInitListener = new OnInitListener() {
			@Override
			public void onInit(int status) {
			}
		};
		
	    // textToSpeech creation
		tts = new TextToSpeech(this, onInitListener);
		tts.setSpeechRate((float) 2.0);

				
		//TextView
		chooseText = (TextView) findViewById(R.id.textView1);
		
		
		//Adapter for using array with spinner
				//final List<String> list = new ArrayList<String>();
				//list.add("A");
				//list.add("2");
				//ArrayAdapter<String> arrAdapt = new ArrayAdapter<String>(WayPointActivity.this,R.id.spinner1,list);
				//arrAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				
				//Spinner
				way = (Spinner) findViewById(R.id.spinner1);
				
				//"New Waypoint" button
				//button creation
				newWay = (Button) findViewById(R.id.button1);
				
			    //setOnClickListener
			    newWay.setOnClickListener(new View.OnClickListener(){
					// OnClickListener creation			    
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(v==newWay){
							Toast.makeText(WayPointActivity.this,"Clicked new waypoint", Toast.LENGTH_SHORT).show();
							tts.speak("new waypoint", tts.QUEUE_FLUSH, null);
							//change to the "NewWayPoint" activity
							Intent newActivity = new Intent(WayPointActivity.this,NewWayPointActivity.class);
							startActivity(newActivity);
						}
					}//end of onClick
			    	
			    });//end of View.OnClickListener
				
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.way_point, menu);
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

}
