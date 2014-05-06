package orion.ms.sara;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
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
	private ArrayList<WP> tempList = new ArrayList<WP>();
	private ArrayList<WP> anotherList = new ArrayList<WP>();
	
	private WP selectedWP1 = null;
	private WP selectedWP2 = null;
	private String name;
	private String latitude;
	private String longitude;
	
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
		
		//OnInitListener Creation
		OnInitListener onInitListener = new OnInitListener() {
			@Override
			public void onInit(int status) {
			}
		};
		
	    // textToSpeech creation
		tts = new TextToSpeech(NewWayActivity.this, onInitListener);

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
		tempList = setUpTempWPList();
		anotherList = setUpAnotherWPList();
		setUpWP1List(tempList);
		setUpWP2List(anotherList);		
		
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
					wayName = wayNameBox.getText().toString();
					waypoint1Name = selectedWP1.getName();
					waypoint2Name = selectedWP2.getName();		
					
					//check if the filled name or the waypoints are already recorded
					if(isRecorded(wayName, waypoint1Name, waypoint2Name,NewWayActivity.this)){
						tts.speak("Please fill the new information", tts.QUEUE_ADD, null);
					}
					else{
						if(waypoint1Name.equals("No selected waypoint") || waypoint2Name.equals("No selected waypoint") || wayName.isEmpty()){
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
								tts.speak("the new way already saved", tts.QUEUE_ADD, null);
								Toast.makeText(NewWayActivity.this,"new way already saved", Toast.LENGTH_SHORT);
		
								//change back to the way activity
								//pass the parameters
								intentToWay.putExtra("newWayName",wayName);//name
								intentToWay.putExtra("newWP1Name", waypoint1Name);//way point1 name
								intentToWay.putExtra("newWP2Name", waypoint2Name);//way point2 name
								isAlsoActivateForNW = false;//change status
		
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
		saveActButton.setOnClickListener(new OnClickListener() {
			//onClick creation
			@SuppressLint("ShowToast")
			@SuppressWarnings("static-access")
			@Override
			public void onClick(View v) {
				if(v==saveActButton){
					//get the new way's name EditText
					wayName = wayNameBox.getText().toString();
					waypoint1Name = selectedWP1.getName();
					waypoint2Name = selectedWP2.getName();		
					
					//check if the filled name or the waypoints are already recorded
					if(isRecorded(wayName, waypoint1Name, waypoint2Name,NewWayActivity.this)){
						tts.speak("Please fill the new information", tts.QUEUE_ADD, null);
					}
					else{
						if(waypoint1Name.equals("No selected waypoint") || waypoint2Name.equals("No selected waypoint") || wayName.isEmpty()){
							//prevent incomplete information
							tts.speak("Please fill all information before saving", tts.QUEUE_ADD, null);
						}
						else{
							//sent the new way information back to way activity
							
							//notification
							tts.speak("the new way already saved", tts.QUEUE_ADD, null);
							Toast.makeText(NewWayActivity.this,"new way already saved", Toast.LENGTH_SHORT);

							//change back to the way activity
							//pass the parameters
							intentToWay.putExtra("newWayName",wayName);//name
							intentToWay.putExtra("newWP1Name", waypoint1Name);//latitude
							intentToWay.putExtra("newWP2Name", waypoint2Name);//longitude
							isAlsoActivateForNW = true;//change status

							//back to WayPoint activity and send some parameters to the activity
							setResult(RESULT_OK, intentToWay);
							finish();
							
						}//end else in if-else
						
					}//end else				
				}//end if
			}//end onClick
		});//end setOnClick
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
		
		for(int i =0;i<WayPointActivity.getWayPointList().size();i++){
			Log.i("waypoint list", WayPointActivity.getWayPointList().get(i).getName());
		}
		
		return tempList;
	}
	
	//change the default name
	private ArrayList<WP> setUpAnotherWPList() {
		for(int i=0;i<WayPointActivity.getWayPointList().size();i++){
			String tmpname = WayPointActivity.getWayPointList().get(i).getName();
			Log.i("tempName", tmpname);
			if(!tmpname.equals("Please selected a waypoint")){
				name = WayPointActivity.getWayPointList().get(i).getName();
				latitude = WayPointActivity.getWayPointList().get(i).getLatitude();
				longitude = WayPointActivity.getWayPointList().get(i).getLongitude();
				anotherList.add(new WP(name,latitude,longitude));
			}
		}
		String defaultName = "No selected waypoint";
		anotherList.add(0,new WP(defaultName,"",""));
			
			return anotherList;
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
	private void setUpWP2List(final List<WP> wList2) {
		wp2List = (Spinner) findViewById(R.id.Spinner2);
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

	//to check if the filled name or the position (latitude and longitude) are already recorded
	@SuppressWarnings("static-access")
	@SuppressLint("ShowToast")
	public boolean isRecorded(String wayName, String w1name, String w2name,Context con){
		List<Way> wayList = WayActivity.getWayList();
		WP tempwp1 = null;
		WP tempwp2 = null;
			
		//same waypoints
		tempwp1 = WayActivity.findWPfromName(w1name);
		tempwp2 = WayActivity.findWPfromName(w2name);

		//check same name or way points
		for(int i = 1;i<wayList.size();i++){
			if(wayList.get(i).getName().equalsIgnoreCase(wayName)){
				// same name
				tts.speak("This name is already recorded.", tts.QUEUE_ADD, null);
				Toast.makeText(con, "This name is already recorded.", Toast.LENGTH_SHORT);
				return true;
			}
			else if(wayList.get(i).getFirstWP().equals(tempwp1) && wayList.get(i).getWP(1).equals(tempwp2)){
				//same way points
				tts.speak("These waypoints are already recorded.", tts.QUEUE_ADD, null);
				Toast.makeText(con, "These waypoints are already recorded.", Toast.LENGTH_SHORT);
				return true;
			}//end else
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
			if(selectedWP1 != null && selectedWP2 != null){
				//get the new way's name EditText
				wayName = wayNameBox.getText().toString();
				waypoint1Name = selectedWP1.getName();
				waypoint2Name = selectedWP2.getName();
				
				//check if some values change without saving
				if((!waypoint1Name.equals("No selected waypoint") || !waypoint2Name.equals("No selected waypoint")) 
						&& !waypoint1Name.equals(waypoint2Name)
						&& !isRecorded(wayName, waypoint1Name, waypoint2Name,NewWayActivity.this)){
					final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
					dialog.setTitle("Some values change, do you want to save?");
					dialog.setNegativeButton("Yes", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							//pass the parameters
							intentToWay.putExtra("newWayName",wayName);//name
							intentToWay.putExtra("newWP1Name", waypoint1Name);//way point1 name
							intentToWay.putExtra("newWP2Name", waypoint2Name);//way point2 name
							isAlsoActivateForNW = false;//change status
	
							//back to Way activity and send some parameters to the activity
							setResult(RESULT_OK, intentToWay);
							finish();
						}
					});
					
					dialog.setNeutralButton("No", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							//don't save
							//pass the parameters
							intentToWay.putExtra("newWayName","");//name
							intentToWay.putExtra("newWP1Name", "");//way point1 name
							intentToWay.putExtra("newWP2Name", "");//way point2 name
							isAlsoActivateForNW = false;//change status
	
							//back to Way activity and send some parameters to the activity
							setResult(RESULT_OK, intentToWay);
							finish();
						}
					});
					dialog.show();
				}
				else{
					//don't save
					//pass the parameters
					intentToWay.putExtra("newWayName","");//name
					intentToWay.putExtra("newWP1Name", "");//way point1 name
					intentToWay.putExtra("newWP2Name", "");//way point2 name
					isAlsoActivateForNW = false;//change status
	
					//back to Way activity and send some parameters to the activity
					setResult(RESULT_OK, intentToWay);
					finish();
					break;
				}
		}//end if selected
		else{
			//don't save
			//pass the parameters
			intentToWay.putExtra("newWayName","");//name
			intentToWay.putExtra("newWP1Name", "");//way point1 name
			intentToWay.putExtra("newWP2Name", "");//way point2 name
			isAlsoActivateForNW = false;//change status

			//back to Way activity and send some parameters to the activity
			setResult(RESULT_OK, intentToWay);
			finish();
			break;
		}
		default:
			break;
		}
		return false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		tts.shutdown();
	}
  
	@Override
	protected void onStop() {
		super.onStop();
		tts.shutdown();
	}
  
	@Override
	protected void onDestroy() {
		super.onDestroy();
		tts.shutdown();
	}

}