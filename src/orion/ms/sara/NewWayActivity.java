package orion.ms.sara;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NewWayActivity extends Activity {
	//components
	//TextView
	private TextView wayNameText;
	private TextView wp1Text;
	private TextView wp1NameText;
	private TextView wp2Text;
	private TextView wp2NameText;
	
	//EditText
	private EditText wayNameBox;
	
	//button
	private Button saveButton = null;
	private Button saveActButton = null;
	private Button addMoreButton = null;
	
	//spinner
	private Spinner wp1List;
	private Spinner wp2List;
	private List<WP> tempList;
	
	//array adapter
	private ArrayAdapter<String> arrAd = null;
	
	private TextToSpeech tts = null;
	
	//Intent
	private Intent intentFromWay;
	private Intent intentToWay;
	
	//status for check if save and activate button is pressed (new way)
	public static boolean isAlsoActivateForNW = false;
	
	//way attributes
	private String wayName = "Way1";
	private String waypoint1Name = "no selected waypoint";
	private String waypoint2Name = "no selected waypoint";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_way);

		//TextView
		wayNameText = (TextView) findViewById(R.id.textView1);
		wayNameText.setContentDescription("new way's name");
		wp1Text = (TextView) findViewById(R.id.textView2);
		wp1Text.setContentDescription("waypoint 1 name is"); 
		wp1NameText = (TextView) findViewById(R.id.textView3);
		wp2Text = (TextView) findViewById(R.id.TextView4);
		wp2Text.setContentDescription("waypoint 2 name is"); 
		wp2NameText = (TextView) findViewById(R.id.TextView5);
		
		//EditText
		wayNameBox = (EditText) findViewById(R.id.editText1);
		
		//Intent creation
		intentFromWay = getIntent();
		intentToWay = new Intent(NewWayActivity.this,WayActivity.class);
		
		//set default name
		wayName = intentFromWay.getStringExtra("defaultNameFromWay");
		wayNameBox.setText(wayName);
		
		//get way list
		tempList = WayPointActivity.getWayPointList();
		setUpWP1List(tempList);
		//setUpWP2List(tempList);
		
		//add more button
		addMoreButton = (Button) findViewById(R.id.button1);
		
		//"save" button
		saveButton = (Button) findViewById(R.id.button2);
		saveButton.setTextSize(30);
		//setOnClickedListener
		saveButton.setOnClickListener(new OnClickListener() {
			//OnClickedListener creation
			@SuppressWarnings("static-access")
			@SuppressLint("ShowToast")
			@Override
			public void onClick(View v) {
				if(v==saveButton){
					//get the new waypoint's name, latitude and longitude from the EditText
					wayName = wayNameBox.getText().toString();
					
					//check if the filled name or the position (latitude and longitude) are already recorded
					/*if(isRecorded(name, latitude, longitude)){
						tts.speak("Please fill the new information", tts.QUEUE_ADD, null);
					}
					else{
						if(latitude.isEmpty() || longitude.isEmpty() || name.isEmpty()){
							//prevent unfilled text box(es)
							tts.speak("Please fill all information before saving", tts.QUEUE_ADD, null);
						}
						else{*/
							//sent the new waypoint information back to waypoint activity
							
							//notification
							tts.speak("the new waypoint already saved", tts.QUEUE_ADD, null);
							Toast.makeText(NewWayActivity.this,"new way already saved", Toast.LENGTH_SHORT);
							
							//change back to the waypoint activity
							//pass the parameters including name,latitude,longitude
							intentToWay.putExtra("newWayName",wayName);//name
							intentToWay.putExtra("newWP1Name", waypoint1Name);//latitude
							intentToWay.putExtra("newWP2Name", waypoint2Name);//longitude
							isAlsoActivateForNW = false;//change status

							//back to WayPoint activity and send some parameters to the activity
							setResult(RESULT_OK, intentToWay);
							finish();
							
						}//end else in if-else
						
					//}//end else	
				//}	
			}//end onClick
		});
		
		//save and activate button
		saveActButton = (Button) findViewById(R.id.button3);
		/*saveActButton.setOnClickListener(new OnClickListener() {
			//onClick creation
			@Override
			public void onClick(View v) {
				if(v==saveActButton){
					//get the new waypoint's name, latitude and longitude from the EditText
					wayName = wayNameBox.getText().toString();
					latitude = latitudeBox.getText().toString();
					longitude = longitudeBox.getText().toString();
					
					//check if the filled name or the position (latitude and longitude) are already recorded
					if(isRecorded(name, latitude, longitude)){
						tts.speak("Please fill the new information", tts.QUEUE_ADD, null);
					}
					else{
						if(latitude.isEmpty() || longitude.isEmpty() || name.isEmpty()){
							//prevent unfilled text box(es)
							tts.speak("Please fill all information before saving", tts.QUEUE_ADD, null);
						}
						else{
							//sent the new waypoint information back to waypoint activity
							//intent to way point
							//pass the parameters including name,latitude,longitude
							intentToWay.putExtra("newName",name);//name
							intentToWay.putExtra("newLatitude", latitude);//latitude
							intentToWay.putExtra("newLongitude", longitude);//longitude
							isAlsoActivateForNWP = true;//change the status
							
							//back to WayPoint activity and send some parameters to the activity
							setResult(RESULT_OK, intentToWay);
							finish();
						}//end else in if-else
					} //end else						
				}//end if
			}//end onClick
		});//end setOnClick
		*/
	}

	//setting up waypoint list to spinner1
	private void setUpWP1List(List<WP> wList1) {
		wp1List = (Spinner) findViewById(R.id.spinner1);
		arrAd = new ArrayAdapter<String>(NewWayActivity.this,
						android.R.layout.simple_spinner_item, 
						WayPointActivity.toNameArrayList(wList1));
		arrAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);      
		wp1List.setAdapter(arrAd);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_way, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.way_setting:
			//back to main activity and send some parameters to the activity
			
			finish();
			break;
		default:
			break;
		}
		return false;
	}

}
