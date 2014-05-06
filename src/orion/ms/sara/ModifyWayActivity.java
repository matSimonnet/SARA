package orion.ms.sara;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
	private ArrayList<WP> anotherList = new ArrayList<WP>();
	
	//way point
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
	private String modWP1Name = "No selected waypoint";
	private String modWP2Name = "No selected waypoint";
	private String oldWayName = "";
	private String oldWP1Name = "";
	private String oldWP2Name = "";
	

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
		oldWayName = intentFromWay.getStringExtra("modName");
		wayNameBox.setText(oldWayName);
		oldWP1Name = intentFromWay.getStringExtra("modWP1");
		wp1NameText.setText(oldWP1Name);
		oldWP2Name = intentFromWay.getStringExtra("modWP2");
		wp2NameText.setText(oldWP2Name);		
		
		//get way list
		tempList = setUpTempWPList();
		anotherList = setUpAnotherWPList();
		setUpWP1List(tempList,oldWP1Name);
		setUpWP2List(anotherList,oldWP2Name);
		
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
					
					//check if the filled name or the way points are already recorded
					if(!isRecorded(modWayName, modWP1Name, modWP2Name)){
						tts.speak("Please fill the new information or create a new way", tts.QUEUE_ADD, null);
					}
					else{
						if(modWP1Name.equals("No selected waypoint") || modWP2Name.equals("No selected waypoint") || modWayName.isEmpty()){
							//prevent incomplete information
							tts.speak("Please fill all information before saving", tts.QUEUE_ADD, null);
						}
						else{
							//sent the new way information back to way activity
							if(modWP1Name.equals(modWP2Name)){
								//prevent same way points
								tts.speak("You selected the same waypoints", tts.QUEUE_ADD, null);
							}
							else{
								Way temp = new Way(modWayName,WayActivity.findWPfromName(modWP1Name),WayActivity.findWPfromName(modWP2Name));
								if(isAllow(temp)){
									tts.speak("new way already saved", tts.QUEUE_ADD, null);
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
								else{
									tts.speak("This way is already recorded", tts.QUEUE_ADD, null);
									Toast.makeText(ModifyWayActivity.this,"This way is already recorded", Toast.LENGTH_SHORT);
								}
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
					modWayName = wayNameBox.getText().toString();
					modWP1Name = selectedWP1.getName();
					modWP2Name = selectedWP2.getName();		
					
					//check if the filled name or the way points are already recorded
					if(!isRecorded(modWayName, modWP1Name, modWP2Name)){
						tts.speak("Please fill the new information or create a new way", tts.QUEUE_ADD, null);
					}
					else{
						if(modWP1Name.equals("No selected waypoint") || modWP2Name.equals("No selected waypoint") || modWayName.isEmpty()){
							//prevent incomplete information
							tts.speak("Please fill all information before saving", tts.QUEUE_ADD, null);
						}
						else{
							//sent the new way information back to way activity
							if(modWP1Name.equals(modWP2Name)){
								//prevent same way points
								tts.speak("You selected the same waypoints", tts.QUEUE_ADD, null);
							}
							else{
								Way temp = new Way(modWayName,WayActivity.findWPfromName(modWP1Name),WayActivity.findWPfromName(modWP2Name));
								if(isAllow(temp)){
									tts.speak("new way already saved", tts.QUEUE_ADD, null);
									Toast.makeText(ModifyWayActivity.this,"new way already saved", Toast.LENGTH_SHORT);
									//change back to the way activity
									//pass the parameters
									intentToWay.putExtra("modWayName",modWayName);//name
									intentToWay.putExtra("modWP1Name", modWP1Name);//latitude
									intentToWay.putExtra("modWP2Name", modWP2Name);//longitude
									isAlsoActivateForMW = true;//change status
			
									//back to WayPoint activity and send some parameters to the activity
									setResult(RESULT_OK, intentToWay);
									finish();
								}
								else{
									tts.speak("This way is already recorded", tts.QUEUE_ADD, null);
									Toast.makeText(ModifyWayActivity.this,"This way is already recorded", Toast.LENGTH_SHORT);
								}
							}
						}//end else in if-else
					}//end else
				}//end if
			}//end onClick
		});
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

	//setting up way point list to spinner1
	private void setUpWP1List(final List<WP> wList1, String wp1) {
		wp1List = (Spinner) findViewById(R.id.spinner1);
		//set top of the list with old WP1 name
		WP tempWP = null;
		List<WP> list1 = null;
		list1 = wList1;
		if(!wp1.equals("No selected way")){
        	//get the selected way point and add it on the top of the list
        	for(int i = 0;i<list1.size();i++){
        		if(wList1.get(i).getName().equals(wp1)){
        			tempWP = list1.remove(i);
        			Log.i("selected item from sort", tempWP.getName());
        		}
        	}
        	list1.add(0,tempWP);
        	
        }
		arrAd = new ArrayAdapter<String>(ModifyWayActivity.this,
						android.R.layout.simple_spinner_item, 
						WayPointActivity.toNameArrayList(list1));
		arrAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);      
		wp1List.setAdapter(arrAd);
		wp1List.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() { 
			//OnItemSelectedListener creation
			public void onItemSelected(final AdapterView<?> adapterView, View view, int i, long l) { 
      				try{
                		switch(adapterView.getId()){
                		case R.id.spinner1: 
                			selectedWP1 = wList1.get(i);
                			modWP1Name = selectedWP1.getName();
                			if(!modWP1Name.equals("No selected waypoint"))
	            				wp1NameText.setText(modWP1Name);
                			Log.i("wp1 selected", modWP1Name);
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
	private void setUpWP2List(final List<WP> wList2,String wp2) {
		wp2List = (Spinner) findViewById(R.id.Spinner2);
		//set top of the list with old WP2 name
		WP tempWP = null;
		List<WP> list2 = null;
		list2 = wList2;
		if(!wp2.equals("No selected way")){
        	//get the selected waypoint and add it on the top of the list
        	for(int i = 0;i<list2.size();i++){
        		if(wList2.get(i).getName().equals(wp2)){
        			tempWP = list2.remove(i);
        			Log.i("selected item from sort", tempWP.getName());
        		}
        	}
        	list2.add(0,tempWP);
        	
        }
		arrAd = new ArrayAdapter<String>(ModifyWayActivity.this,
						android.R.layout.simple_spinner_item, 
						WayPointActivity.toNameArrayList(list2));
		arrAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);      
		wp2List.setAdapter(arrAd);
		wp2List.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() { 
			//OnItemSelectedListener creation
			public void onItemSelected(final AdapterView<?> adapterView, View view, int i, long l) { 
	  				try{
	            		switch(adapterView.getId()){
	            		case R.id.Spinner2: 
	            			selectedWP2 = wList2.get(i);
	            			modWP2Name = selectedWP2.getName();
	            			if(!modWP2Name.equals("No selected waypoint"))
	            				wp2NameText.setText(modWP2Name);
	            			Log.i("wp2 selected", modWP2Name);
	            		}
	  				}catch(Exception e){
	                        e.printStackTrace();
	                }//end try-catch
	        }//end onItemSelected
	        
			public void onNothingSelected(AdapterView<?> arg0) {
				Toast.makeText(ModifyWayActivity.this,"You selected Empty",Toast.LENGTH_SHORT).show();
			}
		});//end onSelected
	}

	//to check if the filled name is already recorded
	private boolean isRecorded(String wayName, String w1name, String w2name){
		List<Way> wayList = WayActivity.getWayList();
		WP tempwp1 = WayActivity.findWPfromName(w1name);
		WP tempwp2 = WayActivity.findWPfromName(w2name);

		//check same name or way points
		for(int i = 1;i<wayList.size();i++){
			if(wayList.get(i).getName().equalsIgnoreCase(wayName) ||
				(wayList.get(i).getFirstWP().equals(tempwp1) && wayList.get(i).getWP(1).equals(tempwp2)) ){
				return true;
			}//end else
		}//end for
		return false;
	}//end isRecored
	
	//check if this way allow to have same name with different way points
	//or same way points with different name
	private boolean isAllow(Way way){
		if(usedName(way.getName())&&!usedWay(way)) return true;
		else if(!usedName(way.getName())&&usedWay(way)) return true;
		return false;
	}//end isAllow
	
	//check if this way's name is already used
	public static boolean usedName(String wayName){
		List<Way> wayList = WayActivity.getWayList();
		for(int i = 0;i<wayList.size();i++){
			if(wayList.get(i).getName().equals(wayName))
				return true;
		}
		return false;
	}
	
	//check if these way points is already used
	public static boolean usedWay(Way way){
		List<Way> wayList = WayActivity.getWayList();
		for(int i = 1;i<wayList.size();i++){//ways in way list
			Way w = wayList.get(i);
			if(sameWay(w,way))	
				return true;
		}
		return false;
	}
	
	//check if the same way (order, way points and size)
	public static boolean sameWay(Way w1, Way w2){
		if(w1.getSize()!=w2.getSize()) 
			return false;
		for(int i= 0;i<w1.getSize();i++){
			if(!w1.getWP(i).getName().equals(w2.getWP(i).getName()))
				return false;
		}
		return true;
	}
	
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
			if(selectedWP1 != null && selectedWP2 != null){
				//get the new way's name EditText
				modWayName = wayNameBox.getText().toString();
				modWP1Name = selectedWP1.getName();
				modWP2Name = selectedWP2.getName();	
				
				Log.i("without saving", "=====old====");
				Log.i("without saving", oldWayName);
				Log.i("without saving", oldWP1Name);
				Log.i("without saving", oldWP2Name);
				Log.i("without saving", "=====MOD====");
				Log.i("without saving", modWayName);
				Log.i("without saving", modWP1Name);
				Log.i("without saving", modWP2Name);
			
				//check if some values change without saving
				if((!modWP1Name.equals("No selected waypoint") || !modWP2Name.equals("No selected waypoint")) 
						&& !modWP1Name.equals(modWP2Name)
						&& (!modWayName.equals(oldWayName) || !modWP1Name.equals(oldWP1Name) ||!modWP2Name.equals(oldWP2Name) )
						&& isAllow(new Way(modWayName,WayActivity.findWPfromName(modWP1Name),WayActivity.findWPfromName(modWP2Name)))){
					final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
					dialog.setTitle("Some values change, do you want to save?");
					dialog.setNegativeButton("Yes", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							//pass the parameters
							intentToWay.putExtra("modWayName",modWayName);//name
							intentToWay.putExtra("modWP1Name", modWP1Name);//latitude
							intentToWay.putExtra("modWP2Name", modWP2Name);//longitude
							isAlsoActivateForMW = false;//change status
	
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
							intentToWay.putExtra("modWayName","");//name
							intentToWay.putExtra("modWP1Name", "");//latitude
							intentToWay.putExtra("modWP2Name", "");//longitude
							isAlsoActivateForMW = false;//change status
	
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
					intentToWay.putExtra("modWayName","");//name
					intentToWay.putExtra("modWP1Name", "");//latitude
					intentToWay.putExtra("modWP2Name", "");//longitude
					isAlsoActivateForMW = false;//change status
	
					//back to Way activity and send some parameters to the activity
					setResult(RESULT_OK, intentToWay);
					finish();
					break;
				}
		}//end if selected != null
		else{
			//don't save
			//pass the parameters
			intentToWay.putExtra("modWayName","");//name
			intentToWay.putExtra("modWP1Name", "");//latitude
			intentToWay.putExtra("modWP2Name", "");//longitude
			isAlsoActivateForMW = false;//change status

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

}