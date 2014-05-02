package orion.ms.sara;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class ModifyWayActivity extends Activity {
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
	private ArrayList<WP> tempList = new ArrayList<WP>();
	
	//waypoint
	private WP tempWP;
	private WP selectedWP1;
	private WP selectedWP2;
	private String name;
	private String latitude;
	private String longitude;
	
	//array adapter
	private ArrayAdapter<String> arrAd = null;
	
	private TextToSpeech tts = null;
	
	//Intent
	private Intent intentFromWay;
	private Intent intentToWay;
	
	//status for check if save and activate button is pressed (modify way)
	public static boolean isAlsoActivateForMW = false;
	
	//way attributes
	private String modWayName = "Way1";
	private String waypoint1Name = "No selected waypoint";
	private String waypoint2Name = "No selected waypoint";
	private String modWP1Name = "No selected waypoint";
	private String modWP2Name = "No selected waypoint";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_way);

		//OnInitListener Creation
		OnInitListener onInitListener = new OnInitListener() {
			@Override
			public void onInit(int status) {
			}
		};
		
	    // textToSpeech creation
		tts = new TextToSpeech(ModifyWayActivity.this, onInitListener);

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
		intentToWay = new Intent(ModifyWayActivity.this,WayActivity.class);
		
		//set default name
		modWayName = intentFromWay.getStringExtra("modName");
		wayNameBox.setText(modWayName);
		wp1NameText.setText(intentFromWay.getStringExtra("modWP1"));
		wp2NameText.setText(intentFromWay.getStringExtra("modWP2"));		
		
		//get way list
		tempList = setUpTempWPList();
		setUpWP1List(tempList);
		
		//add more button
		addMoreButton = (Button) findViewById(R.id.button1);
		
		//"save" button
		saveButton = (Button) findViewById(R.id.button2);
		//setOnClickedListener
		saveButton.setOnClickListener(new OnClickListener() {
			//OnClickedListener creation
			@SuppressWarnings("static-access")
			@SuppressLint("ShowToast")
			@Override
			public void onClick(View v) {
				if(v==saveButton){
					//get the new way's name EditText
					modWayName = wayNameBox.getText().toString();
					modWP1Name = selectedWP1.getName();
					modWP2Name = selectedWP2.getName();		
					
					//check if the filled name or the waypoints are already recorded
					if(!isRecorded(modWayName, waypoint1Name, waypoint2Name)){
						tts.speak("Please fill the new information or create a new way", tts.QUEUE_ADD, null);
					}
					else{
						if(waypoint1Name.equals("No selected waypoint") || waypoint2Name.equals("No selected waypoint") || modWayName.isEmpty()){
							//prevent incomplete information
							tts.speak("Please fill all information before saving", tts.QUEUE_ADD, null);
						}
						else{
							//sent the new way information back to way activity
							if(waypoint1Name.equals(waypoint2Name)){
								//prevent same way points
								tts.speak("You selected the same waypoints", tts.QUEUE_ADD, null);
							}
							else{
								//notification
								tts.speak("this modify way is already saved", tts.QUEUE_ADD, null);
								Toast.makeText(ModifyWayActivity.this,"new way already saved", Toast.LENGTH_SHORT);
		
								//change back to the way activity
								//pass the parameters
								intentToWay.putExtra("modWayName",modWayName);//name
								intentToWay.putExtra("modWP1Name", modWP1Name);//latitude
								intentToWay.putExtra("modWP2Name", modWP2Name);//longitude
								isAlsoActivateForMW = false;//change status
		
								//back to WayPoint activity and send some parameters to the activity
								setResult(RESULT_OK, intentToWay);
								finish();
							}
						}//end else in if-else
						
					}//end else
				}//end if
			}//end onClick
		});
		
		//save and activate button
		saveActButton = (Button) findViewById(R.id.button3);
		
	}

	//change default item name
		private ArrayList<WP> setUpTempWPList() {
		for(int i=0;i<WayPointActivity.getWayPointList().size();i++){
			String tmpname = WayPointActivity.getWayPointList().get(i).getName();
			Log.i("tempName", tmpname);
			if(!tmpname.equals("Please selected a waypoint")){
				name = WayPointActivity.getWayPointList().get(i).getName();
				latitude = WayPointActivity.getWayPointList().get(i).getLatitude();
				longitude = WayPointActivity.getWayPointList().get(i).getLongitude();
				tempList.add(new WP(name,latitude,longitude));
			}
		}
		String defaultName = "No selected waypoint";
		tempList.add(0,new WP(defaultName,"",""));
			
			return tempList;
		}

		//setting up waypoint list to spinner1
		private void setUpWP1List(final List<WP> wList1) {
			wp1List = (Spinner) findViewById(R.id.spinner1);
			arrAd = new ArrayAdapter<String>(ModifyWayActivity.this,
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
	  				Toast.makeText(ModifyWayActivity.this,"You selected Empty",Toast.LENGTH_SHORT).show();
				} 

		    });//end setOnSelected
		}//end setUpWP1List
		
		//setting up waypoint list to spinner2 and also remove selected item
		private void setUpWP2List(final List<WP> wList2, final WP sWP) {
			wp2List = (Spinner) findViewById(R.id.Spinner2);
			//if not selecting default item
			if(!sWP.getName().equals("No selected waypoint")){
				//remove the selected item in the waypoint list1 from the list2
				arrAd = new ArrayAdapter<String>(ModifyWayActivity.this,
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
						Toast.makeText(ModifyWayActivity.this,"You selected Empty",Toast.LENGTH_SHORT).show();
					} 
			
			    });//end setOnSelected
			}
		}

		//to check if the filled name or the position (latitude and longitude) are already recorded
		@SuppressWarnings("static-access")
		@SuppressLint("ShowToast")
		public boolean isRecorded(String wayName, String w1name, String w2name){
			List<Way> wayList = WayActivity.getWayList();
			List<WP> wList = WayPointActivity.getWayPointList();
			WP tempwp1 = null;
			WP tempwp2 = null;
			
			Log.i("record show wp1",w1name);
			
			//same waypoints
			for(int j=0;j<wList.size();j++){
				if(wList.get(j).getName().equals(w1name)){
					tempwp1 = wList.get(j);
					Log.i("record? wp1",tempwp1.getName());
				}
				if(wList.get(j).getName().equals(w2name)){
					tempwp2 = wList.get(j);
					Log.i("record? wp2",tempwp2.getName());
				}
			}
			//check same name or waypoints
			for(int i = 1;i<wayList.size();i++){
				if(wayList.get(i).getName().equalsIgnoreCase(wayName)){
					// same name
					Toast.makeText(ModifyWayActivity.this, "This name is already recorded.", Toast.LENGTH_SHORT);
					tts.speak("This name is already recorded.", tts.QUEUE_FLUSH, null);
					return true;
				}
				if(wayList.get(i).getFirstWP().equals(tempwp1) && wayList.get(i).getWP(1).equals(tempwp2)){
					//same waypoints
					Toast.makeText(ModifyWayActivity.this, "These waypoints are already recorded.", Toast.LENGTH_SHORT);
					tts.speak("These waypoints are already recorded.", tts.QUEUE_FLUSH, null);
					return true;
				}//end else
			}//end for
			return false;
		}//end isRecored
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.modify_way, menu);
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
