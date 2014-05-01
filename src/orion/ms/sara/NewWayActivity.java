package orion.ms.sara;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
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
	
	//waypoint
	private WP tempWP;
	private WP selectedWP1;
	private WP selectedWP2;
	
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
	private String waypoint1Name = "No selected waypoint";
	private String waypoint2Name = "No selected waypoint";


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
		tempList = setUpTempWPList(tempList);
		setUpWP1List(tempList);
		
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
					waypoint1Name = selectedWP1.getName();
					waypoint2Name = selectedWP2.getName();		
					
					Log.i("saveeeee", "way name= "+wayName+" with wp1= "+waypoint1Name+" &wp2= "+waypoint2Name);
					//check if the filled name or the position (latitude and longitude) are already recorded
					if(isRecorded(wayName, waypoint1Name, waypoint2Name)){
						tts.speak("Please fill the new information", tts.QUEUE_ADD, null);
					}
					else{
						if(waypoint1Name.equals("No selected waypoint") || waypoint2Name.equals("No selected waypoint") || wayName.isEmpty()){
							//prevent unfilled text box(es)
							tts.speak("Please fill all information before saving", tts.QUEUE_ADD, null);
						}
						else{
							//sent the new waypoint information back to waypoint activity
							
							//notification
							//tts.speak("the new way already saved", tts.QUEUE_ADD, null);
							//Toast.makeText(NewWayActivity.this,"new way already saved", Toast.LENGTH_SHORT);
							Log.i("save", "the new way already saved");
							//change back to the waypoint activity
							//pass the parameters including name,latitude,longitude
							/*intentToWay.putExtra("newWayName",wayName);//name
							intentToWay.putExtra("newWP1Name", waypoint1Name);//latitude
							intentToWay.putExtra("newWP2Name", waypoint2Name);//longitude
							isAlsoActivateForNW = false;//change status

							//back to WayPoint activity and send some parameters to the activity
							setResult(RESULT_OK, intentToWay);
							finish();*/
							
						}//end else in if-else
						
					}//end else
				}
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


	//change default item name
	private List<WP> setUpTempWPList(List<WP> wList) {
		for(int i = 0;i<wList.size();i++){
			tempWP = wList.get(i);
			if(tempWP.getName().equals("Please selected a waypoint"))
				tempWP.setName("No selected waypoint");
		}
		return wList;
	}

	//setting up waypoint list to spinner1
	private void setUpWP1List(final List<WP> wList1) {
		wp1List = (Spinner) findViewById(R.id.spinner1);
		arrAd = new ArrayAdapter<String>(NewWayActivity.this,
						android.R.layout.simple_spinner_item, 
						WayPointActivity.toNameArrayList(wList1));
		arrAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);      
		wp1List.setAdapter(arrAd);
		wp1List.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() { 
			//OnItemSelectedListener creation
			public void onItemSelected(final AdapterView<?> adapterView, View view, int i, long l) { 
      				try{
                		switch(adapterView.getId()){
                		case R.id.spinner1: 
                			selectedWP1 = wList1.get(i);
                			waypoint1Name = selectedWP1.getName();
                			if(!waypoint1Name.equals("No selected waypoint"))
	            				wp1NameText.setText(waypoint1Name);
                			Log.i("wp1 selected", waypoint1Name);
                			setUpWP2List(tempList,selectedWP1);
                		}
      				}catch(Exception e){
	                        e.printStackTrace();
	                }//end try-catch
            }//end onItemSelected
            
			public void onNothingSelected(AdapterView<?> arg0) {
  				Toast.makeText(NewWayActivity.this,"You selected Empty",Toast.LENGTH_SHORT).show();
			} 

	    });//end setOnSelected
	}//end setUpWP1List
	
	//setting up waypoint list to spinner2 and also remove selected item
	private void setUpWP2List(final List<WP> wList2, WP sWP) {
		wp2List = (Spinner) findViewById(R.id.Spinner2);
		//if not selecting default item
		if(!sWP.getName().equals("No selected waypoint")){
			//remove the selected item in the waypoint list1 from the list2
			tempList = wList2;
			tempList.remove(sWP);
			arrAd = new ArrayAdapter<String>(NewWayActivity.this,
							android.R.layout.simple_spinner_item, 
							WayPointActivity.toNameArrayList(tempList));
			arrAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);      
			wp2List.setAdapter(arrAd);
			wp2List.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() { 
				//OnItemSelectedListener creation
				public void onItemSelected(final AdapterView<?> adapterView, View view, int i, long l) { 
		  				try{
		            		switch(adapterView.getId()){
		            		case R.id.Spinner2: 
		            			selectedWP2 = wList2.get(i);
		            			waypoint2Name = selectedWP2.getName();
		            			if(!waypoint2Name.equals("No selected waypoint"))
		            				wp2NameText.setText(waypoint2Name);
		            			Log.i("wp2 selected", waypoint2Name);
		            		}
		  				}catch(Exception e){
		                        e.printStackTrace();
		                }//end try-catch
		        }//end onItemSelected
		        
				public void onNothingSelected(AdapterView<?> arg0) {
					Toast.makeText(NewWayActivity.this,"You selected Empty",Toast.LENGTH_SHORT).show();
				} 
		
		    });//end setOnSelected
		}
	}

	//to check if the filled name or the position (latitude and longitude) are already recorded
	@SuppressLint("ShowToast")
	public boolean isRecorded(String wayName, String w1name, String w2name){
		List<Way> wayList = WayActivity.getWayList();
		for(int i = 1;i<wayList.size();i++){
			if(wayList.get(i).getName().equalsIgnoreCase(wayName)){
				// same name
				Toast.makeText(NewWayActivity.this, "This name is already recorded.", Toast.LENGTH_SHORT);
				tts.speak("This name is already recorded.", tts.QUEUE_FLUSH, null);
				return true;
			}//end if
			else if(wayList.get(i).getFirstWP().getName().equalsIgnoreCase(w1name) && 
					wayList.get(i).getWP(1).getName().equalsIgnoreCase(w2name)){
				//same position
				Toast.makeText(NewWayActivity.this, "These waypoints are already recorded.", Toast.LENGTH_SHORT);
				tts.speak("These waypoints are already recorded.", tts.QUEUE_FLUSH, null);
				return true;
			}//end if
		}//end for
		return false;
	}//end isRecored
	
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
